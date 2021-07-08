package com.example.treino.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.treino.R;
import com.example.treino.data.Connection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class Login extends AppCompatActivity {

    private EditText username, password;
    private Button btnLogin, btnRegister;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        startComponents();
        eventClicks();
    }

    private void eventClicks(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().equals("") || password.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Você deve preencher todos os campos", Toast.LENGTH_LONG).show();
                } else {
                    String email = username.getText().toString().trim();
                    String key_password = password.getText().toString().trim();
                    logar(email, key_password);
                }
            }
        });
    }

    private void logar(String email, String key_password) {
        auth.signInWithEmailAndPassword(email,key_password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                       if ( task.isSuccessful()){
                           Intent i = new Intent(Login.this, MainActivity.class);
                           startActivity(i);
                       } else {
                            alert("e-mail ou senha incorretos");
                       }
                    }
                });
    }

    private void alert(String message) {
        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
    }

    private void startComponents() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = Connection.getFirebaseAuth();
    }
}