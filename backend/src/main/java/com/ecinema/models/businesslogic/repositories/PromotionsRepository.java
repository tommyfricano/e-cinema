package com.ecinema.models.businesslogic.repositories;

import com.ecinema.models.promotion.Promotions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionsRepository extends JpaRepository<Promotions, Integer> {

    public Promotions findByPromoID(int id);

    public Promotions findByCode(String code);
}
