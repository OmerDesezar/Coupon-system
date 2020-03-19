package facades;

import dao.*;
import entities.*;
import exceptions.*;
import java.util.*;

public class CompanyFacade extends ClientFacade {

    private long companyId;

    public CompanyFacade(){
        companyDAO = new CompanyDBDAO();
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
        isLoggedIn = companyDAO.isCompanyExists(email,password);
        if (isLoggedIn){
            companyId = companyDAO.getByEmail(email).getId();
        }
        return isLoggedIn;
    }

    /**
     * Same method from dbdao just with
     * some logic that might throw exceptions
     */
    public Coupon addCoupon (Coupon coupon) throws NotLoggedInException, AlreadyExistsException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        for (Coupon coupon1 : couponDAO.getAllCoupons()){
            if (coupon1.getTitle() == coupon.getTitle() &&
                coupon1.getCompannyID() == coupon.getCompannyID()){
                throw new AlreadyExistsException(String.format(
                "A coupon with the title %s from the company %s already exists",coupon.getTitle(),companyDAO.getCompanyByID(coupon.getCompannyID()).getName()));
            }
        }
        coupon = couponDAO.addCoupon(coupon);
        return coupon;
    }

    /**
     * Same method from dbdao just with
     * some logic that might throw exceptions
     */
    public void updateCoupon(Coupon coupon) throws NotLoggedInException, NotExistsException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        if (couponDAO.getCouponByID(coupon.getId()) == null){
            throw new NotExistsException(String.format("There is no coupon with the id %d",coupon.getId()));
        }
        coupon = couponDAO.updateCoupon(coupon);
    }

    /**
     * Same method from dbdao just with
     * some logic that might throw exceptions
     * also deleted all the coupon purchases from the database
     */
    public void deleteCoupon(long couponId) throws NotLoggedInException, NotExistsException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        if (couponDAO.getCouponByID(couponId) == null){
            throw new NotExistsException(String.format("There is no coupon with the id %d",couponId));
        }
        for (Customer customer : customerDAO.getAllCustomers()){
            couponDAO.deleteCouponPurchase(customer.getId(),couponId);
        }
        couponDAO.deleteCoupon(couponId);
    }

    /**
     * Same method from dbdao just with
     * some logic that might throw exceptions
     */
    public List<Coupon> getCompanyCoupons() throws NotLoggedInException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        return companyDAO.getCompanyCoupons(companyId);
    }

    /**
     * Same method as the one above just with a specific filter
     */
    public List<Coupon> getCouponsByCategory(Category category) throws NotLoggedInException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        List<Coupon> categoryCoupons = new ArrayList<>();
        for (Coupon coupon : companyDAO.getCompanyCoupons(companyId)){
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
        for (Coupon coupon : companyDAO.getCompanyCoupons(companyId)){
            if (coupon.getPrice() <= price){
                priceCoupons.add(coupon);
            }
        }
        return priceCoupons;
    }

    /**
     * return the info of the company logged in
     */
    public Company getInfo() throws NotLoggedInException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        return companyDAO.getCompanyByID(companyId);
    }
}
