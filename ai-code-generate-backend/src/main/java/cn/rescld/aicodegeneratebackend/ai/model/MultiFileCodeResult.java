package cn.rescld.aicodegeneratebackend.ai.model;

import lombok.Data;

@Data
public class MultiFileCodeResult {
    /**
     * AI 生成的 HTML 代码
     */
    private String htmlCode;

    /**
     * AI 生成的 CSS 代码
     */
    private String cssCode;

    /**
     * AI 生成的 JS 代码
     */
    private String jsCode;

    /**
     * AI 生成的文字说明
     */
    private String description;
}
