package cn.rescld.aicodegeneratebackend.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CodeGenTypeEnum {

    SINGLE_HTML("单 HTML 方式", "html"),
    MULTI_FILE("原生多文件方式", "multi_file");

    private final String text;
    private final String type;

    /**
     * 根据 type 得到枚举值
     *
     * @param type 枚举值的 type
     * @return 枚举值
     */
    public static CodeGenTypeEnum getEnumByType(String type) {
        for (CodeGenTypeEnum e : CodeGenTypeEnum.values()) {
            if (e.type.equals(type)) {
                return e;
            }
        }
        return null;
    }
}
