package com.victor.forevents.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.victor.forevents.R;
import com.victor.forevents.model.Event;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public Context context;
    public List<String> categories;
    public  OnCategoryListener mOnCategoryListener;

    public CategoryAdapter(Context context, List<String> categories, OnCategoryListener onCategoryListener) {
        this.context = context;
        this.categories = categories;
        this.mOnCategoryListener = onCategoryListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new CategoryAdapter.ViewHolder(view, mOnCategoryListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String category = categories.get(position);

        if (category.equals("")) {
            holder.name.setVisibility(View.GONE);
        } else {
            if (category.equals("Acádemico")) {
                holder.name.setBackground(context.getResources().getDrawable(R.drawable.style_academic));
                holder.name.setTextColor(Color.parseColor("#FFFFFF"));
            }else if (category.equals("Comunitario")) {
                holder.name.setBackground(context.getResources().getDrawable(R.drawable.style_community));
                holder.name.setTextColor(Color.parseColor("#FFFFFF"));
            }else if (category.equals("Cultural")) {
                holder.name.setBackground(context.getResources().getDrawable(R.drawable.style_cultural));
                holder.name.setTextColor(Color.parseColor("#FFFFFF"));
            }else if (category.equals("Deportivo")) {
                holder.name.setBackground(context.getResources().getDrawable(R.drawable.style_sport));
                holder.name.setTextColor(Color.parseColor("#FFFFFF"));
            }else if (category.equals("Empresarial")) {
                holder.name.setBackground(context.getResources().getDrawable(R.drawable.style_business));
                holder.name.setTextColor(Color.parseColor("#FFFFFF"));
            }else if (category.equals("Ocio")) {
                holder.name.setBackground(context.getResources().getDrawable(R.drawable.style_entertainment));
                holder.name.setTextColor(Color.parseColor("#FFFFFF"));
            }else if (category.equals("Politico")) {
                holder.name.setBackground(context.getResources().getDrawable(R.drawable.style_politician));
                holder.name.setTextColor(Color.parseColor("#FFFFFF"));
            }else if (category.equals("Social")) {
                holder.name.setBackground(context.getResources().getDrawable(R.drawable.style_social));
                holder.name.setTextColor(Color.parseColor("#FFFFFF"));
            }else{
                holder.name.setBackground(context.getResources().getDrawable(R.drawable.style_cultural));
                holder.name.setTextColor(Color.parseColor("#FFFFFF"));
            }

            holder.name.setVisibility(View.VISIBLE);
            holder.name.setText(category);
        }

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView name;
        OnCategoryListener onCategoryListener;

        public ViewHolder(@NonNull View itemView , OnCategoryListener onCategoryListener) {
            super(itemView);

            name = itemView.findViewById(R.id.tematica_evento);
            this.onCategoryListener = onCategoryListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCategoryListener.onCategoryClick(getAdapterPosition());
        }
    }

    public interface OnCategoryListener{
        void onCategoryClick(int position);
    }


}
