package com.victor.forevents.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.victor.forevents.R;
import com.victor.forevents.model.Preferencias;

import java.util.List;

public class PreferencesAdapter extends RecyclerView.Adapter<PreferencesAdapter.ViewHolder>{

    public Context context;
    public List<String> preferencias;
    FirebaseUser firebaseUser;

    public PreferencesAdapter(Context context, List<String> preferencias) {
        this.context = context;
        this.preferencias = preferencias;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.preference_item,parent,false);
        return new PreferencesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final String preferencia = preferencias.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (preferencia.equals("")) {
            holder.categoria.setVisibility(View.GONE);
        }else {
            if (preferencia.equals("Acádemico")) {
                holder.image.setImageResource(R.drawable.aca);
            } else if (preferencia.equals("Comunitario")) {
                holder.image.setImageResource(R.drawable.comuni);
            } else if (preferencia.equals("Cultural")) {
                holder.image.setImageResource(R.drawable.cultu);
            } else if (preferencia.equals("Deportivo")) {
                holder.image.setImageResource(R.drawable.deport);
            } else if (preferencia.equals("Empresarial")) {
                holder.image.setImageResource(R.drawable.empresa);
            } else if (preferencia.equals("Ocio")) {
                holder.image.setImageResource(R.drawable.ocio);
            } else if (preferencia.equals("Politico")) {
                holder.image.setImageResource(R.drawable.politico);
            } else if (preferencia.equals("Social")) {
                holder.image.setImageResource(R.drawable.social);
            } else {
                holder.image.setImageResource(R.drawable.ocio);
            }

            isPreferences(preferencia, holder.categoria);

            holder.categoria.setVisibility(View.VISIBLE);
            holder.categoria.setText(preferencia);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    if(holder.categoria.getTag().equals("intereste")){
                        FirebaseDatabase.getInstance().getReference().child("Preferences").child(firebaseUser.getUid()).child(preferencia).setValue(true);
                    }else{
                        FirebaseDatabase.getInstance().getReference().child("Preferences").child(firebaseUser.getUid()).child(preferencia).removeValue();
                    }



                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return preferencias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView categoria;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.category_image);
            categoria = itemView.findViewById(R.id.preferencia_categoria);
        }
    }


    private void isPreferences(final String categoria, final TextView preferencia){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Preferences").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(categoria).exists()){
                    preferencia.setBackground(context.getResources().getDrawable(R.drawable.style_entertainment));
                    preferencia.setTextColor(Color.parseColor("#FFFFFF"));
                    preferencia.setTag("interested");
                }else{
                    preferencia.setTextColor(Color.parseColor("#000000"));
                    preferencia.setBackground(Drawable.createFromPath("#00000000"));
                    preferencia.setTag("intereste");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
