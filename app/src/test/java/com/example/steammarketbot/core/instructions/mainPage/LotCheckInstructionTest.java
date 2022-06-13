package com.example.steammarketbot.core.instructions.mainPage;

import com.example.steammarketbot.core.profileInfo.lot.LotForSell;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class LotCheckInstructionTest {

    private static final String RESULTS_HTML = "results_html";
    private static final String HOVERS = "hovers";
    private static LotCheckInstruction lotCheckInstruction;

    @Before
    public void setUp() {
        lotCheckInstruction = new LotCheckInstruction(
                0,
                0,
                10000,
                "Description",
                0);
    }

    @Test
    public void testParseJsonResponse() throws NoSuchMethodException, IOException, JSONException, InvocationTargetException, IllegalAccessException {


        LotForSell expectedLotForSell = new LotForSell(
                3641730753887412892L,
                730,
                "Sticker | Boombl4 | Stockholm 2021",
                "Наклейка | Boombl4 | Стокгольм 2021",
                new Date(1648846800000L),
                0.3f,
                0.34f,
                25331696611L,
                2);
    }

}