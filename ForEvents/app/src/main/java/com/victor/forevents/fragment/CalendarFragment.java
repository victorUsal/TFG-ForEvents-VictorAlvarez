package com.victor.forevents.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.victor.forevents.R;
import com.victor.forevents.adapter.EventAdapter;
import com.victor.forevents.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CalendarFragment extends Fragment {

    //Attends

    private List<String> myAttends;
    public String selectedDate;


    RecyclerView recyclerViewAttends;
    EventAdapter eventAdapter;
    List<Event> eventList;

    TextView no_calend;
    ImageButton imageButton,calendar;
    DrawerLayout drawer;
    NavigationView navigationView;
    FirebaseUser firebaseUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerViewAttends = view.findViewById(R.id.recycler_view);
        recyclerViewAttends.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewAttends.setLayoutManager(linearLayoutManager);
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(),eventList, "attend");
        recyclerViewAttends.setAdapter(eventAdapter);

        calendar = view.findViewById(R.id.ic_calendar);
        no_calend = view.findViewById(R.id.no_calendar);
        imageButton = (ImageButton)view.findViewById(R.id.ic_menu);
        drawer = (DrawerLayout)getActivity().findViewById(R.id.drawer);
        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(navigationView);

            }
        });


        calendar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                int yy = calendario.get(Calendar.YEAR);
                int mm = calendario.get(Calendar.MONTH);
                int dd = calendario.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog (getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        selectedDate = year  + "-" + twoDigits(monthOfYear+1) + "-" + twoDigits(dayOfMonth);
                        filterEventForDate(selectedDate);
                    }
                },yy,mm,dd);
                datePicker.show();
            }
        });


        myAttends();
        return view;
    }

    private void filterEventForDate(final String selectedDate) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Event event = snapshot.getValue(Event.class);
                    for(String id : myAttends){
                        try {
                            if(event.getPostid().equals(id) && estaFecha(event.getFechaInicio(),selectedDate) ){
                                eventList.add(event);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }


    private void myAttends() {

        myAttends = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Attends").child(firebaseUser.getUid());
        
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myAttends.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    myAttends.add(snapshot.getKey());
                }

                    readAttends();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        
    }

    private void readAttends(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Event event = snapshot.getValue(Event.class);
                            for(String id : myAttends){
                                try {
                                    if(event.getPostid().equals(id) && compararFechas(event.getFechaInicio()) ){
                                        eventList.add(event);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                }
                if(eventList.isEmpty()){
                    no_calend.setVisibility(View.VISIBLE);
                }else{
                    no_calend.setVisibility(View.GONE);
                }

                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private boolean compararFechas(String fechaInicio) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        final String fecha = dateFormat.format(date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date inicio = sdf.parse(fechaInicio);
        Date actual = sdf.parse(fecha);

        if(actual.compareTo(inicio)<= 0){
            return true;
        }else{
            return false;
        }

    }


    private boolean estaFecha(String fechaInicio, String fechaElegida) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date inicio = sdf.parse(fechaInicio);
        Date elegida = sdf.parse(fechaElegida);

        if(elegida.compareTo(inicio)== 0){
            return true;
        }else{
            return false;
        }

    }







}
