package com.example.treino;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treino.utils.Connection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText editName, editEmail;
    private ListView list;
    private TextView tvHello;

    private FirebaseAuth auth;
    private FirebaseUser userF;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<User> listUser = new ArrayList<User>();
    private ArrayAdapter<User> arrayAdapterUser;

    User userSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        list = findViewById(R.id.list);
        tvHello = findViewById(R.id.tvHello);

        startFirebase();
        eventDatabase();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userSelected = (User) parent.getItemAtPosition(position);
                editName.setText(userSelected.getNome());
                editEmail.setText(userSelected.getEmail());
            }
        });
    }

    private void eventDatabase() {
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listUser.clear();
                for ( DataSnapshot objSnapshot : snapshot.getChildren() ){
                    User user = objSnapshot.getValue(User.class);
                    listUser.add(user);
                }
                arrayAdapterUser = new ArrayAdapter<User>(MainActivity.this,
                        android.R.layout.simple_list_item_1, listUser);
                list.setAdapter(arrayAdapterUser);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void startFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.new_menu){ // insert user
            User user = new User();
            user.setUid(UUID.randomUUID().toString());
            user.setNome(editName.getText().toString());
            user.setEmail(editEmail.getText().toString());
            databaseReference.child("User").child(user.getUid()).setValue(user);
            Toast.makeText(getApplicationContext(), "USUÁRIO INSERIDO", Toast.LENGTH_SHORT).show();
            cleanEditTexts();
        } else if ( id == R.id.edit_menu){ // edit user
            User user = new User();
            user.setUid(userSelected.getUid());
            user.setNome(editName.getText().toString().trim());
            user.setEmail(editEmail.getText().toString().trim());
            databaseReference.child("User").child(user.getUid()).setValue(user);
            Toast.makeText(getApplicationContext(), "USUÁRIO ATUALIZADO", Toast.LENGTH_SHORT).show();
            cleanEditTexts();
        } else if ( id == R.id.delete_menu){ // delete user
            User user = new User();
            user.setUid(userSelected.getUid());
            databaseReference.child("User").child(user.getUid()).removeValue();
            Toast.makeText(getApplicationContext(), "USUÁRIO REMOVIDO", Toast.LENGTH_SHORT).show();
            cleanEditTexts();
        } else if ( id == R.id.back_menu){
            onStart();
            Connection.logOut();
            finish();
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = Connection.getFirebaseAuth();
        userF = Connection.getFirebaseUser();

        checkUser();
    }

    private void checkUser() {
        if( userF == null){
            finish();
        } else {
            tvHello.setText(" Olá, "+userF.getEmail());
        }
    }

    private void cleanEditTexts() {
        editName.setText("");
        editEmail.setText("");
    }
}