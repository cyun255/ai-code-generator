package cn.rescld.aicodegeneratebackend.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.rescld.aicodegeneratebackend.ai.model.MultiFileCodeResult;
import cn.rescld.aicodegeneratebackend.ai.model.SingleHtmlCodeResult;
import cn.rescld.aicodegeneratebackend.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 将 AI 生成的代码保存到本地
 */
@Deprecated
public class CodeFileSaver {

    private final static String ROOT_DIR =
            System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "output";

    /**
     * 保存单个 HTML 文件
     *
     * @param result 解析出来的 HTML 代码
     * @return 保存的文件目录
     */
    public static File saveSingleHtmlFile(SingleHtmlCodeResult result) {
        String path = buildUniquePath(CodeGenTypeEnum.SINGLE_HTML.getType());
        writeToFile(result.getHtmlCode(), path, "index.html");
        return new File(path);
    }

    /**
     * 保存原生多文件
     *
     * @param result 解析出来的多文件代码
     * @return 保存的文件目录
     */
    public static File saveMultiFile(MultiFileCodeResult result) {
        String path = buildUniquePath(CodeGenTypeEnum.MULTI_FILE.getType());
        writeToFile(result.getHtmlCode(), path, "index.html");
        writeToFile(result.getCssCode(), path, "style.css");
        writeToFile(result.getJsCode(), path, "script.js");
        return new File(path);
    }

    /**
     * 根据 AI 的生成代码方式，构造唯一的目录路径
     *
     * @param type AI 生成代码的方式
     * @return 唯一的目录路径
     */
    private static String buildUniquePath(String type) {
        String path = type + "_" + IdUtil.getSnowflakeNextIdStr();
        String fullPath = ROOT_DIR + File.separator + path;
        FileUtil.mkdir(fullPath);
        return fullPath;
    }

    /**
     * 将代码写入文件
     *
     * @param content  代码
     * @param path     目录路径
     * @param fileName 文件名
     */
    private static void writeToFile(String content, String path, String fileName) {
        String filePath = path + File.separator + fileName;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }
}
