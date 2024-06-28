package com.example.zerine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import com.example.zerine.databinding.ActivityMainNavigationBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.MenuItem;

import android.os.Bundle;



public class MainNavigation extends AppCompatActivity {

    ActivityMainNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        Intent serviceIntent = new Intent(this, ForegroundServices.class);

        startService(serviceIntent);

        foregroundServiceRunning();



        loadFragment(new home_Fragment());
        binding.bottomNavigationView6.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
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

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(ForegroundServices.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}