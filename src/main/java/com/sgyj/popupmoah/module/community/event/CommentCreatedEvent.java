package com.sgyj.popupmoah.module.community.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentCreatedEvent {
    private final Long commentId;
    private final String author;
    private final String content;
} 