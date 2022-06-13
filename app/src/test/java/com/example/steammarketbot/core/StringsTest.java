package com.example.steammarketbot.core;


import static com.example.steammarketbot.core.Strings.search;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class StringsTest {

    @Test
    public void testSearch() {
        String[] target = new String[] {
                "https://steamcommunity.com/market/listings/730/AUG%20%7C%20Surveillance%20%28Field-Tested%29",
                "https://steamcommunity.com/market/listings/730730/AUG%20%7C%20Surveillance%20%28Field-Tested%29",
                "https://steamcommunity.com/market/listings/730/AUG%20%7C%20Surveillance%20%28Field-Tested%29",
                "https://steamcommunity.com/market/listings/730/AUG%20%7C%20Surveillance%20%28Field-Tested%29",
                "https://steamcommunity.com/market/listings/730003/AUG%20%7C%20Surveillance%20%28Field-Tested%29"};

        String[] expect = new String[] {
                "AUG%20%7C%20Surveillance%20%28Field-Tested%29",
                "/730730/AUG%20%7C%20Surveillance%20%28Field-Tested%29",
                "/730/AUG%20%7C%20Surveillance%20%28Field-Tested%29",
                "AUG%20%7C%",
                "AUG%20%7C%"};
        String[] begin = new String[] {
                "/\\d+/",
                "/\\d+/",
                "/\\d+/",
                "/730/",
                "/\\d+/"
        };
        String[] end = new String[] {
                "$",
                "$",
                "$",
                "20Survei",
                "20Survei"
        };
        boolean[] include = new boolean[] {
                false,
                true,
                true,
                false,
                false};
        String[] actual = new String[expect.length];
            for (int i = 0; i < expect.length; i++) {
                actual[i] = search(target[i], begin[i], end[i], include[i]);
                System.out.println("i: " + i + ", actual: " + actual[i]);
            }
        assertArrayEquals(expect, actual);
    }
}