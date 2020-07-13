package www.hjrobot.blemoule.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**https://blog.csdn.net/u014418171/article/details/81223681
 * @author WangYong
 * create at 2020/5/28 15:18
 * @Description
 */
public class StatusBarUtil {

    public static final  int TYPE_MIUI = 0;
    public static final  int TYPE_FLYME = 1;
    public static final  int TYPE_M = 3;//6.0


    //顶部状态栏透明
    public static void setStatusBar(Activity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上透明状态栏
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN//在不隐藏StatusBar的情况下，将view所在window的显示范围扩展到StatusBar下面。同时Activity的部分内容也因此被StatusBar覆盖遮挡。
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;//当StatusBar和NavigationBar动态显示和隐藏时，系统为fitSystemWindow=true的view设置的padding大小都不会变化，所以view的内容的位置也不会发生移动。
            decorView.setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//View.SYSTEM_UI_FLAG_LAYOUT_STABLE和View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }
    //底部导航栏透明
    public static void setNavagationBar(Activity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上透明状态栏
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION//NavagationBar
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;//当StatusBar和NavigationBar动态显示和隐藏时，系统为fitSystemWindow=true的view设置的padding大小都不会变化，所以view的内容的位置也不会发生移动。
            decorView.setSystemUiVisibility(option);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//NavagationBar
        }
    }


    //android:fitsSystemWindows

    public static void setRootViewFitsSystemWindows(Activity activity, boolean fitSystemWindow) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup mContentView = activity.findViewById(Window.ID_ANDROID_CONTENT);
            if (mContentView.getChildCount() > 0) {
                ViewGroup rootView = (ViewGroup) mContentView.getChildAt(0);
                if (rootView != null) {
                    rootView.setFitsSystemWindows(fitSystemWindow);
                }
            }
        }
    }

    //setStatusBarColor
    public static void setStatusBarColor(Activity activity, int colorId) {
        ViewGroup mContentView = activity.findViewById(Window.ID_ANDROID_CONTENT);
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = activity.getResources().getDimensionPixelSize(resourceId);
        }

        //给statusbar着色
        View view = new View(activity);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight1));
        view.setBackgroundColor(ContextCompat.getColor(activity, colorId));
        mContentView.addView(view);
    }



    /**
     * 设置状态栏深色浅色切换
     */
    public static boolean setStatusBarDarkTheme(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setStatusBarFontIconDark(activity, TYPE_M, dark);
            } else if (OSUtils.isMiui()) {
                setStatusBarFontIconDark(activity, TYPE_MIUI, dark);
            } else if (OSUtils.isFlyme()) {
                setStatusBarFontIconDark(activity, TYPE_FLYME, dark);
            } else {//其他情况
                return false;
            }

            return true;
        }
        return false;
    }

    /**
     * 设置 状态栏深色浅色切换
     */
    public static boolean setStatusBarFontIconDark(Activity activity, int type, boolean dark) {
        switch (type) {
            case TYPE_MIUI:
                return setMiuiUI(activity, dark);
            case TYPE_FLYME:
                return setFlymeUI(activity, dark);
            case TYPE_M:
            default:
                return setCommonUI(activity,dark);
        }
    }

    //设置6.0 状态栏深色浅色切换
    public static boolean setCommonUI(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = activity.getWindow().getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                if (dark) {
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                if (decorView.getSystemUiVisibility() != vis) {
                    decorView.setSystemUiVisibility(vis);
                }
                return true;
            }
        }
        return false;

    }

    //设置Flyme 状态栏深色浅色切换
    public static boolean setFlymeUI(Activity activity, boolean dark) {
        try {
            Window window = activity.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //设置MIUI 状态栏深色浅色切换
    public static boolean setMiuiUI(Activity activity, boolean dark) {
        try {
            Window window = activity.getWindow();
            Class<?> clazz = activity.getWindow().getClass();
            @SuppressLint("PrivateApi") Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getDeclaredMethod("setExtraFlags", int.class, int.class);
            extraFlagField.setAccessible(true);
            if (dark) {    //状态栏亮色且黑色字体
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
