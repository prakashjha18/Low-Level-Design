package Splitwise;

public class ExactSplit extends Split {
    public ExactSplit(User user, double exactAmount) {
        super(user);
        this.setAmount(exactAmount);
    }
}
