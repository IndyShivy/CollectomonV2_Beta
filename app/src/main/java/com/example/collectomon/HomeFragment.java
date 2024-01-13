package com.example.collectomon;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.Set;



//HomeFragment is the first fragment that is displayed when the app is opened
public class HomeFragment extends Fragment {
    public ArrayList<String> artistNames;
    private static final String PREFS_FILE_NAME = "MyPrefsFile";
    private static final String ARTIST_KEY = "artist";
    private SharedPreferences sharedPreferences;
    Button backup, restore,addArtistButton;
    CardDatabase db;
    Context context;
    private ArrayAdapter<String> storedArtistNames;
    private AutoCompleteTextView addArtist;
    private BackupRestoreActions backupRestoreActions;


    String[] artistSuggestions = {
            "0313","Akira Komayama", "Amelicart", "Asako Ito", "Atsuko Nishida", "Chibi",
            "Eske Yoshinob", "Hasuno", "Hataya", "Hyogonosuke", "Kawayoo","Kedamahadaitai Yawarakai", "Kiyotaka Oshiyama",
            "Kodama", "Kurumitsu", "Kyoko Umemoto", "Lee Hyunjung", "Mahou",
            "Megumi Mizutani", "Miki Kudo", "Miki Tanaka", "Mina Nakai", "Misa Tsutsui",
            "Mitsuhiro Arita", "Mizue", "Naoyo Kimura", "Okacheke", "Ooyama", "Oswaldo Kato",
            "Ryoma Uratsuka", "Saya Tsuruta", "Sekio", "Shibuzoh.", "Sowsow", "Sui","Sumiyoshi Kizuki",
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
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BackupRestoreActions) {
            backupRestoreActions = (BackupRestoreActions) context;
        } else {
            throw new RuntimeException(context + " must implement BackupRestoreActions");
        }
    }

    // Inflate the layout for this fragment
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
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
            builder.setPositiveButton("Yes", (dialog, which) -> backupRestoreActions.saveBackup())
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

            builder.setPositiveButton("Yes", (dialog, which) -> backupRestoreActions.restoreBackup())
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

        //webScrapeSetNames();
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

        // Close the keyboard when the user touches the screen
        view.setOnTouchListener((v, event) -> {
            closeKeyboard();
            return false;
        });

        listViewArtists.setOnTouchListener((v, event) -> {
            closeKeyboard();
            return false;
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
            closeKeyboard();

        } else {
            Toast.makeText(context, "Artist " + name + " is already in the list.", Toast.LENGTH_SHORT).show();
            addArtist.setText("");
            closeKeyboard();
        }
    }

    //save the list of artists to shared preferences
    private void saveArtistList(ArrayList<String> artistList) {
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

    //close the keyboard
    private void closeKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void setArtistNames(ArrayList<String> artistNames) {
        this.artistNames = artistNames;
        artistNames.sort(String::compareToIgnoreCase);
        //remove all the artist from artistNames that is not in the drop down list


        storedArtistNames = new ArtistAdapter(context, artistNames, sharedPreferences);
        ListView listViewArtists = requireView().findViewById(R.id.listViewArtists);
        listViewArtists.setAdapter(storedArtistNames);
    }

//    // Web scrape the set names
//    private void webScrapeSetNames() {
//        new Thread(() -> {
//            try {
//                // The URL of the webpage to scrape
//                String url = "https://www.serebii.net/card/english.shtml";
//
//                // Fetch the HTML code of the webpage
//                Document document = Jsoup.connect(url).get();
//
//                // Select the elements that contain the set names
//                Elements setElements = document.select("td.cen > a");
//
//                // Create an ArrayList to hold the set names
//                setNameList = new ArrayList<>();
//
//                // Iterate over each element and extract the set name
//                for (Element setElement : setElements) {
//                    String setName = setElement.text(); // Extract the text (set name)
//                    setNameList.add(setName); // Add set name to ArrayList
//                }
//                // Print the ArrayList of set names
//                for (String setName : setNameList) {
//                    System.out.println(setName);
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
//    public ArrayList<String> getSetNameList(){
//        return setNameList;
//    }




}
