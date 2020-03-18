package dao;

import entities.Customer;
import exceptions.AlreadyExistsException;
import exceptions.NotExistsException;

import java.util.List;

public interface CustomerDAO {
    boolean isCustomerExists(String email,String password);
    Customer getCustomerByID(long CustomerID);
    List<Customer> getAllCustomers();
    Customer addCustomer(Customer customer);
    Customer updateCustomer(Customer customer);
    Customer deleteCustomer (long CustomerID);
}
