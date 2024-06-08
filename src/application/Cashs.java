package application;

public class Cashs extends Sale {
    private int processNumber;
    private double bankCash;
    private double directCash;
    private float discounts;

    public Cashs(int saleID, int processNumber, double bankCash, double directCash, float discounts) {
        super(saleID, null, 0, 0, 0); // Only saleID is relevant here
        this.processNumber = processNumber;
        this.bankCash = bankCash;
        this.directCash = directCash;
        this.discounts = discounts;
    }

    public int getProcessNumber() {
        return processNumber;
    }

    public void setProcessNumber(int processNumber) {
        this.processNumber = processNumber;
    }

    public double getBankCash() {
        return bankCash;
    }

    public void setBankCash(double bankCash) {
        this.bankCash = bankCash;
    }

    public double getDirectCash() {
        return directCash;
    }

    public void setDirectCash(double directCash) {
        this.directCash = directCash;
    }

    public float getDiscounts() {
        return discounts;
    }

    public void setDiscounts(float discounts) {
        this.discounts = discounts;
    }
}
