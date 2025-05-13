package com.board.article.domain.dto.request;

public record ArticleUpdateRequest(
        String title,
        String content
){
}
