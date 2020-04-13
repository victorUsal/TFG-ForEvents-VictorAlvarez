package com.victor.forevents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.victor.forevents.fragment.CalendarFragment;
import com.victor.forevents.fragment.HomeFragment;
import com.victor.forevents.fragment.NotificationFragment;
import com.victor.forevents.fragment.SearchFragment;

public class HomeActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    FirebaseAuth firebaseAuth;
    public BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = new HomeFragment();

    DrawerLayout drawer;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();

        String id = firebaseAuth.getCurrentUser().getUid();

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        drawer = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


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

            case R.id.nav_configuration:
                selectedFragment = null;
                startActivity(new Intent(HomeActivity.this,OptionsActivity.class));
                break;

        }

        if(selectedFragment != null){
            drawer.closeDrawer(navigationView);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,selectedFragment).commit();
        }
        return true;
    }
}
