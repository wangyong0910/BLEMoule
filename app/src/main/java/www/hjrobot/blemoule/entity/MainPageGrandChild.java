package www.hjrobot.blemoule.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import www.hjrobot.blemoule.adapter.NewMainPagerAdapter;

/**
 * @author WangYong
 * create at 2020/5/22 10:34
 * @Description
 */
public class MainPageGrandChild implements MultiItemEntity {
    @Override
    public int getItemType() {
        return NewMainPagerAdapter.LEVEL_GRAND_CHILD;
    }
}
