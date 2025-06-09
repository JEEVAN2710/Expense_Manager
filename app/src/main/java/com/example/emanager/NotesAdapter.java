package com.example.emanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notesList;
    private OnNoteClickListener onNoteClickListener;
    private OnNoteDeleteListener onNoteDeleteListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    public interface OnNoteClickListener {
        void onNoteClick(Note note, int position);
    }

    public interface OnNoteDeleteListener {
        void onNoteDelete(Note note, int position);
    }

    public NotesAdapter(List<Note> notesList, OnNoteClickListener onNoteClickListener, OnNoteDeleteListener onNoteDeleteListener) {
        this.notesList = notesList;
        this.onNoteClickListener = onNoteClickListener;
        this.onNoteDeleteListener = onNoteDeleteListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notesList.get(position);
        
        holder.titleText.setText(note.getTitle());
        holder.contentText.setText(note.getContent());
        holder.timestampText.setText(dateFormat.format(new Date(note.getTimestamp())));
        
        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (onNoteClickListener != null) {
                onNoteClickListener.onNoteClick(note, position);
            }
        });
        
        holder.deleteButton.setOnClickListener(v -> {
            if (onNoteDeleteListener != null) {
                onNoteDeleteListener.onNoteDelete(note, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView contentText;
        TextView timestampText;
        ImageButton deleteButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.note_title);
            contentText = itemView.findViewById(R.id.note_content);
            timestampText = itemView.findViewById(R.id.note_timestamp);
            deleteButton = itemView.findViewById(R.id.delete_note_btn);
        }
    }
}