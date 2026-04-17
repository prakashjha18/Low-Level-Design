package FoodOrderingSystem;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {
    private final String id;
    private final String name;
    private final List<MenuItem> menu;

    public Restaurant(String id, String name) {
        this.id = id;
        this.name = name;
        this.menu = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<MenuItem> getMenu() { return menu; }

    public void addMenuItem(MenuItem item) {
        menu.add(item);
    }
}
