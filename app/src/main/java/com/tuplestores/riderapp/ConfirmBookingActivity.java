package com.tuplestores.riderapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.tuplestores.riderapp.api.ApiClient;
import com.tuplestores.riderapp.api.ApiInterface;
import com.tuplestores.riderapp.model.ApiResponse;
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
import java.util.HashMap;
import java.util.List;

public class ConfirmBookingActivity extends AppCompatActivity implements OnMapReadyCallback {

   EditText edt_place_source;
   EditText edt_place_dest;
   TextView txtEstRate;
   TextView txtEstTime;
   TextView txtEstDist;
   Button btnConfirmBooking;
   private GoogleMap mMap;
   boolean zoomed;
   String source;

   LatLng mPosition;
   LatLng mPostion2;

   ProgressBar pgBar;

   int AUTOCOMPLETE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);
    }

    private void initialize(){


        zoomed =false;
        edt_place_dest = (EditText)findViewById(R.id.edt_place_dest);
        edt_place_source = (EditText)findViewById(R.id.edt_place_source);
        txtEstRate = (TextView) findViewById(R.id.txtEstRate);
        txtEstTime = (TextView)findViewById(R.id.txtEstTime);
        txtEstDist = (TextView)findViewById(R.id.txtEstDist);
        btnConfirmBooking = (Button) findViewById(R.id.btnConfirmBooking);
        pgBar = (ProgressBar)findViewById(R.id.pgBar);

        if(edt_place_source.getText()==null || edt_place_source.getText().equals("")){

            txtEstRate.setText("0.0");
        }
        else{

            txtEstRate.setText("Calculating...");
        }

        if(UtilityFunctions.eta!=null ){

            txtEstTime.setText("Est.Time: "+UtilityFunctions.eta);
        }
        else{
            txtEstTime.setText("Est.Time: "+"0");
        }

        if(UtilityFunctions.dist!=null){

            txtEstDist.setText("Est.Time: "+"0");
        }

        edt_place_dest.setText(UtilityFunctions.source==null?"":UtilityFunctions.source);
        edt_place_dest.setText(UtilityFunctions.dest==null?"":UtilityFunctions.dest);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Check if the user revoked runtime permissions.


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

        btnConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirmBooking();
            }
        });




    }//init



    ///Map and other


    private void showHidePg(boolean isShown){

        if(isShown){
            pgBar.setVisibility(View.VISIBLE);
        }
        else{
            pgBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        initLocations();


    }
    private void plotVehicleOnMap(){


    }

    private void initLocations(){

        Marker m1,m2;
        m1=null;
        boolean both = false;
        if(UtilityFunctions.position!=null){

            MarkerOptions mopt = new MarkerOptions();
            mopt.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_man_pin));
            mopt.title("");
            mopt.position(UtilityFunctions.position);
            m1 = mMap.addMarker(mopt);

            UtilityFunctions.riderMarker= m1;


         }
         if(UtilityFunctions.position2!=null) {

             MarkerOptions mopt = new MarkerOptions();
             mopt.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_man_pin));
             mopt.title("");
             mopt.position(UtilityFunctions.position);
             m2 = mMap.addMarker(mopt);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
             builder.include(m1.getPosition());
             builder.include(m2.getPosition());
             LatLngBounds bounds = builder.build();
             int padding = 0; // offset from edges of the map in pixels
             CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
             if(mMap!=null) {
                 mMap.animateCamera(cu);
             }

             //Calculate ETA
             if(UtilityFunctions.position!=null){

                 rideNow();
             }

         }
         else{

             CameraPosition cameraPosition = new CameraPosition.Builder()
                     .target(UtilityFunctions.position)      // Sets the center of the map to Mountain View
                     .zoom(17)                   // Sets the zoom
                     .build();                   // Creates a CameraPosition from the builder
             mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
             zoomed = true;

       }




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
                    UtilityFunctions.position = place.getLatLng();
                    //Zoom to the palce
                    mMap.clear();
                    putRiderMarkerOnMap(place.getLatLng());
                    if(mMap.getCameraPosition().zoom >16 || mMap.getCameraPosition().zoom < 16){
                        zoomed = true;
                    }
                    if(zoomed) {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(place.getLatLng());
                        mMap.animateCamera(cameraUpdate);
                    }
                    else{
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(),16);
                        mMap.animateCamera(cameraUpdate);
                    }

                }else if(source.equals("D")){

                    if (!place.getAddress().toString().contains(place.getName())) {
                        edt_place_dest.setText(place.getName() + ", " + place.getAddress());
                    } else {
                        edt_place_dest.setText(place.getAddress());
                        UtilityFunctions.position2 = place.getLatLng();

                        //Calculate ETA,DIST,and Fare estimation
                        rideNow();
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


    private void putRiderMarkerOnMap(LatLng latLng) {

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


    private  void rideNow(){

        if(UtilityFunctions.position2==null || edt_place_dest.getText().toString().equals("")){


            return;

        }

        else{
            //Distance and time estimate from source to destination should be calculated
           showHidePg(true);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(mPosition, mPostion2);

            ConfirmBookingActivity.DownloadTask downloadTask = new ConfirmBookingActivity.DownloadTask();

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
    private String downloadUrl(String strUrl) throws IOException {
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

            ConfirmBookingActivity.ParserTask parserTask = new ConfirmBookingActivity.ParserTask();

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

           // showPgBar(false);

            showHidePg(false);

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
            UtilityFunctions.dist = distance;
            UtilityFunctions.eta = duration;
            txtEstTime.setText("Est.time: "+duration);
            txtEstDist.setText("Est.dist: "+distance);

            showCost(distance);


            // Drawing polyline in the Google Map for the i-th route
            // map.addPolyline(lineOptions);

        }
    }

    private void showCost(String distance){

        showHidePg(true);
        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);


        Call<ApiResponse> call = apiService.getEstimatedFare(UtilityFunctions.tenant_id,
                                                             UtilityFunctions.productId,
                                                             distance);

        showHidePg(true);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                showHidePg(false);
                if(response.body()!=null){

                    ApiResponse api = response.body();
                    if(api.getStatus().trim().equals("S")){

                        //Go to the Verification Activity

                        String msg = api.getMsg();
                        String[]  str = msg.split("_");
                        txtEstRate.setText(str[0]+ " "+str[1]+"-"+str[2]);

                        UtilityFunctions.farestart = str[1];
                        UtilityFunctions.fareend = str[2];


                    }
                    else {

                       // ShowAlert(thisActivity,getResources().getString(R.string.something_went_wrong),"");
                    }

                }
                else  if(response.body()==null){

                    //ShowAlert(thisActivity,getResources().getString(R.string.something_went_wrong),"");

                }
            }//OnResponse

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

                showHidePg(false);
                //Something went wrong
              //  ShowAlert(getBaseContext(),getResources().getString(R.string.something_went_wrong),"");

            }
        });

    }

    private void confirmBooking(){


        showHidePg(true);
        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        if(UtilityFunctions.eta==null){
            UtilityFunctions.eta="";
        }

        if(UtilityFunctions.dist==null){
            UtilityFunctions.dist="";
        }

        if(UtilityFunctions.fareend==null){
            UtilityFunctions.fareend="";
        }

        if(UtilityFunctions.farestart==null){
            UtilityFunctions.farestart="";
        }

        Call<ApiResponse> call = apiService.createRideRequest(UtilityFunctions.tenant_id,UtilityFunctions.productId,
                UtilityFunctions.rider_id,UtilityFunctions.position.latitude+"",
                UtilityFunctions.position.longitude+""
                ,edt_place_source.getText().toString(),

                UtilityFunctions.position2.latitude+"",
                UtilityFunctions.position2.longitude+"",
                edt_place_dest.getText().toString(),UtilityFunctions.dist,
                UtilityFunctions.farestart,UtilityFunctions.fareend);

        showHidePg(true);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                showHidePg(false);
                if(response.body()!=null){

                    ApiResponse api = response.body();
                    if(api.getStatus().trim().equals("S")){

                        ShowAlert(getBaseContext(),"Ride request created successfully.","");


                    }
                    else {

                         ShowAlert(getBaseContext(),getResources().getString(R.string.something_went_wrong),"");
                    }

                }
                else  if(response.body()==null){
                    ShowAlert(getBaseContext(),getResources().getString(R.string.something_went_wrong),"");

                }
            }//OnResponse

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

                showHidePg(false);
                //Something went wrong
                 ShowAlert(getBaseContext(),getResources().getString(R.string.something_went_wrong),"");

            }
        });
    }

    private void ShowAlert(Context ctx, String msg, final String focus){

        androidx.appcompat.app.AlertDialog.Builder dlg = null;

        dlg =   new AlertDialog.Builder(ctx,R.style.AlertDialogTheme)
                .setTitle("")
                .setMessage(msg)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if(focus.equals("F")) {


                        }
                        else if(focus.equals("O")){


                        }
                    }
                });


        if(dlg!=null){

            dlg.show();
        }
    }//ShowAlert




}
