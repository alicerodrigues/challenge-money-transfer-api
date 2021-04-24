package challengemoneytransferapi.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import challengemoneytransferapi.exception.DuplicateUserDataException;
import challengemoneytransferapi.exception.NotFoundException;
import challengemoneytransferapi.model.dto.UserDTO;
import challengemoneytransferapi.model.entity.User;
import challengemoneytransferapi.service.UserService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/users")
@Slf4j
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createUser(@Valid @RequestBody UserDTO userDTO) {
		log.info("Creating user {}", userDTO);
		try {
			User userSaved = userService.createUser(userDTO);
			return new ResponseEntity<>(userSaved, HttpStatus.CREATED);
		} catch (DuplicateUserDataException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(path = "/{id}")
	public ResponseEntity<Object> getUser(@PathVariable Long id) {
		log.info("Retrieving user for id {}", id);
		try {
			User user = userService.getUser(id);
			return ResponseEntity.ok(user);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping
	public ResponseEntity<Object> getAllUser() {
		log.info("Retrieving all users");
		Iterable<User> user = userService.getAllUsers();
		return ResponseEntity.ok(user);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		log.info("Delete user id {}", id);
		userService.deleteUser(id);
	}

}
