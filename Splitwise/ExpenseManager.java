package Splitwise;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseManager {
    // Map<User Owed, Map<User Owes, Amount>>
    // The core data structure of splitwise tracking balances between any two users
    private final Map<String, Map<String, Double>> balanceSheet;
    private final Map<String, User> userMap;

    public ExpenseManager() {
        this.balanceSheet = new HashMap<>();
        this.userMap = new HashMap<>();
    }

    public void addUser(User user) {
        userMap.put(user.getId(), user);
        balanceSheet.put(user.getId(), new HashMap<>());
    }

    public void addExpense(ExpenseType expenseType, double amount, String paidByUserId, List<Split> splits) {
        // Instantiate the right strategy for calculation
        SplitStrategy strategy = getStrategy(expenseType);

        if (!strategy.validateRequest(amount, splits)) {
            System.out.println("❌ Validation failed for " + expenseType + " expense.");
            return;
        }

        // Calculate and set the split amounts dynamically based on type
        strategy.calculateAmounts(amount, splits);

        // Update balances
        for (Split split : splits) {
            String paidToUserId = split.getUser().getId();
            double oweAmount = split.getAmount();

            if (paidByUserId.equals(paidToUserId)) {
                continue; // You don't owe yourself
            }

            // Scenario: Alice pays 100 for Bob. Bob owes Alice 100.
            // BalanceSheet tracks: Alice -> {Bob: 100}  and Bob -> {Alice: -100}
            
            // 1. Update balances for the person who paid (paidByUserId)
            Map<String, Double> balancesOfPaidBy = balanceSheet.get(paidByUserId);
            balancesOfPaidBy.put(paidToUserId, balancesOfPaidBy.getOrDefault(paidToUserId, 0.0) + oweAmount);

            // 2. Update balances for the person who owes (paidToUserId)
            Map<String, Double> balancesOfPaidTo = balanceSheet.get(paidToUserId);
            balancesOfPaidTo.put(paidByUserId, balancesOfPaidTo.getOrDefault(paidByUserId, 0.0) - oweAmount);
        }
        
        System.out.println("✅ Expense of ₹" + amount + " added successfully.");
    }

    private SplitStrategy getStrategy(ExpenseType type) {
        switch (type) {
            case EQUAL: return new EqualSplitStrategy();
            case EXACT: return new ExactSplitStrategy();
            case PERCENT: return new PercentSplitStrategy();
            default: throw new IllegalArgumentException("Unknown Strategy");
        }
    }

    public void showBalance(String userId) {
        boolean isEmpty = true;
        Map<String, Double> balances = balanceSheet.get(userId);
        
        for (Map.Entry<String, Double> userBalance : balances.entrySet()) {
            if (userBalance.getValue() != 0) {
                isEmpty = false;
                printBalance(userId, userBalance.getKey(), userBalance.getValue());
            }
        }

        if (isEmpty) {
            System.out.println(userMap.get(userId).getName() + " is all settled up!");
        }
    }

    public void showAllBalances() {
        boolean isEmpty = true;
        
        for (Map.Entry<String, Map<String, Double>> allBalances : balanceSheet.entrySet()) {
            for (Map.Entry<String, Double> userBalance : allBalances.getValue().entrySet()) {
                if (userBalance.getValue() > 0) { // Only print strictly positive to avoid double printing
                    isEmpty = false;
                    printBalance(allBalances.getKey(), userBalance.getKey(), userBalance.getValue());
                }
            }
        }

        if (isEmpty) {
            System.out.println("No balances to show. Everyone is settled up.");
        }
    }

    private void printBalance(String user1Id, String user2Id, double amount) {
        String user1Name = userMap.get(user1Id).getName();
        String user2Name = userMap.get(user2Id).getName();

        if (amount < 0) {
            System.out.println(user1Name + " owes " + user2Name + ": ₹" + Math.abs(amount));
        } else if (amount > 0) {
            System.out.println(user2Name + " owes " + user1Name + ": ₹" + Math.abs(amount));
        }
    }
}
