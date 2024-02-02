package com.example.zerine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.Bundle;
import com.example.zerine.databinding.ActivityMainBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.MenuItem;

import android.os.Bundle;



public class MainNavigation extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        loadFragment(new home_Fragment());
        binding.
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemID()) {
                case R.id.home:
                    loadFragment(new home_Fragment()) ;
                    break;
                case R.id.profile:
                    loadFragment(new profile_Fragment()) ;
                    break;
                case R.id.account:
                    loadFragment(new account_Fragment());
                    break;
                case R.id.settings:
                    loadFragment(new settings_Fragment());
                    break;
            }

            return true;
        });
        }


    private void loadFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}