package www.hjrobot.blemoule.callback;

import com.clj.fastble.data.BleDevice;

import www.hjrobot.blemoule.entity.MainPageChild;

/**
 * @author WangYong
 * create at 2019/10/27 8:36
 * @Description
 */
public interface CustomCommitClick {
    void clickCommit(String startTime, String endTime, String tagId, String name, String dataId, String tagTypeId);
    void clickCommit();
    void clickCommit(String etApprovalComment, MainPageChild mainPageChild, BleDevice bleDevice);
}
