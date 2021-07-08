package com.example.treino.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treino.R;
import com.example.treino.data.Connection;
import com.example.treino.data.model.Workout;
import com.google.firebase.FirebaseApp;
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

    private EditText editName, editDescription;
    private ListView list;
    private TextView tvHello;

    private FirebaseAuth auth;
    private FirebaseUser userF;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Workout> listWorkout = new ArrayList<Workout>();
    private ArrayAdapter<Workout> workoutArrayAdapter;

    Workout workoutSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = findViewById(R.id.editName);
        editDescription = findViewById(R.id.editDescription);
        list = findViewById(R.id.list);
        tvHello = findViewById(R.id.tvHello);

        startFirebase();
        eventDatabase();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                workoutSelected = (Workout) parent.getItemAtPosition(position);
                editName.setText(workoutSelected.getName());
                editDescription.setText(workoutSelected.getDescription());
            }
        });
    }

    private void eventDatabase() {
        databaseReference.child("Workout").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                listWorkout.clear();
                for ( DataSnapshot objSnapshot : snapshot.getChildren() ){
                    Workout user = objSnapshot.getValue(Workout.class);
                    listWorkout.add(user);
                }
                workoutArrayAdapter = new ArrayAdapter<Workout>(MainActivity.this,
                        android.R.layout.simple_list_item_1, listWorkout);
                list.setAdapter(workoutArrayAdapter);
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

        if(id == R.id.new_menu){ // insert workout
            Workout workout = new Workout();
            workout.setUid(UUID.randomUUID().toString());
            workout.setName(editName.getText().toString());
            workout.setDescription(editDescription.getText().toString());
            databaseReference.child("Workout").child(workout.getUid()).setValue(workout);
            Toast.makeText(getApplicationContext(), "Treino inserido", Toast.LENGTH_SHORT).show();
            cleanEditTexts();
        } else if ( id == R.id.edit_menu){ // edit workout
            Workout workout = new Workout();
            workout.setUid(workoutSelected.getUid());
            workout.setName(editName.getText().toString().trim());
            workout.setDescription(editDescription.getText().toString().trim());
            databaseReference.child("Workout").child(workout.getUid()).setValue(workout);
            Toast.makeText(getApplicationContext(), "Treino atualizado", Toast.LENGTH_SHORT).show();
            cleanEditTexts();
        } else if ( id == R.id.delete_menu){ // delete workout
            Workout workout = new Workout();
            workout.setUid(workoutSelected.getUid());
            databaseReference.child("Workout").child(workout.getUid()).removeValue();
            Toast.makeText(getApplicationContext(), "Treino removido", Toast.LENGTH_SHORT).show();
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
        editDescription.setText("");
    }
}