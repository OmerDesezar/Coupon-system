package dao;

import entities.*;

import java.sql.*;
import java.util.*;
import pool.ConnectionPool;
import java.time.LocalDate;


public class CompanyDBDAO implements CompanyDAO {
    private ConnectionPool connectionPool = ConnectionPool.getInstance();

    /**
     * A method that accepts two parameters and compares
     * them to the database to see if the company exists there
     */
    @Override
    public boolean isCompanyExists(String email, String password) {
        Connection connection = connectionPool.getConnection();
        boolean isExists = false;
        String sql = "SELECT * FROM COMPANIES WHERE EMAIL = ? AND PASSWORD = ?";
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

    /**
     * A method that accepts id parameter to get all
     * the coupons from the company with that id
     */
    public List<Coupon> getCompanyCoupons(long companyID) {
        Connection connection = connectionPool.getConnection();
        List<Coupon> couponList = new ArrayList<>();
        String sql = "SELECT * FROM COUPONS WHERE COMPANY_ID = ?";
        try (PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setLong(1, companyID);
            ResultSet resultSet = prstmt.executeQuery();
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
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

    /**
     *A method that gets an id parameter and returns
     *the company with that id
     */
    @Override
    public Company getCompanyByID(long companyID){
        Company company = null;
        Connection connection = connectionPool.getConnection();
        String sql = "SELECT * FROM COMPANIES WHERE ID = ?";
        try (PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setLong(1, companyID);
            ResultSet resultSet = prstmt.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString(2);
                String email = resultSet.getString(3);
                String password = resultSet.getString(4);
                List<Coupon> coupons = getCompanyCoupons(companyID);
                company = new Company(companyID,name,email,password,coupons);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return company;
    }

    /**
     * A method that returns a list with all the
     * companies from the database
     */
    @Override
    public List<Company> getAllCompanies() {
        List<Company> companyList = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        String sql = "SELECT * FROM COMPANIES";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)){
            ResultSet resultSet = prstmt.executeQuery();
            while (resultSet.next()){
                long id = resultSet.getLong(1);
                String name = resultSet.getString(2);
                String email = resultSet.getString(3);
                String password = resultSet.getString(4);
                List<Coupon> coupons = getCompanyCoupons(id);
                Company company = new Company(id,name,email,password,coupons);
                companyList.add(company);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return companyList;
    }

    /**
     * A method that gets a string parameter and returns
     * the company that matches it from the database
     */
    public Company getByEmail(String email){
        Company company = null;
        Connection connection = connectionPool.getConnection();
        String sql = "SELECT * FROM COMPANIES WHERE EMAIL = ?";
        try (PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setString(1, email);
            ResultSet resultSet = prstmt.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong(1);
                String name = resultSet.getString(2);
                String password = resultSet.getString(4);
                List<Coupon> coupons = getCompanyCoupons(id);
                company = new Company(id,name,email,password,coupons);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return company;
    }

    /**
     * A method that gets a string parameter and returns
     * the company that matches it from the database
     */
    public Company getByName(String name){
        Company company = null;
        Connection connection = connectionPool.getConnection();
        String sql = "SELECT * FROM COMPANIES WHERE NAME = ?";
        try (PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setString(1, name);
            ResultSet resultSet = prstmt.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong(1);
                String email = resultSet.getString(3);
                String password = resultSet.getString(4);
                List<Coupon> coupons = getCompanyCoupons(id);
                company = new Company(id,name,email,password,coupons);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return company;
    }

    /**
     * A method that gets a company and adds it
     * to the database, and then adds the id to the object
     */
    @Override
    public Company addCompany(Company company) {
        Connection connection = connectionPool.getConnection();
        String sql = "INSERT INTO COMPANIES (NAME,EMAIL,PASSWORD) VALUES (?,?,?)";
        try(PreparedStatement prstmt = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            prstmt.setString(1, company.getName());
            prstmt.setString(2,company.getEmail());
            prstmt.setString(3,company.getPassword());
            prstmt.executeUpdate();
            ResultSet resultSet = prstmt.getGeneratedKeys();
            if (resultSet.next()){
                company.setId(resultSet.getLong(1));
                company.setCouponList(getCompanyCoupons(company.getId()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return company;
    }

    /**
     * A method that gets a company and updates
     * the database to match it
     */
    @Override
    public Company updateCompany(Company company){
        Connection connection = connectionPool.getConnection();
        String sql = "UPDATE COMPANIES SET EMAIL = ?, PASSWORD = ? WHERE ID = ?";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setString(1,company.getEmail());
            prstmt.setString(2,company.getPassword());
            prstmt.setLong(3,company.getId());
            prstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return company;
    }

    /**
     * A method that gets an id as a parameter and
     * deletes the company with that id from the database
     */
    @Override
    public Company deleteCompany(long companyID){
        Company company = getCompanyByID(companyID);
        Connection connection = connectionPool.getConnection();
        String sql = "DELETE FROM COMPANIES WHERE ID = ?";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)){
            prstmt.setLong(1, companyID);
            prstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return company;
    }

    /**
     *A method that helps to convert the category
     * from a number(in the database) to an enum for the java object.
     */
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
