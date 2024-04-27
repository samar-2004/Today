package com.example.today;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;


public class MainActivity extends AppCompatActivity {

    AppCompatButton login_btn,signUp_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        login_btn = findViewById(R.id.Login_btn);
         signUp_btn = findViewById(R.id.SignUp_btn);

         signUp_btn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this,RegistrationActivity.class)));

         login_btn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LogInActivity.class)));
    }
}