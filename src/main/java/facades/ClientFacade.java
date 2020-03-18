package facades;

import dao.*;

public abstract class ClientFacade {
    protected CompanyDBDAO companyDAO;
    protected CustomerDBDAO customerDAO;
    protected CouponDBDAO couponDAO;
    protected boolean isLoggedIn;

    public boolean login(String email, String password){
        return false;
    }
}
