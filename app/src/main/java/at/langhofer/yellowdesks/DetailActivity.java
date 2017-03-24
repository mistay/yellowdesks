package at.langhofer.yellowdesks;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalService;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {

    Host host;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onresume");
        updateLogindependingfields();
    }

    private void updateLogindependingfields() {
        final Button btnNextStep = (Button) findViewById( R.id.btnNextStep);
        LoginDetails loginDetails = Data.getInstance().loginDetails;

        btnNextStep.setText( loginDetails == null ? getResources().getText(R.string.nextStepLogin) : getResources().getText(R.string.nextStepOverview));

        TextView tvLoggedinas = (TextView) findViewById( R.id.tvLoggedinas );

        String prefLogintarget = Data.getInstance().prefLoadString( LoggedInUser.PREFLOGINTARGET );
        String prefUsername = Data.getInstance().prefLoadString( LoggedInUser.PREFUSERNAME );
        tvLoggedinas.setText( loginDetails == null ? getResources().getString(R.string.notloggedin) : getResources().getString(R.string.loggedinas, prefUsername, prefLogintarget));
        tvLoggedinas.setMovementMethod( LinkMovementMethod.getInstance());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println(String.format("%d 5d", requestCode, resultCode));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle b = getIntent().getExtras();








        long hostId = -1;
        if (b != null)
            hostId = b.getLong("hostId");

        System.out.println("DetailActivity. on create, hostId:" + hostId);

        host = Data.getInstance().getHost(hostId);

        if (host == null) {
            System.out.println("DetailActivity::onCreate: host was null wtf? :(");
            return;
        }


        //final LinearLayout llimages = (LinearLayout) findViewById( R.id.llimages );


        final Dialog dialogFullscreenImage = new Dialog(DetailActivity.this,android.R.style.Theme_Translucent);
        dialogFullscreenImage.requestWindowFeature( Window.FEATURE_NO_TITLE);
        dialogFullscreenImage.setCancelable(false);
        dialogFullscreenImage.setContentView(R.layout.preview_image);
        Button btnClose = (Button)dialogFullscreenImage.findViewById(R.id.btnIvClose);
        final ImageView ivPreview = (ImageView)dialogFullscreenImage.findViewById(R.id.iv_preview_image);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialogFullscreenImage.dismiss();
            }
        });

        dialogFullscreenImage.setOnKeyListener( new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                    dialogFullscreenImage.dismiss();
                }
                return true;
            }
        } );


        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);




        HashMap<String, Bitmap> images = host.getImages();

        if (images != null) {
            for (HashMap.Entry<String, Bitmap> entry : images.entrySet()) {
                Bitmap bitmap = entry.getValue();

                //System.out.println("adding imageview. width parent: " + llimages.getWidth());
                //final ImageView ivImage = new ImageView( this );

                final Space space = new Space(this);
                space.setMinimumHeight( 20 );

                //ivImage.setMinimumWidth( llimages.getWidth() );
                //ivImage.setMinimumHeight( 500 );
                //ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
/*
                ivImage.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("showing dialog full screen video");
                        ivPreview.setImageDrawable( ivImage.getDrawable());

                        dialogFullscreenImage.show();
                    }
                } );

*/








                //llimages.addView( ivImage );
                //llimages.addView ( space );

                if (bitmap == null) {
                    // download

                    RotateAnimation anim = new RotateAnimation(0f, 360, 0, 0 );
                    //System.out.println("pivot: " + ivImage.getWidth()/2);
                    anim.setInterpolator(new LinearInterpolator());
                    anim.setRepeatCount( Animation.INFINITE);
                    anim.setDuration(1000);
                    //ivImage.setImageBitmap( BitmapFactory.decodeResource(getResources(), R.drawable.loader ));
                    //ivImage.startAnimation( anim );

                    DownloadWebimageTask downloadWebimageTask = new DownloadWebimageTask();
                    downloadWebimageTask.delegate = new DelegateImageDownloaded() {
                        @Override
                        public void imageDownloaded(Bitmap result, Object tag) {
                        System.out.println("taskCompletionResult tag: " + tag.toString());

                        HashMap.Entry<String, Bitmap> entry = (HashMap.Entry<String, Bitmap> ) tag;

                        entry.setValue( result );

                            //ivImage.setAnimation(null);
                            //ivImage.setImageBitmap( entry.getValue() );

                        host.setBitmapForImage( entry.getKey(), result );

                            try {
                                mPagerAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                System.out.println("notifyDataSetChanged exc: " + e.toString());

                            }
                           // mPager.invalidate();
                            System.out.println("taskCompletionResult: " + result);
                        }
                    };
                    System.out.println("sending download request: " + entry.getKey());

                    downloadWebimageTask.setTag( entry );
                    downloadWebimageTask.execute(entry.getKey());
                } else {
                    //ivImage.setAnimation(null);
                    //System.out.println("setting image from already downloaded cache");
                    //ivImage.setImageBitmap( bitmap );
                }
            }
        } else {
            System.out.println("images were null :(");
        }








        //final TextView textViewDeskstatus2 = (TextView) findViewById(R.id.deskstatus2);
        //textViewDeskstatus2.setText("YELLOW desks: " + host.gettotalDesks() + "/" + host.getAvailableDesks());

        final TextView textviewDetail = (TextView) findViewById(R.id.textviewDetail);
        textviewDetail.setText("Included: " + host.getDetails());

        //View root = textviewDetail.getRootView();
        //getWindow().getDecorView().setBackgroundColor(Data.colorYellowdesks());
        //root.setBackgroundColor(  Data.colorYellowdesks()  );

        final TextView tvExtras = (TextView) findViewById(R.id.tvExtras);
        tvExtras.setText("Extras: " +  (host.getExtras() == "" ? "none" : host.getExtras()));


        final TextView tvOpeninghours = (TextView) findViewById(R.id.tvOpeninghours);
        if (host.getOpenFrom() == null)
            tvOpeninghours.setText(String.format("Opening Hours: n/a"));
        else
            tvOpeninghours.setText(String.format("Opening Hours: Mon %s till Fri %s%s", host.getOpenFrom(), host.getOpenTill(), host.getOpen247fixworkers() ? "\nMember Access 24/7" : "" ));

        StringBuilder tmp= new StringBuilder();
        if (host.getPrice1Day() != null)
            tmp.append ( String.format("1 day: %s EUR\n" , host.getPrice1Day()));
        if (host.getPrice1Day() != null)
            tmp.append ( String.format("10 days: %s EUR\n" , host.getPrice10Days()));
        if (host.getPrice1Day() != null)
            tmp.append ( String.format("1 month: %s EUR\n" , host.getPrice1Month()));
        if (host.getPrice1Day() != null)
            tmp.append ( String.format("6 months: %s EUR\n" , host.getPrice6Months()));
        tmp.append ( String.format("prices excluding VAT\n") );


        System.out.println("host getPrice1Day(): " + host.getPrice1Day());
        final TextView tvPrices = (TextView) findViewById(R.id.tvPrices);
        tvPrices.setText(String.format("Prices: %s", ((tmp.length() == 0) ? "n/a" : "\n" + tmp.toString())));

        final TextView hostDetails = (TextView) findViewById(R.id.txt_hostdetails);
        hostDetails.setText( String.format("%s\n%s desks available", host.getTitle(), host.getAvailableDesks()) );


        //final ImageView detailImage = (ImageView) findViewById(R.id.detailimage);

//        Drawable drawable = getResources().getDrawable(R.drawable.alex);

        if (host.getBitmap()!=null) {
            //detailImage.setImageDrawable(new BitmapDrawable(host.getBitmap()));
        } else {
            DelegateImageDownloaded downloadFinished = new DelegateImageDownloaded() {
                @Override
                public void imageDownloaded(Bitmap result, Object tag) {
                    if (result != null) {
                        System.out.println("imageDownloaded, result: " + result.toString());

                        Drawable myDrawable = new BitmapDrawable(result);
                        //detailImage.setImageDrawable(myDrawable);
                    } else {
                        System.out.println("DelegateImageDownloaded downloadFinished but result was null :(");
                    }
                }
            };

            System.out.println("downloadImage. id: " + host.getId() + " url: " +  host.getImageURL() + " isnull? " + ( host.getImageURL() == null));
            if (host.getImageURL() != null)
                Data.getInstance().downloadImage(host, downloadFinished);
        }



        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        System.out.println(String.format("day: %d month: %d year: %d", day, month, year));


        final DatePicker dpFrom = (DatePicker) findViewById(R.id.dpFrom);
        dpFrom.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                changedate();
            }
        });

        final DatePicker dpTo = (DatePicker) findViewById(R.id.dpTo);
        dpTo.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                changedate();
            }
        });


        updateLogindependingfields();

        TextView tvLoggedinas = (TextView) findViewById( R.id.tvLoggedinas );
        tvLoggedinas.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Data.getInstance().logout();
                updateLogindependingfields();
            }
        } );

        final Button btnNextStep = (Button) findViewById( R.id.btnNextStep);
        btnNextStep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("btnNextStep button clicked");

                LoginDetails loginDetails = Data.getInstance().loginDetails;
                if (loginDetails == null) {
                    Intent myIntent = new Intent( DetailActivity.this, LoginActivity.class );
                    startActivity(myIntent);
                } else {
                    Intent myIntent = new Intent( DetailActivity.this, BookingreviewActivity.class );
                    myIntent.putExtra("hostId", host.getId());
                    myIntent.putExtra("from", dpFrom.getYear() + "-" + (dpFrom.getMonth()+1) + "-" + dpFrom.getDayOfMonth());
                    myIntent.putExtra("to", dpTo.getYear() + "-" + (dpTo.getMonth()+1) + "-" + dpTo.getDayOfMonth());
                    startActivity(myIntent);
                }
            }
        });

        changedate();
    }

    Double price = null;
    String begin="";
    String end="";
    private void changedate() {
        System.out.println("changedate()");
        final DatePicker dpTo = (DatePicker) findViewById(R.id.dpTo);
        final DatePicker dpFrom = (DatePicker) findViewById(R.id.dpFrom);

        final TextView tvPricecalc = (TextView) findViewById( R.id.tvPricecalc );

        DownloadWebTask downloadWebTask = new DownloadWebTask();
        downloadWebTask.delegate = new TaskDelegate() {
            @Override
            public void taskCompletionResult(String raw) {
                if (raw != null && raw != "") {
                    try {
                        JSONObject value = new JSONObject(raw);
                        Integer num_workingdays = value.has( "num_workingdays" ) ? value.getInt( "num_workingdays" ) : null;
                        Integer num_months = value.has( "num_months" ) ? value.getInt( "num_months" ) : null;
                        Integer num_days = value.has( "num_days" ) ? value.getInt( "num_days" ) : null;
                        price = value.getDouble( "price" );

                        StringBuilder text = new StringBuilder( );
                        if (value.has( "num_workingdays" )) {
                            // tagesabrechnung
                            text.append( String.format("%s Workingdays\n", value.getInt( "num_workingdays" )));
                        } else {
                            // monatsabrechnung
                            if (value.has( "num_months" ))
                                text.append( String.format("%s Months ", value.getInt( "num_months" )));
                            if (value.has( "num_days" ))
                                text.append( String.format("%s Days\n", value.getInt( "num_days" )));
                        }
                        if (price != null)
                            text.append( String.format("Price: %s", NumberFormat.getCurrencyInstance().format(price)));

                        tvPricecalc.setText(text.toString());
                        System.out.println("eof debugging new loginappfb()");
                    } catch (Exception e) {
                        System.out.println("could not parse login json: " + raw + ". exception: " + e.toString());
                    }
                }

            }
        };

        String url = "https://api.yellowdesks.com/bookings/prepare/" + host.getId() + "/" + dpFrom.getYear() + "-" + (dpFrom.getMonth()+1) + "-" + dpFrom.getDayOfMonth() + "/" + dpTo.getYear() + "-" + (dpTo.getMonth()+1) + "-" + dpTo.getDayOfMonth();
        tvPricecalc.setText("Loading...");
        downloadWebTask.execute(url);
        System.out.println("sent url: " + url);


        //calendar.set(dpFrom.getYear(), dpFrom.getMonth(),dpFrom.getDayOfMonth());




    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ScreenSlidePageFragment f = new ScreenSlidePageFragment();

            Object[] o = host.getImages().values().toArray();

            if (host.getVideoURL() == null) {
                Bitmap b = (Bitmap) o[position];
                f.setBitmap( b );
            } else {
                if (position == 0)
                    f.setVideoURL( host.getVideoURL() );
                else {
                    Bitmap b = (Bitmap) o[position - 1];
                    f.setBitmap( b );
                }
            }
            return f;
        }

        @Override
        public int getCount() {
            return host.getImages().size() + (host.getVideoURL() == null ? 0 : 1);
        }

        // http://stackoverflow.com/questions/7263291/viewpager-pageradapter-not-updating-the-view
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

}
