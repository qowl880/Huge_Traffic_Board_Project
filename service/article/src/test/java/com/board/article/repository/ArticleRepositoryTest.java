package com.board.article.repository;

import com.board.article.domain.entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.antlr.v4.runtime.tree.xpath.XPath.findAll;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ArticleRepositoryTest {

    @Autowired
    ArticleRepository articleRepository;

    @Test
    void findAllTest(){
        List<Article> articles = articleRepository.findAll(1L, 1499970L, 30L);
        log.info("articles.size = {}", articles.size());
        for(Article article : articles){
            log.info("article = {}", article);
        }
    }

    @Test
    void countTest(){
        Long count = articleRepository.count(1L, 10000L);
        log.info("count = {}", count);
    }

    @Test
    @DisplayName("무한 스크롤")
    void findInfiniteScrollTest(){
        List<Article> articles = articleRepository.findAllInfiniteScroll(1L, 30L);
        for(Article article : articles){
            log.info("articleId = {}", article.getArticleId());
        }

        Long lastArticleId = articles.getLast().getArticleId();
        List<Article> articles2 = articleRepository.findAllInfiniteScroll(1L,30L,lastArticleId);
        for(Article article : articles2){
            log.info("articleId = {}", article.getArticleId());
        }
    }
}