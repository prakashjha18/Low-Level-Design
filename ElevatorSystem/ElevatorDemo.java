package ElevatorSystem;

public class ElevatorDemo {
    public static void main(String[] args) {
        System.out.println("\n=== 🛗 Elevator System LLD Demo (SCAN Algorithm) ===\n");

        Elevator elevator = new Elevator("ELEV-1");
        ElevatorController controller = new ElevatorController(elevator);

        System.out.println("Elevator initialized. At floor: " + elevator.getCurrentFloor() + ", Status: " + elevator.getCurrentDirection());
        
        // Simulating rapid-fire requests coming in asynchronously
        System.out.println("\n--- Receiving Requests ---");
        
        // User at floor 0 presses 5
        controller.acceptRequest(new Request(5, false));
        
        // While moving up, another user at floor 3 presses UP (wants to go to 7)
        controller.acceptRequest(new Request(3, true));
        controller.acceptRequest(new Request(7, false));
        
        // While moving up, a user at floor 2 presses DOWN (wants to go to 0) -> Must wait for DOWN sweep
        controller.acceptRequest(new Request(2, true));
        controller.acceptRequest(new Request(0, false));
        
        // Another request way up top
        controller.acceptRequest(new Request(9, true));

        // Start processing requests using SCAN 
        System.out.println("\n--- Processing Requests ---");
        controller.startElevator();
    }
}
