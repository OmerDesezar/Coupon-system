package facades;

import dao.CompanyDBDAO;
import dao.CouponDBDAO;
import dao.CustomerDBDAO;
import entities.Category;
import entities.Company;
import entities.Coupon;
import entities.Customer;
import exceptions.AlreadyExistsException;
import exceptions.NotExistsException;
import exceptions.NotLoggedInException;

import java.util.ArrayList;
import java.util.List;

public class CompanyFacade extends ClientFacade {

    private long companyId;

    public CompanyFacade(){
        companyDAO = new CompanyDBDAO();
        couponDAO = new CouponDBDAO();
        customerDAO = new CustomerDBDAO();
        isLoggedIn = false;
    }

    @Override
    public boolean login(String email, String password){
        isLoggedIn = companyDAO.isCompanyExists(email,password);
        if (isLoggedIn){
            companyId = companyDAO.getByEmail(email).getId();
        }
        return isLoggedIn;
    }

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

    public void updateCoupon(Coupon coupon) throws NotLoggedInException, NotExistsException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        if (couponDAO.getCouponByID(coupon.getId()) == null){
            throw new NotExistsException(String.format("There is no coupon with the id %d",coupon.getId()));
        }
        coupon = couponDAO.updateCoupon(coupon);
    }

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

    public List<Coupon> getCompanyCoupons() throws NotLoggedInException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        return companyDAO.getCompanyCoupons(companyId);
    }

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

    public Company getInfo() throws NotLoggedInException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        return companyDAO.getCompanyByID(companyId);
    }
}
