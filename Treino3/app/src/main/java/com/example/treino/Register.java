package com.example.treino;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class Register extends AppCompatActivity {

    private EditText registerEmail, registerPassword;
    private Button btnRegister2, btnBack;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        startComponents();
        eventClicks();
    }

    private void eventClicks() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnRegister2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registerEmail.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();
                createUser(email, password);
            }
        });
    }

    private void createUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            alert("Usuario cadastrado");
                            Intent i = new Intent (Register.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else{
                            alert("Erro ao cadastrar usuario");
                        }
                    }
                });
    }

    private void alert(String message){
        Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
    }

    private void startComponents() {
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        btnRegister2 = findViewById(R.id.btnRegister2);
        btnBack = findViewById(R.id.btnBack);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = Connection.getFirebaseAuth();
    }
}