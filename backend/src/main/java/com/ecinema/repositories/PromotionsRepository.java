package com.ecinema.repositories;

import com.ecinema.promotion.Promotions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionsRepository extends JpaRepository<Promotions, Integer> {

    public Promotions findByPromoID(int id);
}
