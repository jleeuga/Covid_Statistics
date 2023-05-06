package cs1302.api;

import javafx.scene.layout.Priority;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import java.lang.Runnable;
import java.lang.Thread;

import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import java.util.ArrayList;
import javafx.scene.text.Text;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.http.HttpResponse.BodyHandlers;

import java.io.IOException;
import java.lang.InterruptedException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This app gives information about the city the user has inputed.
 * In addition, statistics about the relation betweeen the country the city
 * resides in and Covid-19 are displayed as well.
 * If the city name is used by multiple cities. It the more well-known one will be used.
 * For example, the Memphis, Tennessee will be used instead of Memphis, Egypt.
 */
public class ApiApp extends Application {
    private Stage stage;
    private Scene scene;
    private VBox root;
    private HBox topHBox;

    private TextFlow textFlow;
    private TextField textField;
    private Button button;
    private Text text;
    private ScrollPane textPane;

    // Display Information
    private Double cases;
    private Double deaths;
    private Double casesMil;
    private Double deathsMil;
    private Double popSize;
    private String specificName;
    private Double popResult;
    private Double longID;
    private Double latID;

    private int cityArraySize;

    // ArrayLists
    private ArrayList<String> cityInfo;
    private ArrayList<String> countryInfo;
    private ArrayList<String> cityInfoHeader;
    private ArrayList<String> countryInfoHeader;

    private Alert alert;

    private static final String EMPTY_STRING = "Information not available";

    private static final String DISEASE_API = "https://disease.sh/v3/covid-19/countries/";

    private static final String CITY_API = "https://api.teleport.org/api/cities/?search=";

    private static final String OPENING_MESSAGE = "Input a city in the text bar above and " +
        " press the Go button \n \n" +
        "This will give information about the city and information about Covid-19" +
        " in the country the city resides in." + "\n \n If the city's name is used in multiple "
        + "places, the more well-known city is used. \n" +
        "Ex. Memphis, Tennessee will be used over Memphis, Egypt.";

    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();                                     // builds and returns a HttpClient object

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object


    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox();
        this.topHBox = new HBox();

        this.textPane = new ScrollPane();
        this.textFlow = new TextFlow();
        this.textField = new TextField("Type in a city");
        this.button = new Button("Go");

        this.cityInfoHeader = new ArrayList<String>();
        this.cityInfo = new ArrayList<String>();
        this.countryInfoHeader = new ArrayList<String>();
        this.countryInfo = new ArrayList<String>();

        this.cityInfoHeader.add(new String("Specifc Name: "));
        this.cityInfoHeader.add(new String("Population: "));
        this.cityInfoHeader.add(new String("Longitude: "));
        this.cityInfoHeader.add(new String("Latitude: "));

        this.countryInfoHeader.add(new String("Total Cases: "));
        this.countryInfoHeader.add(new String("Total Deaths: "));
        this.countryInfoHeader.add(new String("Cases Per Million: "));
        this.countryInfoHeader.add(new String("Deaths Per Million: "));
        this.countryInfoHeader.add(new String("Population: "));

        this.alert = new Alert(AlertType.ERROR);
        this.alert.setHeaderText("Error");
        this.alert.setTitle("Error");

    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void init() {
        System.out.println("init() is called");

        // intialize the text flow
        this.textFlow.getChildren().add(new Text(OPENING_MESSAGE));
        this.textFlow.setMaxWidth(630);
        this.textPane.setPrefHeight(500);
        this.textPane.setContent(textFlow);
        // Setting the HBox to grow around the textfield
        HBox.setHgrow(this.textField, Priority.ALWAYS);
        this.topHBox.getChildren().addAll(this.textField, this.button);

        // Adding to the root
        this.root.getChildren().addAll(this.topHBox, this.textPane);

        // Setting the button usable
        button.setDisable(false);

        // User clicks the goButton
        Runnable task = () -> {
            goButton();
        };

        this.button.setOnAction(event -> runInNewThread(task));

    } // init

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;
        scene = new Scene(root);

        // setup stage
        stage.setTitle("ApiApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start


    /**
     * This method returns the country a city is located in.
     *
     * @param input is the specific location of a city.
     * @return a string of the country a city resides in.
     */
    public String countryGetter(String input) {
        int index = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ',') {
                index = i;
            } //if
        } // for

        return input.substring(index + 2);
    } // countryGetter

    /**
     * This method gets the most well know city with the name that
     * the user has inputted. In addtion, it makes sure that the city
     * the user inputted exists.
     */
    public void cityApiMethod() {
        try {
            String term = URLEncoder.encode(textField.getText().toLowerCase().trim(),
                StandardCharsets.UTF_8);
            String uri = CITY_API + term;

            // build request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
            // send request / receive response in the form of a String
            HttpResponse<String> response = HTTP_CLIENT
                .send(request, BodyHandlers.ofString());

            // ensure the request is okay
            if (response.statusCode() != 200) {
                throw new IOException(response.toString());
            } // if

            // get request body (the content we requested)
            String jsonString = response.body();

            CityResult cityResult = GSON
                .fromJson(jsonString, CityResult.class);

            // geting the geonameID of a city
            Embedded result = cityResult.embedded;
            this.cityArraySize = result.searchResults.length;
            if (cityArraySize != 0) {
                CitySearchResults result2 = result.searchResults[0];
                LinksClass result3 = result2.links;
                CityItem result4 = result3.cityItem;
                String result5 = result4.geoName;
                geoNameMethod(result5);
            } // if

        } catch (IOException | InterruptedException e) {
            System.out.println("There is an error in cityapi");
        } // try
    } // cityApiMethod

    /**
     * This method generates the population, latitiude and longitude of the city
     * that was inputed by the user.
     *
     * @param geoName is the uri for the for the city that
     * the user inputs.
     */
    public void geoNameMethod(String geoName) {
        try {
            String uri = geoName;
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();

            // send request / receive response in the form of a String
            HttpResponse<String> response = HTTP_CLIENT
                .send(request, BodyHandlers.ofString());

            // ensure the request is okay
            if (response.statusCode() != 200) {
                throw new IOException(response.toString());
            } // if

            // get request body (the content we requested)
            String jsonString = response.body();

            GeoNameResult geoNameResult = GSON
                .fromJson(jsonString, GeoNameResult.class);

            this.cityInfo.clear();

            if (geoNameResult.fullName != null) {
                specificName = geoNameResult.fullName;
                cityInfo.add(specificName);
            } else {
                cityInfo.add(EMPTY_STRING);
            } // if
            if (geoNameResult.population != null) {
                popResult = geoNameResult.population;
                cityInfo.add(new String("" + popResult));
            } else {
                cityInfo.add(EMPTY_STRING);
            } // if
            Location locate = geoNameResult.location;
            Latlon latlonID = locate.latlon;
            if (latlonID.longitude != null) {
                longID = latlonID.longitude;
                cityInfo.add(new String("" + longID));
            } else {
                cityInfo.add(EMPTY_STRING);
            } // if
            if (latlonID.latitude != null) {
                latID = latlonID.latitude;
                cityInfo.add(new String("" + latID));
            } else {
                cityInfo.add(EMPTY_STRING);
            } // if
        } catch (IOException | InterruptedException e) {
            System.out.println("There is an error in geoNameMethod.");
        } // try
    } // geoNameMethod

    /**
     * This buttons gathers information based on the input of the url.
     */
    public void goButton() {
        if (textField.getText().trim() == null || textField.getText().trim().equals("")) {
            Platform.runLater(() -> this.textFlow.getChildren().clear());
            Platform.runLater(() -> this.textFlow.getChildren().add(new Text("Input is invalid")));
            return;
        } // if

        Platform.runLater(() -> this.textFlow.getChildren().clear());
        Platform.runLater(() -> this.textFlow.getChildren().add(new Text("Loading ...")));

        this.cityApiMethod();
        if (cityArraySize == 0) {
            Platform.runLater(() -> this.textFlow.getChildren().clear());
            Platform.runLater(() -> this.textFlow.getChildren().add(new Text(OPENING_MESSAGE)));
            this.alert.setContentText("The city inputed does not exist. \n \n" +
                "Please type in another city.");
            Platform.runLater(() -> alert.showAndWait());
            return;
        } // if

        try {
            // form URI
            String country = countryGetter(specificName);
            String term = URLEncoder.encode(country.toLowerCase(), StandardCharsets.UTF_8);
            String query = String.format("%s", term);
            String uri = DISEASE_API + query.replace("+", "%20");

            // build request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri)).build();
            // send request / receive response in the form of a String
            HttpResponse<String> response = HTTP_CLIENT
                .send(request, BodyHandlers.ofString());
            // ensure the request is okay
            if (response.statusCode() != 200) {
                throw new IOException(response.toString());
            } // if
            // get request body (the content we requested)
            String jsonString = response.body();
            CountryResult countryResult = GSON
                .fromJson(jsonString, CountryResult.class);

            this.countryInfo.clear();
            cases = countryResult.cases;
            countryInfo.add(new String("" + cases));
            deaths = countryResult.deaths;
            countryInfo.add(new String("" + deaths));
            casesMil = countryResult.casesPerOneMillion;
            countryInfo.add(new String("" + casesMil));
            deathsMil = countryResult.deathsPerOneMillion;
            countryInfo.add(new String("" + deathsMil));
            popSize = countryResult.population;
            countryInfo.add(new String("" + popSize));

            this.screenOutput();
        } catch (IOException | InterruptedException e) {
            System.out.println("There is an error in the go Button method");
        } // try
    } // goButton

    /**
     * This method will update the GUI with information about
     * a city and the country's relation to Covid-19.
     */
    public void screenOutput() {
        Platform.runLater(() -> this.textFlow.getChildren().clear());
        Platform.runLater(() -> this.textFlow.getChildren().add(
            new Text("CITY INFORMATION: \n \n")));
        for (int i = 0; i < cityInfo.size(); i++) { // adding city information to app
            Text line = new Text(cityInfoHeader.get(i)
                + cityInfo.get(i) + "\n \n");
            Platform.runLater(() -> this.textFlow.getChildren().add(line));
        } // for

        String country = countryGetter(specificName);
        String countryString = country.toUpperCase() + " COVID-19 STATISTICS: ";
        Platform.runLater(() -> this.textFlow.getChildren().add(new Text(countryString + "\n \n")));
        for (int i = 0; i < countryInfo.size(); i++) { // adding country information to app
            Text line = new Text(countryInfoHeader.get(i)
                + countryInfo.get(i) + "\n \n");
            Platform.runLater(() -> this.textFlow.getChildren().add(line));
        } // for
    } // screenOutput

    /**
     * This method is used to make a new thread.
     * @param task is the method that is run on a separate thread.
     */
    public static void runInNewThread(Runnable task) {
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    } // runInNewThread

    /** {@inheritDoc} */
    @Override
    public void stop() {
        // feel free to modify this method
        System.out.println("stop() called");
    } // stop

} // ApiApp
