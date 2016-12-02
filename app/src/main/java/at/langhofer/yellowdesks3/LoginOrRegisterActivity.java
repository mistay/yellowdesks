package at.langhofer.yellowdesks3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookSdk;

import static android.R.attr.value;


public class LoginOrRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println ("Starting Yellow Desks ...");
// facebook login button//
        FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);

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

    }
}
