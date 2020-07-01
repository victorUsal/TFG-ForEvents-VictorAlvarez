package com.victor.forevents;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;
import com.victor.forevents.model.Event;
import com.victor.forevents.model.Rating;
import com.victor.forevents.model.Tokens;
import com.victor.forevents.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.victor.forevents.adapter.EventAdapter.diaSemana;
import static com.victor.forevents.adapter.EventAdapter.nombreMesCompleto;

public class PostDetailActivity extends AppCompatActivity implements RatingDialogListener {
    String postid;
    private FirebaseUser firebaseUser;
    Event event;

    public String mtoken="";
    public String muser;


    ImageView image_profile, post_image, like,comment, save,invitation;
    TextView username,likes,publisher,titulo,fecha_ini,fecha_fin,hora_ini,hora_fin,ubicacion,descripcion,aforo,tematica,tipo;
    FloatingActionButton btnRating;
    Button btnAsistir;
    TabLayout tabLayout;
    RatingBar ratingBar;
    ImageView more_options;
    ImageButton back;

    LinearLayout info_layout, opiniones_layout;

    FirebaseAnalytics analytics;
    FirebaseDatabase database;
    DatabaseReference ratingTbl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        database = FirebaseDatabase.getInstance();
        ratingTbl = database.getReference("Rating");

        SharedPreferences preferences = this.getSharedPreferences("PREFS", MODE_PRIVATE);
        postid = preferences.getString("postid", "none");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        image_profile = findViewById (R.id.profile_image);
        post_image = findViewById(R.id.post_image_event);
        more_options = findViewById(R.id.more_options);
        back = findViewById(R.id.ic_back);
      //  like = findViewById(R.id.like);
        btnAsistir = findViewById(R.id.button_asistir);
        // likes = itemView.findViewById(R.id.likes);
        comment = findViewById(R.id.comment);
        invitation = findViewById(R.id.invitation);
        save = findViewById(R.id.save);
        username = findViewById(R.id.publisher);
        titulo = findViewById(R.id.titulo_evento);
        fecha_ini =findViewById(R.id.fecha_ini_evento);
        fecha_fin =findViewById(R.id.fecha_fin_evento);
        hora_ini =findViewById(R.id.hora_ini_evento);
        hora_fin =findViewById(R.id.hora_fin_evento);
        ubicacion = findViewById(R.id.ubicacion);
        descripcion= findViewById(R.id.descripcion_evento);
        aforo = findViewById(R.id.aforo_evento);
        tematica= findViewById(R.id.tematica_evento);
        tipo= findViewById(R.id.tipo_evento);
        btnRating =findViewById(R.id.rating);
        ratingBar = findViewById(R.id.ratingBar);

        info_layout = findViewById(R.id.info_layout);
        opiniones_layout = findViewById(R.id.opiniones_layout);

        userInfo();
        readPost();
        getRating(postid);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        more_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(PostDetailActivity.this, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit:
                                editPost(event.getPostid());
                                return true;
                            case R.id.share:
                               /* BitmapDrawable bitmapDrawable = (BitmapDrawable)post_image.getDrawable();
                                if(bitmapDrawable == null){
                                    shareTextOnly(event.getNombre(), event.getDescripcion());
                                }else{
                                    Bitmap bitmap = bitmapDrawable.getBitmap();
                                    //shareImageAndText(event.getNombre(),event.getDescripcion(),bitmap);
                                }
                                */

                                return true;
                            case R.id.delete:
                                FirebaseDatabase.getInstance().getReference("Posts").child(event.getPostid()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(PostDetailActivity.this,"Evento eliminado", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                return true;
                            case R.id.report:
                                reportPost(event.getPostid());
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


        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor =getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid" , event.getPublisher());
                editor.apply();

               startActivity(new Intent(PostDetailActivity.this, ProfileActivity.class));
            }
        });

       username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid" , event.getPublisher());
                editor.apply();

                startActivity(new Intent(PostDetailActivity.this, ProfileActivity.class));
            }
        });



       invitation.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                startActivity( new Intent(PostDetailActivity.this,SearchUserForInvitationActivity.class));
                sendInvitation();
           }
       });



    comment.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PostDetailActivity.this, CommentsActivity.class);
            intent.putExtra("postid", event.getPostid());
            intent.putExtra("publisherid", event.getPublisher());
            startActivity(intent);
        }
    });

     save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(event.getPostid()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(event.getPostid()).removeValue();
                }

            }
        });

       btnAsistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnAsistir.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(event.getPostid()).child(firebaseUser.getUid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Attends").child(firebaseUser.getUid()).child(event.getPostid()).setValue(true);
                    addNotifications(event.getPublisher(), event.getPostid());
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(event.getPostid()).child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Attends").child(firebaseUser.getUid()).child(event.getPostid()).removeValue();
                }
            }
        });


       btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();

            }
        });



        tabLayout = findViewById(R.id.tablayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                                info_layout.setVisibility(View.VISIBLE);
                                opiniones_layout.setVisibility(View.GONE);
                        break;
                    case 1:
                                info_layout.setVisibility(View.GONE);
                                opiniones_layout.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void sendInvitation() {

    }

    private void editPost(final String postid){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Editar descripcion del evento");


        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams lp  = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        getText(postid, editText);

        alertDialog.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("descripcion", editText.getText().toString());

                FirebaseDatabase.getInstance().getReference("Posts").child(postid).updateChildren(hashMap);
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void reportPost(final String postid){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Denunciar el evento");

        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        alertDialog.setPositiveButton("Denunciar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail = "foreventstfg@gmail.com";
                String asunto = "Denuncia evento: "+postid;
                String mensaje = editText.getText().toString();
                dialog.dismiss();

                if(!editText.getText().toString().isEmpty()) {
                    //Enviamos el email
                    JavaMailAPI javaMailAPI = new JavaMailAPI(PostDetailActivity.this, mail, asunto, mensaje);
                    javaMailAPI.execute();

                }else{
                    Toast.makeText(PostDetailActivity.this,"Introducir motivo de la denuncia", Toast.LENGTH_SHORT).show();
                }

            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
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



    private void shareImageAndText(String nombre, String descripcion, Bitmap bitmap) {

        String shareBody = nombre +"\n"+ descripcion;

        Uri uri = saveImageToShare(bitmap);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT,shareBody);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        intent.setType("image/png");
        this.startActivity(Intent.createChooser(intent, "Compartir via "));


    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(this.getCacheDir(), "images");
        Uri uri = null;
        try{
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_event.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "com.victor.forevents.fileprovider", file);

        }catch (Exception e){
            Toast.makeText(PostDetailActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    private void shareTextOnly(String nombre, String descripcion) {
        String shareBody = nombre +"\n"+ descripcion;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        this.startActivity(Intent.createChooser(intent, "Compartir via"));
    }


    private void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(Event.class);
                addvalue();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void addvalue() {
        //FECHA INICIO
        String valor_mes_completo = "";
        valor_mes_completo = nombreMesCompleto(event.getFechaInicio());

        String valor_dia = "";
        valor_dia = diaSemana(event.getFechaInicio());


        String[] fechaDividida = event.getFechaInicio().split("-");
        String anio = fechaDividida[0];
        String dias = fechaDividida[2];

        String valor_mes_completo_fin = "";
        String valor_dia_fin = "";
        String[] fechaDividida_fin;
        String anio_fin="";
        String dias_fin="";

        //FECHA FIN
        if(!event.getFechaFin().equals("Sin determinar"))
        {
            valor_mes_completo_fin = nombreMesCompleto(event.getFechaFin());


            valor_dia_fin = diaSemana(event.getFechaFin());

            fechaDividida_fin = event.getFechaFin().split("-");
             anio_fin = fechaDividida_fin[0];
             dias_fin = fechaDividida_fin[2];

        }

        Glide.with(PostDetailActivity.this).load(event.getPostimage()).into(post_image);

        if (event.getNombre().equals("")) {
            titulo.setVisibility(View.GONE);
        } else {
            titulo.setVisibility(View.VISIBLE);
            titulo.setText(event.getNombre());
        }

        if (event.getDescripcion().equals("")) {
            descripcion.setVisibility(View.GONE);
        } else {
            descripcion.setVisibility(View.VISIBLE);
            descripcion.setText(event.getDescripcion());
        }

        if (event.getUbicacion().equals("")) {
            ubicacion.setVisibility(View.GONE);
        } else {
            ubicacion.setVisibility(View.VISIBLE);
            ubicacion.setText(event.getUbicacion());
        }
        if (event.getFechaInicio().equals("")) {
            fecha_ini.setVisibility(View.GONE);
        } else {
            fecha_ini.setVisibility(View.VISIBLE);
            fecha_ini.setText(valor_dia+ ", "+dias+" "+valor_mes_completo+" "+anio);
        }
        if (event.getFechaFin().equals("")) {
            fecha_fin.setVisibility(View.GONE);
        } else {
            fecha_fin.setVisibility(View.VISIBLE);
            if(!event.getFechaFin().equals("Sin determinar")) {
                fecha_fin.setText(valor_dia_fin + ", " + dias_fin + " " + valor_mes_completo_fin + " " + anio_fin);
            }else{
                fecha_fin.setText(event.getFechaFin());
            }
        }
        if (event.getHoraInicio().equals("")) {
            hora_ini.setVisibility(View.GONE);
        } else {
            hora_ini.setVisibility(View.VISIBLE);
            hora_ini.setText(event.getHoraInicio());
        }
        if (event.getHoraFin().equals("")) {
            hora_fin.setVisibility(View.GONE);
        } else {
            hora_fin.setVisibility(View.VISIBLE);
            hora_fin.setText(event.getHoraFin());
        }
        if (event.getAforo().equals("")) {
            aforo.setVisibility(View.GONE);
        } else {
            aforo.setVisibility(View.VISIBLE);
            aforo.setText(event.getAforo() + " personas.");
        }
        if (event.getTematica().equals("")) {
            tematica.setVisibility(View.GONE);
        } else {
            if (event.getTematica().equals("Acádemico")) {
                tematica.setBackground(getResources().getDrawable(R.drawable.style_academic));
                tematica.setTextColor(Color.parseColor("#FFFFFF"));
            } else if (event.getTematica().equals("Comunitario")) {
                tematica.setBackground(getResources().getDrawable(R.drawable.style_community));
                tematica.setTextColor(Color.parseColor("#FFFFFF"));
            } else if (event.getTematica().equals("Cultural")) {
                tematica.setBackground(getResources().getDrawable(R.drawable.style_cultural));
                tematica.setTextColor(Color.parseColor("#FFFFFF"));
            } else if (event.getTematica().equals("Deportivo")) {
                tematica.setBackground(getResources().getDrawable(R.drawable.style_sport));
                tematica.setTextColor(Color.parseColor("#FFFFFF"));
            } else if (event.getTematica().equals("Empresarial")) {
                tematica.setBackground(getResources().getDrawable(R.drawable.style_business));
                tematica.setTextColor(Color.parseColor("#FFFFFF"));
            } else if (event.getTematica().equals("Ocio")) {
                tematica.setBackground(getResources().getDrawable(R.drawable.style_entertainment));
                tematica.setTextColor(Color.parseColor("#FFFFFF"));
            } else if (event.getTematica().equals("Politico")) {
                tematica.setBackground(getResources().getDrawable(R.drawable.style_politician));
                tematica.setTextColor(Color.parseColor("#FFFFFF"));
            } else if (event.getTematica().equals("Social")) {
                tematica.setBackground(getResources().getDrawable(R.drawable.style_social));
                tematica.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                tematica.setBackground(getResources().getDrawable(R.drawable.style_cultural));
                tematica.setTextColor(Color.parseColor("#FFFFFF"));
            }

            tematica.setVisibility(View.VISIBLE);
            tematica.setText(event.getTematica());
        }

        if (event.getTipo().equals("")) {
            tipo.setVisibility(View.GONE);
        } else {

            if (event.getTipo().equals("Público")) {
                tipo.setBackground(getResources().getDrawable(R.drawable.style_public));
                tipo.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                tipo.setBackground(getResources().getDrawable(R.drawable.style_private));
                tipo.setTextColor(Color.parseColor("#FFFFFF"));
            }

            tipo.setVisibility(View.VISIBLE);
            tipo.setText(event.getTipo());
        }

        if(event.getTipo().equals("Privado") && event.getPublisher().equals(firebaseUser.getUid())){
            invitation.setVisibility(View.VISIBLE);
        }else{
            invitation.setVisibility(View.GONE);
        }

        publisherInfo(image_profile, username, event.getPublisher());


        btnRating.setVisibility(View.VISIBLE);


        isLikes(event.getPostid() , btnAsistir);
        isSaved(event.getPostid() , save);
        isRating(event.getPostid());
        myToken();

    }


    private boolean compararFechas(String fechaInicio) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        final String fecha = dateFormat.format(date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaEvento = sdf.parse(fechaInicio);
        Date actual = sdf.parse(fecha);

        if(actual.compareTo(fechaEvento)> 0){
            return true;
        }else{
            return false;
        }

    }


    private void isLikes (String postid , final Button btnAsistir){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists()){
                    btnAsistir.setBackground(getResources().getDrawable(R.drawable.style_public));
                    btnAsistir.setTextColor(Color.parseColor("#FFFFFF"));
                    btnAsistir.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checked, 0, 0, 0);
                    btnAsistir.setTag("liked");
                }else{
                    btnAsistir.setBackground(getResources().getDrawable(R.drawable.button_style_asistir));
                    btnAsistir.setTextColor(Color.parseColor("#000000"));
                    btnAsistir.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unchecked, 0, 0, 0);
                    btnAsistir.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isRating(final String postid){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Rating").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists()){
                    btnRating.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isSaved(final String postid, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_save_pink);
                    imageView.setTag("saved");
                }else{
                    imageView.setImageResource(R.drawable.ic_bookmark_gray);
                    imageView.setTag("save");
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
        hashMap.put("tag", "asistir");
        hashMap.put("isPost", true);
        reference.push().setValue(hashMap);
        llamarEspecifico(muser,"Asistirá al evento");
        //Toast.makeText(PostDetailActivity.this,mtoken,Toast.LENGTH_SHORT).show();

    }


    private void publisherInfo (final ImageView image_profile, final TextView username , String userid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(PostDetailActivity.this).load(user.getImagen()).into(image_profile);
                username.setText(user.getUsername());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void userInfo(){
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





    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Enviar")
                .setNegativeButtonText("Cancelar")
                .setNoteDescriptions(Arrays.asList("Muy mal", "Mal", "Regular", "Bien", "Excelente"))
                .setDefaultRating(3)
                .setTitle("Puntua esté evento")
                .setDescription("Por favor, evalua este evento para seguir mejorando")
                .setTitleTextColor(R.color.colorBlack)
                .setDescriptionTextColor(R.color.colorBlack)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(PostDetailActivity.this)
                .show();
    }


    private void getRating(String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Rating").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            int numVotaciones = 0, suma = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                        Rating item = postSnapShot.getValue(Rating.class);
                        suma += Integer.parseInt(item.getRateValue());
                        numVotaciones++;

                }
                if(numVotaciones != 0){
                        float media = suma / numVotaciones;
                        ratingBar.setRating(media);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int valor, String comentario) {
        final Rating rating = new Rating(firebaseUser.getUid(),
                postid,
                String.valueOf(valor));

        final DatabaseReference reference = ratingTbl.child(postid);

        reference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                reference.child(firebaseUser.getUid()).setValue(rating);
                btnRating.setVisibility(View.GONE);
                getRating(postid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void llamarEspecifico(String titulo, String detalle){
        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
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


    private void llamartopico(){
        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json = new JSONObject();

        try{

            json.put("to","/topics/"+"enviaratodos");
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo","Soy un titulo");
            notificacion.put("detalle", "Soy un detalle");

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


    private void myToken(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Tokens").child(event.getPublisher());
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





}
