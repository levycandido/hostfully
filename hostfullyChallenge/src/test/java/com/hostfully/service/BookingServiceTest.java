package com.hostfully.service;

import com.hostfully.entity.*;
import com.hostfully.repository.BookingRepository;
import com.hostfully.repository.BlockRepository;
import com.hostfully.service.dao.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BlockRepository blockRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateBooking() {

        Customer customer = new Customer(1L, "123", "levycandido@hotmail.com");
        Guest guest = new Guest(1L, "John Doe", customer);


        Person guest = new Guest(1L, "John Doe", customer);
        Place property = new Place(1L, "Test Property", "Test Location");
        Booking booking = new Booking(1L, LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 7), status.PENDING, guest, property);

        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingRepository.findOverlappingBookings(any(LocalDate.class), any(LocalDate.class), anyLong()))
                .thenReturn(Collections.emptyList());
        when(blockRepository.findOverlappingBlocks(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        Booking createdBooking = bookingService.createBooking(booking);

        assertEquals(booking, createdBooking);
    }

    @Test
    public void testCreateBookingWithOverlappingDates() {
        Customer customer = new Customer(1L, "123", "levycandido@hotmail.com");
        Guest guest = new Guest(1L, "John Doe", customer);

//        Customer customer = new Customer(1L, "123", "levycandido@hotmail.com");
        Guest guest = new Guest(1L, "John Doe", customer);
        Person guest = new Guest(1L, "John Doe", customer);
        Place property = new Place(1L, "Test Property", "Test Location");
        Booking booking = new Booking(1L, LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 7), status.PENDING, guest, property);

        when(bookingRepository.findOverlappingBookings(any(LocalDate.class), any(LocalDate.class), anyLong()))
                .thenReturn(Collections.singletonList(new Booking()));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            bookingService.createBooking(booking);
        });

        assertEquals("Booking dates overlap with existing bookings", exception.getMessage());
    }

    @Test
    public void testCreateBookingWithOverlappingBlocks() {

        Person guest = new Guest(1L, "John Doe");
        Place property = new Place(1L, "Test Property", "Test Location");
        Booking booking = new Booking(1L, LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 7), status.PENDING, guest, property);

        when(bookingRepository.findOverlappingBookings(any(LocalDate.class), any(LocalDate.class), anyLong()))
                .thenReturn(Collections.emptyList());
        when(blockRepository.findOverlappingBlocks(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(new Block()));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            bookingService.createBooking(booking);
        });

        assertEquals("Booking dates overlap with existing blocks", exception.getMessage());
    }

    @Test
    public void testGetAllBookings() {
        Person guest1 = new Guest(1L, "John Doe");
        Place property1 = new Place(1L, "Test Property", "Test Location");
        Booking booking1 = new Booking(1L, LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 7), status.PENDING, guest1, property1);

        Person guest2 = new Guest(1L, "John Doe");
        Place property2 = new Place(1L, "Test Property", "Test Location");
        Booking booking2 = new Booking(1L, LocalDate.of(2023, 7, 10), LocalDate.of(2023, 7, 15), status.PENDING, guest2, property2);

        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> result = bookingService.getAllBookings();

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getGuest().getName());
        assertEquals("Jane Doe", result.get(0).getGuest().getName());
    }
}
