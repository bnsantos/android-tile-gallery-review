package com.bnsantos.tile.gallery.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
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

    private LinearLayout mColumn1;
    private LinearLayout mColumn2;

    private SimpleDraweeView mMedia11;
    private SimpleDraweeView mMedia12;
    private SimpleDraweeView mMedia21;
    private SimpleDraweeView mMedia22;

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
        } finally {
            typedArray.recycle();
        }
    }

    private void initViews(){
        inflate(getContext(), R.layout.layout, this);
        mColumn1 = (LinearLayout) findViewById(R.id.column1);
        mColumn2 = (LinearLayout) findViewById(R.id.column2);

        mMedia11 = (SimpleDraweeView) findViewById(R.id.media11);
        mMedia12 = (SimpleDraweeView) findViewById(R.id.media12);
        mMedia21 = (SimpleDraweeView) findViewById(R.id.media21);
        mMedia22 = (SimpleDraweeView) findViewById(R.id.media22);

        switch (mTilesNumber){
            case 1:
                mMedia12.setVisibility(GONE);
                mMedia21.setVisibility(GONE);
                mMedia22.setVisibility(GONE);
                mColumn2.setVisibility(GONE);
                break;
            case 2:
                mMedia21.setVisibility(GONE);
                mMedia22.setVisibility(GONE);
                break;
            case 3:
                mMedia21.setVisibility(GONE);
                break;
            default:
                mMedia12.setVisibility(VISIBLE);
                mMedia21.setVisibility(VISIBLE);
                mMedia22.setVisibility(VISIBLE);
                mColumn2.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void loadFromUri(List<Uri> files){
        if(files==null||files.size()<mTilesNumber){
            //TODO
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
