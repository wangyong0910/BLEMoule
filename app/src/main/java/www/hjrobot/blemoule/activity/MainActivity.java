package www.hjrobot.blemoule.activity;


import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.shehuan.statusview.StatusView;
import com.shehuan.statusview.StatusViewBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import www.hjrobot.blemoule.R;
import www.hjrobot.blemoule.entity.MainRecyclerItem;
import www.hjrobot.blemoule.util.DialogCenterUtil;
import www.hjrobot.blemoule.util.StatusBarUtil;
import www.hjrobot.blemoule.util.ToastUtil;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_status_view)
    StatusView mainStatusView;
    @BindView(R.id.main_swipe_refresh_layout)
    SmartRefreshLayout mainSwipeRefreshLayout;
    @BindView(R.id.main_recycler_view)
    RecyclerView mainRecyclerView;
    @BindView(R.id.tv_header_title)
    TextView tvHeaderTitle;
    @BindView(R.id.iv_header_back_ico)
    ImageView ivHeaderBack;

    private static String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    boolean enBtFlag = true;

    List<BleDevice> bleDevices = new ArrayList<>();

    List<MainRecyclerItem> mainRecyclerItems = new ArrayList<>();

    BaseQuickAdapter<MainRecyclerItem, BaseViewHolder> baseViewHolderBaseQuickAdapter;

    Unbinder unbinder;

    AlertDialog blueTeethdialog;

    boolean isRefresh = true;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setStatusBarDarkTheme(this,true);
        unbinder = ButterKnife.bind(this);
        ivHeaderBack.setVisibility(View.GONE);
        tvHeaderTitle.setText("蓝牙设备列表");

        initBTDialog();
        checkPermission();
        initProgressDialog();
        initMultStatusView();
        initRefreshLayout();
        initRecyclerView();
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this);
    }

    private void initRefreshLayout() {
        mainSwipeRefreshLayout.setRefreshHeader(new ClassicsHeader(this));
        mainSwipeRefreshLayout.setOnRefreshListener(refreshLayout -> {
            scanBTDevicesAndSet();
            isRefresh = true;
        });
    }

    private void initMultStatusView() {
        mainStatusView.config(new StatusViewBuilder.Builder()
                .setOnEmptyRetryClickListener(v -> {
                    firstScan();
                })
                .setOnErrorRetryClickListener(v -> {
                    firstScan();
                }).build());
    }

    private void initRecyclerView() {
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        baseViewHolderBaseQuickAdapter = new BaseQuickAdapter<MainRecyclerItem, BaseViewHolder>(R.layout.main_recycler_item, mainRecyclerItems) {
            @Override
            protected void convert(BaseViewHolder helper, MainRecyclerItem item) {
                helper.setText(R.id.tv_ble_name, item.getName());
                helper.setText(R.id.tv_ble_mac, item.getMac());
                if (item.isStatus()) {
                    helper.setGone(R.id.btn_connect_detail, true);
                    helper.setText(R.id.tv_connect_status, "断开连接");
                    helper.setTextColor(R.id.tv_connect_status, Color.WHITE);
                    helper.setImageResource(R.id.iv_connect_status,R.drawable.connect);
                    helper.setBackgroundRes(R.id.btn_connect_status, R.drawable.button_blue_ripple);
                } else {
                    helper.setGone(R.id.btn_connect_detail, false);
                    helper.setText(R.id.tv_connect_status, "未连接");
                    helper.setTextColor(R.id.tv_connect_status, ContextCompat.getColor(MainActivity.this, R.color.deBlack));
                    helper.setImageResource(R.id.iv_connect_status,R.drawable.unconnect);
                    helper.setBackgroundRes(R.id.btn_connect_status, R.drawable.button_gray_ripple);
                }
                helper.addOnClickListener(R.id.btn_connect_detail);
                helper.addOnClickListener(R.id.btn_connect_status);
            }
        };
        baseViewHolderBaseQuickAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            int id = view.getId();
            BleDevice bleDevice = bleDevices.get(position);
            MainRecyclerItem mainRecyclerItem = mainRecyclerItems.get(position);
            //没有开启蓝牙开启蓝牙
            if(!BleManager.getInstance().isBlueEnable()){
                blueTeethdialog.show();
                return;
            }
            //连接或者关闭
            if (R.id.btn_connect_status == id) {
                if (!mainRecyclerItem.isStatus()) {
                    BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
                        @Override
                        public void onStartConnect() {
                            progressDialog.setMessage("连接中...");

                            progressDialog.show();

                            DialogCenterUtil.setDialogCenter(progressDialog);
                        }

                        @Override
                        public void onConnectFail(BleDevice bleDevice, BleException exception) {
                            mainRecyclerItem.setStatus(false);
                            ToastUtil.showMsg(bleDevice.getMac()+" 连接失败", 2000);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                            mainRecyclerItem.setStatus(true);
                            ToastUtil.showMsg(bleDevice.getMac()+" 连接成功", 2000);
                            baseViewHolderBaseQuickAdapter.notifyItemChanged(position);
                            progressDialog.dismiss();

                        }

                        @Override
                        public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                            mainRecyclerItem.setStatus(false);
                            ToastUtil.showMsg(bleDevice.getMac()+" 连接断开", 2000);
                            baseViewHolderBaseQuickAdapter.notifyItemChanged(position);
                        }
                    });
                } else {
                    BleManager.getInstance().disconnect(bleDevice);
                }
            } else if (R.id.btn_connect_detail == id) {
                Intent intent = new Intent(MainActivity.this, BluetoothGattDetailActivity.class);
                intent.putExtra("bleDevice", bleDevice);
                startActivity(intent);
            }
        });
        mainRecyclerView.setAdapter(baseViewHolderBaseQuickAdapter);
    }

    private void initBTDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("提示");
        builder.setMessage("蓝牙未开启,点击确定启动蓝牙");
        builder.setPositiveButton("确定", (dialogInterface, i) -> {
            BleManager.getInstance().enableBluetooth();//开启蓝牙
            new Thread(() -> {//开启一个线程检测蓝牙是否开启
                enBtFlag = true;
                while (enBtFlag) {
                    if (BleManager.getInstance().isBlueEnable()) {
                        enBtFlag = false;
                        runOnUiThread(() -> firstScan());
                    }
                }
            }).start();

            dialogInterface.dismiss();
        });
        builder.setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss());
        blueTeethdialog = builder.create();
    }

    private void checkInitBLE() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setScanTimeOut(1000)              // 扫描时间，1S
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);


        boolean isSupportBle = BleManager.getInstance().isSupportBle();

        if (isSupportBle) {
            if (BleManager.getInstance().isBlueEnable()) {
                firstScan();
            } else {
                blueTeethdialog.show();
            }
        } else {
            ToastUtil.showMsg("抱歉，该设备不支持蓝牙", 2000);
        }
    }

    private void firstScan() {
        mainStatusView.showLoadingView();
        scanBTDevicesAndSet();//开启扫描
        isRefresh = false;
    }

    private void scanBTDevicesAndSet() {
        BleManager.getInstance().disconnectAllDevice();//先停止连接所有设备

        //开启扫描
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {

            }

            @Override
            public void onScanning(BleDevice bleDevice) {

            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

                bleDevices.clear();//

                mainRecyclerItems.clear();//

                for (BleDevice bleDevice : scanResultList) {
                    String bleName = bleDevice.getName();
                   /** if (!TextUtils.isEmpty(bleName)) {
                        if (bleName.contains("TWR")) {
                            MainRecyclerItem mainRecyclerItem = new MainRecyclerItem();
                            mainRecyclerItem.setName(bleName);
                            mainRecyclerItem.setMac(bleDevice.getMac());
                            mainRecyclerItems.add(mainRecyclerItem);
                            bleDevices.add(bleDevice);
                        }
                    }**/

                    //测试用
                MainRecyclerItem mainRecyclerItem = new MainRecyclerItem();
                    mainRecyclerItem.setName(bleName);
                    mainRecyclerItem.setMac(bleDevice.getMac());
                    mainRecyclerItems.add(mainRecyclerItem);
                    bleDevices.add(bleDevice);

                }
                baseViewHolderBaseQuickAdapter.notifyDataSetChanged();

                mainStatusView.showContentView();

                if (isRefresh) {
                    mainSwipeRefreshLayout.finishRefresh();
                    ToastUtil.showMsg("刷新成功", 2000);
                }

            }


        });
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }else{
            checkInitBLE();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
               if(permissions!=null&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                   checkInitBLE();
               }else{
                   ToastUtil.showMsg("拒绝次权限将无法扫描到蓝牙设备，请重新打开App申请权限",2000);
               }
               break;
            default:
                break;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().disconnectAllDevice();
        unbinder.unbind();
    }
}
