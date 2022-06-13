package com.example.steammarketbot.core.eventBus;

import com.example.steammarketbot.core.items.Category;
import com.example.steammarketbot.core.items.Item;

public interface ServiceEventReceiverListener {

    void doLogin(String username, String password, String twoFactorCode);
    void setPause();
    void setUnpause();
    void removeItem(Item item);
    void removeCategory(Category category);
    void checkItem(Item item);
}
