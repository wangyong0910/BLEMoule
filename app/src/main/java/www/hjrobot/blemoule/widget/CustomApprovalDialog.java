package www.hjrobot.blemoule.widget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;

import www.hjrobot.blemoule.R;
import www.hjrobot.blemoule.callback.CustomCommitClick;
import www.hjrobot.blemoule.entity.MainPageChild;
import www.hjrobot.blemoule.util.DialogCenterUtil;


/**
 * @author WangYong
 * create at 2019/7/8 9:19
 * @Description
 */
public class CustomApprovalDialog extends ProgressDialog implements View.OnClickListener {

    private Context mContext;
    private View approvalCommnetView;
    private Activity activity;

    private CustomCommitClick customCommitClick;

    EditText etDialogApprovalComment;
    MainPageChild mainPageChild;
    BleDevice bleDevice;

    public CustomApprovalDialog(Context context,Activity activity) {
        this(context, 0);
        this.activity=activity;
    }

    public CustomApprovalDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        init();
    }

    public void setListener(CustomCommitClick customCommitClick) {
        this.customCommitClick = customCommitClick;
    }

    private void init() {
        approvalCommnetView = LayoutInflater.from(mContext).inflate(R.layout.approval_comment_view, null);
        etDialogApprovalComment=approvalCommnetView.findViewById(R.id.et_dialog_approval_comment);
        TextView btn_approval_commnet_cancel=approvalCommnetView.findViewById(R.id.btn_approval_commnet_cancel);
        Button btn_approval_commnet_commit = approvalCommnetView.findViewById(R.id.btn_approval_commnet_commit);

        btn_approval_commnet_cancel.setOnClickListener(this);
        btn_approval_commnet_commit.setOnClickListener(this);
    }


    public CustomApprovalDialog customShow(float dimAmount) {
        this.show();
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(approvalCommnetView, layoutParams);

        Window dialogWindow =getWindow();

        DialogCenterUtil.setDialogCenter(this);

        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);//软键盘弹出
        dialogWindow.setDimAmount(dimAmount);//去除蒙层
        dialogWindow.setBackgroundDrawable(null);//去除背景
        return this;
    }

    public void setData(MainPageChild mainPageChild, BleDevice bleDevice)
    {
        this.mainPageChild=mainPageChild;
        this.bleDevice=bleDevice;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_approval_commnet_cancel) {
            dismiss();
        } else if (id == R.id.btn_approval_commnet_commit) {
            customCommitClick.clickCommit(etDialogApprovalComment.getText().toString(),mainPageChild,bleDevice);
        }
    }

}
