package com.example.reminderapp.view;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.reminderapp.MessagingService;
import com.example.reminderapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseFirestore firebaseFirestore;
    public String deviceID;
    String device_ID;
    String token;




    @Override





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();



        setContentView(view);
        firebaseFirestore =FirebaseFirestore.getInstance();
        deviceID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        controlID();
        getToken();





    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()){
                    Log.e(TAG, "Failed to get the token.");
                    return;
                }

                token = task.getResult();
                Log.d(TAG, "Token: "+token);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Failed to get the token: "+e.getLocalizedMessage());
            }
        });


    }


    private void controlID(){

        firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                ArrayList<String> users = new ArrayList<String>();
                for (QueryDocumentSnapshot documentSnapshot: value){
                    if(documentSnapshot.get("deviceID") != null){
                        users.add(documentSnapshot.getString("deviceID"));
                       int size = users.size();

                        for (int i=0; i<size; i++){

                            device_ID=users.get(i);
                            System.out.println("Device ID is: "+device_ID);

                            if(device_ID == deviceID){
                                Intent intent = new Intent(MainActivity.this,RemindersActivity.class);
                                startActivity(intent);
                                finish();
                                break;

                            }

                        }


                    }

                    }


            }
        });


    }

    public void passClicked(View view){
        Intent intent = new Intent(MainActivity.this,RemindersActivity.class);
        startActivity(intent);
        finish();
    }

    public void enterClicked(View view){
        String name = binding.nameText.getText().toString();
        String surname = binding.surnameText.getText().toString();
        String birthdayDate = binding.birthdayDate.getText().toString();







        HashMap<String,Object> postData = new HashMap<>();
       postData.put("token",token);
        postData.put("name",name);
        postData.put("surname",surname);
        postData.put("date",birthdayDate);
        postData.put("deviceID",deviceID);

        firebaseFirestore.collection("Users").document(deviceID).set(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                        Intent intent = new Intent(MainActivity.this,RemindersActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                });





    }
}