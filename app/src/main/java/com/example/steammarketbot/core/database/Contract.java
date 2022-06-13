package com.example.steammarketbot.core.database;

public abstract class Contract {

//    private List<String> entries;

    public final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ItemsContract.Entry.TABLE_NAME;

    public final String SQL_CREATE_ENTRIES;

    Contract(String SQL_CREATE_ENTRIES/*, ArrayList<String> entries*/) {
        this.SQL_CREATE_ENTRIES = SQL_CREATE_ENTRIES;
//        this.entries = entries;
    }

//    String getEntry(int i) {
//        return entries.get(i);
//    }
//
//    int entriesCount() {
//        return entries.size();
//    }
}
