package com.bnsantos.tile.gallery.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruno on 11/06/15.
 */
public class GalleryTilePreview extends RelativeLayout
{
    private final int MAX_TILES = 4;

    private int mTilesNumber;
    private boolean mShowAnimation;
    private boolean mShowMask;

    private List<SimpleDraweeView> mSimpleDraweeViewList;

    private TextView mMask;

    public GalleryTilePreview(Context context) {
        super(context);
        initViews();
    }

    public GalleryTilePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initViews();
    }

    public GalleryTilePreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initViews();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GalleryTilePreview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
        initViews();
    }

    private void initAttrs(AttributeSet attrs){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.GalleryTilePreview);
        try {
            mTilesNumber = typedArray.getInteger(R.styleable.GalleryTilePreview_maxTiles, 1);
            if(mTilesNumber>MAX_TILES){
                mTilesNumber = MAX_TILES;
            }
            if(mTilesNumber<1){
                mTilesNumber = 1;
            }
            mShowMask = typedArray.getBoolean(R.styleable.GalleryTilePreview_showMask, false);
            mShowAnimation = typedArray.getBoolean(R.styleable.GalleryTilePreview_showAnimation, false);
            mSimpleDraweeViewList = new ArrayList<>();
        } finally {
            typedArray.recycle();
        }
    }

    private void initViews(){
        for(int i=0;i<mTilesNumber;i++){
            SimpleDraweeView simpleDraweeView = initSimpleDraweeView();
            mSimpleDraweeViewList.add(simpleDraweeView);
            addView(simpleDraweeView);
        }

        mMask = new TextView(getContext());
        mMask.setGravity(Gravity.CENTER);
        mMask.setTextSize(getResources().getDimensionPixelSize(R.dimen.font_size));
        mMask.setTextColor(Color.WHITE);
        mMask.setBackgroundResource(android.R.color.black);
        mMask.setAlpha(0.6f);
        mMask.setVisibility(GONE);
        addView(mMask);
    }

    @SuppressWarnings("deprecation")
    private SimpleDraweeView initSimpleDraweeView(){
        GenericDraweeHierarchyBuilder gdhBuilder = new GenericDraweeHierarchyBuilder(getContext().getResources())
                .setProgressBarImage(new ProgressBarDrawable())
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            gdhBuilder = gdhBuilder.setPlaceholderImage(getContext().getResources().getDrawable(android.R.color.holo_blue_bright, getContext().getTheme()))
                    .setFailureImage(getContext().getResources().getDrawable(android.R.color.holo_red_dark, getContext().getTheme()));
        }else{
            gdhBuilder = gdhBuilder.setPlaceholderImage(getContext().getResources().getDrawable(android.R.color.holo_blue_bright))
                    .setFailureImage(getContext().getResources().getDrawable(android.R.color.holo_red_dark));
        }
        return new SimpleDraweeView(getContext(), gdhBuilder.build());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        updateViewsLayoutParams(w, h);

    }

    private void updateViewsLayoutParams(int w, int h){
        for(int i=0;i<mSimpleDraweeViewList.size();i++){
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(tileWidth(w), tileHeight(i, h));
            layoutParams.addRule(tileRuleHorizontal(i));
            layoutParams.addRule(tileRuleVertical(i));
            mSimpleDraweeViewList.get(i).setLayoutParams(layoutParams);

            if(i==mSimpleDraweeViewList.size()-1){
                mMask.setLayoutParams(layoutParams);
            }
        }
    }

    private int tileWidth(int w){
        switch (mTilesNumber){
            case 1:
                return w;
            default:
                return w/2;
        }
    }

    private int tileHeight(int idx, int h){
        switch (mTilesNumber){
            case 3:
                if(idx==0){
                    return h;
                }else{
                    return h/2;
                }
            case 4:
                return h/2;
            default:
                return h;
        }
    }

    private int tileRuleHorizontal(int idx){
        switch (idx){
            case 1:
            case 3:
                return RelativeLayout.ALIGN_PARENT_RIGHT;
            case 2:
                if(mTilesNumber==3){
                    return RelativeLayout.ALIGN_PARENT_RIGHT;
                }
            default:
                return RelativeLayout.ALIGN_PARENT_LEFT;
        }
    }

    private int tileRuleVertical(int idx){
        switch (idx){
            case 2:
            case 3:
                return RelativeLayout.ALIGN_PARENT_BOTTOM;
            default:
                return RelativeLayout.ALIGN_PARENT_TOP;
        }
    }

    public void loadFromUri(List<Uri> files){
        if(files==null||files.size()<mTilesNumber){
            //TODO throw exception
            return;
        }
        for(int i=0;i<mSimpleDraweeViewList.size();i++){
            setDraweePicture(mSimpleDraweeViewList.get(i), files.get(i));
        }
        if(mShowMask&&files.size()>mTilesNumber){
            mMask.setVisibility(VISIBLE);
            mMask.setText("+" + (files.size() - mTilesNumber));
        }
    }

    public void loadFromString(List<String> pictures){
        List<Uri> uriList = new ArrayList<>();
        for (int i = 0; i < pictures.size(); i++) {
            uriList.add(Uri.parse(pictures.get(i)));
        }
        loadFromUri(uriList);
    }

    private void setDraweePicture(SimpleDraweeView view, Uri uri){
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(mShowAnimation)
                .setOldController(view.getController())
                .setTapToRetryEnabled(true)
                .build();
        view.setController(controller);
    }
}
