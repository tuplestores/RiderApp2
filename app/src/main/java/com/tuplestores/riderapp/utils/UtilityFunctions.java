package com.tuplestores.riderapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/*Created By Ajish Dharman on 28-August-2019
 *
 *
 */public class UtilityFunctions {

    public static String tenant_id;
    public static final String SHARED_P = "private_shared_peref";

    public static Marker riderMarker;
    public  static Location ridernprevLoc;

    public static Location riderLoc;

    public  static String source;
    public static String dest;
    public static String dist;
    public static String eta;
    public static LatLng position;
    public  static LatLng position2;
    public static String productId;
    public static String rider_id;

    public  static  String farestart;
    public  static String  fareend;



    public static boolean setSharedPreferenceRegUser(Context ctx, String rider_id, String org_code){

        try {
            ctx.getApplicationContext().getSharedPreferences(SHARED_P, 0);

            SharedPreferences sharedPreferences = ctx.getApplicationContext().getSharedPreferences(SHARED_P, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("rider_id", rider_id);
            editor.putString("tenant_id", tenant_id);
            editor.commit();
            return true;
        }
        catch (Exception ex){
            return  false;
        }
    }


    public static boolean getAllSharedPrefValues(Context ctx){

        SharedPreferences sharedPreferences =  ctx.getApplicationContext().getSharedPreferences(SHARED_P,0);

        tenant_id = sharedPreferences.getString("tenant_id","");

        if(tenant_id!=null && !tenant_id.equals("") ){

            UtilityFunctions.tenant_id = tenant_id;
            return true;
        }
        else{
            return  false;
        }

    }
}
