package com.litiron.code.lineage.sql.dto.deepseek;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @description: ai问答响应Dto
 * @author: 李日红
 * @create: 2025/3/7 14:27
 */
//public record DeepseekResponseDto(
//        String id,
//        String object,
//        long created,
//        String model,
//        List<Choice> choices
//) {
//    public record Choice(
//            int index,
//            Message message,
//            String finish_reason
//    ) {
//        public record Message(String role, String content) {
//        }
//    }
//}
@Getter
@Setter
public class DeepseekResponseDto {
    String id;
    String object;
    long created;
    String model;
    List<Choice> choices;


    public static class Choice {
        int index;
        Message message;
        Delta delta;
        String finish_reason;

        public Choice(int index, Message message, Delta delta, String finish_reason) {
            this.index = index;
            this.message = message;
            this.delta = delta;
            this.finish_reason = finish_reason;
        }

        public Delta getDelta() {
            return delta;
        }

        public void setDelta(Delta delta) {
            this.delta = delta;
        }

        public Choice(int index, Message message, String finish_reason) {
            this.index = index;
            this.message = message;
            this.finish_reason = finish_reason;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public String getFinish_reason() {
            return finish_reason;
        }

        public void setFinish_reason(String finish_reason) {
            this.finish_reason = finish_reason;
        }
    }

    public static class Delta {
        String role;
        String content;

        public Delta(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
    public static class Message {
        String role;
        String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
