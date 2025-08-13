package cn.rescld.aicodegeneratebackend.core.parser;

import cn.rescld.aicodegeneratebackend.exception.BusinessException;
import cn.rescld.aicodegeneratebackend.exception.ErrorCode;
import cn.rescld.aicodegeneratebackend.model.enums.CodeGenTypeEnum;

/**
 * 代码解析器执行器
 */
public class CodeParserExecutor {

    private static final SingleHtmlCodeParser singleHtmlCodeParser = new SingleHtmlCodeParser();
    private static final MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();

    /**
     * 根据代码生成类型，做对应的解析操作
     *
     * @param codeContent     代码内容
     * @param codeGenTypeEnum 代码生成类型
     * @return 解析后结果对象
     */
    public static Object execute(String codeContent, CodeGenTypeEnum codeGenTypeEnum) {
        return switch (codeGenTypeEnum) {
            case SINGLE_HTML -> singleHtmlCodeParser.parse(codeContent);
            case MULTI_FILE -> multiFileCodeParser.parse(codeContent);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持该代码生产方式");
        };
    }
}
