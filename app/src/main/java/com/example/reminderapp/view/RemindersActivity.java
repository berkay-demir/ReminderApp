package com.example.reminderapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.reminderapp.R;
import com.example.reminderapp.adapter.PostAdapter;
import com.example.reminderapp.databinding.ActivityRemindersBinding;
import com.example.reminderapp.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Timestamp;


import java.util.ArrayList;
import java.util.Map;

public class RemindersActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    ArrayList<Post> postArrayList;
    private ActivityRemindersBinding binding;
    PostAdapter postAdapter;
    String deviceID;
    String device_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRemindersBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        firebaseFirestore = FirebaseFirestore.getInstance();
        postArrayList = new ArrayList<>();
        getData();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter= new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);

    }

    private void getData() {


        firebaseFirestore.collection("Reminder").orderBy("dateFormat", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(RemindersActivity.this,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
                if (value != null){
                    for (DocumentSnapshot document : value.getDocuments()){
                        Map<String,Object> data = document.getData();

                        String reminder =(String) data.get("reminder");
                        String reminderDate = (String) data.get("reminderDate");
                        String reminderTime = (String) data.get("reminderTime");
                        String name = (String) data.get("name");
                        String title =(String) data.get("title");
                        String timeFormat = reminderDate+" "+reminderTime;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        Date parsedDate = null;
                        try {
                            parsedDate = dateFormat.parse(timeFormat);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Timestamp timestamp = new Timestamp(parsedDate.getTime());
                        Date date = new Date();
                        Timestamp timestampCurrent = new Timestamp(date.getTime());
                        int control = timestamp.compareTo(timestampCurrent);

                            if (control<0) {

                                String docId = (String) document.getId();
                                firebaseFirestore.collection("Reminder").document(docId).delete();

                                System.out.println(timestamp + " " + timestampCurrent);


                            }




                        Post post = new Post(reminder,reminderDate,reminderTime,name);
                        postArrayList.add(post);


                    }
                    postAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.add_reminder){
            Intent intentToAdd = new Intent(RemindersActivity.this,AddReminderActivity.class);
            startActivity(intentToAdd);
        }
        else if (item.getItemId()==R.id.refresh){
            Intent intentRefresh = new Intent(RemindersActivity.this,RemindersActivity.class);
            startActivity(intentRefresh);
        }
        else if (item.getItemId()==R.id.signOut){
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


                                if(device_ID.equals(deviceID)){
                                    String deleteId =documentSnapshot.getId();
                                    firebaseFirestore.collection("Users").document(deleteId).delete();

                                    Intent intent = new Intent(RemindersActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();


                                }

                            }


                        }

                    }


                }
            });


        }
        return super.onOptionsItemSelected(item);
    }
}