package cs1302.api;

import com.google.gson.annotations.SerializedName;

/**
 * This class is made to be collect infromation about the results from
 * the JSON response. It includes an object from the Embedded class
 * which contains information about cities from user input and
 * the number of cities that share the same name.
 */
public class CityResult {
    @SerializedName("_embedded") Embedded embedded;
    int count;
} // CityResult
