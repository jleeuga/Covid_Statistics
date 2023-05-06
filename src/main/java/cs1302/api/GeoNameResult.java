package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * This clas is used to hold the full name, the coordinates and population of
 * a country based on the user's input.
 */
public class GeoNameResult {
    @SerializedName("full_name") String fullName;
    Location location;
    Double population;
} // GeoNameResult
