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