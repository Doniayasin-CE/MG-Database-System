package application;

public class Managers extends Employee {
    private String monthlyReport;

    public Managers(int employeeID, String monthlyReport) {
        super(employeeID, null, 0, null); // Only employeeID is relevant here
        this.monthlyReport = monthlyReport;
    }

    public String getMonthlyReport() {
        return monthlyReport;
    }

    public void setMonthlyReport(String monthlyReport) {
        this.monthlyReport = monthlyReport;
    }
}
