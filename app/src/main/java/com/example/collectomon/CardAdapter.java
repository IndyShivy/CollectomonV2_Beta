package com.example.collectomon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// Adapter for the list of cards on the search page
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private ArrayList<CardItem> cardItems;
    private final Context context;
    private CardDatabase db;



    // Constructor
    public CardAdapter(ArrayList<CardItem> cardItems, Context context) {
        this.cardItems = cardItems;
        this.context = context;
    }


    // Filter the list of cards
    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<CardItem> filteredList) {
        ((Activity) context).runOnUiThread(() -> {
            cardItems = filteredList;
            notifyDataSetChanged();
        });
    }

    // Get the number of items in the list
    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    // View holder for each item in the list
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView cardNameTextView;
        TextView setDetailsTextView;
        TextView cardDetailsTextView;
        Button addButton;
        TextView displayAddedText;

        // Constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            cardNameTextView = itemView.findViewById(R.id.cardNameTextView);
            setDetailsTextView = itemView.findViewById(R.id.setDetailsTextView);
            cardDetailsTextView = itemView.findViewById(R.id.cardDetailsTextView);
            addButton = itemView.findViewById(R.id.addButton);
            displayAddedText = itemView.findViewById(R.id.displayAddedText);
        }

    }

    // Create the view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_search_adapter_item, parent, false);
        db = new CardDatabase(context);
        //cardStuff = new ArrayList<>();
        return new ViewHolder(view);
    }

    // Bind the view holder to the card item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= cardItems.size()) {
            return;
        }

        CardItem cardItem = cardItems.get(position);
        boolean isChecked = db.isCardExists(cardItem.getCardId());
        cardItem.setChecked(isChecked);

        if (cardItem.isChecked()) {
            holder.addButton.setVisibility(View.GONE);
            holder.displayAddedText.setVisibility(View.VISIBLE);
        } else {
            holder.addButton.setVisibility(View.VISIBLE);
            holder.displayAddedText.setVisibility(View.GONE);
        }

        Picasso.get()
                .load(cardItem.getImageSrc())
                .resize(600, 600)
                .centerInside()
                .into(holder.imageView);

        holder.cardNameTextView.setText(cardItem.getCardName());
        holder.setDetailsTextView.setText(cardItem.getSetDetails());
        holder.cardDetailsTextView.setText(cardItem.getCardDetails());

        holder.addButton.setOnClickListener(v -> {
                db.addCard(cardItem);
                holder.addButton.setVisibility(View.GONE);
                holder.displayAddedText.setVisibility(View.VISIBLE);
        });
    }
}


