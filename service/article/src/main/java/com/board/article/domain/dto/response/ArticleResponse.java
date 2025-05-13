package com.board.article.domain.dto.response;

import com.board.article.domain.entity.Article;

import java.time.LocalDateTime;

public record ArticleResponse (
         Long articleId,
         String title,
         String content,
         Long boardId, // shard ke,
         Long writerId,
         LocalDateTime createdAt,
         LocalDateTime modifiedAt
){

    public static ArticleResponse from(Article article){
        ArticleResponse response = new ArticleResponse(
                article.getArticleId(),
                article.getTitle(),
                article.getContent(),
                article.getBoardId(),
                article.getWriterId(),
                article.getCreatedAt(),
                article.getModifiedAt()
        );

        return response;
    }
}
