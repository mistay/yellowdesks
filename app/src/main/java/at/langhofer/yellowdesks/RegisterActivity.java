package at.langhofer.yellowdesks;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );

        Button btnRegisternow = (Button) findViewById( R.id.btnRegisternow );
        btnRegisternow.setOnClickListener( new View.OnClickListener() {

        final TextView tvRegisterErrorMessage = (TextView) findViewById( R.id.tvRegisterErrorMessage );

        final TaskDelegate taskDelegate = new TaskDelegate() {
            @Override
            public void taskCompletionResult(String result) {
                // data is ready
                System.out.println( "data ready" );
                LoginDetails loginDetails = Data.getInstance().loginDetails;

                if (loginDetails != null) {
                    tvRegisterErrorMessage.setText("login successful");
                    tvRegisterErrorMessage.setTextColor( Color.GREEN );
                    System.out.println( "login successful, redirecting to detail activity" );

                    Intent myIntent = new Intent( RegisterActivity.this, DetailActivity.class );
                    RegisterActivity.this.startActivity( myIntent );

                } else  {
                    String error = "login failed: " + result;
                    System.out.println( error );
                    tvRegisterErrorMessage.setText(error);
                    tvRegisterErrorMessage.setTextColor( Color.RED );
                }
            }
        };

            @Override
            public void onClick(View view) {
                System.out.println("register now clicked");

                DownloadWebTask downloadWebTask = new DownloadWebTask();
                downloadWebTask.delegate = new TaskDelegate() {
                    @Override
                    public void taskCompletionResult(String raw) {
                    if (raw != null && raw != "") {
                        try {
                            System.out.println("raw: " + raw);

                            JSONObject jsonObject = new JSONObject( raw );

                            if (jsonObject.getString("error").equals("")) {
                                // registatrion successful. store data in preferences and try to login

                                tvRegisterErrorMessage.setTextColor( Color.RED );
                                // todo: wirklich error msg vom server zum user durchschleifen?
                                tvRegisterErrorMessage.setText( "successfully registered, yay!" );

                                JSONObject coworker = jsonObject.getJSONObject( "coworker" );

                                Data.getInstance().prefSave( LoggedInUser.PREFLOGINTARGET, LoginDetails.Logintargets.YD.toString() );
                                Data.getInstance().prefSave( LoggedInUser.PREFUSERNAME, coworker.getString("username"));
                                Data.getInstance().prefSave( LoggedInUser.PREFPASSWORD, ((EditText) findViewById( R.id.password )).getText().toString() );

                                Data.getInstance().login( coworker.getString("username"), ((EditText) findViewById( R.id.password )).getText().toString(), taskDelegate);

                            } else {
                                // registration unsuccessful, provide info about error


                                tvRegisterErrorMessage.setTextColor( Color.RED );
                                // todo: wirklich error msg vom server zum user durchschleifen?
                                tvRegisterErrorMessage.setText( jsonObject.getString("error") );
                            }
                            System.out.println( "coworkers/register done." );
                        } catch (Exception e) {
                            System.out.println( "err from json (/coworkers/register): " + e.toString());
                        }
                    }
                    }
                };

                JSONObject jObj = new JSONObject();
                try {
                    jObj.put( "companyname", ((EditText) findViewById( R.id.companyname )).getText().toString() );
                    jObj.put( "firstname", ((EditText) findViewById( R.id.firstname )).getText().toString() );
                    jObj.put( "lastname", ((EditText) findViewById( R.id.lastname )).getText().toString() );
                    jObj.put( "username", ((EditText) findViewById( R.id.username )).getText().toString() );
                    jObj.put( "password", ((EditText) findViewById( R.id.password )).getText().toString() );
                    jObj.put( "address", ((EditText) findViewById( R.id.address )).getText().toString() );
                    jObj.put( "postal_code", ((EditText) findViewById( R.id.postal_code )).getText().toString() );
                    jObj.put( "city", ((EditText) findViewById( R.id.city )).getText().toString() );
                    jObj.put( "vatid", ((EditText) findViewById( R.id.vatid )).getText().toString() );
                    jObj.put( "email", ((EditText) findViewById( R.id.email )).getText().toString() );
                } catch(Exception e) {
                    System.out.println("exception while reading fields: " + e.toString());
                }

                String data = jObj.toString();

                String url = "https://yellowdesks.com/coworkers/register";
                downloadWebTask.post_data = "data=" + data;
                downloadWebTask.execute( url );
            }
        } );
    }
}
