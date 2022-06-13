package com.example.steammarketbot.core.instructions.itemPage;

import static org.mockito.Mockito.mock;

import com.example.steammarketbot.core.SteamMarketBot;
import com.example.steammarketbot.core.httpsClient.HttpClientResponse;
import com.example.steammarketbot.core.instructions.Instruction;
import com.example.steammarketbot.core.items.Category;
import com.example.steammarketbot.core.items.Item;
import com.example.steammarketbot.core.items.ItemChangeListener;
import com.example.steammarketbot.core.items.Orders;
import com.example.steammarketbot.core.logic.RequestList;
import com.example.steammarketbot.core.testHTML.TestResponse;

import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.script.ScriptException;

public class ItemPageInstructionTest {

    static ItemPageInstruction itemPageInstruction;
    static SteamMarketBot steamMarketBot;

    @BeforeClass
    public static void prepare() {
        steamMarketBot = mock(SteamMarketBot.class);
       itemPageInstruction = new ItemPageInstruction(
                0,
                0,
                Instruction.PRIORITY_CHECK_ITEMS,
                10000,
                "") {
            @Override
            public RequestList execute() {
                return null;
            }

            @Override
            public RequestList getRequestList(ArrayList<HttpClientResponse> httpClientResponses) throws JSONException, IOException, ScriptException, NoSuchMethodException, ParseException {
                return null;
            }
        };
    }

    @Test
    public void superItemPageStep_CaseManualBuyAvailable() throws IOException, JSONException {
        itemPageInstruction.execute();
        Item item = new Item(
                null,
                "730",
                "AUG%20%7C%20Surveillance%20%28Field-Tested%29",
                "AUG | Слежка",
                "",
                new ItemChangeListener() {
                    @Override
                    public void onItemAdded(Item item, String categoryName) {

                    }

                    @Override
                    public void onItemRemoved(Item removedItem, String categoryName) {

                    }

                    @Override
                    public void onItemPropertiesChanged(Item item) {
                        System.out.println("onItemPropertiesChanged");
                    }

                    @Override
                    public void onItemUserDataChanged(Item item, String categoryName) {
                        System.out.println("onItemUserDataChanged");
                    }

                    @Override
                    public void onItemOrdersChanged(Item item, Orders orders) {
                        System.out.println("onItemOrdersChanged");
                    }

                    @Override
                    public void onCategoryRemoved(String name) {

                    }

                    @Override
                    public void onCategoryPropertiesChanged(Category category) {

                    }

                    @Override
                    public void onCategoryNameChanged(String oldName, String newName) {

                    }
                });

        itemPageInstruction.superItemPageStep(
                item,
                TestResponse.getResponse(TestResponse.ITEM_PAGE1),
                TestResponse.getResponse(TestResponse.JSON_HISTOGRAM1));
    }

    @Test
    public void superItemPageStep_CaseOrderBuyOnly() throws IOException, JSONException {
        itemPageInstruction.execute();
        Item item = new Item(
                null,
                "730",
                "AUG%20%7C%20Surveillance%20%28Field-Tested%29",
                "AUG | Слежка",
                "",
                new ItemChangeListener() {
                    @Override
                    public void onItemAdded(Item item, String categoryName) {

                    }

                    @Override
                    public void onItemRemoved(Item removedItem, String categoryName) {

                    }

                    @Override
                    public void onItemPropertiesChanged(Item item) {
                        System.out.println("onItemPropertiesChanged");
                    }

                    @Override
                    public void onItemUserDataChanged(Item item, String categoryName) {
                        System.out.println("onItemUserDataChanged");
                    }

                    @Override
                    public void onItemOrdersChanged(Item item, Orders orders) {
                        System.out.println("onItemOrdersChanged");
                    }

                    @Override
                    public void onCategoryRemoved(String name) {

                    }

                    @Override
                    public void onCategoryPropertiesChanged(Category category) {

                    }

                    @Override
                    public void onCategoryNameChanged(String oldName, String newName) {

                    }
                });

        itemPageInstruction.superItemPageStep(
                item,
                TestResponse.getResponse(TestResponse.ITEM_PAGE1),
                TestResponse.getResponse(TestResponse.JSON_HISTOGRAM1));
    }
}