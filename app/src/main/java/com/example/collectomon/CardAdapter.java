package com.example.collectomon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private List<CardItem> cardItems;
    private final Context context;
    private CardDatabase databaseHelper;
    ArrayList<CardItem> cardStuff;


    public CardAdapter(List<CardItem> cardItems, Context context) {
        this.cardItems = cardItems;
        this.context = context;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<CardItem> filteredList) {
        cardItems = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }



    public List<CardItem> getSelectedCardItems() {
        List<CardItem> selectedItems = new ArrayList<>();
        for (CardItem cardItem : cardItems) {
            if (cardItem.isChecked()) {
                selectedItems.add(cardItem);
                cardItem.setChecked(false);
            }
        }
        return selectedItems;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView cardNameTextView;
        TextView setDetailsTextView;
        TextView cardDetailsTextView;
        CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            cardNameTextView = itemView.findViewById(R.id.cardNameTextView);
            setDetailsTextView = itemView.findViewById(R.id.setDetailsTextView);
            cardDetailsTextView = itemView.findViewById(R.id.cardDetailsTextView);
            checkbox = itemView.findViewById(R.id.checkbox);
            itemView.setOnClickListener(v -> checkbox.setChecked(!checkbox.isChecked()));
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_search_cardadapter_item, parent, false);
//        databaseHelper = new CardDatabase(context);
//        cardStuff = new ArrayList<>();
//        return new ViewHolder(view);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_search_cardadapter_item, parent, false);
        databaseHelper = new CardDatabase(context);
        cardStuff = new ArrayList<>();
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int position = viewHolder.getAdapterPosition();
            CardItem cardItem = cardItems.get(position);
            cardStuff.add(cardItem);
            if (isChecked) {
                // Add the card to the database
                databaseHelper.addCards(cardStuff);
            }
        });

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= cardItems.size()) {
            return;
        }

        CardItem cardItem = cardItems.get(position);
        boolean isChecked = databaseHelper.isCardExists(cardItem.getCardId());
        cardItem.setChecked(isChecked);

        holder.checkbox.setChecked(cardItem.isChecked());

        Picasso.get()
                .load(cardItem.getImageSrc())
                .resize(600, 600)
                .centerInside()
                .into(holder.imageView);

        holder.cardNameTextView.setText(cardItem.getCardName());
        holder.setDetailsTextView.setText(cardItem.getSetDetails());
        holder.cardDetailsTextView.setText(cardItem.getCardDetails());

        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(cardItem.isChecked());

        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked1) -> cardItem.setChecked(isChecked1));
    }
}


