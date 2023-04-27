package com.ecinema.models.businesslogic;

import com.ecinema.models.booking.Booking;
import com.ecinema.models.payment.PaymentCards;
import com.ecinema.models.promotion.Promotions;
import com.ecinema.models.seat.Seat;
import com.ecinema.models.seat.Seats;
import com.ecinema.models.show.Show;
import com.ecinema.models.show.ShowRoom;
import com.ecinema.models.ticket.Ticket;
import com.ecinema.models.users.User;
import com.ecinema.models.dataaccess.BookingRepository;
import com.ecinema.models.dataaccess.PromotionsRepository;
import com.ecinema.models.dataaccess.SeatRepository;
import com.ecinema.models.dataaccess.UserRespository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;

    private final TicketService ticketService;
    private final UserRespository userRespository;

    private final SeatRepository seatRepository;

    private final PromotionsRepository promotionsRepository;


    @Autowired
    public BookingService(BookingRepository bookingRepository, TicketService ticketService, SeatRepository seatRepository, PromotionsRepository promotionsRepository, UserRespository userRespository) {
        this.bookingRepository = bookingRepository;
        this.ticketService = ticketService;
        this.userRespository = userRespository;
        this.seatRepository = seatRepository;
        this.promotionsRepository = promotionsRepository;
    }

    public List<Booking> getBookingsById(int id){
        User user = userRespository.findOneByUserID(id);
        return bookingRepository.findAllByUser(user);
    }

    public Booking createPartialBooking(Show show, List<Ticket> tickets, User user, ShowRoom showRoom, ArrayList<Seats> seats){
        Booking booking = new Booking();
        booking.setShow(show);
        booking.setUser(user);
        List<Ticket> completeTickets = new ArrayList<>();
        double bookingTotal = 0;

        int seatCount = 0;
        List<Seat> selectedSeats = new ArrayList<>();


        for(int i=0 ; i < 48; i++){
            if(seats.get(i).isAvailable() != showRoom.getSeats().get(i).isAvailable()){
                showRoom.getSeats().get(i).setAvailable(seats.get(i).isAvailable());

                Seat seat = new Seat();
                seat.setSeatID(seats.get(i).getSeatID());
                seat.setAvailable(seats.get(i).isAvailable());
                seat.setShowRoomSeating(showRoom);
                seat.setSeatNO(seats.get(i).getSeatNO());
                selectedSeats.add(seat);
                seatCount += 1;
            }
        }


        int totalTickets = tickets.get(0).getCheck() + tickets.get(1).getCheck()+ tickets.get(2).getCheck();
        if(totalTickets != seatCount ){
            return null;
        }

        if (tickets.get(0).getCheck() != 0){
            for(int i=0; i < tickets.get(0).getCheck();i++) {
                Ticket ticket = new Ticket();
                ticket.setType(ticketService.getTicketType("Senior"));
                ticket.setPrice(8);

                ticket.setSeat(selectedSeats.get(i));

                ticket.setBooking(booking);

                bookingTotal += 8;
                completeTickets.add(ticket);
            }
        }
        if (tickets.get(1).getCheck() != 0){
            for(int i=0; i < tickets.get(1).getCheck();i++) {
                Ticket ticket = new Ticket();
                ticket.setType(ticketService.getTicketType("Adult"));
                ticket.setPrice(12);

                ticket.setSeat(selectedSeats.get(i));
                ticket.setBooking(booking);


                bookingTotal += 12;
                completeTickets.add(ticket);
            }
        }
        if (tickets.get(2).getCheck() != 0){
            for(int i=0; i < tickets.get(2).getCheck();i++) {
                Ticket ticket = new Ticket();
                ticket.setType(ticketService.getTicketType("Child"));
                ticket.setPrice(7);

                ticket.setSeat(selectedSeats.get(i));
                ticket.setBooking(booking);

                bookingTotal += 7;
                completeTickets.add(ticket);
            }
        }
        for(Ticket ticket : completeTickets){
            System.out.println(ticket);
        }

        booking.setTotal(bookingTotal);
        booking.setTickets(completeTickets);


        return booking;
    }

    public void completeBooking(Booking booking, PaymentCards paymentCard){
        booking.setPaymentCards(paymentCard);
        if (booking.getPromotions() == null) {
            Promotions promo = promotionsRepository.findByCode("No promo");
//            promotionsRepository.save(promo);
            booking.setPromotions(promo);
            booking.setFinalTotal(booking.getTotal());
        } else {
            double discount = (booking.getPromotions().getDiscount()/100) * (booking.getTotal());
            double finalTotal = booking.getTotal() - discount;
            booking.setFinalTotal(finalTotal);
        }
        bookingRepository.save(booking);
        for(int i=0;i<booking.getTickets().size();i++){
            Ticket ticket = new Ticket();
            ticket.setBooking(booking);
            ticket.setCheck(0);
            ticket.setType(booking.getTickets().get(i).getType());
            ticket.setSeat(booking.getTickets().get(i).getSeat());
            ticket.setPrice(booking.getTickets().get(i).getPrice());
            Seat seat = seatRepository.getSeatBySeatID(ticket.getSeat().getSeatID());
            seat.setAvailable(true);
            ticketService.saveTicket(ticket);
            seatRepository.save(seat);

        }
    }

    public void deleteBookingsByPaymentCardId(int id) {
        bookingRepository.deleteByPaymentCards(id);


    }

    public Booking findByPaymentCards(PaymentCards cards) {
        return bookingRepository.findByPaymentCards(cards);
    }

}
