package Splitwise;

import java.util.List;

public class EqualSplitStrategy implements SplitStrategy {
    @Override
    public boolean validateRequest(double totalAmount, List<Split> splits) {
        for (Split split : splits) {
            if (!(split instanceof EqualSplit)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void calculateAmounts(double totalAmount, List<Split> splits) {
        int totalSplits = splits.size();
        // Calculate the exact amount up to 2 decimal places
        double splitAmount = Math.round((totalAmount / totalSplits) * 100.0) / 100.0;
        
        for (Split split : splits) {
            split.setAmount(splitAmount);
        }
        
        // Handle precision loss by putting the remainder on the first person
        double calculatedTotal = splitAmount * totalSplits;
        if (calculatedTotal != totalAmount) {
            double remainder = totalAmount - calculatedTotal;
            splits.get(0).setAmount(splits.get(0).getAmount() + remainder);
        }
    }
}
