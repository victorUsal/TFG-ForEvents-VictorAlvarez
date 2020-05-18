package com.victor.forevents.adapter;

import android.app.Activity;
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
import com.bumptech.glide.request.RequestOptions;
import com.victor.forevents.PostDetailActivity;
import com.victor.forevents.R;
import com.victor.forevents.model.Event;

import java.util.List;

public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.ViewHolder>{

    private Context context;
    private List<Event> myEvents;

    public MyEventsAdapter(Context context, List<Event> myEvents) {
        this.context = context;
        this.myEvents = myEvents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_events_item , parent, false);

        return new  MyEventsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Event event = myEvents.get(position);

        Glide.with(context).load(event.getPostimage()).into(holder.post_event);
       // Glide.with(context).load(event.getPostimage()).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.post_event);
        holder.titulo.setText(event.getNombre());

        holder.post_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid" , event.getPostid());
                editor.apply();

                ((Activity)context).startActivity(new Intent(((Activity)context), PostDetailActivity.class));

            }
        });


    }

    @Override
    public int getItemCount() {
        return myEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView post_event;
        public TextView titulo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_event = itemView.findViewById(R.id.post_image_event);
            titulo = itemView.findViewById(R.id.titulo_evento);
        }
    }
}
