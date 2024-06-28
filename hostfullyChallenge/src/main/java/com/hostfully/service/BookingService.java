package com.hostfully.service;

import com.hostfully.entity.Block;
import com.hostfully.entity.Booking;
import com.hostfully.repository.BlockRepository;
import com.hostfully.repository.BookingRepository;
import com.hostfully.service.dao.status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Transactional
    public Booking createBooking(Booking booking) {
        validateBookingDates(booking);
        Booking savedBooking = bookingRepository.save(booking);

        Block block = new Block();
        block.setStartDate(booking.getStartDate());
        block.setEndDate(booking.getEndDate());
        block.setPlace(booking.getPlace());
        blockRepository.save(block);

        return savedBooking;
    }

    @Transactional
    public Booking updateBooking(Long id, Booking booking) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setId(id);
        validateBookingDates(booking);
        return bookingRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isPresent()) {
            bookingRepository.delete(booking.get());
        } else {
            throw new IllegalArgumentException("Block not found");
        }
    }

    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        List<Booking> blocks = bookingRepository.findAll();
        if (blocks.isEmpty()) {
            throw new IllegalArgumentException("No blocks found");
        }
        return blocks;
    }

    public List<Booking> findBookingsByPersonId(Long personId) {
        return bookingRepository.findByGuestId(personId);
    }

    @Transactional
    public Booking cancel(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus(status.CANCELED);
            bookingRepository.save(booking);

            Optional<Block> optionalBlock = blockRepository.findById(bookingId);
            optionalBlock.ifPresent(blockRepository::delete);

            return booking;
        } else {
            throw new RuntimeException("Booking not found with id: " + bookingId);
        }
    }

    @Transactional
    public Booking rebook(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(status.BOOKED);
        validateBookingDates(booking);
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    private void validateBookingDates(Booking booking) {
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                booking.getStartDate(), booking.getEndDate(), booking.getId());
        if (!overlappingBookings.isEmpty()) {
            throw new RuntimeException("Booking dates overlap with existing bookings");
        }

        List<Block> overlappingBlocks = blockRepository.findOverlappingBlocks(
                booking.getStartDate(), booking.getEndDate());
        if (!overlappingBlocks.isEmpty()) {
            throw new RuntimeException("Booking dates overlap with existing blocks");
        }
    }
}
