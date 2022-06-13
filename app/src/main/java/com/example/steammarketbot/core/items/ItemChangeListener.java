package com.example.steammarketbot.core.items;

public interface ItemChangeListener {

    void onItemAdded(Item item, String categoryName);
    void onItemRemoved(Item removedItem, String categoryName);
    void onItemPropertiesChanged(Item item);
    void onItemUserDataChanged(Item item, String categoryName);
    void onItemOrdersChanged(Item item, Orders orders);
    void onCategoryRemoved(String name);
    void onCategoryPropertiesChanged(Category category);
    void onCategoryNameChanged(String oldName, String newName);
}
