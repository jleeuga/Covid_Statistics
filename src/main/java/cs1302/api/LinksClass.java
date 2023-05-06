package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * This class has a CityItem object and is used by the CitySearchResults class.
 */
public class LinksClass {
    @SerializedName("city:item") CityItem cityItem;
} // LinksClass
