package challengemoneytransferapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import challengemoneytransferapi.enums.PersonType;
import challengemoneytransferapi.model.dto.UserDTO;
import challengemoneytransferapi.model.entity.User;
import challengemoneytransferapi.service.UserService;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private UserService userService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	public void prepareMockMvc() {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
		userService.getUserRepository().deleteAll();
	}

	private ResultActions createUserWithContent(final String content) throws Exception {
		return this.mockMvc.perform(post("/v1/users").contentType(MediaType.APPLICATION_JSON).content(content));
	}

	@Test
	public void testcreateAccount() throws Exception {
		createUserWithContent(
				"{\"name\":\"John Doe\",\"email\":\"john@john.com\",\"password\":\"123456\",\"document\":\"11111111111\",\"personType\":\"NATURAL_PERSON\"}")
						.andExpect(status().isCreated());
		User user = userService.getAllUsers().iterator().next();
		assertThat(user.getName()).isEqualTo("John Doe");
		assertThat(user.getEmail()).isEqualTo("john@john.com");
		assertThat(user.getPassword()).isEqualTo("123456");
		assertThat(user.getDocument()).isEqualTo("11111111111");
		assertThat(user.getPersonType()).isEqualTo(PersonType.NATURAL_PERSON);
	}

	@Test
	public void testCreateDuplicateUserEmail() throws Exception {
		createUserWithContent(
				"{\"name\":\"John Doe\",\"email\":\"john@john.com\",\"password\":\"123456\",\"document\":\"11111111111\",\"personType\":\"NATURAL_PERSON\"}")
						.andExpect(status().isCreated());
		createUserWithContent(
				"{\"name\":\"John Doe\",\"email\":\"john@john.com\",\"password\":\"123456\",\"document\":\"22222222222\",\"personType\":\"NATURAL_PERSON\"}")
						.andExpect(status().isBadRequest());
	}

	@Test
	public void testCreateDuplicateUserDocument() throws Exception {
		createUserWithContent(
				"{\"name\":\"John Doe\",\"email\":\"john@john.com\",\"password\":\"123456\",\"document\":\"11111111111\",\"personType\":\"NATURAL_PERSON\"}")
						.andExpect(status().isCreated());
		createUserWithContent(
				"{\"name\":\"John Doe\",\"email\":\"john2@john.com\",\"password\":\"123456\",\"document\":\"11111111111\",\"personType\":\"NATURAL_PERSON\"}")
						.andExpect(status().isBadRequest());
	}

	@Test
	public void testGetUser() throws Exception {
		UserDTO userDTO = new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON);
		User user = userService.createUser(userDTO);
		mockMvc.perform(get("/v1/users/{id}", user.getId())).andExpect(status().isOk())
				.andExpect(content().string("{\"id\":" + user.getId() + ",\"name\":\"" + userDTO.getName()
						+ "\",\"email\":\"" + userDTO.getEmail() + "\",\"password\":\"" + userDTO.getPassword()
						+ "\",\"document\":\"" + userDTO.getDocument() + "\",\"personType\":\""
						+ userDTO.getPersonType() + "\"}"));
	}

	@Test
	public void testGetUserNotFound() throws Exception {
		mockMvc.perform(get("/v1/users/{id}", 99)).andExpect(status().isNotFound());
	}

	@Test
	public void testGetAllUsers() throws Exception {
		UserDTO user1 = new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON);
		UserDTO user2 = new UserDTO("Doe Co.", "doeco@john.com", "123456", "11111111111111", PersonType.LEGAL_PERSON);
		User resp1 = userService.createUser(user1);
		User resp2 = userService.createUser(user2);

		mockMvc.perform(get("/v1/users")).andExpect(status().isOk())
				.andExpect(content().string("[{\"id\":" + resp1.getId() + ",\"name\":\"" + user1.getName()
						+ "\",\"email\":\"" + user1.getEmail() + "\",\"password\":\"" + user1.getPassword()
						+ "\",\"document\":\"" + user1.getDocument() + "\",\"personType\":\"" + user1.getPersonType()
						+ "\"}," + "{\"id\":" + resp2.getId() + ",\"name\":\"" + user2.getName() + "\",\"email\":\""
						+ user2.getEmail() + "\",\"password\":\"" + user2.getPassword() + "\",\"document\":\""
						+ user2.getDocument() + "\",\"personType\":\"" + user2.getPersonType() + "\"}]"));
	}

	@Test
	public void testDeleteUser() throws Exception {
		UserDTO user = new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON);
		User resp = userService.createUser(user);
		String uri = "/v1/users/{id}";
		mockMvc.perform(MockMvcRequestBuilders.delete(uri, resp.getId()))
				.andExpect(MockMvcResultMatchers.status().is(200));
	}

}
