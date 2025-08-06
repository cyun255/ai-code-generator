package cn.rescld.aicodegeneratebackend.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.rescld.aicodegeneratebackend.exception.ErrorCode;
import cn.rescld.aicodegeneratebackend.exception.ThrowUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 代码保持器模板方法
 */
public abstract class CodeFileSaverTemplate<T> {

    protected final static String ROOT_DIR =
            System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "output";

    /**
     * 保存代码到本地文件的模板方法，不允许被修改
     *
     * @param result 代码结果对象
     * @return 保存的文件所在目录
     */
    public final File save(T result, Long appId) {
        // 校验输入
        validateInput(result);
        // 生成唯一目录路径
        String path = buildUniquePath(appId);
        // 保存文件
        saveFile(result, path);
        // 返回文件所在目录
        return new File(path);
    }

    /**
     * 校验输入的对象，允许子类重写
     *
     * @param result 代码结果对象
     */
    protected void validateInput(T result) {
        ThrowUtils.throwIf(result == null, ErrorCode.PARAMS_ERROR);
    }

    /**
     * 由子类重写，返回唯一的目录路径
     *
     * @return 唯一的目录路径
     */
    protected abstract String buildUniquePath(Long appId);

    /**
     * 由子类重写，将代码保存到本地文件中
     *
     * @param result 代码结果对象
     * @param path   保存的文件目录路径
     */
    protected abstract void saveFile(T result, String path);

    /**
     * 子类使用该方法保存文件，不允许重写
     *
     * @param content  代码
     * @param path     目录路径
     * @param fileName 文件名
     */
    protected final void writeToFile(String content, String path, String fileName) {
        String filePath = path + File.separator + fileName;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }
}
