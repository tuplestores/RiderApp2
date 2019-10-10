package com.tuplestores.riderapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


import com.tuplestores.riderapp.PickupArrivalActivity;

import androidx.core.app.NotificationCompat;

/*Created By Ajish Dharman on 15-July-2019
 *
 *
 */public class NotificationUtil {

    private Context mCtx;
    private static NotificationUtil mInstance;

    private NotificationUtil(Context context) {
        mCtx = context;
    }

    public static synchronized NotificationUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NotificationUtil(context);
        }
        return mInstance;
    }

    public void displayNotification(String title, String body) {


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx, Constants.FIREBASE_CHANNEL_ID);

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(body);

        NotificationManager mNotifyMgr =
                (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);

        /*
         *  Clicking on the notification will take us to this intent
         *  Right now we are using the MainActivity as this is the only activity we have in our application
         *  But for your project you can customize it as you want
         * */

        Intent resultIntent = new Intent(mCtx, PickupArrivalActivity.class);
        resultIntent.putExtra("EXTRA_RIDE_REQUEST_ID","");


        /*
         *  Now we will create a pending intent
         *  The method getActivity is taking 4 parameters
         *  All paramters are describing themselves
         *  0 is the request code (the second parameter)
         *  We can detect this code in the activity that will open by this we can get
         *  Which notification opened the activity
         * */
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(Constants.FIREBASE_CHANNEL_ID, Constants.CHANNEL_NAME, importance);
            mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        } else {

            mBuilder.setVibrate(new long[]{100, 250})
                    .setAutoCancel(true);




            /*
             * The first parameter is the notification id
             * better don't give a literal here (right now we are giving a int literal)
             * because using this id we can modify it later
             * */
            if (mNotifyMgr != null) {
                mNotifyMgr.notify(1, mBuilder.build());
            }
        }
    }
}
