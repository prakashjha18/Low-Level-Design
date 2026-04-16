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

---

## 🧩 3. Design Patterns (Most Important Section)

> You don't need all patterns — just these. Master them deeply.

| Category | Pattern | Interview Frequency |
|----------|---------|:-------------------:|
| **Creational** | Singleton | ⭐⭐⭐ |
| **Creational** | Factory / Factory Method | ⭐⭐⭐ |
| **Structural** | Adapter | ⭐⭐ |
| **Structural** | Decorator | ⭐⭐ |
| **Behavioral** | Strategy | ⭐⭐⭐⭐⭐ |
| **Behavioral** | Observer | ⭐⭐⭐⭐ |
| **Behavioral** | State | ⭐⭐⭐ |

**Common interview cues:**
- *"How will you support multiple payment methods?"* → **Strategy**
- *"How will you notify users?"* → **Observer**
- *"How do you ensure only one instance?"* → **Singleton**
- *"How will you create objects without exposing logic?"* → **Factory**

---

### 3.1 Singleton Pattern (Creational)

**Intent:** Ensure a class has **exactly one instance** and provide a global point of access to it.

**When to use:** Database connections, configuration managers, logging, thread pools — any shared resource.

```java
// ❌ Bad: Naive Singleton — NOT thread-safe
class DatabaseConnection {
    private static DatabaseConnection instance;

    private DatabaseConnection() { } // private constructor

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection(); // 💀 Race condition in multi-threaded env
        }
        return instance;
    }
}
```

```java
// ✅ Good: Thread-safe Singleton using Double-Checked Locking
class DatabaseConnection {
    private static volatile DatabaseConnection instance; // volatile prevents instruction reordering

    private String connectionUrl;

    private DatabaseConnection() {
        // expensive initialization
        this.connectionUrl = "jdbc:mysql://localhost:3306/mydb";
        System.out.println("🔌 Database connection created");
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {                    // 1st check (no lock)
            synchronized (DatabaseConnection.class) {
                if (instance == null) {            // 2nd check (with lock)
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public void query(String sql) {
        System.out.println("Executing: " + sql + " on " + connectionUrl);
    }
}

// Usage
DatabaseConnection db1 = DatabaseConnection.getInstance();
DatabaseConnection db2 = DatabaseConnection.getInstance();
System.out.println(db1 == db2); // true — same instance
```

```java
// ✅ Best: Enum Singleton (recommended by Joshua Bloch — Effective Java)
enum AppConfig {
    INSTANCE;

    private String appName = "MyApp";
    private int maxRetries = 3;

    public String getAppName() { return appName; }
    public int getMaxRetries() { return maxRetries; }
}

// Usage
AppConfig.INSTANCE.getAppName(); // "MyApp"
// Handles serialization, reflection attacks, and thread safety automatically
```

**⚠️ Common follow-up questions:**
- *"How do you break Singleton?"* → Reflection, Serialization, Cloning
- *"How to prevent breaking?"* → Enum Singleton handles all three
- *"Is Singleton an anti-pattern?"* → It can be, if overused. It creates hidden global state and makes testing harder.

---

### 3.2 Factory / Factory Method Pattern (Creational)

**Intent:** Create objects **without exposing the instantiation logic** to the client. Let subclasses or a factory decide which class to instantiate.

**When to use:** When the exact type of object isn't known until runtime, or when creation logic is complex.

```java
// ── Simple Factory ──

// Step 1: Define the product interface
interface Notification {
    void send(String message);
}

// Step 2: Concrete products
class EmailNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("📧 Email: " + message);
    }
}

class SMSNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("📱 SMS: " + message);
    }
}

class PushNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("🔔 Push: " + message);
    }
}

// Step 3: Factory — encapsulates creation logic
class NotificationFactory {
    public static Notification create(String type) {
        switch (type.toLowerCase()) {
            case "email": return new EmailNotification();
            case "sms":   return new SMSNotification();
            case "push":  return new PushNotification();
            default: throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}

// Usage — client never uses 'new' directly
Notification n = NotificationFactory.create("email");
n.send("Welcome!"); // 📧 Email: Welcome!
```

```java
// ── Factory Method Pattern (more extensible) ──

// Abstract creator
abstract class NotificationCreator {
    // Factory method — subclasses decide what to create
    abstract Notification createNotification();

    // Template method using the factory method
    void notifyUser(String message) {
        Notification notification = createNotification();
        notification.send(message);
    }
}

// Concrete creators
class EmailNotificationCreator extends NotificationCreator {
    @Override
    Notification createNotification() {
        return new EmailNotification();
    }
}

class SMSNotificationCreator extends NotificationCreator {
    @Override
    Notification createNotification() {
        return new SMSNotification();
    }
}

// Usage
NotificationCreator creator = new EmailNotificationCreator();
creator.notifyUser("Your order is confirmed"); // 📧 Email: Your order is confirmed

// To add a new notification type: create new product + new creator
// No existing code is modified → OCP satisfied ✅
```

**Simple Factory vs Factory Method:**
| Aspect | Simple Factory | Factory Method |
|--------|---------------|----------------|
| Creation logic | Centralized in one class | Distributed across subclasses |
| Extensibility | Must modify factory for new types | Just add new creator subclass |
| OCP compliance | ❌ Violates OCP | ✅ Follows OCP |
| Complexity | Simple | Slightly more complex |
| Use when | Few types, unlikely to grow | Types will grow over time |

---

### 3.3 Adapter Pattern (Structural)

**Intent:** Convert the interface of a class into another interface that clients expect. Lets classes work together that otherwise couldn't due to **incompatible interfaces**.

**When to use:** Integrating third-party libraries, legacy code, or systems with mismatched APIs.

```java
// Scenario: Your system uses a PaymentProcessor interface,
// but a third-party library (Stripe) has a completely different API.

// Your system's expected interface
interface PaymentProcessor {
    void pay(String customerId, double amount);
    void refund(String transactionId);
}

// Third-party class — you CANNOT modify this
class StripeAPI {
    void makeCharge(String stripeCustomerId, int amountInCents) {
        System.out.println("💳 Stripe charging " + amountInCents + " cents to " + stripeCustomerId);
    }

    void issueRefund(String chargeId, int amountInCents) {
        System.out.println("↩️ Stripe refunding " + amountInCents + " cents for charge " + chargeId);
    }
}

// Adapter — bridges the gap
class StripeAdapter implements PaymentProcessor {
    private StripeAPI stripe;

    StripeAdapter(StripeAPI stripe) {
        this.stripe = stripe;
    }

    @Override
    public void pay(String customerId, double amount) {
        // Convert dollars to cents + adapt method name
        int cents = (int) (amount * 100);
        stripe.makeCharge(customerId, cents);
    }

    @Override
    public void refund(String transactionId) {
        stripe.issueRefund(transactionId, 0); // full refund
    }
}

// Usage — rest of your code doesn't know about Stripe's API
PaymentProcessor processor = new StripeAdapter(new StripeAPI());
processor.pay("cust_123", 49.99); // 💳 Stripe charging 4999 cents to cust_123
```

```java
// Another common example: Adapter for legacy logging

// New interface your system uses
interface Logger {
    void log(String level, String message);
}

// Legacy logger you can't change
class OldFileLogger {
    void writeToFile(String text) {
        System.out.println("[FILE] " + text);
    }
}

// Adapter
class FileLoggerAdapter implements Logger {
    private OldFileLogger oldLogger;

    FileLoggerAdapter(OldFileLogger oldLogger) {
        this.oldLogger = oldLogger;
    }

    @Override
    public void log(String level, String message) {
        oldLogger.writeToFile("[" + level + "] " + message);
    }
}
```

**Key insight:** The Adapter wraps an existing class with a new interface. It doesn't add new behavior — it just **translates**.

---

### 3.4 Decorator Pattern (Structural)

**Intent:** Attach **additional responsibilities** to an object dynamically. Provides a flexible alternative to subclassing for extending functionality.

**When to use:** Adding features like logging, encryption, compression, caching — layered on top of existing behavior without modifying the original class.

```java
// Step 1: Component interface
interface Coffee {
    String getDescription();
    double getCost();
}

// Step 2: Concrete component (base)
class SimpleCoffee implements Coffee {
    @Override
    public String getDescription() { return "Simple Coffee"; }

    @Override
    public double getCost() { return 50.0; }
}

// Step 3: Base Decorator (abstract)
abstract class CoffeeDecorator implements Coffee {
    protected Coffee decoratedCoffee;

    CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }

    public String getDescription() { return decoratedCoffee.getDescription(); }
    public double getCost() { return decoratedCoffee.getCost(); }
}

// Step 4: Concrete Decorators
class MilkDecorator extends CoffeeDecorator {
    MilkDecorator(Coffee coffee) { super(coffee); }

    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + " + Milk";
    }

    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 15.0;
    }
}

class WhipCreamDecorator extends CoffeeDecorator {
    WhipCreamDecorator(Coffee coffee) { super(coffee); }

    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + " + Whip Cream";
    }

    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 25.0;
    }
}

class CaramelDecorator extends CoffeeDecorator {
    CaramelDecorator(Coffee coffee) { super(coffee); }

    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + " + Caramel";
    }

    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 30.0;
    }
}

// Usage — stack decorators dynamically!
Coffee coffee = new SimpleCoffee();                        // Simple Coffee ₹50
coffee = new MilkDecorator(coffee);                        // + Milk ₹65
coffee = new WhipCreamDecorator(coffee);                   // + Whip Cream ₹90
coffee = new CaramelDecorator(coffee);                     // + Caramel ₹120

System.out.println(coffee.getDescription()); // Simple Coffee + Milk + Whip Cream + Caramel
System.out.println("₹" + coffee.getCost()); // ₹120.0
```

```java
// Real-world example: I/O Stream Decorators (java.io uses this pattern!)

// BufferedInputStream decorates FileInputStream
InputStream is = new BufferedInputStream(
                     new FileInputStream("data.txt"));

// DataInputStream decorates BufferedInputStream
DataInputStream dis = new DataInputStream(
                          new BufferedInputStream(
                              new FileInputStream("data.txt")));
```

**Decorator vs Inheritance:**
| Aspect | Decorator | Inheritance |
|--------|-----------|-------------|
| When decided | Runtime | Compile time |
| Combinations | Mix & match freely | Class explosion (MilkCoffee, MilkCaramelCoffee, ...) |
| Open/Closed | ✅ Yes | ❌ Must create new subclass for each combo |

---

### 3.5 Strategy Pattern (Behavioral) ⭐ MOST IMPORTANT

**Intent:** Define a family of algorithms, encapsulate each one, and make them **interchangeable**. The algorithm varies independently from the clients that use it.

**When to use:** Multiple ways to do the same thing — pricing, sorting, validation, payment, discount calculation.

> 🗣️ Interview cue: *"How will you support multiple payment methods?"* → **Strategy**

```java
// Step 1: Strategy interface
interface PaymentStrategy {
    void pay(double amount);
    boolean validate();
}

// Step 2: Concrete strategies
class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    private String cvv;

    CreditCardPayment(String cardNumber, String cvv) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
    }

    @Override
    public boolean validate() {
        return cardNumber != null && cardNumber.length() == 16;
    }

    @Override
    public void pay(double amount) {
        if (!validate()) throw new IllegalStateException("Invalid card");
        System.out.println("💳 Paid ₹" + amount + " via Credit Card ending " + cardNumber.substring(12));
    }
}

class UPIPayment implements PaymentStrategy {
    private String upiId;

    UPIPayment(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public boolean validate() {
        return upiId != null && upiId.contains("@");
    }

    @Override
    public void pay(double amount) {
        if (!validate()) throw new IllegalStateException("Invalid UPI ID");
        System.out.println("📲 Paid ₹" + amount + " via UPI: " + upiId);
    }
}

class WalletPayment implements PaymentStrategy {
    private double walletBalance;

    WalletPayment(double walletBalance) {
        this.walletBalance = walletBalance;
    }

    @Override
    public boolean validate() {
        return walletBalance > 0;
    }

    @Override
    public void pay(double amount) {
        if (amount > walletBalance) throw new IllegalStateException("Insufficient wallet balance");
        walletBalance -= amount;
        System.out.println("👛 Paid ₹" + amount + " via Wallet. Remaining: ₹" + walletBalance);
    }
}

// Step 3: Context — uses the strategy
class ShoppingCart {
    private List<Double> items = new ArrayList<>();

    void addItem(double price) {
        items.add(price);
    }

    double getTotal() {
        return items.stream().mapToDouble(Double::doubleValue).sum();
    }

    void checkout(PaymentStrategy paymentStrategy) {  // strategy injected
        double total = getTotal();
        paymentStrategy.pay(total);
    }
}

// Usage — swap payment strategy at runtime
ShoppingCart cart = new ShoppingCart();
cart.addItem(1200);
cart.addItem(800);

// Customer chooses payment at checkout time
cart.checkout(new CreditCardPayment("1234567890123456", "123"));
// 💳 Paid ₹2000.0 via Credit Card ending 3456

cart.checkout(new UPIPayment("user@paytm"));
// 📲 Paid ₹2000.0 via UPI: user@paytm

cart.checkout(new WalletPayment(5000));
// 👛 Paid ₹2000.0 via Wallet. Remaining: ₹3000.0
```

```java
// Another classic: Discount Strategy

interface DiscountStrategy {
    double applyDiscount(double price);
}

class NoDiscount implements DiscountStrategy {
    public double applyDiscount(double price) { return price; }
}

class PercentageDiscount implements DiscountStrategy {
    private double percent;
    PercentageDiscount(double percent) { this.percent = percent; }

    public double applyDiscount(double price) {
        return price - (price * percent / 100);
    }
}

class FlatDiscount implements DiscountStrategy {
    private double flat;
    FlatDiscount(double flat) { this.flat = flat; }

    public double applyDiscount(double price) {
        return Math.max(0, price - flat);
    }
}

// Usage
DiscountStrategy diwaliSale = new PercentageDiscount(20);
DiscountStrategy clearance  = new FlatDiscount(500);

double price = 2000;
System.out.println("Diwali: ₹" + diwaliSale.applyDiscount(price));  // ₹1600.0
System.out.println("Clearance: ₹" + clearance.applyDiscount(price)); // ₹1500.0
```

**Why Strategy is king for LLD:** It embodies OCP + DIP + composition all at once. You can add new algorithms without modifying the context class.

---

### 3.6 Observer Pattern (Behavioral) ⭐ Very Common

**Intent:** Define a **one-to-many dependency** between objects so that when one object (Subject) changes state, all its dependents (Observers) are notified automatically.

**When to use:** Event systems, notifications, pub-sub, reactive UIs, stock price tickers.

> 🗣️ Interview cue: *"How will you notify users?"* → **Observer**

```java
// Step 1: Observer interface
interface Observer {
    void update(String eventType, String data);
}

// Step 2: Subject (Observable)
interface Subject {
    void subscribe(String eventType, Observer observer);
    void unsubscribe(String eventType, Observer observer);
    void notifyObservers(String eventType, String data);
}

// Step 3: Concrete Subject — Event Manager
class EventManager implements Subject {
    private Map<String, List<Observer>> listeners = new HashMap<>();

    @Override
    public void subscribe(String eventType, Observer observer) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(observer);
    }

    @Override
    public void unsubscribe(String eventType, Observer observer) {
        List<Observer> observers = listeners.get(eventType);
        if (observers != null) observers.remove(observer);
    }

    @Override
    public void notifyObservers(String eventType, String data) {
        List<Observer> observers = listeners.getOrDefault(eventType, List.of());
        for (Observer o : observers) {
            o.update(eventType, data);
        }
    }
}

// Step 4: Concrete Observers
class EmailAlertObserver implements Observer {
    private String email;

    EmailAlertObserver(String email) { this.email = email; }

    @Override
    public void update(String eventType, String data) {
        System.out.println("📧 Email to " + email + " → [" + eventType + "] " + data);
    }
}

class SMSAlertObserver implements Observer {
    private String phone;

    SMSAlertObserver(String phone) { this.phone = phone; }

    @Override
    public void update(String eventType, String data) {
        System.out.println("📱 SMS to " + phone + " → [" + eventType + "] " + data);
    }
}

class DashboardObserver implements Observer {
    @Override
    public void update(String eventType, String data) {
        System.out.println("📊 Dashboard updated → [" + eventType + "] " + data);
    }
}

// Step 5: Using it in a real-world scenario — E-commerce Store
class Store {
    private EventManager events;

    Store() {
        this.events = new EventManager();
    }

    public EventManager getEvents() {
        return events;
    }

    void newProductArrival(String product) {
        System.out.println("🏪 New product arrived: " + product);
        events.notifyObservers("NEW_PRODUCT", product);
    }

    void flashSale(String deal) {
        System.out.println("🔥 Flash sale started: " + deal);
        events.notifyObservers("FLASH_SALE", deal);
    }
}

// Usage
Store store = new Store();

Observer emailUser   = new EmailAlertObserver("alice@example.com");
Observer smsUser     = new SMSAlertObserver("+91-9876543210");
Observer dashboard   = new DashboardObserver();

// Subscribe to specific events
store.getEvents().subscribe("NEW_PRODUCT", emailUser);
store.getEvents().subscribe("NEW_PRODUCT", dashboard);
store.getEvents().subscribe("FLASH_SALE", smsUser);
store.getEvents().subscribe("FLASH_SALE", emailUser);

store.newProductArrival("iPhone 16");
// 🏪 New product arrived: iPhone 16
// 📧 Email to alice@example.com → [NEW_PRODUCT] iPhone 16
// 📊 Dashboard updated → [NEW_PRODUCT] iPhone 16

store.flashSale("50% off on Electronics");
// 🔥 Flash sale started: 50% off on Electronics
// 📱 SMS to +91-9876543210 → [FLASH_SALE] 50% off on Electronics
// 📧 Email to alice@example.com → [FLASH_SALE] 50% off on Electronics
```

**Observer vs direct calls:**
| Aspect | Direct Calls | Observer Pattern |
|--------|-------------|-----------------|
| Coupling | Tight — subject knows all listeners | Loose — subject only knows the interface |
| Adding listeners | Modify subject code | Just subscribe — no modification |
| Flexibility | Static | Dynamic (subscribe/unsubscribe at runtime) |

---

### 3.7 State Pattern (Behavioral)

**Intent:** Allow an object to **alter its behavior when its internal state changes**. The object will appear to change its class.

**When to use:** Vending machines, order status workflow, document approval, traffic lights — any entity with defined state transitions.

```java
// Scenario: Order management system with state transitions
// NEW → CONFIRMED → SHIPPED → DELIVERED
//                              ↘ CANCELLED (from any state before DELIVERED)

// Step 1: State interface
interface OrderState {
    void next(Order order);
    void prev(Order order);
    void cancel(Order order);
    String getStatus();
}

// Step 2: Concrete states
class NewOrderState implements OrderState {
    @Override
    public void next(Order order) {
        order.setState(new ConfirmedState());
        System.out.println("✅ Order confirmed");
    }

    @Override
    public void prev(Order order) {
        System.out.println("⚠️ Already at the initial state");
    }

    @Override
    public void cancel(Order order) {
        order.setState(new CancelledState());
        System.out.println("❌ Order cancelled");
    }

    @Override
    public String getStatus() { return "NEW"; }
}

class ConfirmedState implements OrderState {
    @Override
    public void next(Order order) {
        order.setState(new ShippedState());
        System.out.println("📦 Order shipped");
    }

    @Override
    public void prev(Order order) {
        order.setState(new NewOrderState());
        System.out.println("↩️ Order moved back to NEW");
    }

    @Override
    public void cancel(Order order) {
        order.setState(new CancelledState());
        System.out.println("❌ Order cancelled");
    }

    @Override
    public String getStatus() { return "CONFIRMED"; }
}

class ShippedState implements OrderState {
    @Override
    public void next(Order order) {
        order.setState(new DeliveredState());
        System.out.println("🎉 Order delivered!");
    }

    @Override
    public void prev(Order order) {
        order.setState(new ConfirmedState());
        System.out.println("↩️ Order moved back to CONFIRMED");
    }

    @Override
    public void cancel(Order order) {
        order.setState(new CancelledState());
        System.out.println("❌ Order cancelled (was in transit)");
    }

    @Override
    public String getStatus() { return "SHIPPED"; }
}

class DeliveredState implements OrderState {
    @Override
    public void next(Order order) {
        System.out.println("⚠️ Order already delivered — no next state");
    }

    @Override
    public void prev(Order order) {
        System.out.println("⚠️ Cannot undo delivery");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("⚠️ Cannot cancel a delivered order");
    }

    @Override
    public String getStatus() { return "DELIVERED"; }
}

class CancelledState implements OrderState {
    @Override
    public void next(Order order) {
        System.out.println("⚠️ Cannot proceed — order is cancelled");
    }

    @Override
    public void prev(Order order) {
        System.out.println("⚠️ Cannot go back — order is cancelled");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("⚠️ Already cancelled");
    }

    @Override
    public String getStatus() { return "CANCELLED"; }
}

// Step 3: Context — the Order class
class Order {
    private OrderState state;
    private String orderId;

    Order(String orderId) {
        this.orderId = orderId;
        this.state = new NewOrderState(); // initial state
    }

    void setState(OrderState state) {
        this.state = state;
    }

    void nextStep() { state.next(this); }
    void prevStep() { state.prev(this); }
    void cancel()   { state.cancel(this); }

    void printStatus() {
        System.out.println("Order " + orderId + " → Status: " + state.getStatus());
    }
}

// Usage
Order order = new Order("ORD-001");
order.printStatus(); // Order ORD-001 → Status: NEW

order.nextStep();    // ✅ Order confirmed
order.printStatus(); // Order ORD-001 → Status: CONFIRMED

order.nextStep();    // 📦 Order shipped
order.nextStep();    // 🎉 Order delivered!
order.printStatus(); // Order ORD-001 → Status: DELIVERED

order.cancel();      // ⚠️ Cannot cancel a delivered order
order.nextStep();    // ⚠️ Order already delivered — no next state
```

**State vs Strategy:**
| Aspect | State | Strategy |
|--------|-------|----------|
| **Purpose** | Change behavior based on internal state | Choose an algorithm externally |
| **Who decides** | Object transitions itself | Client injects the strategy |
| **Awareness** | States know about each other (transitions) | Strategies are independent |
| **Relationship** | States replace themselves | Strategy is set once (or swapped by client) |
| **Think of it as** | Finite state machine | Pluggable algorithm |

---

### 🗺️ Design Pattern Cheat Sheet

```
┌─────────────────────────────────────────────────────────────────────┐
│                     WHEN TO USE WHAT?                                │
├─────────────────┬───────────────────────────────────────────────────┤
│  SINGLETON      │ "Only one instance needed"                        │
│                 │ → DB connection, config, cache                    │
├─────────────────┼───────────────────────────────────────────────────┤
│  FACTORY        │ "Create objects without specifying exact class"   │
│                 │ → Notification types, document parsers            │
├─────────────────┼───────────────────────────────────────────────────┤
│  ADAPTER        │ "Make incompatible interfaces work together"      │
│                 │ → Third-party integrations, legacy code           │
├─────────────────┼───────────────────────────────────────────────────┤
│  DECORATOR      │ "Add behavior dynamically without subclassing"    │
│                 │ → I/O streams, coffee add-ons, logging layers     │
├─────────────────┼───────────────────────────────────────────────────┤
│  STRATEGY       │ "Multiple ways to do the same thing"              │
│                 │ → Payment methods, sorting, discounts             │
├─────────────────┼───────────────────────────────────────────────────┤
│  OBSERVER       │ "Notify many when one thing changes"              │
│                 │ → Events, notifications, pub-sub                  │
├─────────────────┼───────────────────────────────────────────────────┤
│  STATE          │ "Object behaves differently based on its state"   │
│                 │ → Order workflow, vending machine, traffic light  │
└─────────────────┴───────────────────────────────────────────────────┘
```

---

## 4. Class Design Skills (THIS is the Real Test)

> You should be able to look at **any system** and immediately start thinking:
> *"What are the core objects? What do they own? How do they interact?"*

This section covers the **thinking framework** you need in every LLD interview.

---

### 4.1 Identifying Entities, Attributes, and Methods

**The 3-step mental model:**

```
Step 1: NOUNS → Entities (classes)
Step 2: ADJECTIVES / PROPERTIES → Attributes (fields)
Step 3: VERBS / ACTIONS → Methods (behavior)
```

**Example — Parking Lot System:**

> *"Design a parking lot that supports different vehicle types, has multiple floors, and charges based on duration."*

```
Nouns:     ParkingLot, Floor, Spot, Vehicle, Ticket, Payment
Properties: spotNumber, floorNumber, vehicleType, entryTime, exitTime, amount
Verbs:     parkVehicle, unparkVehicle, calculateFee, findAvailableSpot, processPayment
```

```java
// Translating the mental model to code

enum VehicleType { BIKE, CAR, TRUCK }

class Vehicle {
    private String licensePlate;
    private VehicleType type;

    Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }

    public String getLicensePlate() { return licensePlate; }
    public VehicleType getType() { return type; }
}

class ParkingSpot {
    private int spotNumber;
    private VehicleType supportedType;
    private Vehicle currentVehicle;  // null if empty

    ParkingSpot(int spotNumber, VehicleType supportedType) {
        this.spotNumber = spotNumber;
        this.supportedType = supportedType;
    }

    boolean isAvailable() { return currentVehicle == null; }

    boolean canFit(Vehicle vehicle) {
        return isAvailable() && vehicle.getType() == supportedType;
    }

    void park(Vehicle vehicle) {
        if (!canFit(vehicle)) throw new IllegalStateException("Cannot park here");
        this.currentVehicle = vehicle;
    }

    Vehicle unpark() {
        Vehicle v = this.currentVehicle;
        this.currentVehicle = null;
        return v;
    }
}

class ParkingFloor {
    private int floorNumber;
    private List<ParkingSpot> spots;

    ParkingFloor(int floorNumber, List<ParkingSpot> spots) {
        this.floorNumber = floorNumber;
        this.spots = spots;
    }

    ParkingSpot findAvailableSpot(VehicleType type) {
        return spots.stream()
                    .filter(s -> s.canFit(new Vehicle("", type)))
                    .findFirst()
                    .orElse(null);
    }
}

class Ticket {
    private String ticketId;
    private Vehicle vehicle;
    private ParkingSpot spot;
    private LocalDateTime entryTime;

    Ticket(String ticketId, Vehicle vehicle, ParkingSpot spot) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
    }

    // getters...
}
```

**Key takeaway:** Start from the problem statement. Underline nouns, circle verbs — that's your class diagram skeleton.

---

### 4.2 Relationships Between Classes

This is where most candidates get it wrong. There are **three key relationships** you must understand:

#### Overview Table

| Relationship | Meaning | Strength | Lifecycle | Example |
|-------------|---------|:--------:|-----------|---------|
| **Association** | "uses" or "knows about" | Weak | Independent | Doctor ↔ Patient |
| **Aggregation** | "has-a" (shared) | Medium | Independent | Department → Professor |
| **Composition** | "has-a" (owned) | Strong | Dependent (part dies with whole) | House → Room |

---

#### 4.2.1 Association

**Two objects are related but exist independently.** Neither owns the other. They can exist without each other.

Think: *"X uses Y"* or *"X knows about Y"*

```java
// A Doctor treats many Patients. A Patient can visit many Doctors.
// Neither owns the other — both exist independently.

class Doctor {
    private String name;
    private String specialization;
    private List<Patient> patients;  // association — Doctor knows about Patients

    Doctor(String name, String specialization) {
        this.name = name;
        this.specialization = specialization;
        this.patients = new ArrayList<>();
    }

    void consult(Patient patient) {
        patients.add(patient);
        System.out.println("Dr. " + name + " consulting " + patient.getName());
    }
}

class Patient {
    private String name;
    private List<Doctor> doctors;  // association — Patient knows about Doctors

    Patient(String name) {
        this.name = name;
        this.doctors = new ArrayList<>();
    }

    void bookAppointment(Doctor doctor) {
        doctors.add(doctor);
        doctor.consult(this);
    }

    String getName() { return name; }
}

// Both exist independently
Doctor doc = new Doctor("Smith", "Cardiology");
Patient pat = new Patient("Alice");
pat.bookAppointment(doc);
// If we delete the Doctor object, the Patient still exists (and vice versa)
```

**Keyword clue:** If you can say *"X uses Y"* but neither creates or owns the other → **Association**.

---

#### 4.2.2 Aggregation (Weak "has-a")

**The whole contains parts, but parts can exist independently.** If the container is destroyed, the parts survive.

Think: *"X has Y, but Y can exist without X"*

```java
// A Department has Professors, but Professors can exist without the Department.
// If CS Department is dissolved, Professors still exist — they can join another dept.

class Professor {
    private String name;
    private String subject;

    Professor(String name, String subject) {
        this.name = name;
        this.subject = subject;
    }

    String getName() { return name; }
}

class Department {
    private String name;
    private List<Professor> professors;  // aggregation — has professors, doesn't own them

    Department(String name) {
        this.name = name;
        this.professors = new ArrayList<>();
    }

    // Professors are PASSED IN, not created here
    void addProfessor(Professor professor) {
        professors.add(professor);
        System.out.println(professor.getName() + " joined " + name + " department");
    }

    void removeProfessor(Professor professor) {
        professors.remove(professor);
        // Professor still exists! Can join another department.
    }
}

// Usage
Professor prof = new Professor("Dr. Kumar", "Algorithms");

Department cs = new Department("Computer Science");
cs.addProfessor(prof);  // prof passed in — not created by Department

cs = null; // Department destroyed
// prof is still alive! → Aggregation
```

**How to spot it:** The container receives objects from outside (via constructor/method params). It does NOT create them.

---

#### 4.2.3 Composition (Strong "has-a")

**The whole owns its parts completely.** Parts cannot exist without the whole. When the whole dies, the parts die too.

Think: *"X owns Y. Y has no meaning without X."*

```java
// A House has Rooms. Rooms CANNOT exist without the House.
// If the House is demolished, the Rooms are destroyed too.

class Room {
    private String name;
    private double area;

    Room(String name, double area) {
        this.name = name;
        this.area = area;
    }

    String getName() { return name; }
}

class House {
    private String address;
    private List<Room> rooms;  // composition — House CREATES and OWNS rooms

    House(String address) {
        this.address = address;
        // Rooms are created INSIDE the House — they belong to it
        this.rooms = new ArrayList<>();
        this.rooms.add(new Room("Living Room", 300));  // created here
        this.rooms.add(new Room("Bedroom", 200));      // created here
        this.rooms.add(new Room("Kitchen", 150));       // created here
    }

    void addRoom(String name, double area) {
        rooms.add(new Room(name, area));  // House controls creation
    }

    List<Room> getRooms() {
        return Collections.unmodifiableList(rooms); // don't leak ownership
    }

    // When House is garbage collected, all Rooms go with it
}

// Usage
House house = new House("123 Main St");
// Rooms exist only within the House
house = null; // House destroyed → all Rooms are destroyed too → Composition
```

**How to spot it:** The container creates objects internally (using `new` inside). Parts are never passed from outside.

---

#### Quick Decision Flow

```
Q: Can the part exist without the whole?
│
├─ YES → Q: Is it a simple usage / reference?
│         ├─ YES → ASSOCIATION  (Doctor ↔ Patient)
│         └─ NO  → AGGREGATION (Department → Professor)
│
└─ NO  → COMPOSITION (House → Room)
```

---

#### Comparison in Code

```java
// All three relationships side by side

class Order {
    // COMPOSITION — Order CREATES and OWNS line items
    // LineItems die when Order is deleted
    private List<LineItem> items = new ArrayList<>();

    // AGGREGATION — Order HAS a customer, but doesn't own them
    // Customer exists independently
    private Customer customer;  // passed in from outside

    // ASSOCIATION — Order USES a payment gateway
    // PaymentGateway is completely independent
    private PaymentGateway gateway;

    Order(Customer customer, PaymentGateway gateway) {
        this.customer = customer;   // aggregation — received, not created
        this.gateway = gateway;     // association — used, not owned
    }

    void addItem(String product, int qty, double price) {
        items.add(new LineItem(product, qty, price));  // composition — created inside
    }

    void checkout() {
        double total = items.stream()
                           .mapToDouble(LineItem::getSubtotal)
                           .sum();
        gateway.charge(customer.getId(), total);  // association — just using it
    }
}

class LineItem {
    private String product;
    private int quantity;
    private double price;

    LineItem(String product, int quantity, double price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    double getSubtotal() { return quantity * price; }
}
```

---

### 4.3 Interfaces vs Abstract Classes — When to Use Which

| Aspect | Interface | Abstract Class |
|--------|-----------|----------------|
| **Purpose** | Define a **contract** (what) | Define a **partial implementation** (what + some how) |
| **Multiple** | A class can implement **many** interfaces | A class can extend only **one** abstract class |
| **State** | No instance fields (only constants) | Can have instance fields and constructors |
| **Methods** | All abstract (+ default methods in Java 8+) | Mix of abstract + concrete methods |
| **Relationship** | "CAN-DO" / capability | "IS-A" / identity |
| **Examples** | `Serializable`, `Comparable`, `Runnable` | `InputStream`, `AbstractList`, `HttpServlet` |

```java
// ── When to use INTERFACE: Define a capability ──

// Multiple unrelated classes can fly — it's a CAPABILITY, not identity
interface Flyable {
    void fly();
}

interface Swimmable {
    void swim();
}

class Duck implements Flyable, Swimmable {  // can do both!
    public void fly()  { System.out.println("Duck flying"); }
    public void swim() { System.out.println("Duck swimming"); }
}

class Airplane implements Flyable {  // totally different class, same capability
    public void fly() { System.out.println("Airplane flying"); }
}
```

```java
// ── When to use ABSTRACT CLASS: Shared identity + partial implementation ──

// All notifications ARE notifications (identity) and share some common logic
abstract class Notification {
    protected String recipient;
    protected LocalDateTime timestamp;

    Notification(String recipient) {
        this.recipient = recipient;
        this.timestamp = LocalDateTime.now();  // shared state
    }

    // Shared behavior — all notifications have this
    String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));
    }

    // Template method — common workflow
    void sendNotification(String message) {
        validate();                              // shared step
        String formatted = formatMessage(message); // subclass decides
        deliver(formatted);                       // subclass decides
        log(formatted);                           // shared step
    }

    private void validate() {
        if (recipient == null || recipient.isEmpty())
            throw new IllegalStateException("Recipient is required");
    }

    private void log(String message) {
        System.out.println("[LOG " + getFormattedTimestamp() + "] Sent to " + recipient);
    }

    // Subclasses implement these differently
    abstract String formatMessage(String message);
    abstract void deliver(String formattedMessage);
}

class EmailNotification extends Notification {
    EmailNotification(String email) { super(email); }

    @Override
    String formatMessage(String message) {
        return "<html><body><h1>" + message + "</h1></body></html>";
    }

    @Override
    void deliver(String formattedMessage) {
        System.out.println("📧 Sending email to " + recipient + ": " + formattedMessage);
    }
}

class SMSNotification extends Notification {
    SMSNotification(String phone) { super(phone); }

    @Override
    String formatMessage(String message) {
        return message.substring(0, Math.min(message.length(), 160)); // SMS limit
    }

    @Override
    void deliver(String formattedMessage) {
        System.out.println("📱 Sending SMS to " + recipient + ": " + formattedMessage);
    }
}
```

```java
// ── Combining BOTH: Interface for contract + Abstract class for shared logic ──

// Interface: defines the contract
interface Searchable {
    List<String> search(String query);
}

// Abstract class: partial implementation for database-backed search
abstract class DatabaseSearchable implements Searchable {
    protected Connection connection;

    DatabaseSearchable(Connection connection) {
        this.connection = connection;
    }

    // Shared logic
    protected ResultSet executeQuery(String sql) {
        // common DB query execution
        return null;
    }

    // Subclasses just define the SQL
    abstract String buildSearchQuery(String query);

    @Override
    public List<String> search(String query) {
        String sql = buildSearchQuery(query);
        ResultSet rs = executeQuery(sql);
        return parseResults(rs);
    }

    private List<String> parseResults(ResultSet rs) {
        // common parsing logic
        return new ArrayList<>();
    }
}

class UserSearch extends DatabaseSearchable {
    UserSearch(Connection conn) { super(conn); }

    @Override
    String buildSearchQuery(String query) {
        return "SELECT name FROM users WHERE name LIKE '%" + query + "%'";
    }
}

class ProductSearch extends DatabaseSearchable {
    ProductSearch(Connection conn) { super(conn); }

    @Override
    String buildSearchQuery(String query) {
        return "SELECT title FROM products WHERE title LIKE '%" + query + "%'";
    }
}
```

#### Decision Cheat Sheet

```
Q: Are the classes related by IDENTITY (is-a) or CAPABILITY (can-do)?
│
├─ CAPABILITY → use INTERFACE
│   "Can this thing fly? Can it be serialized? Can it be sorted?"
│
└─ IDENTITY → Q: Do they share implementation / state?
              │
              ├─ YES → ABSTRACT CLASS
              │   "All notifications share timestamp, validation, logging"
              │
              └─ NO  → INTERFACE
                  "They're the same kind of thing, but have nothing in common implementation-wise"
```

---

### 4.4 Full Worked Example — Library Management System

> **Problem:** *"Design a Library Management System where members can borrow and return books."*

#### Step 1: Identify Entities (Nouns)

```
Library, Book, Member, Librarian, BookCopy, Loan, Fine, Catalog
```

#### Step 2: Identify Attributes & Methods

```
Book       → title, author, ISBN, genre           | getDetails()
BookCopy   → copyId, book, status (AVAILABLE/BORROWED/RESERVED)
Member     → memberId, name, email, activeLoans   | borrowBook(), returnBook()
Librarian  → empId, name                          | addBook(), removeBook()
Loan       → loanId, bookCopy, member, issueDate, dueDate, returnDate
Fine       → amount, loan, paid                   | calculate(), pay()
Catalog    → books                                | search(), addBook()
```

#### Step 3: Identify Relationships

```
Library  ◆── Catalog      (Composition — Catalog doesn't exist without Library)
Library  ◆── BookCopy      (Composition — copies belong to this library)
Catalog  ◆── Book          (Composition — Book entries owned by catalog)
Book     ◇── BookCopy      (Aggregation — multiple copies per book)
Member   ── Loan           (Association — member takes a loan)
Loan     ◆── Fine          (Composition — fine belongs to the loan)
BookCopy ── Loan           (Association — copy is lent via a loan)
```

#### Step 4: Code It

```java
// ── Enums ──
enum BookStatus { AVAILABLE, BORROWED, RESERVED, LOST }
enum Genre { FICTION, NON_FICTION, SCIENCE, HISTORY, TECHNOLOGY }

// ── Core Entities ──

class Book {
    private String isbn;
    private String title;
    private String author;
    private Genre genre;

    Book(String isbn, String title, String author, Genre genre) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    // getters...
    String getIsbn()  { return isbn; }
    String getTitle() { return title; }
    String getAuthor(){ return author; }
}

class BookCopy {
    private String copyId;
    private Book book;                // aggregation — refers to a Book
    private BookStatus status;

    BookCopy(String copyId, Book book) {
        this.copyId = copyId;
        this.book = book;
        this.status = BookStatus.AVAILABLE;
    }

    boolean isAvailable() { return status == BookStatus.AVAILABLE; }

    void markBorrowed()  { this.status = BookStatus.BORROWED; }
    void markAvailable() { this.status = BookStatus.AVAILABLE; }
    void markLost()      { this.status = BookStatus.LOST; }

    Book getBook()       { return book; }
    String getCopyId()   { return copyId; }
}

// ── Loan (tracks borrowing) ──

class Loan {
    private String loanId;
    private BookCopy bookCopy;         // association
    private Member member;             // association
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Fine fine;                 // composition — fine belongs to this loan

    Loan(String loanId, BookCopy bookCopy, Member member, int loanDays) {
        this.loanId = loanId;
        this.bookCopy = bookCopy;
        this.member = member;
        this.issueDate = LocalDate.now();
        this.dueDate = issueDate.plusDays(loanDays);
    }

    boolean isOverdue() {
        return returnDate == null && LocalDate.now().isAfter(dueDate);
    }

    void returnBook() {
        this.returnDate = LocalDate.now();
        bookCopy.markAvailable();

        if (returnDate.isAfter(dueDate)) {
            long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate);
            this.fine = new Fine(overdueDays * 5.0); // ₹5 per day — composition
        }
    }

    Fine getFine()          { return fine; }
    BookCopy getBookCopy()  { return bookCopy; }
    String getLoanId()      { return loanId; }
}

class Fine {
    private double amount;
    private boolean paid;

    Fine(double amount) {
        this.amount = amount;
        this.paid = false;
    }

    double getAmount() { return amount; }
    boolean isPaid()   { return paid; }

    void pay() {
        this.paid = true;
        System.out.println("💰 Fine of ₹" + amount + " paid");
    }
}

// ── Users (using abstract class — shared identity) ──

abstract class User {
    protected String id;
    protected String name;
    protected String email;

    User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    String getName() { return name; }
}

// ── Member ──

class Member extends User {
    private static final int MAX_BOOKS = 5;
    private static final int LOAN_PERIOD_DAYS = 14;

    private List<Loan> activeLoans;

    Member(String id, String name, String email) {
        super(id, name, email);
        this.activeLoans = new ArrayList<>();
    }

    Loan borrowBook(BookCopy copy) {
        if (activeLoans.size() >= MAX_BOOKS) {
            throw new IllegalStateException("Max borrow limit reached (" + MAX_BOOKS + ")");
        }
        if (!copy.isAvailable()) {
            throw new IllegalStateException("Book copy not available");
        }

        copy.markBorrowed();
        Loan loan = new Loan("LOAN-" + System.currentTimeMillis(), copy, this, LOAN_PERIOD_DAYS);
        activeLoans.add(loan);
        System.out.println("📖 " + name + " borrowed: " + copy.getBook().getTitle());
        return loan;
    }

    void returnBook(Loan loan) {
        loan.returnBook();
        activeLoans.remove(loan);
        System.out.println("📚 " + name + " returned: " + loan.getBookCopy().getBook().getTitle());

        if (loan.getFine() != null) {
            System.out.println("⚠️ Fine due: ₹" + loan.getFine().getAmount());
        }
    }

    int getActiveLoansCount() { return activeLoans.size(); }
}

// ── Librarian ──

class Librarian extends User {
    Librarian(String id, String name, String email) {
        super(id, name, email);
    }

    void addBook(Catalog catalog, Book book) {
        catalog.addBook(book);
        System.out.println("📚 Librarian " + name + " added: " + book.getTitle());
    }
}

// ── Catalog (search & manage books) ──

class Catalog {
    private Map<String, Book> booksByIsbn;     // composition — catalog owns books
    private List<BookCopy> allCopies;          // composition — library owns copies

    Catalog() {
        this.booksByIsbn = new HashMap<>();
        this.allCopies = new ArrayList<>();
    }

    void addBook(Book book) {
        booksByIsbn.put(book.getIsbn(), book);
    }

    BookCopy addCopy(Book book) {
        BookCopy copy = new BookCopy("COPY-" + (allCopies.size() + 1), book);
        allCopies.add(copy);
        return copy;
    }

    List<Book> searchByTitle(String keyword) {
        return booksByIsbn.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    List<Book> searchByAuthor(String author) {
        return booksByIsbn.values().stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    BookCopy findAvailableCopy(String isbn) {
        return allCopies.stream()
                .filter(c -> c.getBook().getIsbn().equals(isbn) && c.isAvailable())
                .findFirst()
                .orElse(null);
    }
}

// ── Library (top-level — composes everything) ──

class Library {
    private String name;
    private Catalog catalog;            // composition

    Library(String name) {
        this.name = name;
        this.catalog = new Catalog();   // created inside — composition
    }

    Catalog getCatalog() { return catalog; }

    void displayInfo() {
        System.out.println("🏛️ Welcome to " + name);
    }
}
```

#### Step 5: Putting It All Together

```java
// Full usage scenario
Library library = new Library("City Central Library");
library.displayInfo();

Librarian librarian = new Librarian("LIB-001", "Priya", "priya@library.com");

// Add books
Book b1 = new Book("978-0-13-468599-1", "Clean Code", "Robert C. Martin", Genre.TECHNOLOGY);
Book b2 = new Book("978-0-20-163361-0", "Design Patterns", "Gang of Four", Genre.TECHNOLOGY);

librarian.addBook(library.getCatalog(), b1);
librarian.addBook(library.getCatalog(), b2);

// Add copies
BookCopy copy1 = library.getCatalog().addCopy(b1);
BookCopy copy2 = library.getCatalog().addCopy(b1);  // second copy
BookCopy copy3 = library.getCatalog().addCopy(b2);

// Member borrows
Member member = new Member("MEM-001", "Rahul", "rahul@email.com");
Loan loan = member.borrowBook(copy1);
// 📖 Rahul borrowed: Clean Code

// Member returns
member.returnBook(loan);
// 📚 Rahul returned: Clean Code

// Search
List<Book> results = library.getCatalog().searchByAuthor("Martin");
// → [Clean Code]
```

---

### 🧠 Class Design Thought Process Checklist

Use this every time you get a *"Design X"* question:

```
┌─────────────────────────────────────────────────────────────────────┐
│                 CLASS DESIGN CHECKLIST                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  1. IDENTIFY ENTITIES                                               │
│     □ Underline nouns in the problem statement                      │
│     □ Filter out duplicates and non-essential ones                  │
│     □ Each noun = potential class                                   │
│                                                                     │
│  2. DEFINE ATTRIBUTES                                               │
│     □ What data does each entity hold?                              │
│     □ Use proper types (enum for fixed values, etc.)                │
│     □ Make fields private (encapsulation!)                          │
│                                                                     │
│  3. DEFINE METHODS                                                  │
│     □ What can each entity DO?                                      │
│     □ Verbs → methods                                               │
│     □ Keep methods focused (SRP)                                    │
│                                                                     │
│  4. ESTABLISH RELATIONSHIPS                                         │
│     □ Association → uses / knows about                              │
│     □ Aggregation → has-a (shared, independent lifecycle)           │
│     □ Composition → has-a (owned, dependent lifecycle)              │
│                                                                     │
│  5. CHOOSE ABSTRACTIONS                                             │
│     □ Shared identity + implementation → Abstract Class             │
│     □ Shared capability / contract → Interface                      │
│     □ Can combine both                                              │
│                                                                     │
│  6. APPLY PATTERNS (if needed)                                      │
│     □ Multiple algorithms → Strategy                                │
│     □ Notifications → Observer                                      │
│     □ State transitions → State                                     │
│     □ Object creation logic → Factory                               │
│     □ Single instance → Singleton                                   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 6. API & Method Design

> Interviewers care a LOT about naming clarity. A well-named method is self-documenting code.

Your public methods ARE your API. In an LLD interview, the method signatures you write
reveal how you **think** — clean signatures = clean thinking.

---

### 6.1 Clean Method Signatures — The Rules

#### Rule 1: Method name = verb + noun (what it does)

```java
// ❌ Bad: vague, unclear
void process(Object o);
void handle(String s);
void doStuff();
int calc(int a, int b);
void data(User u);

// ✅ Good: reads like English
boolean parkVehicle(Vehicle vehicle);
double calculateFare(Ticket ticket);
void sendNotification(User user, String message);
List<Product> searchByCategory(String category);
Order placeOrder(Cart cart, PaymentMethod payment);
```

**Naming conventions:**

| Action | Verb Pattern | Example |
|--------|-------------|---------|
| Create | `create`, `add`, `register` | `createUser(String name)` |
| Read | `get`, `find`, `fetch`, `search` | `findAvailableSpot(VehicleType type)` |
| Update | `update`, `set`, `modify` | `updateOrderStatus(String orderId, Status status)` |
| Delete | `delete`, `remove`, `cancel` | `cancelReservation(String reservationId)` |
| Check | `is`, `has`, `can`, `exists` | `isAvailable()`, `hasPermission(User user)` |
| Convert | `to`, `from`, `parse` | `toDTO()`, `fromEntity(UserEntity entity)` |

---

#### Rule 2: Return type tells the caller what to expect

```java
// ❌ Bad: void when the caller needs feedback
void parkVehicle(Vehicle vehicle);
// Did it work? Which spot? The caller has no idea.

// ✅ Good: return meaningful result
Ticket parkVehicle(Vehicle vehicle);
// Returns a Ticket on success — caller knows the spot, entry time, etc.

// ✅ Also good: return boolean for simple success/failure
boolean cancelBooking(String bookingId);

// ✅ For search operations: return a List (never null)
List<Product> searchProducts(String keyword);
// Empty list = no results (not null!)

// ✅ For lookups: use Optional to signal "might not exist"
Optional<User> findUserById(String userId);
// Forces the caller to handle the absent case
```

**Return type decision guide:**

| Scenario | Return Type | Why |
|----------|------------|-----|
| Action that produces something | The produced object (`Ticket`, `Order`) | Caller needs the result |
| Simple success/failure | `boolean` | Clean and obvious |
| Lookup (may not exist) | `Optional<T>` | No null surprises |
| Search (zero or more) | `List<T>` | Never return null — return empty list |
| Action with no meaningful result | `void` | Only when caller truly doesn't need feedback |

---

#### Rule 3: Parameters should be specific, not generic

```java
// ❌ Bad: too many primitive params — "Primitive Obsession" smell
void createUser(String name, String email, int age, String phone, String address, boolean isActive);
// What order are they in? Easy to mix up String params.

// ✅ Good: wrap related params in an object
void createUser(UserRegistrationRequest request);

class UserRegistrationRequest {
    private String name;
    private String email;
    private int age;
    private String phone;
    private String address;
    // validation inside constructor
}
```

```java
// ❌ Bad: boolean params — "what does true mean here?"
void sendNotification(String message, boolean urgent, boolean retry);
// sendNotification("Hello", true, false) — unreadable at call site

// ✅ Good: use enums or separate methods
void sendNotification(String message, Priority priority, RetryPolicy retryPolicy);
// sendNotification("Hello", Priority.URGENT, RetryPolicy.NO_RETRY) — crystal clear

// ✅ Or split into separate methods if behavior differs significantly
void sendUrgentNotification(String message);
void sendNotification(String message);
```

**Parameter design rules:**

| Guideline | Why |
|-----------|-----|
| Max 3-4 params per method | More than that → use a request/config object |
| No boolean params | Use enums or separate methods instead |
| Most specific type possible | `VehicleType` not `String`, `Coin` not `int` |
| Required params first, optional later | Natural reading order |
| Avoid `null` params | Use overloaded methods or Optional |

---

### 6.2 Naming — The Art of Self-Documenting Code

#### Class naming

```java
// ❌ Bad class names
class Manager;          // Manager of what?
class Data;             // What data?
class Helper;           // Helps with what?
class Processor;        // Too generic
class Info;             // Useless suffix

// ✅ Good class names
class ParkingLotManager;    // specific
class UserRepository;       // clear responsibility
class PaymentProcessor;     // combined with context, it's clear
class NotificationService;
class TicketGenerator;
```

#### Method naming for different patterns

```java
// ── Factory methods ──
static Notification create(String type);          // ✅ Factory
static User fromDTO(UserDTO dto);                 // ✅ Conversion factory
static ParkingSpot of(int floor, int spotNumber); // ✅ Named constructor

// ── Boolean methods (always start with is/has/can/should) ──
boolean isAvailable();
boolean hasActiveSubscription();
boolean canAccommodate(VehicleType type);
boolean shouldRetry(int attemptCount);

// ── Lifecycle methods ──
void initialize();
void start();
void stop();
void shutdown();
void dispose();  // cleanup resources

// ── Collection methods ──
void add(Item item);          // add single
void addAll(List<Item> items); // add multiple
void remove(Item item);
void clear();
int size();
boolean isEmpty();
boolean contains(Item item);
```

---

### 6.3 Input/Output Clarity

#### Use DTOs / Request-Response objects for complex APIs

```java
// ❌ Bad: raw types, unclear what's returned
Map<String, Object> checkout(String userId, List<String> itemIds, String paymentType, String coupon);
// What's in the Map? Nobody knows without reading the implementation.

// ✅ Good: typed request and response
class CheckoutRequest {
    private final String userId;
    private final List<String> itemIds;
    private final PaymentMethod paymentMethod;
    private final String couponCode;  // nullable — optional

    // constructor with validation...
}

class CheckoutResponse {
    private final String orderId;
    private final double totalAmount;
    private final double discount;
    private final double finalAmount;
    private final PaymentStatus paymentStatus;
    private final LocalDateTime estimatedDelivery;

    // constructor + getters
}

// Clean API
CheckoutResponse checkout(CheckoutRequest request);
// Caller knows EXACTLY what goes in and what comes out
```

#### Use enums instead of magic strings/numbers

```java
// ❌ Bad: magic strings
void setStatus(String status);     // "active"? "ACTIVE"? "Active"? "1"?
void setPriority(int priority);    // 1=high? 0=high? Who knows?

// ✅ Good: enum restricts to valid values
enum OrderStatus { NEW, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }
enum Priority { LOW, MEDIUM, HIGH, CRITICAL }

void setStatus(OrderStatus status);
void setPriority(Priority priority);
// Impossible to pass an invalid value — compile-time safety
```

---

### 6.4 Handling Edge Cases

Every method should consider: *"What could go wrong?"*

```java
// ── A well-designed method handles ALL edge cases ──

public class ParkingLot {

    /**
     * Parks a vehicle in the first available spot.
     *
     * @param vehicle the vehicle to park (must not be null)
     * @return Ticket for the parked vehicle
     * @throws IllegalArgumentException if vehicle is null
     * @throws ParkingFullException if no spots are available for this vehicle type
     * @throws DuplicateVehicleException if vehicle is already parked
     */
    public Ticket parkVehicle(Vehicle vehicle) {
        // 1. Null check
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }

        // 2. Duplicate check
        if (isAlreadyParked(vehicle.getLicensePlate())) {
            throw new DuplicateVehicleException(
                "Vehicle " + vehicle.getLicensePlate() + " is already parked"
            );
        }

        // 3. Availability check
        ParkingSpot spot = findAvailableSpot(vehicle.getType());
        if (spot == null) {
            throw new ParkingFullException(
                "No available spots for " + vehicle.getType()
            );
        }

        // 4. Happy path
        spot.park(vehicle);
        Ticket ticket = new Ticket(generateTicketId(), vehicle, spot);
        activeTickets.put(ticket.getTicketId(), ticket);

        return ticket;
    }

    /**
     * Unparks a vehicle and calculates the fee.
     *
     * @param ticketId the ticket ID from parking
     * @return Payment receipt with fee details
     * @throws IllegalArgumentException if ticketId is null or empty
     * @throws TicketNotFoundException if ticket doesn't exist
     * @throws TicketAlreadyUsedException if vehicle already unparked
     */
    public PaymentReceipt unparkVehicle(String ticketId) {
        // 1. Input validation
        if (ticketId == null || ticketId.isBlank()) {
            throw new IllegalArgumentException("Ticket ID cannot be null or empty");
        }

        // 2. Existence check
        Ticket ticket = activeTickets.get(ticketId);
        if (ticket == null) {
            throw new TicketNotFoundException("Ticket " + ticketId + " not found");
        }

        // 3. State check
        if (ticket.isUsed()) {
            throw new TicketAlreadyUsedException("Ticket " + ticketId + " already used");
        }

        // 4. Happy path
        ParkingSpot spot = ticket.getSpot();
        spot.unpark();
        ticket.markUsed();

        double fee = feeCalculator.calculate(ticket);
        return new PaymentReceipt(ticket, fee);
    }
}
```

#### Edge case checklist

```
┌───────────────────────────────────────────────────────────┐
│              EDGE CASE CHECKLIST                          │
├───────────────────────────────────────────────────────────┤
│                                                           │
│  INPUT VALIDATION                                         │
│  □ Null check — reject null params early                  │
│  □ Empty check — empty strings, empty collections         │
│  □ Range check — negative amounts, zero quantities        │
│  □ Format check — invalid email, malformed ID             │
│                                                           │
│  STATE VALIDATION                                         │
│  □ Duplicate check — entity already exists                │
│  □ Not found — entity doesn't exist                       │
│  □ Wrong state — e.g., cancelling a delivered order       │
│  □ Concurrent modification — two users same resource      │
│                                                           │
│  BUSINESS RULES                                           │
│  □ Capacity — parking full, max loans reached             │
│  □ Permissions — user not authorized                      │
│  □ Limits — max items in cart, max withdraw amount        │
│  □ Timeout — reservation expired                          │
│                                                           │
│  OUTPUT                                                   │
│  □ Never return null for collections (return empty list)  │
│  □ Use Optional for single lookups                        │
│  □ Return meaningful objects, not void                    │
│  □ Custom exceptions with descriptive messages            │
│                                                           │
└───────────────────────────────────────────────────────────┘
```

---

### 6.5 Custom Exceptions — Not Just `RuntimeException`

```java
// ❌ Bad: generic exceptions — caller doesn't know what went wrong
throw new RuntimeException("Error");
throw new Exception("Something failed");

// ✅ Good: custom exceptions organized by domain

// Base exception for the parking domain
class ParkingException extends RuntimeException {
    ParkingException(String message) { super(message); }
}

// Specific exceptions
class ParkingFullException extends ParkingException {
    private final VehicleType vehicleType;

    ParkingFullException(VehicleType type) {
        super("No available spots for " + type);
        this.vehicleType = type;
    }

    public VehicleType getVehicleType() { return vehicleType; }
}

class TicketNotFoundException extends ParkingException {
    TicketNotFoundException(String ticketId) {
        super("Ticket not found: " + ticketId);
    }
}

class DuplicateVehicleException extends ParkingException {
    DuplicateVehicleException(String plate) {
        super("Vehicle already parked: " + plate);
    }
}

// Now the caller can handle specific failures:
try {
    Ticket ticket = parkingLot.parkVehicle(myCar);
} catch (ParkingFullException e) {
    System.out.println("Try another parking lot");
} catch (DuplicateVehicleException e) {
    System.out.println("Your car is already here!");
}
```

---

### 6.6 Full Before/After — Parking Lot API Refactored

#### ❌ BEFORE (poor API design)

```java
class ParkingLot {
    // Vague name, returns int (what does -1 mean?), takes String instead of enum
    int park(String type, String plate) {
        if (type == null) return -1;
        // ... messy if-else
        return spotNumber; // or -1 on failure
    }

    // What does the double[] contain? Who knows.
    double[] unpark(int spotNumber) {
        // returns {hours, fee} ... or null on error
        return new double[]{hours, fee};
    }

    // Boolean but no details on failure
    boolean checkAvailability(String type) {
        // ...
    }
}
```

#### ✅ AFTER (clean API design)

```java
class ParkingLot {
    /**
     * Parks a vehicle and returns a ticket.
     * @throws ParkingFullException if no spots available
     * @throws DuplicateVehicleException if vehicle already parked
     */
    Ticket parkVehicle(Vehicle vehicle) { ... }

    /**
     * Unparks a vehicle and returns a detailed payment receipt.
     * @throws TicketNotFoundException if ticket is invalid
     */
    PaymentReceipt unparkVehicle(String ticketId) { ... }

    /**
     * Returns the number of available spots for a vehicle type.
     */
    int getAvailableSpotCount(VehicleType type) { ... }

    /**
     * Checks if at least one spot is available for the given type.
     */
    boolean hasAvailableSpot(VehicleType type) { ... }

    /**
     * Returns all currently active (unpaid) tickets.
     */
    List<Ticket> getActiveTickets() { ... }
}

// Supporting types — everything is typed, not String/int/double[]
class Ticket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final LocalDateTime entryTime;
}

class PaymentReceipt {
    private final Ticket ticket;
    private final Duration duration;
    private final double fee;
    private final LocalDateTime exitTime;
}
```

**What changed:**
| Aspect | Before | After |
|--------|--------|-------|
| Method names | `park()`, `unpark()` | `parkVehicle()`, `unparkVehicle()` |
| Parameters | `String type, String plate` | `Vehicle vehicle` (typed) |
| Return types | `int`, `double[]`, `null` | `Ticket`, `PaymentReceipt`, `List<>` |
| Error handling | Return `-1` or `null` | Custom exceptions with messages |
| Discoverability | Need to read implementation | Self-documenting signatures |

---

### 🎯 Method Design Golden Rules

```
┌─────────────────────────────────────────────────────────────────────┐
│                 METHOD DESIGN GOLDEN RULES                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  1. NAME IT LIKE ENGLISH                                            │
│     "If you can't name it clearly, you don't understand it."       │
│                                                                     │
│  2. ONE METHOD = ONE JOB                                            │
│     If the method name has "And" → split it.                       │
│     validateAndSave() → validate() + save()                        │
│                                                                     │
│  3. RETURN, DON'T PRINT                                             │
│     Methods should return results, not print them.                  │
│     Let the caller decide what to do with the result.              │
│                                                                     │
│  4. FAIL FAST, FAIL LOUD                                            │
│     Validate inputs at the top. Throw specific exceptions.         │
│     Don't return -1 or null to signal errors.                      │
│                                                                     │
│  5. USE TYPES, NOT PRIMITIVES                                       │
│     VehicleType > String, Coin > int, OrderStatus > String         │
│     The compiler catches bugs that tests miss.                     │
│                                                                     │
│  6. NEVER RETURN NULL FOR COLLECTIONS                               │
│     Return Collections.emptyList(), not null.                      │
│     Use Optional<T> for single-item lookups.                       │
│                                                                     │
│  7. KEEP PARAMS ≤ 3                                                 │
│     More than 3 → wrap in a request object.                        │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## ⚙️ 7. Data Structures in Design

> In an LLD interview, picking the right data structure is as important as picking the right class.
> Expect: *"Why did you choose this DS?"* — you must justify every choice.

---

### 7.1 HashMap — The Workhorse (Used Almost Everywhere)

**What it is:** Key-value store with O(1) average lookup, insert, and delete.

**When to use:** Anytime you need fast lookup by a unique key.

```java
// ── Use Case 1: Entity lookup by ID ──

// Parking Lot → find ticket by ID in O(1)
Map<String, Ticket> activeTickets = new HashMap<>();

activeTickets.put(ticket.getTicketId(), ticket);        // O(1) insert
Ticket t = activeTickets.get("TKT-001");                // O(1) lookup
activeTickets.remove("TKT-001");                        // O(1) delete
```

```java
// ── Use Case 2: Counting / frequency tracking ──

// How many vehicles of each type are parked?
Map<VehicleType, Integer> vehicleCount = new HashMap<>();

vehicleCount.merge(VehicleType.CAR, 1, Integer::sum);   // increment
int carCount = vehicleCount.getOrDefault(VehicleType.CAR, 0);
```

```java
// ── Use Case 3: Grouping / categorization ──

// Observer pattern → group observers by event type
Map<String, List<Observer>> listeners = new HashMap<>();

listeners.computeIfAbsent("ORDER_PLACED", k -> new ArrayList<>()).add(emailObserver);
listeners.computeIfAbsent("ORDER_PLACED", k -> new ArrayList<>()).add(smsObserver);

// Notify all observers for an event
listeners.getOrDefault("ORDER_PLACED", List.of())
         .forEach(o -> o.update("ORDER_PLACED", data));
```

```java
// ── Use Case 4: Cache / memoization ──

// Cache expensive computations
Map<String, Double> fareCache = new HashMap<>();

double getFare(String routeKey) {
    return fareCache.computeIfAbsent(routeKey, key -> {
        // expensive calculation only happens once per route
        return calculateFare(key);
    });
}
```

**HashMap variants — when to use which:**

| Variant | When to Use | Example |
|---------|------------|---------|
| `HashMap` | Default choice — fast, unordered | Product lookup by ID |
| `LinkedHashMap` | Need **insertion order** preserved | LRU Cache, audit trail |
| `TreeMap` | Need **sorted keys** | Leaderboard (sorted scores), scheduling |
| `ConcurrentHashMap` | **Thread-safe** access | Shared resource in multi-threaded system |
| `EnumMap` | Keys are an **enum** type | VehicleType → List\<ParkingSpot\> |

```java
// LinkedHashMap as LRU Cache (classic interview question)
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    LRUCache(int capacity) {
        super(capacity, 0.75f, true); // true = access order (not insertion order)
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity; // auto-evict when full
    }
}

LRUCache<String, User> cache = new LRUCache<>(100);
cache.put("user-1", user);     // add to cache
User u = cache.get("user-1");  // moves to "most recently used"
// When cache exceeds 100, least recently used entry is auto-removed
```

---

### 7.2 List vs Set — When Order & Duplicates Matter

| Feature | List | Set |
|---------|------|-----|
| **Duplicates** | ✅ Allowed | ❌ No duplicates |
| **Order** | ✅ Maintained (insertion order) | ❌ No order (`HashSet`) / ✅ Sorted (`TreeSet`) |
| **Lookup** | O(n) — `contains()` scans | O(1) — `HashSet.contains()` |
| **Use when** | Order matters, duplicates OK | Uniqueness required, fast membership checks |

```java
// ── When to use LIST ──

// Order matters: queue of customers, order history, chat messages
List<Order> orderHistory = new ArrayList<>();  // chronological order matters
List<Message> chatMessages = new LinkedList<>();

// Duplicates OK: items in cart (2x same product)
List<CartItem> cartItems = new ArrayList<>();
cartItems.add(new CartItem("Coke", 2));
cartItems.add(new CartItem("Coke", 1));  // duplicate product is fine
```

```java
// ── When to use SET ──

// Uniqueness required: registered users, unique tags
Set<String> registeredEmails = new HashSet<>();
registeredEmails.add("alice@email.com");
registeredEmails.add("alice@email.com");  // ignored — duplicate
System.out.println(registeredEmails.size()); // 1

// Fast membership check: is this user blocked?
Set<String> blockedUsers = new HashSet<>();
if (blockedUsers.contains(userId)) {  // O(1) check
    throw new UnauthorizedException("User is blocked");
}
```

```java
// ── TreeSet: when you need sorted unique elements ──

// Available time slots (sorted)
TreeSet<LocalTime> availableSlots = new TreeSet<>();
availableSlots.add(LocalTime.of(9, 0));
availableSlots.add(LocalTime.of(10, 30));
availableSlots.add(LocalTime.of(14, 0));

// Find the next available slot after a given time
LocalTime nextSlot = availableSlots.ceiling(LocalTime.of(10, 0)); // 10:30
```

**Decision guide:**

```
Q: Do I need duplicates?
│
├─ YES → LIST
│         Q: Frequent random access by index?
│         ├─ YES → ArrayList
│         └─ NO (mostly add/remove from ends) → LinkedList
│
└─ NO (unique elements only) → SET
          Q: Need sorted order?
          ├─ YES → TreeSet (O(log n) operations)
          └─ NO  → HashSet (O(1) operations)
```

---

### 7.3 PriorityQueue — For Scheduling & Priority Problems

**What it is:** A heap-based queue where elements are dequeued by **priority**, not insertion order.

**Time complexity:** O(log n) insert, O(1) peek, O(log n) poll.

**When to use:** Task scheduling, meeting room allocation, hospital triage, Dijkstra's algorithm.

```java
// ── Use Case 1: Task scheduler (highest priority first) ──

class Task {
    String name;
    int priority; // lower number = higher priority

    Task(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }
}

// Min-heap: lowest priority number comes out first
PriorityQueue<Task> taskQueue = new PriorityQueue<>(
    Comparator.comparingInt(t -> t.priority)
);

taskQueue.offer(new Task("Send report", 3));
taskQueue.offer(new Task("Fix critical bug", 1));    // highest priority
taskQueue.offer(new Task("Code review", 2));

taskQueue.poll(); // Fix critical bug (priority 1)
taskQueue.poll(); // Code review (priority 2)
taskQueue.poll(); // Send report (priority 3)
```

```java
// ── Use Case 2: Meeting room allocation (earliest end time first) ──

class Meeting {
    String title;
    LocalTime start;
    LocalTime end;

    Meeting(String title, LocalTime start, LocalTime end) {
        this.title = title;
        this.start = start;
        this.end = end;
    }
}

// Min-heap by end time → room that frees up earliest is at the top
PriorityQueue<Meeting> occupiedRooms = new PriorityQueue<>(
    Comparator.comparing(m -> m.end)
);

// Schedule meetings
Meeting m1 = new Meeting("Standup", LocalTime.of(9, 0), LocalTime.of(9, 30));
Meeting m2 = new Meeting("Sprint Planning", LocalTime.of(9, 0), LocalTime.of(10, 0));

occupiedRooms.offer(m1);
occupiedRooms.offer(m2);

// Check if earliest room is free for a new meeting starting at 9:30
Meeting earliest = occupiedRooms.peek(); // Standup (ends at 9:30)
if (earliest.end.compareTo(newMeeting.start) <= 0) {
    occupiedRooms.poll();  // room is free, reuse it
}
```

```java
// ── Use Case 3: Hospital triage / emergency queue ──

enum Severity { CRITICAL, SERIOUS, MODERATE, MINOR }

class Patient {
    String name;
    Severity severity;
    LocalTime arrivalTime;
}

// Critical patients first, then by arrival time (FIFO within same severity)
PriorityQueue<Patient> triageQueue = new PriorityQueue<>(
    Comparator.comparing((Patient p) -> p.severity)
              .thenComparing(p -> p.arrivalTime)
);
```

---

### 7.4 Queue — For Real-World Flows (FIFO)

**What it is:** First-In-First-Out data structure. Process in the order things arrive.

**When to use:** Order processing, message queues, BFS traversal, request handling.

```java
// ── Use Case 1: Order processing pipeline ──

Queue<Order> orderQueue = new LinkedList<>();

// Orders arrive
orderQueue.offer(new Order("ORD-001", "Pizza"));
orderQueue.offer(new Order("ORD-002", "Burger"));
orderQueue.offer(new Order("ORD-003", "Pasta"));

// Kitchen processes in FIFO order
while (!orderQueue.isEmpty()) {
    Order order = orderQueue.poll();  // removes from front
    kitchen.prepare(order);
}
```

```java
// ── Use Case 2: Ride sharing — driver request queue ──

Queue<RideRequest> requestQueue = new LinkedList<>();

// Passengers request rides (first-come-first-served)
requestQueue.offer(new RideRequest("Alice", Location.DOWNTOWN));
requestQueue.offer(new RideRequest("Bob", Location.AIRPORT));

// When a driver becomes available
if (!requestQueue.isEmpty()) {
    RideRequest next = requestQueue.poll();
    assignDriver(next, driver);
}
```

```java
// ── Use Case 3: Deque (Double-Ended Queue) — for undo/redo ──

Deque<Action> undoStack = new ArrayDeque<>();  // use as stack
Deque<Action> redoStack = new ArrayDeque<>();

// User performs an action
void performAction(Action action) {
    action.execute();
    undoStack.push(action);   // push to undo stack
    redoStack.clear();        // clear redo after new action
}

// Undo
void undo() {
    if (!undoStack.isEmpty()) {
        Action action = undoStack.pop();
        action.reverse();
        redoStack.push(action);
    }
}

// Redo
void redo() {
    if (!redoStack.isEmpty()) {
        Action action = redoStack.pop();
        action.execute();
        undoStack.push(action);
    }
}
```

**Queue variants:**

| Variant | When to Use | Example |
|---------|------------|---------|
| `LinkedList` (as Queue) | Basic FIFO queue | Order processing, BFS |
| `ArrayDeque` | Stack or double-ended queue (faster than Stack) | Undo/redo, expression evaluation |
| `PriorityQueue` | Priority-based dequeue | Scheduling, triage |
| `BlockingQueue` | Thread-safe producer-consumer | Message queue, task pool |

---

### 7.5 Bonus — Other DS Choices in LLD

#### Stack (use `ArrayDeque`)

```java
// Undo/redo, expression parsing, back/forward navigation
Deque<String> browserHistory = new ArrayDeque<>();
browserHistory.push("google.com");
browserHistory.push("github.com");
browserHistory.pop();  // back to google.com
```

#### LinkedList

```java
// When you need frequent insertions/deletions in the middle
// Example: playlist where songs are constantly added/removed/reordered
LinkedList<Song> playlist = new LinkedList<>();
playlist.add(2, newSong);   // insert at position — O(1) after traversal
playlist.remove(1);          // remove from middle
```

#### EnumMap / EnumSet

```java
// When keys are always enum values — faster and more memory-efficient than HashMap
enum VehicleType { BIKE, CAR, TRUCK }

// Spots organized by vehicle type
EnumMap<VehicleType, List<ParkingSpot>> spotsByType = new EnumMap<>(VehicleType.class);
spotsByType.put(VehicleType.BIKE, bikeSpots);
spotsByType.put(VehicleType.CAR, carSpots);

// Allowed vehicle types for a floor
EnumSet<VehicleType> allowedTypes = EnumSet.of(VehicleType.BIKE, VehicleType.CAR);
if (allowedTypes.contains(vehicle.getType())) { /* allowed */ }
```

---

### 🗺️ Data Structure Decision Cheat Sheet

```
┌─────────────────────────────────────────────────────────────────────────┐
│                  DATA STRUCTURE DECISION GUIDE                          │
├──────────────────┬──────────────────────────────────────────────────────┤
│                  │                                                      │
│  "I need fast    │  HashMap<K,V>                                        │
│   lookup by key" │  → O(1) get/put/remove                              │
│                  │  → Ticket by ID, User by email, Product by code     │
│                  │                                                      │
├──────────────────┼──────────────────────────────────────────────────────┤
│                  │                                                      │
│  "I need an      │  ArrayList<T>                                        │
│   ordered list"  │  → O(1) random access, O(n) insert/delete           │
│                  │  → Order history, cart items, chat messages          │
│                  │                                                      │
├──────────────────┼──────────────────────────────────────────────────────┤
│                  │                                                      │
│  "I need unique  │  HashSet<T>                                          │
│   elements"      │  → O(1) contains/add/remove                         │
│                  │  → Blocked users, unique tags, registered emails    │
│                  │                                                      │
├──────────────────┼──────────────────────────────────────────────────────┤
│                  │                                                      │
│  "I need sorted  │  TreeMap<K,V> or TreeSet<T>                          │
│   elements"      │  → O(log n) operations                              │
│                  │  → Leaderboard, time slots, price ranges            │
│                  │                                                      │
├──────────────────┼──────────────────────────────────────────────────────┤
│                  │                                                      │
│  "I need FIFO    │  Queue (LinkedList or ArrayDeque)                    │
│   processing"    │  → O(1) offer/poll                                  │
│                  │  → Order queue, request pipeline, BFS               │
│                  │                                                      │
├──────────────────┼──────────────────────────────────────────────────────┤
│                  │                                                      │
│  "I need LIFO /  │  Deque (ArrayDeque) — NOT Stack class               │
│   stack behavior"│  → O(1) push/pop                                    │
│                  │  → Undo/redo, browser back, expression eval         │
│                  │                                                      │
├──────────────────┼──────────────────────────────────────────────────────┤
│                  │                                                      │
│  "I need priority│  PriorityQueue<T>                                    │
│   ordering"      │  → O(log n) offer/poll, O(1) peek                  │
│                  │  → Task scheduler, meeting rooms, triage            │
│                  │                                                      │
├──────────────────┼──────────────────────────────────────────────────────┤
│                  │                                                      │
│  "I need order-  │  LinkedHashMap<K,V>                                  │
│   preserving map"│  → Insertion-order or access-order                  │
│                  │  → LRU Cache, audit log                             │
│                  │                                                      │
├──────────────────┼──────────────────────────────────────────────────────┤
│                  │                                                      │
│  "Keys are       │  EnumMap / EnumSet                                   │
│   enums"         │  → Faster than HashMap/HashSet for enum keys        │
│                  │  → Spots by VehicleType, permissions by Role        │
│                  │                                                      │
└──────────────────┴──────────────────────────────────────────────────────┘
```

### 📌 Common LLD Scenarios → DS Mapping

| LLD Problem | Data Structure | Why |
|-------------|---------------|-----|
| Parking Lot — find ticket | `HashMap<String, Ticket>` | O(1) lookup by ticket ID |
| Parking Lot — spots by type | `EnumMap<VehicleType, List<Spot>>` | Enum keys, grouped access |
| Vending Machine — inventory | `HashMap<String, Product>` + `HashMap<String, Integer>` | Code → Product & stock count |
| Library — active loans | `List<Loan>` | Ordered, duplicates possible (borrow same book twice after return) |
| Library — unique members | `HashSet<String>` or `HashMap<String, Member>` | No duplicate member IDs |
| Observer — listeners by event | `HashMap<String, List<Observer>>` | Group observers per event type |
| Task Scheduler | `PriorityQueue<Task>` | Always process highest priority first |
| Order Processing | `Queue<Order>` | FIFO — first order, first served |
| Undo/Redo | `Deque<Action>` (×2) | Two stacks — undo stack & redo stack |
| LRU Cache | `LinkedHashMap` | Access-order + auto-eviction |
| Leaderboard | `TreeMap<Integer, List<Player>>` | Sorted by score, O(log n) updates |
| Unique tags/categories | `HashSet<String>` | Fast contains check, no duplicates |

---

## ⏱️ 8. Scalability Thinking (Light, Not HLD Level)

> In LLD interviews, you don't need to design distributed systems — but you MUST show awareness
> of what happens when your design grows. Think: *"What breaks if this scales?"*

---

### 8.1 Designing for Extensibility — "What If We Add X Later?"

The #1 signal interviewers look for: **Can your design accommodate new requirements without rewriting?**

#### The Open/Closed Mindset

```java
// ❌ Rigid design — adding a new vehicle type requires modifying existing code

class ParkingLot {
    double calculateFee(String vehicleType, int hours) {
        if (vehicleType.equals("BIKE"))       return hours * 10;
        else if (vehicleType.equals("CAR"))   return hours * 20;
        else if (vehicleType.equals("TRUCK")) return hours * 40;
        // 💀 Adding BUS means modifying this method
        return 0;
    }
}
```

```java
// ✅ Extensible design — new vehicle type = new class, zero changes to existing code

interface FeeStrategy {
    double calculate(int hours);
}

class BikeFee implements FeeStrategy {
    public double calculate(int hours) { return hours * 10; }
}

class CarFee implements FeeStrategy {
    public double calculate(int hours) { return hours * 20; }
}

// Adding BUS → Just add a new class. Nothing else changes.
class BusFee implements FeeStrategy {
    public double calculate(int hours) { return hours * 50; }
}

class ParkingLot {
    private Map<VehicleType, FeeStrategy> feeStrategies;

    double calculateFee(VehicleType type, int hours) {
        return feeStrategies.get(type).calculate(hours);
    }
}
```

#### Extension points checklist

```
When designing, ask yourself:

□ What if we add a new TYPE?      → Use Strategy / Factory
□ What if we add a new STATE?     → Use State Pattern
□ What if we add a new LISTENER?  → Use Observer Pattern
□ What if we add a new RULE?      → Use Strategy / Chain of Responsibility
□ What if the DATA SOURCE changes?→ Use Repository pattern / Dependency Injection
```

#### Real examples of extensibility questions

| Interviewer asks... | Your design should... |
|---------------------|----------------------|
| *"What if we add UPI payment?"* | New `UPIPayment implements PaymentStrategy` — zero changes elsewhere |
| *"What if we add a VIP parking floor?"* | New `VIPFloor extends ParkingFloor` or new `VIPFeeStrategy` |
| *"What if we need SMS + Push notifications?"* | New observers — just subscribe them to the event manager |
| *"What if we need to support electric vehicles?"* | New `VehicleType.ELECTRIC` + new `EVParkingSpot` — existing code untouched |
| *"What if pricing rules change seasonally?"* | Swap the `FeeStrategy` at runtime — Strategy pattern handles this |

---

### 8.2 Handling Large Data — Basic Awareness

You won't design sharded databases in LLD, but you should know the **impact** of data size on your design.

#### Use the right data structure for scale

```java
// ❌ Problem: Linear search doesn't scale

class ParkingLot {
    private List<ParkingSpot> allSpots; // 10,000 spots

    ParkingSpot findAvailableSpot(VehicleType type) {
        // O(n) scan every time — gets slow with 10K+ spots
        for (ParkingSpot spot : allSpots) {
            if (spot.isAvailable() && spot.getType() == type) {
                return spot;
            }
        }
        return null;
    }
}
```

```java
// ✅ Better: Pre-index available spots for O(1) access

class ParkingLot {
    // Available spots organized by type — O(1) lookup
    private Map<VehicleType, Queue<ParkingSpot>> availableSpots;

    ParkingSpot findAvailableSpot(VehicleType type) {
        Queue<ParkingSpot> queue = availableSpots.get(type);
        return (queue != null && !queue.isEmpty()) ? queue.poll() : null;  // O(1)
    }

    void freeSpot(ParkingSpot spot) {
        availableSpots.get(spot.getType()).offer(spot);  // O(1) return to pool
    }
}
```

#### Pagination for large result sets

```java
// ❌ Bad: return ALL orders (could be millions)
List<Order> getAllOrders(String userId) {
    return orderRepository.findByUserId(userId); // 💀 OOM for active users
}

// ✅ Good: paginate
List<Order> getOrders(String userId, int page, int pageSize) {
    int offset = page * pageSize;
    return orderRepository.findByUserId(userId, offset, pageSize);
}

// Even better: cursor-based pagination
List<Order> getOrders(String userId, String lastOrderId, int limit) {
    return orderRepository.findAfter(userId, lastOrderId, limit);
}
```

#### Lazy loading vs eager loading

```java
// ❌ Eager: loads everything upfront
class BookCatalog {
    private List<Book> allBooks;  // loads 100K books into memory on startup 💀

    BookCatalog() {
        this.allBooks = database.loadAllBooks(); // slow startup, huge memory
    }
}

// ✅ Lazy: load on demand
class BookCatalog {
    private Map<String, Book> cache = new HashMap<>();

    Book getBook(String isbn) {
        return cache.computeIfAbsent(isbn, id -> database.findByIsbn(id));
        // Only fetched when actually needed, then cached
    }
}
```

---

### 8.3 Thread Safety — Basic Awareness

> *"What if multiple users access this simultaneously?"*

This is the most common scalability follow-up in LLD interviews. You don't need to write lock-free algorithms — just show **awareness** of the problem and basic solutions.

#### The Problem: Race Conditions

```java
// ❌ NOT thread-safe: two users could get the SAME parking spot

class ParkingLot {
    private List<ParkingSpot> availableSpots;

    ParkingSpot findAndPark(Vehicle vehicle) {
        ParkingSpot spot = availableSpots.get(0);   // Thread A reads spot #1
                                                     // Thread B also reads spot #1
        spot.park(vehicle);                          // Thread A parks
                                                     // Thread B also parks → 💀 COLLISION!
        availableSpots.remove(spot);
        return spot;
    }
}
```

#### Solution 1: `synchronized` (simplest)

```java
// ✅ Thread-safe with synchronized — one thread at a time

class ParkingLot {
    private final List<ParkingSpot> availableSpots;

    // Only one thread can execute this method at a time
    synchronized ParkingSpot findAndPark(Vehicle vehicle) {
        if (availableSpots.isEmpty()) {
            throw new ParkingFullException("No spots available");
        }
        ParkingSpot spot = availableSpots.remove(0); // atomic: find + remove
        spot.park(vehicle);
        return spot;
    }

    synchronized void freeSpot(ParkingSpot spot) {
        spot.unpark();
        availableSpots.add(spot);
    }
}
```

**Trade-off:** Simple, but only one thread can park/unpark at a time → potential bottleneck.

#### Solution 2: `ReentrantLock` (more control)

```java
// ✅ Fine-grained locking — lock per vehicle type, not the entire lot

class ParkingLot {
    private final Map<VehicleType, Queue<ParkingSpot>> availableSpots;
    private final Map<VehicleType, ReentrantLock> locks;

    ParkingLot() {
        availableSpots = new EnumMap<>(VehicleType.class);
        locks = new EnumMap<>(VehicleType.class);

        for (VehicleType type : VehicleType.values()) {
            availableSpots.put(type, new LinkedList<>());
            locks.put(type, new ReentrantLock());
        }
    }

    ParkingSpot findAndPark(Vehicle vehicle) {
        VehicleType type = vehicle.getType();
        ReentrantLock lock = locks.get(type);

        lock.lock(); // lock only for THIS vehicle type
        try {
            Queue<ParkingSpot> spots = availableSpots.get(type);
            if (spots.isEmpty()) {
                throw new ParkingFullException(type);
            }
            ParkingSpot spot = spots.poll();
            spot.park(vehicle);
            return spot;
        } finally {
            lock.unlock(); // ALWAYS unlock in finally
        }
    }
}
// Now: Car parking and Bike parking can happen simultaneously!
```

#### Solution 3: Concurrent Collections

```java
// ✅ Use thread-safe collections from java.util.concurrent

// Instead of HashMap → ConcurrentHashMap
Map<String, Ticket> activeTickets = new ConcurrentHashMap<>();

// Instead of ArrayList → CopyOnWriteArrayList (for read-heavy scenarios)
List<Observer> observers = new CopyOnWriteArrayList<>();

// Instead of LinkedList as Queue → ConcurrentLinkedQueue
Queue<ParkingSpot> availableSpots = new ConcurrentLinkedQueue<>();

// Instead of LinkedList as Queue (blocking) → LinkedBlockingQueue
BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();
// .take() blocks until an item is available — perfect for producer-consumer
```

#### Solution 4: Atomic Operations

```java
// ✅ For simple counters/flags — no locks needed

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

class ParkingFloor {
    private AtomicInteger availableCount = new AtomicInteger(100);

    boolean tryPark() {
        int current = availableCount.get();
        if (current <= 0) return false;
        return availableCount.compareAndSet(current, current - 1);
        // Atomic: only succeeds if no other thread changed the value
    }

    void freeSpot() {
        availableCount.incrementAndGet(); // atomic increment
    }
}
```

#### Thread-safety decision guide

```
Q: What kind of shared resource?
│
├─ Simple counter / flag
│  → AtomicInteger, AtomicBoolean
│     (lock-free, highest performance)
│
├─ Key-value lookup (read-heavy)
│  → ConcurrentHashMap
│     (segment-level locking, concurrent reads)
│
├─ Ordered list (read-heavy, rare writes)
│  → CopyOnWriteArrayList
│     (copy on write, fast reads)
│
├─ Queue (producer-consumer)
│  → ConcurrentLinkedQueue (non-blocking)
│  → LinkedBlockingQueue (blocking — waits for items)
│
├─ Complex multi-step operation
│  → synchronized method or block
│     (simple, coarse-grained)
│
└─ Need per-resource locking
   → ReentrantLock (fine-grained control)
   → ReadWriteLock (many readers, few writers)
```

---

### 8.4 Common Interview Scenarios & Responses

#### Scenario 1: *"What if multiple users try to book the same slot?"*

```
Answer framework:

1. IDENTIFY the shared resource → the slot/spot/seat
2. EXPLAIN the race condition → two users read "available", both try to book
3. PROPOSE a solution:
   - Option A: synchronized block around check-and-book
   - Option B: Optimistic locking (check version, retry on conflict)
   - Option C: Database-level locking (SELECT FOR UPDATE)
4. DISCUSS trade-offs:
   - synchronized → simple but bottleneck under high load
   - Optimistic locking → better performance, but requires retry logic
```

```java
// Concrete answer:

class BookingService {
    private final ReentrantLock lock = new ReentrantLock();

    Booking bookSlot(User user, Slot slot) {
        lock.lock();
        try {
            if (!slot.isAvailable()) {
                throw new SlotUnavailableException("Slot already booked");
            }
            slot.markBooked();
            return new Booking(user, slot);
        } finally {
            lock.unlock();
        }
    }
}
```

#### Scenario 2: *"What if the inventory runs out mid-transaction?"*

```
Answer:
- Check availability BEFORE deducting money
- Use synchronized/lock to make check-and-deduct atomic
- If out of stock after money inserted → refund immediately
- This is exactly what the Vending Machine State Pattern handles:
  HasMoneyState validates stock before transitioning to DispensingState
```

#### Scenario 3: *"How would you handle 10 million products?"*

```
Answer (LLD level — not HLD):
- Don't load all into memory → use lazy loading / pagination
- Index by key fields → HashMap<productId, Product>
- Use in-memory cache for hot products (LRU cache with eviction)
- Search by category → pre-group into Map<Category, List<Product>>
- If asked to go deeper → acknowledge it's an HLD concern
  (sharding, Elasticsearch, Redis caching)
```

#### Scenario 4: *"What if we need to add audit logging to every action?"*

```
Answer:
- Use Decorator Pattern → wrap existing services with logging decorator
- Keeps existing code untouched (OCP)
```

```java
interface OrderService {
    Order placeOrder(OrderRequest request);
}

class OrderServiceImpl implements OrderService {
    public Order placeOrder(OrderRequest request) { /* business logic */ }
}

// Decorator adds logging without touching OrderServiceImpl
class AuditedOrderService implements OrderService {
    private OrderService delegate;
    private AuditLogger logger;

    AuditedOrderService(OrderService delegate, AuditLogger logger) {
        this.delegate = delegate;
        this.logger = logger;
    }

    public Order placeOrder(OrderRequest request) {
        logger.log("ORDER_PLACED", request);        // before
        Order order = delegate.placeOrder(request);  // actual work
        logger.log("ORDER_COMPLETED", order);        // after
        return order;
    }
}
```

---

### 🎯 Scalability Thinking Cheat Sheet

```
┌─────────────────────────────────────────────────────────────────────┐
│              SCALABILITY THINKING CHECKLIST                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  EXTENSIBILITY                                                      │
│  □ Can I add a new type without modifying existing code?            │
│  □ Am I using interfaces/strategies for things that vary?           │
│  □ Are my patterns (Strategy/Observer/State) enabling growth?       │
│                                                                     │
│  DATA GROWTH                                                        │
│  □ Am I using O(1) lookup where possible? (HashMap over List scan) │
│  □ Am I paginating large result sets?                               │
│  □ Am I loading lazily instead of eagerly?                          │
│  □ Do I have an eviction strategy for caches?                       │
│                                                                     │
│  THREAD SAFETY                                                      │
│  □ What is my shared mutable state?                                 │
│  □ Can two threads corrupt this data simultaneously?                │
│  □ Am I using the simplest safe solution?                           │
│     (Atomic → ConcurrentCollection → synchronized → Lock)          │
│  □ Am I locking at the right granularity?                           │
│     (Per-type, not the entire system)                               │
│                                                                     │
│  THE MAGIC PHRASE FOR INTERVIEWS                                    │
│  "In a single-instance setup, I'd use [synchronized/ConcurrentMap].│
│   For a distributed system, this would need [DB locks / message     │
│   queue / Redis] — but that's an HLD concern."                     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```