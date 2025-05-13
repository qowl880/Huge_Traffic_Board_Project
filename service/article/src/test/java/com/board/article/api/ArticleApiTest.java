package com.board.article.api;

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

    }

    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
                .uri("/api/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }


}
