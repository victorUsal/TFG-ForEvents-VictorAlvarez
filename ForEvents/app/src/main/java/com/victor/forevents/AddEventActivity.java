package com.victor.forevents;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.victor.forevents.dialog.DatePickerFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AddEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Uri imagenUri;
    String myUrl="";
    StorageTask uploadTask;
    StorageReference storageReference;

    ImageView back, image_added;
    TextView post;
    EditText nombre;
    EditText descripcion;
    EditText fechaInicio;
    EditText horaInicio;
    EditText fechaFin;
    EditText horaFin;
    EditText aforo;
    EditText ubicacion;

    RadioGroup radioGroup;
    RadioButton radioButton;
    Spinner spinner;

    String tipoEvento;
    String tematicaEvento;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        back = findViewById(R.id.ic_back);
        image_added = findViewById(R.id.image_event);
        post = findViewById(R.id.button_add_event);
        nombre = findViewById(R.id.name_event);
        descripcion = findViewById(R.id.description_event);
        fechaInicio = findViewById(R.id.fecha_ini_event);
        fechaFin = findViewById(R.id.fecha_fin_event);
        horaInicio = findViewById(R.id.hora_ini_event);
        horaFin = findViewById(R.id.hora_fin_event);
        ubicacion = findViewById(R.id.ubicacion_event);
        aforo = findViewById(R.id.aforo_event);
        spinner = findViewById(R.id.tematica_event_spinner);
        radioGroup = findViewById(R.id.radioGroup);

        progressBar = findViewById(R.id.progress_circular);

        storageReference = FirebaseStorage.getInstance().getReference("eventos");

        ArrayAdapter<CharSequence> adapter =ArrayAdapter.createFromResource(this, R.array.tematica_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddEventActivity.this, HomeActivity.class));
                finish();
            }
        });


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadEvent();
            }
        });

        image_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(50,70).start(AddEventActivity.this);
            }
        });

        fechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(true);
            }
        });

        fechaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                int yy = calendario.get(Calendar.YEAR);
                int mm = calendario.get(Calendar.MONTH);
                int dd = calendario.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        final String selectedDate = twoDigits(dayOfMonth) + "/" + twoDigits(monthOfYear+1) + "/" + year;
                        fechaFin.setText(selectedDate);
                    }
                }, yy, mm, dd);
                datePicker.show();

            }
        });


        horaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar getDate = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        getDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        getDate.set(Calendar.MINUTE, minute);
                        SimpleDateFormat timeformat=new SimpleDateFormat("HH:mm");
                        String formatedDate = timeformat.format(getDate.getTime());
                        horaInicio.setText(formatedDate);
                    }
                }, getDate.get(Calendar.HOUR_OF_DAY), getDate.get(Calendar.MINUTE), true);

                timePickerDialog.show();
            }
        });

        horaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar getDate = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        getDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        getDate.set(Calendar.MINUTE, minute);
                        SimpleDateFormat timeformat=new SimpleDateFormat("HH:mm");
                        String formatedDate = timeformat.format(getDate.getTime());
                        horaFin.setText(formatedDate);
                    }
                }, getDate.get(Calendar.HOUR_OF_DAY), getDate.get(Calendar.MINUTE), true);

                timePickerDialog.show();
            }
        });

    }


    public void checkButton( View view){

        int radioId = radioGroup.getCheckedRadioButtonId();

            radioButton = findViewById(radioId);
            tipoEvento = radioButton.getText().toString();
            Toast.makeText(this, tipoEvento, Toast.LENGTH_SHORT).show();

    }

    private void showDatePickerDialog(final boolean tipoFecha) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
                if(tipoFecha) {
                    fechaInicio.setText(selectedDate);
                }else{
                    fechaFin.setText(selectedDate);
                }
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }


    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadEvent(){
        //MIRAR PROGREESBARRR
        progressBar.setVisibility(View.VISIBLE);
        if(imagenUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imagenUri));

            uploadTask = fileReference.putFile(imagenUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){


                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        String postid = reference.push().getKey();


                        HashMap<String,Object> hashMap = new HashMap<>();

                        hashMap.put("postid",postid);
                        hashMap.put("postimage",myUrl);
                        hashMap.put("nombre",nombre.getText().toString());
                        hashMap.put("descripcion",descripcion.getText().toString());
                        hashMap.put("fechaInicio",fechaInicio.getText().toString());
                        hashMap.put("horaInicio",horaInicio.getText().toString());
                        hashMap.put("fechaFin",fechaFin.getText().toString());
                        hashMap.put("horaFin",horaFin.getText().toString());
                        hashMap.put("ubicacion",ubicacion.getText().toString());
                        hashMap.put("aforo",aforo.getText().toString());
                        hashMap.put("tematica",tematicaEvento);
                        hashMap.put("tipo",tipoEvento);
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(postid).
                                setValue(hashMap);

                        progressBar.setVisibility(View.GONE);

                        startActivity(new Intent (AddEventActivity.this, HomeActivity.class));
                    }else{
                        Toast.makeText(AddEventActivity.this,"ERROR",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddEventActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(AddEventActivity.this,"No has seleccionado ninguna imagen",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            imagenUri= result.getUri();

            image_added.setImageURI((imagenUri));
        }else{
            Toast.makeText(this, "Ha ocurrido un error!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddEventActivity.this, HomeActivity.class));
            finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tematicaEvento = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
