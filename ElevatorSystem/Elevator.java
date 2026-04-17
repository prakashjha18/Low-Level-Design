package ElevatorSystem;

public class Elevator {
    private final String id;
    private int currentFloor;
    private Direction currentDirection;

    public Elevator(String id) {
        this.id = id;
        this.currentFloor = 0; // Starts at Ground Floor
        this.currentDirection = Direction.IDLE;
    }

    public String getId() { return id; }
    public int getCurrentFloor() { return currentFloor; }
    public Direction getCurrentDirection() { return currentDirection; }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
    }
}
