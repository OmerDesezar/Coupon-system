package dao;

import entities.Category;
import entities.Coupon;
import entities.Customer;
import exceptions.AlreadyExistsException;
import exceptions.NotExistsException;
import pool.ConnectionPool;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerDBDAO implements CustomerDAO {
    ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public boolean isCustomerExists(String email, String password) {
        Connection connection = connectionPool.getConnection();
        boolean isExists = false;
        String sql = "SELECT * FROM CUSTOMERS WHERE EMAIL = ? AND PASSWORD = ?";
        try (PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setString(1, email);
            prstmt.setString(2, password);
            ResultSet resultSet = prstmt.executeQuery();
            if (resultSet.next()) {
                isExists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return isExists;
    }

    public List<Coupon> getCustomerCoupons(long customerID){
        Connection connection = connectionPool.getConnection();
        List<Coupon> couponList = new ArrayList<>();
        String sql = "SELECT * FROM COUPONS where ID IN" +
                "( SELECT COUPON_ID FROM CUSTOMERS_VS_COUPONS WHERE CUSTOMER_ID = ?)";
        try (PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setLong(1, customerID);
            ResultSet resultSet = prstmt.executeQuery();
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                long companyID = resultSet.getLong(2);
                Category category = getCategoryByID(resultSet.getLong(3));
                String title = resultSet.getString(4);
                String description = resultSet.getString(5);
                LocalDate startDate = resultSet.getDate(6).toLocalDate();
                LocalDate endDate = resultSet.getDate(7).toLocalDate();
                int amount = resultSet.getInt(8);
                double price = resultSet.getDouble(9);
                String image = resultSet.getString(10);
                Coupon coupon = new Coupon(id, companyID, category, title, description, startDate, endDate, amount, price, image);
                couponList.add(coupon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return couponList;
    }

    @Override
    public Customer getCustomerByID(long customerID){
        Customer customer = null;
        Connection connection = connectionPool.getConnection();
        String sql = "SELECT * FROM CUSTOMERS WHERE ID = ?";
        try (PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setLong(1, customerID);
            ResultSet resultSet = prstmt.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                String email = resultSet.getString(4);
                String password = resultSet.getString(5);
                List<Coupon> coupons = getCustomerCoupons(customerID);
                customer = new Customer(customerID,firstName,lastName,email,password,coupons);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return customer;
    }

    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> customerList = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        String sql = "SELECT * FROM CUSTOMERS";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)){
            ResultSet resultSet = prstmt.executeQuery();
            while (resultSet.next()){
                long id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                String email = resultSet.getString(4);
                String password = resultSet.getString(5);
                List<Coupon> coupons = getCustomerCoupons(id);
                Customer customer = new Customer(id, firstName, lastName, email, password, coupons);
                customerList.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return customerList;
    }

    public Customer getByEmail(String email) {
        Customer customer = null;
        Connection connection = connectionPool.getConnection();
        String sql = "SELECT * FROM CUSTOMERS WHERE EMAIL = ?";
        try (PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setString(1, email);
            ResultSet resultSet = prstmt.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                String password = resultSet.getString(5);
                List<Coupon> coupons = getCustomerCoupons(id);
                customer = new Customer(id, firstName, lastName, email, password, coupons);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return customer;
    }

    public Customer getByName(String firstName,String lastName){
        Customer customer = null;
        Connection connection = connectionPool.getConnection();
        String sql = "SELECT * FROM CUSTOMERS WHERE FIRST_NAME = ? AND LAST_NAME = ?";
        try (PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setString(1, firstName);
            prstmt.setString(2, lastName);
            ResultSet resultSet = prstmt.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong(1);
                String email = resultSet.getString(4);
                String password = resultSet.getString(5);
                List<Coupon> coupons = getCustomerCoupons(id);
                customer = new Customer(id,firstName,lastName,email,password,coupons);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return customer;
    }

    @Override
    public Customer addCustomer(Customer customer){
        Connection connection = connectionPool.getConnection();
        String sql = "INSERT INTO CUSTOMERS (FIRST_NAME, LAST_NAME, EMAIL, PASSWORD) VALUES (?,?,?,?)";
        try(PreparedStatement prstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            prstmt.setString(1,customer.getFirstName());
            prstmt.setString(2,customer.getLastName());
            prstmt.setString(3,customer.getEmail());
            prstmt.setString(4,customer.getPassword());
            prstmt.executeUpdate();
            ResultSet resultSet = prstmt.getGeneratedKeys();
            if (resultSet.next()){
                customer.setId(resultSet.getLong(1));
                customer.setCouponList(getCustomerCoupons(customer.getId()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return customer;
    }

    @Override
    public Customer updateCustomer(Customer customer)  {
        Connection connection = connectionPool.getConnection();
        String sql = "UPDATE CUSTOMERS SET FIRST_NAME = ?, LAST_NAME = ?, EMAIL = ?, PASSWORD = ? WHERE ID = ?";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setString(1, customer.getFirstName());
            prstmt.setString(2,customer.getLastName());
            prstmt.setString(3,customer.getEmail());
            prstmt.setString(4,customer.getPassword());
            prstmt.setLong(5,customer.getId());
            prstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return customer;
    }

    @Override
    public Customer deleteCustomer(long customerID) {
        Customer customer = getCustomerByID(customerID);
        Connection connection = connectionPool.getConnection();
        String sql = "DELETE FROM CUSTOMERS WHERE ID = ?";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)){
            prstmt.setLong(1, customerID);
            prstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return customer;
    }
    private Category getCategoryByID(long categoryID){
        Connection connection = connectionPool.getConnection();
        String name = "";
        String sql = "SELECT NAME FROM CATEGORIES WHERE ID = ?";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setLong(1, categoryID);
            ResultSet resultSet = prstmt.executeQuery();
            if (resultSet.next()){
                name = resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            connectionPool.returnConnection(connection);
        }
        return Category.valueOf(name);
    }
}
