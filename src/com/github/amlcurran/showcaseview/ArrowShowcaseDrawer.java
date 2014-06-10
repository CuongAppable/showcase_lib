package com.github.amlcurran.showcaseview;

import com.github.amlcurran.showcaseview.targets.ArrowObject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by curraa01 on 13/10/2013.
 */
public class ArrowShowcaseDrawer {
    private final Paint basicPaint;
    private int arrowColor;
    private ArrowObject arrowObject;
    protected int backgroundColour;
    
    public ArrowShowcaseDrawer() {
        basicPaint = new Paint();
    }

    public void setShowcaseColour(int color) {
    	arrowColor = color;
    }

    public void setArrowDrawable(ArrowObject arrow){
    	arrowObject = arrow;
    }
    
    public ArrowObject getArrowObject(){
    	return arrowObject;
    }
    
    public void drawArrow(Bitmap buffer, float x, float y, int w, int h) {
        Canvas bufferCanvas = new Canvas(buffer);
        if(arrowObject.direct == ArrowObject.BELOW_ALIGN_LEFT){
        	arrowObject.drawable.setBounds(
        			(int)(x - w/2 + arrowObject.offsetleft),
        			(int)(y + h/2 + arrowObject.offsetTop),
        			(int)(x - w/2 + arrowObject.offsetleft + getArrowWidth()),
        			(int)(y + h/2 + arrowObject.offsetTop + getArrowHeight())
        	);
        }else if(arrowObject.direct == ArrowObject.BELOW_ALIGN_RIGHT){
        	arrowObject.drawable.setBounds(
        			(int)(x + w/2 + arrowObject.offsetRight - getArrowWidth()),
        			(int)(y + h/2 + arrowObject.offsetTop),
        			(int)(x + w/2 + arrowObject.offsetRight),
        			(int)(y + h/2 + arrowObject.offsetTop + getArrowHeight())
        	);
        }else if(arrowObject.direct == ArrowObject.ABOVE_ALIGN_LEFT){
        	arrowObject.drawable.setBounds(
        			(int)(x - w/2 + arrowObject.offsetleft),
        			(int)(y - h/2 + arrowObject.offsetBot - getArrowHeight()),
        			(int)(x - w/2 + arrowObject.offsetleft + getArrowWidth()),
        			(int)(y - h/2 + arrowObject.offsetBot)
        	);
        }else if(arrowObject.direct == ArrowObject.ABOVE_ALIGN_RIGHT){
        	arrowObject.drawable.setBounds(
        			(int)(x + w/2 + arrowObject.offsetRight - getArrowWidth()),
        			(int)(y - h/2 + arrowObject.offsetBot - getArrowHeight()),
        			(int)(x + w/2 + arrowObject.offsetRight),
        			(int)(y - h/2 + arrowObject.offsetBot)
        	);
        }else if(arrowObject.direct == ArrowObject.ABOVE_ALIGN_CENTER){
        	arrowObject.drawable.setBounds(
        			(int)(x + arrowObject.offsetleft - getArrowWidth()/2),
        			(int)(y - h/2 + arrowObject.offsetBot - getArrowHeight()),
        			(int)(x + arrowObject.offsetleft + getArrowWidth()/2),
        			(int)(y - h/2 + arrowObject.offsetBot)
        	);
        }else if(arrowObject.direct == ArrowObject.BELOW_ALIGN_CENTER){
        	arrowObject.drawable.setBounds(
        			(int)(x + arrowObject.offsetleft - getArrowWidth()/2),
        			(int)(y + h/2 + arrowObject.offsetTop),
        			(int)(x + arrowObject.offsetleft + getArrowWidth()/2),
        			(int)(y + h/2 + arrowObject.offsetTop + getArrowHeight())
        	);
        }else if(arrowObject.direct == ArrowObject.LEFT_VERTICAL_CENTER){
        	arrowObject.drawable.setBounds(
        			(int)(x + w/2 + arrowObject.offsetleft),
        			(int)(y + arrowObject.offsetTop - getArrowHeight()/2),
        			(int)(x + w/2 + arrowObject.offsetleft + getArrowWidth()),
        			(int)(y + arrowObject.offsetTop + getArrowHeight()/2)
        	);
        }else if(arrowObject.direct == ArrowObject.RIGHT_VERTICAL_CENTER){
        	arrowObject.drawable.setBounds(
        			(int)(x - w/2 + arrowObject.offsetRight - getArrowWidth()),
        			(int)(y + arrowObject.offsetTop - getArrowHeight()/2),
        			(int)(x - w/2 + arrowObject.offsetRight),
        			(int)(y + arrowObject.offsetTop + getArrowHeight()/2)
        	);
        }
        arrowObject.drawable.draw(bufferCanvas);
    }

    public void setBackgroundColour(int backgroundColor) {
        this.backgroundColour = backgroundColor;
    }
    
    public void erase(Bitmap bitmapBuffer) {
        bitmapBuffer.eraseColor(backgroundColour);
    }
    
    public int getArrowWidth() {
        return (int)(arrowObject.drawable.getIntrinsicWidth()*arrowObject.scale);
    }

    
    public int getArrowHeight() {
        return (int)(arrowObject.drawable.getIntrinsicHeight()*arrowObject.scale);
    }

    public void drawToCanvas(Canvas canvas, Bitmap bitmapBuffer) {
        canvas.drawBitmap(bitmapBuffer, 0, 0, basicPaint);
    }

}
