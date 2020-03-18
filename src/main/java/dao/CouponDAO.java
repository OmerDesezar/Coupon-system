package dao;

import entities.Coupon;
import exceptions.AlreadyExistsException;
import exceptions.NotExistsException;

import java.util.List;

public interface CouponDAO {
    Coupon addCoupon(Coupon coupon);
    Coupon updateCoupon(Coupon coupon);
    Coupon deleteCoupon(long couponID);
    List<Coupon> getAllCoupons();
    Coupon getCouponByID(long couponID);
    void addCouponPurchase(long customerID, long couponID);
    void deleteCouponPurchase(long customerID, long couponID);
}
