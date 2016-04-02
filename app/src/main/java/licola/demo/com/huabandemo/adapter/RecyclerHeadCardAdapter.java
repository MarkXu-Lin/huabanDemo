package licola.demo.com.huabandemo.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

import java.util.ArrayList;
import java.util.List;

import licola.demo.com.huabandemo.R;
import licola.demo.com.huabandemo.Util.Utils;
import licola.demo.com.huabandemo.bean.PinsEntity;
import licola.demo.com.huabandemo.httpUtils.ImageLoadFresco;
import licola.demo.com.huabandemo.view.ViewHolder.ViewHolderGeneral;
import licola.demo.com.huabandemo.view.recyclerview.RecyclerViewUtils;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.ViewGroup.VISIBLE;


/**
 * Created by LiCola on  2016/03/22  18:00
 * 负责展示  图片CardView 的adapter
 */
public class RecyclerHeadCardAdapter extends RecyclerView.Adapter {
    private final String life = "Life";
    private RecyclerView mRecyclerView;
    private Context mContext;
    private boolean mIsShowUser = false;//是否显示用户头像和名字的标志位
    private List<PinsEntity> mList = new ArrayList<>(20);
    private OnAdapterListener mListener;
    private final String url_root;
    public int mAdapterPosition = 0;

    public List<PinsEntity> getmList() {
        return mList;
    }

    public void setList(List<PinsEntity> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    public void addList(List<PinsEntity> mList) {
        this.mList.addAll(mList);
        notifyDataSetChanged();
    }

    public int getAdapterPosition() {
        return mAdapterPosition;
    }

    public void setAdapterPosition(int mAdapterPosition) {
        this.mAdapterPosition = mAdapterPosition;
    }


    public interface OnAdapterListener {
        void onClickImage(PinsEntity bean, View view);

        void onClickTitleInfo(PinsEntity bean, View view);

        void onClickInfoGather(PinsEntity bean, View view);

        void onClickInfoLike(PinsEntity bean, View view);

    }

    public RecyclerHeadCardAdapter(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        this.mContext = recyclerView.getContext();
        this.url_root = mContext.getResources().getString(R.string.urlImageRoot);
    }

    //多一个标志位的 构造函数
    public RecyclerHeadCardAdapter(RecyclerView recyclerView, boolean isShowUser) {
        this(recyclerView);
        this.mIsShowUser = isShowUser;
    }


    public void setOnClickItemListener(OnAdapterListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Logger.d(life);
        ViewHolderGeneral holder = null;//ViewHolder的子类

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_item_image, parent, false);
        holder = new ViewHolderGeneral(view);//使用子类初始化ViewHolder

        holder.tv_card_like.setCompoundDrawablesWithIntrinsicBounds(
                Utils.getTintCompatDrawable(mContext, R.drawable.ic_favorite_black_18dp, R.color.tint_list_grey),
                null,
                null,
                null);
        holder.tv_card_gather.setCompoundDrawablesWithIntrinsicBounds(
                Utils.getTintCompatDrawable(mContext, R.drawable.ic_explore_black_18dp, R.color.tint_list_grey),
                null,
                null,
                null);

        //子类可以自动转型为父类
        return holder;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        //当有被被回收viewHolder调用
        //Logger.d(life);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        //Logger.d(life);
        final PinsEntity bean = mList.get(position);

        //注释的是 动态修改image高度
//        LayoutParams lp = holder.img_card_image.getLayoutParams();
//        lp.height = height[mAdapterPosition];
//        holder.img_card_image.setLayoutParams(lp);

        //父类强制转换成子类 因为这个holder本来就是子类初始化的 所以可以强转
        ViewHolderGeneral viewHolder = (ViewHolderGeneral) holder;//强制类型转换 转成内部的ViewHolder

        onBindData(viewHolder, bean);
        onBindListener(viewHolder, bean);//初始化点击事件

    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        //Logger.d(life);
//        Logger.d(holder.toString());
//        mAdapterPosition = holder.getAdapterPosition();

//        Logger.d("mAdapterPosition="+mAdapterPosition);
        //这是添加headView后 需要修正的position位置
        mAdapterPosition = RecyclerViewUtils.getAdapterPosition(mRecyclerView, holder);
    }

    private void onBindData(final ViewHolderGeneral holder, PinsEntity bean) {
        //检查图片信息
        if (checkInfoContext(bean)) {
            holder.ll_title_info.setVisibility(VISIBLE);

            String title = bean.getRaw_text();//图片的文字描述
            int like = bean.getLike_count();//被喜欢数量
            int gather = bean.getRepin_count();//被转采的数量
            if (!TextUtils.isEmpty(title)) {
                holder.tv_card_title.setVisibility(VISIBLE);
                holder.tv_card_title.setText(title);
            } else {
                holder.tv_card_title.setVisibility(GONE);
            }
            holder.tv_card_like.setText(" " + like);
            holder.tv_card_gather.setText(" " + gather);
        } else {
            holder.ll_title_info.setVisibility(GONE);
        }

        String url_img = url_root + bean.getFile().getKey()+"_fw320sf";

//        String mImageUrl = "http://img.hb.aicdn.com/1d16a79ac7cffbec844eb48e7e714c9f8c0afffc7f997-ZZCJsm";

        float ratio = Utils.getAspectRatio(bean.getFile().getWidth(), bean.getFile().getHeight());
        //长图 "width":440,"height":5040,
        holder.img_card_image.setAspectRatio(ratio);//设置宽高比
        Drawable dProgressImage =
                Utils.getTintCompatDrawable(mContext, R.drawable.ic_toys_black_48dp, R.color.tint_list_pink);

        new ImageLoadFresco.LoadImageFrescoBuilder(mContext, holder.img_card_image, url_img)
                .setProgressBarImage(dProgressImage)
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        if (animatable != null) {
                            holder.ibtn_card_gif.setVisibility(VISIBLE);
                            setPlayListener(holder, animatable);
                            setPlayDrawable(holder, false);
                        } else {
                            holder.ibtn_card_gif.setVisibility(INVISIBLE);
                        }
                    }
                })
                .build();
    }

    /**
     * 检查三项信息 任何一项不为空 都返回true
     *
     * @param bean
     * @return
     */
    private boolean checkInfoContext(PinsEntity bean) {

        String title = bean.getRaw_text();//图片的文字描述
        int like = bean.getLike_count();//被喜欢数量
        int gather = bean.getRepin_count();//被转采的数量

        if (!TextUtils.isEmpty(title)) {
            return true;
        } else if (like > 0 || gather > 0) {
            return true;
        }

        return false;
    }

    private void onBindListener(ViewHolderGeneral holder, final PinsEntity bean) {

        holder.rl_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickImage(bean, v);
            }
        });

        holder.ll_title_info.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickTitleInfo(bean, v);
            }
        });

        holder.tv_card_gather.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickInfoGather(bean, v);
            }
        });

        holder.tv_card_like.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickInfoLike(bean, v);
            }
        });
    }

    /**
     * 设置ibtn_card_gif的显示
     * 传true就显示暂停 传false显示播放
     *
     * @param holder
     * @param isRunning 默认false为不播放
     */
    private void setPlayDrawable(ViewHolderGeneral holder, boolean isRunning) {
        if (!isRunning) {
            Drawable drawable = holder.ibtn_card_gif.getResources().getDrawable(android.R.drawable.ic_media_play);
            holder.ibtn_card_gif.setImageDrawable(drawable);
        } else {
            Drawable drawable = holder.ibtn_card_gif.getResources().getDrawable(android.R.drawable.ic_media_pause);
            holder.ibtn_card_gif.setImageDrawable(drawable);
        }
    }

    private void setPlayListener(final ViewHolderGeneral holder, final Animatable animatable) {
        holder.ibtn_card_gif.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!animatable.isRunning()) {
                    animatable.start();
                    setPlayDrawable(holder, true);
                } else {
                    animatable.stop();
                    setPlayDrawable(holder, false);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }





}