package com.example.locationpinnedapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LocationDatabaseHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Set the Toolbar as the app's action bar
        setSupportActionBar(toolbar);

        // Set the title for the Toolbar
        getSupportActionBar().setTitle("PinnedLocations");

        // Initialize the database and create a database instance
        dbHelper = new LocationDatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        // Call the GeocodingHelper to read the input file and perform geocoding
        List<GeocodedLocation> geocodedLocations = GeocodingHelper.geocodeLocationsFromAssets(this, "locations.txt");

        // Insert the geocoded locations into the database
        for (GeocodedLocation location : geocodedLocations) {
            insertLocation(location);
        }

        // Display the geocoded locations in a TextView
        displayGeocodedLocations();

        // Initialize UI elements for add, delete, update, and query
        EditText newAddressEditText = findViewById(R.id.newAddressEditText);
        Button addEntryButton = findViewById(R.id.addEntryButton);
        EditText deleteAddressEditText = findViewById(R.id.deleteAddressEditText);
        Button deleteEntryButton = findViewById(R.id.deleteEntryButton);
        EditText updateAddressEditText = findViewById(R.id.updateAddressEditText);
        EditText updateLatitudeEditText = findViewById(R.id.updateLatitudeEditText);
        EditText updateLongitudeEditText = findViewById(R.id.updateLongitudeEditText);
        Button updateEntryButton = findViewById(R.id.updateEntryButton);
        EditText queryAddressEditText = findViewById(R.id.queryAddressEditText);
        Button queryButton = findViewById(R.id.queryButton);
        TextView queryResultTextView = findViewById(R.id.queryResultTextView);

        // Handle the add entry button click
        addEntryButton.setOnClickListener(view -> {
            String newAddress = newAddressEditText.getText().toString();

            if (!newAddress.isEmpty()) {
                GeocodedLocation newLocation = new GeocodedLocation(newAddress, 0.0, 0.0); // You can set initial values for latitude and longitude.
                insertLocation(newLocation);
                refreshGeocodedLocations();
            }
        });

        // Handle the delete entry button click
        deleteEntryButton.setOnClickListener(view -> {
            String deleteAddress = deleteAddressEditText.getText().toString();

            if (!deleteAddress.isEmpty()) {
                deleteLocationByAddress(deleteAddress);
                refreshGeocodedLocations();
            }
        });

        // Handle the update entry button click
        updateEntryButton.setOnClickListener(view -> {
            String updateAddress = updateAddressEditText.getText().toString();
            double newLatitude = Double.parseDouble(updateLatitudeEditText.getText().toString());
            double newLongitude = Double.parseDouble(updateLongitudeEditText.getText().toString());

            updateLocation(updateAddress, newLatitude, newLongitude);
            refreshGeocodedLocations();
        });

        // Handle the query button click
        queryButton.setOnClickListener(view -> {
            String queryAddress = queryAddressEditText.getText().toString();

            if (!queryAddress.isEmpty()) {
                GeocodedLocation queriedLocation = queryLocationByAddress(queryAddress);

                if (queriedLocation != null) {
                    // Query successful, display the latitude and longitude
                    queryResultTextView.setText("Latitude: " + queriedLocation.getLatitude() + "\nLongitude: " + queriedLocation.getLongitude());
                } else {
                    // Address not found in the database
                    queryResultTextView.setText("Address not found in the database.");
                }
            } else {
                // Empty query address
                queryResultTextView.setText("Please enter an address to query.");
            }
        });
    }

    // Helper method to insert a location into the database
    private long insertLocation(GeocodedLocation location) {
        ContentValues values = new ContentValues();
        values.put(LocationDatabaseHelper.COLUMN_ADDRESS, location.getAddress());
        values.put(LocationDatabaseHelper.COLUMN_LATITUDE, location.getLatitude());
        values.put(LocationDatabaseHelper.COLUMN_LONGITUDE, location.getLongitude());

        return database.insert(LocationDatabaseHelper.TABLE_LOCATIONS, null, values);
    }

    // Helper method to delete a location by address
    private void deleteLocationByAddress(String address) {
        database.delete(
                LocationDatabaseHelper.TABLE_LOCATIONS,
                LocationDatabaseHelper.COLUMN_ADDRESS + " = ?",
                new String[]{address}
        );
    }

    // Helper method to update a location by address
    private void updateLocation(String address, double newLatitude, double newLongitude) {
        ContentValues values = new ContentValues();
        values.put(LocationDatabaseHelper.COLUMN_LATITUDE, newLatitude);
        values.put(LocationDatabaseHelper.COLUMN_LONGITUDE, newLongitude);

        database.update(
                LocationDatabaseHelper.TABLE_LOCATIONS,
                values,
                LocationDatabaseHelper.COLUMN_ADDRESS + " = ?",
                new String[]{address}
        );
    }

    // Helper method to query a location by address
    private GeocodedLocation queryLocationByAddress(String address) {
        Cursor cursor = database.query(
                LocationDatabaseHelper.TABLE_LOCATIONS,
                null,
                LocationDatabaseHelper.COLUMN_ADDRESS + " = ?",
                new String[]{address},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            double latitude = cursor.getDouble(cursor.getColumnIndex(LocationDatabaseHelper.COLUMN_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(LocationDatabaseHelper.COLUMN_LONGITUDE));
            return new GeocodedLocation(address, latitude, longitude);
        }

        return null; // Address not found in the database
    }

    // Helper method to display all geocoded locations
    private void displayGeocodedLocations() {
        Cursor cursor = database.query(
                LocationDatabaseHelper.TABLE_LOCATIONS,
                null,
                null,
                null,
                null,
                null,
                null
        );

        TextView geocodedLocationsText = findViewById(R.id.geocodedLocationsText);
        StringBuilder locationText = new StringBuilder();

        while (cursor.moveToNext()) {
            String address = cursor.getString(cursor.getColumnIndex(LocationDatabaseHelper.COLUMN_ADDRESS));
            double latitude = cursor.getDouble(cursor.getColumnIndex(LocationDatabaseHelper.COLUMN_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(LocationDatabaseHelper.COLUMN_LONGITUDE));

            locationText.append("Address: ").append(address).append("\n");
            locationText.append("Latitude: ").append(latitude).append("\n");
            locationText.append("Longitude: ").append(longitude).append("\n\n");
        }

        geocodedLocationsText.setText(locationText.toString());
    }

    // Refresh the displayed geocoded locations
    private void refreshGeocodedLocations() {
        displayGeocodedLocations();
    }
}
