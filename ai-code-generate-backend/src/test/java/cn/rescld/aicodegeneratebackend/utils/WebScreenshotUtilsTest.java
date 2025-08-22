package cn.rescld.aicodegeneratebackend.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WebScreenshotUtilsTest {

    @Test
    void saveWebPageScreenshot() {
        String url = "https://www.baidu.com";
        String result = WebScreenshotUtils.saveWebPageScreenshot(url);
        Assertions.assertNotNull(result);
    }
}