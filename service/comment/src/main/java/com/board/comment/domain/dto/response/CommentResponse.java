package com.board.comment.domain.dto.response;

import com.board.comment.domain.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
         Long commentId,
         String content,
         Long parentCommentId,
         Long articleId,
         Long writerId,
         boolean deleted,
         LocalDateTime createdAt
){

    public static CommentResponse from(Comment comment){
        CommentResponse response = new CommentResponse(
                comment.getCommentId(),
                comment.getContent(),
                comment.getParentCommentId(),
                comment.getArticleId(),
                comment.getWriterId(),
                comment.isDeleted(),
                comment.getCreatedAt()
        );

        return response;
    }
}
