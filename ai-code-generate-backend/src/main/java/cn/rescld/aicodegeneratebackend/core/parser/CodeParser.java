package cn.rescld.aicodegeneratebackend.core.parser;

/**
 * 代码解析器，策略模式接口
 *
 * @param <T>
 */
public interface CodeParser<T> {
    /**
     * 根据泛型解析代码
     *
     * @param codeContent 原始文本
     * @return 解析后的对象
     */
    T parse(String codeContent);
}
