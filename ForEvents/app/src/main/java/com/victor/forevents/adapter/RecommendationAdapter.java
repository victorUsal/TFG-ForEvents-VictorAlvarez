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
import com.victor.forevents.PostDetailActivity;
import com.victor.forevents.R;
import com.victor.forevents.model.Event;

import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder>{

    private Context context;
    private List<Event> myRecommendations;

    public RecommendationAdapter(Context context, List<Event> myRecommendations) {
        this.context = context;
        this.myRecommendations = myRecommendations;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recommendation_item,parent,false);

        return new RecommendationAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Event event = myRecommendations.get(position);

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
        return myRecommendations.size();
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
