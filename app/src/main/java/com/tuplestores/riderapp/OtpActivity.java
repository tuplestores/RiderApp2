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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.tuplestores.riderapp.api.ApiClient;
import com.tuplestores.riderapp.api.ApiInterface;
import com.tuplestores.riderapp.model.ApiResponse;

public class OtpActivity extends AppCompatActivity {

    EditText edtOTP1;
    EditText edtOTP2;
    EditText edtOTP3;
    EditText edtOTP4;
    ProgressBar pgBar;
    String isd;
    String mobile;
    Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
    }

    private void initialize(){

        edtOTP1 = (EditText)findViewById(R.id.otp1);
        edtOTP2 = (EditText)findViewById(R.id.otp2);

        edtOTP3 = (EditText)findViewById(R.id.otp3);
        edtOTP4 = (EditText)findViewById(R.id.otp4);
        pgBar = (ProgressBar)findViewById(R.id.pgBar);

        edtOTP1.requestFocus();

        edtOTP1.addTextChangedListener(new GenericTextWatcher(edtOTP1));
        edtOTP2.addTextChangedListener(new GenericTextWatcher(edtOTP2));
        edtOTP3.addTextChangedListener(new GenericTextWatcher(edtOTP3));
        edtOTP4.addTextChangedListener(new GenericTextWatcher(edtOTP4));

        isd = this.getIntent().getStringExtra("EXTRA_ISD");
        mobile = this.getIntent().getStringExtra("EXTRA_MOBILE");
        thisActivity = this;

    }

    private void  showHidePgBar(boolean toShow){

        if(toShow){

            pgBar.setVisibility(View.VISIBLE);
        }
        else{
            pgBar.setVisibility(View.GONE);
        }
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

                        if(focus=="MOB") {

                           // edtMobileNumber.requestFocus();
                        }
                    }
                });


        if(dlg!=null){

            dlg.show();
        }
    }//ShowAlert
    private  void verifyOTP(){


        //Call the API
        final ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);


        String otp = edtOTP1.getText().toString()+edtOTP2.getText().toString()
                +edtOTP3.getText().toString()+edtOTP4.getText().toString();
        Call<ApiResponse> call = apiService.verifyOTP("",isd,mobile,otp);

        showHidePgBar(true);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                showHidePgBar(false);
                if(response.body()!=null){

                    ApiResponse api = response.body();
                    if(api.getStatus().trim().equals("S")){

                        //Go to the Verification Activity

                        Intent ii = new Intent(thisActivity,RegisterUserActivity.class);

                        ii.putExtra("EXTRA_ISD",isd);
                        ii.putExtra("EXTRA_MOBILE",mobile);

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

    public class GenericTextWatcher implements TextWatcher{

        View view;

        private GenericTextWatcher(View view)
        {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            String text = editable.toString();
            switch(view.getId())
            {

                case R.id.otp1:
                    if(text.length()==1)
                        edtOTP2.requestFocus();
                    break;
                case R.id.otp2:
                    if(text.length()==1)
                        edtOTP3.requestFocus();
                    else if(text.length()==0)
                        edtOTP1.requestFocus();
                    break;
                case R.id.otp3:
                    if(text.length()==1)
                        edtOTP4.requestFocus();
                    else if(text.length()==0)
                        edtOTP2.requestFocus();
                    break;
                case R.id.otp4:
                    if(text.length()==0)
                        edtOTP3.requestFocus();
                    else if(text.length()==1){

                        if(isd!=null && isd!="" && mobile==null && mobile!=""){

                            verifyOTP();
                        }
                        else{
                            ShowAlert(getBaseContext(),getResources().getString(R.string.something_went_wrong),"");
                        }
                        //Call verify OTP
                    }
                    break;
            }

        }
    }//TextWatcher
}
