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
        scrollTo(0, pattern.getScrollY());
    }

    public void imageScale(Float scale) {
        if ( scale != null) {
            scale = checkScale(scale);
            pattern.setScale(scale);
            matrix.setScale(scale, scale);
            setImageMatrix(matrix);
        }
    }

    public void setRowHeight(int height) {
        pattern.setRowHeight(height);
        requestLayout();
        getLayoutParams().height = pattern.getRowHeight();
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
        int scrollY = pattern.getScrollY() + ROW_HEIGHT_STEP;
        scrollY = Math.min(scrollY, (int) (getDrawable().getIntrinsicHeight() * pattern.getScale()) );
        pattern.setScrollY(scrollY);

        scrollTo(pattern.getScrollX(), pattern.getScrollY());
    }

    public void imageDown() {
        int scrollY = pattern.getScrollY() - ROW_HEIGHT_STEP;
        scrollY = Math.max(0, scrollY);
        pattern.setScrollY(scrollY);

        scrollTo(pattern.getScrollX(), pattern.getScrollY());
    }

    public void rowUp() {
        int scrollY = pattern.getScrollY() - pattern.getRowHeight();
        scrollY = Math.max( 0, scrollY);
        pattern.setScrollY(scrollY);

        scrollTo(pattern.getScrollX(), pattern.getScrollY());
    }

    public void rowDown() {
        int scrollY = pattern.getScrollY() + pattern.getRowHeight();
        scrollY = Math.min( scrollY, (int) ( getDrawable().getIntrinsicHeight() * pattern.getScale() - pattern.getRowHeight()) );
        pattern.setScrollY(scrollY);

        scrollTo(pattern.getScrollX(), pattern.getScrollY());
    }

    public void patternLeft() {
        int scrollX = pattern.getScrollX() - getMeasuredWidth() / 2;
        scrollX = Math.max( 0, scrollX);
        pattern.setScrollX(scrollX);

        scrollTo(pattern.getScrollX(), pattern.getScrollY());
    }

    public void patternRight() {
        int iw = getMeasuredWidth();
        float iWf = getDrawable().getIntrinsicWidth() * pattern.getScale() ;
        int iW = (int) iWf;
        int scrollX = pattern.getScrollX() + iw / 2;
        scrollX = Math.min( scrollX, iW - iw );
        pattern.setScrollX(scrollX);

        scrollTo(pattern.getScrollX(), pattern.getScrollY());
    }

    private float checkScale(float scale) {
        return Math.max(MIN_SCALE, Math.min(scale, MAX_SCALE));
    }

    public Float getScale() {
        return pattern.getScale();
    }

    public void initial() {
        pattern = new Pattern();
        setRowHeight(ROW_HEIGHT_DEFAULT);
        imageOriginalZoom();
        scrollTo(0, 0);
        setImageURI(null);
    }

    public Uri getUri() {
        if ( pattern.getUri() != null)
            return Uri.parse( pattern.getUri() );
        else
            return null;
    }

    public void setUri(Uri uri) {
        pattern.setUri(uri.toString());
        setImageURI(uri);
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
