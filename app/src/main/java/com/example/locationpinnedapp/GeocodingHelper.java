package com.example.locationpinnedapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeocodingHelper {
    public static List<GeocodedLocation> geocodeLocationsFromAssets(Context context, String fileName) {
        List<GeocodedLocation> geocodedLocations = new ArrayList<>();

        try {
            InputStream inputStream = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] coordinates = line.split(",");
                if (coordinates.length == 2) {
                    double latitude = Double.parseDouble(coordinates[0]);
                    double longitude = Double.parseDouble(coordinates[1]);

                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                    if (!addresses.isEmpty()) {
                        String address = addresses.get(0).getAddressLine(0);
                        geocodedLocations.add(new GeocodedLocation(address, latitude, longitude));
                    }
                }
            }

            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return geocodedLocations;
    }
}
