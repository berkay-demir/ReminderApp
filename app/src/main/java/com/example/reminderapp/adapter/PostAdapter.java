package com.example.reminderapp.adapter;

import android.graphics.Color;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminderapp.R;
import com.example.reminderapp.databinding.RecyclerRowBinding;
import com.example.reminderapp.model.Post;
import com.example.reminderapp.view.MainActivity;
import com.example.reminderapp.view.RemindersActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    private static String deviceID;
    String deleteController;
    public String docID;
    ArrayList<Post> postArrayList;
     private String[]colors={"#ff99cc","#7d89f5","#497530","#bab400","#079471"};
     private FirebaseFirestore firebaseFirestore;

    public PostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
    }

        public static void main(String[] args){
            RemindersActivity remindersActivity = new RemindersActivity();
            deviceID= remindersActivity.deviceID;

    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        firebaseFirestore =FirebaseFirestore.getInstance();




        return new PostHolder(recyclerRowBinding);

    }



    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.recyclerRowBinding.reminder.setText(postArrayList.get(position).reminder);
        holder.recyclerRowBinding.reminderDate.setText(postArrayList.get(position).reminderDate);
        holder.recyclerRowBinding.reminderTime.setText(postArrayList.get(position).reminderTime);
        holder.recyclerRowBinding.name.setText(postArrayList.get(position).name);
        holder.bind(postArrayList.get(position),colors,position);


        firebaseFirestore.collection("Reminder").whereEqualTo("deleteController",deleteController).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!= null){

                }
                if (value!=null){
                    for (DocumentSnapshot document: value.getDocuments()){
                        docID=document.getId();

                    }
                }
            }
        });

        ImageButton deleter = (ImageButton) holder.itemView.findViewById(R.id.listDeleter);
        deleter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Reminder").document(docID).delete();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        RecyclerRowBinding recyclerRowBinding;
        TextView reminder;
        TextView reminderDate;
        TextView reminderTime;
        TextView name;
        ImageButton deleter;

        public PostHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;




        }

        public void bind(Post post,String [] colors, Integer position){
            itemView.setBackgroundColor(Color.parseColor(colors[position%5]));
            reminder = itemView.findViewById(R.id.reminder);
            reminderDate=itemView.findViewById(R.id.reminderDate);
            reminderTime=itemView.findViewById(R.id.reminderTime);
            name=itemView.findViewById(R.id.name);
            deleter=itemView.findViewById(R.id.listDeleter);
            String sReminder= reminder.getText().toString();
            String sReminderDate=reminderDate.getText().toString();
            String sReminderTime= reminderTime.getText().toString();
            deleteController= sReminder+sReminderDate+sReminderTime+deviceID;

            //deleter=itemView.findViewById(R.id.listDeleter);


        }
    }


}
