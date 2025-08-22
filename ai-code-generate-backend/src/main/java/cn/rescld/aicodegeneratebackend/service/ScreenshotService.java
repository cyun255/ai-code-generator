package cn.rescld.aicodegeneratebackend.service;

public interface ScreenshotService {
    /**
     * 截图并上传
     * @param webUrl 要截图的网站链接
     * @return 截图访问的URL
     */
    String generateAndUploadScreenshot(String webUrl);
}
