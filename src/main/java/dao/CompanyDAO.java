package dao;

import entities.Company;
import exceptions.AlreadyExistsException;
import exceptions.NotExistsException;

import java.util.List;
import java.util.Set;

public interface CompanyDAO {
     boolean isCompanyExists(String email,String password);
     Company getCompanyByID(long CompanyID);
     List<Company> getAllCompanies();
     Company addCompany(Company company);
     Company updateCompany(Company company);
     Company deleteCompany (long CompanyID);
}
