package com.nhnacademy.yes25.application.service.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public record DoorayMessagePayload(
    String botName,
    String botIconImage,
    String text,
    List<Attachment> attachments) {

    public static DoorayMessagePayload from(String authNumber) {
        return DoorayMessagePayload.builder()
            .botName("인증 서버 Bot")
            .botIconImage("https://www.tistory.com/favicon.ico")
            .text("인증번호: " + authNumber)
            .attachments(List.of(
                Attachment.builder()
                    .title("인증번호")
                    .color("green")
                    .text("인증번호: " + authNumber)
                    .build()
            ))
            .build();
    }

    @Builder
    public record Attachment(
        String title,
        String titleLink,
        String color,
        String text) {
    }
}
