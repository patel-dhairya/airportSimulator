package com.project.airportsimulator.gui;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import com.project.airportsimulator.airport.portexceptions.AirportException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

import com.project.airportsimulator.airport.Airport;
import com.project.airportsimulator.airport.Runway;
import com.project.airportsimulator.airplane.Airplane;

/**
 * Class to manage JavaFX gui for airport system
 *
 * @author Dhairya Patel
 * @since November 23, 2023
 */
public class AirportGUI extends Application{
    // Airport object
    private Airport userAirport;

    private int numberOfRunways;
    private final String fileName = "airportSave.dat";

    // Arrival information GUI section
    private HBox arrivalSection = new HBox(50);
    // Unique
    private VBox arrivalFlightLocation = new VBox();

    // Common between arrival and departure
    private VBox flightName = new VBox();
    private VBox flightStatus = new VBox();
    // Assume that each runway point to each unique gate. Runway 1 => Check in Gate 1
    private VBox flightGate = new VBox();


    // Departure information GUI section
    private HBox departureSection = new HBox(50);
    // Unique
    private VBox departureFlightLocation = new VBox();

    // Methods

    /**
     * Init screen and airport GUI
     *
     * @param stage Stage object
     */
    @Override
    public void start(Stage stage){
        // Ask if user want data to be loaded from file
        Alert loadDataAlert = new Alert(AlertType.INFORMATION, "Do you want to load your data?", ButtonType.YES, ButtonType.NO);
        String loadDataResponse = loadDataAlert.showAndWait().get().getText();

        // User wants to load old data
        if (loadDataResponse.equals("Yes")){
            // Try loading data
            try{
                userAirport = new Airport(fileName);
                getArrivals();
                getDepartures();
                showMessage("Airport data loaded successfully!");
            } catch (IOException e) {
                showError("File IO error");
                System.exit(1);
            } catch (ClassNotFoundException e) {
                showError("Required classes not found");
                System.exit(1);
            }
        }
        // New data
        else{
            // Number of runways/gates of airport
            numberOfRunways = askNumberOfRunways();
            try{
                userAirport = new Airport(numberOfRunways);
            }
            catch (AirportException e){
                showError(e.getMessage());
                System.exit(1);
            }
            // Handle undefined cases
            catch (Exception e)
            {
                showError(e.getMessage());
                System.exit(1);
            }
        }

        // Three important tabs
        TabPane tabPanels = new TabPane();
        Tab tabControl = new Tab("Airport Control");
        Tab tabArrival = new Tab("Airport Arrivals");
        Tab tabDeparture = new Tab("Airport Departures");
        tabPanels.getTabs().addAll(tabControl, tabArrival, tabDeparture);
        tabControl.setClosable(false);
        tabArrival.setClosable(false);
        tabDeparture.setClosable(false);

        VBox root = new VBox();

        // Menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.setMinHeight(20);
        Menu fileItem = new Menu("File");
        Menu continueItem = new Menu("Save and Continue");
        Menu exitWithSavingItem = new Menu("Save and Exit");
        Menu exitWithoutSavingItem = new Menu("Exit without save");
        fileItem.getItems().addAll(continueItem, exitWithSavingItem, exitWithoutSavingItem);
        menuBar.getMenus().add(fileItem);

        // Airport control screen
        VBox controlBox = new VBox();
        controlBox.setPadding(new Insets(10));
        controlBox.setMinHeight(200);
        tabControl.setContent(controlBox);
        controlBox.setAlignment(Pos.BOTTOM_LEFT);

        Image airportImage = new Image("C:\\Users\\dpat5\\Desktop\\Github\\java-projects\\airportSimulator\\src\\main\\java\\com\\project\\airportsimulator\\gui\\image\\airportImg.jpg");
        ImageView airportImageView = new ImageView(airportImage);

        Label infoLabel = new Label("Choose an option:");

        HBox flightControls = new HBox(10);
        Button registerButton = new Button("Register");
        registerButton.setTooltip(new Tooltip("Register an incoming flight with the airport"));
        Button requestLandButton = new Button("Land request");
        requestLandButton.setTooltip(new Tooltip("Use when flight is requesting to land"));
        Button landButton = new Button("Land");
        landButton.setTooltip(new Tooltip("Use when flight is landing"));
        Button boardingButton = new Button("Board");
        boardingButton.setTooltip(new Tooltip("Use when flight is ready for boarding"));
        Button takeOffButton = new Button("Take Off");
        takeOffButton.setTooltip(new Tooltip("Use when flight leaving airport"));
        flightControls.getChildren().addAll(registerButton, requestLandButton, landButton, boardingButton, takeOffButton);

        controlBox.getChildren().addAll(airportImageView, infoLabel, flightControls);

        try{
            registerButton.setOnAction(e->registerFunc());
            requestLandButton.setOnAction(e->requestFunc());
            landButton.setOnAction(e->landFunc());
            boardingButton.setOnAction(e->boardingFunc());
            takeOffButton.setOnAction(e->takeOffFunc());

            continueItem.setOnAction(e->fileSave(fileName));
            exitWithSavingItem.setOnAction(e->{
                fileSave(fileName);
                Platform.exit();
            });
            exitWithoutSavingItem.setOnAction(e-> exitWithoutSavingFunc());
        }
        catch (Exception e){
            showError("Invalid option selected");
        }

        // Flight arrival tab
        arrivalSection.setPadding(new Insets(10));
        arrivalSection.getChildren().addAll(flightName, arrivalFlightLocation, flightStatus, flightGate);
        tabArrival.setContent(arrivalSection);

        // Flight departure tab
        departureSection.setPadding(new Insets(10));
        departureSection.getChildren().addAll(flightName, departureFlightLocation, flightStatus, flightGate);
        tabDeparture.setContent(departureSection);

        root.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(2))));
        root.getChildren().addAll(menuBar, tabPanels);

        Scene mainScene = new Scene(root,450, 300);
        stage.setScene(mainScene);
        stage.setTitle("Airport Sim");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    private void exitWithoutSavingFunc() {
        Alert alert = new Alert( AlertType.WARNING, "Do you want to exit without saving current session?", ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle("Confirmation");
        String exitNoSaveResponse = alert.showAndWait().get().getText();
        if(exitNoSaveResponse.equals("Yes"))
        {
            Platform.exit();
        }
    }

    private void fileSave(String fileName) {
        try{
            userAirport.save(fileName);
            showMessage("Current session saved successfully");
        } catch (Exception e) {
            showError("Error regarding file save");
        }
    }

    private void open(String fileName){
        try{
            userAirport.load(fileName);
            getArrivals();
            getDepartures();
            showMessage("Old session loaded successfully");
        }
        catch (Exception e){
            showError("Error regarding file open");
            System.exit(1);
        }
    }

    private void takeOffFunc() {
        String flightNumber;

        try{
            TextInputDialog takeOffDialog = new TextInputDialog();
            takeOffDialog.setHeaderText("Enter flight number: ");
            takeOffDialog.setTitle("Taking off");
            flightNumber = takeOffDialog.showAndWait().get();

            // Check if flight number is empty
            checkNotEmptyResponse(flightNumber, "Flight number cannot be empty");

            userAirport.readyToTakeOff(flightNumber);
            showMessage("Confirmation:\nFlight-" +flightNumber+ " has departed from airport and been removed " +
                    "from airport system.");
        }
        catch (AirportException e){
            showError(e.getMessage());
        }
        getDepartures();
    }

    private void boardingFunc() {
        String flightNumber, destinationCity;

        try{
            TextInputDialog boardingDialog = new TextInputDialog();
            boardingDialog.setHeaderText("Enter flight number: ");
            boardingDialog.setTitle("Boarding process");
            flightNumber = boardingDialog.showAndWait().get();

            // Check if flight number is empty
            checkNotEmptyResponse(flightNumber, "Flight number cannot be empty");

            boardingDialog = new TextInputDialog();
            boardingDialog.setHeaderText("Enter destination city: ");
            boardingDialog.setTitle("Boarding process");
            destinationCity = boardingDialog.showAndWait().get();

            // Check if city is entered
            checkNotEmptyResponse(destinationCity, "Please enter destination city");

            userAirport.readyToBoard(flightNumber, destinationCity);
            showMessage("Confirmation:\nFlight-" +flightNumber+ " is start boarding for destination-" +destinationCity);
        }
        catch (AirportException e){
            showError(e.getMessage());
        }
        getArrivals();
        getDepartures();
    }

    private void landFunc() {
        String flightNumber, runwayNumber;
        int runway;

        try{
            TextInputDialog landDialog = new TextInputDialog();
            landDialog.setHeaderText("Enter flight number: ");
            landDialog.setTitle("Landing process");
            flightNumber = landDialog.showAndWait().get();

            // Check if flight number is empty
            checkNotEmptyResponse(flightNumber, "Flight number cannot be empty");

            landDialog = new TextInputDialog();
            landDialog.setHeaderText("Enter runway number: ");
            landDialog.setTitle("Landing process");
            runwayNumber = landDialog.showAndWait().get();

            // Check if runway number provided is empty
            checkNotEmptyResponse(runwayNumber, "Runway number cannot be empty");

            runway = Integer.parseInt(runwayNumber);

            userAirport.readyToLand(flightNumber, runway);
            showMessage("Confirmation:\nFlight-" +flightNumber+ " landed on runway number-"+runway);
        }
        catch (AirportException e){
            showError(e.getMessage());
        }
        getArrivals();
    }

    private void requestFunc() {
        String flightNumber, requestMessage;
        try{
            TextInputDialog requestDialog = new TextInputDialog();
            requestDialog.setHeaderText("Enter flight number: ");
            requestDialog.setTitle("Request to land");
            flightNumber = requestDialog.showAndWait().get();

            // Check if flight number is empty
            checkNotEmptyResponse(flightNumber, "Flight number cannot be empty");

            int runwayNumber = userAirport.airplaneAssignedRunway(flightNumber);

            if (runwayNumber==0){
                requestMessage = "All runways are currently occupied, please join waiting queue";
            }
            else{
                requestMessage = ". You can land at runway number-" +runwayNumber;
            }
            showMessage("Confirmation:\nFlight-" +flightNumber+ requestMessage);
        }
        catch (AirportException e){
            showError(e.getMessage());
        }
        getArrivals();
    }

    private void registerFunc() {
        String flightNumber, cityOfOrigin;
        try{
            TextInputDialog registerDialog = new TextInputDialog();
            registerDialog.setHeaderText("Enter flight number: ");
            registerDialog.setTitle("Flight registration form");
            flightNumber = registerDialog.showAndWait().get();

            // Check if flight number is empty
            checkNotEmptyResponse(flightNumber, "Flight number cannot be empty");

            registerDialog = new TextInputDialog();
            registerDialog.setHeaderText("Enter city of origin for flight");
            registerDialog.setTitle("Flight registration form");
            cityOfOrigin = registerDialog.showAndWait().get();

            checkNotEmptyResponse(cityOfOrigin, "City of origin cannot be empty");

            userAirport.flightRegister(flightNumber, cityOfOrigin);
            showMessage("Confirmation:\nFlight-" +flightNumber+ " registered for arrival from " +cityOfOrigin);
        }
        catch (AirportException e){
            showError(e.getMessage());
        }
        getArrivals();
    }

    private void checkNotEmptyResponse(String checker, String errorMessage)
    {
        if (checker.equals(""))
        {
            throw new AirportException(errorMessage);
        }
    }

    private int askNumberOfRunways() {
        TextInputDialog numberOfRunway = new TextInputDialog("6"); //Default number of gates are 6
        numberOfRunway.setHeaderText("Enter number of runways/gates");
        numberOfRunway.setTitle("Runway/Gate info");

        Optional<String> result = numberOfRunway.showAndWait();

        if (result.isPresent() && isPositiveInteger(result.get())) {
            return Integer.parseInt(result.get());
        } else {
            return -1;
        }
    }

    private boolean isPositiveInteger(String s) {
        try {
            int value = Integer.parseInt(s);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showError(String fileErrorMessage) {
        Alert errorAlert = new Alert(AlertType.ERROR);
        errorAlert.setHeaderText("Airport session error");
        errorAlert.setContentText(fileErrorMessage);
        errorAlert.showAndWait();
    }

    private void showMessage(String message) {
        Alert messageAlert = new Alert(AlertType.INFORMATION);
        messageAlert.setHeaderText("Airport session information");
        messageAlert.setContentText(message);
        messageAlert.showAndWait();
    }

    private void getDepartures() {
        Set<Airplane> departureAirplanes = userAirport.getAllDepartures();

        flightName.getChildren().clear();
        departureFlightLocation.getChildren().clear();
        flightStatus.getChildren().clear();
        flightGate.getChildren().clear();
        flightName.getChildren().add(new Text("FLIGHT"));
        departureFlightLocation.getChildren().add(new Text("TO"));
        flightStatus.getChildren().add(new Text("STATUS"));
        flightGate.getChildren().add(new Text("GATE No."));

        for (Airplane currentAirplane: departureAirplanes){
            flightName.getChildren().add(new Text(currentAirplane.getFlightNumber()));
            departureFlightLocation.getChildren().add(new Text(currentAirplane.getDestinationCity()));
            flightStatus.getChildren().add(new Text(currentAirplane.getStatus().name()));
            try{
                flightGate.getChildren().add(new Text(Integer.toString(currentAirplane.getRunwayNumber())));
            }
            catch (Exception e){
                flightGate.getChildren().add(new Text(""));
            }
        }
    }

    private void getArrivals() {
        System.out.println(numberOfRunways);
        Set<Airplane> arrivalAirplanes = userAirport.getAllArrivals();

        flightName.getChildren().clear();
        arrivalFlightLocation.getChildren().clear();
        flightStatus.getChildren().clear();
        flightGate.getChildren().clear();
        flightName.getChildren().add(new Text("FLIGHT"));
        arrivalFlightLocation.getChildren().add(new Text("FROM"));
        flightStatus.getChildren().add(new Text("STATUS"));
        flightGate.getChildren().add(new Text("GATE No."));

        for (Airplane currentAirplane: arrivalAirplanes){
            flightName.getChildren().add(new Text(currentAirplane.getFlightNumber()));
            arrivalFlightLocation.getChildren().add(new Text(currentAirplane.getOriginCity()));
            flightStatus.getChildren().add(new Text(currentAirplane.getStatus().name()));
            try{
                flightGate.getChildren().add(new Text(Integer.toString(currentAirplane.getRunwayNumber())));
            }
            catch (Exception e){
                flightGate.getChildren().add(new Text(""));
            }
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }


}
