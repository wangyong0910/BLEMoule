package www.hjrobot.blemoule.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import www.hjrobot.blemoule.R;
import www.hjrobot.blemoule.entity.MainPageChild;
import www.hjrobot.blemoule.entity.MainPageFather;


/**
 * @author WangYong
 * create at 2020/3/23 13:21
 * @Description
 */
public class NewMainPagerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int LEVEL_FATHER = 2;
    public static final int LEVEL_CHILD = 1;
    public static final int LEVEL_GRAND_CHILD=0;

    public NewMainPagerAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(LEVEL_FATHER, R.layout.detail_recycler_father);
        addItemType(LEVEL_CHILD, R.layout.detail_recycler_son);
        addItemType(LEVEL_GRAND_CHILD,R.layout.detail_recycler_grandchild);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        int type = helper.getItemViewType();
        if (type == LEVEL_FATHER) {

            MainPageFather mainPageFather = (MainPageFather) item;
            helper.setText(R.id.tv_father_name, mainPageFather.getName());
            helper.setText(R.id.tv_father_uuid, mainPageFather.getUuid());
                /*help.itemView.setOnClickListener(v->{
                    int pos=help.getAdapterPosition();
                    if(equipTreeFather.isExpanded()){
                        collapse(pos);
                    }else{
                        expand(pos);
                    }
                });*/

        } else if (type == LEVEL_CHILD) {
            MainPageChild mainPageChild = (MainPageChild) item;
            helper.setText(R.id.tv_son_name, mainPageChild.getName());
            helper.setText(R.id.tv_son_uuid, mainPageChild.getUuid());
            helper.setText(R.id.tv_son_value,mainPageChild.getValue());
            helper.addOnClickListener(R.id.btn_write);
            helper.addOnClickListener(R.id.btn_read);
        }
    }
}
