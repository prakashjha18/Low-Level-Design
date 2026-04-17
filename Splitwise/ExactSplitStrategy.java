package Splitwise;

import java.util.List;

public class ExactSplitStrategy implements SplitStrategy {
    @Override
    public boolean validateRequest(double totalAmount, List<Split> splits) {
        double sum = 0;
        for (Split split : splits) {
            if (!(split instanceof ExactSplit)) {
                return false;
            }
            sum += split.getAmount();
        }
        return Math.round(sum * 100.0) / 100.0 == Math.round(totalAmount * 100.0) / 100.0;
    }

    @Override
    public void calculateAmounts(double totalAmount, List<Split> splits) {
        // Amounts are already predefined in ExactSplit, just need to validate
    }
}
