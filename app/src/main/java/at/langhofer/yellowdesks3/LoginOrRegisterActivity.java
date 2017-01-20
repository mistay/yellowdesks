package at.langhofer.yellowdesks3;

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
    private int endIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println ("Starting Yellow Desks ...");
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

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        if (Profile.getCurrentProfile() != null) {
            final String id = Profile.getCurrentProfile().getId();
            System.out.println("fb access id: " + id);
            if (id != null) {
                // user already logged in in fb ->
                Intent myIntent = new Intent(LoginOrRegisterActivity.this, MapActivity.class);
                LoginOrRegisterActivity.this.startActivity(myIntent);
            }
        }

        setContentView(R.layout.activity_login_or_register);

        final Button btnLogin = (Button) findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //
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
        imageArray[0] = R.drawable.start01;
        imageArray[1] = R.drawable.twocoworkers;
        imageArray[2] = R.drawable.alex;

        startIndex = 0;
        endIndex = 2;
        nextImage();



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
