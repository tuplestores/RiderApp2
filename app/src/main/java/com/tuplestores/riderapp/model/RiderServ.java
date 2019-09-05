package com.tuplestores.riderapp.model;

/*Created By Ajish Dharman on 03-September-2019
 *
 *
 */public class RiderServ {

    private String product_id;
    private String product_name;
    private int sort_number;
    private String default_product;

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

    public int getSort_number() {
        return sort_number;
    }

    public void setSort_number(int sort_number) {
        this.sort_number = sort_number;
    }

    public String getDefault_product() {
        return default_product;
    }

    public void setDefault_product(String default_product) {
        this.default_product = default_product;
    }
}
