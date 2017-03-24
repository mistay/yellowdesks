package com.yellowdesks.yellowdesks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ResultsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        YellowdeskRowAdapter adapter = new YellowdeskRowAdapter(this, R.layout.listviewentry_searchresult, Data.getInstance().getData());

        ListView listView = (ListView) findViewById(R.id.listviewSearchResults);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("clicked item" + i + " long: " + l);

                Host host = (Host) adapterView.getItemAtPosition(i);
                System.out.println("clicked item" + host.getId() );

                Intent myIntent = new Intent(ResultsActivity.this, DetailActivity.class);
                myIntent.putExtra("hostId", host.getId());

                ResultsActivity.this.startActivity(myIntent);
            }
        });

    }

}
