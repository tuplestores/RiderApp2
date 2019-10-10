package com.tuplestores.riderapp.model;

/*Created By Ajish Dharman on 23-September-2019
 *
 *
 */public class RideRequest {

    private String ride_request_id;
    private String request_date_time;
    private String ride_request_status_id;
    private String ride_request_status_desc;
    private String ride_request_status_date_time;
    private String product_id;
    private String product_name;
    private String pick_up_latitude;
    private String pick_up_longitude;
    private String pick_up_location_text;
    private String drop_off_latitude;
    private String drop_off_longitude;
    private String drop_off_location_text;
    private String vehicle_make_name;
    private String vehicle_model_name;
    private String plate_number;
    private String  driver_photo_path;
    private int  ride_rating;

    public String getRide_request_id() {
        return ride_request_id;
    }

    public void setRide_request_id(String ride_request_id) {
        this.ride_request_id = ride_request_id;
    }

    public String getRequest_date_time() {
        return request_date_time;
    }

    public void setRequest_date_time(String request_date_time) {
        this.request_date_time = request_date_time;
    }

    public String getRide_request_status_id() {
        return ride_request_status_id;
    }

    public void setRide_request_status_id(String ride_request_status_id) {
        this.ride_request_status_id = ride_request_status_id;
    }

    public String getRide_request_status_desc() {
        return ride_request_status_desc;
    }

    public void setRide_request_status_desc(String ride_request_status_desc) {
        this.ride_request_status_desc = ride_request_status_desc;
    }

    public String getRide_request_status_date_time() {
        return ride_request_status_date_time;
    }

    public void setRide_request_status_date_time(String ride_request_status_date_time) {
        this.ride_request_status_date_time = ride_request_status_date_time;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPick_up_latitude() {
        return pick_up_latitude;
    }

    public void setPick_up_latitude(String pick_up_latitude) {
        this.pick_up_latitude = pick_up_latitude;
    }

    public String getPick_up_longitude() {
        return pick_up_longitude;
    }

    public void setPick_up_longitude(String pick_up_longitude) {
        this.pick_up_longitude = pick_up_longitude;
    }

    public String getPick_up_location_text() {
        return pick_up_location_text;
    }

    public void setPick_up_location_text(String pick_up_location_text) {
        this.pick_up_location_text = pick_up_location_text;
    }

    public String getDrop_off_latitude() {
        return drop_off_latitude;
    }

    public void setDrop_off_latitude(String drop_off_latitude) {
        this.drop_off_latitude = drop_off_latitude;
    }

    public String getDrop_off_longitude() {
        return drop_off_longitude;
    }

    public void setDrop_off_longitude(String drop_off_longitude) {
        this.drop_off_longitude = drop_off_longitude;
    }

    public String getDrop_off_location_text() {
        return drop_off_location_text;
    }

    public void setDrop_off_location_text(String drop_off_location_text) {
        this.drop_off_location_text = drop_off_location_text;
    }

    public String getVehicle_make_name() {
        return vehicle_make_name;
    }

    public void setVehicle_make_name(String vehicle_make_name) {
        this.vehicle_make_name = vehicle_make_name;
    }

    public String getVehicle_model_name() {
        return vehicle_model_name;
    }

    public void setVehicle_model_name(String vehicle_model_name) {
        this.vehicle_model_name = vehicle_model_name;
    }

    public String getPlate_number() {
        return plate_number;
    }

    public void setPlate_number(String plate_number) {
        this.plate_number = plate_number;
    }

    public String getDriver_photo_path() {
        return driver_photo_path;
    }

    public void setDriver_photo_path(String driver_photo_path) {
        this.driver_photo_path = driver_photo_path;
    }

    public int getRide_rating() {
        return ride_rating;
    }

    public void setRide_rating(int ride_rating) {
        this.ride_rating = ride_rating;
    }
}
