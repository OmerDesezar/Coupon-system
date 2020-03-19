package test;

import entities.*;
import facades.*;
import job.CouponExpirationDailyJob;
import loginManager.LoginManager;
import pool.ConnectionPool;

import java.time.LocalDate;
import java.util.ArrayList;

public class Test {
    public static void testAll(){
        CouponExpirationDailyJob cedj = new CouponExpirationDailyJob();
        Thread thread = new Thread(cedj);
        thread.setDaemon(true);
        thread.start();

        try {

            LoginManager loginManager = LoginManager.getInstance();

            AdminFacade adminFacade = (AdminFacade) loginManager.login("admin@admin.com", "admin", ClientType.ADMIN);
            Company company = new Company("max brenner", "chocolate@gmail.com", "444444", new ArrayList<>());
            company = adminFacade.addCompany(company);
            company.setPassword("4567");
            adminFacade.updateCompany(company);
            adminFacade.deleteCompany(company.getId());
            System.out.println("printing all companies: ");
            adminFacade.getAllCompanies().forEach(System.out::println);
            System.out.println("printing the company with id 3: ");
            System.out.println(adminFacade.getCompanyByID(3));
            Customer customer = new Customer("omer", "desezar", "omer@des.com", "omeriscool", new ArrayList<>());
            customer = adminFacade.addCustomer(customer);
            customer.setPassword("omerisVERYcool");
            adminFacade.updateCustomer(customer);
            adminFacade.deleteCustomer(customer.getId());
            System.out.println("printing all customers: ");
            adminFacade.getAllCustomers().forEach(System.out::println);
            System.out.println("printing the customer with id 2: ");
            System.out.println(adminFacade.getCustomerByID(2));

            CompanyFacade companyFacade = (CompanyFacade) loginManager.login("capuchino@gmail.com", "7654", ClientType.COMPANY);
            Coupon coupon = new Coupon
                    (2, Category.LIFESTYLE, "Test coupon", "Test description", LocalDate.now(), LocalDate.now().plusMonths(1), 30, 100, "");
            coupon = companyFacade.addCoupon(coupon);
            coupon.setImage("gjeigghbo.jpg");
            companyFacade.updateCoupon(coupon);
            System.out.println("the company logged in: ");
            System.out.println(companyFacade.getInfo());
            System.out.println("printing all the company coupons");
            companyFacade.getCompanyCoupons().forEach(System.out::println);
            System.out.println("all company coupons with food category: ");
            companyFacade.getCouponsByCategory(Category.FOOD).forEach(System.out::println);
            System.out.println("all company coupons under the price of 200: ");
            companyFacade.getCouponsByPrice(200).forEach(System.out::println);

            CustomerFacade customerFacade = (CustomerFacade) loginManager.login("dash@gmail.com", "1234", ClientType.CUSTOMER);
            customerFacade.purchaseCoupon(coupon);
            System.out.println("the customer logged in: ");
            System.out.println(customerFacade.getInfo());
            System.out.println("all customer coupons: ");
            customerFacade.getCustomerCoupons().forEach(System.out::println);
            System.out.println("all customer coupons with electronics category: ");
            customerFacade.getCouponsByCategory(Category.ELECTRONICS).forEach(System.out::println);
            System.out.println("all customer coupons under the price of 400: ");
            customerFacade.getCouponsByPrice(400).forEach(System.out::println);
            companyFacade.deleteCoupon(coupon.getId());
        } catch (Exception e){
            e.printStackTrace();
        }
        cedj.stop();
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        connectionPool.closeAllConnections();
    }
}
