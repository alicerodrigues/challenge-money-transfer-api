package challengemoneytransferapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import challengemoneytransferapi.enums.PersonType;
import challengemoneytransferapi.exception.DuplicateUserDataException;
import challengemoneytransferapi.exception.NotFoundException;
import challengemoneytransferapi.model.dto.UserDTO;
import challengemoneytransferapi.model.entity.User;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

	@Autowired
	private UserService userService;

	@BeforeEach
	public void setUp() {
		userService.getUserRepository().deleteAll();
	}

	@Test
	public void testCreateUser() throws Exception {
		User user = userService.createUser(
				new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON));
		assertThat(user.getId()).isEqualTo(user.getId());
		assertThat(user.getName()).isEqualTo(user.getName());
		assertThat(user.getEmail()).isEqualTo(user.getEmail());
		assertThat(user.getPassword()).isEqualTo(user.getPassword());
		assertThat(user.getDocument()).isEqualTo(user.getDocument());
		assertThat(user.getPersonType()).isEqualTo(user.getPersonType());
	}

	@Test
	public void testCreateUserDuplicateEmail() {
		userService.createUser(
				new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON));
		assertThatThrownBy(() -> this.userService.createUser(
				new UserDTO("Jane Doe", "john@john.com", "123456", "22222222222", PersonType.NATURAL_PERSON)))
						.isInstanceOf(DuplicateUserDataException.class)
						.hasMessage("Email john@john.com already exists.");
	}

	@Test
	public void testCreateUserDuplicateDocument() {
		userService.createUser(
				new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON));
		assertThatThrownBy(() -> this.userService.createUser(
				new UserDTO("Jane Doe", "jane@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON)))
						.isInstanceOf(DuplicateUserDataException.class)
						.hasMessage("Document 11111111111 already exists.");
	}

	@Test
	public void testGetUserNotFound() throws Exception {
		assertThatThrownBy(() -> this.userService.getUser(1L)).isInstanceOf(NotFoundException.class)
				.hasMessage("User 1 not found.");
	}

}
