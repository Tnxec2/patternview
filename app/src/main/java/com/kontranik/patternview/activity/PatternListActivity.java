package com.kontranik.patternview.activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kontranik.patternview.R;
import com.kontranik.patternview.adapter.PatternAdapter;
import com.kontranik.patternview.database.DatabaseAdapter;
import com.kontranik.patternview.model.Pattern;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class PatternListActivity extends AppCompatActivity {

    List<Pattern> patterns;

    ListView patternListView;
    PatternAdapter patternAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_list);

        DatabaseAdapter adapter = new DatabaseAdapter(this);
        adapter.open();

        // Datenbank auf Maximale Anzahl bereinigen
        adapter.clean();

        patterns = adapter.getPatterns();
        adapter.close();

        patternListView = findViewById(R.id.lv_patternList);

        patternAdapter = new PatternAdapter(this, R.layout.patternlist_item, patterns);

        patternListView.setAdapter(patternAdapter);

        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                giveBackPattern(position);
            }
        };
        patternListView.setOnItemClickListener(itemListener);

        registerForContextMenu(patternListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.patternlist_context_menu, menu);
        menu.setHeaderTitle(R.string.select_action);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        if(item.getItemId()==R.id.action_open){
            giveBackPattern(info.position);
        } else if(item.getItemId()==R.id.action_delete){
            deletePattern(info.position);
        }else{
            return false;
        }
        return true;
    }


    private void giveBackPattern(int position) {
        Pattern selectedPattern = patterns.get(position);

        // und zurück an MainActivity übergeben
        Intent data = new Intent();
        data.putExtra( MainActivity.IMAGE_URI, selectedPattern.getUriString() );
        setResult(RESULT_OK, data);
        finish();
    }

    private void deletePattern(int position) {
        DatabaseAdapter dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();
        if ( dbAdapter.deleteByUri( patterns.get(position).getUriString()) ) {
            patterns.remove(position);
            patternAdapter.notifyDataSetChanged();
        }
        dbAdapter.close();
    }
}
