package com.tuplestores.riderapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.tuplestores.riderapp.api.ApiClient;
import com.tuplestores.riderapp.api.ApiInterface;
import com.tuplestores.riderapp.model.ApiResponse;
import com.tuplestores.riderapp.model.BottomRiderServButton;
import com.tuplestores.riderapp.model.RiderServ;
import com.tuplestores.riderapp.model.Vehicle;
import com.tuplestores.riderapp.services.LocationFGService;
import com.tuplestores.riderapp.utils.Constants;
import com.tuplestores.riderapp.utils.DirectionsJSONParser;
import com.tuplestores.riderapp.utils.UtilityFunctions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;


/*
   Work Flow:

   Activity will start a foreground service on startup to fetch location and current location is
   plotted on map and zoom to that point

   User can change the pushpin to change the location by dragging the pushpin

   User can type in the location search box to chanage the pickup location.

   When user location is detected the maker is  put on map

   Vehicles will be listed based on the location update which is received by the foreground service..
   please refer BroadcastReceiver for location udpate

   Marker function -  setRiderPickUpPinMarkeronMap(loc);->which will calll getNearestVehicles-->Which will call
   riderSericesInit-->which willl call makebottoButtonPanel-->which will start a thread to call getNearestVehiclesThreadProc to
   get vehicle locations periodically




 */
public class RiderHome extends AppCompatActivity implements OnMapReadyCallback {

    int AUTOCOMPLETE_REQUEST_CODE = 1;
    EditText edt_place_source;
    EditText edt_place_dest;

    LinearLayout btnPanelBottom;
    String source="";

    boolean permissionGranted = false;
    private static final String TAG ="FUSED_HELPER";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    BroadcastReceiver receiver;
    boolean zoomed=false;
    boolean inq =false;

    private GoogleMap mMap;

    List<Vehicle> lstVehiclesMain;
    List<Vehicle> lstVehiclesByService;
    List<RiderServ> lstRiderService;
    BottomRiderServButton[] bottomBtnArray;

    List<BottomRiderServButton> lstBottomButtons;

    LinearLayout btnVeh1,btnVeh2,btnVeh3,btnVeh4,btnVeh5,btnVeh6;
    TextView txtreachtime1,txtreachtime2,txtreachtime3,txtreachtime4,txtreachtime5,txtreachtime6;
    ImageButton imgveh1,imgveh2,imgveh3,imgveh4,imgveh5,imgveh6;
    TextView txtvtype1,txtvtype2,txtvtype3,txtvtype4,txtvtype5,txtvtype6;
    ProgressBar pgBar;
    Button btnRideNow;
    List<Marker>vMarkerList;
    Handler handler ;
    Runnable runnableCode;
    LatLng mPosition;
    LatLng mPostion2;
    double mZoom;
    String DIST,ETA;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_home);


        initialize();
    }

    private void initialize(){

        zoomed = false;
        lstVehiclesByService = new ArrayList<Vehicle>();
        vMarkerList = new ArrayList<Marker>();
        handler = new Handler();

        lstBottomButtons = new ArrayList<BottomRiderServButton>();

        Places.initialize(getApplicationContext(),getResources().getString(R.string.google_maps_key));

        pgBar = (ProgressBar)findViewById(R.id.pgBar) ;

        edt_place_source = (EditText)findViewById(R.id.edt_place_source);

        edt_place_dest = (EditText)findViewById(R.id.edt_place_dest);

        btnPanelBottom = (LinearLayout)findViewById(R.id.btnPanelBottom) ;
        btnRideNow =(Button)findViewById(R.id.btnRideNow) ;

        btnVeh1 = (LinearLayout)findViewById(R.id.btnVeh1) ;
        txtreachtime1  = (TextView)findViewById(R.id.txtreachtime1);
        imgveh1 = (ImageButton) findViewById(R.id.imgveh1);
        txtvtype1 = (TextView) findViewById(R.id.txtvtype1);

        btnVeh2 = (LinearLayout)findViewById(R.id.btnVeh2) ;
        txtreachtime2  = (TextView)findViewById(R.id.txtreachtime2);
        imgveh2 = (ImageButton) findViewById(R.id.imgveh2);
        txtvtype2 = (TextView) findViewById(R.id.txtvtype2);

        btnVeh3 = (LinearLayout)findViewById(R.id.btnVeh3) ;
        txtreachtime3  = (TextView)findViewById(R.id.txtreachtime3);
        imgveh3 = (ImageButton) findViewById(R.id.imgveh3);
        txtvtype3 = (TextView) findViewById(R.id.txtvtype3);


        btnVeh4 = (LinearLayout)findViewById(R.id.btnVeh4) ;
        txtreachtime4  = (TextView)findViewById(R.id.txtreachtime4);
        imgveh4= (ImageButton) findViewById(R.id.imgveh4);
        txtvtype4 = (TextView) findViewById(R.id.txtvtype4);


        btnVeh5 = (LinearLayout)findViewById(R.id.btnVeh5) ;
        txtreachtime5  = (TextView)findViewById(R.id.txtreachtime5);
        imgveh5 = (ImageButton) findViewById(R.id.imgveh5);
        txtvtype5 = (TextView) findViewById(R.id.txtvtype5);

        btnVeh6 = (LinearLayout)findViewById(R.id.btnVeh6) ;
        txtreachtime6  = (TextView)findViewById(R.id.txtreachtime6);
        imgveh6 = (ImageButton) findViewById(R.id.imgveh6);
        txtvtype6 = (TextView) findViewById(R.id.txtvtype6);

        edt_place_dest.setClickable(true);
        edt_place_source.setClickable(true);

        edt_place_source.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        edt_place_source.setSingleLine(true);
        edt_place_source.setMarqueeRepeatLimit(-1);
        edt_place_source.setSelected(true);

        edt_place_dest.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        edt_place_dest.setSingleLine(true);
        edt_place_dest.setMarqueeRepeatLimit(-1);
        edt_place_dest.setSelected(true);



        edt_place_source.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                source = "S";
                startPlaceAutoSelection();
                return  true;
            }
        });

        edt_place_dest.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                source = "D";
                startPlaceAutoSelection();
                return  true;
            }
        });
        btnRideNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RideNow
            }
        });

        UtilityFunctions.getAllSharedPrefValues(this);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
          .findFragmentById(R.id.map);
         mapFragment.getMapAsync(this);


        // Check if the user revoked runtime permissions.
        if (!checkPermissions()) {
            requestPermissions();
        }

        if(permissionGranted){

            startLocationService();

        }


        ///LBS Receiver

        //Borad cast reciever fired when the FG service gets a location

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               /* if(intent.getAction().equals(Constants.ACTION_TAXI_DISPATCH_FIREBASE_TRIP_R)){

                    String s = intent.getStringExtra(Constants.EXTRA_TAXI_DISPATCH_FIREBASE_TRIP_M);
                    if(s!=null && !s.equals("")){
                        //Show Ride request popup

                        //UtilityFunctions.ride_req_id=s;

                        startCountDownPopUp();
                    }

                }*/
                 if(intent.getAction().equals(Constants.ACTION_TAXI_DISPATCH_LBS_R)){

                    Location loc = intent.getExtras().getParcelable(Constants.EXTRA_TAXI_DISPATCH_LBS_MSG_R);
                    //Zoom to that location in map
                    if (loc != null) {

                       setRiderPickUpPinMarkeronMap(loc);
                    }

                }

            }
        };//Receiver

        btnPanelBottom.setVisibility(View.GONE);
        bottomButtonInit();




    }//initialize

    private void bottomButtonInit(){

        btnVeh1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityFunctions.productId = btnVeh1.getTag().toString();
                if(btnVeh1.getTag().equals("NC")){

                    btnRideNow.setEnabled(false);
                }
                else{

                    btnRideNow.setEnabled(true);

                    getVehiclesByRiderService(v.getTag().toString());
                    putVehicleMarkersOnMap();
                }



            }
        });

        btnVeh2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityFunctions.productId = btnVeh2.getTag().toString();
                if(btnVeh2.getTag().equals("NC")){

                    btnRideNow.setEnabled(false);
                }
                else{

                    btnRideNow.setEnabled(true);

                    getVehiclesByRiderService(v.getTag().toString());
                    putVehicleMarkersOnMap();
                }
            }
        });

        btnVeh3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityFunctions.productId = btnVeh4.getTag().toString();
                if(btnVeh3.getTag().equals("NC")){

                    btnRideNow.setEnabled(false);
                }
                else{

                    btnRideNow.setEnabled(true);

                    getVehiclesByRiderService(v.getTag().toString());
                    putVehicleMarkersOnMap();
                }
            }
        });
        btnVeh4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityFunctions.productId = btnVeh4.getTag().toString();
                if(btnVeh4.getTag().equals("NC")){

                    btnRideNow.setEnabled(false);
                }
                else{

                    btnRideNow.setEnabled(true);

                    getVehiclesByRiderService(v.getTag().toString());

                    putVehicleMarkersOnMap();
                }
            }
        });
        btnVeh5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityFunctions.productId = btnVeh5.getTag().toString();
                if(btnVeh5.getTag().equals("NC")){

                    btnRideNow.setEnabled(false);
                }
                else{

                    btnRideNow.setEnabled(true);

                    getVehiclesByRiderService(v.getTag().toString());
                    putVehicleMarkersOnMap();
                }
            }
        });
        btnVeh6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UtilityFunctions.productId = btnVeh6.getTag().toString();

                if(btnVeh5.getTag().equals("NC")){

                    btnRideNow.setEnabled(false);
                }
                else{

                    btnRideNow.setEnabled(true);

                    getVehiclesByRiderService(v.getTag().toString());
                    putVehicleMarkersOnMap();
                }
            }
        });
    }

    private  void startPlaceAutoSelection(){

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                //Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                if(source.equals("S")){
                    if (!place.getAddress().toString().contains(place.getName())) {
                        edt_place_source.setText(place.getName() + ", " + place.getAddress());
                    } else {
                        edt_place_source.setText(place.getAddress());
                    }
                    mPosition = place.getLatLng();
                    //Zoom to the palce
                    setRiderMarkerOnMap(place.getLatLng());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16);
                    mMap.animateCamera(cameraUpdate);
                }else if(source.equals("D")){

                    if (!place.getAddress().toString().contains(place.getName())) {
                        edt_place_dest.setText(place.getName() + ", " + place.getAddress());
                    } else {
                        edt_place_dest.setText(place.getAddress());
                        mPostion2 = place.getLatLng();
                    }
                }



            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
               // Status status = Autocomplete.getStatusFromIntent(data);
               // Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    ////Map Related and LBS related functionalities

    private void setRiderMarkerOnMap(LatLng latLng) {

        if (UtilityFunctions.riderMarker != null) {

            UtilityFunctions.riderMarker.setPosition(latLng);

        }
        else {
             MarkerOptions mopt = new MarkerOptions();
             mopt.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_man_pin));
             mopt.title("");
             mopt.position(latLng);
             Marker m = mMap.addMarker(mopt);
             UtilityFunctions.riderMarker = m;
      }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        initCameraIdle();

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Cleaning all the markers.

                mPosition = mMap.getCameraPosition().target;
                mZoom = mMap.getCameraPosition().zoom;

                //Do reverse geocode to get the place name in pick up point text box

            }
        });
    }


    private void initCameraIdle() {
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mPosition = mMap.getCameraPosition().target;
                getAddressFromLocation(mPosition.latitude, mPosition.longitude);
            }
        });
    }

    private void getAddressFromLocation(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);


        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();
                for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append(" ");
                }

                 edt_place_dest.setText(strAddress.toString());

            } else {
                edt_place_dest.setText("Searching Current Address");
            }

        } catch (IOException e) {
            e.printStackTrace();
           // printToast("Could not get address..!");
        }
    }

    /*
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int fineLocationPermissionState = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);

        int coarseLocationPermissionState = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION);

        //  int backgroundLocationPermissionState = ActivityCompat.checkSelfPermission(
        //  ctx, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        if (fineLocationPermissionState == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermissionState == PackageManager.PERMISSION_GRANTED){
            permissionGranted = true;
        }
        else{
            permissionGranted = false;
        }
        return permissionGranted;
    }


    private void requestPermissions() {

        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        boolean backgroundLocationPermissionApproved =
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        boolean shouldProvideRationale =
                permissionAccessFineLocationApproved && backgroundLocationPermissionApproved;

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.frame_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(RiderHome.this,
                                    new String[] {
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION },
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(RiderHome.this,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    private  void startLocationService(){

        Intent ii = new Intent(this,LocationFGService.class);
        ContextCompat.startForegroundService(this,ii);

    }

    private  void stopLocationService(){

        Intent ii = new Intent(this,LocationFGService.class);
        stopService(ii);
    }


    private void setRiderPickUpPinMarkeronMap(Location loc){

        CameraUpdate center;
        if(loc!=null && mMap!=null){

            if(UtilityFunctions.riderMarker == null){
                //No marker yet so create
                MarkerOptions mopt  = new MarkerOptions();
                mopt.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_man_pin));
                mopt.title("");
                mopt.position(new LatLng(loc.getLatitude(), loc.getLongitude()));
                Marker m = mMap.addMarker(mopt);
                UtilityFunctions.riderMarker =m;


                //Set angle for marker

                //Rotate Marker
                UtilityFunctions.riderLoc =loc;
                updateCameraByZoom(zoomed,loc);
                stopLocationService();//stop location service
                getNearestVehicles();
            }
            else{

              //  moveVehicle(loc);
                UtilityFunctions.riderLoc =loc;
                // UtilityFunctions.vehicleMarker.setPosition(new LatLng(loc.getLatitude(), loc.getLongitude()));
               // updateCameraByZoom(zoomed,loc);
            }

        }

    }//SetManMarkerOnMap

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



        UtilityFunctions.ridernprevLoc = loc;


    }

    private void putVehicleMarkersOnMap(){

        if(lstVehiclesByService==null){
            return;
        }
        else {

            if(vMarkerList!=null && vMarkerList.size()>0){

                for(Marker m :vMarkerList){
                    m.remove();
                }
            }
            for(Vehicle v : lstVehiclesByService) {
                MarkerOptions mopt = new MarkerOptions();
                mopt.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_man_pin));
                mopt.title("");
                mopt.position(new LatLng(Double.parseDouble(v.getLatitude()), Double.parseDouble(v.getLongitude())));
                Marker m = mMap.addMarker(mopt);

                vMarkerList.add(m);

            }
        }
    }

    private  void makeBottomButtonPanel(){



        List<Vehicle>lstVehTemp = new ArrayList<Vehicle>();
        HashMap<String,String> vmap = new HashMap<>();

        if(lstRiderService!=null && lstRiderService.size() > 0){

            if(lstVehiclesMain!=null && lstVehiclesMain.size() >0){

                btnPanelBottom.setVisibility(View.VISIBLE);
                for(RiderServ rds : lstRiderService){

                    //Group the vehicles of each rider service

                    for(Vehicle v : lstVehiclesMain){

                        if(v.getProduct_name().equals(rds.getProduct_name())){
                            lstVehTemp.add(v);
                        }
                        double min = Double.parseDouble(lstVehTemp.get(0).getMinutes_away());
                        String pname = lstVehTemp.get(0).getProduct_name();

                        for(Vehicle v1:lstVehTemp){

                            double d =Double.parseDouble(v1.getMinutes_away());
                            if(d<min){
                                min = d;
                                pname = v1.getProduct_name();
                            }
                        }//for
                        //After this loop ,min contains minum minutes away and pname contains the product name
                        //Add this values to a hashmap
                        vmap.put(pname,min+"");

                    }//for

                    lstVehTemp=null;

              }//for outermost

                if(lstBottomButtons!=null) {
                    Collections.sort(lstBottomButtons, new RiderServiceSort());

                    //Iterate through the button list and update the minutes away fields using the vmap

                    for (BottomRiderServButton btn : lstBottomButtons) {

                        String minaway = vmap.get(btn.getRider_serv_name());

                            btn.setMinutes_away(minaway);
                            if (btn.getButtonorder() == 1) {

                                if(btn.getMinutes_away()!=null) {
                                    txtreachtime1.setText(btn.getMinutes_away());
                                }
                                else{
                                    txtreachtime1.setText("NC");
                                }
                                int resID = 0;
                                if (btn.getIsdefault().equals("Y")) {

                                    if(btn.getMinutes_away()!=null){
                                        getVehiclesByRiderService(btn.getProduct_id());
                                        putVehicleMarkersOnMap();
                                    }


                                } else {
                                    resID = getResources().getIdentifier(btn.getRider_serv_name(),
                                            "drawable", getPackageName());
                                }

                                imgveh1.setBackgroundResource(resID);

                                txtvtype1.setText(btn.getRider_serv_name());
                                if(btn.getMinutes_away()==null){
                                    btnVeh1.setTag("NC");
                                }
                                else {
                                    btnVeh1.setTag(btn.getProduct_id());
                                }
                            } else if (btn.getButtonorder() == 2) {

                                if(btn.getMinutes_away()!=null) {

                                    txtreachtime2.setText(btn.getMinutes_away());
                                }
                                else{
                                    txtreachtime2.setText("No cabs");
                                }
                                int resID = getResources().getIdentifier(btn.getRider_serv_name(),
                                        "drawable", getPackageName());

                                imgveh2.setBackgroundResource(resID);

                                txtvtype2.setText(btn.getRider_serv_name());
                                btnVeh2.setTag(btn.getMinutes_away()==null?"NC":btn.getProduct_id());
                            } else if (btn.getButtonorder() == 3) {

                                txtreachtime3.setText(btn.getMinutes_away()==null?"No cabs":btn.getMinutes_away());
                                int resID = getResources().getIdentifier(btn.getRider_serv_name(),
                                        "drawable", getPackageName());

                                imgveh3.setBackgroundResource(resID);

                                txtvtype3.setText(btn.getRider_serv_name());
                                btnVeh3.setTag(btn.getMinutes_away()==null?"NC":btn.getProduct_id());
                            } else if (btn.getButtonorder() == 4) {

                                txtreachtime4.setText(btn.getMinutes_away()==null?"No cabs":btn.getMinutes_away());
                                int resID = getResources().getIdentifier(btn.getRider_serv_name(),
                                        "drawable", getPackageName());

                                imgveh4.setBackgroundResource(resID);

                                txtvtype4.setText(btn.getRider_serv_name());

                                btnVeh4.setTag(btn.getMinutes_away()==null?"NC":btn.getProduct_id());
                            } else if (btn.getButtonorder() == 5) {

                                txtreachtime5.setText(btn.getMinutes_away()==null?"No cabs":btn.getMinutes_away());
                                int resID = getResources().getIdentifier(btn.getRider_serv_name(),
                                        "drawable", getPackageName());

                                imgveh5.setBackgroundResource(resID);

                                txtvtype5.setText(btn.getRider_serv_name());
                                btnVeh5.setTag(btn.getMinutes_away()==null?"NC":btn.getProduct_id());
                            } else if (btn.getButtonorder() == 6) {

                                txtreachtime6.setText(btn.getMinutes_away()==null?"No cabs":btn.getMinutes_away());
                                int resID = getResources().getIdentifier(btn.getRider_serv_name(),
                                        "drawable", getPackageName());

                                imgveh6.setBackgroundResource(resID);

                                txtvtype6.setText(btn.getRider_serv_name());
                                btnVeh6.setTag(btn.getMinutes_away()==null?"NC":btn.getProduct_id());
                            }


                        }


                    //Start Thread to call get nearest vehicles
                    //Hence update the button panel
                       Runnable runnableCode = new Runnable() {
                        @Override
                        public void run() {
                            // Do something here on the main thread
                            Log.d("Handlers", "Called on main thread");
                            // Repeat this the same runnable code block again another 2 seconds
                            getNearestVehiclesThredProc();
                            handler.postDelayed(this, 2000);
                        }
                    };
                     // Start the initial runnable task by posting through the handler
                    handler.post(runnableCode);



                }//if


            }



        }
    }

    private void getVehiclesByRiderService(String riderServiceId){//ProductId

        //Get all rider services
        //Get All vechices

        if(lstVehiclesMain!=null && lstVehiclesMain.size() > 0) {

            for (Vehicle veh : lstVehiclesMain) {

                if (veh.getProduct_id().equals(riderServiceId)) {

                    lstVehiclesByService.add(veh);
                }
            }
        }
    }


    private void getRiderServicesInit(){

        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        if(UtilityFunctions.tenant_id==null && UtilityFunctions.tenant_id.equals("")){

            return;
        }
        Call<List<RiderServ>> call = apiService.getRideServices(UtilityFunctions.tenant_id);

        call.enqueue(new Callback<List<RiderServ>>() {
            @Override
            public void onResponse(Call<List<RiderServ>> call, Response<List<RiderServ>> response) {


                if(response.body()!=null){


                    lstRiderService = response.body();
                    for(RiderServ rds : lstRiderService){

                        BottomRiderServButton btm = new BottomRiderServButton();
                        btm.setButtonorder(rds.getSort_number());
                        btm.setRider_serv_name(rds.getProduct_name());
                        btm.setProduct_id(rds.getProduct_id());
                        btm.setIsdefault(rds.getDefault_product());
                        //Y N default button shown in blue

                        lstBottomButtons.add(btm);
                        btm= null;
                        makeBottomButtonPanel();
                    }


                }
                else  if(response.body()==null){

                    lstRiderService = null;
                    //Show Blank template;


                }
            }//OnResponse

            @Override
            public void onFailure(Call<List<RiderServ>> call, Throwable t) {

                //Something went wrong
              lstRiderService = null;

            }
        });
    }



    private void getNearestVehicles(){

        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        if(UtilityFunctions.tenant_id==null && UtilityFunctions.tenant_id.equals("")){

            lstVehiclesMain = null;
            return;
        }
        Call<List<Vehicle>> call = apiService.getNearestVehicles(UtilityFunctions.tenant_id,
                UtilityFunctions.riderLoc.getLatitude()+"",
                UtilityFunctions.riderLoc.getLongitude()+"");

        call.enqueue(new Callback<List<Vehicle>>() {
            @Override
            public void onResponse(Call<List<Vehicle>> call, Response<List<Vehicle>> response) {


                if(response.body()!=null){


                    lstVehiclesMain = response.body();
                    getRiderServicesInit();


                }
                else  if(response.body()==null){

                    lstVehiclesMain = null;
                    //Show Blank template;


                }
            }//OnResponse

            @Override
            public void onFailure(Call<List<Vehicle>> call, Throwable t) {

                //Something went wrong
                lstVehiclesMain = null;

            }
        });
    }

    private void getNearestVehiclesThredProc(){

        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        if(UtilityFunctions.tenant_id==null && UtilityFunctions.tenant_id.equals("")){

            lstVehiclesMain = null;
            return;
        }
        if(inq){
            return;
        }
        inq = true;
        Call<List<Vehicle>> call = apiService.getNearestVehicles(UtilityFunctions.tenant_id,
                UtilityFunctions.riderLoc.getLatitude()+"",
                UtilityFunctions.riderLoc.getLongitude()+"");

        call.enqueue(new Callback<List<Vehicle>>() {
            @Override
            public void onResponse(Call<List<Vehicle>> call, Response<List<Vehicle>> response) {


                inq=false;
                if(response.body()!=null){


                    lstVehiclesMain = response.body();
                    putVehicleMarkersOnMap();
                    updateRiderServiceButtonsThreadProc();


                }
                else  if(response.body()==null){

                    lstVehiclesMain = null;
                    //Show Blank template;
                    inq=false;


                }
            }//OnResponse

            @Override
            public void onFailure(Call<List<Vehicle>> call, Throwable t) {

                //Something went wrong
                lstVehiclesMain = null;
                inq=false;

            }
        });
    }


    private  void updateRiderServiceButtonsThreadProc(){



        List<Vehicle>lstVehTemp = new ArrayList<Vehicle>();
        HashMap<String,String> vmap = new HashMap<>();

        if(lstRiderService!=null && lstRiderService.size() > 0){

            if(lstVehiclesMain!=null && lstVehiclesMain.size() >0){

                btnPanelBottom.setVisibility(View.VISIBLE);
                for(RiderServ rds : lstRiderService){

                    //Group the vehicles of each rider service

                    for(Vehicle v : lstVehiclesMain){

                        if(v.getProduct_name().equals(rds.getProduct_name())){
                            lstVehTemp.add(v);
                        }
                        double min = Double.parseDouble(lstVehTemp.get(0).getMinutes_away());
                        String pname = lstVehTemp.get(0).getProduct_name();

                        for(Vehicle v1:lstVehTemp){

                            double d =Double.parseDouble(v1.getMinutes_away());
                            if(d<min){
                                min = d;
                                pname = v1.getProduct_name();
                            }
                        }//for
                        //After this loop ,min contains minum minutes away and pname contains the product name
                        //Add this values to a hashmap
                        vmap.put(pname,min+"");

                    }//for

                    lstVehTemp=null;

                }//for outermost

                if(lstBottomButtons!=null) {
                    Collections.sort(lstBottomButtons, new RiderServiceSort());

                    //Iterate through the button list and update the minutes away fields using the vmap

                    for (BottomRiderServButton btn : lstBottomButtons) {

                        String minaway = vmap.get(btn.getRider_serv_name());
                        btn.setMinutes_away(minaway);
                        if(btn.getButtonorder()==1){

                            txtreachtime1.setText(btn.getMinutes_away());

                        }
                        else if(btn.getButtonorder()==2){

                            txtreachtime2.setText(btn.getMinutes_away());

                        }
                        else if(btn.getButtonorder()==3){

                            txtreachtime3.setText(btn.getMinutes_away());

                        }
                        else if(btn.getButtonorder()==4){

                            txtreachtime4.setText(btn.getMinutes_away());

                        }
                        else if(btn.getButtonorder()==5){

                            txtreachtime5.setText(btn.getMinutes_away());

                        }

                        else if(btn.getButtonorder()==6){

                            txtreachtime6.setText(btn.getMinutes_away());

                        }


                    }
                }//if
            }
        }
    }




    class RiderServiceSort implements Comparator<BottomRiderServButton>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(BottomRiderServButton a, BottomRiderServButton b)
        {
            return a.getButtonorder() - b.getButtonorder();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

       if(handler!=null){
           if(runnableCode!=null){
               handler.removeCallbacks(runnableCode);
           }
       }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        lstBottomButtons=null;
        lstVehiclesMain = null;
        lstRiderService=null;
        lstVehiclesByService = null;

        UtilityFunctions.riderMarker=null;
        UtilityFunctions.riderLoc=null;
    }

    private void showPgBar(boolean isShown){

        if(isShown){

            pgBar.setVisibility(View.VISIBLE);
        }
        else{
            pgBar.setVisibility(View.GONE);
        }
    }


    private  void rideNow(){

        if(mPostion2==null || edt_place_dest.getText().toString().equals("")){

        }
        else{
            //Distance and time estimate from source to destination should be calculated
            showPgBar(true);
            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(mPosition, mPostion2);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

        }

    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{

            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            showPgBar(false);


            if(result.size()<1){
               // Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }



            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
               // lineOptions.addAll(points);
               // lineOptions.width(2);
               // lineOptions.color(Color.RED);
            }

            //tvDistanceDuration.setText("Distance:"+distance + ", Duration:"+duration);
            ETA = duration;
            DIST = distance;



            // Drawing polyline in the Google Map for the i-th route
           // map.addPolyline(lineOptions);
            startConfirmBooking();
        }
    }

    private void startConfirmBooking(){

         Intent ii = new Intent(getBaseContext(),ConfirmBookingActivity.class);
         UtilityFunctions.source = edt_place_source.getText().toString();
         UtilityFunctions.source = edt_place_dest.getText().toString();
         UtilityFunctions.position = mPosition;
         UtilityFunctions.position2 = mPostion2;
         UtilityFunctions.dist =  DIST;
         UtilityFunctions.eta = ETA;

    }
}
