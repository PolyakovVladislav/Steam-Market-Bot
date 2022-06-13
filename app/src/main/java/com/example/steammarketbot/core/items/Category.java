package com.example.steammarketbot.core.items;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.TreeSet;

public class Category implements Comparable<Category> {

    private TreeSet<Item> items;
    private String name;
    private boolean buyOn;
    private boolean sellOn;
    private int orderCount = 15;
    private int checkOrdersMin = 5;
    private boolean ordersOnly;

    private final ItemChangeListener itemChangeListener;

    public Category(String name,
                    boolean buyOn,
                    boolean sellOn,
                    int orderCount,
                    int checkOrdersMin,
                    boolean ordersOnly,
                    TreeSet<Item> items,
                    ItemChangeListener itemChangeListener) {
        this.name = name;
        this.buyOn = buyOn;
        this.sellOn = sellOn;
        this.orderCount = orderCount;
        this.checkOrdersMin = checkOrdersMin;
        this.ordersOnly = ordersOnly;
        this.items = items;
        this.itemChangeListener = itemChangeListener;
    }

    public Category(String name, ItemChangeListener itemChangeListener) {
        this.name = name;
        buyOn = false;
        sellOn = false;
        ordersOnly = false;
        this.itemChangeListener = itemChangeListener;
    }

    public void setUserData(boolean buyOn,
                            boolean sellOn,
                            int orderCount,
                            int checkOrdersMin,
                            boolean ordersOnly) {
        this.buyOn = buyOn;
        this.sellOn = sellOn;
        this.orderCount = orderCount;
        this.checkOrdersMin = checkOrdersMin;
        this.ordersOnly = ordersOnly;
        itemChangeListener.onCategoryPropertiesChanged(this);
    }

    public void removeItem(String urlName) {
        Item item = getItem(urlName);
        items.remove(item);
        itemChangeListener.onItemRemoved(item, name);
    }

    public void addItem(Item item) {
        items.add(item);
        itemChangeListener.onItemAdded(item, name);
    }

    public Item getItem(String urlName) {
        Iterator<Item> iterator = items.iterator();
        Item item;
        while (iterator.hasNext()) {
            item = iterator.next();
            if (item.getUrlName().equals(urlName))
                return item;
        }
        return null;
    }

    public LinkedHashSet<String> getItemUrlNames() {
        LinkedHashSet<String> urlNames = new LinkedHashSet<String>();
        for (Item item : items) {
            urlNames.add(item.getUrlName());
        }
        return urlNames;
    }

    public TreeSet<Item> getItems() {
        return items;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        itemChangeListener.onCategoryNameChanged(oldName, name);
    }

    @Override
    public int compareTo(Category category) {
        if (category.getName().equals(this.name))
            return 0;
        else
            return category.getName().compareTo(this.name);
    }

    public String getName() {
        return name;
    }

    public boolean isBuyOn() {
        return buyOn;
    }

    public boolean isSellOn() {
        return sellOn;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public int getCheckOrdersMin() {
        return checkOrdersMin;
    }

    public boolean isOrdersOnly() {
        return ordersOnly;
    }
}
