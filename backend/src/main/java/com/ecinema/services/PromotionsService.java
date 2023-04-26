package com.ecinema.services;

import com.ecinema.models.promotion.Promotions;
import com.ecinema.repositories.PromotionsRepository;
import com.ecinema.models.users.User;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class PromotionsService {

    private final UserService userService;

    private final PromotionsRepository promotionsRepository;

    @Autowired
    public PromotionsService(UserService userService, PromotionsRepository promotionsRepository) {
        this.userService = userService;
        this.promotionsRepository = promotionsRepository;
    }

    public List<Promotions> getPromotions(){
        return promotionsRepository.findAll();
    }

    public Promotions getPromotion(int id){
        return promotionsRepository.findByPromoID(id);
    }


    public Promotions applyPromoCode(String code){
        Promotions promo = promotionsRepository.findByCode(code);
        if(promo == null){
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/uuuu");
        LocalDate currentDate = java.time.LocalDate.now();
        LocalDate endDate = LocalDate.parse(promo.getEndDate(), formatter);

        System.out.println("Current " + currentDate + " end "+ endDate+ " " + endDate.isBefore(currentDate));
        boolean valid = endDate.isBefore(currentDate);
        System.out.println(valid);
        if(valid){
            promo.setCode("expired");
            return promo;
        }
        if(!promo.isSent()){
            promo.setCode("DNE");
            return promo;
        }
        return promo;
    }


    public String savePromotion(Promotions promo, String link) throws MessagingException, UnsupportedEncodingException {
        promo.setSent(false);
        promotionsRepository.save(promo);
        return "/admin/promotions?success";
    }

    public String sendPromotion(int id, String link) throws MessagingException, UnsupportedEncodingException {
        Promotions promo = promotionsRepository.findByPromoID(id);
        List<User> users = userService.getPromoUsers();

        for(User user : users){
            System.out.println(user.getEmail());
            userService.sendPromoEmail(user.getEmail(), link, promo.getCode(), promo.getDiscount());
        }
        promo.setSent(true);
        promotionsRepository.save(promo);
        return "redirect:/admin/promotions?emailSuccess";
    }

    public String editPromo(int id, Promotions promo){
        Promotions promoToUpdate = promotionsRepository.findByPromoID(id);
        if(promoToUpdate == null)
            return "/admin/promotions?error";
        promoToUpdate.setCode(promo.getCode());
        promoToUpdate.setEndDate(promo.getEndDate());
        promoToUpdate.setStartDate(promo.getStartDate());
        promoToUpdate.setDiscount(promo.getDiscount());

        promotionsRepository.save(promoToUpdate);
        return "/admin/promotions?success";
    }

    public String deletePromo(int id){
        promotionsRepository.deleteById(id);
        return "/admin/promotions";
    }
}
