package at.langhofer.yellowdesks3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);



        String[] myStringArray = {"a", "b"};

        List<Item> arrayOfList = new ArrayList<Item>();
        arrayOfList.add(new Item("a", "b", "a", "b", "c"));
        arrayOfList.add(new Item("a", "b", "a", "b", "c"));
        arrayOfList.add(new Item("a", "b", "a", "b", "c"));

        YellowdeskRowAdapter adapter = new YellowdeskRowAdapter(this, R.layout.listviewentry_searchresult, arrayOfList);

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        //        android.R.layout.simple_list_item_1, myStringArray);


        ListView listView = (ListView) findViewById(R.id.listviewSearchResults);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("clicked item");
            }
        });

    }

}
