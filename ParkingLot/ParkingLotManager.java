package ParkingLot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParkingLotManager {
    // Singleton Instance
    private static ParkingLotManager instance;
    private final List<ParkingLevel> levels;
    private final PricingStrategy pricingStrategy;

    private ParkingLotManager() {
        this.levels = new ArrayList<>();
        this.pricingStrategy = new HourlyPricingStrategy();
    }

    public static synchronized ParkingLotManager getInstance() {
        if (instance == null) {
            instance = new ParkingLotManager();
        }
        return instance;
    }

    public void addLevel(ParkingLevel level) {
        levels.add(level);
    }

    public synchronized Ticket parkVehicle(Vehicle vehicle) {
        System.out.println("\n[Entry] Attempting to park " + vehicle.getType() + " (" + vehicle.getLicensePlate() + ")");
        for (ParkingLevel level : levels) {
            ParkingSpot spot = level.findAvailableSpot(vehicle);
            if (spot != null) {
                spot.park(vehicle);
                String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                Ticket ticket = new Ticket(ticketId, vehicle, spot);
                System.out.println("✅ Success! Parked at spot: " + spot.getSpotId() + " | Ticket: " + ticketId);
                return ticket;
            }
        }
        System.out.println("❌ Parking Full! No available spots for " + vehicle.getType());
        return null;
    }

    public synchronized double unparkVehicle(Ticket ticket) {
        System.out.println("\n[Exit] Vehicle leaving: " + ticket.getVehicle().getLicensePlate());
        
        ParkingSpot spot = ticket.getSpot();
        spot.removeVehicle();

        // Normally exitTime is LocalDateTime.now(), but we simulate a 2.5 hour stay for the demo
        LocalDateTime exitTime = ticket.getEntryTime().plusHours(2).plusMinutes(30);
        
        double cost = pricingStrategy.calculateCost(ticket, exitTime);
        System.out.println("✅ Spot " + spot.getSpotId() + " is now free. Total fee: ₹" + cost);
        return cost;
    }
}
