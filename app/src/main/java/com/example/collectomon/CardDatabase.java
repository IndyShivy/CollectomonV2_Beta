package com.example.collectomon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

// Database for storing cards
public class CardDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "card_database";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "cards";
    public static final String ARTIST_NAME = "artist_name";
    public static final String CARD_ID = "card_id";
    public static final String COLUMN_IMAGE_SRC = "image_src";
    public static final String COLUMN_CARD_NAME = "card_name";
    public static final String COLUMN_SET_DETAILS = "set_details";
    public static final String COLUMN_CARD_DETAILS = "card_details";
    Context context;
    String dbPath;

    // Constructor
    public CardDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Create the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                ARTIST_NAME + " TEXT, " +
                CARD_ID + " TEXT, " +
                COLUMN_IMAGE_SRC + " TEXT, " +
                COLUMN_CARD_NAME + " TEXT, " +
                COLUMN_SET_DETAILS + " TEXT, " +
                COLUMN_CARD_DETAILS + " TEXT)";
        db.execSQL(createTableQuery);
        dbPath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();

    }

    // Upgrade the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    //save the database (storage)
//    public void saveBackup() {
//        String backupFileName = "CollectomonDatabase.db";
//        File backupFile = new File(context.getExternalFilesDir(null), backupFileName);
//
//        try {
//            SQLiteDatabase db = getWritableDatabase();
//
//            File dbFile = new File(db.getPath());
//            FileInputStream fis = new FileInputStream(dbFile);
//            FileOutputStream fos = new FileOutputStream(backupFile);
//
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = fis.read(buffer)) > 0) {
//                fos.write(buffer, 0, length);
//            }
//
//            fos.flush();
//            fos.close();
//            fis.close();
//
//            Toast.makeText(context,"Database backup created successfully",Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(context,"Failed to create backup",Toast.LENGTH_SHORT).show();
//        }
//    }

    //restore the database
//    public void restoreBackup() {
//        String backupFileName = "CollectomonDatabase.db";
//        File backupFile = new File(context.getExternalFilesDir(null), backupFileName);
//
//        try {
//            SQLiteDatabase db = getWritableDatabase();
//
//            // Clear the existing table
//            db.execSQL("DELETE FROM " + TABLE_NAME);
//
//            FileInputStream fis = new FileInputStream(backupFile);
//            FileOutputStream fos = new FileOutputStream(db.getPath());
//
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = fis.read(buffer)) > 0) {
//                fos.write(buffer, 0, length);
//            }
//
//            fos.flush();
//            fos.close();
//            fis.close();
//
//            Toast.makeText(context,"Database backup restored successfully",Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(context,"Failed to restore database backup, missing backup",Toast.LENGTH_SHORT).show();
//        }
//    }
    //    // Save the database to the Downloads directory
//    public void saveBackup() {
//        String backupFileName = "CollectomonDatabase.db";
//        File backupFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), backupFileName);
//
//        try {
//            SQLiteDatabase db = getWritableDatabase();
//
//            File dbFile = new File(db.getPath());
//            FileInputStream fis = new FileInputStream(dbFile);
//            FileOutputStream fos = new FileOutputStream(backupFile);
//
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = fis.read(buffer)) > 0) {
//                fos.write(buffer, 0, length);
//            }
//
//            fos.flush();
//            fos.close();
//            fis.close();
//
//            Toast.makeText(context,"Backup 'CollectomonDatabase' created in 'Downloads'",Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(context,"Failed to create backup",Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // Restore the database from the Downloads directory
//    public void restoreBackup() {
//        String backupFileName = "CollectomonDatabase.db";
//        File backupFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), backupFileName);
//
//        if (backupFile.exists()) {
//            SQLiteDatabase db = getWritableDatabase();
//            db.close();
//            try {
//                // Reopen the database in write mode
//                db = SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
//
//                // Clear the existing table
//                db.execSQL("DELETE FROM " + TABLE_NAME);
//
//                FileInputStream fis = new FileInputStream(backupFile);
//                FileOutputStream fos = new FileOutputStream(db.getPath());
//
//                byte[] buffer = new byte[1024];
//                int length;
//                while ((length = fis.read(buffer)) > 0) {
//                    fos.write(buffer, 0, length);
//                }
//
//                fos.flush();
//                fos.close();
//                fis.close();
//
//                Toast.makeText(context,"Database backup restored successfully",Toast.LENGTH_SHORT).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(context,"Failed to restore database backup: " + e.getMessage(),Toast.LENGTH_LONG).show();
//                Log.e("Database", "Failed to restore database backup: " + e.getMessage());
//                //Toast.makeText(context,"Failed to restore database backup",Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(context,"Missing backup 'CollectomonDatabase' in 'Downloads' folder",Toast.LENGTH_SHORT).show();
//        }
//    }

    // Save the database to the given FileOutputStream
    public void saveBackup(FileOutputStream fos) {
        // Get a readable database
        SQLiteDatabase db = getReadableDatabase();

        // Get the path of the database file
        String dbPath = db.getPath();

        // Create an input stream for the database file
        try (FileInputStream fis = new FileInputStream(dbPath)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Restore the database from the given FileInputStream
    public void restoreBackup(FileInputStream fis) {
        // Get a writable database
        SQLiteDatabase db = getWritableDatabase();

        // Get the path of the database file
        String dbPath = db.getPath();

        // Create an output stream for the database file
        try (FileOutputStream fos = new FileOutputStream(dbPath)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get all cards by artist
    public ArrayList<CardItem> getCardsByArtist(String artistName) {
        ArrayList<CardItem> cardList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                ARTIST_NAME,
                CARD_ID,
                COLUMN_IMAGE_SRC,
                COLUMN_CARD_NAME,
                COLUMN_SET_DETAILS,
                COLUMN_CARD_DETAILS
        };

        String selection = ARTIST_NAME + " = ?";
        String[] selectionArgs = {artistName};

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME));
                String cardId = cursor.getString(cursor.getColumnIndexOrThrow(CARD_ID));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_SRC));
                String cardName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CARD_NAME));
                String setDetails = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SET_DETAILS));
                String cardDetails = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CARD_DETAILS));

                CardItem cardItem = new CardItem(artist, cardId, imageUrl, cardName, setDetails, cardDetails);
                cardList.add(cardItem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cardList;
    }

    // Add a card to the database
    public void addCard(CardItem card) {
        SQLiteDatabase db = getWritableDatabase();

        if (!isCardIdExists(db, card.getCardId())) {
            ContentValues values = new ContentValues();
            values.put(ARTIST_NAME, card.getArtistName());
            values.put(CARD_ID, card.getCardId());
            values.put(COLUMN_IMAGE_SRC, card.getImageUrl());
            values.put(COLUMN_CARD_NAME, card.getCardName());
            values.put(COLUMN_SET_DETAILS, card.getSetDetails());
            values.put(COLUMN_CARD_DETAILS, card.getCardDetails());
            db.insert(TABLE_NAME, null, values);
        }
        db.close();
    }

    // Delete a card from the database
    public void deleteCard(CardItem card) {
        SQLiteDatabase db = getWritableDatabase();
        String cardId = card.getCardId();
        if (isCardIdExists(db, cardId)) {
            db.delete(TABLE_NAME, CARD_ID + " = ?", new String[]{cardId});
        }
        db.close();
    }

    // Check if a card exists in the database
    private boolean isCardIdExists(SQLiteDatabase db, String cardId) {
        String[] columns = {CARD_ID};
        String selection = CARD_ID + " = ?";
        String[] selectionArgs = {cardId};
        String limit = "1";

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return exists;
    }

    // Check if a card exists in the database
    public boolean isCardExists(String cardId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {CARD_ID};
        String selection = CARD_ID + " = ?";
        String[] selectionArgs = {cardId};
        String limit = "1";
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        assert cursor != null;
        cursor.close();
        db.close();
        return exists;
    }

    // Get all cards from the database
    public ArrayList<CardItem> getAllCards() {
        ArrayList<CardItem> cards = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME));
                String cardId = cursor.getString(cursor.getColumnIndexOrThrow(CARD_ID));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_SRC));
                String cardName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CARD_NAME));
                String setDetails = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SET_DETAILS));
                String cardDetails = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CARD_DETAILS));
                CardItem cardItem = new CardItem(artist, cardId, imageUrl, cardName, setDetails, cardDetails);
                cards.add(cardItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        //sort the cards by card name
        cards.sort((o1, o2) -> o1.getCardName().compareToIgnoreCase(o2.getCardName()));
        return cards;
    }
    public ArrayList<CardItem> getCardsByCardName(String name){
        ArrayList<CardItem> cards = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CARD_NAME + " LIKE '%" + name + "%'", null);
        if (cursor.moveToFirst()) {
            do {
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME));
                String cardId = cursor.getString(cursor.getColumnIndexOrThrow(CARD_ID));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_SRC));
                String cardName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CARD_NAME));
                String setDetails = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SET_DETAILS));
                String cardDetails = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CARD_DETAILS));
                CardItem cardItem = new CardItem(artist, cardId, imageUrl, cardName, setDetails, cardDetails);
                cards.add(cardItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cards;
    }
    public ArrayList<String> getAllArtistNames() {
        ArrayList<String> artistNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + ARTIST_NAME + " FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                String artistName = cursor.getString(0);
                artistNames.add(artistName);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return artistNames;
    }
    public int getCardCountByArtist(String artistName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + ARTIST_NAME + " = ?", new String[]{artistName});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
}

