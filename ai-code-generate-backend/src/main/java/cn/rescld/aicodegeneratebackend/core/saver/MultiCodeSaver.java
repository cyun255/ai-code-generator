package cn.rescld.aicodegeneratebackend.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.rescld.aicodegeneratebackend.ai.model.MultiFileCodeResult;
import cn.rescld.aicodegeneratebackend.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * 原生多文件（HTML、CSS、JS）代码保存器
 */
public class MultiCodeSaver extends CodeFileSaverTemplate<MultiFileCodeResult> {
    @Override
    protected String buildUniquePath(Long appId) {
        String path = CodeGenTypeEnum.MULTI_FILE.getType() + "_" + appId.toString();
        String fullPath = ROOT_DIR + File.separator + path;
        FileUtil.mkdir(fullPath);
        return fullPath;
    }

    @Override
    protected void saveFile(MultiFileCodeResult result, String path) {
        writeToFile(result.getHtmlCode(), path, "index.html");
        writeToFile(result.getCssCode(), path, "style.css");
        writeToFile(result.getJsCode(), path, "script.js");
    }
}
