package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * This class contains is an array of City SearchResults objects.
 * This is done in the cases that there are multiple cities that
 * share the same name.
 */
public class Embedded {
    @SerializedName("city:search-results") CitySearchResults []searchResults;
} // Embedded
