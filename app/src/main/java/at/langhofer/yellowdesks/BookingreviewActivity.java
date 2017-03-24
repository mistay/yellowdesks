package at.langhofer.yellowdesks;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Iterator;

public class BookingreviewActivity extends AppCompatActivity {

    Host host = null;

    String from = "";
    String to = "";
    String total = "";
    String booking_id = "";

    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     * <p>
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     * <p>
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;

    // note that these credentials will differ between live & sandbox environments.

    // sandbox hello-facilitator@yellowdesks.com:                                   AWAYB0oayBfWbFgfqgRYGIpMUXfw_5YvgR6ObNbNfxOXvxnC0YwoZW0wvF9bIuPZwX8lrec0vTuTuJ-f
    // production hello@yellowdesks.com (tested successfully mar 17):               ASYWIHLM6Qloye_viETFTHaC7kcAcgY_P2CaRkttpzDxJxNqRxRB_Dm6fNfW3po3pn4Cdi7gGF6yPkV4

    // https://developer.paypal.com/developer/applications/edit/QVNZV0lITE02UWxveWVfdmlFVEZUSGFDN2tjQWNnWV9QMkNhUmt0dHB6RHhKeE5xUnhSQl9EbTZmTmZXM3BvM3BuNENkaTdnR0Y2eVBrVjQ=
    private static final String CONFIG_CLIENT_ID = "ASYWIHLM6Qloye_viETFTHaC7kcAcgY_P2CaRkttpzDxJxNqRxRB_Dm6fNfW3po3pn4Cdi7gGF6yPkV4";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;


    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment( CONFIG_ENVIRONMENT )
            .clientId( CONFIG_CLIENT_ID )
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName( "Yellowdesks" )
            .merchantPrivacyPolicyUri( Uri.parse( "http://www.yellowdesks.com/termsandconditions" ) )
            .merchantUserAgreementUri( Uri.parse( "http://www.yellowdesks.com/termsandconditions" ) );


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra( PaymentActivity.EXTRA_RESULT_CONFIRMATION );
                if (confirm != null) {
                    try {
                        System.out.println("onActivityResult(): "  + confirm.toJSONObject().toString( 4 ) );
                        System.out.println( "onActivityResult(): "  + confirm.getPayment().toJSONObject().toString( 4 ) );

                        // todo: check result, proof against server?

                        Button btnPayNow = (Button) findViewById( R.id.btnPayNow ) ;
                        btnPayNow.setVisibility( View.INVISIBLE );


                        TextView tvBookingoverview = (TextView) findViewById( R.id.tvBookingoverview );
                        tvBookingoverview.setText(String.format("Booking Confirmation"));

                    } catch (Exception e) {
                        System.out.println( "excption onactivtyresult bookingreview: " + e.toString() );
                    }
                }
            }
        }
    }

    public void setTotal(String total) {
        this.total = total;
        Button btnPayNow = (Button) findViewById( R.id.btnPayNow ) ;
        btnPayNow.setText( String.format("Pay Now %s", total ));
        btnPayNow.setEnabled( true );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_bookingreview );


        Bundle b = getIntent().getExtras();
        long hostId = -1;

        if (b != null) {
            hostId = b.getLong( "hostId" );
            from = b.getString( "from" );
            to = b.getString( "to" );
        }
        host = Data.getInstance().getHost(hostId);

        System.out.println(String.format("BookingreviewActivity. on create, hostId: %s from: %s to: %s", hostId, from, to));

        final TextView tvHostDetails = (TextView) findViewById( R.id.tvHostDetails);
        final TextView tvHostTitle = (TextView) findViewById( R.id.tvHostTitle);
        final ImageView ivHost = (ImageView) findViewById( R.id.ivHost);
        tvHostDetails.setText(String.format(host.getHost()));
        tvHostTitle.setText(String.format(host.getTitle()));
        ivHost.setImageBitmap( host.getBitmap() );

        final TextView tvBookingresponse = (TextView) findViewById( R.id.tvBookingresponse);
        DownloadWebTask downloadWebTask = new DownloadWebTask();
        downloadWebTask.delegate = new TaskDelegate() {
            @Override
            public void taskCompletionResult(String raw) {
                if (raw != null && raw != "") {
                    try {

                        System.out.println("raw: " + raw);

                        JSONObject jsonObject = new JSONObject( raw );
                        Iterator<?> keys = jsonObject.keys();

                        StringBuffer sb = new StringBuffer();

                        while( keys.hasNext() ) {
                            String key = (String)keys.next();
                            System.out.println(String.format("key %s", key));

                            if (key.equals("total")) {
                                String total = jsonObject.getString(key);
                                System.out.println("total amount: " + total);
                                setTotal( total );
                            }
                            if ( jsonObject.get(key) instanceof JSONObject ) {

                                booking_id = key;
                                System.out.println("booking_id: " + booking_id);

                                JSONObject o = jsonObject.getJSONObject(key);
                                String txt = String.format("%s\nBegin: %s End: %s\nPrice: %s excl. VAT: %s\n", o.getString("description"), o.getString("begin"), o.getString("end"), o.getString("price"), o.getString("vat"));
                                sb.append(txt + "\n");
                            }
                        }
                        tvBookingresponse.setText( sb.toString() );
                        System.out.println( "bookingpreparation done." );
                    } catch (Exception e) {
                        System.out.println( "err from json (/bookings/prepare): " + e.toString());
                    }
                }
            }
        };
        downloadWebTask.execute(String.format("https://api.yellowdesks.com/bookings/prepare/%s/%s/%s/true", hostId, from, to));

        Intent intent = new Intent( this, PayPalService.class );
        intent.putExtra( PayPalService.EXTRA_PAYPAL_CONFIGURATION, config );
        startService( intent );

        final Button btnPayNow = (Button) findViewById( R.id.btnPayNow );
        btnPayNow.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println( "btnPayNow" );

                /*
                 * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
                 * Change PAYMENT_INTENT_SALE to
                 *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
                 *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
                 *     later via calls from your server.
                 *
                 * Also, to include additional payment details and an item list, see getStuffToBuy() below.
                 */

                PayPalPayment thingToBuy = new PayPalPayment( new BigDecimal( total ), "EUR", String.format( "Yellow Desk %s - %s Host: %s", from, to, host.getHost()),
                        PayPalPayment.PAYMENT_INTENT_SALE );

                thingToBuy.custom( "[" + booking_id + "]" );
                /*
                 * See getStuffToBuy(..) for examples of some available payment options.
                 */

                System.out.println(String.format("starting paypal payment. total: %s booking_id: %s ", total, booking_id));

                Intent intent = new Intent( BookingreviewActivity.this, PaymentActivity.class );

                // send the same configuration for restart resiliency
                intent.putExtra( PayPalService.EXTRA_PAYPAL_CONFIGURATION, config );
                intent.putExtra( PaymentActivity.EXTRA_PAYMENT, thingToBuy );
                startActivityForResult( intent, REQUEST_CODE_PAYMENT );
            }
        } );
    }
}
