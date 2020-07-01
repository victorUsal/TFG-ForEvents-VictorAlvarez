package com.victor.forevents;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.victor.forevents.model.Notification;

import java.util.HashMap;
import java.util.Random;

public class Fcm extends FirebaseMessagingService {

    FirebaseUser firebaseUser;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }

    private void guardarToken(String s) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Tokens").child("LOL");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", s);

        reference.push().setValue(hashMap);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();
        Log.e("TAG","Mensaje recibido de" + from);

        if(remoteMessage.getData().size() > 0){

            String titulo = remoteMessage.getData().get("titulo");
            String detalle = remoteMessage.getData().get("detalle");

            mayorqueoreo(titulo,detalle);

        }



    }

    private void mayorqueoreo(String titulo, String detalle) {

        String id = "mensaje";
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(id, "nuevo", NotificationManager.IMPORTANCE_HIGH);
            nc.setShowBadge(true);
            assert nm != null;
            nm.createNotificationChannel(nc);
        }
        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(titulo)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(detalle)
                .setContentIntent(clicknotificacion())
                .setContentInfo("nuevo");

        Random random = new Random();
        int idNotify = random.nextInt(8000);

        assert nm != null;
        nm.notify(idNotify,builder.build());

    }

    public PendingIntent clicknotificacion(){
        Intent nf = new Intent(getApplicationContext(), HomeActivity.class);
        nf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this,0,nf,0);
    }


}
