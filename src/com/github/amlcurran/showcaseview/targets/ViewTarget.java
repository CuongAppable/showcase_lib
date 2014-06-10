package com.github.amlcurran.showcaseview.targets;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Target a view on the screen. This will centre the target on the view.
 */
public class ViewTarget implements Target {

    private final View mView;
    private final int mViewSharp;
    private final String mDescription;
    private final ArrowObject arrowDrawable;
    private final String mTargetID;
    
    public ViewTarget(View view, int viewsharp, String descript, ArrowObject arrow, String targetID) {
        mView = view;
        mViewSharp = viewsharp;
        mDescription = descript;
        arrowDrawable = arrow;
        mTargetID = targetID;
    }

    public ViewTarget(int viewId, int viewsharp, String descript, Activity activity, ArrowObject arrow, String targetID) {
        mView = activity.findViewById(viewId);
        mViewSharp = viewsharp;
        mDescription = descript;
        arrowDrawable = arrow;
        mTargetID = targetID;
    }

    @Override
    public Point getPoint() {
        int[] location = new int[2];
        mView.getLocationInWindow(location);
        int x = location[0] + mView.getWidth() / 2;
        int y = location[1] + mView.getHeight() / 2;
        return new Point(x, y);
    }

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return mView.getWidth();
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return mView.getHeight();
	}

	@Override
	public int getViewSharp() {
		// TODO Auto-generated method stub
		return mViewSharp;
	}

	@Override
	public ArrowObject getArrowObject() {
		// TODO Auto-generated method stub
		return arrowDrawable;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return mDescription;
	}

	@Override
	public String getTargetID() {
		// TODO Auto-generated method stub
		return mTargetID;
	}
}
