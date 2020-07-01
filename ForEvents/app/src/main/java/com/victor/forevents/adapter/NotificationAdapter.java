package com.victor.forevents.adapter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.victor.forevents.PostDetailActivity;
import com.victor.forevents.ProfileActivity;
import com.victor.forevents.R;
import com.victor.forevents.model.Event;
import com.victor.forevents.model.Notification;
import com.victor.forevents.model.User;



import java.util.HashMap;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{


    private Context context;
    private List<Notification> notifications;
    private FirebaseUser firebaseUser;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent,false);


        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Notification notification = notifications.get(position);

        if(notification.getTag().equals("invitar")){
            holder.botones_notificacion.setVisibility(View.VISIBLE);
        }else{
            holder.botones_notificacion.setVisibility(View.GONE);
        }

        holder.text.setText((notification.getText()));

       getUserInfo(holder.image_profile,holder.username, notification.getUserid());

       if(notification.getPostid().equals("")){
           holder.post_event.setVisibility(View.GONE);
       }else{
           holder.post_event.setVisibility(View.VISIBLE);
           getPostInfo(holder.post_event, notification.getPostid());

       }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.getTag().equals("asistir") || notification.getTag().equals("invitar")){
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("postid",notification.getPostid());
                    editor.apply();
                    context.startActivity(new Intent(context, PostDetailActivity.class));
                }else{
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",notification.getUserid());
                    editor.apply();

                    context.startActivity(new Intent(context, ProfileActivity.class));
                }
            }
        });

       holder.btn_rechazar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               holder.botones_notificacion.setVisibility(View.GONE);

               FirebaseDatabase.getInstance().getReference().child("Invitation").child(notification.getPostid()).child(firebaseUser.getUid()).removeValue();
               FirebaseDatabase.getInstance().getReference().child("Invitation").child(notification.getPostid()).child(notification.getUserid()).child("send").child(firebaseUser.getUid()).removeValue();


               DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
               reference.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                           Notification notification1 = snapshot.getValue(Notification.class);
                           if(notification1.getTag().equals("invitar") && notification1.getPostid().equals(notification.getPostid())){
                               snapshot.getRef().removeValue();
                               Toast.makeText(context, "Se ha rechazado la invitacion", Toast.LENGTH_SHORT).show();
                           }
                       }

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });

           }
       });




       holder.btn_aceptar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FirebaseDatabase.getInstance().getReference().child("Attends").child(firebaseUser.getUid()).child(notification.getPostid()).setValue(true);
               FirebaseDatabase.getInstance().getReference().child("Likes").child(notification.getPostid()).child(firebaseUser.getUid()).setValue(true);

               holder.botones_notificacion.setVisibility(View.GONE);
               addNotifications(notification.getUserid(), notification.getPostid());

               FirebaseDatabase.getInstance().getReference().child("Invitation").child(notification.getPostid()).child(firebaseUser.getUid()).removeValue();
               FirebaseDatabase.getInstance().getReference().child("Invitation").child(notification.getPostid()).child(notification.getUserid()).child("send").child(firebaseUser.getUid()).removeValue();


               DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                           Notification notification1 = snapshot.getValue(Notification.class);
                           if(notification1.getTag().equals("invitar") && notification1.getPostid().equals(notification.getPostid())){
                               snapshot.getRef().removeValue();
                               Toast.makeText(context, "Se ha confirmado tu asistencia", Toast.LENGTH_SHORT).show();
                           }
                       }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

           }
       });

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_event;
        public LinearLayout botones_notificacion;
        public Button btn_aceptar, btn_rechazar;
        public TextView username, text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.profile_image);
            post_event = itemView.findViewById(R.id.post_image_event);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
            botones_notificacion = itemView.findViewById(R.id.notification_button);
            btn_aceptar = itemView.findViewById(R.id.aceptar_invitacion);
            btn_rechazar = itemView.findViewById(R.id.rechazar_invitacion);
        }
    }

        private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){

            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(publisherid);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Glide.with(context).load(user.getImagen()).into(imageView);
                    username.setText(user.getUsername());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void getPostInfo(final ImageView imageView, String postid){
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts").child(postid);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Event event = dataSnapshot.getValue(Event.class);
                    Glide.with(context).load(event.getPostimage()).into(imageView);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    private void addNotifications(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "Asistirá al evento");
        hashMap.put("postid", postid);
        hashMap.put("tag", "asistir");
        hashMap.put("isPost", true);

        reference.push().setValue(hashMap);
    }



}
