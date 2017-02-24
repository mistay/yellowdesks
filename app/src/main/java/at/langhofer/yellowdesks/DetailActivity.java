package at.langhofer.yellowdesks;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Space;
import android.widget.TextView;
import android.widget.VideoView;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;


public class DetailActivity extends AppCompatActivity {
    Host host;


    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AWAYB0oayBfWbFgfqgRYGIpMUXfw_5YvgR6ObNbNfxOXvxnC0YwoZW0wvF9bIuPZwX8lrec0vTuTuJ-f";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Yellowdesks")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.yellowdesks.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.yellowdesks.com/legal"));


    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println(String.format("%d 5d", requestCode, resultCode));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle b = getIntent().getExtras();



        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);



        long hostId = -1;
        if (b != null)
            hostId = b.getLong("hostId");

        System.out.println("DetailActivity. on create, hostId:" + hostId);

        host = Data.getInstance().getHost(hostId);

        if (host == null) {
            System.out.println("DetailActivity::onCreate: host was null wtf? :(");
            return;
        }


        final LinearLayout llimages = (LinearLayout) findViewById( R.id.llimages );


        final Dialog dialogFullscreenImage = new Dialog(DetailActivity.this,android.R.style.Theme_Translucent);
        dialogFullscreenImage.requestWindowFeature( Window.FEATURE_NO_TITLE);
        dialogFullscreenImage.setCancelable(false);
        dialogFullscreenImage.setContentView(R.layout.preview_image);
        Button btnClose = (Button)dialogFullscreenImage.findViewById(R.id.btnIvClose);
        final ImageView ivPreview = (ImageView)dialogFullscreenImage.findViewById(R.id.iv_preview_image);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialogFullscreenImage.dismiss();
            }
        });

        dialogFullscreenImage.setOnKeyListener( new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                    dialogFullscreenImage.dismiss();
                }
                return true;
            }
        } );




        HashMap<String, Bitmap> images = host.getImages();

        if (images != null) {
            for (HashMap.Entry<String, Bitmap> entry : images.entrySet()) {
                Bitmap bitmap = entry.getValue();

                System.out.println("adding imageview. width parent: " + llimages.getWidth());
                final ImageView ivImage = new ImageView( this );

                final Space space = new Space(this);
                space.setMinimumHeight( 20 );

                ivImage.setMinimumWidth( llimages.getWidth() );
                ivImage.setMinimumHeight( 500 );
                ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                ivImage.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("showing dialog full screen video");
                        ivPreview.setImageDrawable( ivImage.getDrawable());

                        dialogFullscreenImage.show();
                    }
                } );










                llimages.addView( ivImage );
                llimages.addView ( space );

                if (bitmap == null) {
                    // download

                    RotateAnimation anim = new RotateAnimation(0f, 360, 0, 0 );
                    System.out.println("pivot: " + ivImage.getWidth()/2);
                    anim.setInterpolator(new LinearInterpolator());
                    anim.setRepeatCount( Animation.INFINITE);
                    anim.setDuration(1000);
                    ivImage.setImageBitmap( BitmapFactory.decodeResource(getResources(), R.drawable.loader ));
                    ivImage.startAnimation( anim );

                    DownloadWebimageTask downloadWebimageTask = new DownloadWebimageTask();
                    downloadWebimageTask.delegate = new DelegateImageDownloaded() {
                        @Override
                        public void imageDownloaded(Bitmap result, Object tag) {
                        System.out.println("taskCompletionResult tag: " + tag.toString());

                        HashMap.Entry<String, Bitmap> entry = (HashMap.Entry<String, Bitmap> ) tag;

                        entry.setValue( result );

                        ivImage.setAnimation(null);
                        ivImage.setImageBitmap( entry.getValue() );

                        host.setBitmapForImage( entry.getKey(), result );
                        System.out.println("taskCompletionResult: " + result);
                        }
                    };
                    System.out.println("sending download request: " + entry.getKey());

                    downloadWebimageTask.setTag( entry );
                    downloadWebimageTask.execute(entry.getKey());
                } else {

                    ivImage.setAnimation(null);
                    System.out.println("setting image from already downloaded cache");
                    ivImage.setImageBitmap( bitmap );





                }
            }
        } else {
            System.out.println("images were null :(");
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
            tvOpeninghours.setText(String.format("Opening Hours: Mon %s till Fri %s%s", host.getOpenFrom(), host.getOpenTill(), host.getOpen247fixworkers() ? "\nMember Access 24/7" : "" ));

        StringBuilder tmp= new StringBuilder();
        if (host.getPrice1Day() != null)
            tmp.append ( String.format("1 day: %s EUR\n" , host.getPrice1Day()));
        if (host.getPrice1Day() != null)
            tmp.append ( String.format("10 days: %s EUR\n" , host.getPrice10Days()));
        if (host.getPrice1Day() != null)
            tmp.append ( String.format("1 month: %s EUR\n" , host.getPrice1Month()));
        if (host.getPrice1Day() != null)
            tmp.append ( String.format("6 months: %s EUR\n" , host.getPrice6Months()));
        tmp.append ( String.format("prices excluding VAT\n") );


        System.out.println("host getPrice1Day(): " + host.getPrice1Day());
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
                public void imageDownloaded(Bitmap result, Object tag) {
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





        final Button btnPayNow = (Button) findViewById(R.id.btnPayNow);
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                System.out.println("btnPayNow");

                /*
                 * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
                 * Change PAYMENT_INTENT_SALE to
                 *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
                 *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
                 *     later via calls from your server.
                 *
                 * Also, to include additional payment details and an item list, see getStuffToBuy() below.
                 */

                if (price == null)
                    System.out.println("price null, no payment sinnvoll");
                else {

                    PayPalPayment thingToBuy = new PayPalPayment( new BigDecimal( price ), "EUR", String.format("Yellow Desk %s - %s at host: %s", begin, end, host.getHost()),
                            PayPalPayment.PAYMENT_INTENT_SALE );

                /*
                 * See getStuffToBuy(..) for examples of some available payment options.
                 */


                    Intent intent = new Intent( DetailActivity.this, PaymentActivity.class );

                    // send the same configuration for restart resiliency
                    intent.putExtra( PayPalService.EXTRA_PAYPAL_CONFIGURATION, config );

                    intent.putExtra( PaymentActivity.EXTRA_PAYMENT, thingToBuy );

                    startActivityForResult( intent, REQUEST_CODE_PAYMENT );
                }
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

                System.out.println("videoview element set to visible, video started w/ url: " + host.getVideoURL());

            }
        }





        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        System.out.println(String.format("day: %d month: %d year: %d", day, month, year));


        final DatePicker dpFrom = (DatePicker) findViewById(R.id.dpFrom);
        dpFrom.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                changedate();
            }
        });

        final DatePicker dpTo = (DatePicker) findViewById(R.id.dpTo);
        dpTo.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                changedate();
            }
        });
    }

    Double price = null;
    String begin="";
    String end="";
    private void changedate() {
        System.out.println("changedate()");
        final DatePicker dpFrom = (DatePicker) findViewById(R.id.dpFrom);
        final DatePicker dpTo = (DatePicker) findViewById(R.id.dpTo);

        final TextView tvPricecalc = (TextView) findViewById( R.id.tvPricecalc );

        DownloadWebTask downloadWebTask = new DownloadWebTask();
        downloadWebTask.delegate = new TaskDelegate() {
            @Override
            public void taskCompletionResult(String raw) {
                if (raw != null && raw != "") {
                    try {
                        JSONObject value = new JSONObject(raw);
                        Integer days = value.getInt( "count" );
                        begin = value.getString("begin");
                        end = value.getString("end");
                        price = value.getDouble( "price" );
                        if (price != null)
                            tvPricecalc.setText(String.format("Days: %s. Price: %s", days, NumberFormat.getCurrencyInstance().format(price)));
                        else
                            tvPricecalc.setText(String.format("Days: %s. Price: n/a", days));
                        System.out.println("eof debugging new loginappfb()");
                    } catch (Exception e) {
                        System.out.println("could not parse login json: " + raw + ". exception: " + e.toString());
                    }
                }

            }
        };

        String url = "https://yellowdesks.com/holidays/getprice/" + host.getId() + "/" + dpFrom.getYear() + "-" + (dpFrom.getMonth()+1) + "-" + dpFrom.getDayOfMonth() + "/" + dpTo.getYear() + "-" + (dpTo.getMonth()+1) + "-" + dpTo.getDayOfMonth();
        tvPricecalc.setText("Loading...");
        downloadWebTask.execute(url);
        System.out.println("sent url: " + url);


        //calendar.set(dpFrom.getYear(), dpFrom.getMonth(),dpFrom.getDayOfMonth());




    }
}
