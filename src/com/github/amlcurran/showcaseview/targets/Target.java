package com.github.amlcurran.showcaseview.targets;

import android.graphics.Point;
import android.graphics.drawable.Drawable;

public interface Target {
	
    Target NONE = new Target() {
        @Override
        public Point getPoint() {
            return new Point(1000000, 1000000);
        }

		@Override
		public int getWidth() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getHeight() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getViewSharp() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public ArrowObject getArrowObject() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getTargetID() {
			// TODO Auto-generated method stub
			return "";
		}
    };

    public int getViewSharp();
    public Point getPoint();
    public int getWidth();
    public int getHeight();
    public ArrowObject getArrowObject();
    public String getTargetID();
}
