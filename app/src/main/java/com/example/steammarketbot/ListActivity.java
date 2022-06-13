package com.example.steammarketbot;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.steammarketbot.core.items.Item;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    Button addItem;
    ListView lvItems;
    Spinner categories;
    ArrayList <Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        addItem = findViewById(R.id.btnAddItem);
        lvItems = findViewById(R.id.lvItems);
        categories = findViewById(R.id.spinnerCategories);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        items = new ArrayList<>();
        ListView lvItems = findViewById(R.id.lvItems);
        lvItems.setAdapter(new ItemListAdapter(this, items));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}