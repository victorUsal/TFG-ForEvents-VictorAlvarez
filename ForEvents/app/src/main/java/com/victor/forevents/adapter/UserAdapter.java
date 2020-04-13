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
import com.victor.forevents.model.User;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context context;
    private List<User> users;

    private FirebaseUser firebaseUser;




    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
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
        if (user.getId().equals((firebaseUser.getUid()))) {
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", user.getId());
                editor.apply();

              //  ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new ProfileFragment()).commit();

                ///**************************************************************************************************************************************************
                ///**************************************************************************************************************************************************
                ///**************************************************************************************************************************************************
                 context.startActivity(new Intent (context, ProfileActivity.class));
                ///**************************************************************************************************************************************************
                ///**************************************************************************************************************************************************
                ///**************************************************************************************************************************************************

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

    }

    private void addNotifications(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "Te ha comenzado a seguir");
        hashMap.put("postid", "");
        hashMap.put("isPost", false);

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
        public Button btn_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            profile_image = itemView.findViewById(R.id.profile_image);
            btn_follow = itemView.findViewById(R.id.followButton);

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
}
