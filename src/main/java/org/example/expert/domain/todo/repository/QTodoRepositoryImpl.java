package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class QTodoRepositoryImpl implements QTodoRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> dlsFindByIdWithUser(Long todoId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(todo)
                        .join(todo.user, user)
                        .fetchJoin()
                        .where(todo.id.eq(todoId))
                        .fetchOne()
        );
    }
}
