package com.example.steammarketbot.core.items;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class ItemsManager {

    private static final String SHARED_PREFERENCE_NAME = "Items";
    private static final String CATEGORIES_KEY = "Categories";

    private final HashSet<Category> categories;

    private final ItemChangeListener itemChangeListener;

    public ItemsManager(ItemChangeListener itemChangeListener) {
        categories = new HashSet<>();
        this.itemChangeListener = itemChangeListener;
    }

    public void addCategory(String name) {
        categories.add(new Category(name, itemChangeListener));
    }

    public void removeCategory(String name) {
        if (categories.removeIf(category -> category.getName().equals(name)))
            itemChangeListener.onCategoryRemoved(name);
    }

    public void renameCategory(String name, String newName) {
        getCategory(name).setName(newName);
    }

    public Category getCategory(String categoryName) {
        for (Category category: categories) {
            if (category.getName().equals(categoryName))
                return category;
        }
        return null;
    }

    public LinkedHashSet<String> getCategoryNames() {
        LinkedHashSet<String> names = new LinkedHashSet<String>();
        for (Category category : categories) {
            names.add(category.getName());
        }
        return names;
    }

    public void saveItems(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        Iterator<Category> categoryIterator = categories.iterator();
        Iterator<Item> itemIterator;
        Category category;
        TreeSet<String> convertedItems;
        LinkedHashSet<String> convertedCategories = new LinkedHashSet<>();
        while (categoryIterator.hasNext()) {
            category = categoryIterator.next();
            convertedCategories.add(new Gson().toJson(category));
            itemIterator = category.getItems().iterator();
            convertedItems = new TreeSet<>();
            while (itemIterator.hasNext()) {
                convertedItems.add(new Gson().toJson(itemIterator.next()));
            }
            sharedPreferences.edit().putStringSet(category.getName(), convertedItems).apply();
        }
        sharedPreferences.edit().putStringSet(CATEGORIES_KEY, convertedCategories).apply();
    }

    public void loadItems(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        Set<String> convertedCategories = sharedPreferences.getStringSet(CATEGORIES_KEY, null);
        if (convertedCategories != null) {
            Category category;
            Set<String> convertedItems;
            for (String convertedCategory: convertedCategories) {
                category = new Gson().fromJson(convertedCategory, Category.class);
                convertedItems = sharedPreferences.getStringSet(category.getName(), new HashSet<>());
                for (String convertedItem: convertedItems) {
                    category.addItem(new Gson().fromJson(convertedItem, Item.class));
                }
                categories.add(category);
            }
        }
    }
}
