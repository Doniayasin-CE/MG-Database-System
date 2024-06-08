package application;

public class Salesmans extends Employee {
    private int employeeTarget;

    public Salesmans(int employeeID, int employeeTarget) {
        super(employeeID, null, 0, null); // Only employeeID is relevant here
        this.employeeTarget = employeeTarget;
    }

    public int getEmployeeTarget() {
        return employeeTarget;
    }

    public void setEmployeeTarget(int employeeTarget) {
        this.employeeTarget = employeeTarget;
    }
}
