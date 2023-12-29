package com.example.collectomon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

// Fragment for the Collection page
public class CollectionFragment extends Fragment {

    private static final String PREFS_FILE_NAME = "MyPrefsFile";
    private static final String ARTIST_KEY = "artist";
    private CardDatabase db;
    private Context context;
    private RecyclerView recyclerView;
    private CollectionAdapter collectionAdapter;

    TextView loadName;
    ListView loadArtistList;
    Button viewArtistList;
    View overlay, artistView;
    EditText searchEditText;
    String artistSelected;

    // Required empty public constructor
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // When the fragment is resumed, set the back button to go to the HomeFragment
    @Override
    public void onResume() {
        super.onResume();


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Replace the current fragment with HomeFragment
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // When the fragment is hidden, close the ListView if it's open
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            if (loadArtistList.getVisibility() == View.VISIBLE) {
                loadArtistList.setVisibility(View.GONE);
                overlay.setVisibility(View.GONE);
            }

            if (artistSelected != null) {
                List<CardItem> cardItems = db.getCardsByArtist(artistSelected);
                collectionAdapter = new CollectionAdapter(cardItems, context);
                recyclerView.setAdapter(collectionAdapter);
            }
        }
    }

    // When the fragment is created, set up the ListView and RecyclerView
    @SuppressLint({"NotifyDataSetChanged", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = requireContext();
        db = new CardDatabase(context);
        View rootView = inflater.inflate(R.layout.frag_collection, container, false);
        viewArtistList = rootView.findViewById(R.id.artistViewButton);
        loadArtistList = rootView.findViewById(R.id.loadArtistList);
        artistView = rootView.findViewById(R.id.artistView);
        loadName = rootView.findViewById(R.id.loadName);
        overlay = rootView.findViewById(R.id.overlay);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        overlay.setLayoutParams(params);
        searchEditText = rootView.findViewById(R.id.searchEditText1);
        searchEditText.addTextChangedListener(textWatcher);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        collectionAdapter = new CollectionAdapter(new ArrayList<>(), context);
        recyclerView.setAdapter(collectionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        List<String> artistNames = new ArrayList<>();
        Set<String> artistSet = sharedPreferences.getStringSet(ARTIST_KEY, null);
        if (artistSet != null) {
            artistNames = new ArrayList<>(artistSet);
        }
        // Sort the list of artist names
        Collections.sort(artistNames);


        //add select artist to list
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(context, R.layout.frag_search_artist_dropdown, artistNames);
        loadArtistList.setAdapter(listViewAdapter);


        viewArtistList.setOnClickListener(v -> {
            if (loadArtistList.getVisibility() == View.GONE) {
                loadArtistList.setVisibility(View.VISIBLE);
                overlay.setVisibility(View.VISIBLE);
            } else {
                loadArtistList.setVisibility(View.GONE);
                overlay.setVisibility(View.GONE);
            }
        });

        loadArtistList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedArtist = parent.getItemAtPosition(position).toString();
            List<CardItem> cardItems = db.getCardsByArtist(selectedArtist);
            collectionAdapter = new CollectionAdapter(cardItems, context);
            loadName.setText(selectedArtist);
            loadArtistList.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            artistSelected = selectedArtist;
            recyclerView.setAdapter(collectionAdapter);
        });

        overlay.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (loadArtistList.getVisibility() == View.VISIBLE) {
                    loadArtistList.setVisibility(View.GONE);
                    overlay.setVisibility(View.GONE);
                }
            }
            return true;
        });
        return rootView;
    }

    // TextWatcher for the search bar
    private final TextWatcher textWatcher = new TextWatcher() {
        // When the text is changed, filter the list of cards by the search text
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        // When the text is changed, filter the list of cards by the search text
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        // When the text is changed, filter the list of cards by the search text
        @Override
        public void afterTextChanged(Editable s) {
            String searchText = s.toString().toLowerCase().trim();

            if (!searchText.isEmpty()) {
                filterCardItems(searchText);
            } else {
                collectionAdapter.filterList(db.getCardsByArtist(artistSelected));
            }
        }

        // Filter the list of cards by the search text
        private void filterCardItems(String searchText) {
            List<CardItem> filteredList = new ArrayList<>();

            for (CardItem cardItem : db.getCardsByArtist(artistSelected)) {
                if (cardItem.getCardName().toLowerCase().startsWith(searchText.toLowerCase())) {
                    filteredList.add(cardItem);
                }
            }
            collectionAdapter.filterList(filteredList);
        }
    };

}