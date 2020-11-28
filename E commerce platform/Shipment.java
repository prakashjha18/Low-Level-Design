import java.util.*;

public class Shipment{
    private static int numberCounter =0;
    private int shipmentNumber;
    private Date estimatedArrival;
    private Order order;

    public Shipment(Order orde){
        numberCounter += 1;
        this.shipmentNumber = numberCounter;
        this.order = orde;
        this.estimatedArrival = new Date();
        orde.setOrderStatus(OrderStatus.SHIPPED);
    }

    @Override
    public String toString() {
        return "Order{" +
                "estimatedArrival=" + estimatedArrival+
                '}';
    }
}