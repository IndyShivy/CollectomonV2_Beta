package com.example.collectomon;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


//HomeFragment is the first fragment that is displayed when the app is opened
public class HomeFragment extends Fragment{
    public List<String> artistNames;
    private static final String PREFS_FILE_NAME = "MyPrefsFile";
    private static final String ARTIST_KEY = "artist";
    private SharedPreferences sharedPreferences;
    Button backup, restore,addArtistButton;
    CardDatabase db;
    Context context;
    private ArrayAdapter<String> storedArtistNames;
    private AutoCompleteTextView addArtist;

    //stored list of artist names

    String[] artistSuggestions = {
            "0313","Akira Komayama", "Amelicart", "Asako Ito", "Atsuko Nishida", "Chibi",
            "Eske Yoshinob", "Hasuno", "Hataya", "Hyogonosuke", "Kawayoo", "Kiyotaka Oshiyama",
            "Kodama", "Kurumitsu", "Kyoko Umemato", "Lee Hyunjung", "Mahou",
            "Megumi Mizutani", "Miki Kudo", "Miki Tanaka", "Mina Nakai", "Misa Tsutsui",
            "Mitsuhiro Arita", "Mizue", "Naoyo Kimura", "Okacheke", "Ooyama", "Oswaldo Kato",
            "Ryoma Uratsuka", "Saya Tsuruta", "Sekio", "Shibuzoh", "Sowsow", "Sui",
            "Tetsuya Koizumi", "Tika Matsuno", "Tokiya", "You Iribi", "Yuka Morii"
    };

    // Required empty public constructor
    public HomeFragment() {

    }

    // Create a new instance of the fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // Inflate the layout for this fragment
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_home, container, false);

        addArtistButton = view.findViewById(R.id.addArtistButton);
        addArtist = view.findViewById(R.id.searchCard);
        backup = view.findViewById(R.id.backupButton);
        restore = view.findViewById(R.id.restoreButton);
        context = requireContext();
        db = new CardDatabase(context);
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        artistNames = new ArrayList<>();
        storedArtistNames = new ArrayAdapter<>(requireContext(), R.layout.frag_home_artist_list_item, artistNames);

        Set<String> artistSet = sharedPreferences.getStringSet(ARTIST_KEY, null);
        if (artistSet != null) {
            artistNames = new ArrayList<>(artistSet);
        }
        saveArtistList(artistNames);
        ListView listViewArtists = view.findViewById(R.id.listViewArtists);
        storedArtistNames = new ArtistAdapter(context, artistNames, sharedPreferences);
        listViewArtists.setAdapter(storedArtistNames);
        listViewArtists.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        backup.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogCustom);
            LayoutInflater backupInflate = getLayoutInflater();
            View dialogView = backupInflate.inflate(R.layout.frag_home_backup_restore_dialog, null);
            builder.setView(dialogView);
            TextView title = dialogView.findViewById(R.id.dialog_title);
            title.setText("Backup");
            title.setTextColor(Color.WHITE);
            TextView message = dialogView.findViewById(R.id.dialog_message);
            message.setText("Do you want to backup your collection?");
            message.setTextColor(Color.WHITE);  // Set the color
            builder.setPositiveButton("Yes", (dialog, which) -> db.saveBackup())
                    .setNegativeButton("No", null)
                    .show();
        });

        restore.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialogCustom);
            LayoutInflater backupInflate = getLayoutInflater();
            View dialogView = backupInflate.inflate(R.layout.frag_home_backup_restore_dialog, null);
            builder.setView(dialogView);
            TextView title = dialogView.findViewById(R.id.dialog_title);
            title.setText("Restore");
            title.setTextColor(Color.WHITE);
            TextView message = dialogView.findViewById(R.id.dialog_message);
            message.setText("Do you want to restore a previous backup?");
            message.setTextColor(Color.WHITE);  // Set the color

            builder.setPositiveButton("Yes", (dialog, which) -> db.restoreBackup())
                    .setNegativeButton("No", null)
                    .show();
        });


        addArtistButton.setOnClickListener(v -> {
            if (addArtist.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "No artist name", Toast.LENGTH_SHORT).show();
                pulseAnimation(addArtistButton);
            } else {
                String name = addArtist.getText().toString();
                addArtistToList(name);
                pulseAnimation(addArtistButton);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.frag_home_search_box_dropdown_list, artistSuggestions);
        addArtist.setAdapter(adapter);


        addArtist.setOnClickListener(v -> addArtist.showDropDown());
        addArtist.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                addArtist.showDropDown();
            }
        });

        addArtist.addTextChangedListener(new TextWatcher() {

            //
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            //when text is changed, filter the list of suggestions
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence, count -> {
                    int maxItemsToShow = 6; // Max items to show
                    int itemsToShow = Math.min(count, maxItemsToShow);
                    int singleItemHeight = 155;
                    int dropdownHeight = itemsToShow * singleItemHeight;
                    addArtist.setDropDownHeight(dropdownHeight);
                });
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
    }

    //when the fragment is resumed, update the list of artists
    @Override
    public void onResume() {
        super.onResume();
    }

    //add artist to the list of artists
    public void addArtistToList(String name) {
        if (!artistNames.contains(name)) {
            artistNames.add(name);
            storedArtistNames.notifyDataSetChanged();
            addArtist.setText("");

            saveArtistList(artistNames);
            hideKeyboard();

        } else {
            Toast.makeText(context, "Artist " + name + " is already in the list.", Toast.LENGTH_SHORT).show();
            addArtist.setText("");
            hideKeyboard();
        }
    }

    //save the list of artists to shared preferences
    private void saveArtistList(List<String> artistList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        artistList.sort(String::compareToIgnoreCase);
        Set<String> set = new HashSet<>(artistList);
        editor.putStringSet(ARTIST_KEY, set);
        editor.apply();
    }

    //animation for the buttons
    private void pulseAnimation(Button button) {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                button,
                PropertyValuesHolder.ofFloat("scaleX", 1.1f),
                PropertyValuesHolder.ofFloat("scaleY", 1.1f)
        );
        scaleDown.setDuration(500);
        scaleDown.setRepeatCount(ObjectAnimator.RESTART);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();

    }

    //hide the keyboard
    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
