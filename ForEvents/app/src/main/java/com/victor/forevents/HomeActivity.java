package com.victor.forevents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.victor.forevents.fragment.CalendarFragment;
import com.victor.forevents.fragment.HomeFragment;
import com.victor.forevents.fragment.NotificationFragment;
import com.victor.forevents.fragment.SearchFragment;
import com.victor.forevents.model.User;

public class HomeActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    FirebaseAuth firebaseAuth;
    public BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = new HomeFragment();

    FirebaseUser firebaseUser;
    DrawerLayout drawer;
    NavigationView navigationView;
    TextView email, fullname;
    ImageView image_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseMessaging.getInstance().subscribeToTopic("enviaratodos").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        drawer = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        email = hView.findViewById(R.id.email);
        fullname = hView.findViewById(R.id.fullname);
        image_profile = hView.findViewById(R.id.image_profile);
        userInfo();

        if (savedInstanceState == null) {
            //Realiza la transacción del Fragmento el cual mediante preferencias podemos determinar cual fue el último.
            Fragment fragment = selectedFragment;

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
        }


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =

            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){

                        case R.id.nav_home:

                            selectedFragment = new HomeFragment();

                            break;

                        case R.id.nav_search:

                            selectedFragment = new SearchFragment();

                            break;

                        case R.id.nav_add:
                            selectedFragment = null;
                            startActivity(new Intent(HomeActivity.this,AddEventActivity.class));
                            break;

                        case R.id.nav_notification:
                            selectedFragment = new NotificationFragment();

                            break;
                        case R.id.nav_calendar:
                            selectedFragment = new CalendarFragment();

                            break;
                    }

                    if(selectedFragment != null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,selectedFragment).commit();
                    }
                    return true;
                }
            };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_profile:
                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                editor.apply();
                selectedFragment = null;
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
                break;

            case R.id.nav_save_events:
                selectedFragment = null;
                startActivity(new Intent(HomeActivity.this,SavedEventsActivity.class));
                break;

            case R.id.nav_personal_events:
                selectedFragment = null;
                startActivity(new Intent(HomeActivity.this,MemoryEventsActivity.class));
                break;

            case R.id.nav_configuration:
                selectedFragment = null;
                startActivity(new Intent(HomeActivity.this,OptionsActivity.class));
                break;

            case R.id.nav_help :
                selectedFragment = null;
                startActivity(new Intent(HomeActivity.this,HelpActivity.class));
                break;

        }

        if(selectedFragment != null){
            drawer.closeDrawer(navigationView);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,selectedFragment).commit();
        }
        return true;
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

                Glide.with(getApplicationContext()).load(user.getImagen()).into(image_profile);
                email.setText(user.getEmail());
                fullname.setText(user.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





}
