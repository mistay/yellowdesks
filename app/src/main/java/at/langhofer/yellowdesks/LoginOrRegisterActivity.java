package at.langhofer.yellowdesks;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;

import java.security.MessageDigest;

import static android.R.attr.value;

public class LoginOrRegisterActivity extends AppCompatActivity {
    private ImageView image1;
    private int[] imageArray;
    private int currentIndex;
    private int startIndex;
    private int endIndex = 0;


    private void alreadyloggedin(String facebookid) {
        // todo: send facebook id to yd servers and auth() on yd servers ...

        // user already logged in in fb ->

        // todo: redirect if login succeeded
        //Intent myIntent = new Intent(LoginOrRegisterActivity.this, MapActivity.class);
        //LoginOrRegisterActivity.this.startActivity(myIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        System.out.println ("Starting Yellow Desks ...");

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Data.version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println (String.format("Yellow Desks v%s", Data.version));

        String prefLogintarget = Data.getInstance().prefLoadString( LoggedInUser.PREFLOGINTARGET );
        String prefUsername = Data.getInstance().prefLoadString( LoggedInUser.PREFUSERNAME );
        String prefPassword = Data.getInstance().prefLoadString( LoggedInUser.PREFPASSWORD );

        System.out.println(String.format("preflogintarget %s prefusername %s prefpass %s",  prefLogintarget, prefUsername, prefPassword));

        final TaskDelegate taskDelegate = new TaskDelegate() {
            @Override
            public void taskCompletionResult(String result) {
                // data is ready
                System.out.println( "data ready" );
                LoginDetails loginDetails = Data.getInstance().loginDetails;

                if (loginDetails != null) {
                    System.out.println( "login successful, redirecting to map" );

                    Intent myIntent = new Intent( LoginOrRegisterActivity.this, MapActivity.class );
                    LoginOrRegisterActivity.this.startActivity( myIntent );
                } else  {
                    System.out.println( "login failed: " + result );
                }

            }
        };

        if (prefLogintarget.equals( LoginDetails.Logintargets.YD.toString() )) {
            System.out.println("try to login at yd api");
            Data.getInstance().login(prefUsername, prefPassword, taskDelegate);
        }

        if (prefLogintarget.equals( LoginDetails.Logintargets.FACEBOOK.toString() )) {
            System.out.println("try to login at facebook");

            Data.getInstance().loginfb(prefPassword, taskDelegate);
        }

        System.out.println("done w/ login");

        // facebook login button//
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "at.langhofer.yellowdesks",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                System.out.println ("KeyHash (for e.g. facebook):" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            System.out.println ("Exception Facebook:" + e.getMessage());
        }
        System.out.println ("Printed Facebook Keyhash");

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        if (Profile.getCurrentProfile() != null) {
            final String id = Profile.getCurrentProfile().getId();
            System.out.println("fb access id: " + id);
            if (id != null) {
                alreadyloggedin(id);
            }
        }
        setContentView(R.layout.activity_login_or_register);

        final Button btnLogin = (Button) findViewById(R.id.btnLogin);

            btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("login button pressed");
                Intent myIntent = new Intent(LoginOrRegisterActivity.this, LoginActivity.class);
                LoginOrRegisterActivity.this.startActivity(myIntent);
            }
        });




        final Button btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginOrRegisterActivity.this, MapActivity.class);
                myIntent.putExtra("key", value); //Optional parameters
                LoginOrRegisterActivity.this.startActivity(myIntent);
            }
        });

        image1 = (ImageView)findViewById(R.id.imageView);
        imageArray = new int[8];
        imageArray[endIndex++] = R.drawable.domore;
        imageArray[endIndex++] = R.drawable.eva;
        imageArray[endIndex++] = R.drawable.event;
        imageArray[endIndex++] = R.drawable.map;
        imageArray[endIndex++] = R.drawable.per;
        imageArray[endIndex++] = R.drawable.team;
        imageArray[endIndex++] = R.drawable.todo;
        imageArray[endIndex++] = R.drawable.gelb;

        startIndex = 0;
        endIndex--;
        nextImage();

        TextView tvBy = (TextView)findViewById(R.id.tvBy);
        tvBy.setText(Data.getByString());
    }

    public void nextImage(){
        image1.setImageResource(imageArray[currentIndex]);
        //Animation rotateimage = AnimationUtils.loadAnimation(this, R.anim.custom_anim);
        //image1.startAnimation(rotateimage);
        currentIndex++;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(currentIndex>endIndex){
                    currentIndex--;
                    previousImage();
                }else{
                    nextImage();
                }

            }
        },2000);
    }
    public void previousImage(){
        image1.setImageResource(imageArray[currentIndex]);
        //Animation rotateimage = AnimationUtils.loadAnimation(this, R.anim.custom_anim);
        //image1.startAnimation(rotateimage);
        currentIndex--;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(currentIndex<startIndex){
                    currentIndex++;
                    nextImage();
                }else{
                    previousImage();
                }
            }
        },2000);
    }
}
