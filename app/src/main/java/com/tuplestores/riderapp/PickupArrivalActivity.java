package com.tuplestores.riderapp;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tuplestores.riderapp.api.ApiClient;
import com.tuplestores.riderapp.api.ApiInterface;
import com.tuplestores.riderapp.model.ApiResponse;
import com.tuplestores.riderapp.model.RideRequest;
import com.tuplestores.riderapp.utils.UserObject;
import com.tuplestores.riderapp.utils.UtilityFunctions;

public class PickupArrivalActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText edt_place_source;
    EditText edt_place_dest;
    ImageView img_rider;
    TextView txtrider_name1;
    TextView txtrider_name2;
    TextView txtrider_name3;
    TextView txtVehType;
    ImageView imgVehType;

    private GoogleMap mMap;
    Handler handler;
    Runnable runnable;

    boolean zoomed = false;
    String rideRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_arrival);
       rideRequest = this.getIntent().getStringExtra("EXTRA_RIDER_REQ_ID");
    }

    private void initialize() {


        edt_place_dest = (EditText) findViewById(R.id.edt_place_dest);
        edt_place_source = (EditText) findViewById(R.id.edt_place_source);
        img_rider = (ImageView) findViewById(R.id.img_rider);

        txtrider_name1 = (TextView) findViewById(R.id.txtrider_name1);
        txtrider_name2 = (TextView) findViewById(R.id.txtrider_name2);
        txtrider_name3 = (TextView) findViewById(R.id.txtrider_name3);

        txtVehType = (TextView) findViewById(R.id.txtVehType);
        txtVehType = (TextView) findViewById(R.id.txtVehType);
        imgVehType = (ImageView) findViewById(R.id.imgVehType);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {

            getRideDetails();

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {


                }
            };

            handler.postDelayed(runnable, 10000);

        }

    }

    private void getRideDetails() {

        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        if(rideRequest!=null && !rideRequest.equals("")){

            UtilityFunctions.getAllSharedPrefValues(getBaseContext());
            Call<RideRequest> call = apiService.getRideRequest(UserObject.tenant_id,rideRequest);
            

            call.enqueue(new Callback<RideRequest>() {
                @Override
                public void onResponse(Call<RideRequest> call, Response<RideRequest> response) {


                    if(response.body()!=null){

                        RideRequest rideRequest = response.body();
                        if(rideRequest!=null){

                            txtVehType.setText(rideRequest.getProduct_name());

                        }
                        else {

                           // ShowAlert(thisActivity,getResources().getString(R.string.something_went_wrong),"");
                        }

                    }
                    else  if(response.body()==null){

                       // ShowAlert(thisActivity,getResources().getString(R.string.something_went_wrong),"");

                    }
                }//OnResponse

                @Override
                public void onFailure(Call<RideRequest> call, Throwable t) {

                    //showHidePgBar(false);
                    //Something went wrong
                   // ShowAlert(getBaseContext(),getResources().getString(R.string.something_went_wrong),"");

                }
            });
        }
    }

    private void setVehicleMarkerOnMap(Location loc) {

        CameraUpdate center;
        if (loc != null && mMap != null) {

            if (UtilityFunctions.vehicleMarker == null) {
                //No marker yet so create
                MarkerOptions mopt = new MarkerOptions();
                mopt.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker2));
                mopt.title("");
                mopt.position(new LatLng(loc.getLatitude(), loc.getLongitude()));
                Marker m = mMap.addMarker(mopt);
                UtilityFunctions.vehicleMarker = m;


                //Set angle for marker

                //Rotate Marker
                updateCameraByZoom(zoomed, loc);
            } else {
                // rotateMakerAsperAngle(loc);
                moveVehicle(loc);

                // UtilityFunctions.vehicleMarker.setPosition(new LatLng(loc.getLatitude(), loc.getLongitude()));
                updateCameraByZoom(zoomed, loc);
            }

        }

    }

    private void moveVehicle(final Location finalPosition) {
        //Move the vehicle smoothly using interpolation
        final Handler handler = new Handler();

        final long start = SystemClock.uptimeMillis();
        final android.view.animation.Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;
        final LatLng startPosition = UtilityFunctions.vehicleMarker.getPosition();
        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + (finalPosition.getLatitude()) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.getLongitude()) * t);
                UtilityFunctions.vehicleMarker.setPosition(currentPosition);
                // myMarker.setRotation(finalPosition.getBearing());


                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                } else {
                    if (hideMarker) {
                        UtilityFunctions.vehicleMarker.setVisible(false);
                    } else {
                        UtilityFunctions.vehicleMarker.setVisible(true);
                    }
                }
            }
        });


    }

    private void updateCameraByZoom(boolean isZoomed,Location loc){

        if(!zoomed){
            //mMap.clear();


          /*  CameraUpdate center =
                    CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(),
                            loc.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);*/

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(loc.getLatitude(),
                            loc.getLongitude()))      // Sets the center of the map to Mountain View
                    .zoom(17)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            zoomed = true;

        }
        else{
            //Already Zoomed
            //Only move camera with out zoom

            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(),
                            loc.getLongitude()));
            mMap.moveCamera(center);
        }



        UtilityFunctions.prevLoc = loc;


    }


}
