package at.langhofer.yellowdesks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BookingresponseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookingresponse);

        Bundle b = getIntent().getExtras();
        long hostId = -1;
        if (b != null)
            hostId = b.getLong("hostId");

        System.out.println("DetailActivity. on create, hostId:" + hostId);

        final Host host = Data.getInstance().getHost(hostId);
        if (host == null) {
            System.out.println("bookingresponse: empty result");
            return;
        }

        // hook up data ready
        final TaskDelegate taskDelegate = new TaskDelegate() {
            @Override
            public void taskCompletionResult(String result) {
                // data is ready
                System.out.println( "data ready" );

                if (result == "OK") {
                    System.out.println("GUI: todo show success screen");
                }


            }
        };
        Data.getInstance().sendBookingRequest(host, new java.util.Date(), taskDelegate);
    }
}
