package com.example.todo.persistence;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.todo.model.TodoEntity;

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity,String>{
	
	@Query("select t from TodoEntity t where t.userId = ?1")
	List<TodoEntity>findByUserId(String userId);
	

	@Transactional
	void deleteAllByUserId(String userId);
	
	Page<TodoEntity> findByUserId(String userId, Pageable pageable);

}
