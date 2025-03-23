package com.litiron.code.lineage.sql.dto.deepseek;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李日红
 * @description: ai问答请求Dto
 * @create 2025/3/7 14:25
 */
@Getter
@Setter
public class DeepseekRequestDto {
    private String model = "deepseek-chat";
    private List<Message> messages = new ArrayList<>();
    private double temperature = 1.0;
    private int max_tokens = 4096;
    private Boolean stream = true;

    public static DeepseekRequestDto create(String content) {
        DeepseekRequestDto deepseekRequestDto = new DeepseekRequestDto();
        List<Message> messagesList = deepseekRequestDto.getMessages();
        messagesList.add(new Message("You are a helpful assistant", "system"));
        messagesList.add(new Message(content, "user"));
        return deepseekRequestDto;
    }

    static class Message {
        private String content;
        private String role;

        public Message(String content, String role) {
            this.content = content;
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
