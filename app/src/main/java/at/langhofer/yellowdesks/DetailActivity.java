package at.langhofer.yellowdesks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.PostalAddress;

public class DetailActivity extends AppCompatActivity implements PaymentMethodNonceCreatedListener, BraintreeErrorListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle b = getIntent().getExtras();
        long hostId = -1;
        if (b != null)
            hostId = b.getLong("hostId");

        System.out.println("DetailActivity. on create, hostId:" + hostId);

        final Host host = Data.getInstance().getHost(hostId);

        if (host == null) {
            System.out.println("DetailActivity::onCreate: host was null wtf? :(");
            return;
        }

        //final TextView textViewDeskstatus2 = (TextView) findViewById(R.id.deskstatus2);
        //textViewDeskstatus2.setText("YELLOW desks: " + host.gettotalDesks() + "/" + host.getAvailableDesks());

        final TextView textviewDetail = (TextView) findViewById(R.id.textviewDetail);
        textviewDetail.setText("Included: " + host.getDetails());

        //View root = textviewDetail.getRootView();
        //getWindow().getDecorView().setBackgroundColor(Data.colorYellowdesks());
        //root.setBackgroundColor(  Data.colorYellowdesks()  );

        final TextView tvExtras = (TextView) findViewById(R.id.tvExtras);
        tvExtras.setText("Extra: " + host.getExtras());


        final TextView tvOpeninghours = (TextView) findViewById(R.id.tvOpeninghours);
        if (host.getOpenFrom() == null)
            tvOpeninghours.setText(String.format("Opening Hours: n/a"));
        else
            tvOpeninghours.setText(String.format("Opening Hours: Mon %s till Sun %s", host.getOpenFrom(), host.getOpenTill()));

        StringBuilder tmp= new StringBuilder();
        if (host.getPrice1Day() != null)
            tmp.append ( String.format("1 day: %s EUR\n" , host.getPrice1Day()));
        if (host.getPrice1Day() != null)
            tmp.append ( String.format("10 days: %s EUR\n" , host.getPrice10Days()));
        if (host.getPrice1Day() != null)
            tmp.append ( String.format("1 month: %s EUR\n" , host.getPrice1Month()));
        if (host.getPrice1Day() != null)
            tmp.append ( String.format("6 months: %s EUR\n" , host.getPrice6Months()));


        System.out.println("dbug: " + host.getPrice1Day());
        final TextView tvPrices = (TextView) findViewById(R.id.tvPrices);
        tvPrices.setText(String.format("Prices: %s", ((tmp.length() == 0) ? "n/a" : "\n" + tmp.toString())));

        final TextView hostDetails = (TextView) findViewById(R.id.txt_hostdetails);
        hostDetails.setText( String.format("%s\n%s desks available", host.getTitle(), host.getAvailableDesks()) );


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




        final Button btnBookNow = (Button) findViewById(R.id.btnBookNow);
        btnBookNow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("btnBookNow button clicked");
                Intent myIntent = new Intent(DetailActivity.this, BookingresponseActivity.class);
                myIntent.putExtra("hostId", host.getId());
                DetailActivity.this.startActivity(myIntent);
            }
        });


        BraintreeFragment mBraintreeFragment = null;
        try {
            String mAuthorization="production_t5tm8zyp_8jmd464425vhc6n2";
            mBraintreeFragment = BraintreeFragment.newInstance(this, mAuthorization);
            // mBraintreeFragment is ready to use!



            System.out.println("braintree init complete.");
            System.out.println("braintree: " + mAuthorization);

        } catch (InvalidArgumentException e) {
            System.out.println("exception braintree: " + e.toString());
        }

        final BraintreeFragment a = mBraintreeFragment;

        final Button btnPayNow = (Button) findViewById(R.id.btnPayNow);
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (a!=null)

                    System.out.println("paypal started");


                    PayPal.authorizeAccount(a);

                    System.out.println("paypal authed");


                PayPalRequest request = new PayPalRequest("0.01");
                request.intent(PayPalRequest.INTENT_SALE);
                //request.userAction(PayPalRequest.USER_ACTION_COMMIT);
                PayPal.requestOneTimePayment(a, request);

                System.out.println("paypal getPayPalRequest");

            }
        });



        final ImageButton btnBacktomap = (ImageButton) findViewById(R.id.btnBacktomap);
        btnBacktomap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("btnBacktomap button clicked");
                Intent myIntent = new Intent( DetailActivity.this, MapActivity.class );
                System.out.println("opening detail activity with key: " + host.getId());
                DetailActivity.this.startActivity(myIntent);
            }
        });







        final VideoView vvHost = (VideoView) findViewById(R.id.vvHost);
        vvHost.setVisibility(View.GONE);
        if (host.getVideoURL() != null) {
            System.out.println("trying to play video: " + host.getVideoURL());

            Uri uri = Uri.parse(host.getVideoURL());
            if (uri != null) {
                vvHost.setVisibility(View.VISIBLE);
                System.out.println("setting up video: " + host.getVideoURL());
                vvHost.setMediaController(new MediaController(this));
                vvHost.setVideoURI(uri);
                vvHost.requestFocus();
                vvHost.start();
            }
        }




    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        System.out.println("onpaymentmethodnoncecreated");
        System.out.println("onpaymentmethodnoncecreated" + paymentMethodNonce);

        PayPalAccountNonce paypalAccountNonce = (PayPalAccountNonce) paymentMethodNonce;
        PostalAddress billingAddress = paypalAccountNonce.getBillingAddress();
        String streetAddress = billingAddress.getStreetAddress();
        String extendedAddress = billingAddress.getExtendedAddress();
        String locality = billingAddress.getLocality();
        String countryCodeAlpha2 = billingAddress.getCountryCodeAlpha2();
        String postalCode = billingAddress.getPostalCode();
        String region = billingAddress.getRegion();

        System.out.println(String.format("%s %s %s %s %s %s", streetAddress, extendedAddress, locality, countryCodeAlpha2, postalCode, region));

    }

    @Override
    public void onError(Exception error) {
        System.out.println("error: " + error.toString());
    }


}
