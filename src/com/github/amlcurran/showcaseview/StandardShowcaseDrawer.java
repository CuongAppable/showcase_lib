package com.github.amlcurran.showcaseview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;

/**
 * Created by curraa01 on 13/10/2013.
 */
class StandardShowcaseDrawer implements ShowcaseDrawer {

    protected final Paint eraserPaint;
    protected final Drawable showcaseDrawable;
    private final Paint basicPaint;
    protected int backgroundColour;

    public StandardShowcaseDrawer(Resources resources) {
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
        eraserPaint = new Paint();
        eraserPaint.setColor(0xFFFFFF);
        eraserPaint.setAlpha(0);
        eraserPaint.setXfermode(xfermode);
        eraserPaint.setAntiAlias(true);
        basicPaint = new Paint();
        showcaseDrawable = resources.getDrawable(R.drawable.bg_cling);
    }

    @Override
    public void setShowcaseColour(int color) {
        showcaseDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void drawShowcase(Bitmap buffer, float x, float y, int w, int h, int sharp) {
        Canvas bufferCanvas = new Canvas(buffer);
        if(sharp == 0){
        	bufferCanvas.drawCircle(x, y, (int)(0.5f*Math.sqrt(w*w + h*h)), eraserPaint);
        }else{
        	bufferCanvas.drawRect(x - w/2, y - h/2, x + w/2, y + h/2, eraserPaint);
        }
    }

    @Override
    public int getShowcaseWidth() {
        return showcaseDrawable.getIntrinsicWidth();
    }

    @Override
    public int getShowcaseHeight() {
        return showcaseDrawable.getIntrinsicHeight();
    }

    @Override
    public float getBlockedRadius() {
        return 0;
    }

    @Override
    public void setBackgroundColour(int backgroundColor) {
        this.backgroundColour = backgroundColor;
    }

    @Override
    public void erase(Bitmap bitmapBuffer) {
        bitmapBuffer.eraseColor(backgroundColour);
    }

    @Override
    public void drawToCanvas(Canvas canvas, Bitmap bitmapBuffer) {
        canvas.drawBitmap(bitmapBuffer, 0, 0, basicPaint);
    }

}
