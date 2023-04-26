package com.ecinema.repositories;

import com.ecinema.models.booking.Booking;
import com.ecinema.models.payment.PaymentCards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Booking findByBookingID(int id);

    void deleteByPaymentCards(int id);

    Booking findByPaymentCards(PaymentCards cards);
}
