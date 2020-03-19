package facades;

import dao.*;
import entities.*;
import exceptions.*;
import java.util.List;

public class AdminFacade extends ClientFacade {

    public AdminFacade(){
        companyDAO = new CompanyDBDAO();
        customerDAO = new CustomerDBDAO();
        isLoggedIn = false;
    }

    /**
     * A method that checks if the email and password match the database
     * and if they are it logs in to that account
     */
    @Override
    public boolean login(String email, String password){
        String adminEmail = "admin@admin.com";
        String adminPass = "admin";
        isLoggedIn = (email.equalsIgnoreCase(adminEmail) && password.equals(adminPass));
        return isLoggedIn;
    }

    /**
     * Same method from dbdao just with
     * some logic that might throw exceptions
     */
    public Company addCompany(Company company) throws NotLoggedInException, AlreadyExistsException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        if ((companyDAO.getByName(company.getName())!= null) || (companyDAO.getByEmail(company.getEmail()) != null)){
            throw new AlreadyExistsException(String.format("The email %s or name %s are already in use", company.getEmail(), company.getName()));
        }
        company = companyDAO.addCompany(company);
        return company;
    }

    /**
     * Same method from dbdao just with
     * some logic that might throw exceptions
     */
    public void updateCompany(Company company) throws NotLoggedInException, NotExistsException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        if (companyDAO.getCompanyByID(company.getId()) == null){
            throw new NotExistsException(String.format("There is no company with the id %d",company.getId()));
        }
        company = companyDAO.updateCompany(company);
    }

    /**
     * Same method from dbdao just with
     * some logic that might throw exceptions
     * also deletes all coupons from that company and all coupon purchases related
     */
    public void deleteCompany(long companyID) throws NotLoggedInException, NotExistsException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        if (companyDAO.getCompanyByID(companyID) == null){
            throw new NotExistsException(String.format("There is no company with the id %d",companyID));
        }
        List<Coupon> toDelete = companyDAO.getCompanyCoupons(companyID);
        List<Customer> allCustomers = customerDAO.getAllCustomers();
        for (Coupon coupon : toDelete){
                couponDAO.deleteCouponPurchaseByCouponID(coupon.getId());
            couponDAO.deleteCoupon(coupon.getId());
        }
        companyDAO.deleteCompany(companyID);
    }

    /**
     * Same method from dbdao just with
     * some logic that might throw exceptions
     */
    public List<Company> getAllCompanies() throws NotLoggedInException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        return companyDAO.getAllCompanies();
    }

    /**
     * Same method from dbdao just with
     * some logic that might throw exceptions
     */
    public Company getCompanyByID(long companyID) throws NotLoggedInException, NotExistsException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        if (companyDAO.getCompanyByID(companyID) == null){
            throw new NotExistsException(String.format("There is no company with the id %d", companyID));
        }
        return companyDAO.getCompanyByID(companyID);
    }

    /**
     * Same method from dbdao just with
     * some logic that might throw exceptions
     */
    public Customer addCustomer(Customer customer) throws NotLoggedInException, AlreadyExistsException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        if (customerDAO.getByEmail(customer.getEmail()) != null){
            throw new AlreadyExistsException(String.format("The email %s is already in use", customer.getEmail()));
        }
        customer = customerDAO.addCustomer(customer);
        return customer;
    }

    /**
     * Same method from dbdao just with
     * some logic that might throw exceptions
     */
    public void updateCustomer(Customer customer) throws NotLoggedInException, NotExistsException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        if (customerDAO.getCustomerByID(customer.getId()) == null){
            throw new NotExistsException(String.format("There is no customer with the id %d", customer.getId()));
        }
        customer = customerDAO.updateCustomer(customer);
    }

    /**
     * Same method from dbdao just with
     * some logic that might throw exceptions
     * also deletes all coupons purchased by that customer from the database
     */
    public void deleteCustomer(long customerId)throws NotLoggedInException, NotExistsException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        if (customerDAO.getCustomerByID(customerId) == null){
            throw new NotExistsException(String.format("There is no customer with the id %d", customerId));
        }
        List<Coupon> toDelete = customerDAO.getCustomerCoupons(customerId);
        for (Coupon coupon : toDelete){
            couponDAO.deleteCouponPurchase(customerId,coupon.getId());
        }
        customerDAO.deleteCustomer(customerId);
    }

    /**
     * Same method from dbdao just with
     * some logic that might throw exceptions
     */
    public List<Customer> getAllCustomers()throws NotLoggedInException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        return customerDAO.getAllCustomers();
    }

    /**
     * Same method from dbdao just with
     * some logic that might throw exceptions
     */
    public Customer getCustomerByID(long customerID)throws NotLoggedInException, NotExistsException{
        if (!isLoggedIn){
            throw new NotLoggedInException();
        }
        if (customerDAO.getCustomerByID(customerID) == null){
            throw new NotExistsException(String.format("There is no customer with the id %d", customerID));
        }
        return customerDAO.getCustomerByID(customerID);
    }
}
