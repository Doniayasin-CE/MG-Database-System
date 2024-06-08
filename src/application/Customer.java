package application;

public class Customer {
    private int customerID;
    private String customerName;
    private String email;
    private String NIN;

    public Customer(int customerID, String customerName, String email, String NIN) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.email = email;
        this.NIN = NIN;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNIN() {
        return NIN;
    }

    public void setNIN(String NIN) {
        this.NIN = NIN;
    }
}
