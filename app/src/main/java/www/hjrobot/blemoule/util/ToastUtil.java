package www.hjrobot.blemoule.util;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;


import www.hjrobot.blemoule.app.BLEApplication;


public class ToastUtil {

    public static Toast toast;

    private static Handler handler=new Handler();


    private static Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(toast!=null) {
                toast.cancel();
                toast = null;
            }
        }
    };

    public static void showMsg(@NonNull String msg, int time){

            handler.removeCallbacks(runnable);
            if(toast==null){
                toast= Toast.makeText(BLEApplication.getContext(),msg, Toast.LENGTH_LONG);
            }
            toast.setText(msg);
            toast.show();
            handler.postDelayed(runnable,time);
    }

}
