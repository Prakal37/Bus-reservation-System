package com.busreservation.ui;

import com.busreservation.dto.BookingDTO;
import com.busreservation.dto.BusDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public class TrackBusScreen {
    private final Stage stage;
    private final NavigationContext navigationContext;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    public TrackBusScreen(Stage stage, NavigationContext navigationContext) {
        this.stage = stage;
        this.navigationContext = navigationContext;
    }

    public Parent getView() {
        VBox root = new VBox(0);
        root.setStyle("-fx-background-color: #f4f7fb;");

        VBox header = new VBox(12);
        header.getStyleClass().add("app-header");
        header.setPadding(new Insets(18, 28, 18, 28));

        HBox headerRow = new HBox(16);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().add("ghost-button");
        backButton.setOnAction(e -> navigationContext.showScene(new DashboardScreen(stage, navigationContext).getView()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label title = new Label("Journey Status");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: white;");
        headerRow.getChildren().addAll(backButton, spacer, title);

        Label subtitle = new Label("View the schedule and journey details for a bus or booking.");
        subtitle.getStyleClass().add("subtitle-text");
        header.getChildren().addAll(headerRow, subtitle);

        VBox content = new VBox(18);
        content.setPadding(new Insets(26, 24, 32, 24));
        content.setMaxWidth(920);
        content.setFillWidth(true);

        VBox searchPanel = new VBox(16);
        searchPanel.getStyleClass().add("card");
        searchPanel.setPadding(new Insets(22));

        Label searchTitle = new Label("Find a journey");
        searchTitle.getStyleClass().add("section-title");
        Label searchHint = new Label("Enter a booking reference, booking ID, or bus ID.");
        searchHint.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");

        HBox queryRow = new HBox(12);
        queryRow.setAlignment(Pos.CENTER_LEFT);
        TextField queryField = new TextField();
        queryField.setPromptText("Booking reference, booking ID, or bus ID");
        queryField.setPrefWidth(390);
        queryField.setMaxWidth(Double.MAX_VALUE);
        queryField.getStyleClass().add("input-field");
        HBox.setHgrow(queryField, Priority.ALWAYS);
        queryField.setText(getInitialLookup());

        Button checkButton = new Button("Track Journey");
        checkButton.getStyleClass().add("primary-button");

        queryRow.getChildren().addAll(queryField, checkButton);

        Label feedback = new Label();
        feedback.setWrapText(true);
        feedback.setStyle("-fx-text-fill: #b42318; -fx-font-size: 12px;");

        VBox journeyPanel = createJourneyPanel();
        journeyPanel.setVisible(false);
        journeyPanel.setManaged(false);

        Runnable trackJourney = () -> {
            String lookup = queryField.getText() == null ? "" : queryField.getText().trim();
            if (lookup.isEmpty()) {
                feedback.setText("Enter a booking reference, booking ID, or bus ID to continue.");
                journeyPanel.setVisible(false);
                journeyPanel.setManaged(false);
                return;
            }
            try {
                Journey journey = findJourney(lookup);
                if (journey == null) {
                    feedback.setText("No matching booking or bus was found for that ID.");
                    journeyPanel.setVisible(false);
                    journeyPanel.setManaged(false);
                    return;
                }
                updateJourneyPanel(journeyPanel, journey);
                feedback.setText("");
                journeyPanel.setVisible(true);
                journeyPanel.setManaged(true);
            } catch (Exception ex) {
                feedback.setText("Journey details could not be loaded. Check your connection and try again.");
                journeyPanel.setVisible(false);
                journeyPanel.setManaged(false);
            }
        };
        checkButton.setOnAction(e -> trackJourney.run());
        queryField.setOnAction(e -> trackJourney.run());

        searchPanel.getChildren().addAll(searchTitle, searchHint, queryRow, feedback);
        content.getChildren().addAll(searchPanel, journeyPanel);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        root.getChildren().addAll(header, scroll);
        return root;
    }

    private VBox createJourneyPanel() {
        VBox panel = new VBox(20);
        panel.getStyleClass().add("card");
        panel.setPadding(new Insets(22));

        HBox statusRow = new HBox(12);
        statusRow.setAlignment(Pos.CENTER_LEFT);
        VBox statusBlock = new VBox(5);
        Label statusCaption = new Label("CURRENT JOURNEY STATUS");
        statusCaption.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label statusValue = new Label();
        statusValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        statusBlock.getChildren().addAll(statusCaption, statusValue);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label arrivalValue = new Label();
        arrivalValue.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 13px; -fx-font-weight: bold;");
        statusRow.getChildren().addAll(statusBlock, spacer, arrivalValue);

        HBox progress = new HBox();
        progress.setAlignment(Pos.CENTER);
        progress.setPadding(new Insets(12, 0, 2, 0));
        Label fromDot = progressDot();
        Label middleDot = progressDot();
        Label toDot = progressDot();
        Region firstLine = progressLine();
        Region secondLine = progressLine();
        VBox fromStep = progressStep(fromDot, "Departure", "");
        VBox middleStep = progressStep(middleDot, "In Transit", "");
        VBox toStep = progressStep(toDot, "Arrival", "");
        HBox.setHgrow(firstLine, Priority.ALWAYS);
        HBox.setHgrow(secondLine, Priority.ALWAYS);
        progress.getChildren().addAll(fromStep, firstLine, middleStep, secondLine, toStep);

        HBox details = new HBox(16);
        details.setAlignment(Pos.TOP_LEFT);
        VBox operatorField = detailField("BUS OPERATOR");
        VBox busField = detailField("BUS NUMBER");
        VBox routeField = detailField("ROUTE");
        VBox dateField = detailField("TRAVEL DATE");
        for (VBox field : List.of(operatorField, busField, routeField, dateField)) {
            HBox.setHgrow(field, Priority.ALWAYS);
        }
        details.getChildren().addAll(operatorField, busField, routeField, dateField);

        Label note = new Label("Journey status is based on scheduled/estimated information. Live location data is not available.");
        note.setWrapText(true);
        note.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-padding: 8 0 0 0;");

        panel.getChildren().addAll(statusRow, progress, details, note);
        return panel;
    }

    private void updateJourneyPanel(VBox panel, Journey journey) {
        BusDTO bus = journey.bus;
        String departure = valueOr(bus.getDepartureTime(), "Not available");
        String arrival = valueOr(bus.getArrivalTime(), "Not available");
        String source = bus.getRoute() != null ? valueOr(bus.getRoute().getSource(), "Departure") : "Departure";
        String destination = bus.getRoute() != null ? valueOr(bus.getRoute().getDestination(), "Arrival") : "Arrival";
        JourneyEstimate estimate = estimateStatus(journey.travelDate, departure, arrival);

        HBox statusRow = (HBox) panel.getChildren().get(0);
        VBox statusBlock = (VBox) statusRow.getChildren().get(0);
        Label statusValue = (Label) statusBlock.getChildren().get(1);
        statusValue.setText(estimate.status);
        statusValue.setStyle("-fx-text-fill: " + getStatusColor(estimate.status) + ";");
        Label arrivalValue = (Label) statusRow.getChildren().get(2);
        arrivalValue.setText("Scheduled arrival\n" + formatArrival(journey.travelDate, departure, arrival));

        HBox progress = (HBox) panel.getChildren().get(1);
        VBox fromStep = (VBox) progress.getChildren().get(0);
        Region firstLine = (Region) progress.getChildren().get(1);
        VBox middleStep = (VBox) progress.getChildren().get(2);
        Region secondLine = (Region) progress.getChildren().get(3);
        VBox toStep = (VBox) progress.getChildren().get(4);
        ((Label) fromStep.getChildren().get(0)).setStyle(progressDotStyle(estimate.progress >= 0.05));
        ((Label) middleStep.getChildren().get(0)).setStyle(progressDotStyle(estimate.progress >= 0.4));
        ((Label) toStep.getChildren().get(0)).setStyle(progressDotStyle(estimate.progress >= 0.95));
        firstLine.setStyle("-fx-background-color: " + (estimate.progress >= 0.4 ? "#168a68" : "#cbd5e1") + "; -fx-min-height: 3px;");
        secondLine.setStyle("-fx-background-color: " + (estimate.progress >= 0.95 ? "#168a68" : "#cbd5e1") + "; -fx-min-height: 3px;");
        ((Label) fromStep.getChildren().get(1)).setText(source);
        ((Label) middleStep.getChildren().get(1)).setText(estimate.status);
        ((Label) toStep.getChildren().get(1)).setText(destination);

        HBox details = (HBox) panel.getChildren().get(2);
        setDetailValue((VBox) details.getChildren().get(0), valueOr(bus.getOperatorName(), bus.getBusName(), "Not available"));
        setDetailValue((VBox) details.getChildren().get(1), valueOr(bus.getBusNumber(), "Bus ID " + bus.getBusId()));
        setDetailValue((VBox) details.getChildren().get(2), source + " → " + destination + "\n" + departure + " - " + arrival);
        setDetailValue((VBox) details.getChildren().get(3), formatDate(journey.travelDate));
    }

    private Journey findJourney(String lookup) throws Exception {
        String normalizedLookup = lookup.trim();
        BookingDTO booking = null;
        List<BookingDTO> bookings = Collections.emptyList();
        if (navigationContext.getUserId() != null) {
            bookings = navigationContext.getApiClient().getBookingsByUser(navigationContext.getUserId(), navigationContext.getJwtToken());
        }
        BookingDTO created = navigationContext.getCreatedBookingDTO();
        if (matchesBooking(created, normalizedLookup)) {
            booking = created;
        }
        for (BookingDTO candidate : bookings) {
            if (matchesBooking(candidate, normalizedLookup)) {
                booking = candidate;
                break;
            }
        }

        if (booking != null && booking.getBusId() != null) {
            BusDTO bus = navigationContext.getApiClient().getBusById(booking.getBusId(), booking.getJourneyDate());
            if (bus != null) {
                return new Journey(booking, bus, booking.getJourneyDate());
            }
        }

        try {
            long busId = Long.parseLong(normalizedLookup);
            LocalDate travelDate = navigationContext.getSearchQuery() != null
                    ? parseDate(navigationContext.getSearchQuery().date)
                    : LocalDate.now();
            BusDTO bus = navigationContext.getApiClient().getBusById(busId, travelDate.toString());
            if (bus != null) {
                return new Journey(null, bus, travelDate.toString());
            }
        } catch (NumberFormatException ignored) {
            return null;
        }

        return null;
    }

    private boolean matchesBooking(BookingDTO booking, String lookup) {
        if (booking == null) {
            return false;
        }
        if (booking.getBookingReference() != null && booking.getBookingReference().equalsIgnoreCase(lookup)) {
            return true;
        }
        try {
            long id = Long.parseLong(lookup);
            return id == (booking.getBookingId() != null ? booking.getBookingId() : -1L)
                    || id == (booking.getBusId() != null ? booking.getBusId() : -1L);
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private JourneyEstimate estimateStatus(String travelDate, String departure, String arrival) {
        LocalDate date = parseDate(travelDate);
        LocalTime departureTime = parseTime(departure);
        LocalTime arrivalTime = parseTime(arrival);
        if (date == null || departureTime == null || arrivalTime == null) {
            return new JourneyEstimate("Scheduled", 0.0);
        }

        LocalDateTime start = LocalDateTime.of(date, departureTime);
        LocalDateTime end = LocalDateTime.of(date, arrivalTime);
        if (!end.isAfter(start)) {
            end = end.plusDays(1);
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(start)) {
            return new JourneyEstimate("Scheduled", 0.0);
        }
        if (!now.isBefore(end)) {
            return new JourneyEstimate("Arrived", 1.0);
        }

        double progress = (double) java.time.Duration.between(start, now).toMinutes()
                / java.time.Duration.between(start, end).toMinutes();
        if (progress < 0.08) {
            return new JourneyEstimate("Departed", 0.2);
        }
        if (progress < 0.8) {
            return new JourneyEstimate("In Transit", Math.max(0.4, progress));
        }
        return new JourneyEstimate("Arriving Soon", 0.85);
    }

    private String getInitialLookup() {
        BookingDTO booking = navigationContext.getCreatedBookingDTO();
        if (booking != null && booking.getBookingReference() != null) {
            return booking.getBookingReference();
        }
        if (navigationContext.getBookingConfirmation() != null && navigationContext.getBookingConfirmation().confirmationNumber != null) {
            return navigationContext.getBookingConfirmation().confirmationNumber;
        }
        BusDTO bus = navigationContext.getSelectedBusDTO();
        return bus != null && bus.getBusId() != null ? String.valueOf(bus.getBusId()) : "";
    }

    private VBox detailField(String caption) {
        VBox field = new VBox(7);
        Label heading = new Label(caption);
        heading.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label value = new Label("—");
        value.setWrapText(true);
        value.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 12px; -fx-font-weight: bold;");
        field.getChildren().addAll(heading, value);
        return field;
    }

    private void setDetailValue(VBox field, String value) {
        ((Label) field.getChildren().get(1)).setText(value);
    }

    private VBox progressStep(Label dot, String title, String location) {
        VBox step = new VBox(7, dot, new Label(location.isBlank() ? title : location));
        step.setAlignment(Pos.CENTER);
        step.setMinWidth(90);
        ((Label) step.getChildren().get(1)).setStyle("-fx-text-fill: #334155; -fx-font-size: 11px; -fx-font-weight: bold;");
        return step;
    }

    private Label progressDot() {
        Label dot = new Label("●");
        dot.setStyle(progressDotStyle(false));
        return dot;
    }

    private String progressDotStyle(boolean active) {
        return "-fx-font-size: 19px; -fx-text-fill: " + (active ? "#168a68" : "#cbd5e1") + ";";
    }

    private Region progressLine() {
        Region line = new Region();
        line.setPrefHeight(3);
        line.setMinWidth(24);
        line.setStyle("-fx-background-color: #cbd5e1;");
        return line;
    }

    private String getStatusColor(String status) {
        return switch (status) {
            case "Scheduled" -> "#475569";
            case "Departed" -> "#1565c0";
            case "In Transit" -> "#b45309";
            case "Arriving Soon" -> "#c2410c";
            default -> "#168a68";
        };
    }

    private String formatArrival(String travelDate, String departure, String arrival) {
        LocalDate date = parseDate(travelDate);
        LocalTime arrivalTime = parseTime(arrival);
        LocalTime departureTime = parseTime(departure);
        if (date != null && arrivalTime != null) {
            if (departureTime != null && !arrivalTime.isAfter(departureTime)) {
                date = date.plusDays(1);
            }
            return date.format(DATE_FORMAT) + " at " + arrival;
        }
        return valueOr(arrival, "Not available");
    }

    private String formatDate(String dateValue) {
        LocalDate date = parseDate(dateValue);
        return date != null ? date.format(DATE_FORMAT) : valueOr(dateValue, "Not available");
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(value);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private String valueOr(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String valueOr(String value, String other, String fallback) {
        return value != null && !value.isBlank() ? value : valueOr(other, fallback);
    }

    private static class Journey {
        private final BookingDTO booking;
        private final BusDTO bus;
        private final String travelDate;

        private Journey(BookingDTO booking, BusDTO bus, String travelDate) {
            this.booking = booking;
            this.bus = bus;
            this.travelDate = travelDate;
        }
    }

    private static class JourneyEstimate {
        private final String status;
        private final double progress;

        private JourneyEstimate(String status, double progress) {
            this.status = status;
            this.progress = progress;
        }
    }
}
