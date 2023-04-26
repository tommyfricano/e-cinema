package com.ecinema.models.businesslogic.repositories;

import com.ecinema.models.booking.Booking;
import com.ecinema.models.users.User;
import com.ecinema.models.payment.PaymentCards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    public List<Booking> findAllByUser(User user);
    Booking findByBookingID(int id);

    void deleteByPaymentCards(int id);

    Booking findByPaymentCards(PaymentCards cards);
}
