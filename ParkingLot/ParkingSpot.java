package ParkingLot;

public class ParkingSpot {
    private final String spotId;
    private final VehicleType spotType;
    private Vehicle parkedVehicle;

    public ParkingSpot(String spotId, VehicleType spotType) {
        this.spotId = spotId;
        this.spotType = spotType;
    }

    public String getSpotId() { return spotId; }
    public VehicleType getSpotType() { return spotType; }

    public boolean isAvailable() {
        return parkedVehicle == null;
    }

    public boolean canFitVehicle(Vehicle vehicle) {
        // A compact spot can only fit a compact vehicle (Car)
        // A large spot can fit a large vehicle, OR a compact vehicle.
        if (this.spotType == VehicleType.LARGE) {
            return true;
        }
        return vehicle.getType() == VehicleType.COMPACT;
    }

    public void park(Vehicle vehicle) {
        if (!isAvailable()) {
            throw new IllegalStateException("Spot is already occupied.");
        }
        this.parkedVehicle = vehicle;
    }

    public void removeVehicle() {
        this.parkedVehicle = null;
    }
}
