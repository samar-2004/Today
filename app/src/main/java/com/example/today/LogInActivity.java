package com.example.today;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class LogInActivity extends AppCompatActivity {
//
//    private static final int RC_SIGN_IN = 100;
//    GoogleSignInClient mGoogleSignInClient;
    TextView mDontHaveAcc, mRecoverPassTv;
    EditText mEmail , mPassword;
    Button mLoginBtn;
    SignInButton mGoogleLoginBtn;

    private FirebaseAuth mAuth;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Login");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mEmail = findViewById(R.id.emailEt);
        mPassword = findViewById(R.id.passwordEt);
        mDontHaveAcc = findViewById(R.id.dont_have_accountTv);
        mLoginBtn = findViewById(R.id.loginBtn);
        mRecoverPassTv = findViewById(R.id.recoverPassTv);
//        mGoogleLoginBtn = findViewById(R.id.googleLoginBtn);

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail().build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        mAuth = FirebaseAuth.getInstance();

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    mEmail.setError("Invalid email");
                    mEmail.setFocusable(true);
                }
                else
                {
                    loginUser(email,password);
                }
            }
        });

        mDontHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this , RegistrationActivity.class));
                finish();
            }
        });

        mRecoverPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPassDialog();
            }
        });

//        mGoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//                startActivityForResult(signInIntent, RC_SIGN_IN);
//            }
//        });

        pd = new ProgressDialog(this);
        pd.setMessage("Logging in...");
    }

    private void showRecoverPassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
       builder.setTitle("Recover Password");
        LinearLayout linearLayout = new LinearLayout(this);

        EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailEt.getText().toString().trim();
                beginRecovery(email);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void beginRecovery(String email) {
        pd.setMessage("Sending email...");
        pd.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task){
                        pd.dismiss();
                        if(task.isSuccessful())
                        {
                            Toast.makeText(LogInActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(LogInActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
                        }
                    
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        pd.dismiss();
                        Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void loginUser(String emaill, String password)
    {

        pd.setMessage("Logging in...");
        pd.show();
        mAuth.signInWithEmailAndPassword(emaill,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {  pd.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();


                            if (user != null)
                            {
                                if (emaill.equals("admin@gmail.com") && password.equals("admin123")) {
                                    startActivity(new Intent(LogInActivity.this, adminDashboardActiviy.class));
                                    finish();
                                } else
                                {
                                    startActivity(new Intent(LogInActivity.this, UserDashboardActivity.class));
                                    finish();
                                }
                            }
                        }
                        else
                        {
                            pd.dismiss();
                            Toast.makeText(LogInActivity.this, "Log in Failed...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(LogInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


}