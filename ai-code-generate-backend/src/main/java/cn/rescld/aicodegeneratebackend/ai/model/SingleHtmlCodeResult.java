package cn.rescld.aicodegeneratebackend.ai.model;

import lombok.Data;

@Data
public class SingleHtmlCodeResult {
    /**
     * AI 生成的 HTML 代码
     */
    private String htmlCode;

    /**
     * AI 生成的文字说明
     */
    private String description;
}
