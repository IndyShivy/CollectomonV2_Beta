package com.example.collectomon;
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
    String artist;

    // Constructor
    public ArtistAdapter(Context context, ArrayList<String> artists, SharedPreferences sharedPreferences) {
        super(context, 0, artists);
        this.sharedPreferences = sharedPreferences;
        artistsGlobal = artists;
    }

    // Get the view for each item in the list
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.frag_home_artist_list_item, parent, false);
        }
        artistName = convertView.findViewById(R.id.artist_name);
        deleteButton = convertView.findViewById(R.id.delete_button);

        artist = getItem(position);
        artistName.setText(artist);

        // Check if the artist name is a Pokemon name
        if (PokeNameHolder.getInstance().getPokemonNames().contains(artist.toLowerCase())) {
            artistName.setTextColor(Color.parseColor("#FFCB05"));
        }

        deleteButton.setOnClickListener(v -> {
            remove(getItem(position));
            saveArtistList(artistsGlobal);
            notifyDataSetChanged();
        });


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
