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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

// SearchFragment is the second fragment that is displayed when the app is opened
public class SearchFragment extends Fragment {

    ActionBarDrawerToggle drawerToggle;
    private static final String PREFS_FILE_NAME = "MyPrefsFile";
    private static final String ARTIST_KEY = "artist";
    String nonSelected = "No artist selected";


    String artistSelected;
    TextView loadName;
    Context context;
    EditText searchEditText;
    ListView loadArtistList;
    Button viewArtistList;
    View overlay,artistView;
    private CardAdapter cardAdapter;
    private List<CardItem> cardItems;
    private AppCompatActivity activity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Create a new instance of the fragment
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            if (loadArtistList.getVisibility() == View.VISIBLE) {
                loadArtistList.setVisibility(View.GONE);
                overlay.setVisibility(View.GONE);
            }
            if (artistSelected != null) {
                webScrape(artistSelected);
            }
        }
    }

    // TextWatcher for the search bar
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        // Filter the list of cards based on the search text
        @Override
        public void afterTextChanged(Editable s) {
            String searchText = s.toString().toLowerCase().trim();

            if (!searchText.isEmpty()) {
                filterCardItems(searchText);
            } else {
                cardAdapter.filterList(cardItems);
            }
        }
        // Filter the list of cards based on the search text
        private void filterCardItems(String searchText) {
            List<CardItem> filteredList = new ArrayList<>();

            for (CardItem cardItem : cardItems) {
                if (cardItem.getCardName().toLowerCase().startsWith(searchText.toLowerCase())) {
                    filteredList.add(cardItem);
                }
            }
            cardAdapter.filterList(filteredList);
        }
    };

    // Set up the drawer toggle
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Set up the drawer toggle
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            activity = (AppCompatActivity) context;
        }
    }

    // Set up the drawer toggle
    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    // Set up the drawer toggle
    @Override
    public void onResume() {
        super.onResume();

        if (loadArtistList.getVisibility() == View.VISIBLE) {
            loadArtistList.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // OnCreateView for the fragment
    @SuppressLint({"NotifyDataSetChanged", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_search, container, false);
        context = requireContext();
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
        Collections.sort(artistNames);


        searchEditText = rootView.findViewById(R.id.searchCard);
        searchEditText.addTextChangedListener(textWatcher);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        cardItems = new ArrayList<>();
        cardAdapter = new CardAdapter(cardItems, context);
        recyclerView.setAdapter(cardAdapter);


        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(context,R.layout.frag_search_artist_dropdown, artistNames);
        loadArtistList.setAdapter(listViewAdapter);

        // Set the name to none selected at the start
        loadName.setText(nonSelected);

        viewArtistList.setOnClickListener(v -> {
            pulseAnimation(viewArtistList);
            searchEditText.setText("");
            closeKeyboard();
            if(loadArtistList.getVisibility() == View.GONE) {
                loadArtistList.setVisibility(View.VISIBLE);
                overlay.setVisibility(View.VISIBLE);
            } else {
                loadArtistList.setVisibility(View.GONE);
                overlay.setVisibility(View.GONE);
            }
        });
        loadArtistList.setOnItemClickListener((parent, view, position, id) -> {
            String selectedArtist = (String) parent.getItemAtPosition(position);
            artistSelected = selectedArtist;
            loadName.setText(selectedArtist);
            searchEditText.setHint(R.string.search_card);
            loadArtistList.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            //send a toast message to the user
            Toast.makeText(context, "Searching...", Toast.LENGTH_SHORT).show();

            //if no cards are found, send a toast message to the user
            webScrape(selectedArtist);
            closeKeyboard();
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

        // Close the keyboard when the user touches the screen
        rootView.setOnTouchListener((v, event) -> {
            closeKeyboard();
            return false;
        });

        // Close the keyboard when the user touches the screen
        recyclerView.setOnTouchListener((v, event) -> {
            closeKeyboard();
            return false;
        });

        return rootView;
    }


// Web scrape the data from the website
    public void webScrape(String name) {
        cardItems.clear();
        String stringWithoutGaps = name.replaceAll("\\s+", "");
        String modifiedName = stringWithoutGaps.toLowerCase();
        String theLink = "https://www.serebii.net/card/dex/artist/" + modifiedName + ".shtml";
        ArrayList<CardItem> cards = new ArrayList<>();
        System.out.println(theLink);
        @SuppressLint("NotifyDataSetChanged") Thread webScrapingThread = new Thread(() -> {
            try {
                Document doc = Jsoup.connect(theLink).get();
                Element tableElement = doc.select("table.dextable").first();
                assert tableElement != null;
                Elements rowElements = tableElement.select("tr");
                cards.clear();
                cardItems.clear();

                for (int i = 1; i < rowElements.size(); i++) {

                    Element row = rowElements.get(i);
                    Elements columnElements = row.select("td");

                    if (columnElements.size() >= 3 && columnElements.get(0).selectFirst("a img") != null) {
                        Element imageLink = columnElements.get(0).selectFirst("a");
                        String imageSrc = (imageLink != null) ? Objects.requireNonNull(imageLink.selectFirst("img")).attr("src") : "";
                        String imageSrc1 = "https://www.serebii.net/" + imageSrc;
                        Element cardNameElement = columnElements.get(1).selectFirst("font");
                        String cardName = (cardNameElement != null) ? cardNameElement.text() : "";

                        if (cardName.equals("")) {
                            Elements aElements = columnElements.get(1).select("a");
                            cardName = aElements.text();
                        }

                        Element setLink = columnElements.get(2).selectFirst("a");
                        String setDetails = (setLink != null) ? setLink.text() : "";

                        String cardDetails = columnElements.get(2).ownText();
                        String cardId = name + cardName + setDetails + cardDetails;
                        CardItem cardItem = new CardItem(name, cardId, imageSrc1, cardName, setDetails, cardDetails);

                        boolean isDuplicate = false;
                        for (CardItem card : cards) {
                            if (card.getCardId().equals(cardItem.getCardId())) {
                                isDuplicate = true;
                                break;
                            }
                        }

                        if (!isDuplicate) {
                            cards.add(cardItem);
                            cardItems.add(cardItem);
                        }
                    }
                }


                if (activity != null) {
                    activity.runOnUiThread(() -> cardAdapter.notifyDataSetChanged());
                }

            } catch (IOException e) {
                e.printStackTrace();
                if (e instanceof HttpStatusException) {
                    if (activity != null) {
                        activity.runOnUiThread(() -> Toast.makeText(context, "No cards found, check artist name", Toast.LENGTH_SHORT).show());
                    }
                }
            }

        });

        webScrapingThread.start();
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
    private void closeKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

