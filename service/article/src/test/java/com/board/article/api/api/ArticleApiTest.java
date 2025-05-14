package com.board.article.api.api;

import com.board.article.domain.dto.request.ArticleCreateRequest;
import com.board.article.domain.dto.request.ArticleUpdateRequest;
import com.board.article.domain.dto.response.ArticleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

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
}
