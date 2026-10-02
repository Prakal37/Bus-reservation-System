package com.busreservation.ui;

import com.busreservation.dto.ApiResponse;
import com.busreservation.dto.BookingDTO;
import com.busreservation.dto.BookingRequestDTO;
import com.busreservation.dto.BusDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class PaymentScreen {
    private final Stage stage;
    private final NavigationContext navigationContext;

    public PaymentScreen(Stage stage, NavigationContext navigationContext) {
        this.stage = stage;
        this.navigationContext = navigationContext;
    }

    public Parent getView() {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox headerBox = new VBox(5);
        headerBox.getStyleClass().add("app-header");
        headerBox.setPadding(new Insets(18, 26, 18, 26));

        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Button backButton = new Button("← Back to Passenger Details");
        backButton.getStyleClass().add("ghost-button");
        backButton.setOnAction(e -> navigationContext.showScene(new PassengerDetailsScreen(stage, navigationContext).getView()));
        Label title = new Label("Secure Payment");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: white;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button homeButton = new Button("Home");
        homeButton.getStyleClass().add("ghost-button");
        homeButton.setOnAction(e -> navigationContext.showScene(new DashboardScreen(stage, navigationContext).getView()));
        headerRow.getChildren().addAll(backButton, spacer, title, homeButton);
        Label subtitle = new Label("Demo payment only — no real money is charged");
        subtitle.setStyle("-fx-text-fill: rgba(255,255,255,0.82); -fx-font-size: 12px;");
        headerBox.getChildren().addAll(headerRow, subtitle);

        VBox contentBox = new VBox(18);
        contentBox.setPadding(new Insets(28));
        contentBox.setMaxWidth(760);

        BusDTO busDTO = navigationContext.getSelectedBusDTO();
        NavigationContext.Bus bus = navigationContext.getSelectedBus();
        NavigationContext.SearchQuery query = navigationContext.getSearchQuery();
        NavigationContext.PassengerInfo passenger = navigationContext.getPassengerInfo();
        String[] seats = navigationContext.getSelectedSeats();

        String busOperator = busDTO != null && busDTO.getOperatorName() != null ? busDTO.getOperatorName() : (bus != null ? bus.busOperator : "Express Bus");
        String routeStr = (query != null ? query.departureCity : "Origin") + " → " + (query != null ? query.destinationCity : "Destination");
        String travelDate = query != null ? query.date : "2026-10-01";
        double pricePerSeat = busDTO != null && busDTO.getPricePerSeat() != null ? busDTO.getPricePerSeat() : (bus != null ? bus.price : 500.0);
        int seatCount = (seats != null && seats.length > 0) ? seats.length : 1;
        double baseFare = pricePerSeat * seatCount;
        double convenienceFee = 35.0;
        double totalAmount = baseFare + convenienceFee;

        VBox summaryCard = new VBox(10);
        summaryCard.getStyleClass().add("card");
        summaryCard.setPadding(new Insets(18));
        Label summaryTitle = new Label("Booking Summary");
        summaryTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        summaryTitle.setStyle("-fx-text-fill: #0d47a1;");
        summaryCard.getChildren().addAll(summaryTitle,
                new Label("Bus: " + busOperator + " (" + (busDTO != null && busDTO.getBusType() != null ? busDTO.getBusType() : "Standard") + ")"),
                new Label("Route: " + routeStr),
                new Label("Travel Date: " + travelDate),
                new Label("Passenger: " + (passenger != null ? passenger.passengerName : "Passenger")),
                new Label("Seat(s): " + (seats != null && seats.length > 0 ? String.join(", ", seats) : "A1")),
                new Label("Total Amount: ₹" + (int) totalAmount));

        VBox priceCard = new VBox(8);
        priceCard.setPadding(new Insets(10, 0, 10, 0));
        priceCard.getChildren().addAll(
                createPriceRow("Base Fare (" + seatCount + " seat(s)):", "₹" + (int) baseFare),
                createPriceRow("Convenience & Taxes:", "₹" + (int) convenienceFee),
                new Separator(),
                createPriceRow("Total Payable:", "₹" + (int) totalAmount)
        );

        VBox methodBox = new VBox(12);
        methodBox.getStyleClass().add("card");
        methodBox.setPadding(new Insets(18));
        Label methodTitle = new Label("Payment Method");
        methodTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        ToggleGroup group = new ToggleGroup();
        RadioButton upiRadio = new RadioButton("UPI");
        upiRadio.setToggleGroup(group);
        upiRadio.setSelected(true);
        RadioButton cardRadio = new RadioButton("Card");
        cardRadio.setToggleGroup(group);
        RadioButton bankRadio = new RadioButton("Net Banking");
        bankRadio.setToggleGroup(group);
        methodBox.getChildren().addAll(methodTitle, upiRadio, cardRadio, bankRadio);

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #0d47a1; -fx-font-size: 12px; -fx-font-weight: bold;");

        Button payButton = new Button("Pay ₹" + (int) totalAmount + " & Confirm Booking");
        payButton.getStyleClass().add("primary-button");
        payButton.setOnAction(e -> {
            payButton.setDisable(true);
            statusLabel.setText("Processing mock payment...");
            try {
                Long userId = navigationContext.getUserId();
                if (userId == null || userId <= 0) {
                    showAlert("Session Error", "You are not logged in. Please log in first.");
                    payButton.setDisable(false);
                    return;
                }

                Long busId = busDTO != null ? busDTO.getBusId() : (bus != null ? (long) bus.busId : 1L);
                List<Integer> seatNumbers = parseSeatNumbers(seats);
                BookingRequestDTO bookingRequest = new BookingRequestDTO(
                        busId,
                        seatNumbers,
                        travelDate,
                        passenger != null ? passenger.passengerName : "Passenger",
                        passenger != null ? passenger.email : "passenger@example.com",
                        passenger != null ? passenger.mobileNumber : "9876543210"
                );

                ApiResponse<BookingDTO> response = navigationContext.getApiClient().createBooking(userId, bookingRequest, navigationContext.getJwtToken());
                if (response != null && response.isSuccess() && response.getData() != null) {
                    BookingDTO bookingDTO = response.getData();
                    navigationContext.setCreatedBookingDTO(bookingDTO);
                    NavigationContext.BookingConfirmation confirmation = new NavigationContext.BookingConfirmation(
                            bookingDTO.getBookingReference() != null ? bookingDTO.getBookingReference() : "TNBR" + bookingDTO.getBookingId(),
                            bus != null ? bus : new NavigationContext.Bus(busId.intValue(), busOperator, "Standard", routeStr.split(" → ")[0], routeStr.split(" → ")[1], "09:00 PM", "05:30 AM", "8h", pricePerSeat, 40, 38),
                            seats != null && seats.length > 0 ? seats : new String[]{"A1"},
                            passenger != null ? passenger : new NavigationContext.PassengerInfo("Passenger", 25, "Male", "p@example.com", "9876543210"),
                            totalAmount
                    );
                    navigationContext.setBookingConfirmation(confirmation);
                    navigationContext.showScene(new BookingConfirmationScreen(stage, navigationContext).getView());
                } else {
                    String message = response != null && response.getMessage() != null ? response.getMessage() : "Booking could not be completed. Please try again.";
                    showAlert("Booking failed", message);
                    payButton.setDisable(false);
                    statusLabel.setText("");
                }
            } catch (Exception ex) {
                showAlert("Payment error", "Unable to connect to the server. Please make sure the backend is running.");
                payButton.setDisable(false);
                statusLabel.setText("");
            }
        });

        contentBox.getChildren().addAll(summaryCard, priceCard, methodBox, statusLabel, payButton);
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        root.getChildren().addAll(headerBox, scrollPane);
        return root;
    }

    private List<Integer> parseSeatNumbers(String[] seats) {
        List<Integer> list = new ArrayList<>();
        if (seats == null || seats.length == 0) {
            list.add(1);
            return list;
        }
        for (String seat : seats) {
            try {
                String digits = seat.replaceAll("[^0-9]", "");
                list.add(digits.isEmpty() ? 1 : Integer.parseInt(digits));
            } catch (Exception ignored) {
                list.add(1);
            }
        }
        return list;
    }

    private HBox createPriceRow(String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 12px; -fx-font-weight: bold;");
        row.getChildren().addAll(labelNode, spacer, valueNode);
        return row;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
