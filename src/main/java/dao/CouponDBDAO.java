package dao;

import entities.*;
import pool.ConnectionPool;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CouponDBDAO implements CouponDAO {
    ConnectionPool connectionPool = ConnectionPool.getInstance();

    /**
     * A method that accepts three parameters and compares
     * them to the database to see if the coupon exists there
     */
    public boolean isCouponExists(Category category, String title, String description){
        Connection connection = connectionPool.getConnection();
        boolean exists = false;
        String sql = "SELECT * FROM COUPONS WHERE TITLE = ? AND DESCRIPTION = ? AND CATEGORY_ID = ?";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setString(1,title);
            prstmt.setString(2, description);
            prstmt.setLong(3, getCategoryID(category));
            ResultSet resultSet = prstmt.executeQuery();
            if (resultSet.next()){
                exists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return exists;
    }

    /**
     *A method that gets an id parameter and returns
     *the coupon with that id
     */
    @Override
    public Coupon getCouponByID(long couponID) {
        Connection connection = connectionPool.getConnection();
        Coupon coupon = null;
        String sql = "SELECT * FROM COUPONS WHERE ID = ?";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)) {
        prstmt.setLong(1,couponID);
        ResultSet resultSet = prstmt.executeQuery();
        if (resultSet.next()){
            long companyID = resultSet.getLong(2);
            Category category = getCategoryByID(resultSet.getLong(3));
            String title = resultSet.getString(4);
            String description = resultSet.getString(5);
            LocalDate startDate = resultSet.getDate(6).toLocalDate();
            LocalDate endDate = resultSet.getDate(7).toLocalDate();
            int amount = resultSet.getInt(8);
            double price = resultSet.getDouble(9);
            String image = resultSet.getString(10);
            coupon = new Coupon(couponID, companyID, category, title, description, startDate, endDate, amount, price, image);
           }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return coupon;
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

    /**
     *A method that helps to convert the category
     * from an enum to a number for the database.
     */
    private long getCategoryID(Category category){
        Connection connection = connectionPool.getConnection();
        long id = 0;
        String sql = "SELECT ID FROM CATEGORIES WHERE NAME = ?";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)) {
        prstmt.setString(1, category.toString());
        ResultSet resultSet = prstmt.executeQuery();
        if (resultSet.next()){
            id = resultSet.getLong(1);
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            connectionPool.returnConnection(connection);
        }
        return id;
    }

    /**
     * A method that gets a coupon and adds it
     * to the database, and then adds the id to the object
     */
    @Override
    public Coupon addCoupon(Coupon coupon){
        Connection connection = connectionPool.getConnection();
        String sql = "INSERT INTO COUPONS " +
                "(COMPANY_ID, CATEGORY_ID, TITLE, DESCRIPTION, START_DATE, END_DATE, AMOUNT, PRICE, IMAGE) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement prstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        prstmt.setLong(1, coupon.getCompannyID());
        prstmt.setLong(2, getCategoryID(coupon.getCategory()));
        prstmt.setString(3, coupon.getTitle());
        prstmt.setString(4, coupon.getDescription());
        prstmt.setDate(5, Date.valueOf(coupon.getStartDate()));
        prstmt.setDate(6, Date.valueOf(coupon.getEndDate()));
        prstmt.setInt(7, coupon.getAmount());
        prstmt.setDouble(8,coupon.getPrice());
        prstmt.setString(9, coupon.getImage());
        prstmt.executeUpdate();
        ResultSet resultSet = prstmt.getGeneratedKeys();
        while (resultSet.next()){
            coupon.setId(resultSet.getLong(1));
        }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return coupon;
    }

    /**
     * A method that returns a list with all the
     * coupons from the database
     */
    @Override
    public List<Coupon> getAllCoupons() {
        List<Coupon> coupons = new ArrayList<>();
        Connection connection = connectionPool.getConnection();
        String sql = "SELECT * FROM COUPONS";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)) {
        ResultSet resultSet = prstmt.executeQuery();
        while (resultSet.next()){
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
            coupons.add(new Coupon(id, companyID, category, title, description, startDate, endDate, amount, price, image));
        }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return coupons;
    }

    /**
     * A method that gets a coupon and updates
     * the database to match it
     */
    @Override
    public Coupon updateCoupon(Coupon coupon) {
        Connection connection = connectionPool.getConnection();
        String sql = "UPDATE COUPONS SET CATEGORY_ID = ?, TITLE = ?, DESCRIPTION = ?, START_DATE = ?, END_DATE = ?, AMOUNT =?, PRICE = ?, IMAGE = ? WHERE ID = ?";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setLong(1, getCategoryID(coupon.getCategory()));
            prstmt.setString(2, coupon.getTitle());
            prstmt.setString(3, coupon.getDescription());
            prstmt.setDate(4, Date.valueOf(coupon.getStartDate()));
            prstmt.setDate(5, Date.valueOf(coupon.getEndDate()));
            prstmt.setInt(6, coupon.getAmount());
            prstmt.setDouble(7, coupon.getPrice());
            prstmt.setString(8, coupon.getImage());
            prstmt.setLong(9, coupon.getId());
            prstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return coupon;
    }

    /**
     * A method that gets an id as a parameter and
     * deletes the coupon with that id from the database
     */
    @Override
    public Coupon deleteCoupon(long couponID) {
        Coupon coupon = getCouponByID(couponID);
        Connection connection = connectionPool.getConnection();
        String sql = "DELETE FROM COUPONS WHERE ID = ?";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)){
            prstmt.setLong(1, couponID);
            prstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
        return coupon;
    }

    /**
     * A method that gets two id parameters and
     * saves them together as a purchased coupon by
     * a customer
     */
    @Override
    public void addCouponPurchase(long customerID, long couponID) {
        Connection connection = connectionPool.getConnection();
        String sql = "INSERT INTO CUSTOMERS_VS_COUPONS (CUSTOMER_ID, COUPON_ID) VALUES (?, ?)";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setLong(1, customerID);
            prstmt.setLong(2, couponID);
            prstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
    }

    /**
     * A method that gets two id parameters and
     * deletes them from being a purchased coupon by
     * a customer
     */
    @Override
    public void deleteCouponPurchase(long customerID, long couponID){
        Connection connection = connectionPool.getConnection();
        String sql = "DELETE FROM CUSTOMERS_VS_COUPONS WHERE CUSTOMER_ID = ? AND COUPON_ID = ?";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setLong(1, customerID);
            prstmt.setLong(2, couponID);
            prstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
    }

    /**
     * A method that gets an id parameter and
     * deletes all the purchased with this id
     */
    public void deleteCouponPurchaseByCouponID(long couponID){
        Connection connection = connectionPool.getConnection();
        String sql = "DELETE FROM CUSTOMERS_VS_COUPONS WHERE COUPON_ID = ?";
        try(PreparedStatement prstmt = connection.prepareStatement(sql)) {
            prstmt.setLong(2, couponID);
            prstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionPool.returnConnection(connection);
        }
    }
}
