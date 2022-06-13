package com.example.steammarketbot.core.testHTML;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;

public class TestResponse {


    public static String getResponse(String file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        return IOUtils.toString(fis, "UTF-8");
    }

    public static final String MAIN_PAGE_1 =
            "src/test/java/com/example/steammarketbot/core/testHTML/MarketMainPageHTML/MarketMainPageHTML1";
    //388 предметов на продаже (10 предметов на старницу) и 2 на покупке
    public static final String MAIN_PAGE_2 =
            "src/test/java/com/example/steammarketbot/core/testHTML/MarketMainPageHTML/MarketMainPageHTML2";
    //2 предмета на продаже (10 предметов на старницу) и 2 на покупке
    public static final String MAIN_PAGE_3 =
            "src/test/java/com/example/steammarketbot/core/testHTML/MarketMainPageHTML/MarketMainPageHTML3";
    //9 предмета на продаже (10 предметов на старницу) и 2 на покупке
    public static final String MAIN_PAGE_4 =
            "src/test/java/com/example/steammarketbot/core/testHTML/MarketMainPageHTML/MarketMainPageHTML4";
    //387 предмета на продаже (100 предметов на старницу) и 2 на покупке


    public static final String LOGIN_PAGE = "src/test/java/com/example/steammarketbot/core/testHTML/LoginHtml/LoginPage";
    public static final String RSA_JSON_RESPONSE_CASE_SUCCESS = "src/test/java/com/example/steammarketbot/core/testHTML/LoginHtml/RsaJsonResponse_CaseSuccess";
    public static final String DO_LOGIN_RESPONSE_CASE_REQUIRED_TWO_FACTOR_CODE = "src/test/java/com/example/steammarketbot/core/testHTML/LoginHtml/DoLoginJsonResponse_CaseSuccessFalse_CaseTwoFactorRequired";

    public static final String JSON_MY_LISTINGS_RESPONSE1 =
            "src/test/java/com/example/steammarketbot/core/testHTML/MarketMainPageHTML/JsonItemsForSellResponse/JsonItemsForSellResponse1";
    //Ответ по запросу на 2ую страницу из 4ех списка предметов на продажу (по 100 предметов)
    public static final String JSON_MY_LISTINGS_RESPONSE2 =
            "src/test/java/com/example/steammarketbot/core/testHTML/MarketMainPageHTML/JsonItemsForSellResponse/JsonItemsForSellResponse2";
    //Ответ по запросу на 3ую страницу из 4ех списка предметов на продажу (по 100 предметов)
    public static final String JSON_MY_LISTINGS_RESPONSE3 =
            "src/test/java/com/example/steammarketbot/core/testHTML/MarketMainPageHTML/JsonItemsForSellResponse/JsonItemsForSellResponse3";
    //Ответ по запросу на 4ую страницу из 4ех списка предметов на продажу (по 100 предметов)

    public static final String ITEM_PAGE1 = "src/test/java/com/example/steammarketbot/core/testHTML/itemPage/itemPage1";
    //1 предмет в списке на продажу и 1 в списке предложений. ManualBuy + OrderBuy
    public static final String JSON_HISTOGRAM1 = "src/test/java/com/example/steammarketbot/core/testHTML/itemPage/jsonHistogram/jsonHistogram1";
    //json к предыдущему html-лу
}