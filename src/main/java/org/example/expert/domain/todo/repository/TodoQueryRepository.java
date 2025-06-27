package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TodoQueryRepository {

    Page<Todo> findAllWithWeatherAndModifiedAt(Pageable pageable, String weather, LocalDate startDate, LocalDate endDate);
}
