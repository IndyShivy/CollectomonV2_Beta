//package com.example.collectomon;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.app.AppCompatDelegate;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.navigation.NavigationBarView;
//
//public class MainActivity extends AppCompatActivity {
//
//    private BottomNavigationView bottomNavigationView;
//    private HomeFragment homeFragment;
//    private SearchFragment searchFragment;
//    private CollectionFragment collectionFragment;
//    @SuppressLint("ObsoleteSdkInt")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.frag_all_bottom_nav_menu);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setNavigationBarColor(getColor(R.color.bottom_bar));
//        }
//
//        bottomNavigationView = findViewById(R.id.navigationView);
//        bottomNavigationView.setOnItemSelectedListener(navItemSelectedListener);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//
//        homeFragment = new HomeFragment();
//        searchFragment = new SearchFragment();
//        collectionFragment = new CollectionFragment();
//
//        Menu menu = bottomNavigationView.getMenu();
//        menu.getItem(0).setIcon(R.drawable.icon_home_filled);
//        bottomNavigationView = findViewById(R.id.navigationView);
//
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragmentContainer, homeFragment, "home")
//                .add(R.id.fragmentContainer, searchFragment, "search")
//                .hide(searchFragment)
//                .add(R.id.fragmentContainer, collectionFragment, "collection")
//                .hide(collectionFragment)
//                .commit();
//
//    }
//
//
//    private final NavigationBarView.OnItemSelectedListener navItemSelectedListener =
//            item -> {
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//
//                    if (item.getItemId() == R.id.menu_home) {
//                        item.setIcon(R.drawable.icon_home_filled);
//                        transaction.show(homeFragment);
//                        transaction.hide(searchFragment);
//                        transaction.hide(collectionFragment);
//                    }
//                    else if (item.getItemId() == R.id.menu_search) {
//                        transaction.show(searchFragment);
//                        transaction.hide(homeFragment);
//                        transaction.hide(collectionFragment);
//                    }
//                    else if (item.getItemId() == R.id.menu_collection) {
//                        transaction.show(collectionFragment);
//                        transaction.hide(homeFragment);
//                        transaction.hide(searchFragment);
//                    } else {
//                        return false;
//                    }
//
//                transaction.commit();
//                updateIcons(item.getItemId()); // A method to update icons based on selection
//                return true;
//            };
//
//    private void updateIcons(int itemId) {
//        Menu menu = bottomNavigationView.getMenu();
//        menu.findItem(R.id.menu_home).setIcon(R.drawable.icon_home_non);
//        menu.findItem(R.id.menu_search).setIcon(R.drawable.icon_search_non);
//        menu.findItem(R.id.menu_collection).setIcon(R.drawable.icon_collection_non);
//
//        if (itemId == R.id.menu_home) {
//            menu.findItem(R.id.menu_home).setIcon(R.drawable.icon_home_filled);
//        } else if (itemId == R.id.menu_search) {
//            menu.findItem(R.id.menu_search).setIcon(R.drawable.icon_search_filled);
//        } else if (itemId == R.id.menu_collection) {
//            menu.findItem(R.id.menu_collection).setIcon(R.drawable.icon_collection_filled);
//        }
//    }
//    private void loadFragment(Fragment fragment) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragmentContainer, fragment);
//        transaction.commit();
//    }
//}


package com.example.collectomon;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_all_bottom_nav_menu);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getColor(R.color.bottom_bar));
        }

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
}


