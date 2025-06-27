package org.example.expert.domain.todo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    private final EntityManager entityManager;

    @Override
    public Page<Todo> findAllWithWeatherAndModifiedAt(
            Pageable pageable, String weather, LocalDate startDate, LocalDate endDate
    ) {
        StringBuilder sql = new StringBuilder("SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(t) FROM Todo t LEFT JOIN t.user u WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if(weather != null) {
            sql.append(" AND t.weather LIKE :weather");
            countSql.append(" AND t.weather LIKE :weather");
            params.put("weather", "%" + weather + "%");
        }

        if(startDate != null) {
            sql.append(" AND t.modifiedAt >= :startDate");
            countSql.append(" AND t.modifiedAt >= :startDate");
            params.put("startDate", startDate.atStartOfDay());
        }

        if(endDate != null) {
            sql.append(" AND t.modifiedAt <= :endDate");
            countSql.append(" AND t.modifiedAt <= :endDate");
            params.put("endDate", endDate.atTime(23, 59, 59));
        }

        sql.append(" ORDER BY t.modifiedAt DESC");

        TypedQuery<Todo> query = entityManager.createQuery(sql.toString(), Todo.class);
        TypedQuery<Long> counts = entityManager.createQuery(countSql.toString(), Long.class);
        for (Map.Entry<String, Object> param : params.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
            counts.setParameter(param.getKey(), param.getValue());
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Todo> todos = query.getResultList();
        long count = counts.getSingleResult();

        return new PageImpl<>(todos, pageable, count);
    }
}
