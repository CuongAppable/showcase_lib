package com.github.amlcurran.showcaseview;

import com.github.amlcurran.showcaseview.targets.Target;

import android.graphics.Point;
import android.view.View;

interface AnimationFactory {
    void fadeInView(View target, long duration, AnimationStartListener listener);

    void fadeOutView(View target, long duration, AnimationEndListener listener);

    void animateTargetToPoint(ShowcaseView showcaseView, Target target);

    public interface AnimationStartListener {
        void onAnimationStart();
    }

    public interface AnimationEndListener {
        void onAnimationEnd();
    }
}
