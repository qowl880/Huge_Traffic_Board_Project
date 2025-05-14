package com.board.article.domain.dto.response;

import com.board.article.domain.entity.Article;
import lombok.NoArgsConstructor;

import java.util.List;

public record ArticlePageResponse(
        List<ArticleResponse> articles,
        Long articleCount
) {

    public static ArticlePageResponse of(List<ArticleResponse> articles, Long articleCount) {
        ArticlePageResponse response = new ArticlePageResponse(articles,articleCount);
        return response;
    }
}
