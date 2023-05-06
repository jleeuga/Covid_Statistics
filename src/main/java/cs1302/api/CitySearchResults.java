package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * This class uses the LinksClass and obtains
 * the specific name of a city which includes the country it is in.
 */
public class CitySearchResults {
    @SerializedName("_links") LinksClass links;
    @SerializedName("matching_full_name") String fullName;
} // CitySearchResults
