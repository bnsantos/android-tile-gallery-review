package com.bnsantos.tile.gallery.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruno on 11/06/15.
 */
public class GalleryTilePreview extends LinearLayout
{
    private final int MAX_TILES = 4;
    private int mTilesNumber;
    private boolean mShowMask;

    private SimpleDraweeView mMedia11;
    private SimpleDraweeView mMedia12;
    private SimpleDraweeView mMedia21;
    private SimpleDraweeView mMedia22;

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
            mShowMask = typedArray.getBoolean(R.styleable.GalleryTilePreview_showMask, false);
        } finally {
            typedArray.recycle();
        }
    }

    private void initViews(){
        inflate(getContext(), R.layout.layout, this);

        LinearLayout column2 = (LinearLayout) findViewById(R.id.column2);
        RelativeLayout layout1 = (RelativeLayout) findViewById(R.id.layout1);

        SimpleDraweeView media11 = (SimpleDraweeView) findViewById(R.id.media11);
        SimpleDraweeView media12 = (SimpleDraweeView) findViewById(R.id.media12);
        SimpleDraweeView media21 = (SimpleDraweeView) findViewById(R.id.media21);
        SimpleDraweeView media22 = (SimpleDraweeView) findViewById(R.id.media22);

        TextView mask1 = (TextView) findViewById(R.id.mask1);
        mask1.setVisibility(GONE);
        TextView mask2 = (TextView) findViewById(R.id.mask2);
        mask2.setVisibility(GONE);

        switch (mTilesNumber){
            case 1:
                media11.setVisibility(GONE);
                layout1.setVisibility(VISIBLE);
                column2.setVisibility(GONE);
                mMedia11 = media21;
                mMask = mask1;
                break;
            case 2:
                layout1.setVisibility(GONE);
                mMedia11 = media11;
                mMedia12 = media22;

                media12.setVisibility(GONE);
                mMask = mask2;
                break;
            case 3:
                layout1.setVisibility(GONE);
                mMedia11 = media11;
                mMedia12 = media12;
                mMedia22 = media22;
                mMask = mask2;
                break;
            default:
                mMedia11 = media11;
                mMedia12 = media12;
                mMedia21 = media21;
                mMedia22 = media22;
                mMask = mask2;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void loadFromUri(List<Uri> files){
        if(files==null||files.size()<mTilesNumber){
            //TODO throw exception
            return;
        }
        setDraweePicture(mMedia11, files.get(0));
        switch (mTilesNumber){
            case 2:
                setDraweePicture(mMedia12, files.get(1));
                break;
            case 3:
                setDraweePicture(mMedia12, files.get(1));
                setDraweePicture(mMedia22, files.get(2));
                break;
            case 4:
                setDraweePicture(mMedia12, files.get(1));
                setDraweePicture(mMedia21, files.get(2));
                setDraweePicture(mMedia22, files.get(3));
                break;
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
                .setAutoPlayAnimations(true)
                .setOldController(view.getController())
                .setTapToRetryEnabled(true)
                .build();

        view.setController(controller);
    }
}
