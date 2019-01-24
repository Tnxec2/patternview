package com.example.thanx2.patternview.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thanx2.patternview.R;
import com.example.thanx2.patternview.database.DatabaseAdapter;
import com.example.thanx2.patternview.helper.ImageHelper;
import com.example.thanx2.patternview.model.Pattern;

import java.io.IOException;
import java.util.List;

public class PatternAdapter extends ArrayAdapter<Pattern> {

    private LayoutInflater inflater;
    private int layout;
    private List<Pattern> patterns;

    public PatternAdapter(Context context, int resource, List<Pattern> patterns) {
        super(context, resource, patterns);
        this.patterns = patterns;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Pattern item = patterns.get(position);

        Bitmap bitmap;
        try {
            Uri uri = Uri.parse(item.getUri());
            bitmap = ImageHelper.getBitmapFromUri(uri, getContext());
            viewHolder.imageView.setImageBitmap( bitmap );
            String uriString = item.getUri();
            viewHolder.nameView.setText(  uriString.substring(uriString.lastIndexOf("%2F") + 3) );
            viewHolder.uriView.setText( item.getUri() );
            return convertView;
        } catch (IOException e) {
            e.printStackTrace();
            DatabaseAdapter adapter = new DatabaseAdapter( parent.getContext() );
            adapter.open();
            adapter.deleteByUri(item.getUri());
            adapter.close();
        }

        return null;
    }

    private class ViewHolder {
        final ImageView imageView;
        final TextView nameView;
        final TextView uriView;
        ViewHolder(View view){
            imageView = view.findViewById(R.id.image);
            nameView = view.findViewById(R.id.name);
            uriView = view.findViewById(R.id.uri);
        }
    }

}