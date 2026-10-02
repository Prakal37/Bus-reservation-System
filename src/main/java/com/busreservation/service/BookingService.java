package com.busreservation.service;

import com.busreservation.dto.BookingDTO;
import com.busreservation.dto.BookingRequestDTO;
import com.busreservation.entity.Booking;
import com.busreservation.entity.Bus;
import com.busreservation.entity.Payment;
import com.busreservation.entity.User;
import com.busreservation.repository.BookingRepository;
import com.busreservation.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for Booking related operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    /**
     * Fixed convenience & tax amount added to every booking total.
     * Single source of truth for the stored fare - the client cannot change it.
     */
    public static final double CONVENIENCE_FEE = 35.0;

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final BusService busService;
    private final SeatService seatService;

    /**
     * Create a new booking
     */
    @Transactional
    public BookingDTO createBooking(Long userId, BookingRequestDTO bookingRequest) {
        log.info("Creating booking for user: {} on bus: {}", userId, bookingRequest.getBusId());

        validateSeatNumbers(bookingRequest.getSeatNumbers());

        User user = userService.getUserEntityById(userId);
        Bus bus = busService.getBusEntityById(bookingRequest.getBusId());

        seatService.initializeSeatsForBusAndDate(bus, bookingRequest.getJourneyDate());

        if (!seatService.areSeatsAvailable(bus, bookingRequest.getSeatNumbers(), bookingRequest.getJourneyDate())) {
            throw new IllegalArgumentException("One or more selected seats are not available");
        }

        seatService.markSeatsAsBooked(bus, bookingRequest.getSeatNumbers(), bookingRequest.getJourneyDate());

        // Base fare + fixed convenience fee. The backend owns the fee so the
        // stored total can never be tampered with by the client.
        int seatCount = bookingRequest.getSeatNumbers().size();
        double baseFare = bus.getPricePerSeat() * seatCount;
        double totalPrice = baseFare + CONVENIENCE_FEE;

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBus(bus);
        booking.setSeatNumbers(convertSeatNumbersToString(bookingRequest.getSeatNumbers()));
        booking.setNumberOfSeats(seatCount);
        booking.setJourneyDate(bookingRequest.getJourneyDate());
        booking.setTotalPrice(totalPrice);
        booking.setBookingStatus("CONFIRMED");
        booking.setPaymentStatus("PAID");
        booking.setPassengerName(bookingRequest.getPassengerName());
        booking.setPassengerEmail(bookingRequest.getPassengerEmail());
        booking.setPassengerPhone(bookingRequest.getPassengerPhone());
        booking.setBookingReference(generateBookingReference());

        Booking savedBooking = bookingRepository.save(booking);

        Payment payment = new Payment();
        payment.setBooking(savedBooking);
        payment.setAmount(totalPrice);
        payment.setPaymentMethod(bookingRequest.getPaymentMethod() != null ? bookingRequest.getPaymentMethod() : "UPI");
        payment.setPaymentGateway("MOCK_COLLEGE_GATEWAY");
        payment.setPaymentStatus("SUCCESS");
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        payment.setReferenceNumber("REF-" + System.currentTimeMillis());
        payment.setProcessedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        log.info("Booking created successfully with bookingId: {}", savedBooking.getBookingId());
        return toDto(savedBooking);
    }

    /**
     * Get booking by ID
     */
    public BookingDTO getBookingById(Long bookingId) {
        log.info("Fetching booking by bookingId: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with bookingId: " + bookingId));
        return toDto(booking);
    }

    /**
     * Get booking by reference
     */
    public BookingDTO getBookingByReference(String bookingReference) {
        log.info("Fetching booking by reference: {}", bookingReference);
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with reference: " + bookingReference));
        return toDto(booking);
    }

    /**
     * Get all bookings for a user
     */
    public List<BookingDTO> getBookingsByUser(Long userId) {
        log.info("Fetching bookings for user: {}", userId);
        User user = userService.getUserEntityById(userId);
        List<Booking> bookings = bookingRepository.findByUserOrderByBookedAtDesc(user);
        return bookings.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get active bookings for a user
     */
    public List<BookingDTO> getActiveBookingsByUser(Long userId) {
        log.info("Fetching active bookings for user: {}", userId);
        List<Booking> bookings = bookingRepository.findActiveBookingsByUser(userId);
        return bookings.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Confirm booking (after payment)
     */
    @Transactional
    public BookingDTO confirmBooking(Long bookingId) {
        log.info("Confirming booking with bookingId: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with bookingId: " + bookingId));

        booking.setBookingStatus("CONFIRMED");
        booking.setPaymentStatus("PAID");

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking confirmed successfully with bookingId: {}", bookingId);

        return toDto(savedBooking);
    }

    /**
     * Cancel booking
     */
    @Transactional
    public BookingDTO cancelBooking(Long bookingId) {
        log.info("Cancelling booking with bookingId: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with bookingId: " + bookingId));

        if ("CANCELLED".equals(booking.getBookingStatus())) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        // Mark seats as available
        List<Integer> seatNumbers = convertStringToSeatNumbers(booking.getSeatNumbers());
        seatService.markSeatsAsAvailable(booking.getBus(), seatNumbers, booking.getJourneyDate());

        booking.setBookingStatus("CANCELLED");
        booking.setCancelledAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking cancelled successfully with bookingId: {}", bookingId);

        return toDto(savedBooking);
    }

    /**
     * Get bookings by status
     */
    public List<BookingDTO> getBookingsByStatus(String status) {
        log.info("Fetching bookings by status: {}", status);
        List<Booking> bookings = bookingRepository.findByBookingStatus(status);
        return bookings.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get recent bookings (for dashboard)
     */
    public List<BookingDTO> getRecentBookings(Long userId, int limit) {
        log.info("Fetching recent bookings for user: {}", userId);
        List<Booking> bookings = bookingRepository.findActiveBookingsByUser(userId).stream()
                .limit(limit)
                .collect(Collectors.toList());
        return bookings.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Generate unique booking reference
     */
    private String generateBookingReference() {
        return "BUS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Convert seat numbers list to comma-separated string
     */
    private String convertSeatNumbersToString(List<Integer> seatNumbers) {
        return seatNumbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    /**
     * Convert comma-separated seat numbers string to list
     */
    private List<Integer> convertStringToSeatNumbers(String seatNumbers) {
        return java.util.Arrays.stream(seatNumbers.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    /**
     * Validate the seat numbers coming from the booking request before they
     * touch the database. Rejects empty lists, non positive numbers and
     * duplicates (duplicates would inflate the seat count and the fare).
     */
    private void validateSeatNumbers(List<Integer> seatNumbers) {
        if (seatNumbers == null || seatNumbers.isEmpty()) {
            throw new IllegalArgumentException("At least one seat number is required");
        }
        for (Integer seatNumber : seatNumbers) {
            if (seatNumber == null || seatNumber <= 0) {
                throw new IllegalArgumentException("Invalid seat number: " + seatNumber);
            }
        }
        long distinctCount = seatNumbers.stream().distinct().count();
        if (distinctCount != seatNumbers.size()) {
            throw new IllegalArgumentException("Duplicate seat numbers are not allowed");
        }
    }

    /**
     * Map a Booking entity to its DTO. Route and city details are read through
     * the bus relation so the booking history screen can show a full itinerary.
     */
    private BookingDTO toDto(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setBookingId(booking.getBookingId());
        dto.setUserId(booking.getUser() != null ? booking.getUser().getUserId() : null);
        dto.setBusId(booking.getBus() != null ? booking.getBus().getBusId() : null);
        dto.setSeatNumbers(booking.getSeatNumbers());
        dto.setNumberOfSeats(booking.getNumberOfSeats());
        dto.setJourneyDate(booking.getJourneyDate());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setBookingStatus(booking.getBookingStatus());
        dto.setPaymentStatus(booking.getPaymentStatus());
        dto.setPassengerName(booking.getPassengerName());
        dto.setPassengerEmail(booking.getPassengerEmail());
        dto.setPassengerPhone(booking.getPassengerPhone());
        dto.setBookingReference(booking.getBookingReference());
        dto.setBookedAt(booking.getBookedAt());
        return dto;
    }

    /**
     * Get Booking entity by ID (for internal use)
     */
    public Booking getBookingEntityById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with bookingId: " + bookingId));
    }
}
