package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class QTodoRepositoryImpl implements QTodoRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> dslFindByIdWithUser(Long todoId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(todo)
                        .join(todo.user, user)
                        .fetchJoin()
                        .where(todo.id.eq(todoId))
                        .fetchOne()
        );
    }

    //디비에서 한번에 가져오는법
    @Override
    public Page<TodoSearchResponse> findAll(String title, String username, LocalDate start, LocalDate end, Pageable pageable) {
        List<TodoSearchResponse> fetch = queryFactory
                .select(new QTodoSearchResponse(
                        todo.id, todo.title,
                        manager.id.countDistinct(),
                        comment.id.countDistinct(),
                        todo.createdAt
                ))
                .from(todo)
                .leftJoin(todo.comments, comment)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .where(
                        titleLike(title),
                        usernameLike(username),
                        createdAtGoe(start),
                        createdAtLoe(end)
                )
                .groupBy(todo.id)
                .offset(pageable.getOffset()).limit(pageable.getPageSize())
                .orderBy(todo.createdAt.desc())
                .fetch();

        Long counts = queryFactory
                .select(todo.id.countDistinct())
                .from(todo)
                .leftJoin(todo.comments, comment)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .where(
                        titleLike(title),
                        usernameLike(username),
                        createdAtGoe(start),
                        createdAtLoe(end)
                )
                .fetchOne();
        return new PageImpl<>(fetch, pageable, counts);
    }

    private BooleanExpression titleLike(String title){
        return StringUtils.hasText(title) ? todo.title.like("%"+title+"%") : null;
    }
    private BooleanExpression usernameLike(String username){
        return StringUtils.hasText(username) ? user.username.like("%"+username+"%") : null;
    }
    private BooleanExpression createdAtGoe(LocalDate start){
        return start != null ? todo.createdAt.goe(start.atStartOfDay()) : null;
    }
    private BooleanExpression createdAtLoe(LocalDate end){
        return end != null ? todo.createdAt.loe(end.atTime(23,59,59)) : null;
    }
    //가져와서 계산하는 법
}
