package at.langhofer.yellowdesks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import static at.langhofer.yellowdesks.LoginDetails.username;

// GEO get location: ConnectionCallbacks, OnConnectionFailedListener
public class LoginActivity extends AppCompatActivity {



    CallbackManager callbackManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // facebook login button//


        callbackManager = CallbackManager.Factory.create();

        FacebookSdk.setIsDebugEnabled(true);
        System.out.println("user id:" + AppEventsLogger.getUserID());

        System.out.println("access token: " + AccessToken.getCurrentAccessToken() );

        final TextView txtLoginStatus = (TextView) findViewById(R.id.txtLoginStatus);

        // facebook login button//
        final LoginButton btnF = (LoginButton) findViewById(R.id.btnLoginFacebook);

        // regular (non-facebook) login button
        final Button btnLoginBackend = (Button) findViewById(R.id.btnLoginBackend);
        final EditText txtLoginEmail = (EditText) findViewById(R.id.txtLoginEmail);
        final EditText txtLoginPassword = (EditText) findViewById(R.id.txtLoginPassword);


        // hook up data ready
        final TaskDelegate taskDelegate = new TaskDelegate() {
            @Override
            public void taskCompletionResult(String result) {
                // data is ready
                System.out.println( "data ready" );

                LoginDetails loginDetails = Data.getInstance().loginDetails;

                if (username != "") {
                    System.out.println( "login successful, redirecting to map" );


                    Intent myIntent = new Intent( LoginActivity.this, MapActivity.class );
                    LoginActivity.this.startActivity( myIntent );
                } else  {
                    System.out.println( "login unsuccessful, stay here and display error (TODO)" );

                }

            }
        };

        btnLoginBackend.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
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

                                    txtLoginStatus.setText("Logged in user: " + LoggedInUser.email);

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

        btnF.setReadPermissions("email"); //?!?
        System.out.println("attacced login");



    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
