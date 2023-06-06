package com.example.nav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.nav.Fragment.Fragment_board;
import com.example.nav.Fragment.Fragment_call;
import com.example.nav.Fragment.Fragment_guide;
import com.example.nav.Fragment.Fragment_home;
import com.example.nav.Fragment.Fragment_shelter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.nav_view);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, new Fragment_home()).commit();

        // Set the "Home" menu item as the default selected item
        bottomNavigationView.setSelectedItemId(R.id.home);

        // Set the item click listener for the BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, new Fragment_home()).commit();
                        break;
                    case R.id.call:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, new Fragment_call()).commit();
                        break;
                    case R.id.shelter:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, new Fragment_shelter()).commit();
                        break;
                    case R.id.guide:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, new Fragment_guide()).commit();
                        break;
                    case R.id.board:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, new Fragment_board()).commit();
                        break;
                }
                return true;
            }
        });
    }
}