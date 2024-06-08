package application;

public class CustomersPhone extends Customer {
    private long phone;

    public CustomersPhone(int customerID, long phone) {
        super(customerID, null, null, null);  // Call the parent constructor for customerID
        this.phone = phone;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }
}
