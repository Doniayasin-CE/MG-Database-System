package application;

public class Advertisers extends Employee {
    private int points;

    public Advertisers(int employeeID, int points) {
        super(employeeID, null, 0, null); // Only employeeID is relevant here
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
