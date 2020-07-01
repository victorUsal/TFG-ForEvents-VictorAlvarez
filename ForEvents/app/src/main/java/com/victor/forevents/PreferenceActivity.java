package com.victor.forevents;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.victor.forevents.adapter.PreferencesAdapter;
import com.victor.forevents.model.Preferencias;

import java.util.ArrayList;
import java.util.List;

public class PreferenceActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPreference;
    private PreferencesAdapter preferenceAdapter;
    private List<String> preferencias;
    private Button accept_preferences;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        accept_preferences = findViewById(R.id.accept_preferences);

        recyclerViewPreference = findViewById(R.id.recycler_view);
        recyclerViewPreference.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewPreference.setLayoutManager(linearLayoutManager);
        preferencias = new ArrayList<>();
        preferenceAdapter = new PreferencesAdapter(this, preferencias);
        recyclerViewPreference.setAdapter(preferenceAdapter);


        accept_preferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PreferenceActivity.this,HomeActivity.class));
                finish();

            }
        });
        
        thePreference();

    }

    private void thePreference() {
        preferencias.clear();
        preferencias.add("Acádemico");
        preferencias.add("Comunitario");
        preferencias.add("Cultural");
        preferencias.add("Empresarial");
        preferencias.add("Ocio");
        preferencias.add("Politico");
        preferencias.add("Social");

        preferenceAdapter.notifyDataSetChanged();
    }

}
