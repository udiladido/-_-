package com.example.todo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.todo.dto.TodoDTO;
import com.example.todo.model.TodoEntity;
import com.example.todo.persistence.TodoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

		private final TodoRepository repository;

		
		//public Optional<TodoEntity> create(final TodoEntity entity){
		//return repository.findById(entity.getId());
		public List<TodoEntity> create(final TodoEntity entity){
		
		//Validations
		validate(entity);
		repository.save(entity);
		return repository.findByUserId(entity.getId());
	
	}
	
	public List<TodoEntity>retrieve(final String userId){
			return repository.findByUserId(userId);
	}

	
	//public Optional<TodoEntity> update(final TodoEntity entity){
	public List<TodoEntity> update(final TodoEntity entity){
		//validations
		validate(entity);
		
		if(repository.existsById(entity.getId())){
			repository.save(entity);
		}
		else
			throw new RuntimeException("Unknown id");
		return repository.findByUserId(entity.getId());
		
		
	}
	
	
	/*
	 * 
	 * 
	 
	public Optional<TodoEntity> updateTodo(final TodoEntity entity){
		//validations
		validate(entity);
		
		//테이블에서 id에 해당하는 데이터셋을 가져온다.
		final Optional<TodoEntity> original = repository.findById(entity.getId());
		
		//original 에 담겨진 내용을 todo에 할당하고 title, done 값을 변경한다.
		original.ifPresent(todo->{
			todo.setTitle(entity.getTitle());
			
			todo.setDone(entity.isDone());
			repository.save(todo);

			});
			/* 위의 람다식과 동일한 표현
			* if(original.isPresent()) {
			* final TodoEntity todo = original.get();
			todo.setTitle(entity.getTitle());
			* todo.setDone(entity.isDone);
			repository.save(todo);
			/
		
			return repository.findById(entity.getId());

	}
	*/
	
	/*
	public String delete(final String id){
		if(repository.existsById(id))
			repository.deleteById(id);
		else
			throw new RuntimeException("id does not exist");

		return "Deleted";
	} 
	*/
	
	public List<TodoEntity> delete(final TodoEntity entity){
		if(repository.existsById(entity.getId()))
			repository.deleteById(entity.getId());
		else
			throw new RuntimeException("id does not exist");

		return repository.findByUserId(entity.getUserId());
	} 
	
	
	public void validate(final TodoEntity entity){
		if(entity == null ){
			log.warn("Entity cannot be null.");
			throw new RuntimeException("Entity cannot be null.");
		}
		if(entity.getUserId() == null){
			log.warn("Unknown user.");
			throw new RuntimeException("Unknown user.");
		}
	}
	

  
    public void deleteAllByUserId(String userId) {
        System.out.println("서비스에서 전체 삭제 로직 실행"); // 로그 추가
        repository.deleteAllByUserId(userId);
    }
    

    
	public Page<TodoDTO> paging(Pageable pageable, String userId) {
        int page = pageable.getPageNumber(); // page 위치에 있는 값은 0부터 시작한다.
        int pageLimit = 3; // 한페이지에 보여줄 글 개수
 
        // 한 페이지당 3개식 글을 보여주고 정렬 기준은 ID기준으로 내림차순
        Page<TodoEntity> postsPages = repository.findAll(PageRequest.of(page, pageLimit, Sort.by(Direction.DESC, "id")));
 
        // 목록 : id, title, content, author
        Page<TodoDTO> postsResponseDtos = postsPages.map(
                postPage -> new TodoDTO(postPage));
 
        return postsResponseDtos;
    }
	

}