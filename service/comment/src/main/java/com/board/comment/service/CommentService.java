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
        if(parentCommentId == null){
            return null;
        }

        return commentRepository.findById(parentCommentId)
                .filter(not(Comment::isDeleted))           // delete 값이 존재하는 경우 , 해당 부분은 entity에서 deleted_at 값이 존재하는 것만 찾을 수 있음
                .filter(Comment::isRoot)                   // 자식 댓글 값이 부모 댓글과 같은지 확인, 같으면 부모 댓글임
                .orElseThrow();
    }

    public CommentResponse read(Long commentId){
        return CommentResponse.from(commentRepository.findById(commentId).orElseThrow());
    }

    @Transactional
    public void delete(Long commentId){
        commentRepository.findById(commentId)
                .filter(not(Comment::isDeleted))
                .ifPresent(comment -> {
                    if(hasChildren(comment)){
                        comment.delete();
                    }else{
                        delete(comment);
                    }
                });
    }

    public boolean hasChildren(Comment comment){
        return commentRepository.countBy(comment.getArticleId(), comment.getParentCommentId(), 2L) == 2;
    }

    public void  delete(Comment comment){
        commentRepository.delete(comment);
    }
}
