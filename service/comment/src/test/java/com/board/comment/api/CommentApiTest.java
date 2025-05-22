package com.board.comment.api;

import com.board.comment.domain.dto.response.CommentPageResponse;
import com.board.comment.domain.dto.response.CommentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

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

    @Test
    void readAll() {
        CommentPageResponse response = restClient.get()
                .uri("/api/comments?articleId=1&page=1&pageSize=10")
                .retrieve()
                .body(CommentPageResponse.class);

        System.out.println("response.getCommentCount() =" +response.commentCount());
        for(CommentResponse commentResponse : response.comments()){
            if(commentResponse.commentId().equals(((commentResponse.parentCommentId())))){
                System.out.println("\t");
            }
            System.out.println("comment.getCommentId() =" +commentResponse.commentId());
        }
    }

    /*
        첫번째 페이지
        comment.getCommentId() =183213510629171200
        comment.getCommentId() =183213510683697156
        comment.getCommentId() =183213510629171201
        comment.getCommentId() =183213510683697155
        comment.getCommentId() =183213510633365504
        comment.getCommentId() =183213510683697157
        comment.getCommentId() =183213510633365505
        comment.getCommentId() =183213510683697153
        comment.getCommentId() =183213510633365506
        comment.getCommentId() =183213510704668783
     */

    @Test
    void readAllInfiniteScroll(){
        List<CommentResponse> responses1 =  restClient.get()
                .uri("/api/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("firstPage");
        for(CommentResponse commentResponse : responses1){
            if(!commentResponse.commentId().equals(((commentResponse.parentCommentId())))){
                System.out.println("\t");
            }
            System.out.println("comment.getCommentId() =" +commentResponse.commentId());
        }

        Long lastParentCommentId = responses1.getLast().parentCommentId();
        Long lastCommentId = responses1.getLast().commentId();

        List<CommentResponse> responses2 =  restClient.get()
                .uri("/api/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s"
                        .formatted(lastParentCommentId, lastCommentId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("SecondPage");
        for(CommentResponse commentResponse : responses2){
            if(!commentResponse.commentId().equals(((commentResponse.parentCommentId())))){
                System.out.println("\t");
            }
            System.out.println("comment.getCommentId() =" +commentResponse.commentId());
        }

    }
}
