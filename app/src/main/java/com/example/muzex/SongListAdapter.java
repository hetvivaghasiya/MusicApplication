package com.example.muzex;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.myViewHolder> {
    Context context;
    ArrayList<SongReceive> song;

    public SongListAdapter(Context context, ArrayList<SongReceive> song) {
        this.context = context;
        this.song = song;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.list_display ,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        SongReceive model= song.get(position);
        int index = position;
        holder.textView.setText(model.getSongName().toString());
        Glide.with(holder.imageView.getContext())
                .load(model.getImageUri()).placeholder(R.drawable.custom_image).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyMediaPlayer.currentIndex = index;
                Intent intent = new Intent(context,SongPlayerActivity.class);
                intent.putExtra("List",song);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return song.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        LinearLayout layout;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.ivSongImage);
            textView=itemView.findViewById(R.id.tvSongName);
            layout=itemView.findViewById(R.id.linear_list);
        }
    }
}
