package com.tuplestores.riderapp.api;

import com.tuplestores.riderapp.model.ApiResponse;
import com.tuplestores.riderapp.model.RideRequest;
import com.tuplestores.riderapp.model.RiderServ;
import com.tuplestores.riderapp.model.Vehicle;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/*Created By Ajish Dharman on 28-August-2019
 *
 *
 */
public interface ApiInterface {


    @GET("dispatchAPI/generateOTP")
    Call<ApiResponse> generateOTP(@Query("i_tenant_id") String i_tenant_id,
                                   @Query("i_isd_code") String i_isd_code, @Query("i_mobile") String i_mobile);


     @GET("dispatchAPI/verifyOTP")
     Call<ApiResponse> verifyOTP(@Query("i_tenant_id") String i_tenant_id,
                @Query("i_isd_code") String i_isd_code, @Query("i_mobile") String i_mobile,
                @Query("i_otp") String i_otp);




       @GET("dispatchAPI/createRider")
       Call<ApiResponse> createRider(@Query("i_first_name") String i_first_name,
                                @Query("i_last_name") String i_last_name, @Query("i_isd_code") String i_isd_code,
                                @Query("i_mobile") String i_mobile,  @Query("i_tenant_code") String i_tenant_code);


        @GET("dispatchAPI/getRideServices")
        Call<List<RiderServ>> getRideServices(@Query("i_tenant_id") String i_tenant_id);

        @GET("dispatchAPI/getNearestVehicles")
        Call<List<Vehicle>> getNearestVehicles(@Query("i_tenant_id") String i_tenant_id,
                                               @Query("i_pick_up_latitude") String i_pick_up_latitude,
                                               @Query("i_pick_up_longitude") String i_pick_up_longitude);





       @GET("dispatchAPI/getEstimatedFare")
        Call<ApiResponse> getEstimatedFare(@Query("i_tenant_id") String i_tenant_id,
                                             @Query("i_product_id") String i_product_id,
                                             @Query("i_estimated_distance") String i_estimated_distance);


    /*public @ResponseBody Object createRideRequest(@RequestParam String i_tenant_id,
                                                  @RequestParam String i_product_id,
                                                  @RequestParam String i_rider_id,
                                                  @RequestParam String i_pick_up_latitude,
                                                  @RequestParam String i_pick_up_longitude,
                                                  @RequestParam String i_pick_up_location_text,
                                                  @RequestParam String i_drop_off_latitude,
                                                  @RequestParam String i_drop_off_longitude,
                                                  @RequestParam String i_drop_off_location_text,
                                                  @RequestParam String i_estimated_distance,
                                                  @RequestParam String i_estimated_fare_start,
                                                  @RequestParam String i_estimated_fare_end*/


    @GET("dispatchAPI/createRideRequest")
    Call<ApiResponse> createRideRequest(@Query("i_tenant_id") String i_tenant_id,

                                        @Query("i_product_id") String i_product_id,

                                        @Query("i_rider_id") String i_rider_id,

                                        @Query("i_pick_up_latitude") String i_pick_up_latitude,

                                        @Query("i_pick_up_longitude") String i_pick_up_longitude,

                                        @Query("i_pick_up_location_text") String i_pick_up_location_text,

                                        @Query("i_drop_off_latitude") String i_drop_off_latitude,

                                        @Query("i_drop_off_longitude") String i_drop_off_longitude,

                                        @Query("i_drop_off_location_text") String i_drop_off_location_text,


                                        @Query("i_estimated_distance") String i_estimated_distance,

                                        @Query("i_estimated_fare_start") String i_estimated_fare_start,

                                        @Query("i_estimated_fare_end") String i_estimated_fare_end
                                        );





    @GET("dispatchAPI/updateRiderDeviceTocken")
    Call<ApiResponse> updateRiderDeviceTocken(@Query("i_tenant_id") String i_tenant_id,
                                              @Query("i_rider_id") String i_rider_id,
                                              @Query("i_device_token") String i_device_token);


    @GET("dispatchAPI/getRideRequest")
    Call<RideRequest> getRideRequest(@Query("i_tenant_id") String i_tenant_id,
                                       @Query("i_ride_request_id") String i_ride_request_id);




}

