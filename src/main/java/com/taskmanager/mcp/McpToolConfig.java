// [C practice — Spring AI MCP server]
package com.taskmanager.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the @Tool methods on TaskTools with the MCP server. The starter's
 * auto-configuration picks up this ToolCallbackProvider and advertises the tools over SSE.
 */
@Configuration
public class McpToolConfig {

    @Bean
    ToolCallbackProvider taskToolProvider(TaskTools taskTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(taskTools)
                .build();
    }
}
