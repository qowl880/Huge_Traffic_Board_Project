package com.board.article.repository;

import com.board.article.domain.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article,Long> {

    @Query(
            value = "select article.article_id, article.title, article.content, article.board_id, article.writer_id, " +
                    "article.created_at, article.modified_at " +
                    "from (" +
                    "   select article_id from article " +
                    "   where board_id = :boardId " +
                    "   order by article_id desc " +
                    "   limit :limit offset :offset " +
                    ") t left join article on t.article_id = article.article_id ",
            nativeQuery = true          // JPQL이 아닌 실제 데이터베이스의 SQL 문법을 그대로 사용
    )
    List<Article> findAll(
            @Param("boardId") Long boardId,
            @Param("offset") Long offset,
            @Param("limit") Long limit
    );


    @Query(
            value = "select count(*) from (" +
                    "   select article_id from article where board_id = :boardId limit :limit" +
                    ") t",
            nativeQuery = true
    )
    Long count(@Param("boardId") Long boardId, @Param("limit") Long limit);


    // --------------------- 무한 스크롤
    // 맨 처음 게시물 값 출력
    @Query(
            value = "select article.article_id, article.title, article.content, article.board_id, article.writer_id, " +
                    "article.created_at, article.modified_at " +
                    "from article " +
                    "where board_id =:boardId " +
                    "order by article_id desc limit :limit ",
            nativeQuery = true
    )
    List<Article> findAllInfiniteScroll(@Param("boardId") Long boardId, @Param("limit") Long limit);


    // 2회차 offset 부터 이전 게시물의 마지막 Id 이후의 게시물 값 출력
    @Query(
            value = "select article.article_id, article.title, article.content, article.board_id, article.writer_id, " +
                    "article.created_at, article.modified_at " +
                    "from article " +
                    "where board_id =:boardId and article_id < :lastArticleId " +
                    "order by article_id desc limit :limit ",
            nativeQuery = true
    )
    List<Article> findAllInfiniteScroll(
            @Param("boardId") Long boardId,
            @Param("limit") Long limit,
            @Param("lastArticleId") Long lastArticleId
    );
}
