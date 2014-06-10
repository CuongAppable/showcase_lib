package com.github.amlcurran.showcaseview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.targets.ArrowObject;
import com.github.amlcurran.showcaseview.targets.Target;

import static com.github.amlcurran.showcaseview.AnimationFactory.AnimationEndListener;
import static com.github.amlcurran.showcaseview.AnimationFactory.AnimationStartListener;

/**
 * A view which allows you to showcase areas of your app with an explanation.
 */
public class ShowcaseView extends RelativeLayout
        implements View.OnClickListener, View.OnTouchListener, ViewTreeObserver.OnPreDrawListener, ViewTreeObserver.OnGlobalLayoutListener {

    private static final int DEFAULT_COLOR = Color.parseColor("#ffffff");

    private final RelativeLayout mContent;
    private final Button mEndButton;
    private final TextView mDescipt;
    private final ShowcaseDrawer showcaseDrawer;
    private final ArrowShowcaseDrawer arrowDrawer;
    private final ShowcaseAreaCalculator showcaseAreaCalculator;
    private final AnimationFactory animationFactory;
    private final ShotStateStore shotStateStore;

    private String strDescript;
    
    // Showcase metrics
    private int showcaseWidth = 0;
    private int showcaseHeight = 0;
    private int showcaseX = -1;
    private int showcaseY = -1;
    private int showcaseSharp = 0;

    // Touch items
    private boolean hasCustomClickListener = false;
    private boolean blockTouches = true;
    private boolean hideOnTouch = false;
    private OnShowcaseEventListener mEventListener = OnShowcaseEventListener.NONE;

    private boolean hasAlteredText = false;
    private boolean hasNoTarget = false;
    private boolean shouldCentreText;
    private Bitmap bitmapBuffer;
    private Bitmap arrowBuffer;
    
    // Animation items
    private long fadeInMillis;
    private long fadeOutMillis;

    protected ShowcaseView(Context context, boolean newStyle) {
        this(context, null, R.styleable.CustomTheme_showcaseViewStyle, newStyle);
    }

    protected ShowcaseView(Context context, AttributeSet attrs, int defStyle, boolean newStyle) {
        super(context, attrs, defStyle);

        ApiUtils apiUtils = new ApiUtils();
        animationFactory = new AnimatorAnimationFactory();
        showcaseAreaCalculator = new ShowcaseAreaCalculator();
        shotStateStore = new ShotStateStore(context);

        apiUtils.setFitsSystemWindowsCompat(this);
        getViewTreeObserver().addOnPreDrawListener(this);
        getViewTreeObserver().addOnGlobalLayoutListener(this);

        // Get the attributes for the ShowcaseView
        final TypedArray styled = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ShowcaseView, R.attr.showcaseViewStyle,
                        R.style.ShowcaseView);

        // Set the default animation times
        fadeInMillis = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        fadeOutMillis = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        mContent = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.showcase_layout, null);
        mEndButton = (Button) mContent.findViewById(R.id.showcase_button);
        mDescipt = (TextView) mContent.findViewById(R.id.showcase_descript);
        
        if (newStyle) {
            showcaseDrawer = new NewShowcaseDrawer(getResources());
        } else {
            showcaseDrawer = new StandardShowcaseDrawer(getResources());
        }
        
        arrowDrawer = new ArrowShowcaseDrawer();

        updateStyle(styled, false);

        init();
    }

    private void init() {

        setOnTouchListener(this);

        addView(mContent);
        
        if (mEndButton.getParent() == null) {
            if (!hasCustomClickListener) {
                mEndButton.setOnClickListener(this);
            }
        }
    }

    private boolean hasShot() {
        return shotStateStore.hasShot();
    }
    
    void setShowcaseTarget(Target target) {
    	setShowcaseTarget(
    			target.getPoint().x,
    			target.getPoint().y,
    			target.getWidth(),
    			target.getHeight(),
    			target.getViewSharp());
    }
   
    void setShowcaseTarget(int x, int y, int w, int h, int vs) {
        if (shotStateStore.hasShot()) {
            return;
        }
        showcaseX = x;
        showcaseY = y;
        showcaseWidth = w;
        showcaseHeight = h;
        showcaseSharp = vs;
        //init();
        invalidate();
    }

    public void setTarget(final Target target) {
        setShowcase(target, false);
    }

    public void calDescriptPosition(ArrowObject arrowObject){
    	RelativeLayout.LayoutParams lps = (RelativeLayout.LayoutParams)mDescipt.getLayoutParams();
    	Rect arrow = arrowObject.drawable.getBounds();
    	switch (arrowObject.direct) {
		case ArrowObject.ABOVE_ALIGN_CENTER:
			lps.leftMargin = 0;
			lps.rightMargin = 0;
			lps.topMargin = 100;
			lps.bottomMargin = 0;
			break;
		case ArrowObject.ABOVE_ALIGN_LEFT:
			lps.leftMargin = 0;
			lps.rightMargin = 0;
			lps.topMargin = 100;
			lps.bottomMargin = 0;
			break;
		case ArrowObject.ABOVE_ALIGN_RIGHT:
			lps.leftMargin = 0;
			lps.rightMargin = 0;
			lps.topMargin = 100;
			lps.bottomMargin = 0;
			break;
		case ArrowObject.BELOW_ALIGN_CENTER:
			lps.leftMargin = 0;
			lps.rightMargin = 0;
			lps.topMargin = arrow.bottom;
			lps.bottomMargin = 0;
			break;
		case ArrowObject.BELOW_ALIGN_LEFT:
			lps.leftMargin = 0;
			lps.rightMargin = 0;
			lps.topMargin = arrow.centerY();
			lps.bottomMargin = 0;
			break;
		case ArrowObject.BELOW_ALIGN_RIGHT:
			lps.leftMargin = 0;
			lps.rightMargin = 0;
			lps.topMargin = arrow.centerY();
			lps.bottomMargin = 0;
			break;
		case ArrowObject.LEFT_VERTICAL_CENTER:
			
			break;
		case ArrowObject.RIGHT_VERTICAL_CENTER:
			
			break;
		default:
			break;
		}
    	
    	mDescipt.setLayoutParams(lps);
    }
    
    public void setShowcase(final Target target, final boolean animate) {
        postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!shotStateStore.hasShot()) {
                	arrowDrawer.setArrowDrawable(target.getArrowObject());
                    updateBitmap();
                    Point targetPoint = target.getPoint();
                    if (targetPoint != null) {
                        hasNoTarget = false;
                        setContentText((target.toString()));
                        if (animate) {
                        	isFinishDrawShowCase = 0;
                            animationFactory.animateTargetToPoint(ShowcaseView.this, target);
                        } else {
                        	isFinishDrawShowCase = 1;
                        	setShowcaseTarget(target);
                        }
                    } else {
                        hasNoTarget = true;
                        invalidate();
                    }

                }
            }
        }, 100);
    }

    public int isFinishDrawShowCase = 0;
    
    private void updateBitmap() {
        if (bitmapBuffer == null || haveBoundsChanged()) {
            bitmapBuffer = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        }
        
        if(arrowBuffer == null || haveBoundsChanged()){
        	arrowBuffer = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        }
    }

    private boolean haveBoundsChanged() {
        return getMeasuredWidth() != bitmapBuffer.getWidth() ||
                getMeasuredHeight() != bitmapBuffer.getHeight();
    }

    public boolean hasShowcaseView() {
        return (showcaseX != 1000000 && showcaseY != 1000000) || !hasNoTarget;
    }

	public void setShowcaseWidth(int showcaseWidth) {
		setShowcaseTarget(showcaseX, showcaseY, showcaseWidth, showcaseHeight, showcaseSharp);
	}


	public void setShowcaseHeight(int showcaseHeight) {
		setShowcaseTarget(showcaseX, showcaseY, showcaseWidth, showcaseHeight, showcaseSharp);
	}

	public void setShowcaseX(int x) {
        setShowcaseTarget(x, showcaseY, showcaseWidth, showcaseHeight, showcaseSharp);
    }

    public void setShowcaseY(int y) {
        setShowcaseTarget(showcaseX, y, showcaseWidth, showcaseHeight, showcaseSharp);
    }
    
	public void setShowcaseSharp(int showcaseSharp) {
		setShowcaseTarget(showcaseX, showcaseY, showcaseWidth, showcaseHeight, showcaseSharp);
	}
	
	public int getIsFinishDrawShowCase() {
		return isFinishDrawShowCase;
	}

	public void setIsFinishDrawShowCase(int isFinishDrawShowCase) {
		this.isFinishDrawShowCase = isFinishDrawShowCase;
	}

	public int getShowcaseX() {
        return showcaseX;
    }

    public int getShowcaseY() {
        return showcaseY;
    }

    public int getShowcaseWidth() {
		return showcaseWidth;
	}
    
    public int getShowcaseHeight() {
		return showcaseHeight;
	}
    
    public int getShowcaseSharp() {
		return showcaseSharp;
	}
    
    /**
     * Override the standard button click event
     *
     * @param listener Listener to listen to on click events
     */
    public void overrideButtonClick(OnClickListener listener) {
        if (shotStateStore.hasShot()) {
            return;
        }
        if (mEndButton != null) {
            mEndButton.setOnClickListener(listener != null ? listener : this);
        }
        hasCustomClickListener = true;
    }

    public void setOnShowcaseEventListener(OnShowcaseEventListener listener) {
        if (listener != null) {
            mEventListener = listener;
        } else {
            mEventListener = OnShowcaseEventListener.NONE;
        }
    }

    public void setButtonText(CharSequence text) {
        if (mEndButton != null) {
            mEndButton.setText(text);
        }
    }
    
    public Button getEndButton(){
    	if (mEndButton != null) {
            return mEndButton;
        }
    	return null;
    }

    @Override
    public boolean onPreDraw() {
        boolean recalculatedCling = showcaseAreaCalculator.calculateShowcaseRect(showcaseX, showcaseY, showcaseDrawer);
        boolean recalculateText = recalculatedCling || hasAlteredText;
        
        hasAlteredText = false;
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (showcaseX < 0 || showcaseY < 0 || shotStateStore.hasShot()) {
            super.dispatchDraw(canvas);
            return;
        }

        //Draw background color
        showcaseDrawer.erase(bitmapBuffer);
        // Draw the showcase drawable
        if (!hasNoTarget) {
        	int curW = showcaseDrawer.getShowcaseWidth();
        	int curH = showcaseDrawer.getShowcaseHeight();
            showcaseDrawer.drawShowcase(bitmapBuffer, showcaseX, showcaseY, showcaseWidth, showcaseHeight, showcaseSharp);
            showcaseDrawer.drawToCanvas(canvas, bitmapBuffer);
        }

        // Draw the text on the screen, recalculating its position if necessary
        if(isFinishDrawShowCase == 1){
        	arrowDrawer.erase(arrowBuffer);
            arrowDrawer.drawArrow(arrowBuffer, showcaseX, showcaseY, showcaseWidth, showcaseHeight);
            calDescriptPosition(arrowDrawer.getArrowObject());
            mDescipt.setText(strDescript);
            arrowDrawer.drawToCanvas(canvas, arrowBuffer);
        }
        super.dispatchDraw(canvas);
    }
    
    @Override
    public void onClick(View view) {
        // If the type is set to one-shot, store that it has shot
        shotStateStore.storeShot();
        hide();
    }

    public void hide() {
        mEventListener.onShowcaseViewHide(this);
        fadeOutShowcase();
    }

    private void fadeOutShowcase() {
        animationFactory.fadeOutView(this, fadeOutMillis, new AnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                setVisibility(View.GONE);
                mEventListener.onShowcaseViewDidHide(ShowcaseView.this);
            }
        });
    }

    public void show() {
        mEventListener.onShowcaseViewShow(this);
        fadeInShowcase();
    }

    private void fadeInShowcase() {
        animationFactory.fadeInView(this, fadeInMillis,
                new AnimationStartListener() {
                    @Override
                    public void onAnimationStart() {
                        setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
    	boolean isInFocus = false;
    	
        float xDelta = Math.abs(motionEvent.getRawX() - showcaseX);
        float yDelta = Math.abs(motionEvent.getRawY() - showcaseY);
        
        if(xDelta >= (showcaseX - showcaseWidth/2) 
        		&& xDelta <= (showcaseX + showcaseWidth/2)
        		&& yDelta >= (showcaseY - showcaseHeight/2)
        		&& yDelta <= (showcaseY + showcaseHeight/2)){
        	isInFocus = true;
        }
        
        if (MotionEvent.ACTION_UP == motionEvent.getAction() &&
                hideOnTouch && !isInFocus) {
            this.hide();
            return true;
        }
        
        return (blockTouches || (!isInFocus));
    }

    private static void insertShowcaseView(ShowcaseView showcaseView, Activity activity) {
        ((ViewGroup) activity.getWindow().getDecorView()).addView(showcaseView);
        if (!showcaseView.hasShot()) {
            showcaseView.show();
        } else {
            showcaseView.hideImmediate();
        }
    }

    private void hideImmediate() {
        setVisibility(GONE);
    }

    public void setContentText(CharSequence text) {
    	strDescript = text.toString();
    }
    
    @Override
    public void onGlobalLayout() {
        if (!shotStateStore.hasShot()) {
            updateBitmap();
        }
    }

    public void hideButton() {
        mEndButton.setVisibility(GONE);
    }

    public void showButton() {
        mEndButton.setVisibility(VISIBLE);
    }
    
    public RelativeLayout getContent(){
    	return mContent;
    }

    public TextView getDescript(){
    	return mDescipt;
    }
    
    /**
     * Builder class which allows easier creation of {@link ShowcaseView}s.
     * It is recommended that you use this Builder class.
     */
    public static class Builder {

        final ShowcaseView showcaseView;
        private final Activity activity;

        public Builder(Activity activity) {
            this(activity, false);
        }

        public Builder(Activity activity, boolean useNewStyle) {
            this.activity = activity;
            this.showcaseView = new ShowcaseView(activity, useNewStyle);
            this.showcaseView.setTarget(Target.NONE);
        }

        /**
         * Create the {@link com.github.amlcurran.showcaseview.ShowcaseView} and show it.
         *
         * @return the created ShowcaseView
         */
        public ShowcaseView build() {
            insertShowcaseView(showcaseView, activity);
            return showcaseView;
        }

        /**
         * Set the descriptive text shown on the ShowcaseView.
         */
        public Builder setContentText(int resId) {
            return setContentText(activity.getString(resId));
        }

        /**
         * Set the descriptive text shown on the ShowcaseView.
         */
        public Builder setContentText(CharSequence text) {
            showcaseView.setContentText(text);
            return this;
        }

        /**
         * Set the target of the showcase.
         *
         * @param target a {@link com.github.amlcurran.showcaseview.targets.Target} representing
         *               the item to showcase (e.g., a button, or action item).
         */
        public Builder setTarget(Target target) {
            showcaseView.setTarget(target);
            return this;
        }

        /**
         * Set the style of the ShowcaseView. See the sample app for example styles.
         */
        public Builder setStyle(int theme) {
            showcaseView.setStyle(theme);
            return this;
        }

        /**
         * Set a listener which will override the button clicks.
         * <p/>
         * Note that you will have to manually hide the ShowcaseView
         */
        public Builder setOnClickListener(OnClickListener onClickListener) {
            showcaseView.overrideButtonClick(onClickListener);
            return this;
        }

        /**
         * Don't make the ShowcaseView block touches on itself. This doesn't
         * block touches in the showcased area.
         * <p/>
         * By default, the ShowcaseView does block touches
         */
        public Builder doNotBlockTouches() {
            showcaseView.setBlocksTouches(false);
            return this;
        }

        /**
         * Make this ShowcaseView hide when the user touches outside the showcased area.
         * This enables {@link #doNotBlockTouches()} as well.
         * <p/>
         * By default, the ShowcaseView doesn't hide on touch.
         */
        public Builder hideOnTouchOutside() {
            showcaseView.setBlocksTouches(true);
            showcaseView.setHideOnTouchOutside(true);
            return this;
        }

        /**
         * Set the ShowcaseView to only ever show once.
         *
         * @param shotId a unique identifier (<em>across the app</em>) to store
         *               whether this ShowcaseView has been shown.
         */
        public Builder singleShot(long shotId) {
            showcaseView.setSingleShot(shotId);
            return this;
        }

        public Builder setShowcaseEventListener(OnShowcaseEventListener showcaseEventListener) {
            showcaseView.setOnShowcaseEventListener(showcaseEventListener);
            return this;
        }
    }

    /**
     * Set whether the text should be centred in the screen, or left-aligned (which is the default).
     */
    public void setShouldCentreText(boolean shouldCentreText) {
        this.shouldCentreText = shouldCentreText;
        hasAlteredText = true;
        invalidate();
    }

    /**
     * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder#setSingleShot(long)
     */
    private void setSingleShot(long shotId) {
        shotStateStore.setSingleShot(shotId);
    }

    /**
     * Change the position of the ShowcaseView's button from the default bottom-right position.
     *
     * @param layoutParams a {@link android.widget.RelativeLayout.LayoutParams} representing
     *                     the new position of the button
     */
    public void setButtonPosition(RelativeLayout.LayoutParams layoutParams) {
        mEndButton.setLayoutParams(layoutParams);
    }

    /**
     * Set the duration of the fading in and fading out of the ShowcaseView
     */
    private void setFadeDurations(long fadeInMillis, long fadeOutMillis) {
        this.fadeInMillis = fadeInMillis;
        this.fadeOutMillis = fadeOutMillis;
    }

    /**
     * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder#hideOnTouchOutside()
     */
    public void setHideOnTouchOutside(boolean hideOnTouch) {
        this.hideOnTouch = hideOnTouch;
    }

    /**
     * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder#doNotBlockTouches()
     */
    public void setBlocksTouches(boolean blockTouches) {
        this.blockTouches = blockTouches;
    }

    /**
     * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder#setStyle(int)
     */
    public void setStyle(int theme) {
        TypedArray array = getContext().obtainStyledAttributes(theme, R.styleable.ShowcaseView);
        updateStyle(array, true);
    }

    private void updateStyle(TypedArray styled, boolean invalidate) {
        int backgroundColor = styled.getColor(R.styleable.ShowcaseView_sv_backgroundColor, Color.argb(128, 80, 80, 80));
        int showcaseColor = styled.getColor(R.styleable.ShowcaseView_sv_showcaseColor, DEFAULT_COLOR);
        String buttonText = styled.getString(R.styleable.ShowcaseView_sv_buttonText);
        if (TextUtils.isEmpty(buttonText)) {
            buttonText = getResources().getString(R.string.ok);
        }
        styled.recycle();

        showcaseDrawer.setShowcaseColour(showcaseColor);
        showcaseDrawer.setBackgroundColour(backgroundColor);
        arrowDrawer.setShowcaseColour(showcaseColor);
        arrowDrawer.setBackgroundColour(Color.TRANSPARENT);
        mEndButton.setText(buttonText);
        hasAlteredText = true;

        if (invalidate) {
            invalidate();
        }
    }

}
