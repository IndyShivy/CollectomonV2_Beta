package com.example.collectomon;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Adapter for the list of artists on the home page
public class ArtistAdapter extends ArrayAdapter<String> {
    private static final String ARTIST_KEY = "artist";
    private final SharedPreferences sharedPreferences;
    ArrayList<String> artistsGlobal;
    Button deleteButton;
    TextView artistName;
    TextView cardCountText;
    String artist;
    CardDatabase db;

    // Constructor
    public ArtistAdapter(Context context, ArrayList<String> artists, SharedPreferences sharedPreferences) {
        super(context, 0, artists);
        this.sharedPreferences = sharedPreferences;
        artistsGlobal = artists;
    }

    // Get the view for each item in the list
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.frag_home_artist_list_item, parent, false);
        }
        artistName = convertView.findViewById(R.id.artist_name);
        deleteButton = convertView.findViewById(R.id.delete_button);
        cardCountText = convertView.findViewById(R.id.card_count);

        artist = getItem(position);
        try {
             db = new CardDatabase(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        int cardCount = db.getCardCountByArtist(artist);
        artistName.setText(artist);
        cardCountText.setTextColor(Color.parseColor("#FFCB05"));
        cardCountText.setText(String.valueOf(cardCount));



//        if (PokeNameHolder.getInstance().getPokemonNames().contains(artist.toLowerCase())) {
//            artistName.setTextColor(Color.parseColor("#FFCB05"));
//        }

        deleteButton.setOnClickListener(v -> {
            String artistToDelete = getItem(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom);
            LayoutInflater backupInflate = LayoutInflater.from(getContext());
            View dialogView = backupInflate.inflate(R.layout.frag_home_backup_restore_dialog, null);
            builder.setView(dialogView);
            TextView title = dialogView.findViewById(R.id.dialog_title);
            title.setText("Delete Artist");
            title.setTextColor(Color.WHITE);
            TextView message = dialogView.findViewById(R.id.dialog_message);
            String textSetter = "Are you sure you want to delete " + "\"" + artistToDelete + "\"" +" ?";
            message.setText(textSetter);
            message.setTextColor(Color.WHITE);  // Set the color
            builder.setPositiveButton("Yes", (dialog, which) -> {
                remove(artistToDelete);
                saveArtistList(artistsGlobal);
                notifyDataSetChanged();
            })
            .setNegativeButton("No", null)
            .show();
        });
//        deleteButton.setOnClickListener(v -> {
//            String artistToDelete = getItem(position);
//            new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom)
//                    .setTitle("Delete Artist")
//                    .setMessage("Are you sure you want to delete " + artistToDelete + "?")
//                    .setPositiveButton("Yes", (dialog, which) -> {
//                        remove(artistToDelete);
//                        saveArtistList(artistsGlobal);
//                        notifyDataSetChanged();
//                    })
//                    .setNegativeButton("No", null)
//                    .show();
//        });
        return convertView;
    }
    // Save the list of artists to shared preferences
    private void saveArtistList(List<String> artistList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = new HashSet<>(artistList);
        editor.putStringSet(ARTIST_KEY, set);
        editor.apply();
    }


}
