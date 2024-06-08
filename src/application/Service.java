package application;

public class Service {
    private int serviceID;
    private int customerID;
    private int carID;
    private String serviceDescription;

    public Service(int serviceID, int customerID, int carID, String serviceDescription) {
        this.serviceID = serviceID;
        this.customerID = customerID;
        this.carID = carID;
        this.serviceDescription = serviceDescription;
    }

    public int getServiceID() {
        return serviceID;
    }

    public void setServiceID(int serviceID) {
        this.serviceID = serviceID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getCarID() {
        return carID;
    }

    public void setCarID(int carID) {
        this.carID = carID;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }
}
