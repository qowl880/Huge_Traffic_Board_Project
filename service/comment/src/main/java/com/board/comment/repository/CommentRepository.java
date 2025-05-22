package com.board.comment.repository;

import com.board.comment.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(
            value = "select count(*) from(" +
                    "   select comment_id from comment" +
                    "   where article_id = :articleId and parent_comment_id = :parentCommentId" +
                    "   limit : limit" +
                    ") t",
            nativeQuery = true
    )
    Long countBy(
            @Param("articleId") Long articleId,
            @Param("parentCommentId") Long parentCommentId,
            @Param("limit") Long limit
    );

    /* 서브쿼리에서는 댓글의 작성 시간이 다르기 때문에 DB에 저장되어 있는 순서가 섞여 있음 따라서 내가 찾고자 하는 게시물에 대한 값을 가져와야 하는데
       이때, select *를 사용하게 되면 만약 많은 댓글 데이터가 존재할때 댓글의 모든 값을 찾아와야 하기에 속도나 I/O와 메모리 사용량이 증가됨
       따라서 서브쿼리에서는 comment_id 처럼 인덱스 값만 추출하고 이를 외부에서 left join을 통해 해당 인덱스의 comment 데이터를 찾아옴
     */
    @Query(
            value = "select comment.comment_id, comment.content, comment.parent_comment_id, comment.article_id, " +
                    "comment.writer_id, comment.deleted, comment.created_at " +
                    "from (" +
                    "   select comment_id from comment where article_id = :articleId " +
                    "   order by parent_comment_id asc, comment_id asc " +
                    "   limit :limit offset :offset " +
                    ") t left join comment on t.comment_id = comment.comment_id",
            nativeQuery = true
    )
    List<Comment> findAll(
            @Param("articleId") Long articleId,
            @Param("offset") Long offset,
            @Param("limit") Long limit
    );


    @Query(
            value = "select count(*) from (" +
                    "   select comment_id from comment where article_id = :articleId limit :limit" +
                    ") t",
            nativeQuery = true
    )
    Long count(
            @Param("articleId") Long articleId,
            @Param("limit") Long limit
    );

    // 스크롤 첫 페이지 데이터 출력
    @Query(
            value = "select comment.comment_id, comment.content, comment.parent_comment_id, comment.article_id, " +
                    "comment.writer_id, comment.deleted, comment.created_at " +
                    "from comment " +
                    "where article_id = :articleId " +
                    "order by parent_comment_id asc, comment_id asc " +
                    "limit :limit",
            nativeQuery = true
    )
    List<Comment> findAllInfiniteScroll(
            @Param("articleId") Long articleId,
            @Param("limit") Long limit
    );

    // 스크롤 n 페이지 데이터 출력
    // 무한 스크롤일때는 출력되는 화면에 대댓글 중 일부가 안보이는 경우가 발생함 따라서,
    //                     "   parent_comment_id > :lastParentCommentId or " +
    //                    "   (parent_comment_id = :lastParentCommentId and comment_id > :lastCommentId) " +
    // 해당 코드에서 parent_comment_id값이 출력되는 댓글의 마지막 값인 lastParentCommentId보다 같으면서 commentId값이 lastCommentId보다 큰 경우를 찾아 대댓글까지 출력시킴
    @Query(
            value = "select comment.comment_id, comment.content, comment.parent_comment_id, comment.article_id, " +
                    "comment.writer_id, comment.deleted, comment.created_at " +
                    "from comment " +
                    "where article_id = :articleId and (" +
                    "   parent_comment_id > :lastParentCommentId or " +
                    "   (parent_comment_id = :lastParentCommentId and comment_id > :lastCommentId) " +
                    ")" +
                    "order by parent_comment_id asc, comment_id asc " +
                    "limit :limit",
            nativeQuery = true
    )
    List<Comment> findAllInfiniteScroll(
            @Param("articleId") Long articleId,
            @Param("lastParentCommentId") Long lastParentCommentId,
            @Param("lastCommentId") Long lastCommentId,
            @Param("limit") Long limit
    );
}
