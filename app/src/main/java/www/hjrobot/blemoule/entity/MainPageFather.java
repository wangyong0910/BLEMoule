package www.hjrobot.blemoule.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import www.hjrobot.blemoule.adapter.NewMainPagerAdapter;


/**
 * @author WangYong
 * create at 2020/3/23 13:20
 * @Description
 */
public class MainPageFather extends AbstractExpandableItem<MainPageChild> implements MultiItemEntity {
    private String uuid;
    private String name;

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
    public int getLevel() {
        return 1;
    }

    @Override
    public int getItemType() {
        return NewMainPagerAdapter.LEVEL_FATHER;
    }
}
