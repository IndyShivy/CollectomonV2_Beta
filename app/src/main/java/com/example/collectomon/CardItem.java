package com.example.collectomon;

import androidx.annotation.NonNull;

// Class for each card item
public class CardItem {
    private final String artistName;
    private final String cardID;
    private final String imageSrc;
    private final String cardName;
    private final String setDetails;
    private final String cardDetails;
    private boolean isChecked;

    // Constructor
    public CardItem(String artistName, String cardID, String imageSrc, String cardName, String setDetails, String cardDetails) {
        this.artistName = artistName;
        this.cardID = cardID;
        this.imageSrc = imageSrc;
        this.cardName = cardName;
        this.setDetails = setDetails;
        this.cardDetails = cardDetails;
        this.isChecked = false;
    }

    // Getters and setters
    public String getImageSrc() {
        return imageSrc;
    }

    public String getCardName() {
        return cardName;
    }

    public String getSetDetails() {
        return setDetails;
    }

    public String getCardDetails() {
        return cardDetails;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getId() {
        return cardID;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getCardId() {
        return cardID;
    }

    public String getImageUrl() {
        return imageSrc;
    }

    // toString method
    @NonNull
    @Override
    public String toString() {
        return cardID + " " + imageSrc + " " + cardName + " " + setDetails + " " + cardDetails;
    }


}
