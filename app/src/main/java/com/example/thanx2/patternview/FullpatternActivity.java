package com.example.thanx2.patternview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.thanx2.patternview.helper.ImageHelper;

public class FullpatternActivity extends AppCompatActivity {

    final static String IMAGE_URI = "IMAGE_URI";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullpattern);

        ImageView imageView = findViewById(R.id.iv_fullpattern);

        Intent intent = getIntent();
        Bundle arguments = getIntent().getExtras();

        if(arguments!=null && arguments.containsKey(IMAGE_URI)){
            Uri uri = (Uri) arguments.get(IMAGE_URI);
            if ( uri != null) {
                final int takeFlags = intent.getFlags()
                        & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(uri, takeFlags);
                try {
                    Bitmap bitmap = ImageHelper.getBitmapFromUri(uri, getApplicationContext());
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
