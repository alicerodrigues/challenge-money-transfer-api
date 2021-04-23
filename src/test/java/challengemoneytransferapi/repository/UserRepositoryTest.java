package challengemoneytransferapi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import challengemoneytransferapi.enums.PersonType;
import challengemoneytransferapi.model.dto.UserDTO;
import challengemoneytransferapi.model.entity.User;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTest {

	@Autowired
	UserRepository userRepository;

	@AfterEach
	public final void tearDown() {
		this.userRepository.deleteAll();
	}

	@Test
	public void testReturnEmpty() {
		Iterable<User> requests = userRepository.findAll();
		assertThat(requests).isEmpty();
	}

	@Test
	public void testInsert() {
		User request = userRepository.save(
				new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON).build());
		assertThat(request).isEqualTo(request);
	}

	@Test
	public void testGetAll() {
		User user1 = userRepository.save(
				new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON).build());
		User user2 = userRepository.save(
				new UserDTO("Jane Doe", "jane@john.com", "123456", "22222222222", PersonType.NATURAL_PERSON).build());

		Iterable<User> requests = userRepository.findAll();
		assertThat(requests).hasSize(2).contains(user1, user2);
	}

	@Test
	public void testGetById() {
		User user = userRepository.save(
				new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON).build());
		User foundRequest = userRepository.findById(user.getId()).get();
		assertThat(foundRequest).isEqualTo(user);
	}

	@Test
	public void testGetByEmail() {
		User user = userRepository.save(
				new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON).build());
		User foundRequest = userRepository.findByEmail("john@john.com");
		assertThat(foundRequest).isEqualTo(user);
	}

	@Test
	public void testGetByDocument() {
		User user = userRepository.save(
				new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON).build());
		User foundRequest = userRepository.findByDocument("11111111111");
		assertThat(foundRequest).isEqualTo(user);
	}

	@Test
	public void testDelete() {
		User user1 = userRepository.save(
				new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON).build());
		User user2 = userRepository.save(
				new UserDTO("Jane Doe", "jane@john.com", "123456", "22222222222", PersonType.NATURAL_PERSON).build());
		userRepository.deleteById(user2.getId());
		Iterable<User> requests = userRepository.findAll();
		assertThat(requests).hasSize(1).contains(user1);
	}

}
