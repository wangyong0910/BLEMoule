package www.hjrobot.blemoule.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import www.hjrobot.blemoule.app.BLEApplication;

/**
 * @author WangYong
 * create at 2020/6/30 17:09
 * @Description
 */
public class DialogCenterUtil {

    public static void setDialogCenter(ProgressDialog progressDialog){
        //解决居中问题
        Window dialogWindow =progressDialog.getWindow();

        WindowManager windowManager= dialogWindow.getWindowManager();

        Display d = windowManager.getDefaultDisplay(); // 获取屏幕宽、高

        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        // 设置宽度
        p.width = d.getWidth()*1; // 宽度设置为屏幕的0.95

        // 设置宽度
        p.gravity = Gravity.CENTER;//设置位置

        dialogWindow.setAttributes(p);
    }
}
