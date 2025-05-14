package com.board.article.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

    class PageLimitCalculatorTest {

        @Test
        @DisplayName("페이지 마지막 위치 확인")
        void calculatePageLimitTest(){
            /*
            	page=1: 1페이지 요청
               	pageSize=30: 한 페이지당 30개 게시글
               	movablePageCount=10: 페이지네이션 UI에 10개 페이지 번호 표시
               	expected=301: 예상 결과값 301
             */
            calculatePageLimitTest(1L, 30L, 10L, 301L);
            calculatePageLimitTest(7L, 30L, 10L, 301L);
            calculatePageLimitTest(10L, 30L, 10L, 301L);
            calculatePageLimitTest(11L, 30L, 10L, 601L);
            calculatePageLimitTest(12L, 30L, 10L, 601L);

        }

        void calculatePageLimitTest(Long page, Long pageSize, Long movablePageCount, Long expected){
            Long result = PageLimitCalculator.calculatePageLimit(page, pageSize, movablePageCount);
            assertEquals(expected, result);
        }
    }
