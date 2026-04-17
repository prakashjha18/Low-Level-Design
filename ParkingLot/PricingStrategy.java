package ParkingLot;

import java.time.Duration;
import java.time.LocalDateTime;

public interface PricingStrategy {
    double calculateCost(Ticket ticket, LocalDateTime exitTime);
}

// Concrete Strategy for Hourly Pricing
class HourlyPricingStrategy implements PricingStrategy {
    @Override
    public double calculateCost(Ticket ticket, LocalDateTime exitTime) {
        long hours = Duration.between(ticket.getEntryTime(), exitTime).toHours();
        if (hours == 0) hours = 1; // Minimum 1 hour charge

        double rate = ticket.getVehicle().getType() == VehicleType.COMPACT ? 50.0 : 100.0;
        return hours * rate;
    }
}
