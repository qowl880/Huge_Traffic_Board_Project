package com.board.article.api.data;

import com.board.article.domain.entity.Article;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kuke.board.common.snowflake.Snowflake;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
    12000만 건 데이터 삽입을 위한 초기화 등록 Test
    10개의 스레드 풀 사용하여 병렬 속도 높힘
 */
@SpringBootTest
public class DataInitializer {
    @PersistenceContext
    EntityManager em;

    @Autowired
    TransactionTemplate transactionTemplate;
    Snowflake snowflake = new Snowflake();
    CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);   // 여러 스레드의 작업이 모두 끝날 때까지 대기할 수 있게 해주는 동기화 도구

    static final int BULK_INSERT_SIZE = 2000;       //   insert 작업에서 생성할 Article 엔티티의 개수
    static final int EXECUTE_COUNT = 6000;          //   insert 작업을 몇 번 반복할지

    @Test
    void initialize() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for(int i = 0; i < EXECUTE_COUNT; i++) {
            executorService.submit(() -> {      // insert 작업을 스레드 풀에 제출
                insert();                       // 아래에 정의된 insert() 메서드를 호출하여 Article 엔티티를 대량으로 DB에 저장
                latch.countDown();              // 이 작업이 끝났음을 알리기 위해 latch의 카운트를 1 감소
                System.out.println("latch.getCount() = " + latch.getCount());       // 남은 작업 개수를 출력
            });
        }
        latch.await();                  // 	latch 카운트가 0이 될 때까지(즉, 모든 insert 작업이 끝날 때까지) 현재 스레드를 대기
        executorService.shutdown();     // 모든 작업 제출이 끝난 후 스레드 풀을 정상적으로 종료
    }

    void insert() {
        transactionTemplate.executeWithoutResult(status -> {        // 트랜잭션을 시작, 트랜잭션 내에서 람다로 정의된 작업을 실행
            for(int i = 0; i < BULK_INSERT_SIZE; i++) {
                Article article = Article.create(
                        snowflake.nextId(),
                        "title" + i,
                        "content" + i,
                        1L,
                        1L
                );
                em.persist(article);
            }
        });
    }
}
