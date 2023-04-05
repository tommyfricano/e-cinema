package com.ecinema.repositories;

import com.ecinema.models.payment.PaymentCards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentCardsRepository extends JpaRepository<PaymentCards, Integer> {
    public PaymentCards deleteByPaymentID(int id);

}
