package com.ecinema.repositories;

import com.ecinema.payment.PaymentCards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface PaymentCardsRepository extends JpaRepository<PaymentCards, Integer> {
    public PaymentCards deleteByPaymentID(int id);

}
