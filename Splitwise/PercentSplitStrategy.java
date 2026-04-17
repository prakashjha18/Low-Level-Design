package Splitwise;

import java.util.List;

public class PercentSplitStrategy implements SplitStrategy {
    @Override
    public boolean validateRequest(double totalAmount, List<Split> splits) {
        double totalPercent = 0;
        for (Split split : splits) {
            if (!(split instanceof PercentSplit)) {
                return false;
            }
            totalPercent += ((PercentSplit) split).getPercent();
        }
        return Math.round(totalPercent * 100.0) / 100.0 == 100.0;
    }

    @Override
    public void calculateAmounts(double totalAmount, List<Split> splits) {
        for (Split split : splits) {
            PercentSplit percentSplit = (PercentSplit) split;
            double amount = (totalAmount * percentSplit.getPercent()) / 100.0;
            split.setAmount(Math.round(amount * 100.0) / 100.0);
        }
    }
}
