package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface QTodoRepository {
    Optional<Todo> dslFindByIdWithUser(Long todoId);

    Page<TodoSearchResponse> findAll(String title, String username, LocalDate start, LocalDate end, Pageable pageable);
}
