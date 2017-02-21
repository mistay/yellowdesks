package at.langhofer.yellowdesks;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import static android.R.attr.value;
import static at.langhofer.yellowdesks.R.id.map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener {

    private GoogleMap mMap;
    CircleOptions circleOptions;
    Circle circle;

    Marker youMarker;

    LatLng currentlocation = new LatLng( 47.806021, 13.050602000000026 ); //salzburg

    int circleRadius = 1000;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;



    private Location mLastLocation;
    GoogleApiClient mGoogleApiClient;







    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnected(Bundle connectionHint) {

         if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("not enough permissions to ask for location");

            System.out.println(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION));
            System.out.println(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION));

            ActivityCompat.requestPermissions(this,
                    new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1
                    );


            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
             System.out.println("giving up requesting permissions for gps" );
             return;
        }


        try {
            System.out.println("trying getting location" );
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                System.out.println("lng" + mLastLocation.getLatitude() + " " + mLastLocation.getLongitude());

                currentlocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                resizeCircle();

                youMarker.remove();
                youMarker = mMap.addMarker(new MarkerOptions().position(currentlocation).title("You are here!"));
                youMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.you));

                centerAndZoomCamera();
            } else {
            }
            System.out.println("done reading location. mLastLocation null?");
            System.out.println(mLastLocation == null);

        } catch (SecurityException e) { System.out.println("sec exception " + e.toString() );}
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Data data = Data.getInstance();

        final TextView tvStatusLoading = (TextView) findViewById( R.id.tvStatusLoading );


        // hook up data ready
        TaskDelegate taskDelegate = new TaskDelegate() {
            @Override
            public void taskCompletionResult(String result) {
                // data is ready
                System.out.println("data ready");

                tvStatusLoading.setText( "" );




                List<Host> hosts = Data.getInstance().getData();
                for (Host host : hosts) {
                    LatLng latlng = new LatLng( host.getLat(), host.getLng() );
                    System.out.println("new latlng: " + latlng.toString());
                    Marker myMarker = mMap.addMarker( new MarkerOptions().position( latlng ).title( host.getHost() ) );

                    // attaching host to marker to retreive in OnMarkerClickListener
                    myMarker.setTag(host);
                    myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.yellowdot));

                    GoogleMap.OnMarkerClickListener onMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Intent myIntent = new Intent( MapActivity.this, DetailActivity.class );

                            // retreive host from marker
                            Host host = (Host) marker.getTag();
                            System.out.println("opening detail activity with key: " + host.getId());
                            myIntent.putExtra( "hostId", host.getId() );

                            MapActivity.this.startActivity( myIntent );
                            return false;
                        }
                    };
                    mMap.setOnMarkerClickListener(onMarkerClickListener);
                }
            }
        };

        // start download
        System.out.println("starting download...");
        tvStatusLoading.setText( "Loading..." );


        data.downloadHosts(taskDelegate);

        // disable zoom controls to make space for "open now" and "list" buttons
        mMap.getUiSettings().setZoomControlsEnabled( false );

        mMap.getUiSettings().setAllGesturesEnabled( true );
        mMap.getUiSettings().setZoomGesturesEnabled( true );


        youMarker = mMap.addMarker(new MarkerOptions().position(currentlocation).title("You are here!"));
        youMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.you));

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom( currentlocation, 12 );
        mMap.moveCamera( update );
    }

    Spinner spinner2;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_map );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( map );
        mapFragment.getMapAsync( this );


        final ImageButton btnLogin = (ImageButton) findViewById( R.id.btnList );

        btnLogin.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent( MapActivity.this, ResultsActivity.class );
                myIntent.putExtra( "key", value ); //Optional parameters
                MapActivity.this.startActivity( myIntent );
            }
        } );

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder( this ).addApi( AppIndex.API ).build();













        // retreive GEO location via google play services
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        System.out.println("attached google api client");



        TextView tvByMap = (TextView)findViewById(R.id.tvByMap);
        tvByMap.setText(Data.getByString());








        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.minutes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("selected " + i);
                System.out.println("selected " + spinner.getSelectedItem().toString());
                resizeCircle();
                centerAndZoomCamera();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.vehicle_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("selected " + i);
                System.out.println("selected " + spinner.getSelectedItem().toString());
                resizeCircle();

                centerAndZoomCamera();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void centerAndZoomCamera() {
        int zoomfactor = 17;

        // good values for nexus5
        if (circleRadius > 300)
            zoomfactor = 15;

        if (circleRadius > 900)
            zoomfactor = 13;

        if (circleRadius > 3900)
            zoomfactor = 11;

        if (circleRadius > 10000)
            zoomfactor = 9;

        System.out.println("centerAndZoomCamera: " + circleRadius + " zoomfactor google: " + zoomfactor + " spinner.getselecteditem(): " + spinner.getSelectedItem().toString());

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom( currentlocation, zoomfactor );
        if (mMap != null)
            mMap.moveCamera( update );
    }
    public void resizeCircle() {
        System.out.println("resize: " + spinner.getSelectedItem().toString());
        System.out.println("resize: " + spinner2.getSelectedItem().toString());

        Integer minutes = Integer.parseInt(spinner.getSelectedItem().toString());

        double km_per_hour = spinner2.getSelectedItem().toString().equals("walk") ? 4 : 30;


        // gehen 4km/h lt wikpedia
        // fahren ca 30km/h lt https://de.statista.com/statistik/daten/studie/37200/umfrage/durchschnittsgeschwindigkeit-in-den-15-groessten-staedten-der-welt-2009/
        circleRadius = (int)( minutes * km_per_hour * 1000.0 / 60.0);

        try {
            if (circle != null)
                circle.remove();
        } catch (Exception e) {
            System.out.println("resize circle remove exception: " + e.toString());
        }

        circleOptions = new CircleOptions();

        circleOptions.center(currentlocation);
        circleOptions.radius(circleRadius);

        circleOptions.fillColor(Data.colorYellowdesks()); // yellow
        circleOptions.strokeColor(Data.colorYellowdesks()); // yellow
        circleOptions.strokeWidth(2);

        if (mMap != null)
            circle = mMap.addCircle(circleOptions);




    }



    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName( "Map Page" ) // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl( Uri.parse( "http://[ENTER-YOUR-URL-HERE]" ) )
                .build();
        return new Action.Builder( Action.TYPE_VIEW )
                .setObject( object )
                .setActionStatus( Action.STATUS_TYPE_COMPLETED )
                .build();
    }


    @Override
    public void onStart() {

        mGoogleApiClient.connect();
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start( client, getIndexApiAction() );
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end( client, getIndexApiAction() );
        client.disconnect();
    }
}
