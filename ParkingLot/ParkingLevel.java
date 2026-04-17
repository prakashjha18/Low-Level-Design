package ParkingLot;

import java.util.ArrayList;
import java.util.List;

public class ParkingLevel {
    private final int levelNumber;
    private final List<ParkingSpot> spots;

    public ParkingLevel(int levelNumber, int compactSpotsCount, int largeSpotsCount) {
        this.levelNumber = levelNumber;
        this.spots = new ArrayList<>();
        
        for (int i = 1; i <= compactSpotsCount; i++) {
            spots.add(new ParkingSpot("L" + levelNumber + "-C" + i, VehicleType.COMPACT));
        }
        for (int i = 1; i <= largeSpotsCount; i++) {
            spots.add(new ParkingSpot("L" + levelNumber + "-L" + i, VehicleType.LARGE));
        }
    }

    public ParkingSpot findAvailableSpot(Vehicle vehicle) {
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable() && spot.canFitVehicle(vehicle)) {
                return spot;
            }
        }
        return null;
    }
}
