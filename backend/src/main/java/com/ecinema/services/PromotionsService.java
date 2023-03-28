package com.ecinema.services;

import com.ecinema.movie.Movie;
import com.ecinema.promotion.Promotions;
import com.ecinema.repositories.PromotionsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PromotionsService {

    private final PromotionsRepository promotionsRepository;

    @Autowired
    public PromotionsService(PromotionsRepository promotionsRepository) {
        this.promotionsRepository = promotionsRepository;
    }

    public List<Promotions> getPromotions(){
        return promotionsRepository.findAll();
    }

    public String savePromotion(Promotions promo){

        promotionsRepository.save(promo);

        return "/admin/promotions?success";
    }

    public String editPromo(int id, Promotions promo){
        Promotions promoToUpdate = promotionsRepository.findByPromoID(id);
        promoToUpdate.setCode(promo.getCode());
        promoToUpdate.setEndDate(promo.getEndDate());
        promoToUpdate.setStartDate(promo.getStartDate());

        promotionsRepository.save(promoToUpdate);
        return "/admin/promotions?success";
    }

    public String deletePromo(int id){
        promotionsRepository.deleteById(id);
        return "/admin/promotions";
    }
}
