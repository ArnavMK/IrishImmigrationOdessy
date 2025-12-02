package com.ise.officeescape.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Inventory
 */
public class Inventory {
    
    private Map<String, Item> inventory;

    public Inventory() {
        this.inventory = new HashMap<String, Item>();
    }

    public void addItem(Item item) {
        inventory.put(item.getName(), item);
    }

    public void removeItem(String itemName) {
        inventory.remove(itemName);
    }

    public boolean hasItem(String itemName)  {
        return inventory.containsKey(itemName);
    }

    public boolean hasItem(Item item) {
        return inventory.containsValue(item);
    }

    public Map<String, Item> getInvetoryMap() {
        return inventory;
    }
}
