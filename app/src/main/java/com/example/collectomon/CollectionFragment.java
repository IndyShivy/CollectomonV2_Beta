package com.example.collectomon;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CollectionFragment extends Fragment {

    private static final String PREFS_FILE_NAME = "MyPrefsFile";
    private static final String ARTIST_KEY = "artist";
    private CardDatabase db;
    private Context context;
    private RecyclerView recyclerView;
    private CollectionAdapter collectionAdapter;

    ImageButton deleteCards;
    TextView loadName;
    ListView loadArtistList;
    Button viewArtistList;
    View overlay,artistView;
    EditText searchEditText;
    String artistSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onResume() {
        super.onResume();


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Replace the current fragment with HomeFragment
                getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            // Fragment is no longer hidden

            // Close the ListView if it's open
            if (loadArtistList.getVisibility() == View.VISIBLE) {
                loadArtistList.setVisibility(View.GONE);
                overlay.setVisibility(View.GONE);
            }

            if (artistSelected != null) {
                List<CardItem> cardItems = db.getCardsByArtist(artistSelected);
                collectionAdapter = new CollectionAdapter(cardItems);
                recyclerView.setAdapter(collectionAdapter);
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = requireContext();
        db = new CardDatabase(context);

        View rootView = inflater.inflate(R.layout.frag_collection, container, false);
        deleteCards = rootView.findViewById(R.id.deleteCardButton);
        viewArtistList = rootView.findViewById(R.id.artistViewButton);
        loadArtistList = rootView.findViewById(R.id.loadArtistList);
        artistView = rootView.findViewById(R.id.artistView);
        loadName = rootView.findViewById(R.id.loadName);
        overlay = rootView.findViewById(R.id.overlay);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        overlay.setLayoutParams(params);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        List<String> artistNames = new ArrayList<>();
        Set<String> artistSet = sharedPreferences.getStringSet(ARTIST_KEY, null);
        if (artistSet != null) {
            artistNames = new ArrayList<>(artistSet);
        }
        // Sort the list of artist names
        Collections.sort(artistNames);

        searchEditText = rootView.findViewById(R.id.searchEditText1);
        searchEditText.addTextChangedListener(textWatcher);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        collectionAdapter = new CollectionAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        //add select artist to list
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(context,R.layout.frag_search_artist_dropdown, artistNames);
        loadArtistList.setAdapter(listViewAdapter);


        viewArtistList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle ListView visibility
                if(loadArtistList.getVisibility() == View.GONE) {
                    loadArtistList.setVisibility(View.VISIBLE);
                    overlay.setVisibility(View.VISIBLE);
                } else {
                    loadArtistList.setVisibility(View.GONE);
                    overlay.setVisibility(View.GONE);
                }
            }
        });

//        loadArtistList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // Example action on click: Update TextView with clicked item and hide ListView
//
//                String selectedArtist = (String) parent.getItemAtPosition(position);
//                //artistNameView.setText(selectedArtist);
//                loadName.setText(selectedArtist);
//                loadArtistList.setVisibility(View.GONE);
//                overlay.setVisibility(View.GONE);
//            }
//        });


        loadArtistList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedArtist = parent.getItemAtPosition(position).toString();

            List<CardItem> cardItems = db.getCardsByArtist(selectedArtist);
            collectionAdapter = new CollectionAdapter(cardItems);
            //artistNameView.setText(selectedArtist);
            loadName.setText(selectedArtist);
            loadArtistList.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            //make the loadArtistList position = to the selected artist
            artistSelected = selectedArtist;
            recyclerView.setAdapter(collectionAdapter);
        });

        overlay.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (loadArtistList.getVisibility() == View.VISIBLE) {
                        loadArtistList.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                    }
                }
                return true;
            }
        });


        deleteCards.setOnClickListener(v -> {
            List<CardItem> selectedCardItems = collectionAdapter.getSelectedCardItems();
            db.deleteCards(selectedCardItems);
            List<CardItem> updated = db.getCardsByArtist(artistSelected);
            collectionAdapter = new CollectionAdapter(updated);
            recyclerView.setAdapter(collectionAdapter);
            collectionAdapter.notifyDataSetChanged();
            Toast.makeText(context, "Cards have been removed!", Toast.LENGTH_SHORT).show();
            pulseAnimation();
        });


        return rootView;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            filterCardItems(s.toString());
        }

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

    private void pulseAnimation() {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                deleteCards,
                PropertyValuesHolder.ofFloat("scaleX", 1.1f),
                PropertyValuesHolder.ofFloat("scaleY", 1.1f)
        );
        scaleDown.setDuration(500);
        scaleDown.setRepeatCount(ObjectAnimator.RESTART);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();
    }
}
