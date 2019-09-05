package com.tuplestores.riderapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.tuplestores.riderapp.api.ApiClient;
import com.tuplestores.riderapp.api.ApiInterface;
import com.tuplestores.riderapp.model.ApiResponse;
import com.tuplestores.riderapp.utils.UtilityFunctions;

public class RegisterUserActivity extends AppCompatActivity {

    EditText edtFirstName;

    EditText edtLastName;

    EditText edtOrganizationCode;

    Button btnCompleteReg;
    String isd="";
    String mobile="";
    String tenant_id,rider_id;

    ProgressBar pgBar;
    Activity thisActivity;

    private void initialize(){

        edtFirstName = (EditText)(findViewById(R.id.edtFirstName));
        edtLastName = (EditText)(findViewById(R.id.edtLastName));
        edtOrganizationCode = (EditText)(findViewById(R.id.edtOrganizationCode));
        btnCompleteReg = (Button)(findViewById(R.id.btnCompleteReg));
        pgBar = (ProgressBar) findViewById(R.id.pgBar);

        isd = this.getIntent().getStringExtra("EXTRA_ISD");
        mobile = this.getIntent().getStringExtra("EXTRA_MOBILE");
        showHidePgBar(false);

        btnCompleteReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtFirstName.getText() == null || edtFirstName.getText().toString().equals("")){

                    ShowAlert(getBaseContext(),getResources().getString(R.string.input_first_name),"F");
                }
                else if(edtOrganizationCode.getText() == null || edtOrganizationCode.getText().toString().equals("")){

                    ShowAlert(getBaseContext(),getResources().getString(R.string.input_org_code),"O");
                }
                else{

                    registerUser();
                }

            }
        });


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        initialize();
        thisActivity = this;
    }

    private void registerUser(){


        showHidePgBar(true);
        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);


        Call<ApiResponse> call = apiService.createRider(edtFirstName.getText().toString(),
                                                edtLastName.getText().toString(),isd,mobile,
                                                edtOrganizationCode.getText().toString());

        showHidePgBar(true);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                showHidePgBar(false);
                if(response.body()!=null){

                    ApiResponse api = response.body();
                    if(api.getStatus().trim().equals("S")){

                        //Go to the Verification Activity

                        String msg = api.getMsg();
                        String[] strarr =  msg.split("_");
                       // api.setMsg(tenant_id +"_"+rider_id);
                        if(strarr !=null){

                            tenant_id = strarr[0];
                            rider_id = strarr[1];
                        }


                        UtilityFunctions.setSharedPreferenceRegUser(getBaseContext(),rider_id,tenant_id);
                        Intent ii = new Intent(thisActivity,RiderHome.class);
                        startActivity(ii);
                        finish();
                    }
                    else {

                        ShowAlert(thisActivity,getResources().getString(R.string.something_went_wrong),"");
                    }

                }
                else  if(response.body()==null){

                    ShowAlert(thisActivity,getResources().getString(R.string.something_went_wrong),"");

                }
            }//OnResponse

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

                showHidePgBar(false);
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

                            edtFirstName.requestFocus();
                        }
                        else if(focus.equals("O")){

                            edtOrganizationCode.requestFocus();
                        }
                    }
                });


        if(dlg!=null){

            dlg.show();
        }
    }//ShowAlert


    private void  showHidePgBar(boolean toShow){

        if(toShow){

            pgBar.setVisibility(View.VISIBLE);
        }
        else{
            pgBar.setVisibility(View.GONE);
        }
    }
}
