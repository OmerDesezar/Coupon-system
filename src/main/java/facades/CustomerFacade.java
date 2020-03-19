package facades;

import dao.*;
import entities.*;
import exceptions.*;
import java.time.LocalDate;
import java.util.*;

public class CustomerFacade extends ClientFacade {

    private long customerId;

    public CustomerFacade(){
        couponDAO = new CouponDBDAO();
        customerDAO = new CustomerDBDAO();
        isLoggedIn = false;
    }

    /**
     * A method that checks if the email and password match the database
     * and if they are it logs in to that account
     */
    @Override
    public boolean login(String email, String password){
        isLoggedIn = customerDAO.isCustomerExists(email,password);
        if (isLoggedIn){
            customerId = customerDAO.getByEmail(email).getId();
        }
        return isLoggedIn;
    }

    /**
     * A method that checks all the criteria to purchase a coupon and then
     * if everything is ok adds a purchase to the database
     */
    public void purchaseCoupon(Coupon coupon) throws NotLoggedInException,NotExistsException, CouponException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        if (couponDAO.getCouponByID(coupon.getId()) == null){
            throw new NotExistsException(String.format("There is no coupon with the id %d",coupon.getId()));
        }
        if (coupon.getAmount() == 0){
            throw new CouponException("There are no more coupons of that kind left");
        }
        if (coupon.getEndDate().isBefore(LocalDate.now())){
            throw new CouponException(String.format("The coupon you wish to buy expired in %s",coupon.getEndDate()));
        }
        for (Coupon coupon1 : customerDAO.getCustomerCoupons(customerId)){
            if (coupon.getTitle() == coupon1.getTitle() &&
                coupon.getCompannyID() == coupon1.getCompannyID()){
                throw new CouponException("You already own this coupon");
            }
        }
        couponDAO.addCouponPurchase(customerId,coupon.getId());
        coupon.setAmount(coupon.getAmount() - 1);
        couponDAO.updateCoupon(coupon);
    }

    /**
     * Same method from dbdao just with
     * some logic that might throw exceptions
     */
    public List<Coupon> getCustomerCoupons() throws NotLoggedInException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        return customerDAO.getCustomerCoupons(customerId);
    }

    /**
     * Same method as the one above just with a specific filter
     */
    public List<Coupon> getCouponsByCategory(Category category) throws NotLoggedInException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        List<Coupon> categoryCoupons = new ArrayList<>();
        for (Coupon coupon : customerDAO.getCustomerCoupons(customerId)){
            if (coupon.getCategory() == category){
                categoryCoupons.add(coupon);
            }
        }
        return categoryCoupons;
    }

    /**
     * Same method as the one above just with a specific filter
     */
    public List<Coupon> getCouponsByPrice(double price) throws NotLoggedInException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        List<Coupon> priceCoupons = new ArrayList<>();
        for (Coupon coupon : customerDAO.getCustomerCoupons(customerId)){
            if (coupon.getPrice() <= price){
                priceCoupons.add(coupon);
            }
        }
        return priceCoupons;
    }

    /**
     * return the info of the customer logged in
     */
    public Customer getInfo() throws NotLoggedInException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        return customerDAO.getCustomerByID(customerId);
    }
}
