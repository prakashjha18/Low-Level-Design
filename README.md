# Low-Level-Design

Core LLD

---

## 1. Core OOP (Non-negotiable)

> If this is shaky, everything else collapses.
> Expect: "Design a system" → you must model entities cleanly.

---

### 1.1 Encapsulation

Encapsulation means **bundling data (fields) and methods that operate on that data into a single unit (class)**, and restricting direct access to the internal state. The outside world interacts only through well-defined methods (getters/setters).

**Why it matters:** Protects object integrity, hides internal representation, and allows you to change implementation without breaking callers.

```java
// ❌ Bad: Fields exposed directly — anyone can put invalid data
class BankAccount {
    public double balance;
}

BankAccount a = new BankAccount();
a.balance = -500; // 💀 No validation, no control

// ✅ Good: Encapsulated — controlled access through methods
class BankAccount {
    private double balance;

    public BankAccount(double initialBalance) {
        if (initialBalance < 0) throw new IllegalArgumentException("Initial balance cannot be negative");
        this.balance = initialBalance;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount > balance) throw new IllegalArgumentException("Insufficient funds");
        balance -= amount;
    }
}
```

**Key takeaway:** Never let the outside world mutate your state directly. Guard every mutation with validation.

---

### 1.2 Abstraction

Abstraction means **exposing only the essential details and hiding the complexity behind a clean interface**. The caller doesn't need to know *how* something works — just *what* it does.

**Why it matters:** Reduces complexity for the consumer, allows implementation swapping, and enforces a contract.

```java
// Abstraction via abstract class
abstract class Shape {
    abstract double area();       // what (contract)
    abstract double perimeter();  // what (contract)

    void printInfo() {            // shared behavior
        System.out.println("Area: " + area() + ", Perimeter: " + perimeter());
    }
}

class Circle extends Shape {
    private double radius;

    Circle(double radius) { this.radius = radius; }

    @Override
    double area() { return Math.PI * radius * radius; }          // how (implementation)

    @Override
    double perimeter() { return 2 * Math.PI * radius; }          // how (implementation)
}

class Rectangle extends Shape {
    private double width, height;

    Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    double area() { return width * height; }

    @Override
    double perimeter() { return 2 * (width + height); }
}

// Caller doesn't care about Circle vs Rectangle internals
Shape s = new Circle(5);
s.printInfo(); // Just works — abstraction in action
```

```java
// Abstraction via interface
interface PaymentGateway {
    boolean charge(double amount);   // contract only — no implementation details
    void refund(String transactionId);
}

class StripeGateway implements PaymentGateway {
    public boolean charge(double amount) { /* Stripe-specific API calls */ return true; }
    public void refund(String txnId) { /* Stripe refund logic */ }
}

class RazorpayGateway implements PaymentGateway {
    public boolean charge(double amount) { /* Razorpay-specific API calls */ return true; }
    public void refund(String txnId) { /* Razorpay refund logic */ }
}
```

**Key takeaway:** Program to an interface, not an implementation. The caller should never depend on the "how".

---

### 1.3 Inheritance

Inheritance allows a class to **acquire properties and behaviors of another class** (parent → child). It models an **"IS-A"** relationship.

**Why it matters:** Enables code reuse, establishes a type hierarchy, and supports polymorphism.

```java
// Base class
class Employee {
    protected String name;
    protected double baseSalary;

    Employee(String name, double baseSalary) {
        this.name = name;
        this.baseSalary = baseSalary;
    }

    double calculatePay() {
        return baseSalary;
    }

    String getDetails() {
        return name + " | Pay: ₹" + calculatePay();
    }
}

// Child class — inherits + extends
class Manager extends Employee {
    private double bonus;

    Manager(String name, double baseSalary, double bonus) {
        super(name, baseSalary);  // call parent constructor
        this.bonus = bonus;
    }

    @Override
    double calculatePay() {
        return baseSalary + bonus;  // overrides parent behavior
    }
}

// Child class — different specialization
class Intern extends Employee {
    Intern(String name, double stipend) {
        super(name, stipend);
    }

    @Override
    double calculatePay() {
        return baseSalary * 0.5;  // interns get half
    }
}

// Usage
Employee e1 = new Manager("Alice", 100000, 20000);
Employee e2 = new Intern("Bob", 30000);
System.out.println(e1.getDetails()); // Alice | Pay: ₹120000.0
System.out.println(e2.getDetails()); // Bob   | Pay: ₹15000.0
```

**⚠️ When NOT to use inheritance:** Don't inherit just for code reuse. If the relationship isn't genuinely "IS-A", use composition instead (see §1.5).

---

### 1.4 Polymorphism

Polymorphism means **one interface, many forms**. The same method call behaves differently depending on the actual object type at runtime.

**Two flavors:**
| Type | Mechanism | Resolved At |
|------|-----------|-------------|
| **Compile-time** (Static) | Method Overloading | Compile time |
| **Runtime** (Dynamic) | Method Overriding | Runtime (via vtable) |

```java
// ── Runtime Polymorphism (most important for LLD) ──

abstract class Notification {
    abstract void send(String message);
}

class EmailNotification extends Notification {
    @Override
    void send(String message) {
        System.out.println("📧 Email: " + message);
    }
}

class SMSNotification extends Notification {
    @Override
    void send(String message) {
        System.out.println("📱 SMS: " + message);
    }
}

class PushNotification extends Notification {
    @Override
    void send(String message) {
        System.out.println("🔔 Push: " + message);
    }
}

// The power: caller doesn't know (or care) about the concrete type
class NotificationService {
    void notifyAll(List<Notification> channels, String msg) {
        for (Notification n : channels) {
            n.send(msg);  // polymorphic dispatch — correct method called at runtime
        }
    }
}
```

```java
// ── Compile-time Polymorphism (Overloading) ──

class MathUtils {
    int add(int a, int b) { return a + b; }
    double add(double a, double b) { return a + b; }
    int add(int a, int b, int c) { return a + b + c; }
    // Same name, different parameter lists → resolved at compile time
}
```

**Key takeaway:** Runtime polymorphism is the backbone of extensible LLD. It lets you add new behavior without modifying existing code (ties directly into OCP from SOLID).

---

### 1.5 Composition vs Inheritance (⚡ Very Commonly Asked)

| Aspect | Inheritance | Composition |
|--------|------------|-------------|
| Relationship | IS-A | HAS-A |
| Coupling | Tight (child locked to parent) | Loose (plug & play) |
| Flexibility | Static (fixed at compile time) | Dynamic (swap at runtime) |
| When to use | True subtype relationship | Behavior reuse / delegation |

```java
// ❌ Bad: Using inheritance for code reuse (not a true IS-A)
class Engine {
    void start() { System.out.println("Engine started"); }
}

class Car extends Engine {  // A Car IS-A Engine? 💀 No.
    void drive() {
        start();  // Inherited, but semantically wrong relationship
    }
}

// ✅ Good: Composition — Car HAS-A Engine
class Engine {
    void start() { System.out.println("Engine started"); }
    void stop()  { System.out.println("Engine stopped"); }
}

class Car {
    private Engine engine;  // HAS-A relationship

    Car(Engine engine) {
        this.engine = engine;  // injected — can swap engine types!
    }

    void drive() {
        engine.start();
        System.out.println("Car is moving");
    }

    void park() {
        engine.stop();
        System.out.println("Car is parked");
    }
}
```

```java
// Real-world example: Strategy via Composition

interface SortingStrategy {
    void sort(int[] data);
}

class QuickSort implements SortingStrategy {
    public void sort(int[] data) { /* quicksort impl */ }
}

class MergeSort implements SortingStrategy {
    public void sort(int[] data) { /* mergesort impl */ }
}

class DataProcessor {
    private SortingStrategy strategy;  // composed, not inherited

    DataProcessor(SortingStrategy strategy) {
        this.strategy = strategy;
    }

    void setStrategy(SortingStrategy strategy) {
        this.strategy = strategy;  // 🔥 swap at runtime!
    }

    void process(int[] data) {
        strategy.sort(data);
        // ... further processing
    }
}

// Usage — runtime flexibility
DataProcessor dp = new DataProcessor(new QuickSort());
dp.process(data);
dp.setStrategy(new MergeSort());  // swapped without touching DataProcessor
dp.process(data);
```

**Rule of thumb:** *"Favor composition over inheritance"* — Gang of Four. Use inheritance only when there is a genuine IS-A relationship AND you need the subtype to be substitutable (LSP).

---

### 1.6 Overloading vs Overriding

| Feature | Overloading | Overriding |
|---------|------------|------------|
| **What** | Same method name, different parameters | Same method signature, different implementation in subclass |
| **Where** | Same class (or inherited) | Parent ↔ Child class |
| **Binding** | Compile-time (static) | Runtime (dynamic) |
| **Return type** | Can differ | Must be same or covariant |
| **Access modifier** | Can differ | Cannot be more restrictive |
| **`@Override`** | Not used | Must use |

```java
// ── Overloading: same name, different parameter lists ──

class Logger {
    void log(String message) {
        System.out.println("[INFO] " + message);
    }

    void log(String message, String level) {
        System.out.println("[" + level + "] " + message);
    }

    void log(String message, String level, Exception e) {
        System.out.println("[" + level + "] " + message + " | Error: " + e.getMessage());
    }
}
```

```java
// ── Overriding: subclass redefines parent method ──

class Animal {
    void makeSound() {
        System.out.println("Some generic sound");
    }
}

class Dog extends Animal {
    @Override
    void makeSound() {
        System.out.println("Bark! 🐕");
    }
}

class Cat extends Animal {
    @Override
    void makeSound() {
        System.out.println("Meow! 🐱");
    }
}

// Polymorphism in action
Animal a = new Dog();
a.makeSound(); // Output: Bark! 🐕  (resolved at runtime, not compile time)
```

**Interview tip:** If asked "Can we override static methods?" → **No.** Static methods are bound to the class, not the instance. What you get is *method hiding*, not overriding.

---

### 1.7 Access Modifiers

Access modifiers control **visibility and accessibility** of classes, methods, and fields.

| Modifier | Same Class | Same Package | Subclass (other pkg) | Everywhere |
|----------|:----------:|:------------:|:-------------------:|:----------:|
| `private` | ✅ | ❌ | ❌ | ❌ |
| *(default)* | ✅ | ✅ | ❌ | ❌ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| `public` | ✅ | ✅ | ✅ | ✅ |

```java
class Account {
    public String accountId;          // accessible everywhere
    protected String accountHolder;   // accessible in subclass + same package
    String branch;                    // default: same package only
    private double balance;           // only within this class

    // Controlled access to private field
    public double getBalance() {
        return balance;
    }

    private void audit() {
        // internal only — cannot be called from outside
        System.out.println("Auditing account: " + accountId);
    }

    public void performTransaction(double amount) {
        audit();  // private method used internally
        this.balance += amount;
    }
}

class SavingsAccount extends Account {
    void showInfo() {
        System.out.println(accountId);      // ✅ public — accessible
        System.out.println(accountHolder);  // ✅ protected — accessible in subclass
        System.out.println(branch);         // ✅ default — accessible if same package
        // System.out.println(balance);     // ❌ private — NOT accessible
        System.out.println(getBalance());   // ✅ via public getter
    }
}
```

**Design guideline:**
- Make fields **`private`** by default (encapsulation).
- Use **`protected`** sparingly — only when subclasses genuinely need direct access.
- Keep your public API surface **minimal** — expose only what's necessary.

---

## SOLID Principles in Java

SOLID is an acronym for five design principles that help create maintainable, scalable, and flexible code:

### 1. **Single Responsibility Principle (SRP)**
A class should have only one reason to change, meaning it should have only one responsibility.

```java
// ❌ Bad: Multiple responsibilities
class User {
    void save() { /* save to database */ }
    void sendEmail() { /* send email */ }
}

// ✅ Good: Each class has one responsibility
class User {
    String name;
    String email;
}

class UserRepository {
    void save(User user) { /* save to database */ }
}

class EmailService {
    void sendEmail(String email) { /* send email */ }
}
```

### 2. **Open/Closed Principle (OCP)**
Software entities should be open for extension but closed for modification.

```java
// ❌ Bad: Need to modify existing code to add new payment types
class PaymentProcessor {
    void process(String type, double amount) {
        if (type.equals("credit")) { /* process credit */ }
        else if (type.equals("debit")) { /* process debit */ }
    }
}

// ✅ Good: Extend without modifying existing code
interface PaymentMethod {
    void pay(double amount);
}

class CreditCardPayment implements PaymentMethod {
    public void pay(double amount) { /* process credit */ }
}

class DebitCardPayment implements PaymentMethod {
    public void pay(double amount) { /* process debit */ }
}

class PaymentProcessor {
    void process(PaymentMethod method, double amount) {
        method.pay(amount);
    }
}
```

### 3. **Liskov Substitution Principle (LSP)**
Objects of a superclass should be replaceable with objects of its subclasses without breaking the application.

```java
// ❌ Bad: Subclass violates parent contract
class Bird {
    void fly() { }
}

class Penguin extends Bird {
    @Override
    void fly() {
        throw new UnsupportedOperationException("Penguins can't fly");
    }
}

// ✅ Good: Use proper inheritance hierarchy
interface Animal { }

interface FlyingBird extends Animal {
    void fly();
}

class Sparrow implements FlyingBird {
    public void fly() { /* flying logic */ }
}

class Penguin implements Animal {
    public void swim() { /* swimming logic */ }
}
```

### 4. **Interface Segregation Principle (ISP)**
Clients should not be forced to depend on interfaces they don't use.

```java
// ❌ Bad: Fat interface forces unnecessary methods
interface Worker {
    void work();
    void eat();
    void sleep();
}

class Robot implements Worker {
    public void eat() { } // Robot doesn't eat!
    public void sleep() { } // Robot doesn't sleep!
}

// ✅ Good: Segregated interfaces
interface Workable {
    void work();
}

interface Eatable {
    void eat();
}

interface Sleepable {
    void sleep();
}

class Robot implements Workable {
    public void work() { /* robot works */ }
}

class Human implements Workable, Eatable, Sleepable {
    public void work() { }
    public void eat() { }
    public void sleep() { }
}
```

### 5. **Dependency Inversion Principle (DIP)**
Depend on abstractions, not on concrete implementations.

```java
// ❌ Bad: High-level module depends on low-level module
class EmailService {
    void sendEmail(String message) { }
}

class UserService {
    EmailService emailService = new EmailService();
    
    void notifyUser(String message) {
        emailService.sendEmail(message);
    }
}

// ✅ Good: Both depend on abstraction
interface NotificationService {
    void notify(String message);
}

class EmailNotification implements NotificationService {
    public void notify(String message) { }
}

class SMSNotification implements NotificationService {
    public void notify(String message) { }
}

class UserService {
    private NotificationService notificationService;
    
    public UserService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    void notifyUser(String message) {
        notificationService.notify(message);
    }
}
```

## Benefits of SOLID

- **Maintainability**: Easier to understand and modify code
- **Scalability**: Simple to add new features
- **Testability**: Loosely coupled code is easier to unit test
- **Reusability**: Well-designed components can be reused
- **Flexibility**: Easier to adapt to changing requirements