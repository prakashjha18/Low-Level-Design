# Low-Level-Design

Core LLD

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