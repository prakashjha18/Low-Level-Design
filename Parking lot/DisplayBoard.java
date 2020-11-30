public class DisplayBoard{
    public int id;
    public Status ParkingLotStatus;
    public void update(Status newStatus){
        this.ParkingLotStatus = newStatus;
    }
}