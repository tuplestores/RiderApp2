package com.tuplestores.riderapp.model;

/*Created By Ajish Dharman on 03-September-2019
 *
 *
 */
public class BottomRiderServButton {

     private int buttonorder;
     private String minutes_away;
     private  String rider_serv_name;
     private String product_id;
     private String isdefault;
     private  String imagename;

    public int getButtonorder() {
        return buttonorder;
    }

    public void setButtonorder(int buttonorder) {
        this.buttonorder = buttonorder;
    }

    public String getMinutes_away() {
        return minutes_away;
    }

    public void setMinutes_away(String minutes_away) {
        this.minutes_away = minutes_away;
    }

    public String getRider_serv_name() {
        return rider_serv_name;
    }

    public void setRider_serv_name(String rider_serv_name) {
        this.rider_serv_name = rider_serv_name;
    }

    public String getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(String isdefault) {
        this.isdefault = isdefault;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }
}
