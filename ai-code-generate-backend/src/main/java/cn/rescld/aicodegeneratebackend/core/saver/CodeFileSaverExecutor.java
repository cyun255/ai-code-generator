package cn.rescld.aicodegeneratebackend.core.saver;

import cn.rescld.aicodegeneratebackend.ai.model.MultiFileCodeResult;
import cn.rescld.aicodegeneratebackend.ai.model.SingleHtmlCodeResult;
import cn.rescld.aicodegeneratebackend.exception.BusinessException;
import cn.rescld.aicodegeneratebackend.exception.ErrorCode;
import cn.rescld.aicodegeneratebackend.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * 代码保存执行器
 */
public class CodeFileSaverExecutor {

    private static final SingleHtmlCodeSaver singleHtmlCodeSaver = new SingleHtmlCodeSaver();
    private static final MultiCodeSaver multiCodeSaver = new MultiCodeSaver();

    public static File execute(Object result, CodeGenTypeEnum codeGenType, Long appId) {
        return switch (codeGenType) {
            case SINGLE_HTML -> singleHtmlCodeSaver.save((SingleHtmlCodeResult) result, appId);
            case MULTI_FILE -> multiCodeSaver.save((MultiFileCodeResult) result, appId);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持该类型方案保存方式");
        };
    }
}
