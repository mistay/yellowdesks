package at.langhofer.yellowdesks3;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle b = getIntent().getExtras();
        int value = -1; // or other values
        if(b != null)
            value = b.getInt("itemclicked");

        System.out.println("on create, clicked:" + value);

        Item item = Data.getInstance().getData().get(value);

        final TextView textViewDeskstatus2 = (TextView) findViewById(R.id.deskstatus2);
        textViewDeskstatus2.setText("YELLOW desks: " + item.gettotalDesks() + "/" + item.getAvailableDesks());

        final TextView textviewDetail = (TextView) findViewById(R.id.textviewDetail);
        textviewDetail.setText("Ydetals");


        final ImageView detailImage = (ImageView) findViewById(R.id.detailimage);
        Drawable drawable = getResources().getDrawable(R.drawable.alex);


        detailImage.setImageDrawable(drawable);




    }
}
