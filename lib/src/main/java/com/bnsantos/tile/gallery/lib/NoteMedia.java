package com.bnsantos.tile.gallery.lib;

import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by bruno on 27/07/15.
 */
public class NoteMedia extends RelativeLayout {
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

    public NoteMedia(Context context) {
        super(context);
        initViews();
    }

    public NoteMedia(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initViews();
    }

    public NoteMedia(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initViews();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NoteMedia(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
        initViews();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.NoteMedia);
        try {
            mTilesNumber = typedArray.getInteger(R.styleable.NoteMedia_maxTiles, 1);
            if (mTilesNumber > MAX_TILES) {
                mTilesNumber = MAX_TILES;
            }
            if (mTilesNumber < 1) {
                mTilesNumber = 1;
            }
            mShowMask = typedArray.getBoolean(R.styleable.NoteMedia_showMask, false);
            mShowAnimation = typedArray.getBoolean(R.styleable.NoteMedia_showAnimation, false);

            mPlaceHolderImage = typedArray.getResourceId(R.styleable.NoteMedia_placeholderRes, 0);
            mFailureImage = typedArray.getResourceId(R.styleable.NoteMedia_failureRes, 0);
            mProgressBarImage = typedArray.getResourceId(R.styleable.NoteMedia_progressBarRes, 0);
            mProgressBarAutoRotateInterval = typedArray.getInteger(R.styleable.NoteMedia_progressBarAutoRotateIntervalSeconds, 0);
        } finally {
            typedArray.recycle();
        }
    }

    private void initViews() {
        removeAllViews();
        mSimpleDraweeViewList = new ArrayList<>();
        for (int i = 0; i < mTilesNumber; i++) {
            SimpleDraweeView simpleDraweeView = initSimpleDraweeView();
            mSimpleDraweeViewList.add(simpleDraweeView);
            addView(simpleDraweeView);
        }

        mMask = new TextView(getContext());
        mMask.setGravity(Gravity.CENTER);
        mMask.setTextSize(getResources().getDimensionPixelSize(R.dimen.xxxl_text_size));
        mMask.setTextColor(Color.WHITE);
        mMask.setBackgroundResource(android.R.color.black);
        mMask.setAlpha(0.6f);
        mMask.setVisibility(GONE);
        addView(mMask);
    }

    private SimpleDraweeView initSimpleDraweeView() {
        GenericDraweeHierarchyBuilder gdhBuilder = new GenericDraweeHierarchyBuilder(getContext().getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        if (mPlaceHolderImage > 0) {
            gdhBuilder = gdhBuilder.setPlaceholderImage(getDrawable(mPlaceHolderImage));
        }
        if (mFailureImage > 0) {
            gdhBuilder = gdhBuilder.setFailureImage(getDrawable(mFailureImage));
        }
        if (mProgressBarImage > 0) {
            gdhBuilder = gdhBuilder.setProgressBarImage(new AutoRotateDrawable(getDrawable(mProgressBarImage), mProgressBarAutoRotateInterval));
        }
        SimpleDraweeView view = new SimpleDraweeView(getContext(), gdhBuilder.build());
        RelativeLayout.LayoutParams params = new LayoutParams(mWidth, mHeight);
        params.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.ss_margin), getResources().getDimensionPixelSize(R.dimen.ss_margin));
        view.setLayoutParams(params);
        return view;
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

    private void updateViewsLayoutParams() {
        for (int i = 0; i < mSimpleDraweeViewList.size(); i++) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(tileWidth(mWidth), tileHeight(i, mHeight));
            layoutParams.addRule(tileRuleHorizontal(i));
            layoutParams.addRule(tileRuleVertical(i));
            mSimpleDraweeViewList.get(i).setLayoutParams(layoutParams);
            final int index = i;
            mSimpleDraweeViewList.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onClick(v, index);
                    }
                }
            });

            if (i == mSimpleDraweeViewList.size() - 1) {
                mMask.setLayoutParams(layoutParams);
            }
        }
    }

    private int tileWidth(int w) {
        switch (mTilesNumber) {
            case 1:
                return w;
            default:
                return w / 2;
        }
    }

    private int tileHeight(int idx, int h) {
        switch (mTilesNumber) {
            case 3:
                if (idx == 0) {
                    return h;
                } else {
                    return h / 2;
                }
            case 4:
                return h / 2;
            default:
                return h;
        }
    }

    private int tileRuleHorizontal(int idx) {
        switch (idx) {
            case 1:
            case 3:
                return RelativeLayout.ALIGN_PARENT_RIGHT;
            case 2:
                if (mTilesNumber == 3) {
                    return RelativeLayout.ALIGN_PARENT_RIGHT;
                }
            default:
                return RelativeLayout.ALIGN_PARENT_LEFT;
        }
    }

    private int tileRuleVertical(int idx) {
        switch (idx) {
            case 2:
            case 3:
                return RelativeLayout.ALIGN_PARENT_BOTTOM;
            default:
                return RelativeLayout.ALIGN_PARENT_TOP;
        }
    }

    public void load(List<ConstructFile> files, String thumbnailSize) {
        if (files == null/*||files.size()<mTilesNumber*/) {
            throw new InvalidParameterException("Files can't be null");
        }
        if (files.size() < mTilesNumber) {
            mTilesNumber = files.size();
            initViews();
        }
        for (int i = 0; i < mSimpleDraweeViewList.size(); i++) {
            setDraweePicture(mSimpleDraweeViewList.get(i), files.get(i), thumbnailSize);
        }
        if (mShowMask && files.size() > mTilesNumber) {
            mMask.setVisibility(VISIBLE);
            mMask.setText("+" + (files.size() - mTilesNumber));
        }
    }

    private void setDraweePicture(SimpleDraweeView view, ConstructFile constructFile, String thumbnail) {
        ImageLoader.load(constructFile, thumbnail, view);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateViewsLayoutParams();
    }

    @SuppressWarnings("deprecation")
    private Drawable getDrawable(int res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getContext().getResources().getDrawable(res, getContext().getTheme());
        } else {
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
        void onClick(View v, int index);
    }
}

