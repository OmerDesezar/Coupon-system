package loginManager;

import entities.ClientType;
import facades.AdminFacade;
import facades.ClientFacade;
import facades.CompanyFacade;
import facades.CustomerFacade;

public class LoginManager {

    private static LoginManager instance;

    public static LoginManager getInstance(){
        if (instance == null){
            instance = new LoginManager();
        }
        return instance;
    }

    private LoginManager(){

    }

    public ClientFacade login(String email, String password, ClientType clientType){
        ClientFacade clientFacade = null;
        switch (clientType){
            case ADMIN:
                clientFacade = new AdminFacade();
                break;
            case COMPANY:
                clientFacade = new CompanyFacade();
                break;
            case CUSTOMER:
                clientFacade = new CustomerFacade();
                break;
        }
        if (clientFacade.login(email,password)){
            return clientFacade;
        }
        return null;
    }
}
