package www.hjrobot.blemoule.app;

import android.app.Application;
import android.content.Context;

import com.clj.fastble.BleManager;

/**
 * @author WangYong
 * create at 2020/5/19 17:59
 * @Description
 */
public class BLEApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=getApplicationContext();
        //BLE初始化
        BleManager.getInstance().init(this);
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 3000)
                .setOperateTimeout(5000);
    }

    public static Context getContext(){
        return mContext;
    }
}
