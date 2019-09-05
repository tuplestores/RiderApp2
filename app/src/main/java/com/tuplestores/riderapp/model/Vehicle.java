package com.tuplestores.riderapp.model;

/*Created By Ajish Dharman on 03-September-2019
 *
 *
 */public class Vehicle {

    private String tenant_id;
    private String vehicle_id;
    private String vehicle_name;
    private String plate_number;
    private String rider_service; //Product Name in procedure
    private String product_id;
    private String minutes_away;
    private String vehicle_make_name;
    private String vehicle_model_name;
    private String vehicle_color;
    private String product_name;
    private String latitude;
    private String longitude;

    public String getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getPlate_number() {
        return plate_number;
    }

    public void setPlate_number(String plate_number) {
        this.plate_number = plate_number;
    }

    public String getRider_service() {
        return rider_service;
    }

    public void setRider_service(String rider_service) {
        this.rider_service = rider_service;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getMinutes_away() {
        return minutes_away;
    }

    public void setMinutes_away(String minutes_away) {
        this.minutes_away = minutes_away;
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

    public String getVehicle_color() {
        return vehicle_color;
    }

    public void setVehicle_color(String vehicle_color) {
        this.vehicle_color = vehicle_color;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
