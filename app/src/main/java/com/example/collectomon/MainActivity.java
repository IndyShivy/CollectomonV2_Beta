package com.example.collectomon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity implements BackupRestoreActions{

    private BottomNavigationView bottomNavigationView;
    private CardDatabase db;
    public static ArrayList<String> pokemonNamesList;
    private static final String PREFS_FILE_NAME = "MyPrefsFile";
    private static final String ARTIST_KEY = "artist";
    private SharedPreferences sharedPreferences;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_all_bottom_nav_menu);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getColor(R.color.bottom_bar));
        }
        db = new CardDatabase(this);
        sharedPreferences = getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            final WindowInsetsController insetsController = getWindow().getInsetsController();
//            if (insetsController != null) {
//                insetsController.hide(WindowInsets.Type.statusBars());
//            }
//        } else {
//            getWindow().setFlags(
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//            );
//        }
        bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnItemSelectedListener(navItemSelectedListener);
        FragmentManager fragmentManager = getSupportFragmentManager();

        HomeFragment homeFragment = new HomeFragment();

        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, homeFragment)
                .commit();
        Menu menu = bottomNavigationView.getMenu();
        menu.getItem(0).setIcon(R.drawable.icon_home_filled);
        bottomNavigationView = findViewById(R.id.navigationView);
        pokemonNamesList = new ArrayList<>();
        fetchPokemonNames();

        // Sort the pokemonNamesList
        Collections.sort(pokemonNamesList);
        PokeNameHolder.getInstance().setPokemonNames(pokemonNamesList);
    }
    private final NavigationBarView.OnItemSelectedListener  navItemSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            Menu menu = bottomNavigationView.getMenu();


            int id = item.getItemId();
            if (id == R.id.menu_home) {
                item.setIcon(R.drawable.icon_home_filled);
                menu.getItem(1).setIcon(R.drawable.icon_search_non);
                menu.getItem(2).setIcon(R.drawable.icon_collection_non);
                fragment = new HomeFragment();
            }
            if (id == R.id.menu_search) {
                item.setIcon(R.drawable.icon_search_filled);
                menu.getItem(0).setIcon(R.drawable.icon_home_non);
                menu.getItem(2).setIcon(R.drawable.icon_collection_non);
                fragment = new SearchFragment();
            }
            if (id == R.id.menu_collection) {
                item.setIcon(R.drawable.icon_collection_filled);
                menu.getItem(0).setIcon(R.drawable.icon_home_non);
                menu.getItem(1).setIcon(R.drawable.icon_search_non);
                fragment = new CollectionFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }

            return false;

        }
    };
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }


    //SaveBackup exception handling
    private final ActivityResultLauncher<Intent> saveBackupResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri uri = data.getData();
                                try {
                                    assert uri != null;
                                    try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w")) {
                                        assert pfd != null;
                                        try (FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor())) {
                                            db.saveBackup(fos);
                                            Toast.makeText(this, "Database backup saved successfully.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(this, "Failed to save database backup: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });

//    //RestoreBackup exception handling
//    private final ActivityResultLauncher<Intent> restoreBackupResultLauncher =
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                    result -> {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            Intent data = result.getData();
//                            if (data != null) {
//                                Uri uri = data.getData();
//                                try {
//                                    assert uri != null;
//                                    try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r")) {
//                                        assert pfd != null;
//                                        try (FileInputStream fis = new FileInputStream(pfd.getFileDescriptor())) {
//                                            db.restoreBackup(fis);
//                                            Toast.makeText(this, "Database backup restored successfully.", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                    Toast.makeText(this, "Failed to restore database backup: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        }
//                    });

    private final ActivityResultLauncher<Intent> restoreBackupResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri uri = data.getData();
                                try {
                                    assert uri != null;
                                    try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r")) {
                                        assert pfd != null;
                                        try (FileInputStream fis = new FileInputStream(pfd.getFileDescriptor())) {
                                            db.restoreBackup(fis);
                                            ArrayList<String> artistNames = db.getAllArtistNames();
                                            HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                                            assert homeFragment != null;
                                            homeFragment.setArtistNames(artistNames);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            Set<String> set = new HashSet<>(artistNames);
                                            editor.putStringSet(ARTIST_KEY, set);
                                            editor.apply();
                                            Toast.makeText(this, "Database backup restored successfully.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(this, "Failed to restore database backup: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
    // Save the database to a user-chosen location
    public void saveBackup() {
        // Get the current time and format it as a string
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        Date now = new Date();
        String timeString = formatter.format(now);

        // Create the filename
        String filename = "Collectomon_" + timeString + ".db";

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // Open all types of files
        intent.putExtra(Intent.EXTRA_TITLE, filename); // Set the filename
        saveBackupResultLauncher.launch(intent);
    }

    // Restore the database from a user-chosen location
    public void restoreBackup() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // Open all types of files
        restoreBackupResultLauncher.launch(intent);
    }
    // Fetch the names of all Pokemon from the PokeAPI
    private void fetchPokemonNames() {
        new Thread(() -> {
            try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                        .url("https://pokeapi.co/api/v2/pokemon?limit=1200")
                        .build();

                Response response = client.newCall(request).execute();
                assert response.body() != null;
                String jsonData = response.body().string();

                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray resultsArray = jsonObject.getJSONArray("results");
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject pokemonObject = resultsArray.getJSONObject(i);
                    String pokemonName = pokemonObject.getString("name");
                    pokemonNamesList.add(pokemonName);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}


