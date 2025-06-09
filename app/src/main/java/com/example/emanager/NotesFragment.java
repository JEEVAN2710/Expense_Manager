package com.example.emanager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.emanager.databinding.FragmentNotesBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesFragment extends Fragment {

    private FragmentNotesBinding binding;
    private NotesAdapter notesAdapter;
    private List<Note> notesList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupFab();
        loadSampleNotes();
    }

    private void setupRecyclerView() {
        notesList = new ArrayList<>();
        notesAdapter = new NotesAdapter(notesList, this::onNoteClick, this::onNoteDelete);
        
        binding.notesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.notesRecyclerView.setAdapter(notesAdapter);
    }

    private void setupFab() {
        binding.addNoteFab.setOnClickListener(v -> showAddNoteDialog());
    }

    private void loadSampleNotes() {
        // Check if data has been reset
        DataManager dataManager = DataManager.getInstance(requireContext());
        if (dataManager.isFirstRunAfterReset()) {
            // Don't load sample data if app was reset
            notesList.clear();
            notesAdapter.notifyDataSetChanged();
            updateEmptyState();
            android.widget.Toast.makeText(getContext(), 
                "📝 Notes have been reset. Create new notes using the + button.", 
                android.widget.Toast.LENGTH_LONG).show();
            return;
        }
        
        // Add some sample notes (only if not reset)
        notesList.add(new Note("💰 Budget Reminder", 
            "Remember to review monthly budget by end of this week. Check if I'm staying within the ₹50,000 limit.", 
            System.currentTimeMillis() - 86400000)); // 1 day ago
        
        notesList.add(new Note("🛒 Shopping List", 
            "Need to buy:\n• Groceries for the week\n• Pay electricity bill\n• Renew insurance policy", 
            System.currentTimeMillis() - 172800000)); // 2 days ago
        
        notesList.add(new Note("📈 Investment Goals", 
            "Start SIP of ₹5000 in mutual funds next month. Research good equity funds with 3-year track record.", 
            System.currentTimeMillis() - 259200000)); // 3 days ago
        
        notesList.add(new Note("🎯 Financial Target", 
            "Save ₹2,00,000 by December 2024 for vacation trip. Current savings: ₹1,20,000. Need ₹80,000 more!", 
            System.currentTimeMillis() - 345600000)); // 4 days ago
        
        notesAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void showAddNoteDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_note, null);
        EditText titleEdit = dialogView.findViewById(R.id.note_title_edit);
        EditText contentEdit = dialogView.findViewById(R.id.note_content_edit);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("📝 Add New Note")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = titleEdit.getText().toString().trim();
                    String content = contentEdit.getText().toString().trim();
                    
                    if (title.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (content.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter some content", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    addNote(title, content);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditNoteDialog(Note note, int position) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_note, null);
        EditText titleEdit = dialogView.findViewById(R.id.note_title_edit);
        EditText contentEdit = dialogView.findViewById(R.id.note_content_edit);
        
        titleEdit.setText(note.getTitle());
        contentEdit.setText(note.getContent());

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("✏️ Edit Note")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String title = titleEdit.getText().toString().trim();
                    String content = contentEdit.getText().toString().trim();
                    
                    if (title.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (content.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter some content", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    updateNote(position, title, content);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addNote(String title, String content) {
        Note newNote = new Note(title, content, System.currentTimeMillis());
        notesList.add(0, newNote); // Add to top
        notesAdapter.notifyItemInserted(0);
        binding.notesRecyclerView.scrollToPosition(0);
        updateEmptyState();
        Toast.makeText(getContext(), "Note added successfully! 📝", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Public method for MainActivity to add a note
     * This allows the main FAB to add notes when in Notes tab
     */
    public void addNoteFromMainActivity(String title, String content) {
        addNote(title, content);
    }

    private void updateNote(int position, String title, String content) {
        Note note = notesList.get(position);
        note.setTitle(title);
        note.setContent(content);
        note.setTimestamp(System.currentTimeMillis());
        notesAdapter.notifyItemChanged(position);
        Toast.makeText(getContext(), "Note updated successfully! ✏️", Toast.LENGTH_SHORT).show();
    }

    private void onNoteClick(Note note, int position) {
        showEditNoteDialog(note, position);
    }

    private void onNoteDelete(Note note, int position) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("🗑️ Delete Note")
                .setMessage("Are you sure you want to delete this note?\n\n\"" + note.getTitle() + "\"")
                .setPositiveButton("Delete", (dialog, which) -> {
                    notesList.remove(position);
                    notesAdapter.notifyItemRemoved(position);
                    updateEmptyState();
                    Toast.makeText(getContext(), "Note deleted! 🗑️", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateEmptyState() {
        if (notesList.isEmpty()) {
            binding.emptyStateLayout.setVisibility(View.VISIBLE);
            binding.notesRecyclerView.setVisibility(View.GONE);
        } else {
            binding.emptyStateLayout.setVisibility(View.GONE);
            binding.notesRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}