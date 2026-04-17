package Splitwise;

import java.util.ArrayList;
import java.util.List;

public class SplitwiseDemo {
    public static void main(String[] args) {
        ExpenseManager expenseManager = new ExpenseManager();
        
        // Setup Users
        User u1 = new User("1", "Alice");
        User u2 = new User("2", "Bob");
        User u3 = new User("3", "Charlie");
        User u4 = new User("4", "David");
        
        expenseManager.addUser(u1);
        expenseManager.addUser(u2);
        expenseManager.addUser(u3);
        expenseManager.addUser(u4);

        System.out.println("\n=== 💸 Splitwise LLD Demo ===\n");
        expenseManager.showAllBalances();
        
        System.out.println("\n--- EXPENSE 1: Equal Split (Alice pays ₹1000 for all 4) ---");
        List<Split> equalSplits = new ArrayList<>();
        equalSplits.add(new EqualSplit(u1));
        equalSplits.add(new EqualSplit(u2));
        equalSplits.add(new EqualSplit(u3));
        equalSplits.add(new EqualSplit(u4));
        expenseManager.addExpense(ExpenseType.EQUAL, 1000, "1", equalSplits);
        expenseManager.showAllBalances();

        System.out.println("\n--- EXPENSE 2: Exact Split (Bob pays ₹1250: Alice 370, Bob 880) ---");
        List<Split> exactSplits = new ArrayList<>();
        exactSplits.add(new ExactSplit(u1, 370));
        exactSplits.add(new ExactSplit(u2, 880));
        expenseManager.addExpense(ExpenseType.EXACT, 1250, "2", exactSplits);
        expenseManager.showAllBalances();

        System.out.println("\n--- EXPENSE 3: Percent Split (Charlie pays ₹2000: Alice 40%, Bob 20%, Charlie 20%, David 20%) ---");
        List<Split> percentSplits = new ArrayList<>();
        percentSplits.add(new PercentSplit(u1, 40));
        percentSplits.add(new PercentSplit(u2, 20));
        percentSplits.add(new PercentSplit(u3, 20));
        percentSplits.add(new PercentSplit(u4, 20));
        expenseManager.addExpense(ExpenseType.PERCENT, 2000, "3", percentSplits);
        expenseManager.showAllBalances();
        
        System.out.println("\n--- EXPENSE 4: Invalid Percent Split (Fails validation) ---");
        List<Split> invalidPercentSplits = new ArrayList<>();
        invalidPercentSplits.add(new PercentSplit(u1, 50));
        invalidPercentSplits.add(new PercentSplit(u2, 30));
        // SUM is 80%, not 100%
        expenseManager.addExpense(ExpenseType.PERCENT, 500, "1", invalidPercentSplits);
        
        System.out.println("\n--- Show Balance for Alice ONLY ---");
        expenseManager.showBalance("1");
    }
}
