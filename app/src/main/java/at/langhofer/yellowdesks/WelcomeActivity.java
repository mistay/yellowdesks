package at.langhofer.yellowdesks;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class WelcomeActivity extends AppCompatActivity {
    private ImageView image1;
    private int[] imageArray;
    private int currentIndex;
    private int startIndex;
    private int endIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_welcome );

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

                    Intent myIntent = new Intent( WelcomeActivity.this, MapActivity.class );
                    WelcomeActivity.this.startActivity( myIntent );
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

        final Button btnLetsstart = (Button) findViewById( R.id.btnLetsstart);

        btnLetsstart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("btnLetsstart button pressed");
                Intent myIntent = new Intent(WelcomeActivity.this, MapActivity.class);
                WelcomeActivity.this.startActivity(myIntent);
            }
        });


        System.out.println("android device serial: " + Build.SERIAL);

        if (Developerdevices.isDeveloperDevice()) {
            Button testArmin = new Button( getApplicationContext() );
            testArmin.setText("test (dev phone)");
            LinearLayout ll = (LinearLayout) btnLetsstart.getParent();
            ll.addView( testArmin );
            testArmin.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println( "testbutton button pressed. model: " + android.os.Build.MODEL );
                    Intent myIntent = new Intent( WelcomeActivity.this, RegisterActivity.class );
                    WelcomeActivity.this.startActivity( myIntent );
                }
            } );
        }

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
    }

    public void nextImage(){
        image1.setImageResource(imageArray[currentIndex]);
        //Animation rotateimage = AnimationUtils.loadAnimation(this, R.anim.custom_anim);
        //image1.startAnimation(rotateimage);
        currentIndex++;
        new Handler().postDelayed( new Runnable() {
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
