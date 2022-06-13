package com.example.steammarketbot.core.instructions.itemPage;

import com.example.steammarketbot.core.SteamMarketBot;
import com.example.steammarketbot.core.Strings;
import com.example.steammarketbot.core.instructions.Instruction;
import com.example.steammarketbot.core.items.Item;
import com.example.steammarketbot.core.items.ItemPageListing;
import com.example.steammarketbot.core.items.Orders;
import com.example.steammarketbot.core.logic.Request;
import com.example.steammarketbot.core.profileInfo.lot.LotForSell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public abstract class ItemPageInstruction extends Instruction {

    private Element tabContentsMyListings;
    private Element tabContentsMyActiveMarketListingsTable;
    private boolean itemPage;
    private boolean jsonHistogram;
    private long itemNameId;
    private String urlName;
    private boolean manualBuyAvailable;
    private boolean ordersAvailable;
    private Orders orders;

    protected ItemPageInstruction(
            long timeWindowStarts,
            long timeWindowEnds,
            int priority,
            long instructionTimeout,
            String instructionDescription) {
        super(
                timeWindowStarts,
                timeWindowEnds,
                priority,
                instructionTimeout,
                instructionDescription);
    }

    protected void superItemPageExecute() {
        superInstructionExecute();
    }

    protected void superItemPageStep(Item item, String htmlItemPage, String jsonHistogram) throws JSONException {
        superInstructionStep(htmlItemPage);
        parsePage(jsonHistogram);
        if (isItemPage()) {
            item.setItemProperties(
                    getItemNameId(),
                    isItemPageOrderAvailable(),
                    isItemPageManualBuyAvailable(),
                    item.getCategory());

//            SteamMarketBot.getLotForSellList().addSet(
//                    getSellListingsSet());
            HashSet<LotForSell> sells = getSellListingsSet();

            if (isJsonHistogram()) {
                item.setOrders(getOrders());
            }
        }
    }

    private void parsePage(String mJsonHistogram) throws JSONException {
        itemPage = document.getElementsByClass("market_listing_iteminfo").size() > 0;

        jsonHistogram = mJsonHistogram.contains("buy_order_graph") &&
                        mJsonHistogram.contains("sell_order_graph");
        tabContentsMyListings = document.getElementById("tabContentsMyListings");

        for (Element script: document.getElementsByTag("script")) {
            if (script.data().contains("g_rgAppContextData ")) {
                itemNameId = Long.parseLong(
                        Strings.search(
                                script.data(),
                                "Market_LoadOrderSpread\\(",
                                "\\)").trim());
            }
        }

        tabContentsMyActiveMarketListingsTable =
                document.getElementById("tabContentsMyActiveMarketListingsTable");

        urlName = "";
        Elements market_listing_nav = document.getElementsByClass("market_listing_nav");
        if (market_listing_nav.size() > 0) {
            String href;
            for (Element element : market_listing_nav.get(0).getElementsByTag("a")) {
                href = element.attr("href");
                if (href.contains("listings/")) {
                    urlName =  Strings.search(
                            href,
                            "/\\d+/",
                            "$");
                }
            }
        }

        Element searchResultsRows = document.getElementById("searchResultsRows");
        manualBuyAvailable = searchResultsRows != null;

        ordersAvailable = document.getElementsByClass("market_commodity_order_block").size() > 0 ||
            document.getElementById("market_buyorder_info") != null;

        orders = parseOrders(mJsonHistogram);
    }

    protected boolean isItemPage() {
        return itemPage;
    }

    protected boolean isJsonHistogram() {
        return jsonHistogram;
    }

    private Orders parseOrders(String jsonHistogram) throws JSONException {
        ArrayList<Integer> sellingOrdersCount = new ArrayList<>();
        ArrayList<Double> sellingOrdersPrices = new ArrayList<>();
        ArrayList<ItemPageListing> itemPageListings = new ArrayList<>();
        ArrayList<Integer> buyingOrdersCount = new ArrayList<>();
        ArrayList<Double> buyingOrdersPrices = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonHistogram);
        JSONArray orders = jsonObject.getJSONArray("buy_order_graph");
        int parsingOrdersCount;
        if (orders.length() < 10)
            parsingOrdersCount = orders.length();
        else
            parsingOrdersCount = 10;
        JSONArray order;
        int ordersCount;
        for (int i = 0; i < parsingOrdersCount; i++) {
            order = orders.getJSONArray(i);
            buyingOrdersPrices.add(order.getDouble(0));
            ordersCount = order.getInt(1);
            for (int y = i - 1; y >= 0; y--) {
                ordersCount = ordersCount - orders.getJSONArray(y).getInt(1);
            }
            buyingOrdersCount.add(ordersCount);
        }

        orders = jsonObject.getJSONArray("sell_order_graph");
        if (orders.length() < 10)
            parsingOrdersCount = orders.length();
        else
            parsingOrdersCount = 10;
        for (int i = 0; i < parsingOrdersCount; i++) {
            order = orders.getJSONArray(i);
            sellingOrdersPrices.add(order.getDouble(0));
            ordersCount = order.getInt(1);
            if (i > 0)
                ordersCount = ordersCount - orders.getJSONArray(i -1).getInt(1);

            sellingOrdersCount.add(ordersCount);
        }

        Element manualBuyListingsBlock = document.getElementById("searchResultsRows");

        if (manualBuyListingsBlock != null) {
            Elements market_listing_row =
                    manualBuyListingsBlock.getElementsByClass("market_listing_row");
            ItemPageListing itemPageListing;
            long listingID;
            int appId;
            int contextId;
            long itemId;
            float price;
            String buyMarketListingScript;
            String[] scriptFunctionParams;
            String mListingID;
            String mAppId;
            String mContextId;
            String mItemId;
            String mPrice;
            for (Element row: market_listing_row) {
                buyMarketListingScript = Strings.search(
                        row.getElementsByClass("item_market_action_button")
                                .get(0).attr("href"),
                        "BuyMarketListing\\(",
                        "\\)",
                        false);
                scriptFunctionParams = Strings.stringPart(
                        buyMarketListingScript,
                        ",");
                mListingID = scriptFunctionParams[1].trim();
                mListingID = mListingID.substring(1, mListingID.length() - 1);
                mAppId = scriptFunctionParams[2].trim();
                mContextId = scriptFunctionParams[3].trim();
                mContextId = mContextId.substring(1, mContextId.length() - 1);
                mItemId = scriptFunctionParams[4].trim();
                mItemId = mItemId.substring(1, mItemId.length() - 1);
                mPrice = row
                        .getElementsByClass("market_listing_price_with_fee")
                        .get(0)
                        .text();
                listingID = Long.parseLong(mListingID);
                appId = Integer.parseInt(mAppId);
                contextId = Integer.parseInt(mContextId);
                itemId = Long.parseLong(mItemId);
                price = parsePrice(mPrice);
                itemPageListing = new ItemPageListing(
                        listingID,
                        appId,
                        contextId,
                        itemId,
                        price
                );
                itemPageListings.add(itemPageListing);
            }
        }

        return new Orders(
                sellingOrdersCount,
                sellingOrdersPrices,
                itemPageListings,
                buyingOrdersCount,
                buyingOrdersPrices,
                System.currentTimeMillis()
        );
    }

    protected Orders getOrders() {
        return orders;
    }

    protected long getItemNameId() {
        return itemNameId;
    }

    protected boolean isItemPageOrderAvailable() {
        return ordersAvailable;
    }

    protected boolean isItemPageManualBuyAvailable() {
        return manualBuyAvailable;
    }

    protected Request getItemOrdersHistogram(long itemNameID) {
        Request request = new Request(host);
        request.setPath("/market/itemordershistogram?country=UA&" +
                "language=russian&" +
                "currency=" + SteamMarketBot.getWallet().getWalletCurrency() + "&" +
                "item_nameid=" + itemNameID + "&" +
                "two_factor=0");
        request.dontChangeReferer();
        return request;
    }

    protected boolean isContainingSellOrder(Document document) {
        return tabContentsMyActiveMarketListingsTable != null;
    }

    protected HashSet<LotForSell> getSellListingsSet() {
        HashSet<LotForSell> lotForSellSet = new HashSet<>();
        if (hasMySellListings()) {
            for (Element row :getSellOrdersElement().getElementsByClass("market_listing_row")) {
                lotForSellSet.add(parseListingRow(row));
            }
        }
        return lotForSellSet;
    }

    private LotForSell parseListingRow(Element listingRow) {
        String[] scriptInfo = Strings.stringPart(
                Strings.search(
                        listingRow
                                .getElementsByClass("market_listing_cancel_button")
                                .get(0)
                                .getElementsByTag("a").get(0).attr("href"),
                        "\\(",
                        "\\)"),
                ",");
        long listingId = Long.parseLong(Strings.search(scriptInfo[1], "'", "'"));
        int appid = Integer.parseInt(scriptInfo[2].trim());
        String urlName = Strings.search(
                listingRow
                        .getElementsByClass("market_listing_item_name_link")
                        .get(0)
                        .attr("href"),
                "/\\d+/", "$");
        String itemName = listingRow.getElementsByClass("market_listing_item_name_link")
                .get(0)
                .text();
        Date placeDate = LotForSell
                .parseDate(listingRow
                        .getElementsByClass("market_listing_listed_date")
                        .get(0).text());
        Element price = listingRow.getElementsByClass("market_listing_price").get(0);
        String mBuyerPays = price.child(0).child(0).text();
        float buyerPays = parsePrice(mBuyerPays);
        String mYouReceive = price.child(0).child(2).text();
        float youReceive = parsePrice(mYouReceive);
        String mItemId = scriptInfo[4].trim();
        long itemId = Long.parseLong(mItemId.substring(1, mItemId.length() - 1));
        String mContextId = scriptInfo[3].trim();
        int contextId = Integer.parseInt(mContextId.substring(1, mContextId.length() - 1));
        return new LotForSell(
                listingId,
                appid,
                urlName,
                itemName,
                placeDate,
                buyerPays,
                youReceive,
                itemId,
                contextId);
    }

    protected Element getSellOrdersElement() {
        return tabContentsMyActiveMarketListingsTable;
    }

    protected boolean hasMySellListings() {
        return tabContentsMyListings != null;
    }

    protected String getUrlName(Document document) {
        return urlName;
    }

    private float parsePrice(String price) {
        price = price
                .trim()
                .replace(",", ".")
                .replace("(", "")
                .replace(")", "");
        price = price.substring(0, price.length() - 1);
        return Float.parseFloat(price);
    }

}
