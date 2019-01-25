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
import android.widget.ImageButton;

import com.example.thanx2.patternview.database.DatabaseAdapter;
import com.example.thanx2.patternview.helper.ImageHelper;
import com.example.thanx2.patternview.helper.PatternView;
import com.example.thanx2.patternview.model.Pattern;

import java.util.Date;

import static com.example.thanx2.patternview.constant.Constant.ORIGINAL_SCALE;
import static com.example.thanx2.patternview.constant.Constant.ROW_HEIGHT_DEFAULT;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity {

    private int PICK_GALLERY_REQUEST = 1;
    private int PICK_IMAGE_REQUEST = 2;

    final static String IMAGE_URI = "IMAGE_URI";
    final static String ROW_HEIGHT = "ROW_HEIGHT";
    final static String IMAGE_SCROLL_X = "IMAGE_SCROLL_X";
    final static String IMAGE_SCROLL_Y = "IMAGE_SCROLL_Y";
    final static String IMAGE_SCALE = "IMAGE_SCALE";

    ImageButton btn_RowHeightShrink, btn_RowHeightGrow, btn_RowDown, btn_RowUp,
            btn_PatternLeft, btn_PatternRight, btn_ImageUp, btn_ImageDown,
            btn_ZoomIn, btn_OriginalZoom, btn_ImageFit, btn_ZoomOut, btn_ImageFull;

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
        btn_ImageFull = findViewById(R.id.btn_Full);

        iv_Pattern = findViewById(R.id.iv_Pattern);
        iv_Pattern.setPatternRowHeight(ROW_HEIGHT_DEFAULT);

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
        btn_ImageFull.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { openFullImage(); }});

        adapter = new DatabaseAdapter(this);

        disableButtons();

        adapter.open();
        Pattern lastOpenedPattern = adapter.getLastOpened();
        if ( lastOpenedPattern != null ) {
            iv_Pattern.setUri(lastOpenedPattern.getUri());
            iv_Pattern.setPatternRowHeight(lastOpenedPattern.getRowHeight());
            iv_Pattern.imageScale(lastOpenedPattern.getScale());
            iv_Pattern.scroll(lastOpenedPattern.getPatternX(), lastOpenedPattern.getPatternY());
            loadImage(getIntent());
        }
        adapter.close();
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
            case R.id.action_FullImage :
                openFullImage();
                return true;
            case R.id.action_RecentPatternList :
                saveToDb();
                Intent intent = new Intent(getApplicationContext(), PatternListActivity.class);
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
        if (requestCode == PICK_GALLERY_REQUEST) {
            if ( resultCode == RESULT_OK ) {
                if ( intent != null && intent.getData() != null ) {
                    // aktuelles Bild speichern
                    saveToDb();
                    restoreImage(intent.getData(), intent);
                }
            }
        } else if( requestCode == PICK_IMAGE_REQUEST ){
            if(resultCode == RESULT_OK) {
                if ( intent != null && intent.hasExtra(IMAGE_URI) ) {
                    restoreImage(Uri.parse(intent.getStringExtra(IMAGE_URI)), intent);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void restoreImage(Uri uri, Intent intent) {
        iv_Pattern.setUri(uri);

        loadImage(intent);

        adapter.open();
        Pattern pattern = adapter.getPatternByUri(iv_Pattern.getUri().toString());
        if ( pattern != null) {
            iv_Pattern.setPatternRowHeight( pattern.getRowHeight() );
            iv_Pattern.imageScale( pattern.getScale() );
            iv_Pattern.scroll( pattern.getPatternX(), pattern.getPatternY() );
            pattern.setLastOpened( new Date().getTime() );
            adapter.update(pattern);
        } else {
            iv_Pattern.setPatternRowHeight( ROW_HEIGHT_DEFAULT );
            iv_Pattern.imageScale( ORIGINAL_SCALE );
            iv_Pattern.scroll(0, 0);
            Pattern newPattern = new Pattern( iv_Pattern.getUri(), ROW_HEIGHT_DEFAULT, 0, 0, ORIGINAL_SCALE);
            adapter.insert(newPattern);
        }
        adapter.close();
    }

    // сохранение состояния
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if ( iv_Pattern.getUri() != null) outState.putString(IMAGE_URI, iv_Pattern.getUri().toString());

        outState.putInt(ROW_HEIGHT, iv_Pattern.getPatternRowHeight());
        outState.putInt(IMAGE_SCROLL_X, iv_Pattern.getPatternX());
        outState.putInt(IMAGE_SCROLL_Y, iv_Pattern.getPatternY());
        outState.putFloat(IMAGE_SCALE, iv_Pattern.getScale());

        super.onSaveInstanceState(outState);
    }

    // получение ранее сохраненного состояния
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if ( savedInstanceState.containsKey(IMAGE_URI)) {
            iv_Pattern.setUri( Uri.parse( savedInstanceState.getString(IMAGE_URI) ) );
            loadImage( getIntent() );
        }

        if ( savedInstanceState.containsKey(ROW_HEIGHT)) {
            iv_Pattern.setPatternRowHeight( savedInstanceState.getInt(ROW_HEIGHT) );
        }

        if ( savedInstanceState.containsKey(IMAGE_SCALE)) {
            iv_Pattern.imageScale( savedInstanceState.getFloat(IMAGE_SCALE) );
        }

        if ( savedInstanceState.containsKey(IMAGE_SCROLL_X)
                && savedInstanceState.containsKey(IMAGE_SCROLL_Y)) {
            int x =  savedInstanceState.getInt(IMAGE_SCROLL_X);
            int y = savedInstanceState.getInt(IMAGE_SCROLL_Y);

            iv_Pattern.scroll(x, y);
        }
    }

    private void saveToDb() {
        if ( iv_Pattern.getUri() != null ) {
            adapter.open();

            Pattern dbPattern = adapter.getPatternByUri(iv_Pattern.getUri().toString());

            if ( dbPattern == null ) {
                Pattern pattern = new Pattern(
                        iv_Pattern.getUri(),
                        iv_Pattern.getPatternRowHeight(),
                        iv_Pattern.getPatternX(),
                        iv_Pattern.getPatternY(),
                        iv_Pattern.getScale()
                );
                 adapter.insert(pattern);
            } else {
                dbPattern.setScale(iv_Pattern.getScale());
                dbPattern.setPatternX(iv_Pattern.getScrollX());
                dbPattern.setPatternY(iv_Pattern.getScrollY());
                dbPattern.setRowHeight(iv_Pattern.getPatternRowHeight());
                dbPattern.setLastOpened(new Date().getTime());
                adapter.update(dbPattern);
            }
            adapter.close();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadImage( Intent intent) {
        if ( iv_Pattern.getUri() != null) {
            try {
                final int takeFlags = intent.getFlags()
                        & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(iv_Pattern.getUri(), takeFlags);
                Bitmap bitmap = ImageHelper.getBitmapFromUri(iv_Pattern.getUri(), getApplicationContext());
                iv_Pattern.setImageBitmap(bitmap);
                enableButtons();
            } catch (Exception e) {
                // e.printStackTrace();
                iv_Pattern.setImageBitmap(null);
                iv_Pattern.initial();
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
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_GALLERY_REQUEST);
    }

    public void openFullImage() {
        if ( iv_Pattern.getUri() != null ) {
            Intent intent = new Intent(getApplicationContext(), FullpatternActivity.class);
            intent.putExtra(IMAGE_URI, iv_Pattern.getUri());
            startActivity(intent);
        }
    }

    public void enableButtons() {
        btn_ImageDown.setEnabled(true);
        btn_ImageUp.setEnabled(true);
        btn_ZoomIn.setEnabled(true);
        btn_ZoomOut.setEnabled(true);
        btn_OriginalZoom.setEnabled(true);
        btn_ImageFit.setEnabled(true);
        btn_ImageFull.setEnabled(true);
        btn_PatternRight.setEnabled(true);
        btn_PatternLeft.setEnabled(true);
        btn_RowDown.setEnabled(true);
        btn_RowUp.setEnabled(true);
    }

    public void disableButtons() {
        btn_ImageDown.setEnabled(false);
        btn_ImageUp.setEnabled(false);
        btn_ZoomIn.setEnabled(false);
        btn_ZoomOut.setEnabled(false);
        btn_OriginalZoom.setEnabled(false);
        btn_ImageFit.setEnabled(false);
        btn_ImageFull.setEnabled(false);
        btn_PatternRight.setEnabled(false);
        btn_PatternLeft.setEnabled(false);
        btn_RowDown.setEnabled(false);
        btn_RowUp.setEnabled(false);
    }
}
