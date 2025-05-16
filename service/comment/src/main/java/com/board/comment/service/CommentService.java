package com.board.comment.service;

import com.board.comment.domain.dto.request.CommentCreateRequest;
import com.board.comment.domain.dto.response.CommentResponse;
import com.board.comment.domain.entity.Comment;
import com.board.comment.repository.CommentRepository;
import jakarta.transaction.Transactional;
import kuke.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.util.function.Predicate.not;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final Snowflake snowflake = new Snowflake();

    @Transactional
    public CommentResponse create(CommentCreateRequest request){
        // Comment(부모 객체) 값이 반환되었다면 대댓글, NUll 반환되었다면 부모 객체
        Comment parent = findParent(request);
        commentRepository.save(
                Comment.create(
                        snowflake.nextId(),
                        request.content(),
                        parent == null ? null : parent.getCommentId(),
                        request.articleId(),
                        request.writerId()
                )
        );
        return CommentResponse.from(parent);
    }

    // 부모 댓글이 있는지 없는지 확인
    private Comment findParent(CommentCreateRequest request){
        Long parentCommentId = request.parentCommentId();
        // 부모 댓글이 null이면 null 반환 즉, parentCommentId값이 없다는 것은 새로운 comment이므로 부모 댓글임
        if(parentCommentId == null){
            return null;
        }

        // 부모 댓글이 존재한다면 해당 request는 대댓글이므로 해당 부모 객체의 값을 불러옴
        return commentRepository.findById(parentCommentId)
                .filter(not(Comment::isDeleted))           // 만약 해당 부모 객체의 isDeleted값이 true이면 not이기에 false 가 되어 오류 반환 즉, 삭제되지 않은 데이터만 진행
                .filter(Comment::isRoot)                   // 해당 객체가 부모 객체인지 확인
                .orElseThrow();
    }

    public CommentResponse read(Long commentId){
        return CommentResponse.from(commentRepository.findById(commentId).orElseThrow());
    }

    @Transactional
    public void delete(Long commentId){
        // Comment 정보 호출
        commentRepository.findById(commentId)
                // 만약 해당 Comment가 삭제되지 않고
                .filter(not(Comment::isDeleted))
                // 존재한다면 해당 comment 객체를 가지고 진행
                .ifPresent(comment -> {
                    // 만약 대댓글이 존재하는지 확인한 후
                    if(hasChildren(comment)){
                        // 대댓글이 존재한다면 현재 댓글의 삭제 컬럼에 true 반환
                        comment.delete();
                        // 대댓글이 없다면 완전 삭제
                    }else{
                        delete(comment);
                    }
                });
    }

    // 자식 댓글이 있는지만 확인하면 되는 것이므로 해당 카운트가 2개까지 카운트 하고 2개이면 대댓글이 있다고 판단 (2개 이상부터는 대댓글이 있는 것이기에 2개까지 구해도 됨)
    private boolean hasChildren(Comment comment) {
        return commentRepository.countBy(comment.getArticleId(), comment.getCommentId(), 2L) == 2;
    }

    // 재귀적으로 부모객체가 삭제되어 있는 상태에서 대댓글을 삭제한다면 부모댓글과 대댓글을 모두 완전 삭제 시킴
    private void delete(Comment comment) {
        // 현재 Comment를 완전 삭제함
        commentRepository.delete(comment);
        // 만약 comment가 부모 댓글이 아니라 대댓글이라면
        if (!comment.isRoot()) {
            // 대댓글의 부모 댓글 Id값을 통해 부모 Comment 객체를 가져와
            commentRepository.findById(comment.getParentCommentId())
                    // 해당 객체의 삭제 상태가 true (삭제됨) 이고
                    .filter(Comment::isDeleted)
                    // 해당 객체의 대댓글이 없다면
                    .filter(not(this::hasChildren))
                    // 해당 객체 완전 삭제함
                    .ifPresent(this::delete);
        }
    }
}
