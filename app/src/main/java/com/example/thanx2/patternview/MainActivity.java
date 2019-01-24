package com.example.thanx2.patternview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.thanx2.patternview.database.DatabaseAdapter;
import com.example.thanx2.patternview.helper.ImageHelper;
import com.example.thanx2.patternview.helper.PatternView;
import com.example.thanx2.patternview.model.Pattern;

import java.io.IOException;
import java.util.Date;

import static com.example.thanx2.patternview.constant.Constant.ORIGINAL_SCALE;
import static com.example.thanx2.patternview.constant.Constant.ROW_HEIGHT_DEFAULT;

public class MainActivity extends AppCompatActivity {

    private int PICK_GALERY_REQUEST = 1;
    private int PICK_IMAGE_REQUEST = 2;

    final static String IMAGE_URI = "IMAGE_URI";
    final static String ROW_HEIGHT = "ROW_HEIGHT";
    final static String IMAGE_SCROLL_X = "IMAGE_SCROLL_X";
    final static String IMAGE_SCROLL_Y = "IMAGE_SCROLL_Y";
    final static String IMAGE_SCALE = "IMAGE_SCALE";

    Uri uriImage;

    Button btn_RowHeightShrink, btn_RowHeightGrow, btn_RowDown, btn_RowUp,
            btn_PatternLeft, btn_PatternRight, btn_ImageUp, btn_ImageDown,
            btn_ZoomIn, btn_OriginalZoom, btn_ImageFit, btn_ZoomOut;

    PatternView iv_Pattern;

    private DatabaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btn_RowUp = findViewById(R.id.btn_RowUp);
        btn_RowDown = findViewById(R.id.btn_RowDown);
        btn_RowHeightGrow = findViewById(R.id.btn_RowHeightGrow);
        btn_RowHeightShrink = findViewById(R.id.btn_RowHeightShrink);
        btn_PatternLeft = findViewById(R.id.btn_PatternLeft);
        btn_PatternRight = findViewById(R.id.btn_PatternRight);
        btn_ImageDown = findViewById(R.id.btn_ImageDown);
        btn_ImageUp = findViewById(R.id.btn_ImageUp);
        btn_ZoomIn = findViewById(R.id.btn_ZoomIn);
        btn_ZoomOut = findViewById(R.id.btn_ZoomOut);
        btn_OriginalZoom = findViewById(R.id.btn_OriginalZoom);
        btn_ImageFit = findViewById(R.id.btn_ImageFit);

        iv_Pattern = findViewById(R.id.iv_Pattern);
        iv_Pattern.setRowHeight(ROW_HEIGHT_DEFAULT);

        btn_RowUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { iv_Pattern.rowUp(); }});
        btn_RowDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { iv_Pattern.rowDown(); }});
        btn_RowHeightGrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { iv_Pattern.rowGrow(); }});
        btn_RowHeightShrink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { iv_Pattern.rowShrink(); }});
        btn_PatternLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { iv_Pattern.patternLeft(); }});
        btn_PatternRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { iv_Pattern.patternRight(); }});
        btn_ImageUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { iv_Pattern.imageUp(); }});
        btn_ImageDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { iv_Pattern.imageDown(); }});
        btn_ZoomIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { iv_Pattern.imageZoomIn(); }});
        btn_ZoomOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { iv_Pattern.imageZoomOut(); }});
        btn_OriginalZoom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { iv_Pattern.imageOriginalZoom(); }});
        btn_ImageFit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { iv_Pattern.imageFit(); }});

        adapter = new DatabaseAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_OpenImage :
                openImage();
                return true;
            case R.id.action_RecentPatternList :
                saveToDb();
                Intent intent = new Intent(getApplicationContext(), PatternListActivity.class);
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Image geöffnet
        if (requestCode == PICK_GALERY_REQUEST && resultCode == RESULT_OK && intent != null && intent.getData() != null) {

            // aktuelles Bild speichern
            saveToDb();
            restoreImage(intent.getData(), intent );
        } else if( requestCode == PICK_IMAGE_REQUEST ){
            if(resultCode == RESULT_OK && intent != null && intent.hasExtra(IMAGE_URI)){
                restoreImage(Uri.parse(intent.getStringExtra(IMAGE_URI)), intent);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void restoreImage(Uri uri, Intent intent) {
        uriImage = uri;

        loadImage(uriImage, intent);

        adapter.open();
        Pattern pattern = adapter.getPatternByUri(uriImage.toString());
        if ( pattern != null) {
            iv_Pattern.setRowHeight(pattern.getRowHeight());
            iv_Pattern.imageScale(pattern.getImageScalle());
            iv_Pattern.scrollTo( pattern.getImageScrollX(), pattern.getImageScrollY());
            pattern.setLastOpened(new Date().getTime());
            adapter.update(pattern);
        } else {
            iv_Pattern.setRowHeight(ROW_HEIGHT_DEFAULT);
            iv_Pattern.imageScale(ORIGINAL_SCALE);
            iv_Pattern.scrollTo(0, 0);
            Pattern newPattern = new Pattern( uriImage.toString(), ROW_HEIGHT_DEFAULT, 0, 0, ORIGINAL_SCALE);
            adapter.insert(newPattern);
        }
        adapter.close();
    }

    // сохранение состояния
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if ( uriImage != null) outState.putString(IMAGE_URI, uriImage.toString());

        outState.putInt(ROW_HEIGHT, iv_Pattern.getMeasuredHeight());
        outState.putInt(IMAGE_SCROLL_X, iv_Pattern.getScrollX());
        outState.putInt(IMAGE_SCROLL_Y, iv_Pattern.getScrollY());
        outState.putFloat(IMAGE_SCALE, iv_Pattern.getScale());

        super.onSaveInstanceState(outState);
    }

    // получение ранее сохраненного состояния
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if ( savedInstanceState.containsKey(IMAGE_URI)) {
            uriImage = Uri.parse( savedInstanceState.getString(IMAGE_URI) );
            loadImage(uriImage, getIntent());
        }

        if ( savedInstanceState.containsKey(ROW_HEIGHT)) {
            iv_Pattern.getLayoutParams().height = savedInstanceState.getInt(ROW_HEIGHT);
        }

        if ( savedInstanceState.containsKey(IMAGE_SCALE)) {
            iv_Pattern.imageScale( savedInstanceState.getFloat(IMAGE_SCALE) );
        }

        if ( savedInstanceState.containsKey(IMAGE_SCROLL_Y)
                && savedInstanceState.containsKey(IMAGE_SCROLL_Y)) {
            int x = savedInstanceState.getInt(IMAGE_SCROLL_X);
            int y = savedInstanceState.getInt(IMAGE_SCROLL_Y);

            iv_Pattern.scrollTo(x, y);
        }
    }

    private void saveToDb() {
        if ( uriImage != null ) {
            adapter.open();

            Pattern dbPattern = adapter.getPatternByUri(uriImage.toString());

            if ( dbPattern == null ) {
                Pattern pattern = new Pattern(
                        uriImage.toString(),
                        iv_Pattern.getMeasuredHeight(),
                        iv_Pattern.getScrollX(),
                        iv_Pattern.getScrollY(),
                        iv_Pattern.getScale()
                );
                 adapter.insert(pattern);
            } else {
                dbPattern.setImageScalle(iv_Pattern.getScale());
                dbPattern.setImageScrollX(iv_Pattern.getScrollX());
                dbPattern.setImageScrollY(iv_Pattern.getScrollY());
                dbPattern.setRowHeight(iv_Pattern.getMeasuredHeight());
                dbPattern.setLastOpened(new Date().getTime());
                adapter.update(dbPattern);
            }
            adapter.close();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadImage(Uri uri, Intent intent) {
        if ( uri != null) {
            try {
                final int takeFlags = intent.getFlags()
                        & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(uri, takeFlags);
                Bitmap bitmap = ImageHelper.getBitmapFromUri(uri, getApplicationContext());
                iv_Pattern.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void openImage() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_GALERY_REQUEST);
    }



}
