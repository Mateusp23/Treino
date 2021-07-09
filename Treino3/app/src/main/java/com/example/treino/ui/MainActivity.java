package com.example.treino.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treino.R;
import com.example.treino.data.Connection;
import com.example.treino.data.model.Workout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText editName, editDescription;
    private ListView list;
    private TextView tvHello;
    private ImageView imageUp;

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
        imageUp = findViewById(R.id.imageUp);

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
    public boolean onCreateOptionsMenu(Menu menu) { // criando opções do menu crud
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // validando a opção selecionada
        int id = item.getItemId();

        if(id == R.id.new_menu){ // insert workout
            insertWorkout();
            uploadImage();
            cleanEditTexts();
        } else if ( id == R.id.edit_menu){ // edit workout
            editWorkout();
            cleanEditTexts();
        } else if ( id == R.id.delete_menu){ // delete workout
            deleteWorkout();
            cleanEditTexts();
        } else if ( id == R.id.back_menu){
            onStart();
            Connection.logOut();
            finish();
        }
        return true;
    }

    private void insertWorkout() {
        Workout workout = new Workout();
        if(editName.getText().toString().equals("") || editDescription.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Você deve preencher todos os campos", Toast.LENGTH_LONG).show();
        } else {
            workout.setUid(UUID.randomUUID().toString());
            workout.setName(editName.getText().toString());
            workout.setDescription(editDescription.getText().toString());
            databaseReference.child("Workout").child(workout.getUid()).setValue(workout);
            Toast.makeText(getApplicationContext(), "Treino inserido", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteWorkout() {
        Workout workout = new Workout();
        workout.setUid(workoutSelected.getUid());
        databaseReference.child("Workout").child(workout.getUid()).removeValue();
        Toast.makeText(getApplicationContext(), "Treino removido", Toast.LENGTH_SHORT).show();
    }

    private void editWorkout() {
        Workout workout = new Workout();
        if(editName.getText().toString().equals("") || editDescription.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Você deve preencher todos os campos", Toast.LENGTH_LONG).show();
        } else {
            workout.setUid(workoutSelected.getUid());
            workout.setName(editName.getText().toString().trim());
            workout.setDescription(editDescription.getText().toString().trim());
            databaseReference.child("Workout").child(workout.getUid()).setValue(workout);
            Toast.makeText(getApplicationContext(), "Treino atualizado", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(){
        imageUp.setDrawingCacheEnabled(true);
        imageUp.buildDrawingCache();

        Bitmap bitmap = imageUp.getDrawingCache();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);

        byte [] imageData = baos.toByteArray(); // convertendo baos para pixel

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference image = storageReference.child("images"); // nó do storage

        String imageName = UUID.randomUUID().toString();
        StorageReference referenceImage = image.child(  imageName + ".jpg");

        UploadTask uploadTask = referenceImage.putBytes( imageData ); // objeto que controla o upload
        uploadTask.addOnFailureListener(MainActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(MainActivity.this,
                        "Upload da imagem falhou: "+e.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(MainActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                referenceImage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {
                        Uri url = task.getResult();
                        Toast.makeText(MainActivity.this,
                                "Sucesso ao fazer upload da imagem! ", Toast.LENGTH_LONG).show(); //+url.toString() imprimir o link
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = Connection.getFirebaseAuth();
        userF = Connection.getFirebaseUser();

        checkUser();
    }

    @SuppressLint("SetTextI18n")
    private void checkUser() {
        if( userF == null){
            finish();
        } else {
            tvHello.setText(" Olá, "+userF.getEmail());
        }
    }

    private void cleanEditTexts() { // limpar campos do input
        editName.setText("");
        editDescription.setText("");
    }
}