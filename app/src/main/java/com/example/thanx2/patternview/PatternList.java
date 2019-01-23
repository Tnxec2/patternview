package com.example.thanx2.patternview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.thanx2.patternview.adapter.PatternAdapter;
import com.example.thanx2.patternview.database.DatabaseAdapter;
import com.example.thanx2.patternview.model.Pattern;

import java.util.ArrayList;

public class PatternList extends AppCompatActivity {

    private ArrayList<Pattern> patterns = new ArrayList<>();

    ListView patternListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_list);

        // начальная инициализация списка
        DatabaseAdapter adapter = new DatabaseAdapter(this);

        // получаем элемент ListView
        patternListView = findViewById(R.id.lv_patternList);
        // создаем адаптер
        PatternAdapter patternAdapter = new PatternAdapter(this, R.layout.patternlist_item, patterns);
        // устанавливаем адаптер
        patternListView.setAdapter(patternAdapter);
        // слушатель выбора в списке
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // получаем выбранный пункт
                Pattern selectedPattern = (Pattern) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "Selected: " + selectedPattern.getUri(),
                        Toast.LENGTH_SHORT).show();
            }
        };
        patternListView.setOnItemClickListener(itemListener);
    }

}
