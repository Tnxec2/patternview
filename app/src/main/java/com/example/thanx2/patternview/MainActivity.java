package com.example.thanx2.patternview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.thanx2.patternview.database.DatabaseAdapter;
import com.example.thanx2.patternview.model.Pattern;

import java.io.IOException;
import java.util.Date;


/*

ih=imageView.getMeasuredHeight();//height of imageView
iw=imageView.getMeasuredWidth();//width of imageView
iH=imageView.getDrawable().getIntrinsicHeight();//original height of underlying image
iW=imageView.getDrawable().getIntrinsicWidth();//original width of underlying image

 */

public class MainActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;

    final static String IMAGE_URI = "IMAGE_URI";
    final static String ROW_HEIGHT = "ROW_HEIGHT";
    final static String IMAGE_SCROLL_X = "IMAGE_SCROLL_X";
    final static String IMAGE_SCROLL_Y = "IMAGE_SCROLL_Y";
    final static String IMAGE_SCALE = "IMAGE_SCALE";

    private final static int ROW_DIFF = 10;
    private final static float SCALE_STEP = 0.1f;
    private final static float MIN_SCALE = 0.1f;
    private final static float MAX_SCALE = 10f;

    Uri uriImage;

    Button btn_RowHeightShrink, btn_RowHeightGrow, btn_PatternShrink, btn_PatternGrow,
            btn_PatternLeft, btn_PatternRight, btn_ImageUp, btn_ImageDown;
    ImageView iv_Pattern;
    long patternId = 0;
    Float scale = 1f;
    Matrix matrix = new Matrix();

    private DatabaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btn_PatternGrow = findViewById(R.id.btn_PatternGrow);
        btn_PatternShrink = findViewById(R.id.btn_PatternShrink);
        btn_RowHeightGrow = findViewById(R.id.btn_RowHeightGrow);
        btn_RowHeightShrink = findViewById(R.id.btn_RowHeightShrink);
        btn_PatternLeft = findViewById(R.id.btn_PatternLeft);
        btn_PatternRight = findViewById(R.id.btn_PatternRight);
        btn_ImageDown = findViewById(R.id.btn_ImageDown);
        btn_ImageUp = findViewById(R.id.btn_ImageUp);

        iv_Pattern = findViewById(R.id.iv_Pattern);

        adapter = new DatabaseAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_OpenImage :
                openImage();
                return true;
            case R.id.action_RecentPatternList :
                if ( uriImage != null ) {
                    saveToDb();
                }
                Intent intent = new Intent(getApplicationContext(), PatternList.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO: Laden ausgewählte Image
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            saveToDb();

            uriImage = data.getData();
            loadImage(uriImage);

            adapter.open();
            Pattern pattern = adapter.getPatternByUri(uriImage.toString());
            if ( pattern != null) {
                iv_Pattern.getLayoutParams().height = pattern.getRowHeight();
                iv_Pattern.scrollTo( pattern.getImageScrollX(), pattern.getImageScrollY());
                imageScale(pattern.getImageScalle());
                pattern.setLastOpened(new Date().getTime());
                adapter.update(pattern);
            }
            adapter.close();
        }
    }

    // сохранение состояния
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if ( uriImage != null) outState.putString(IMAGE_URI, uriImage.toString());

        outState.putInt(ROW_HEIGHT, iv_Pattern.getMeasuredHeight());
        outState.putInt(IMAGE_SCROLL_X, iv_Pattern.getScrollX());
        outState.putInt(IMAGE_SCROLL_Y, iv_Pattern.getScrollY());
        outState.putFloat(IMAGE_SCALE, iv_Pattern.getScaleX());

        super.onSaveInstanceState(outState);
    }

    // получение ранее сохраненного состояния
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if ( savedInstanceState.containsKey(IMAGE_URI)) {
            uriImage = Uri.parse( savedInstanceState.getString(IMAGE_URI) );
            loadImage(uriImage);
        }

        if ( savedInstanceState.containsKey(ROW_HEIGHT)) {
            iv_Pattern.getLayoutParams().height = savedInstanceState.getInt(ROW_HEIGHT);
        }

        if ( savedInstanceState.containsKey(IMAGE_SCROLL_Y)) {
            int x = savedInstanceState.getInt(IMAGE_SCROLL_X);
            int y = savedInstanceState.getInt(IMAGE_SCROLL_Y);

            iv_Pattern.scrollTo(x, y);
        }

        if ( savedInstanceState.containsKey(IMAGE_SCALE)) {
            float scale = savedInstanceState.getFloat(IMAGE_SCALE);
            imageScale(scale);
        }
    }

    private void saveToDb() {
        if ( uriImage != null) {
            adapter.open();
            Pattern pattern = new Pattern(
                    patternId,
                    uriImage.toString(),
                    iv_Pattern.getMeasuredHeight(),
                    iv_Pattern.getScrollX(),
                    iv_Pattern.getScrollY(),
                    iv_Pattern.getScaleX());
            pattern.setLastOpened(new Date().getTime());

            if (patternId > 0) {
                adapter.update(pattern);
            } else {
                adapter.insert(pattern);
            }
            adapter.close();
        }
    }

    private void loadImage(Uri uri) {
        if ( uri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                iv_Pattern.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void imageZoomIn(View view) {
        scale = scale * ( 1 + SCALE_STEP );
        scale = checkScale(scale);
        imageScale(scale);
    }

    public void imageZoomOut(View view) {
        scale = scale * ( 1 - SCALE_STEP );
        scale = checkScale(scale);
        imageScale(scale);
    }

    public void imageOriginal(View view) {
        scale = 1f;
        imageScale(scale);
    }

    public void imageFit(View view) {
        float iw = iv_Pattern.getMeasuredWidth();
        float iW = iv_Pattern.getDrawable().getIntrinsicWidth();

        scale = iw / iW;
        scale = checkScale(scale);

        imageScale(scale);
        iv_Pattern.scrollTo(0, iv_Pattern.getScrollY());
    }

    public void imageScale(Float scale) {
        if ( scale != null) {
            matrix.setScale(scale, scale);
            iv_Pattern.setImageMatrix(matrix);
        }
    }

    public void rowShrink( View view) {
        iv_Pattern.requestLayout();
        iv_Pattern.getLayoutParams().height = iv_Pattern.getLayoutParams().height - ROW_DIFF;
    }

    public void rowGrow( View view) {
        iv_Pattern.requestLayout();
        iv_Pattern.getLayoutParams().height = iv_Pattern.getLayoutParams().height + ROW_DIFF;
    }

    public void imageUp(View view) {
        if ( iv_Pattern.getScrollY() + ROW_DIFF < iv_Pattern.getDrawable().getIntrinsicHeight()) {
            iv_Pattern.scrollTo(iv_Pattern.getScrollX(), iv_Pattern.getScrollY() + ROW_DIFF);
        }
    }

    public void imageDown(View view) {
        if ( iv_Pattern.getScrollY() - ROW_DIFF > 0 ) {
            iv_Pattern.scrollTo(iv_Pattern.getScrollX(), iv_Pattern.getScrollY() - ROW_DIFF);
        }
    }

    public void rowUp(View view) {
        iv_Pattern.scrollTo(iv_Pattern.getScrollX(), iv_Pattern.getScrollY() - iv_Pattern.getLayoutParams().height);
        if ( iv_Pattern.getScrollY() < 0 ) {
            iv_Pattern.scrollTo(iv_Pattern.getScrollX(), 0);
        }
    }

    public void rowDown(View view) {
        iv_Pattern.scrollTo(iv_Pattern.getScrollX(), iv_Pattern.getScrollY() + iv_Pattern.getLayoutParams().height);
        if ( iv_Pattern.getScrollY() > iv_Pattern.getDrawable().getIntrinsicHeight() * scale ) {
            iv_Pattern.scrollTo(iv_Pattern.getScrollX(), (int) ( iv_Pattern.getDrawable().getIntrinsicHeight() * scale ) - iv_Pattern.getMeasuredHeight());
        }
    }

    public void patternLeft(View view) {
        int y = iv_Pattern.getScrollY();
        int scrollX = iv_Pattern.getMeasuredWidth() / 2;

        iv_Pattern.scrollTo(iv_Pattern.getScrollX() - scrollX, y);
        if ( iv_Pattern.getScrollX() < 0 ) {
            iv_Pattern.scrollTo(0, y);
        }
    }

    public void patternRight(View view) {
        int iw = iv_Pattern.getMeasuredWidth();
        float iWf = iv_Pattern.getDrawable().getIntrinsicWidth() * scale ;
        int iW = (int) iWf;
        int y = iv_Pattern.getScrollY();
        int scrollX = iw / 2;
        iv_Pattern.scrollTo(iv_Pattern.getScrollX() + scrollX, y);
        if ( iv_Pattern.getScrollX() + iw > iW ) {
            iv_Pattern.scrollTo(iW - iw, y);
        }
    }

    public void openImage() {
        Intent intent = new Intent();
    // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
    // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_IMAGE_REQUEST);
    }

    private float checkScale(float scale) {
        return Math.max(MIN_SCALE, Math.min(scale, MAX_SCALE));
    }

}
