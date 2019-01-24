package com.example.thanx2.patternview.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;

import android.util.AttributeSet;
import android.widget.ImageView;


import static com.example.thanx2.patternview.constant.Constant.MAX_SCALE;
import static com.example.thanx2.patternview.constant.Constant.MIN_SCALE;
import static com.example.thanx2.patternview.constant.Constant.ORIGINAL_SCALE;
import static com.example.thanx2.patternview.constant.Constant.ROW_HEIGHT_STEP;
import static com.example.thanx2.patternview.constant.Constant.SCALE_STEP;

/*

ih=imageView.getMeasuredHeight();//height of imageView
iw=imageView.getMeasuredWidth();//width of imageView
iH=imageView.getDrawable().getIntrinsicHeight();//original height of underlying image
iW=imageView.getDrawable().getIntrinsicWidth();//original width of underlying image

 */

@SuppressLint("AppCompatCustomView")
public class PatternView extends ImageView {

    private Float scale = 1f;
    private Matrix matrix = new Matrix();

    public PatternView(Context context) {
        super(context);
    }

    public PatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PatternView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void imageZoomIn() {
        imageScale( this.scale * ( 1 + SCALE_STEP ) );
    }

    public void imageZoomOut() {
        imageScale( this.scale * ( 1 - SCALE_STEP ) );
    }

    public void imageOriginalZoom() {
        imageScale(ORIGINAL_SCALE);
    }

    public void imageFit() {
        float iw = getMeasuredWidth();
        float iW = getDrawable().getIntrinsicWidth();

        imageScale(iw / iW);
        scrollTo(0, getScrollY());
    }

    public void imageScale(Float scale) {
        if ( scale != null) {
            scale = checkScale(scale);
            this.scale = scale;
            matrix.setScale(scale, scale);
            setImageMatrix(matrix);
        }
    }

    public void setRowHeight(int height) {
        requestLayout();
        getLayoutParams().height = height;
    }

    public void rowShrink() {
        requestLayout();
        getLayoutParams().height = getLayoutParams().height - ROW_HEIGHT_STEP;
    }

    public void rowGrow( ) {
        requestLayout();
        getLayoutParams().height = getLayoutParams().height + ROW_HEIGHT_STEP;
    }

    public void imageUp() {
        if ( getScrollY() + ROW_HEIGHT_STEP < getDrawable().getIntrinsicHeight()) {
            scrollTo(getScrollX(), getScrollY() + ROW_HEIGHT_STEP);
        }
    }

    public void imageDown() {
        if ( getScrollY() - ROW_HEIGHT_STEP > 0 ) {
            scrollTo(getScrollX(), getScrollY() - ROW_HEIGHT_STEP);
        }
    }

    public void rowUp() {
        scrollTo(getScrollX(), getScrollY() - getLayoutParams().height);
        if ( getScrollY() < 0 ) {
            scrollTo(getScrollX(), 0);
        }
    }

    public void rowDown() {
        scrollTo(getScrollX(), getScrollY() + getLayoutParams().height);
        if ( getScrollY() > getDrawable().getIntrinsicHeight() * scale ) {
            scrollTo(getScrollX(), (int) ( getDrawable().getIntrinsicHeight() * scale ) - getMeasuredHeight());
        }
    }

    public void patternLeft() {
        int y = getScrollY();
        int scrollX = getMeasuredWidth() / 2;

        scrollTo(getScrollX() - scrollX, y);
        if ( getScrollX() < 0 ) {
            scrollTo(0, y);
        }
    }

    public void patternRight() {
        int iw = getMeasuredWidth();
        float iWf = getDrawable().getIntrinsicWidth() * scale ;
        int iW = (int) iWf;
        int y = getScrollY();
        int scrollX = iw / 2;
        scrollTo(getScrollX() + scrollX, y);
        if ( getScrollX() + iw > iW ) {
            scrollTo(iW - iw, y);
        }
    }

    private float checkScale(float scale) {
        return Math.max(MIN_SCALE, Math.min(scale, MAX_SCALE));
    }

    public Float getScale() {
        return scale;
    }

}
