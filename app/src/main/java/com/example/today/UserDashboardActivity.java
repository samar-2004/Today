package com.example.today;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class UserDashboardActivity extends AppCompatActivity {


    ActionBar actionBar;
FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashboard);


      actionBar = getSupportActionBar();
      if(actionBar != null) {
          actionBar.setTitle("Profile");
      }

          firebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        if(actionBar != null){
        actionBar.setTitle("Home");}
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment1,"");
        ft1.commit();

    }
    private final BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.nav_home) {
                        if(actionBar != null){
                        actionBar.setTitle("Home");}
                        HomeFragment fragment1 = new HomeFragment();
                        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                        ft1.replace(R.id.content,fragment1,"");
                        ft1.commit();
                        return true;
                    } else if (itemId == R.id.nav_profile) {
                        if(actionBar != null){
                        actionBar.setTitle("Profile");}
                        ProfileFragment fragment2 = new ProfileFragment();
                        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                        ft2.replace(R.id.content,fragment2,"");
                        ft2.commit();
                        return true;
                    } else if (itemId == R.id.nav_users) {
                        if(actionBar != null) {
                        actionBar.setTitle("Users");}
                        UsersFragment fragment3 = new UsersFragment();
                         FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                        ft3.replace(R.id.content,fragment3,"");
                        ft3.commit();
                        return true;
                    }
                    return false;
                }
            };

    private  void CheckUserStatus()
    {
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user!=null)
        {
          //mProfileTv.setText(user.getEmail());
        }
        else {
            startActivity(new Intent(UserDashboardActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        CheckUserStatus();
        super.onStart();
    }
}