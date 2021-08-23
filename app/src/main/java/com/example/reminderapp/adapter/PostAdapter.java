package com.example.reminderapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminderapp.R;
import com.example.reminderapp.databinding.RecyclerRowBinding;
import com.example.reminderapp.model.Post;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
     ArrayList<Post> postArrayList;
     private String[]colors={"#ff99cc","#7d89f5","#497530","#bab400","#079471"};
    public PostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.recyclerRowBinding.reminder.setText(postArrayList.get(position).reminder);
        holder.recyclerRowBinding.reminderDate.setText(postArrayList.get(position).reminderDate);
        holder.recyclerRowBinding.reminderTime.setText(postArrayList.get(position).reminderTime);
        holder.recyclerRowBinding.name.setText(postArrayList.get(position).name);
        holder.bind(postArrayList.get(position),colors,position);

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


        }
    }


}
