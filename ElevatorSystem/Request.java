package ElevatorSystem;

/**
 * Encapsulates a user's request.
 * Can originate from INSIDE the elevator (just a target floor) 
 * or OUTSIDE (source floor + desired direction).
 */
public class Request {
    private final int targetFloor;
    private final boolean isExternal;

    public Request(int targetFloor, boolean isExternal) {
        this.targetFloor = targetFloor;
        this.isExternal = isExternal;
    }

    public int getTargetFloor() { return targetFloor; }
    public boolean isExternal() { return isExternal; }
    
    @Override
    public String toString() {
        return "Floor " + targetFloor + (isExternal ? " (Requested from Hallway)" : " (Button pressed inside)");
    }
}
