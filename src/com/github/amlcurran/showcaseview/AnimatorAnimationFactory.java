package com.github.amlcurran.showcaseview;

import com.github.amlcurran.showcaseview.targets.Target;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

class AnimatorAnimationFactory implements AnimationFactory {

    private static final String ALPHA = "alpha";
    private static final float INVISIBLE = 0f;
    private static final float VISIBLE = 1f;

    private final AccelerateDecelerateInterpolator interpolator;

    public AnimatorAnimationFactory() {
        interpolator = new AccelerateDecelerateInterpolator();
    }

    @Override
    public void fadeInView(View target, long duration, final AnimationStartListener listener) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(target, ALPHA, INVISIBLE, VISIBLE);
        oa.setDuration(duration).addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                listener.onAnimationStart();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        oa.start();
    }

    @Override
    public void fadeOutView(View target, long duration, final AnimationEndListener listener) {
        ObjectAnimator oa = ObjectAnimator.ofFloat(target, ALPHA, INVISIBLE);
        oa.setDuration(duration).addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                listener.onAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        oa.start();
    }

    @Override
    public void animateTargetToPoint(ShowcaseView showcaseView, Target target) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator xAnimator = ObjectAnimator.ofInt(showcaseView, "showcaseX", target.getPoint().x);
        ObjectAnimator yAnimator = ObjectAnimator.ofInt(showcaseView, "showcaseY", target.getPoint().y);
        ObjectAnimator wAnimator = ObjectAnimator.ofInt(showcaseView, "showcaseWidth", target.getWidth());
        ObjectAnimator hAnimator = ObjectAnimator.ofInt(showcaseView, "showcaseHeight", target.getHeight());
        ObjectAnimator sAnimator = ObjectAnimator.ofInt(showcaseView, "showcaseSharp", target.getViewSharp());
        ObjectAnimator isFinishShowCaseAnimator = ObjectAnimator.ofInt(showcaseView, "isFinishDrawShowCase", 1);
        set.playTogether(xAnimator, yAnimator, wAnimator, hAnimator, sAnimator, isFinishShowCaseAnimator);
        set.setInterpolator(interpolator);
        set.start();
    }

}
