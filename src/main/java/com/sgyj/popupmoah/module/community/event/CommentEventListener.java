package com.sgyj.popupmoah.module.community.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class CommentEventListener {

    @Async
    @EventListener
    public void handleCommentCreated(CommentCreatedEvent event) {
        // 실제로는 이메일, 푸시, 통계 등 외부 연동
        System.out.println("[이벤트] 댓글 생성됨: id=" + event.getCommentId() + ", 작성자=" + event.getAuthor());
    }
} 