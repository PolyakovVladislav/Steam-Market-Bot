package com.example.steammarketbot.core.items;

import java.io.File;

public class Item implements Comparable<Item> {

    //Стартовые исходные данныем при добавлнии
    private final File image;
    private final String appid;
    private final String urlName;
    private final String itemName;

    //Данные после проверки стриницы
    private long itemNameID;
    private boolean orderBuyAvailable;
    private boolean manualBuyAvailable;

    //Переменные данные регулярного сканирования
    private Orders orders;
    private long checkedTimestamp;

    //Данные полученные от пользователя
    private int count;
    private float budget;
    private boolean buyOn;
    private float youReceive;
    private float buyerPay;
    private boolean sellOn;
    private String category;

    private long orderedTimestamp;

    private final ItemChangeListener itemChangeListener;

    public Item(File image, String appid, String urlName, String itemName, String category,
                ItemChangeListener itemChangeListener) {
        this.image = image;
        this.itemName = itemName;
        this.category = category;
        this.appid = appid;
        this.urlName = urlName;
        count = 15;
        budget = 0;
        buyOn = false;
        youReceive = 0;
        buyerPay = 0;
        sellOn = false;
        itemNameID = 0;
        orderBuyAvailable = false;
        manualBuyAvailable = false;
        checkedTimestamp = 0;
        orderedTimestamp = 0;
        this.itemChangeListener = itemChangeListener;
    }

    public Item(
            File image, String itemName, int count,
            float budget, boolean buyOn, float youReceive,
                float buyerPay, boolean sellOn, long itemNameID,
            boolean orderBuyAvailable, boolean manualBuyAvailable, String category,
            String appid, String urlName, ItemChangeListener itemChangeListener) {
        this.image = image;
        this.itemName = itemName;
        this.count = count;
        this.budget = budget;
        this.buyOn = buyOn;
        this.youReceive = youReceive;
        this.buyerPay = buyerPay;
        this.sellOn = sellOn;
        this.itemNameID = itemNameID;
        this.orderBuyAvailable = orderBuyAvailable;
        this.manualBuyAvailable = manualBuyAvailable;
        this.category = category;
        this.appid = appid;
        this.urlName = urlName;
        checkedTimestamp = 0;
        orderedTimestamp = 0;
        this.itemChangeListener = itemChangeListener;
    }

    public void setOrderedTimestamp(long timestamp) {
        orderedTimestamp = timestamp;
    }

    public void setItemProperties(
            long itemNameID,
            boolean orderBuyAvailable,
            boolean manualBuyAvailable,
            String category) {
        boolean propertiesChanged =
                this.orderBuyAvailable != orderBuyAvailable ||
                this.manualBuyAvailable != manualBuyAvailable ||
                !this.category.equals(category);
        this.itemNameID = itemNameID;
        this.orderBuyAvailable = orderBuyAvailable;
        this.manualBuyAvailable = manualBuyAvailable;
        this.category = category;
        if (propertiesChanged)
            itemChangeListener.onItemPropertiesChanged(this);
    }

    public void setOrders(
            Orders orders) {
        if (this.orders == null ||
                (!orders.equals(this.orders) && orders.hashCode() != this.orders.hashCode())) {
            this.orders = orders;
            checkedTimestamp = System.currentTimeMillis();
            itemChangeListener.onItemOrdersChanged(this, orders);
        }
    }

    public void setUserData(int count,
                            float budget,
                            boolean buyOn,
                            float youReceive,
                            float buyerPay,
                            boolean sellOn) {
        this.count = count;
        this.budget = budget;
        this.buyOn = buyOn;
        this.youReceive = youReceive;
        this.buyerPay = buyerPay;
        this.sellOn = sellOn;
        itemChangeListener.onItemUserDataChanged(this, category);
    }

    public long getOrderedTimestamp() {
        return orderedTimestamp;
    }

    public long getCheckedTimestamp() {
        return checkedTimestamp;
    }

    public File getImage() {
        return image;
    }

    public String getItemName() {
        return itemName;
    }

    public long getItemNameID() {
        return itemNameID;
    }

    public int getCount() {
        return count;
    }

    public float getBudget() {
        return budget;
    }

    public boolean isBuyOn() {
        return buyOn;
    }

    public float getYouReceive() {
        return youReceive;
    }

    public float getBuyerPay() {
        return buyerPay;
    }

    public boolean isSellOn() {
        return sellOn;
    }

    public boolean isOrderBuyAvailable() {
        return orderBuyAvailable;
    }

    public boolean isManualBuyAvailable() {
        return manualBuyAvailable;
    }

    public Orders getOrders() {
        return orders;
    }

    public String getCategory() {
        return category;
    }

    public String getAppid() {
        return appid;
    }

    public String getUrlName() {
        return urlName;
    }

    @Override
    public int compareTo(Item comparableItem) {
        //- this больше (выше в списке)
        //0 равны
        //+ сравниваемый больше (ниже в списке)
        if (urlName.equals(comparableItem.getUrlName()))
            return 0;
        else if (buyOn && !comparableItem.isBuyOn())
            return -1;
        else if (!buyOn && comparableItem.isBuyOn())
            return 1;
        else {
            if (budget - comparableItem.getBudget() > 0)
                return 1;
            else
                return -1;
        }
    }
}