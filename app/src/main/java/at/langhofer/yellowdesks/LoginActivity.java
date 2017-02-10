package at.langhofer.yellowdesks;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

// GEO get location: ConnectionCallbacks, OnConnectionFailedListener
public class LoginActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    CallbackManager callbackManager;
    private Location mLastLocation;
    GoogleApiClient mGoogleApiClient;

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnected(Bundle connectionHint) {
         /*
         if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("not enough permissions to ask for location");

            System.out.println(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION));
            System.out.println(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION));


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);


            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        */

        try {
            System.out.println("trying getting location" );
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                System.out.println("lng" + mLastLocation.getLatitude() + " " + mLastLocation.getLongitude());
            }
            System.out.println("done. null?");
            System.out.println(mLastLocation == null);

        } catch (SecurityException e) { System.out.println("sec exception " + e.toString() );}




    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // facebook login button//


        callbackManager = CallbackManager.Factory.create();

        FacebookSdk.setIsDebugEnabled(true);
        System.out.println("user id:" + AppEventsLogger.getUserID());

        System.out.println("access token: " + AccessToken.getCurrentAccessToken() );

        // facebook login button//

        final LoginButton btnF = (LoginButton) findViewById(R.id.btnLoginFacebook);

        final TextView txtLoginStatus = (TextView) findViewById(R.id.txtLoginStatus);



        btnF.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("fb login: result " + loginResult.toString());

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                System.out.println("LoginActivity" + response.toString());

                                // Application code
                                try {

                                    LoggedInUser.email = object.getString("email");
                                    LoggedInUser.realname =object.getString("name");

                                    System.out.println("email: " + LoggedInUser.email);
                                    System.out.println("realname: " + LoggedInUser.realname);

                                    txtLoginStatus.setText("Logged in user: " + LoggedInUser.email);
                                } catch (Exception e) {
                                    System.out.println("exc: " + e.toString());
                                }
                                System.out.println("done with app code");

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                System.out.println("fb login: onCancel ");
            }

            @Override
            public void onError(FacebookException exception) {
                System.out.println("fb login: exception: " + exception.toString());
            }
        });

        btnF.setReadPermissions("email"); //?!?
        System.out.println("attacced login");


        // retreive GEO location via google play services
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        System.out.println("attacced google api client");

    }


    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
