package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * This class has the a string of the URI of a city's
 * geoname ID which will be used by another URI.
 */
public class CityItem {
    @SerializedName("href") String geoName;
} // CityItem
