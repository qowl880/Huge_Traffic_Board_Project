package com.board.article.api.api;

import com.board.article.domain.dto.request.ArticleCreateRequest;
import com.board.article.domain.dto.request.ArticleUpdateRequest;
import com.board.article.domain.dto.response.ArticlePageResponse;
import com.board.article.domain.dto.response.ArticleResponse;
import com.board.article.domain.entity.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");     // api 요청 가능 테스트

    ArticleCreateRequest articleCreateRequest;
    ArticleUpdateRequest articleUpdateRequest;

    @BeforeEach
    void setUp(){
        articleCreateRequest = new ArticleCreateRequest("title","content",1L,1L);
        articleUpdateRequest = new ArticleUpdateRequest("updateTitle","updateContent");
    }


    @Test
    void createTest(){
        ArticleResponse response = create(articleCreateRequest);
        System.out.println("Create Post response: "+response);
    }

    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
                .uri("/api/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }


    @Test
    void readTest() {
        ArticleResponse response = read(180931240610676736L);
        System.out.println("response = " + response);
    }

    ArticleResponse read(Long articleId) {
        return restClient.get()
                .uri("/api/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void updateTest() {
        update(180931240610676736L);
        ArticleResponse response = read(180931240610676736L);
        System.out.println("response = " + response);
    }

    void update(Long articleId) {
        restClient.put()
                .uri("/api/v1/articles/{articleId}", articleId)
                .body(articleUpdateRequest)
                .retrieve();
    }

    @Test
    void deleteTest() {
        restClient.delete()
                .uri("/api/v1/articles/{articleId}", 121530268440289280L)
                .retrieve();
    }

    @Test
    @DisplayName("게시물 페이징 - Covering Index")
    void readAllTest(){
        ArticlePageResponse response = restClient.get()
                .uri("/api/v1/articles?boardId=1&pageSize=30&page=50000")
                .retrieve()
                .body(ArticlePageResponse.class);

        System.out.println("Read All Post ResponseCount() =  "+response.articleCount());
        for(ArticleResponse article : response.articles()){
            System.out.println("articleId = "+article.articleId());
        }
    }

    @Test
    @DisplayName("무한 스크롤 페이징")
    void readAllInfiniteScrollTest(){
        List<ArticleResponse> articles = restClient.get()
                .uri("/api/v1/articles/infinite-scroll?boardId=1&pageSize=5")
                .retrieve()
                // ParameterizedTypeReference : 제네릭 타입으로 객체를 반환해주기 위해 사용
                .body(new ParameterizedTypeReference<List<ArticleResponse>>(){
                });

        System.out.println("first Page");
        for(ArticleResponse article : articles){
            System.out.println("articleResponse.getArticleId() =  "+article.articleId());
        }

        Long lastArticleId = articles.getLast().articleId();
        List<ArticleResponse> article2  = restClient.get()
                .uri("/api/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=%s".formatted(lastArticleId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>(){});

        System.out.println("second Page");
        for(ArticleResponse article : article2){
            System.out.println("articleResponse.getArticleId() =  "+article.articleId());
        }
    }
}
