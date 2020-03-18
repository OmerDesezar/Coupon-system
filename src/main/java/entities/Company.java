package entities;

import java.util.List;

public class Company {
    private long id;
    private String name;
    private String email;
    private String password;
    private List<Coupon> couponList;

    public Company(long id, String name, String email, String password, List<Coupon> couponList) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.couponList = couponList;
    }

    public Company(String name, String email, String password, List<Coupon> couponList) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.couponList = couponList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Coupon> getCouponList() {
        return couponList;
    }

    public void setCouponList(List<Coupon> couponList) {
        this.couponList = couponList;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", couponList=" + couponList +
                '}';
    }
}
