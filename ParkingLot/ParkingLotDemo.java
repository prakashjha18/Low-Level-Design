package ParkingLot;

public class ParkingLotDemo {
    public static void main(String[] args) {
        System.out.println("\n=== 🚗 Parking Lot LLD Demo ===\n");

        ParkingLotManager manager = ParkingLotManager.getInstance();

        // Setup: 2 levels
        // Level 1: 2 Compact, 1 Large
        manager.addLevel(new ParkingLevel(1, 2, 1));
        // Level 2: 1 Compact, 0 Large
        manager.addLevel(new ParkingLevel(2, 1, 0));

        // Create Vehicles
        Vehicle car1 = new Car("KA-01-1111");
        Vehicle car2 = new Car("KA-02-2222");
        Vehicle car3 = new Car("KA-03-3333");
        Vehicle car4 = new Car("KA-04-4444"); // Will fail to park eventually
        
        Vehicle truck1 = new Truck("MH-12-9999");
        Vehicle truck2 = new Truck("MH-14-8888");

        // Test Scenario 1: Park Cars successfully
        Ticket t1 = manager.parkVehicle(car1); // Goes to L1-C1
        Ticket t2 = manager.parkVehicle(car2); // Goes to L1-C2

        // Test Scenario 2: Park Truck successfully
        Ticket t3 = manager.parkVehicle(truck1); // Goes to L1-L1

        // Test Scenario 3: Park Truck when Full
        Ticket t4 = manager.parkVehicle(truck2); // Should Fail (No large spots left)

        // Test Scenario 4: Park Car in Large Spot if Compact is Full
        Ticket t5 = manager.parkVehicle(car3); // Goes to L2-C1
        Ticket t6 = manager.parkVehicle(car4); // Should Fail (Everything full)

        // Test Scenario 5: Unpark Car and calculate fee
        if (t1 != null) {
            manager.unparkVehicle(t1);
        }

        // Test Scenario 6: Spot becomes correctly available again
        Ticket t7 = manager.parkVehicle(car4); // Should succeed now in L1-C1!
    }
}
