package ElevatorSystem;

import java.util.Collections;
import java.util.PriorityQueue;

/**
 * Controller using the SCAN/LOOK Algorithm.
 * Why? Instead of moving erratically (First-Come-First-Serve), the elevator
 * continues in one direction picking up everyone along the way, then switches direction.
 */
public class ElevatorController {
    private final Elevator elevator;

    // UP Queue: Min-Heap (Process lowest floors first as we go up)
    private final PriorityQueue<Integer> upQueue;
    
    // DOWN Queue: Max-Heap (Process highest floors first as we go down)
    private final PriorityQueue<Integer> downQueue;

    public ElevatorController(Elevator elevator) {
        this.elevator = elevator;
        this.upQueue = new PriorityQueue<>();
        this.downQueue = new PriorityQueue<>(Collections.reverseOrder());
    }

    public void acceptRequest(Request request) {
        System.out.println("📥 Request received: " + request.toString());
        
        int targetFloor = request.getTargetFloor();
        int currentFloor = elevator.getCurrentFloor();

        if (targetFloor == currentFloor) {
            System.out.println("✅ Elevator is already at floor " + targetFloor);
            return;
        }

        // Logic to assign the request to the correct queue
        if (elevator.getCurrentDirection() == Direction.IDLE) {
            // Wake up and set direction based on the first request
            if (targetFloor > currentFloor) {
                elevator.setCurrentDirection(Direction.UP);
                upQueue.offer(targetFloor);
            } else {
                elevator.setCurrentDirection(Direction.DOWN);
                downQueue.offer(targetFloor);
            }
        } else if (elevator.getCurrentDirection() == Direction.UP) {
            if (targetFloor >= currentFloor) {
                upQueue.offer(targetFloor); // Still on the way UP
            } else {
                downQueue.offer(targetFloor); // Missed it, queue for the DOWN sweep
            }
        } else if (elevator.getCurrentDirection() == Direction.DOWN) {
            if (targetFloor <= currentFloor) {
                downQueue.offer(targetFloor); // Still on the way DOWN
            } else {
                upQueue.offer(targetFloor); // Missed it, queue for the UP sweep
            }
        }
    }

    /**
     * Executes all pending requests using the SCAN logic.
     */
    public void startElevator() {
        while (!upQueue.isEmpty() || !downQueue.isEmpty()) {
            if (elevator.getCurrentDirection() == Direction.UP) {
                processUpRequests();
                // After finishing all UP requests, switch DOWN if any exist
                if (!downQueue.isEmpty()) {
                    System.out.println("\n🔄 Switching direction to DOWN...");
                    elevator.setCurrentDirection(Direction.DOWN);
                } else {
                    elevator.setCurrentDirection(Direction.IDLE);
                }
            } else if (elevator.getCurrentDirection() == Direction.DOWN) {
                processDownRequests();
                // After finishing all DOWN requests, switch UP if any exist
                if (!upQueue.isEmpty()) {
                    System.out.println("\n🔄 Switching direction to UP...");
                    elevator.setCurrentDirection(Direction.UP);
                } else {
                    elevator.setCurrentDirection(Direction.IDLE);
                }
            }
        }
        System.out.println("💤 Elevator is now IDLE at floor " + elevator.getCurrentFloor());
    }

    private void processUpRequests() {
        while (!upQueue.isEmpty()) {
            int nextFloor = upQueue.poll();
            moveElevatorTo(nextFloor);
        }
    }

    private void processDownRequests() {
        while (!downQueue.isEmpty()) {
            int nextFloor = downQueue.poll();
            moveElevatorTo(nextFloor);
        }
    }

    private void moveElevatorTo(int targetFloor) {
        // Skip duplicate presses for the same floor
        if (targetFloor == elevator.getCurrentFloor()) {
            return;
        }
        
        System.out.print("Moving " + elevator.getCurrentDirection() + " ... ");
        try {
            // Simulating movement time (Removed long sleep for rapid demo execution)
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        elevator.setCurrentFloor(targetFloor);
        System.out.println("🚪 Opened doors at floor " + targetFloor);
    }
}
