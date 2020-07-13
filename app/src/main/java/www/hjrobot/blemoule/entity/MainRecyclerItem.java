package www.hjrobot.blemoule.entity;

/**
 * @author WangYong
 * create at 2020/5/20 14:42
 * @Description
 */
public class MainRecyclerItem {
    private String name;
    private String mac;
    private boolean status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
