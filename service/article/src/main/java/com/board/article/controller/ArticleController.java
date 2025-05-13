package com.board.article.controller;

import com.board.article.domain.dto.request.ArticleCreateRequest;
import com.board.article.domain.dto.request.ArticleUpdateRequest;
import com.board.article.domain.dto.response.ArticleResponse;
import com.board.article.domain.entity.Article;
import com.board.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/{articleId}")
    public ArticleResponse read(@PathVariable Long articleId){
        return articleService.read(articleId);
    }

    @PostMapping()
    public ArticleResponse create(@RequestBody ArticleCreateRequest request){
        return articleService.create(request);
    }

    @PutMapping("/{articleId}")
    public ArticleResponse update(@PathVariable Long articleId,@RequestBody ArticleUpdateRequest request){
        return articleService.update(articleId,request);
    }

    @DeleteMapping("/{articleId}")
    public void delete(@PathVariable Long articleId){
        articleService.delete(articleId);
    }
}
