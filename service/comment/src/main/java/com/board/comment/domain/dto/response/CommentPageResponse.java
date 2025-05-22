package com.board.comment.domain.dto.response;


import java.util.List;

public record CommentPageResponse(
        List<CommentResponse> comments,
        Long commentCount
) {

    public static CommentPageResponse of(List<CommentResponse> comments, Long commentCount) {
        CommentPageResponse response = new CommentPageResponse(comments, commentCount);
        return response;
    }
}
