package com.example.reminderapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.reminderapp.databinding.ActivityAddReminderBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.Timestamp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;
import java.util.Map;

public class AddReminderActivity extends AppCompatActivity {
    private ActivityAddReminderBinding binding;
    private FirebaseFirestore firebaseFirestore;

    String deviceID;
    String userName;
    String userSurname;
    String token;
    String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddReminderBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        System.out.println(deviceID);


        setContentView(view);
        firebaseFirestore =FirebaseFirestore.getInstance();
        getUserInfo();
    }

    private void getUserInfo() {
        firebaseFirestore.collection("Users").whereEqualTo("deviceID",deviceID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value!= null){
                    for (DocumentSnapshot documentSnapshot:value.getDocuments()){
                        Map<String,Object> data = documentSnapshot.getData();
                        userName = (String) data.get("name");
                        userSurname= (String) data.get("surname");
                        token = (String) data.get("token");

                    }
                }
            }
        });
    }

    public void addReminderClick(View view) throws ParseException {

        String reminder = binding.reminderText.getText().toString();
        String reminderDate = binding.editTextDate.getText().toString();
        String reminderTime = binding.editTextTime.getText().toString();
        String timeFormat = reminderDate+" "+reminderTime;
        title = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date parsedDate = dateFormat.parse(timeFormat);
        Timestamp timestamp = new Timestamp(parsedDate.getTime());

            HashMap<String,Object> postData = new HashMap<>();

            postData.put("reminder",reminder);
            postData.put("reminderDate",reminderDate);
            postData.put("reminderTime",reminderTime);
            postData.put("name",userName);
            postData.put("surname",userSurname);
            postData.put("token",token);
            postData.put("dateFormat",timestamp);
            postData.put("title",title);


            firebaseFirestore.collection("Reminder").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Intent intent = new Intent(AddReminderActivity.this,RemindersActivity.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddReminderActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });






        }
}