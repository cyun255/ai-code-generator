package cn.rescld.aicodegeneratebackend.ai.tools;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ToolManager {
    private final Map<String, BaseTool> toolMap = new HashMap<>();

    @Resource
    private BaseTool[] tools;

    @PostConstruct
    public void initTools() {
        for (BaseTool tool : tools) {
            toolMap.put(tool.getToolName(), tool);
            log.info("注册工具: {} -> {}", tool.getToolName(), tool.getDisplayName());
        }
        log.info("成功注册 {} 个工具", toolMap.size());
    }

    /**
     * 根据根据名称获取工具
     *
     * @param toolName 工具名称
     * @return 工具实例
     */
    public BaseTool getTool(String toolName) {
        return toolMap.get(toolName);
    }

    /**
     * 获取所有工具
     *
     * @return 工具实例数组
     */
    public BaseTool[] getAllTools() {
        return tools;
    }
}
