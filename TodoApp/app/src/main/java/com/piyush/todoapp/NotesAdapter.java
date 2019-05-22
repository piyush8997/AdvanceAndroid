package com.piyush.todoapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder>{

    private List<Note> noteList;

    NotesAdapter(List<Note> list){
        this.noteList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_note_item,viewGroup,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.noteTitle.setText(noteList.get(i).getTitle());
        myViewHolder.noteText.setText(noteList.get(i).getText());
    }

    @Override
    public int getItemCount() {
        return (this.noteList != null)?this.noteList.size():0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView noteTitle, noteText;
        LinearLayout noteItemLinearLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.note_item_title);
            noteText = itemView.findViewById(R.id.note_item_text);
            noteItemLinearLayout = itemView.findViewById(R.id.note_item_linear_layout);

            // Item click listener
            noteItemLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();

                    String str = String.format(Locale.US,"%d | %s | %s",
                            noteList.get(pos).getId(),noteList.get(pos).getTitle(),noteList.get(pos).getText());
                    Toast.makeText(view.getContext(), str, Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(view.getContext(), AddNoteActivity.class);
                    i.putExtra("note_id",noteList.get(pos).getId());
                    i.putExtra("note_title",noteList.get(pos).getTitle());
                    i.putExtra("note_text",noteList.get(pos).getText());
                    view.getContext().startActivity(i);
                }
            });
        }
    }

    public void updateDataSet(List<Note> list){
        this.noteList = list;
        notifyDataSetChanged();
    }
}
