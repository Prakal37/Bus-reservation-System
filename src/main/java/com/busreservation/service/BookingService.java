package com.busreservation.service;

import com.busreservation.dto.BookingDTO;
import com.busreservation.dto.BookingRequestDTO;
import com.busreservation.entity.Booking;
import com.busreservation.entity.Bus;
import com.busreservation.entity.User;
import com.busreservation.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final BusService busService;
    private final SeatService seatService;
    private final ModelMapper modelMapper;

    /**
     * Create a new booking
     */
    @Transactional
    public BookingDTO createBooking(Long userId, BookingRequestDTO bookingRequest) {
        log.info("Creating booking for user: {} on bus: {}", userId, bookingRequest.getBusId());

        // Validate user
        User user = new User();
        user.setUserId(userId);

        // Get bus
        Bus bus = busService.getBusEntityById(bookingRequest.getBusId());

        // Initialize seats if not already done
        seatService.initializeSeatsForBusAndDate(bus, bookingRequest.getJourneyDate());

        // Check if seats are available
        if (!seatService.areSeatsAvailable(bus, bookingRequest.getSeatNumbers(), bookingRequest.getJourneyDate())) {
            throw new IllegalArgumentException("One or more selected seats are not available");
        }

        // Mark seats as booked
        seatService.markSeatsAsBooked(bus, bookingRequest.getSeatNumbers(), bookingRequest.getJourneyDate());

        // Calculate total price
        Double totalPrice = bus.getPricePerSeat() * bookingRequest.getSeatNumbers().size();

        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBus(bus);
        booking.setSeatNumbers(convertSeatNumbersToString(bookingRequest.getSeatNumbers()));
        booking.setNumberOfSeats(bookingRequest.getSeatNumbers().size());
        booking.setJourneyDate(bookingRequest.getJourneyDate());
        booking.setTotalPrice(totalPrice);
        booking.setBookingStatus("PENDING");
        booking.setPaymentStatus("PENDING");
        booking.setPassengerName(bookingRequest.getPassengerName());
        booking.setPassengerEmail(bookingRequest.getPassengerEmail());
        booking.setPassengerPhone(bookingRequest.getPassengerPhone());
        booking.setBookingReference(generateBookingReference());

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with bookingId: {}", savedBooking.getBookingId());

        return modelMapper.map(savedBooking, BookingDTO.class);
    }

    /**
     * Get booking by ID
     */
    public BookingDTO getBookingById(Long bookingId) {
        log.info("Fetching booking by bookingId: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with bookingId: " + bookingId));
        return modelMapper.map(booking, BookingDTO.class);
    }

    /**
     * Get booking by reference
     */
    public BookingDTO getBookingByReference(String bookingReference) {
        log.info("Fetching booking by reference: {}", bookingReference);
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with reference: " + bookingReference));
        return modelMapper.map(booking, BookingDTO.class);
    }

    /**
     * Get all bookings for a user
     */
    public List<BookingDTO> getBookingsByUser(Long userId) {
        log.info("Fetching bookings for user: {}", userId);
        User user = new User();
        user.setUserId(userId);
        List<Booking> bookings = bookingRepository.findByUserOrderByBookedAtDesc(user);
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Get active bookings for a user
     */
    public List<BookingDTO> getActiveBookingsByUser(Long userId) {
        log.info("Fetching active bookings for user: {}", userId);
        List<Booking> bookings = bookingRepository.findActiveBookingsByUser(userId);
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
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

        return modelMapper.map(savedBooking, BookingDTO.class);
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

        return modelMapper.map(savedBooking, BookingDTO.class);
    }

    /**
     * Get bookings by status
     */
    public List<BookingDTO> getBookingsByStatus(String status) {
        log.info("Fetching bookings by status: {}", status);
        List<Booking> bookings = bookingRepository.findByBookingStatus(status);
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
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
                .map(booking -> modelMapper.map(booking, BookingDTO.class))
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
     * Get Booking entity by ID (for internal use)
     */
    public Booking getBookingEntityById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with bookingId: " + bookingId));
    }
}
