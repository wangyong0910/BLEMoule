package www.hjrobot.blemoule.activity;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import www.hjrobot.blemoule.R;
import www.hjrobot.blemoule.adapter.NewMainPagerAdapter;
import www.hjrobot.blemoule.callback.CustomCommitClick;
import www.hjrobot.blemoule.entity.MainPageChild;
import www.hjrobot.blemoule.entity.MainPageFather;
import www.hjrobot.blemoule.entity.MainPageGrandChild;
import www.hjrobot.blemoule.util.BytesUtil;
import www.hjrobot.blemoule.util.DialogCenterUtil;
import www.hjrobot.blemoule.util.StatusBarUtil;
import www.hjrobot.blemoule.util.ToastUtil;
import www.hjrobot.blemoule.widget.CustomApprovalDialog;

public class BluetoothGattDetailActivity extends AppCompatActivity {

    @BindView(R.id.gatt_recyclerview)
    RecyclerView gattRecyclerView;
    @BindView(R.id.tv_header_title)
    TextView tvHeaderTitle;
    List<MultiItemEntity> multiItemEntities = new ArrayList<>();

    NewMainPagerAdapter mainPagerAdapter;

    Unbinder unbinder;

    BleDevice bleDevice;

    CustomApprovalDialog customApprovalDialog;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_gatt_detail);
        StatusBarUtil.setStatusBarDarkTheme(this,true);
        unbinder = ButterKnife.bind(this);
        initRecyclerView();
        initWirteDialog();
        initData();
    }

    private void initWirteDialog() {
        progressDialog = new ProgressDialog(this);
        customApprovalDialog = new CustomApprovalDialog(this,this);
        customApprovalDialog.setListener(new CustomCommitClick() {
            @Override
            public void clickCommit(String startTime, String endTime, String tagId, String name, String dataId, String tagTypeId) {

            }

            @Override
            public void clickCommit() {

            }

            @Override
            public void clickCommit(String etApprovalComment, MainPageChild mainPageChild, BleDevice bleDevice) {
                if (TextUtils.isEmpty(etApprovalComment)) {
                    ToastUtil.showMsg("还没有输入需要写入的数值", 2000);
                    return;
                }
                if (Integer.parseInt(etApprovalComment) > 2000) {
                    ToastUtil.showMsg("输入的数值区间为0~2000", 2000);
                    return;
                }
                progressDialog.setMessage("正在写入数据...");

                progressDialog.show();

                DialogCenterUtil.setDialogCenter(progressDialog);

                //写数据

                BleManager.getInstance().write(
                        bleDevice,
                        mainPageChild.getFatherUUid(),
                        mainPageChild.getUuid(),
                        BytesUtil.short2BytesLL(Short.parseShort(etApprovalComment)),
                        new BleWriteCallback() {
                            @Override
                            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                progressDialog.dismiss();
                                customApprovalDialog.dismiss();
                                // 发送数据到设备成功（分包发送的情况下，可以通过方法中返回的参数可以查看发送进度）
                                ToastUtil.showMsg("写入数据成功", 2000);
                            }

                            @Override
                            public void onWriteFailure(BleException exception) {
                                progressDialog.dismiss();
                                customApprovalDialog.dismiss();
                                // 写入设备失败
                                ToastUtil.showMsg(exception.getDescription(), 2000);
                            }
                        });
            }
        });

    }


    private void initRecyclerView() {
        mainPagerAdapter = new NewMainPagerAdapter(multiItemEntities);
        gattRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainPagerAdapter.openLoadAnimation();
        gattRecyclerView.setAdapter(mainPagerAdapter);
        mainPagerAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            int viewId = view.getId();
            MainPageChild mainPageChild = (MainPageChild) multiItemEntities.get(position);
            if (viewId == R.id.btn_write) {
                customApprovalDialog.setData(mainPageChild, bleDevice);
                customApprovalDialog.customShow(0.5f);
            } else if (viewId == R.id.btn_read) {
                progressDialog.setMessage("正在读取数据...");
                progressDialog.show();

                DialogCenterUtil.setDialogCenter(progressDialog);


                BleManager.getInstance().read(
                        bleDevice,
                        mainPageChild.getFatherUUid(),
                        mainPageChild.getUuid(),
                        new BleReadCallback() {
                            @Override
                            public void onReadSuccess(byte[] data) {
                                progressDialog.dismiss();
                                // 读特征值数据成功
                                ToastUtil.showMsg("读取数值成功", 2000);

                                mainPageChild.setValue(String.valueOf(BytesUtil.bytes2ShortLL(data)));

                                /** //测试
                                for (byte datum : data) {
                                    mainPageChild.setValue(datum + "  ");
                                }**/
                                mainPagerAdapter.notifyItemChanged(position);
                            }

                            @Override
                            public void onReadFailure(BleException exception) {
                                progressDialog.dismiss();
                                // 读特征值数据失败
                                ToastUtil.showMsg(exception.getDescription(), 2000);
                            }
                        });
            }
        });

    }

    private void initData() {

        bleDevice = getIntent().getParcelableExtra("bleDevice");

        if (BleManager.getInstance().isConnected(bleDevice)) {


            tvHeaderTitle.setText((TextUtils.isEmpty(bleDevice.getName())?"":bleDevice.getName())+"设备详情");

            BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);

            List<BluetoothGattService> serviceList = gatt.getServices();

          /*  for (BluetoothGattService service : serviceList) {
                String serviceUUID = service.getUuid().toString();
                if (serviceUUID.startsWith("00001523")) {
                    MainPageFather mainPageFather = new MainPageFather();
                    mainPageFather.setUuid(serviceUUID);
                    mainPageFather.setName("LED按钮服务");
                    List<BluetoothGattCharacteristic> characteristicList = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristicList) {
                        String charaUUID = characteristic.getUuid().toString();
                        if (charaUUID.startsWith("00001525")) {
                            MainPageChild mainPageChild = new MainPageChild();
                            mainPageChild.setUuid(charaUUID);
                            mainPageChild.setFatherUUid(serviceUUID);
                            mainPageChild.setName("触发距离");
                            mainPageFather.addSubItem(mainPageChild);
                        } else if (charaUUID.startsWith("00001526")) {
                            MainPageChild mainPageChild = new MainPageChild();
                            mainPageChild.setUuid(charaUUID);
                            mainPageChild.setFatherUUid(serviceUUID);
                            mainPageChild.setName("触发刷新频率");
                            mainPageFather.addSubItem(mainPageChild);
                        }
                    }
                    multiItemEntities.add(mainPageFather);
                    MainPageGrandChild mainPageGrandChild = new MainPageGrandChild();
                    multiItemEntities.add(mainPageGrandChild);
                }
            }
            mainPagerAdapter.notifyDataSetChanged();
            mainPagerAdapter.expandAll();*/


            for (BluetoothGattService service : serviceList) {
                String serviceUUID = service.getUuid().toString();
                    MainPageFather mainPageFather = new MainPageFather();
                    mainPageFather.setUuid(serviceUUID);
                     mainPageFather.setName("xxxxxx");
                    List<BluetoothGattCharacteristic> characteristicList = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristicList) {
                        String charaUUID = characteristic.getUuid().toString();
                            MainPageChild mainPageChild = new MainPageChild();
                            mainPageChild.setUuid(charaUUID);
                            mainPageChild.setName("xxxxxxx");
                            mainPageChild.setFatherUUid(serviceUUID);
                            mainPageFather.addSubItem(mainPageChild);
                    }
                    multiItemEntities.add(mainPageFather);
                    MainPageGrandChild mainPageGrandChild = new MainPageGrandChild();
                    multiItemEntities.add(mainPageGrandChild);
                }
            mainPagerAdapter.notifyDataSetChanged();
            mainPagerAdapter.expandAll();

        } else {
            ToastUtil.showMsg("设备未连接，请返回设备列表重新连接", 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.iv_header_back})
    public void clickViews(){
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
