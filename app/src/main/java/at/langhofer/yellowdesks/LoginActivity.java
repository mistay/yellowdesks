package at.langhofer.yellowdesks;

import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONObject;

import at.langhofer.yellowdesks.R;


public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;

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
    }
}
