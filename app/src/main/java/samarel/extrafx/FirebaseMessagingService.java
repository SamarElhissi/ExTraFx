package samarel.extrafx;

/**
 * Created by SamarEL on 12-Aug-18.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FirebaseMessagingService
        extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingServic";

    public FirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String strTitle = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();

        Database DB = new Database(getApplicationContext());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Date today = Calendar.getInstance().getTime();
        String today_date = df.format(today);

        DB.DeleteNotification();
        DB.AddNotification(strTitle, message, today_date);

        String action = remoteMessage.getData().get("action");
        sendNotification(strTitle, message, action);
    }

    @Override
    public void onDeletedMessages() {

    }

    private void sendNotification(String title, String messageBody, String action) {

        Intent[] intents = new Intent[1];
        Intent intent = new Intent(this, NotificationDetails.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("title", title);
        intent.putExtra("text", messageBody);
        intent.putExtra("action", action);
        intents[0] = intent;
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                intents, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri
                (RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationbuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        ;

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationbuilder.build());
    }
}