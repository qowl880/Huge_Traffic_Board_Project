package com.board.article.service;

import com.board.article.domain.dto.request.ArticleCreateRequest;
import com.board.article.domain.dto.request.ArticleUpdateRequest;
import com.board.article.domain.dto.response.ArticlePageResponse;
import com.board.article.domain.dto.response.ArticleResponse;
import com.board.article.domain.entity.Article;
import com.board.article.repository.ArticleRepository;
import jakarta.transaction.Transactional;
import kuke.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final Snowflake snowflake = new  Snowflake();
    private final ArticleRepository articleRepository;

    @Transactional
    public ArticleResponse create(ArticleCreateRequest request){
        Article article = articleRepository.save(
                Article.create(snowflake.nextId(), request.title(), request.content(), request.boardId(), request.writerId())
        );
        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request){
        Article article = articleRepository.findById(articleId).orElseThrow();
        article.update(request.title(), request.content());
        return ArticleResponse.from(article);
    }

    public ArticleResponse read(Long articleId){
        return ArticleResponse.from(articleRepository.findById(articleId).orElseThrow());
    }

    @Transactional
    public void delete(Long articleId){
        articleRepository.deleteById(articleId);
    }

    public ArticlePageResponse readAll(Long boardId, Long page, Long pageSize){
        return ArticlePageResponse.of(
                // 페이징 처리
                articleRepository.findAll(boardId, (page -1) * pageSize, pageSize).stream()         // (page -1) * pageSize : Offset 구하는 공식
                        .map(ArticleResponse::from)
                        .toList(),
                // 전체 페이지 개수
                articleRepository.count(
                        boardId,
                        PageLimitCalculator.calculatePageLimit(page,pageSize,10L)
                )
        );
    }
}
