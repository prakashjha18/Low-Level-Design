package Splitwise;

import java.util.List;

/**
 * Interface for Strategy Pattern.
 * Isolates the logic of verifying and calculating the split amounts.
 */
public interface SplitStrategy {
    boolean validateRequest(double totalAmount, List<Split> splits);
    void calculateAmounts(double totalAmount, List<Split> splits);
}
