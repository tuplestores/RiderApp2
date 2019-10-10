package com.tuplestores.riderapp.services;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tuplestores.riderapp.R;
import com.tuplestores.riderapp.RiderHome;
import com.tuplestores.riderapp.api.ApiClient;
import com.tuplestores.riderapp.api.ApiInterface;
import com.tuplestores.riderapp.model.ApiResponse;
import com.tuplestores.riderapp.utils.UserObject;
import com.tuplestores.riderapp.utils.UtilityFunctions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*Created By Ajish Dharman on 12-September-2019
 *
 *
 */public class FireBaseService  extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken(String s) {

        UserObject.deviceTocken = s;
    }
}
