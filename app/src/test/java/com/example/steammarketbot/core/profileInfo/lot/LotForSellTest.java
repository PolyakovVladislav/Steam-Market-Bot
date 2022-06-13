package com.example.steammarketbot.core.profileInfo.lot;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class LotForSellTest {

    @Test
    public void parseDate() {
        String[] dates = new String[] {"1 апр", "31 дек", "1 апр 2021"};
        long[] expected = new long[] {1648760400000L, 1672437600000L, 1617224400000L};
        long[] actual = new long[dates.length];
        for (int i = 0; dates.length > i; i++) {
            actual[i] = LotForSell.parseDate(dates[i]).getTime();
        }
        assertArrayEquals(expected, actual);
    }
}