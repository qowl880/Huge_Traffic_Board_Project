package com.board.comment.controller;

import com.board.comment.domain.dto.request.CommentCreateRequest;
import com.board.comment.domain.dto.response.CommentPageResponse;
import com.board.comment.domain.dto.response.CommentResponse;
import com.board.comment.domain.entity.Comment;
import com.board.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentResponse read(
            @PathVariable Long commentId
    ){
        return commentService.read(commentId);
    }

    @PostMapping()
    public CommentResponse create(@RequestBody CommentCreateRequest commentCreateRequest){
        return commentService.create(commentCreateRequest);
    }

    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable Long commentId){
        commentService.delete(commentId);
    }


    @GetMapping()
    public CommentPageResponse readAll(
            @RequestParam("articleId") Long articleId,
            @RequestParam("page")Long page,
            @RequestParam("pageSize")Long pageSize
    ){
        return commentService.readAll(articleId, page, pageSize);
    }

    @GetMapping("/infinite-scroll")
    public List<CommentResponse> readAll(
            @RequestParam("articleId") Long articleId,
            @RequestParam(value = "lastParentCommentId", required = false)Long lastParentCommentId,
            @RequestParam(value = "lastCommentId", required = false)Long lastCommentId,
            @RequestParam("pageSize")Long pageSize
    ){
        return commentService.readAll(articleId, lastParentCommentId, lastCommentId, pageSize);
    }
}
