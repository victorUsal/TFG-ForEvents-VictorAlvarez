package com.victor.forevents.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.victor.forevents.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{

    public Context context;
    public List<Event> events;
    public String tag;

    private FirebaseUser firebaseUser;

    public EventAdapter(Context context, List<Event> events, String tag) {
        this.context = context;
        this.events = events;
        this.tag = tag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Event event = events.get(position);


        String valor_mes = "";
        valor_mes = nombreMes(event.getFechaInicio());

        String valor_mes_completo = "";
        valor_mes_completo = nombreMesCompleto(event.getFechaInicio());


        String[] fechaDividida = event.getFechaInicio().split("/");
        String dias = fechaDividida[0];
        String mes = fechaDividida[1];
        String anio = fechaDividida[2];


        if(tag.equals("attend")){

            String valor_dia = "";
            valor_dia = diaSemana(event.getFechaInicio());

            if(position==0){

                if(event.getFechaInicio().equals("")){
                    holder.fecha_titulo.setVisibility(View.GONE);
                }else {
                    holder.fecha_titulo.setVisibility(View.VISIBLE);
                    holder.fecha_titulo.setText(valor_dia+ ", "+dias+" "+valor_mes_completo+" "+anio);
                }

            }else {
                if (!(events.get(position - 1).getFechaInicio().equals(event.getFechaInicio()))) {

                    if (event.getFechaInicio().equals("")) {
                        holder.fecha_titulo.setVisibility(View.GONE);
                    } else {
                        holder.fecha_titulo.setVisibility(View.VISIBLE);
                        holder.fecha_titulo.setText(valor_dia+ " "+valor_mes+" "+event.getFechaInicio());
                    }

                }

            }

        }else{
            holder.fecha_titulo.setVisibility(View.GONE);
        }


        Glide.with(context).load(event.getPostimage()).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.post_image);

            if (event.getNombre().equals("")) {
                holder.titulo.setVisibility(View.GONE);
            } else {
                holder.titulo.setVisibility(View.VISIBLE);
                holder.titulo.setText(event.getNombre());
            }

            if (event.getFechaInicio().equals("")) {
                holder.dia.setVisibility(View.GONE);
                holder.nombreMes.setVisibility(View.GONE);
            } else {
                holder.dia.setVisibility(View.VISIBLE);
                holder.nombreMes.setVisibility(View.VISIBLE);
                holder.dia.setText(dias);
                holder.nombreMes.setText(valor_mes);
            }

            if (event.getUbicacion().equals("")) {
                holder.ubicacion.setVisibility(View.GONE);
            } else {
                holder.ubicacion.setVisibility(View.VISIBLE);
                holder.ubicacion.setText(event.getUbicacion());
            }
            publisherInfo( holder.image_profile, holder.username, event.getPublisher());


        //isLikes(event.getPostid() , holder.like);
        //numLikes(holder.likes , event.getPostid());
        //isSaved(event.getPostid() , holder.save);


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

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid" , event.getPostid());
                editor.apply();

                context.startActivity(new Intent(context, PostDetailActivity.class));

            }
        });

       /* holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit:
                                editPost(event.getPostid());
                                return true;
                            case R.id.share:
                                BitmapDrawable bitmapDrawable = (BitmapDrawable)holder.post_image.getDrawable();
                                if(bitmapDrawable == null){
                                    shareTextOnly(event.getNombre(), event.getDescripcion());
                                }else{
                                    Bitmap bitmap = bitmapDrawable.getBitmap();
                                    shareImageAndText(event.getNombre(),event.getDescripcion(),bitmap);

                                }

                            return true;
                            case R.id.delete:
                                FirebaseDatabase.getInstance().getReference("Posts").child(event.getPostid()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(context,"Evento eliminado", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                return true;
                            case R.id.report:
                                Toast.makeText(context,"Evento denunciado", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;

                        }
                    }
                });

                popupMenu.inflate(R.menu.post_menu);
                if(!event.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });

        */


    }


    private void shareImageAndText(String nombre, String descripcion, Bitmap bitmap) {

        String shareBody = nombre +"\n"+ descripcion;

        Uri uri = saveImageToShare(bitmap);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT,shareBody);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        intent.setType("image/png");
        context.startActivity(Intent.createChooser(intent, "Compartir via "));


    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try{
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_event.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.victor.forevents.fileprovider", file);

        }catch (Exception e){
            Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    private void shareTextOnly(String nombre, String descripcion) {
            String shareBody = nombre +"\n"+ descripcion;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
            intent.putExtra(Intent.EXTRA_TEXT, shareBody);
            context.startActivity(Intent.createChooser(intent, "Compartir via"));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_image,more, like,comment, save;
        public TextView username,likes,publisher,titulo,fecha,ubicacion,comments,descripcion,fecha_titulo,dia,nombreMes;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById (R.id.profile_image);
            fecha_titulo = itemView.findViewById(R.id.fecha_tv);
            post_image = itemView.findViewById(R.id.post_image_event);
            dia = itemView.findViewById(R.id.dia);
            nombreMes = itemView.findViewById(R.id.nombreMes);
           // like = itemView.findViewById(R.id.like);
           // likes = itemView.findViewById(R.id.likes);
            //comment = itemView.findViewById(R.id.comment);
          //  save = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.publisher);
            titulo = itemView.findViewById(R.id.titulo_evento);
            //fecha =itemView.findViewById(R.id.fecha);
            ubicacion = itemView.findViewById(R.id.ubicacion);
            //more = itemView.findViewById(R.id.more_options);

        }
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


    private void numLikes(final TextView likes, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     likes.setText(dataSnapshot.getChildrenCount()+ " likes ");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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



    private void editPost(final String postid){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Editar Evento");


        final EditText editText = new EditText(context);
        LinearLayout.LayoutParams lp  = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        getText(postid, editText);

        alertDialog.setPositiveButton("Evento editado", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("descripcion", editText.getText().toString());

                FirebaseDatabase.getInstance().getReference("Posts").child(postid).updateChildren(hashMap);
            }
        });

        alertDialog.setNegativeButton("Edicion cancelada", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }


    private void getText(String postid, final EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Event.class).getDescripcion());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static String diaSemana(String fecha) {

        String[] fechaDividida = fecha.split("/");
        int dias = Integer.parseInt(fechaDividida[0]);
        int mes = Integer.parseInt(fechaDividida[1]);
        int anio = Integer.parseInt(fechaDividida[2]);

        String dia="";
        int numD;
        Calendar c = Calendar.getInstance(Locale.US);
        c.set(anio,mes-1,dias);
        numD=c.get(Calendar.DAY_OF_WEEK);
        if(numD == Calendar.SUNDAY)
            dia="Domingo";
        else if(numD == Calendar.MONDAY)
            dia="Lunes";
        else if(numD == Calendar.TUESDAY)
            dia="Martes";
        else if(numD == Calendar.WEDNESDAY)
            dia="Miércoles";
        else if(numD == Calendar.THURSDAY)
            dia="Jueves";
        else if(numD == Calendar.FRIDAY)
            dia="Viernes";
        else if(numD == Calendar.SATURDAY)
            dia="Sábado";
        return dia;
    }

    public static String nombreMes(String fecha){
        String[] fechaDividida = fecha.split("/");
        int dias = Integer.parseInt(fechaDividida[0]);
        int mes = Integer.parseInt(fechaDividida[1]);
        int anio = Integer.parseInt(fechaDividida[2]);
        String nombreMes="";

        if(mes == 1){
            nombreMes = "ENE";
        }else if(mes == 2){
            nombreMes = "FEB";
        }else if(mes == 3){
            nombreMes = "MAR";
        }else if(mes == 4){
            nombreMes = "ABR";
        } else if(mes == 5){
            nombreMes = "MAY";
        }else if(mes == 6){
            nombreMes = "JUN";
        }else if(mes == 7){
            nombreMes = "JUL";
        }else if(mes == 8){
            nombreMes = "AGO";
        }else if(mes == 9){
            nombreMes = "SEP";
        }else if(mes == 10){
            nombreMes = "OCT";
        }else if(mes == 11){
            nombreMes = "NOV";
        }else if(mes == 12){
            nombreMes = "DIC";
        }

        return nombreMes;


    }

    public static String nombreMesCompleto(String fecha){
        String[] fechaDividida = fecha.split("/");
        int dias = Integer.parseInt(fechaDividida[0]);
        int mes = Integer.parseInt(fechaDividida[1]);
        int anio = Integer.parseInt(fechaDividida[2]);
        String nombreMes="";

        if(mes == 1){
            nombreMes = "Enero";
        }else if(mes == 2){
            nombreMes = "Febrero";
        }else if(mes == 3){
            nombreMes = "Marzo";
        }else if(mes == 4){
            nombreMes = "Abril";
        } else if(mes == 5){
            nombreMes = "Mayo";
        }else if(mes == 6){
            nombreMes = "Junio";
        }else if(mes == 7){
            nombreMes = "Julio";
        }else if(mes == 8){
            nombreMes = "Agosto";
        }else if(mes == 9){
            nombreMes = "Septiembre";
        }else if(mes == 10){
            nombreMes = "Octubre";
        }else if(mes == 11){
            nombreMes = "Noviembre";
        }else if(mes == 12){
            nombreMes = "Diciembre";
        }

        return nombreMes;


    }


}

