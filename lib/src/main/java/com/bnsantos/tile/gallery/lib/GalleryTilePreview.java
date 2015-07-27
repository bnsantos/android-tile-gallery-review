package com.bnsantos.tile.gallery.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.AutoRotateDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruno on 11/06/15.
 */
public class GalleryTilePreview extends RelativeLayout{
    private final int MAX_TILES = 4;

    private int mTilesNumber;
    private boolean mShowAnimation;
    private boolean mShowMask;
    private int mPlaceHolderImage = 0;
    private int mFailureImage = 0;
    private int mProgressBarImage = 0;
    private int mProgressBarAutoRotateInterval = 0;

    private int mWidth;
    private int mHeight;

    private List<SimpleDraweeView> mSimpleDraweeViewList;

    private TextView mMask;

    private OnItemClickListener mItemClickListener;

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

            mPlaceHolderImage = typedArray.getResourceId(R.styleable.GalleryTilePreview_placeholderRes, 0);
            mFailureImage = typedArray.getResourceId(R.styleable.GalleryTilePreview_failureRes, 0);
            mProgressBarImage = typedArray.getResourceId(R.styleable.GalleryTilePreview_progressBarRes, 0);
            mProgressBarAutoRotateInterval = typedArray.getInteger(R.styleable.GalleryTilePreview_progressBarAutoRotateIntervalSeconds, 0);
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

    private SimpleDraweeView initSimpleDraweeView(){
        GenericDraweeHierarchyBuilder gdhBuilder = new GenericDraweeHierarchyBuilder(getContext().getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        if(mPlaceHolderImage>0){
            gdhBuilder = gdhBuilder.setPlaceholderImage(getDrawable(mPlaceHolderImage));
        }
        if(mFailureImage>0){
            gdhBuilder = gdhBuilder.setFailureImage(getDrawable(mFailureImage));
        }
        if(mProgressBarImage>0){
            gdhBuilder = gdhBuilder.setProgressBarImage(new AutoRotateDrawable(getDrawable(mProgressBarImage), mProgressBarAutoRotateInterval));
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
        mWidth = w;
        mHeight = h;
        updateViewsLayoutParams();

    }

    private void updateViewsLayoutParams(){
        for(int i=0;i<mSimpleDraweeViewList.size();i++){
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(tileWidth(mWidth), tileHeight(i, mHeight));
            layoutParams.addRule(tileRuleHorizontal(i));
            layoutParams.addRule(tileRuleVertical(i));
            mSimpleDraweeViewList.get(i).setLayoutParams(layoutParams);
            final int index = i;
            mSimpleDraweeViewList.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener!=null){
                        mItemClickListener.onClick(v, index==mSimpleDraweeViewList.size()-1, index);
                    }
                }
            });

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateViewsLayoutParams();
    }

    @SuppressWarnings("deprecation")
    private Drawable getDrawable(int res){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            return getContext().getResources().getDrawable(res, getContext().getTheme());
        }else{
            return getContext().getResources().getDrawable(res);
        }
    }

    public void setItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        void onClick(View v, boolean clickedOnMask, int index);
    }
}
