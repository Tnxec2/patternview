package com.example.thanx2.patternview.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;

import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;


import com.example.thanx2.patternview.model.Pattern;

import static com.example.thanx2.patternview.constant.Constant.MAX_SCALE;
import static com.example.thanx2.patternview.constant.Constant.MIN_SCALE;
import static com.example.thanx2.patternview.constant.Constant.ORIGINAL_SCALE;
import static com.example.thanx2.patternview.constant.Constant.ROW_HEIGHT_DEFAULT;
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

    private Pattern pattern;

    private Matrix matrix = new Matrix();

    public PatternView(Context context) {
        super(context);
        pattern = new Pattern();
    }

    public PatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
        pattern = new Pattern();
    }

    public PatternView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        pattern = new Pattern();
    }

    public void imageZoomIn() {
        imageScale( pattern.getScale() * ( 1 + SCALE_STEP ) );
    }

    public void imageZoomOut() {
        imageScale( pattern.getScale() * ( 1 - SCALE_STEP ) );
    }

    public void imageOriginalZoom() {
        imageScale(ORIGINAL_SCALE);
    }

    public void imageFit() {
        float iw = getMeasuredWidth();
        float iW = getDrawable().getIntrinsicWidth();

        imageScale(iw / iW);
        scroll(0, pattern.getPatternY());
    }

    public void imageScale(Float scale) {
        if ( scale != null) {
            scale = checkScale(scale);
            pattern.setScale(scale);
            matrix.setScale(scale, scale);
            setImageMatrix(matrix);
        }
    }

    public void rowShrink() {
        pattern.setRowHeight(pattern.getRowHeight() - ROW_HEIGHT_STEP);
        requestLayout();
        getLayoutParams().height = pattern.getRowHeight();
    }

    public void rowGrow( ) {
        requestLayout();
        pattern.setRowHeight(pattern.getRowHeight() + ROW_HEIGHT_STEP);
        getLayoutParams().height = pattern.getRowHeight();
    }

    public void imageUp() {
        int scrollY = pattern.getPatternY() + ROW_HEIGHT_STEP;
        scrollY = Math.min(scrollY, (int) (getDrawable().getIntrinsicHeight() * pattern.getScale()) );
        scroll( pattern.getPatternX(), scrollY );
    }

    public void imageDown() {
        int scrollY = pattern.getPatternY() - ROW_HEIGHT_STEP;
        scrollY = Math.max(0, scrollY);

        scroll( pattern.getPatternX(), scrollY);
    }

    public void rowUp() {
        int scrollY = pattern.getPatternY() - pattern.getRowHeight();
        scrollY = Math.max( 0, scrollY);

        scroll( pattern.getPatternX(), scrollY );
    }

    public void rowDown() {
        int scrollY = pattern.getPatternY() + pattern.getRowHeight();
        scrollY = Math.min( scrollY, (int) ( getDrawable().getIntrinsicHeight() * pattern.getScale() - pattern.getRowHeight()) );

        scroll( pattern.getPatternX(), scrollY );
    }

    public void patternLeft() {
        int scrollX = pattern.getPatternX() - getMeasuredWidth() / 2;
        scrollX = Math.max( 0, scrollX);

        scroll( scrollX, pattern.getPatternY() );
    }

    public void patternRight() {
        int iw = getMeasuredWidth();
        float iWf = getDrawable().getIntrinsicWidth() * pattern.getScale() ;
        int iW = (int) iWf;
        int scrollX = pattern.getPatternX() + iw / 2;
        scrollX = Math.min( scrollX, iW - iw );

        scroll( scrollX, pattern.getPatternY() );
    }

    private float checkScale(float scale) {
        return Math.max(MIN_SCALE, Math.min(scale, MAX_SCALE));
    }

    public Float getScale() {
        return pattern.getScale();
    }

    public void initial() {
        pattern = new Pattern();
        setPatternRowHeight(ROW_HEIGHT_DEFAULT);
        imageOriginalZoom();
        scroll(0, 0);
        setImageURI(null);
    }

    public Uri getUri() {
        return pattern.getUri();
    }

    public void setUri(Uri uri) {
        pattern.setUri(uri);
        setImageURI(uri);
    }

    public Integer getPatternX() {
        return pattern.getPatternX();
    }

    public Integer getPatternY() {
        return pattern.getPatternY();
    }

    public Integer getPatternRowHeight() {
        return pattern.getRowHeight();
    }

    public void setPatternRowHeight(int height) {
        pattern.setRowHeight(height);
        requestLayout();
        getLayoutParams().height = pattern.getRowHeight();
    }

    public void scroll(int x, int y) {
        pattern.setPatternX(x);
        pattern.setPatternY(y);
        scrollTo(x, y);
    }
}
