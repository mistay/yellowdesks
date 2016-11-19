package at.langhofer.yellowdesks3;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static at.langhofer.yellowdesks3.R.id.map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


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

        LatLng sydney = new LatLng(47.82283700000001, 13.04061200000001);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Romy"));

        LatLng tmp = new LatLng(47.7905403, 13.07501769999999);
        mMap.addMarker(new MarkerOptions().position(tmp).title("Mike"));

        tmp = new LatLng(47.8055588, 13.047860000000014);
        mMap.addMarker(new MarkerOptions().position(tmp).title("Thomas"));

        tmp = new LatLng(47.8223683, 13.071454000000017);
        mMap.addMarker(new MarkerOptions().position(tmp).title("Heidi"));

        tmp = new LatLng(47.782805, 13.064100999999937);
        mMap.addMarker(new MarkerOptions().position(tmp).title("Armin"));

        tmp = new LatLng(47.855221, 13.093958799999996);
        mMap.addMarker(new MarkerOptions().position(tmp).title("Christian"));

        tmp = new LatLng(47.806021, 13.050602000000026);
        mMap.addMarker(new MarkerOptions().position(tmp).title("Max"));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(sydney, 12);
        mMap.moveCamera(update);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);



    }



}
