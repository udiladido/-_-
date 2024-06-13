package com.example.todo.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.todo.dto.ResponseDTO;
import com.example.todo.dto.TodoDTO;
import com.example.todo.model.TodoEntity;
import com.example.todo.service.TodoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor

public class TodoController {

	private final TodoService service;

	@PostMapping
	public ResponseEntity <?>createTodo(@AuthenticationPrincipal String userId,@RequestBody TodoDTO dto){
		try {
			/* POST localhost:8080/todo
		{
		"title" : "My first todo",
		"done" : false

			 */
			log.info("Log:createTodo entrance");
		// dto 를 이용해 테이붙에 저장하기 위한 entity를 생성한다.
			TodoEntity entity = TodoDTO.toEntity(dto);
			log.info("Log:dto => entity ok!");
			
			
			//entity userId를 임시로 지정한다.
			entity.setId(null);
			entity.setUserId(userId);
			
			
			//entity.setUserId("temporary-user");
						
			// service.create 를 등해 repository 에 entity를 저장한다.
			// 이때 넘어노는 값이 없을 수도 있으므로 List가 아닌 Optional 로 한다.
			//Optional<TodoEntity> entities = service.create(entity);
			List <TodoEntity> entities = service.create(entity);
			
			
			log.info("Log:serivce.create ok!");

			// entities 를 dtos 로 스트림 변환한다.
			List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
			log.info("Log:entities => dtos ok!");
			// Response DTO를 생성한다.
			/* 
			 * 
			 * "error" : null,
			"data": [
			{
				
				"id" :	"402809817ed71ddf017ed71dfe720000",
				"title" : "My first todo",
				"done" : false

				}
			}

			 * 
			 * */
			
			ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
			log.info("Log:responsedto ok!");

			// HTTP Status 200 상태로 response 를 전송한다.
			return ResponseEntity.ok().body(response);
			
				}catch (Exception e){
					String error = e.getMessage();
					ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
					return ResponseEntity.badRequest().body(response);
				}
			}						
	
	
	/*
	@GetMapping
	public ResponseEntity<?>retrieveTodoList(){
		String temporaryUserId = "temporary-user";
		List<TodoEntity> entities = service.retrieve(temporaryUserId);
		List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
		
		ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
		
		// HTTP Status 200 상태로 response 를 전송한다.
		return ResponseEntity.ok().body(response);
		
	}
			
	*/
	@GetMapping
	public ResponseEntity<?>retrieveTodo(@AuthenticationPrincipal String userId){
		
		List<TodoEntity> entities = service.retrieve(userId);
		List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
		
		ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
		
		// HTTP Status 200 상태로 response 를 전송한다.
		return ResponseEntity.ok().body(response);
		
	}
	
	
	/*
	@GetMapping("/update")
	public ResponseEntity<?>update(@RequestBody TodoDTO dto){
		try{
		
			/* POST localhost:8080/todo/update

			*

			"id" : " ???????? "
			"title" : "Update first todo",
			"done" : true

			}//

			// dto 를 이용해 테이블에 저장하기 위한 entity를 생성한다.
			TodoEntity entity = TodoDTO.toEntity(dto);

			// entity userId를 임시로 지정한다.
			entity.setUserId("temporary-user");

			// service.create 를 통해 repository 에 entity를 저장한다.
			// 이때 넘어노는 값이 없을 수도 있으므로 List가 아닌 Optional 로 한다.
			Optional<TodoEntity> entities = service.update(entity);
			
			// entities 를 dtos 로 스트림 변환한다.
			List<TodoDTO> dtos = entities.stream().map(TodoDTO :: new).collect(Collectors.toList());

			
			// Response DTO를 생성한다.
			ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

			// HTTP Status 200 상태로 response 를 진송한다.
			return ResponseEntity.ok().body(response);
			}catch (Exception e){
				String error = e.getMessage();
				ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
			
				return ResponseEntity.badRequest().body(response);
			}
		}
	*/
	@PutMapping
	public ResponseEntity<?> updateTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto){
		try{
			/*
			 * 
			 * 
			 * 
			 *}
			 */
			
			// dto 를 이용해 테이블에 저장하기 위한 entity를 생성한다
			TodoEntity entity = TodoDTO.toEntity(dto);

			// entity userId를 임시로 지정한다.
			entity.setUserId(userId);

			// service.create 를 통해 repository 에 entity를 저장한다.
			// 이때 넘어노는 값이 없을 수도 있으므로 List가 아닌 Optional 로 한다.
			List<TodoEntity> entities = service.update(entity);
			
			// entities 를 dtos 로 스트림 변환한다.
			List<TodoDTO> dtos = entities.stream().map(TodoDTO :: new).collect(Collectors.toList());

			
			// Response DTO를 생성한다.
			ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

			// HTTP Status 200 상태로 response 를 진송한다.
			return ResponseEntity.ok().body(response);
			}catch (Exception e){
				String error = e.getMessage();
				ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
			
				return ResponseEntity.badRequest().body(response);
			}
	
	}
			
	
	/*
	 	@DeleteMapping
	public ResponseEntity <?> delete(@RequestBody TodoDTO dto){
	try {

	List<String> message =new ArrayList<>(); // import java.util.ArrayList 추
	String msg = service.delete(dto.getId());
	message.add(msg);
	// Response DTO를 생성한다.
	ResponseDTO<String> response = ResponseDTO.<String>builder().data(message).build();
	return ResponseEntity.ok().body(response);
	}catch (Exception e){
	String error = e.getMessage();
	ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
	return ResponseEntity.badRequest().body(response);
	
		}
	}
	 * 
	 * */

 	@DeleteMapping
	public ResponseEntity <?> deleteTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto){
	try {

		TodoEntity entity = TodoDTO.toEntity(dto);
		
		// entity userId를 임시로 지정한다.
		entity.setUserId(userId);
		
		List<TodoEntity> entities = service.delete(entity);
		
		// entities 를 dtos 로 스트림 변환한다.
		List<TodoDTO> dtos = entities.stream().map(TodoDTO :: new).collect(Collectors.toList());
		
		
	/*
	List<String> message =new ArrayList<>(); // import java.util.ArrayList 추
	String msg = service.delete(dto.getId());
	message.add(msg);	

	  */	
	
	// Response DTO를 생성한다.
	ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
	
	//HTTP Status 200 상태로 response 를 전송한다.
	
	return ResponseEntity.ok().body(response);
	}catch (Exception e){
	String error = e.getMessage();
	ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
	return ResponseEntity.badRequest().body(response);
	
		}
	}
 	
 	 @DeleteMapping("/all")
     public ResponseEntity<?> deleteAllTodos(@AuthenticationPrincipal String userId) {
         try {
             System.out.println("DELETE /todo/all 요청 받음"); // 로그 추가
             service.deleteAllByUserId(userId);
             System.out.println("전체 삭제 처리 완료"); // 로그 추가

             // JSON 형식의 응답 반환
             Map<String, String> response = new HashMap<>();
             response.put("message", "All todos deleted successfully");
             return ResponseEntity.ok().body(response);
         } catch (Exception e) {
             e.printStackTrace(); // 에러 로그 추가
             String error = e.getMessage();
             ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
             return ResponseEntity.badRequest().body(response);
         }
     }
 
 	
 	@GetMapping("/page")
    public ResponseEntity<?> getPagedTodos(@AuthenticationPrincipal String userId, @PageableDefault(page = 0) Pageable pageable) {
        try {
            Page<TodoDTO> todoPage = service.paging(pageable, userId);

            // blockLimit : page 개수 설정
            int blockLimit = 3;
            int currentPage = pageable.getPageNumber() + 1; // Pageable의 page는 0부터 시작하므로 1을 더해줍니다.
            int startPage = (((int) Math.ceil(((double) currentPage / blockLimit))) - 1) * blockLimit + 1;
            int endPage = Math.min((startPage + blockLimit - 1), todoPage.getTotalPages());

            Map<String, Object> response = new HashMap<>();
            response.put("todoPage", todoPage);
            response.put("startPage", startPage);
            response.put("endPage", endPage);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
 	
 	
}
