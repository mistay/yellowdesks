package at.langhofer.yellowdesks3;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.security.MessageDigest;

import static android.R.attr.value;


public class LoginOrRegisterActivity extends AppCompatActivity {

    CallbackManager callbackManager;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println ("Starting Yellow Desks ...");

        super.onCreate(savedInstanceState);

        // facebook login button//
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();


        FacebookSdk.setIsDebugEnabled(true);
        System.out.println("user id:" + AppEventsLogger.getUserID());

        // facebook login button//

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "at.langhofer.yellowdesks3",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(signature.toByteArray());
            System.out.println ("KeyHash (for e.g. facebook):" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
        }
        } catch (Exception e) {
            System.out.println ("Exception Facebook:" + e.getMessage());
        }
        System.out.println ("Printed Keyhash");

        setContentView(R.layout.activity_login_or_register);

        final Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginOrRegisterActivity.this, MapActivity.class);
                myIntent.putExtra("key", value); //Optional parameters
                LoginOrRegisterActivity.this.startActivity(myIntent);
            }
        });


        final Button btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("register..");



                LoginManager loginManager = LoginManager.getInstance();




                loginManager.logOut();

                System.out.println("logged out");
/*

                Set<String> permissions =
                        AccessToken.getCurrentAccessToken().getPermissions();

                for (String permission : permissions) {
                    System.out.println("permission:" + permission);
                }
*/
            }
        });

        final LoginButton btnF = (LoginButton) findViewById(R.id.btnLoginFacebook);

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

                                    System.out.println("email: " + LoggedInUser.email );
                                    System.out.println("realname: " + LoggedInUser.realname );
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
