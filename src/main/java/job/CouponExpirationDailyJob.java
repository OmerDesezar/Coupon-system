package job;

import dao.CouponDBDAO;
import entities.Coupon;

import java.time.LocalDate;
import java.time.LocalTime;

public class CouponExpirationDailyJob implements Runnable {

    private CouponDBDAO couponDAO;
    private boolean quit;
    private LocalTime checkTime;

    public CouponExpirationDailyJob(){
        couponDAO = new CouponDBDAO();
        quit = false;
        checkTime = LocalTime.of(8,0);
    }

    @Override
    public void run() {

        while (!quit){
            if (LocalTime.now().isBefore(checkTime) &&
            LocalTime.now().plusMinutes(1).isAfter(checkTime)){
                for (Coupon coupon : couponDAO.getAllCoupons()){
                    if (coupon.getEndDate().isAfter(LocalDate.now())){
                        couponDAO.deleteCouponPurchaseByCouponID(coupon.getId());
                        couponDAO.deleteCoupon(coupon.getId());
                    }
                }
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop(){
        quit = true;
    }
}
