package cn.rescld.aicodegeneratebackend.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.rescld.aicodegeneratebackend.ai.model.SingleHtmlCodeResult;
import cn.rescld.aicodegeneratebackend.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * 单 HTML 文件保存器
 */
public class SingleHtmlCodeSaver extends CodeFileSaverTemplate<SingleHtmlCodeResult> {
    @Override
    protected String buildUniquePath(Long appId) {
        String path = CodeGenTypeEnum.SINGLE_HTML.getType() + "_" + appId.toString();
        String fullPath = ROOT_DIR + File.separator + path;
        FileUtil.mkdir(fullPath);
        return fullPath;
    }

    @Override
    protected void saveFile(SingleHtmlCodeResult result, String path) {
        writeToFile(result.getHtmlCode(), path, "index.html");
    }
}
