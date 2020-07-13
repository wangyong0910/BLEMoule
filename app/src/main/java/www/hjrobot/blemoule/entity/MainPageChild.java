package www.hjrobot.blemoule.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.lang.ref.PhantomReference;

import www.hjrobot.blemoule.adapter.NewMainPagerAdapter;

/**
 * @author WangYong
 * create at 2020/3/23 13:20
 * @Description
 */
public class MainPageChild implements MultiItemEntity {
    private String name;
    private String uuid;
    private String fatherUUid;
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFatherUUid() {
        return fatherUUid;
    }

    public void setFatherUUid(String fatherUUid) {
        this.fatherUUid = fatherUUid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getItemType() {
        return NewMainPagerAdapter.LEVEL_CHILD;
    }
}
