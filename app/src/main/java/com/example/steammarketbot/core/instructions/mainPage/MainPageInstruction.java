package com.example.steammarketbot.core.instructions.mainPage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.steammarketbot.core.SteamMarketBot;
import com.example.steammarketbot.core.Strings;
import com.example.steammarketbot.core.instructions.Instruction;
import com.example.steammarketbot.core.logic.Request;
import com.example.steammarketbot.core.profileInfo.Wallet;
import com.example.steammarketbot.core.profileInfo.lot.Lot;
import com.example.steammarketbot.core.profileInfo.lot.LotForBuy;
import com.example.steammarketbot.core.profileInfo.lot.LotForBuyList;
import com.example.steammarketbot.core.profileInfo.lot.LotForSell;
import com.example.steammarketbot.core.profileInfo.lot.LotForSellList;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

public abstract class MainPageInstruction extends Instruction {

    public static final String ASSETS = "assets";
    public static final String RESULTS_HTML = "results_html";
    public static final String APPID = "appid";
    public static final String MARKET_HASH_NAME = "market_hash_name";
    public static final String MARKET_NAME = "market_name";
    public static final String ID = "id";
    public static final String CONTEXT_ID = "contextid";
    public static final String HOVERS = "hovers";
    public static final String QUERY = "query";
    public static final String START = "start";
    public static final String COUNT = "count";
    public static final String PAGE_SIZE = "pagesize";
    public static final String TOTAL_COUNT = "total_count";

    private final long walletExpiresTime;
    protected HashSet<LotForSell> lotForSellSet;
    private int totalListingCount;
    private int visibleListingsCount;
    private int listingsPagesCount;
    private int listingsCountPerPage;
    private int totalBuyOrdersCount;
    private int visibleBuyOrdersCount;

    protected MainPageInstruction(long timeWindowStarts,
                                  long timeWindowEnds,
                                  int priority,
                                  long instructionTimeout,
                                  String instructionDescription,
                                  long walletExpiresTime) {
        super(timeWindowStarts, timeWindowEnds, priority, instructionTimeout, instructionDescription);
        this.walletExpiresTime = walletExpiresTime;
    }

    protected void superMainPageExecute() {
        superInstructionExecute();
        lotForSellSet = new HashSet<>();
    }

    protected void superMainPageStep(String response) {
        superInstructionStep(response);
        totalListingCount = -1;
        visibleListingsCount = -1;
        listingsPagesCount = -1;
        listingsCountPerPage = -1;
        totalBuyOrdersCount = -1;
        visibleBuyOrdersCount = -1;

        if (isMainPage()) {
            Wallet wallet = instructionResult.getWallet();
            if (wallet == null || wallet.getTimestamp() + walletExpiresTime > System.currentTimeMillis())
                instructionResult.setWallet(parseWallet());

            int maxSellListingCount = getListingsCountPerPage();

            lotForSellSet.addAll(getLotForSellSetFromMainPage());
            if (getListingsPagesCount() == 1) {
                instructionResult
                        .setSellLotList(
                                new LotForSellList(
                                        lotForSellSet,
                                        System.currentTimeMillis()));
            }

            int totalBuyOrdersCount = getTotalBuyOrdersCount();
            if (totalBuyOrdersCount > 0 && totalBuyOrdersCount == getVisibleBuyOrdersCount()) {
                instructionResult
                        .setBuyLotList(
                                new LotForBuyList(getBuyOrderLotSetFromMainPage(),
                                        System.currentTimeMillis()));
            }

            if (maxSellListingCount != 100)
                setCookieListingCount100();
        }
    }

    @Nullable
    private Wallet parseWallet() {
        Elements scripts = document.getElementsByTag("script");
        String walletInfo;
        for (Element script: scripts) {
            if (script.data().contains("g_rgWalletInfo")) {
                walletInfo = Strings.search(script.data(), "var g_rgWalletInfo = \\{", "\\};");
                float walletBalance = Float.parseFloat(Strings.search(walletInfo, "wallet_balance\":\"", "\""));
                float walletCurrency = Float.parseFloat(Strings.search(walletInfo, "\"wallet_currency\":", ","));
                String walletCountry = Strings.search(walletInfo, "wallet_country\":\"", "\"");
                float walletFee = Float.parseFloat(Strings.search(walletInfo, "wallet_fee\":\"", "\""));
                float walletFeeMinimum = Float.parseFloat(Strings.search(walletInfo, "wallet_fee_minimum\":\"", "\""));
                float walletFeePercent = Float.parseFloat(Strings.search(walletInfo, "wallet_fee_percent\":\"", "\""));
                float walletPublisherFeePercent = Float.parseFloat(Strings.search(walletInfo, "wallet_publisher_fee_percent_default\":\"", "\""));
                float walletFeeBase = Float.parseFloat(Strings.search(walletInfo, "wallet_fee_base\":\"", "\""));
                return new Wallet(
                        walletBalance,
                        walletCurrency,
                        walletCountry,
                        walletFee,
                        walletFeeMinimum,
                        walletFeePercent,
                        walletPublisherFeePercent,
                        walletFeeBase,
                        System.currentTimeMillis());
            }
        }
        return null;
    }

    protected boolean isMainPage() {
        return document.getElementById("myMarketTabs") != null;
    }

    protected HashSet<LotForSell> getLotForSellSetFromMainPage() {
        Elements listingsRows = getElementsListingsFromMainPage();
        HashSet<LotForSell> lotList = new HashSet<>();
        for (Element listingRow: listingsRows) {
            lotList.add(parseListing(listingRow));
        }
        return lotList;
    }

    protected Elements getElementsListingsFromMainPage() {
        Elements listings = new Elements();
        Element tabContentsMyActiveMarketListingsRows =
                document.getElementById("tabContentsMyActiveMarketListingsRows");
        if (tabContentsMyActiveMarketListingsRows != null)
            listings.addAll(
                    tabContentsMyActiveMarketListingsRows
                            .getElementsByClass("market_recent_listing_row"));
        return listings;
    }

    private LotForSell parseListing(Element listingRow) {
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

    protected int getTotalListingsCount() {
        if (totalListingCount != -1)
            return totalListingCount;
        Element my_market_selllistings_number = document.getElementById("my_market_selllistings_number");
        if (my_market_selllistings_number != null)
            totalListingCount = Integer.parseInt(my_market_selllistings_number.text());
        return totalListingCount;
    }

    protected int getVisibleListingsCount() {
        if (visibleListingsCount != -1)
            return visibleListingsCount;
        return Objects.requireNonNull(document.getElementById("tabContentsMyActiveMarketListingsRows"))
                .getElementsByClass("market_listing_row")
                .size();
    }

    protected Request getRequestForItemsPage(int page) {
        Request request = new Request(host);
        request.setPath("/market/mylistings/render/");
        request.putParameters(QUERY, "");
        request.putParameters(START, page * 100);
        request.putParameters(COUNT, 100);
        request.dontChangeReferer();
        return request;
    }

    protected int getListingsPagesCount() {
        if (listingsPagesCount != -1)
            return listingsPagesCount;
        float totalSellingItemsCount = getTotalListingsCount();
        float maxSellListingCount = getListingsCountPerPage();
        double result = Math.ceil(totalSellingItemsCount / maxSellListingCount);
        return (int) result;
    }

    protected int getListingsCountPerPage() {
        if (listingsCountPerPage != -1)
            return listingsCountPerPage;
        if (Objects.requireNonNull(
                document.getElementById("my_listing_pagesize_10")).className().equals("disabled"))
            return 10;
        else if (Objects.requireNonNull(
                document.getElementById("my_listing_pagesize_30")).className().equals("disabled"))
            return 30;
        else if (Objects.requireNonNull(
                document.getElementById("my_listing_pagesize_100")).className().equals("disabled"))
            return 100;
        else
            return 0;
    }

    protected void setCookieListingCount100() {
        //SetCookie( 'ActListPageSize', cListings, 365, '/market' );
        requestList.putAdditionalCookies(
                host,
                "/market",
                "ActListPageSize",
                "100",
                365L * 24 * 60 * 60 * 1000);
    }

    protected int getTotalBuyOrdersCount() {
        if (totalBuyOrdersCount != -1)
            return totalBuyOrdersCount;
        return Integer.parseInt(Objects.requireNonNull(document.getElementById("my_market_buylistings_number"))
        .text());
    }

    protected int getVisibleBuyOrdersCount() {
        if (visibleBuyOrdersCount != -1)
            return visibleBuyOrdersCount;
        Elements buyOrdersListing = document
                .getElementsByClass(
                        "my_listing_section market_content_block market_home_listing_table")
                ;
        if (buyOrdersListing.size()
                > 0) {
            return buyOrdersListing.get(0)
                    .getElementsByClass(
                            "market_listing_row market_recent_listing_row")
                    .size();
        }
        return 0;
    }

    protected HashSet<LotForBuy> getBuyOrderLotSetFromMainPage() {
        HashSet<LotForBuy> lotList = new HashSet<>();
        Elements ordersList = document
                .getElementsByClass(
                        "my_listing_section market_content_block market_home_listing_table")
                .get(0)
                .getElementsByClass("market_listing_row market_recent_listing_row");
        for(Element order: ordersList) {
            if (order.className().equals("market_listing_row market_recent_listing_row"))
                lotList.add(parseBuyOrderRow(order));
        }
        return lotList;
    }

    protected LotForBuy parseBuyOrderRow(@NonNull Element buyOrder) {
        long listingId = Long.parseLong(
                Strings.search(
                        buyOrder.getElementsByClass("market_listing_cancel_button").get(0)
                                .getElementsByTag("a").get(0)
                                .attr("href"),
                        "\\('",
                        "'\\)"));
        String href = buyOrder.getElementsByClass("market_listing_item_name_link").get(0)
                .attr("href");
        href = Strings.search(
                href,
                "/\\d+/",
                "$",
                true).substring(1);
        int appid = Integer.parseInt(Strings.stringPartLeft(href, "/"));
        String urlName = Strings.stringPartRight(href, "/");
        String itemName = buyOrder.getElementsByClass("market_listing_item_name_link").get(0)
                .text();
        int count = Integer.parseInt(buyOrder.getElementsByClass("market_listing_buyorder_qty").get(0)
                .getElementsByClass("market_listing_price").get(0).text().trim());
        buyOrder.getElementsByClass("market_listing_right_cell market_listing_my_price").get(0)
                .getElementsByClass("market_listing_price").get(0).child(0).remove();
        String budgetStr = buyOrder.getElementsByClass("market_listing_right_cell market_listing_my_price").get(0)
                .getElementsByClass("market_listing_price").text();
        budgetStr = budgetStr.trim();
        budgetStr = budgetStr.substring(0, budgetStr.length() - 1);
        float budget = Float.parseFloat(budgetStr.replace(",", "."));
        return new LotForBuy(appid, listingId, urlName, itemName, count, budget);
    }

    protected HashSet<LotForSell> parseJsonResponse(JSONObject json) throws ParseException {
        HashSet<LotForSell> itemsList = new HashSet<>();
        try {
            JSONObject assets = json.getJSONObject(ASSETS);
            String appId;
            Iterator<String> appIds = assets.keys();
            JSONObject someObject;
            JSONObject items;
            String itemKey;
            JSONObject item;
            String hovers = json.getString(HOVERS);
            String resultHtml = json.getString(RESULTS_HTML);
            while (appIds.hasNext()) {
                appId = appIds.next();
                someObject = assets.getJSONObject(appId);
                Iterator<String> someObjectIterator = someObject.keys();
                String someObjectKey;
                while (someObjectIterator.hasNext()) {
                    someObjectKey = someObjectIterator.next();
                    items = someObject.getJSONObject(someObjectKey);
                    Iterator<String> itemsKeys = items.keys();
                    while (itemsKeys.hasNext()) {
                        itemKey = itemsKeys.next();
                        item = items.getJSONObject(itemKey);
                        itemsList.add(parseItem(item, hovers, resultHtml));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemsList;
    }

    protected LotForSell parseItem(JSONObject jsonItem, String hovers, String resultHtml) throws JSONException, ParseException {
        int appid = jsonItem.getInt(APPID);
        String urlName = jsonItem.getString(MARKET_HASH_NAME);
        String itemName = jsonItem.getString(MARKET_NAME);
        long itemId = jsonItem.getLong(ID);
        long listingId = 0;

        ArrayList<String> block = Strings.search(hovers, "CreateItemHoverFromContainer\\(", "\\)", false, false);
        for (String hover: block) {
            if (hover.contains(String.valueOf(itemId)) && hover.contains("name")) {
                listingId = Long.parseLong(Strings.search(hover, "mylisting_", "_name"));
                break;
            }
        }
        if (listingId == 0)
            throw new ParseException("Cant parse item " + jsonItem, -1);
        Date placedDate = null;
        String mPlacedDate;
        String mBuyerPays = null;
        String mYouReceive = null;
        Element parsedResultHtml = Jsoup.parse(resultHtml);
        for (Element hover: parsedResultHtml.getElementsByClass("market_recent_listing_row")) {
            if (hover.className().contains(String.valueOf(listingId))) {
                mPlacedDate = hover.getElementsByClass("market_listing_listed_date").get(0).text();
                placedDate = LotForSell.parseDate(mPlacedDate);
                Element price = hover.getElementsByClass("market_listing_price").get(0);
                mBuyerPays = price.child(0).child(0).text();
                mYouReceive = price.child(0).child(2).text();
                break;
            }
        }
        if (mBuyerPays == null || mYouReceive == null || placedDate == null)
            throw new ParseException("Cant parse item " + jsonItem, -1);
        float buyerPays = parsePrice(mBuyerPays);
        float youReceive = parsePrice(mYouReceive);
        int contextId = jsonItem.getInt(CONTEXT_ID);
        return new LotForSell(
                listingId,
                appid,
                urlName,
                itemName,
                placedDate,
                buyerPays,
                youReceive,
                itemId,
                contextId);
    }

    protected float parsePrice(String price) {
        price = price
                .trim()
                .replace(",", ".")
                .replace("(", "")
                .replace(")", "");
        price = price.substring(0, price.length() - 1);
        return Float.parseFloat(price);
    }

    protected Request getCancelListingRequest(LotForSell lot) {
        return getCancelRequest(lot);
    }

    protected Request getCancelBuyOrderRequest(LotForBuy lot) {
        return getCancelRequest(lot);
    }

    private Request getCancelRequest(Lot lot) {
        Request request = new Request(host);
        request.setPath("/market/removelisting/" + lot.getId());
        request.putData("sessionid", SteamMarketBot.getSessionId());
        return request;
    }
}
