package oose.euphoria.backend.data;

public interface ICompanyManager {

    boolean companyExists(String companyID);

    void createCompany(String companyID);
}
