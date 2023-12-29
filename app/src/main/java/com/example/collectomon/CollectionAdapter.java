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

import java.util.List;

// Adapter for the list of cards on the search page
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {
    private List<CardItem> cardItems;
    private CardDatabase db;
    private final Context context;

    // Constructor
    public CollectionAdapter(List<CardItem> cardItems,Context context) {
        this.cardItems = cardItems;
        this.context = context;

    }

    // Get the number of items in the list
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_collection_adapter_item, parent, false);
        db = new CardDatabase(view.getContext());
        return new ViewHolder(view);
    }

    // Get the view for each item in the list
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardItem cardItem = cardItems.get(position);

        Picasso.get()
                .load(cardItem.getImageSrc())
                .resize(600, 600)
                .centerInside()
                .into(holder.imageView);

        holder.cardNameTextView.setText(cardItem.getCardName());
        holder.setDetailsTextView.setText(cardItem.getSetDetails());
        holder.cardDetailsTextView.setText(cardItem.getCardDetails());

        holder.removeButton.setOnClickListener(v -> {
                db.deleteCard(cardItem);
                cardItems.remove(cardItem);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());

        });

    }

    // Get the number of items in the list
    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    // Create the view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView cardNameTextView;
        TextView setDetailsTextView;
        TextView cardDetailsTextView;
        Button removeButton;

        // Constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            cardNameTextView = itemView.findViewById(R.id.cardNameTextView);
            setDetailsTextView = itemView.findViewById(R.id.setDetailsTextView);
            cardDetailsTextView = itemView.findViewById(R.id.cardDetailsTextView);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }

    // Filter the list of cards
    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<CardItem> filteredList) {
        ((Activity) context).runOnUiThread(() -> {
            cardItems = filteredList;
            notifyDataSetChanged();
        });
    }
}
