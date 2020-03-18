package facades;

import dao.CouponDBDAO;
import dao.CustomerDBDAO;
import entities.Category;
import entities.Coupon;
import entities.Customer;
import exceptions.CouponException;
import exceptions.NotExistsException;
import exceptions.NotLoggedInException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerFacade extends ClientFacade {

    private long customerId;

    public CustomerFacade(){
        couponDAO = new CouponDBDAO();
        customerDAO = new CustomerDBDAO();
        isLoggedIn = false;
    }

    @Override
    public boolean login(String email, String password){
        isLoggedIn = customerDAO.isCustomerExists(email,password);
        if (isLoggedIn){
            customerId = customerDAO.getByEmail(email).getId();
        }
        return isLoggedIn;
    }

    public void purchaseCoupon(Coupon coupon) throws NotLoggedInException,NotExistsException, CouponException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        if (couponDAO.getCouponByID(coupon.getId()) == null){
            throw new NotExistsException("The coupon you wish to buy doesnt exist");
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

    public List<Coupon> getCustomerCoupons() throws NotLoggedInException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        return customerDAO.getCustomerCoupons(customerId);
    }

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

    public Customer getInfo() throws NotLoggedInException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        return customerDAO.getCustomerByID(customerId);
    }
}
