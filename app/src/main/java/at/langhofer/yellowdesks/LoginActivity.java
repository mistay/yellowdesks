package at.langhofer.yellowdesks;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONObject;

import java.util.Iterator;

// GEO get location: ConnectionCallbacks, OnConnectionFailedListener
public class LoginActivity extends AppCompatActivity {



    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        System.out.println("LoginActivity: onCreate()");

        // facebook login button//
        callbackManager = CallbackManager.Factory.create();

        FacebookSdk.setIsDebugEnabled(true);
        System.out.println("user id:" + AppEventsLogger.getUserID());

        System.out.println("access token: " + AccessToken.getCurrentAccessToken() );

        final TextView txtLoginError = (TextView) findViewById( R.id.txtLoginError );


        // facebook login button//
        final LoginButton btnF = (LoginButton) findViewById(R.id.btnLoginFacebook);

        btnF.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtLoginError.setText("");
            }
        } );


        // regular (non-facebook) login button
        final Button btnLoginBackend = (Button) findViewById(R.id.btnLoginBackend);

        String prefLogintarget = Data.getInstance().prefLoadString( LoggedInUser.PREFLOGINTARGET );
        String prefUsername = Data.getInstance().prefLoadString( LoggedInUser.PREFUSERNAME );
        String prefPassword = Data.getInstance().prefLoadString( LoggedInUser.PREFPASSWORD );

        System.out.println(String.format("preflogintarget %s prefusername %s prefpass %s",  prefLogintarget, prefUsername, prefPassword));

        final EditText txtLoginEmail = (EditText) findViewById(R.id.txtLoginEmail);
        txtLoginEmail.setText( prefUsername );

        final EditText txtLoginPassword = (EditText) findViewById(R.id.txtLoginPassword);
        txtLoginPassword.setText( prefPassword );


        txtLoginPassword.setOnKeyListener( new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                txtLoginError.setText("");
                return false;
            }
        } );



        final TaskDelegate taskDelegate = new TaskDelegate() {
            @Override
            public void taskCompletionResult(String result) {
                // data is ready
                System.out.println( "data ready" );
                LoginDetails loginDetails = Data.getInstance().loginDetails;

                if (loginDetails != null) {
                    txtLoginError.setText("login successful");
                    txtLoginError.setTextColor( Color.GREEN );
                    System.out.println( "login successful, redirecting to map" );
                    Intent myIntent = new Intent( LoginActivity.this, MapActivity.class );
                    LoginActivity.this.startActivity( myIntent );
                } else  {
                    String error = "login failed: " + result;
                    System.out.println( error );
                    txtLoginError.setText(error);
                    txtLoginError.setTextColor( Color.RED );
                }
            }
        };

        btnLoginBackend.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("setOnClickListener");

                txtLoginError.setText( "" );

                Data.getInstance().prefSave( LoggedInUser.PREFLOGINTARGET, "yd" );
                Data.getInstance().prefSave( LoggedInUser.PREFUSERNAME, txtLoginEmail.getText().toString() );
                Data.getInstance().prefSave( LoggedInUser.PREFPASSWORD, txtLoginPassword.getText().toString() );

                System.out.println(String.format("saved pref. username: %s password: %s", txtLoginEmail.getText().toString(), txtLoginPassword.getText().toString()));

                Data d = Data.getInstance();
                d.login(txtLoginEmail.getText().toString(), txtLoginPassword.getText().toString(), taskDelegate);
            }
        });

        btnF.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("fb login: result " + loginResult.toString());
                AccessToken accessToken = loginResult.getAccessToken();
                System.out.println("fb accessToken: " + accessToken.toString());
                //System.out.println("fb accessToken app id: " + accessToken.getApplicationId());
                System.out.println("fb accessToken token: " + accessToken.getToken());

                DownloadWebTask downloadWebTask = new DownloadWebTask();
                downloadWebTask.delegate = new TaskDelegate() {
                    @Override
                    public void taskCompletionResult(String raw) {
                        System.out.println("fb yd backend login result");
                        if (raw != null && raw != "") {
                            try {
                                JSONObject value = new JSONObject(raw);
                                System.out.println("result of loginappfb");
                                System.out.println(value.getBoolean("success"));
                                if (value.getBoolean("success")) {
                                    Intent myIntent = new Intent( LoginActivity.this, MapActivity.class );
                                    LoginActivity.this.startActivity( myIntent );
                                }
                                System.out.println("eof debugging new loginappfb()");
                            } catch (Exception e) {
                                System.out.println("could not parse login json: " + raw + ". exception: " + e.toString());
                            }
                        }

                    }
                };
                downloadWebTask.execute("https://yellowdesks.com/users/loginappfb/" + accessToken.getToken());
                System.out.println("sent fb login request to yd server");



                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                System.out.println("LoginActivity" + response.toString());

                                Iterator<String> i = object.keys();

                                while (i.hasNext()) {
                                    System.out.println("facbook key: " + i.next());
                                }


                                // Application code
                                try {

                                    LoggedInUser.email = object.getString("email");
                                    LoggedInUser.realname =object.getString("name");

                                    System.out.println("email: " + LoggedInUser.email);
                                    System.out.println("realname: " + LoggedInUser.realname);

                                    txtLoginError.setTextColor( Color.YELLOW );
                                    txtLoginError.setText("Facebook Auth successful. E-Mail: " + LoggedInUser.email + ". Now performing YD Backend login ...");

                                    //todo (important!): check in backend if login succeeded
                                    // Data.getInstance().loginViaFacebook(LoggedInUser.email);


                                    // brauchen wir e-mail und realname von facebook hier in der app?


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

        // try to fetch e-mail field from user profile (facebook)
        btnF.setReadPermissions("email");

        System.out.println("attached login");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
