package com.example.steammarketbot.core.instructions.mainPage;

import static org.junit.Assert.assertEquals;

import com.example.steammarketbot.core.httpsClient.HttpClientResponse;
import com.example.steammarketbot.core.instructions.Instruction;
import com.example.steammarketbot.core.instructions.InstructionResult;
import com.example.steammarketbot.core.logic.RequestList;
import com.example.steammarketbot.core.profileInfo.Wallet;
import com.example.steammarketbot.core.profileInfo.lot.LotForBuy;
import com.example.steammarketbot.core.profileInfo.lot.LotForBuyList;
import com.example.steammarketbot.core.profileInfo.lot.LotForSell;
import com.example.steammarketbot.core.profileInfo.lot.LotForSellList;
import com.example.steammarketbot.core.testHTML.TestResponse;

import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import javax.script.ScriptException;

public class MainPageInstructionTest {

    static MainPageInstruction mainPageInstruction;

    @BeforeClass
    public static void prepare() throws IOException, NoSuchFieldException, IllegalAccessException {

        mainPageInstruction = new MainPageInstruction(
                0,
                0,
                Instruction.PRIORITY_LOGIN,
                15000,
                "Description",
                300000) {
            @Override
            public RequestList execute() {
                return null;
            }

            @Override
            public RequestList getRequestList(ArrayList<HttpClientResponse> httpClientResponses) throws JSONException, IOException, ScriptException, NoSuchMethodException {
                return null;
            }
        };
    }

    @Test
    public void testSuperMainPageInstruction_with_test_html1() throws IOException {
        mainPageInstruction.superMainPageExecute();
        mainPageInstruction
                .superMainPageStep(TestResponse.getResponse(
                        TestResponse.MAIN_PAGE_1));
        InstructionResult mainPageInstructionResult = mainPageInstruction.getInstructionResult();
        Wallet actualWallet = mainPageInstructionResult.getWallet();
        Wallet expectedWallet = new Wallet(
                360063,
                18,
                "UA",
                1,
                1,
                0.05f,
                0.10f,
                0,
                actualWallet.getTimestamp());
        assertEquals(expectedWallet, actualWallet);

        LotForBuy expectedLotForBuy;
        HashSet<LotForBuy> lotForBuyArrayList = new HashSet<>();
        expectedLotForBuy = new LotForBuy(
                730,
                5080507730L,
                "Sticker%20%7C%20Boombl4%20%7C%20Stockholm%202021",
                "Наклейка | Boombl4 | Стокгольм 2021",
                10,
                0.05f);
        lotForBuyArrayList.add(expectedLotForBuy);
        expectedLotForBuy = new LotForBuy(
                730,
                5084122167L,
                "Spectrum%202%20Case",
                "Кейс «Спектр 2»",
                1,
                1f);
        lotForBuyArrayList.add(expectedLotForBuy);
        LotForBuyList expectedLotForSellList = new LotForBuyList(
                lotForBuyArrayList,
                mainPageInstructionResult
                        .getBuyLotList()
                        .getTimeStamp());

        assertEquals(expectedLotForSellList, mainPageInstructionResult.getBuyLotList());
    }

    @Test
    public void testSuperMainPageInstruction_with_test_html2() throws IOException {
        mainPageInstruction.superMainPageExecute();
        mainPageInstruction
                .superMainPageStep(TestResponse.getResponse(
                        TestResponse.MAIN_PAGE_2));
        InstructionResult mainPageInstructionResult =
                mainPageInstruction.getInstructionResult();
        Wallet actualWallet = mainPageInstructionResult.getWallet();
        Wallet expectedWallet = new Wallet(
                360063,
                18,
                "UA",
                1,
                1,
                0.05f,
                0.10f,
                0,
                actualWallet.getTimestamp());
        assertEquals(expectedWallet, actualWallet);

        LotForSell lotForSell;
        HashSet<LotForSell> lotForSellArrayList = new HashSet<>();
        lotForSell = new LotForSell(
                3641730753877978092L,
                730,
                "Sticker%20%7C%20Boombl4%20%7C%20Stockholm%202021",
                "Наклейка | Boombl4 | Стокгольм 2021",
                new Date(1648760400000L),
                0.34f,
                0.3f,
                25318840441L,
                2);
        lotForSellArrayList.add(lotForSell);
        lotForSell = new LotForSell(
                3641730753880105972L,
                730,
                "AUG%20%7C%20Surveillance%20%28Field-Tested%29",
                "AUG | Слежка (После полевых испытаний)",
                new Date(1648760400000L),
                1.80f,
                1.58f,
                25317679716L,
                2);
        lotForSellArrayList.add(lotForSell);
        LotForSellList expectedLotForSellList = new LotForSellList(
                lotForSellArrayList,
                mainPageInstructionResult
                        .getSellLotList()
                        .getTimeStamp());

        assertEquals(expectedLotForSellList, mainPageInstructionResult.getSellLotList());


        LotForBuy lotForBuy;
        HashSet<LotForBuy> lotForBuyArrayList = new HashSet<>();
        lotForBuy = new LotForBuy(
                730,
                5080507730L,
                "Sticker%20%7C%20Boombl4%20%7C%20Stockholm%202021",
                "Наклейка | Boombl4 | Стокгольм 2021",
                10,
                0.05f);
        lotForBuyArrayList.add(lotForBuy);
        lotForBuy = new LotForBuy(
                730,
                5084122167L,
                "Spectrum%202%20Case",
                "Кейс «Спектр 2»",
                1,
                1f);
        lotForBuyArrayList.add(lotForBuy);
        LotForBuyList expectedLotForBuyList = new LotForBuyList(
                lotForBuyArrayList,
                mainPageInstructionResult
                        .getBuyLotList()
                        .getTimeStamp());

        assertEquals(expectedLotForBuyList, mainPageInstructionResult.getBuyLotList());
    }

    @Test
    public void testGetVisibleSellingItemsCount() throws IOException {
        mainPageInstruction.superMainPageExecute();
        mainPageInstruction
                .superMainPageStep(TestResponse.getResponse(
                        TestResponse.MAIN_PAGE_1));
        assertEquals(10, mainPageInstruction.getVisibleListingsCount());

        mainPageInstruction.superMainPageExecute();
        mainPageInstruction
                .superMainPageStep(TestResponse.getResponse(
                        TestResponse.MAIN_PAGE_2));
        assertEquals(2, mainPageInstruction.getVisibleListingsCount());

        mainPageInstruction.superMainPageExecute();
        mainPageInstruction
                .superMainPageStep(TestResponse.getResponse(
                        TestResponse.MAIN_PAGE_3));
        assertEquals(9, mainPageInstruction.getVisibleListingsCount());

        mainPageInstruction.superMainPageExecute();
        mainPageInstruction
                .superMainPageStep(TestResponse.getResponse(
                        TestResponse.MAIN_PAGE_4));
        assertEquals(100, mainPageInstruction.getVisibleListingsCount());
    }
}