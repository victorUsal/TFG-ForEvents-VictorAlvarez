package com.victor.forevents.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.victor.forevents.ProfileActivity;
import com.victor.forevents.R;
import com.victor.forevents.model.Tokens;
import com.victor.forevents.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context context;
    private List<User> users;
    public String tag;

    public String mtoken="";
    public String muser = "";

    private FirebaseUser firebaseUser;


    public UserAdapter(Context context, List<User> users,  String tag) {
        this.context = context;
        this.users = users;
        this.tag = tag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = users.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getName());

        Glide.with(context).load(user.getImagen()).into(holder.profile_image);

        isFollowing(user.getId(), holder.btn_follow);
        isInvitation(user.getId(), holder.btn_invitation);
        myToken(user.getId());

        if(!tag.equals("follow")){
            holder.btn_follow.setVisibility(View.GONE);
            holder.btn_invitation.setVisibility(View.VISIBLE);
        }else{
            holder.btn_follow.setVisibility(View.VISIBLE);
            holder.btn_invitation.setVisibility(View.GONE);
        }

        if (user.getId().equals((firebaseUser.getUid())) || user.getId().equals("eWXNrrAlqsaW7jmjq4V3SnyU74N2") || user.getId().equals("MH9jqhjZszNSZtepkRKZNQjd3Of1")) {
            holder.btn_follow.setVisibility(View.GONE);
            holder.btn_invitation.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", user.getId());
                editor.apply();

                 context.startActivity(new Intent (context, ProfileActivity.class));


            }
        });

        holder.btn_invitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_invitation.getText().toString().equals("invitar")) {
                    FirebaseDatabase.getInstance().getReference().child("Invitation").child(tag).child(firebaseUser.getUid()).child("send").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Invitation").child(tag).child(user.getId()).child("receive").child(firebaseUser.getUid()).setValue(true);
                    addNotificationsInvitation(user.getId(), tag);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Invitation").child(tag).child(firebaseUser.getUid()).child("send").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Invitation").child(tag).child(user.getId()).child("receive").child(firebaseUser.getUid()).removeValue();

                }
            }
        });

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_follow.getText().toString().equals("seguir")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);

                    addNotifications(user.getId());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("followers").child(firebaseUser.getUid()).removeValue();

                }
            }
        });


        nombreNotification();

    }

    private void addNotifications(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "Te ha comenzado a seguir");
        hashMap.put("postid", "");
        hashMap.put("tag", "seguir");
        hashMap.put("isPost", false);

        llamarEspecifico(muser, "Te ha comenzado a seguir");
        reference.push().setValue(hashMap);
    }

    private void addNotificationsInvitation(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "Te ha invitado al evento");
        hashMap.put("postid", postid);
        hashMap.put("tag", "invitar");
        hashMap.put("isPost", true);


        reference.push().setValue(hashMap);
    }





    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public TextView fullname;
        public CircleImageView profile_image;
        public Button btn_follow,btn_invitation;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            profile_image = itemView.findViewById(R.id.profile_image);
            btn_follow = itemView.findViewById(R.id.followButton);
            btn_invitation = itemView.findViewById(R.id.invitationButton);

        }
    }

    private void myToken(String userToken){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Tokens").child(userToken);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Tokens token = dataSnapshot.getValue(Tokens.class);
                mtoken = token.getToken();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nombreNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(this == null){
                    return;
                }
                User user = dataSnapshot.getValue(User.class);

                muser = user.getUsername();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void llamarEspecifico(String titulo, String detalle){
        RequestQueue myrequest = Volley.newRequestQueue(context);
        JSONObject json = new JSONObject();

        try{
            json.put("to",mtoken);
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo",titulo);
            notificacion.put("detalle", detalle);

            json.put("data", notificacion);

            String URL = "https://fcm.googleapis.com/fcm/send";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL,json,null,null){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> header = new HashMap<>();

                    header.put("content-type", "application/json");
                    header.put("authorization", "key= AAAA3xj18jc:APA91bEVaKYf4hLZVjdFdl8mOq2tysW7afzZKFET5WMnjZ11FEUy8KytjVntlJE4kLSknq05vWLzyncxLiw8Xi0bMeq0xHpvgpi5-r-tF24h06iRWbHv7iZ9TtezNR-7jjRgcY5wGPGC");
                    return header;
                }
            };

            myrequest.add(request);


        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    private void isFollowing(final String userid, final Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userid).exists()){
                    button.setText("siguiendo");
                    button.setBackground(context.getResources().getDrawable(R.drawable.button_style_following));
                    button.setTextColor(Color.parseColor("#FE2472"));
                }else{
                    button.setText("seguir");
                    button.setBackground(context.getResources().getDrawable(R.drawable.button_style_follow));
                    button.setTextColor(Color.parseColor("#F5F5F1"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isInvitation(final String userid, final Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Invitation").child(tag).child(firebaseUser.getUid()).child("send");

       // FirebaseDatabase.getInstance().getReference().child("Invitation").child(tag).child(firebaseUser.getUid()).child("send").child(user.getId()).setValue(true);

        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userid).exists()){
                    button.setText("invitado");
                    button.setBackground(context.getResources().getDrawable(R.drawable.button_style_following));
                    button.setTextColor(Color.parseColor("#FE2472"));
                }else{
                    button.setText("invitar");
                    button.setBackground(context.getResources().getDrawable(R.drawable.button_style_follow));
                    button.setTextColor(Color.parseColor("#F5F5F1"));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
