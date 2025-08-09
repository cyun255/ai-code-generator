package cn.rescld.aicodegeneratebackend.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageTypeEnum {

    AI("AI", "ai"),
    USER("用户", "user");

    private final String text;
    private final String type;

    /**
     * 根据 type 得到枚举值
     *
     * @param type 枚举值的 type
     * @return 枚举值
     */
    public static MessageTypeEnum getEnumByType(String type) {
        for (MessageTypeEnum e : MessageTypeEnum.values()) {
            if (e.type.equals(type)) {
                return e;
            }
        }
        return null;
    }
}
