package com.example.collectomon;

import java.util.ArrayList;

public class PokeNameHolder {
    private static final PokeNameHolder instance = new PokeNameHolder();
    private ArrayList<String> pokemonNames;

    private PokeNameHolder() {
        pokemonNames = new ArrayList<>();
    }

    public static PokeNameHolder getInstance() {
        return instance;
    }

    public ArrayList<String> getPokemonNames() {
        return pokemonNames;
    }

    public void setPokemonNames(ArrayList<String> pokemonNames) {
        this.pokemonNames = pokemonNames;
    }
}
