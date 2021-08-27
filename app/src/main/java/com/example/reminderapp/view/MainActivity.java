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

import com.example.reminderapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseFirestore firebaseFirestore;
    public String deviceID;
    String device_ID;
    String token =null;
    String name;
    String surname;
    String birthdayDate;
    String time;
    String reminder;
    int day;
    int month;
    int year;




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
        if(token==null) {
            getToken();
        }






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


     void controlID(){


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
                            System.out.println("ThisDeviceId is "+ deviceID);

                            if(device_ID.equals(deviceID)){
                                System.out.println("if "+device_ID);
                                Intent intent = new Intent(MainActivity.this,RemindersActivity.class);
                                startActivity(intent);
                                finish();


                            }

                        }


                    }

                    }


            }
        });


    }


    public void enterClicked(View view) throws ParseException {
        name = binding.nameText.getText().toString();
        surname = binding.surnameText.getText().toString();
        birthdayDate = binding.birthdayDate.getText().toString();
        String[] splitArray= birthdayDate.split("/");
        day= Integer.parseInt(splitArray[0]);
        month= Integer.parseInt(splitArray[1]);
        year=Integer.parseInt(splitArray[2]);


        HashMap<String,Object> postData = new HashMap<>();
        postData.put("token",token);
        postData.put("name",name);
        postData.put("surname",surname);
        postData.put("date",birthdayDate);
        postData.put("deviceID",deviceID);
        postData.put("day",day);
        postData.put("month",month);
        postData.put("year",year);

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
       //birthday();  simdilik calismiyor






    }
    public void birthday() throws ParseException {
        time="23:59";
        reminder= name +" kişisinin doğum günü";
        String title ="birthday";
        String timeFormat = birthdayDate+" "+time;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date parsedDate = dateFormat.parse(timeFormat);
        Timestamp timestamp = new Timestamp(parsedDate.getTime());

        HashMap<String,Object> birthdayData = new HashMap<>();
        birthdayData.put("token",token);
        birthdayData.put("name",name);
        birthdayData.put("surname",surname);
        birthdayData.put("reminderDate",birthdayDate);
        birthdayData.put("deviceID",deviceID);
        birthdayData.put("reminderTime",time);
        birthdayData.put("reminder",reminder);
        birthdayData.put("dateFormat",timestamp);
        birthdayData.put("title",title);
        firebaseFirestore.collection("Reminder").add(birthdayData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }
        });


    }
}