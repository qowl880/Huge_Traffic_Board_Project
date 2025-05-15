package com.board.comment.domain.dto.request;

public record CommentCreateRequest(
        Long articleId,
        String content,
        Long parentCommentId,
        Long writerId
) {
}
