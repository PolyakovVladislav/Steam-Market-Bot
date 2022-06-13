package com.example.steammarketbot.core.items;

import java.util.ArrayList;
import java.util.Objects;

public class Orders {

    final private ArrayList<Integer> sellingOrdersCount;
    final private ArrayList<Double> sellingOrdersPrices;

    final private ArrayList<ItemPageListing> itemPageListings;

    final private ArrayList<Integer> buyingOrdersCount;
    final private ArrayList<Double> buyingOrdersPrices;
    final private long timeStamp;

    public Orders(ArrayList<Integer> sellingOrdersCount, ArrayList<Double> sellingOrdersPrices,
                  ArrayList<ItemPageListing> itemPageListings, ArrayList<Integer> buyingOrdersCount, ArrayList<Double> buyingOrdersPrices,
                  long timeStamp) {
        this.sellingOrdersCount = sellingOrdersCount;
        this.sellingOrdersPrices = sellingOrdersPrices;
        this.itemPageListings = itemPageListings;
        this.buyingOrdersCount = buyingOrdersCount;
        this.buyingOrdersPrices = buyingOrdersPrices;
        this.timeStamp = timeStamp;
    }

    int getSellingOrdersCount(int i) {
        return sellingOrdersCount.get(i);
    }

    Double getSellingOrdersPrices(int i) {
        return sellingOrdersPrices.get(i);
    }

    int getBuyingOrdersCount(int i) {
        return buyingOrdersCount.get(i);
    }

    Double getBuyingOrdersPrices(int i) {
        return buyingOrdersPrices.get(i);
    }

    ItemPageListing getItemPageListing(int i) {
        return itemPageListings.get(i);
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Orders)) return false;
        Orders orders = (Orders) o;
        return timeStamp == orders.timeStamp && Objects.equals(sellingOrdersCount, orders.sellingOrdersCount) && Objects.equals(sellingOrdersPrices, orders.sellingOrdersPrices) && Objects.equals(itemPageListings, orders.itemPageListings) && Objects.equals(buyingOrdersCount, orders.buyingOrdersCount) && Objects.equals(buyingOrdersPrices, orders.buyingOrdersPrices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sellingOrdersCount, sellingOrdersPrices, itemPageListings, buyingOrdersCount, buyingOrdersPrices, timeStamp);
    }
}
