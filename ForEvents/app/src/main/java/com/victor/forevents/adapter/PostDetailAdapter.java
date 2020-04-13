package com.victor.forevents.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.victor.forevents.model.Event;
import com.victor.forevents.model.User;

import java.util.HashMap;
import java.util.List;

public class PostDetailAdapter extends RecyclerView.Adapter<PostDetailAdapter.ViewHolder>{

    public Context context;
    public List<Event> events;

    private FirebaseUser firebaseUser;

    public PostDetailAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.event_detail_item,parent,false);
       return new PostDetailAdapter.ViewHolder(view);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Event event = events.get(position);

        Glide.with(context).load(event.getPostimage()).into(holder.post_image);

        if (event.getNombre().equals("")) {
            holder.titulo.setVisibility(View.GONE);
        } else {
            holder.titulo.setVisibility(View.VISIBLE);
            holder.titulo.setText(event.getNombre());
        }

        if (event.getDescripcion().equals("")) {
            holder.descripcion.setVisibility(View.GONE);
        } else {
            holder.descripcion.setVisibility(View.VISIBLE);
            holder.descripcion.setText(event.getDescripcion());
        }

        if (event.getUbicacion().equals("")) {
            holder.ubicacion.setVisibility(View.GONE);
        } else {
            holder.ubicacion.setVisibility(View.VISIBLE);
            holder.ubicacion.setText(event.getUbicacion());
        }
        if (event.getFechaInicio().equals("")) {
            holder.fecha_ini.setVisibility(View.GONE);
        } else {
            holder.fecha_ini.setVisibility(View.VISIBLE);
            holder.fecha_ini.setText("Fecha de comienzo : " + event.getFechaInicio()+ " a las ");
        }
        if (event.getFechaFin().equals("")) {
            holder.fecha_fin.setVisibility(View.GONE);
        } else {
            holder.fecha_fin.setVisibility(View.VISIBLE);
            holder.fecha_fin.setText("Fecha de finalización: "+ event.getFechaFin() + " a las ");
        }
        if (event.getHoraInicio().equals("")) {
            holder.hora_ini.setVisibility(View.GONE);
        } else {
            holder.hora_ini.setVisibility(View.VISIBLE);
            holder.hora_ini.setText(event.getHoraInicio()+ " horas");
        }
        if (event.getHoraFin().equals("")) {
            holder.hora_fin.setVisibility(View.GONE);
        } else {
            holder.hora_fin.setVisibility(View.VISIBLE);
            holder.hora_fin.setText(event.getHoraFin() + " horas");
        }
        if (event.getAforo().equals("")) {
            holder.aforo.setVisibility(View.GONE);
        } else {
            holder.aforo.setVisibility(View.VISIBLE);
            holder.aforo.setText("El aforo estimado del evento es de: "+ event.getAforo() +" personas.");
        }
        if (event.getTematica().equals("")) {
            holder.tematica.setVisibility(View.GONE);
        } else {
            if (event.getTematica().equals("Acádemico")) {
                holder.tematica.setBackground(context.getResources().getDrawable(R.drawable.style_academic));
                holder.tematica.setTextColor(Color.parseColor("#FFFFFF"));
            }else if (event.getTematica().equals("Comunitario")) {
                holder.tematica.setBackground(context.getResources().getDrawable(R.drawable.style_community));
                holder.tematica.setTextColor(Color.parseColor("#FFFFFF"));
            }else if (event.getTematica().equals("Cultural")) {
                holder.tematica.setBackground(context.getResources().getDrawable(R.drawable.style_cultural));
                holder.tematica.setTextColor(Color.parseColor("#FFFFFF"));
            }else if (event.getTematica().equals("Deportivo")) {
                holder.tematica.setBackground(context.getResources().getDrawable(R.drawable.style_sport));
                holder.tematica.setTextColor(Color.parseColor("#FFFFFF"));
            }else if (event.getTematica().equals("Empresarial")) {
                holder.tematica.setBackground(context.getResources().getDrawable(R.drawable.style_business));
                holder.tematica.setTextColor(Color.parseColor("#FFFFFF"));
            }else if (event.getTematica().equals("Ocio")) {
                holder.tematica.setBackground(context.getResources().getDrawable(R.drawable.style_entertainment));
                holder.tematica.setTextColor(Color.parseColor("#FFFFFF"));
            }else if (event.getTematica().equals("Politico")) {
                holder.tematica.setBackground(context.getResources().getDrawable(R.drawable.style_politician));
                holder.tematica.setTextColor(Color.parseColor("#FFFFFF"));
            }else if (event.getTematica().equals("Social")) {
                holder.tematica.setBackground(context.getResources().getDrawable(R.drawable.style_social));
                holder.tematica.setTextColor(Color.parseColor("#FFFFFF"));
            }else{
                holder.tematica.setBackground(context.getResources().getDrawable(R.drawable.style_cultural));
                holder.tematica.setTextColor(Color.parseColor("#FFFFFF"));
            }

            holder.tematica.setVisibility(View.VISIBLE);
            holder.tematica.setText(event.getTematica());
        }

        if (event.getTipo().equals("")) {
            holder.tipo.setVisibility(View.GONE);
        } else {

            if (event.getTipo().equals("Público")) {
                holder.tipo.setBackground(context.getResources().getDrawable(R.drawable.style_public));
                holder.tipo.setTextColor(Color.parseColor("#FFFFFF"));
            }else {
                holder.tipo.setBackground(context.getResources().getDrawable(R.drawable.style_private));
                holder.tipo.setTextColor(Color.parseColor("#FFFFFF"));
            }

            holder.tipo.setVisibility(View.VISIBLE);
            holder.tipo.setText(event.getTipo());
        }

        publisherInfo( holder.image_profile, holder.username, event.getPublisher());

        isLikes(event.getPostid() , holder.like);
        isSaved(event.getPostid() , holder.save);


        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid" , event.getPublisher());
                editor.apply();

                context.startActivity(new Intent(context, ProfileActivity.class));
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid" , event.getPublisher());
                editor.apply();

                context.startActivity(new Intent(context, ProfileActivity.class));
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(event.getPostid()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(event.getPostid()).removeValue();
                }

            }
        });


        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(event.getPostid()).child(firebaseUser.getUid()).setValue(true);
                     addNotifications(event.getPublisher(), event.getPostid());
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(event.getPostid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return events.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_image, like,comment, save;
        public TextView username,likes,publisher,titulo,fecha_ini,fecha_fin,hora_ini,hora_fin,ubicacion,comments,descripcion,aforo,tematica,tipo;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById (R.id.profile_image);
            post_image = itemView.findViewById(R.id.post_image_event);
             like = itemView.findViewById(R.id.like);
            // likes = itemView.findViewById(R.id.likes);
            //comment = itemView.findViewById(R.id.comment);
             save = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.publisher);
            titulo = itemView.findViewById(R.id.titulo_evento);
            fecha_ini =itemView.findViewById(R.id.fecha_ini_evento);
            fecha_fin =itemView.findViewById(R.id.fecha_fin_evento);
            hora_ini =itemView.findViewById(R.id.hora_ini_evento);
            hora_fin =itemView.findViewById(R.id.hora_fin_evento);
            ubicacion = itemView.findViewById(R.id.ubicacion);
            descripcion= itemView.findViewById(R.id.descripcion_evento);
            aforo = itemView.findViewById(R.id.aforo_evento);
            tematica= itemView.findViewById(R.id.tematica_evento);
            tipo= itemView.findViewById(R.id.tipo_evento);
        }
    }



    private void isLikes (String postid , final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_like_pink);
                    imageView.setTag("liked");
                }else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
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
        hashMap.put("isPost", true);

        reference.push().setValue(hashMap);
    }


    private void publisherInfo (final ImageView image_profile, final TextView username , String userid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(context).load(user.getImagen()).into(image_profile);
                username.setText(user.getUsername());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void isSaved(final String postid, final ImageView imageView){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_save_pink);
                    imageView.setTag("saved");
                }else{
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
