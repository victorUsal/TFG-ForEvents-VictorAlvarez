package com.victor.forevents.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{


    private Context context;
    private List<Notification> notifications;


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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notification notification = notifications.get(position);


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
                if(notification.isPost()){
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

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_event;
        public TextView username, text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.profile_image);
            post_event = itemView.findViewById(R.id.post_image_event);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
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

}
