package com.board.comment.api;

import com.board.comment.domain.dto.response.CommentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class CommentApiTest {

    RestClient restClient = RestClient.create("http://localhost:9001");

    public  record CommentCreateRequest(
            Long articleId,
            String content,
            Long parentCommentId,
            Long writerId
    ) {
    }


    @Test
    void create() {
        CommentResponse response1 = createComment(new CommentCreateRequest(1L, "my comment1", null, 1L));
        CommentResponse response2 = createComment(new CommentCreateRequest(1L, "my comment2", response1.commentId(), 1L));
        CommentResponse response3 = createComment(new CommentCreateRequest(1L, "my comment3", response1.commentId(), 1L));

        System.out.println("commentId=%s".formatted(response1.commentId()));
        System.out.println("\tcommentId=%s".formatted(response2.commentId()));
        System.out.println("\tcommentId=%s".formatted(response3.commentId()));

//        commentId=123694721668214784
//          commentId=123694721986981888
//          commentId=123694722045702144
    }

    CommentResponse createComment(CommentCreateRequest request) {
        return restClient.post()
                .uri("/v1/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void read() {
        CommentResponse response = restClient.get()
                .uri("/v1/comments/{commentId}", 123694721668214784L)
                .retrieve()
                .body(CommentResponse.class);

        System.out.println("response = " + response);
    }

    @Test
    void delete() {
        //        commentId=123694721668214784 - x
        //          commentId=123694721986981888 - x
        //          commentId=123694722045702144 - x

        restClient.delete()
                .uri("/v1/comments/{commentId}", 123694722045702144L)
                .retrieve();
    }


}
