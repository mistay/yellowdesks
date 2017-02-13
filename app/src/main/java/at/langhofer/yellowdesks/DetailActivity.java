package at.langhofer.yellowdesks;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
        long hostId = -1;
        if (b != null)
            hostId = b.getLong("hostId");

        System.out.println("DetailActivity. on create, hostId:" + hostId);

        Host host = Data.getInstance().getHost(hostId);

        if (host == null) {
            System.out.println("DetailActivity::onCreate: host was null wtf? :(");
            return;
        }

        final TextView textViewDeskstatus2 = (TextView) findViewById(R.id.deskstatus2);
        textViewDeskstatus2.setText("YELLOW desks: " + host.gettotalDesks() + "/" + host.getAvailableDesks());

        final TextView textviewDetail = (TextView) findViewById(R.id.textviewDetail);
        textviewDetail.setText( host.getDetails() );

        final TextView hostDetails = (TextView) findViewById(R.id.txt_hostdetails);
        hostDetails.setText("YELLOW desks: " + host.getHostDetails());


        final ImageView detailImage = (ImageView) findViewById(R.id.detailimage);

//        Drawable drawable = getResources().getDrawable(R.drawable.alex);

        if (host.getBitmap()!=null)
            detailImage.setImageDrawable(new BitmapDrawable(host.getBitmap()));
        else {
            DelegateImageDownloaded downloadFinished = new DelegateImageDownloaded() {
                @Override
                public void imageDownloaded(Bitmap result) {
                    if (result != null) {
                        System.out.println("imageDownloaded, result: " + result.toString());

                        Drawable myDrawable = new BitmapDrawable(result);
                        detailImage.setImageDrawable(myDrawable);
                    } else {
                        System.out.println("DelegateImageDownloaded downloadFinished but result was null :(");
                    }
                }
            };

            System.out.println("downloadImage. id: " + host.getId() + " url: " +  host.getImageURL() + " isnull? " + ( host.getImageURL() == null));
            if (host.getImageURL() != null)
                Data.getInstance().downloadImage(host, downloadFinished);
        }


    }
}
