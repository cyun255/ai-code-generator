package cn.rescld.aicodegeneratebackend.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IsDeleteEnum {

    NORMAL("正常", 0),
    DELETED("已被删除", 1);

    private final String text;
    private final Integer value;
}
