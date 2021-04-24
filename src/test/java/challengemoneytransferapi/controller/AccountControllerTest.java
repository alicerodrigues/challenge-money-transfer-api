package challengemoneytransferapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

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
import challengemoneytransferapi.model.dto.AccountDTO;
import challengemoneytransferapi.model.dto.UserDTO;
import challengemoneytransferapi.model.entity.Account;
import challengemoneytransferapi.model.entity.User;
import challengemoneytransferapi.service.AccountService;
import challengemoneytransferapi.service.UserService;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class AccountControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserService userService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	public void prepareMockMvc() {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
		userService.getUserRepository().deleteAll();
		accountService.getAccountRepository().deleteAll();
	}

	private ResultActions createAccountWithContent(final String content) throws Exception {
		return this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON).content(content));
	}

	@Test
	public void testCreateAccount() throws Exception {
		UserDTO userDTO = new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON);
		User user = userService.createUser(userDTO);
		createAccountWithContent("{\"userId\":" + user.getId() + ",\"balance\":500}").andExpect(status().isCreated());
		Account account = accountService.getAccountRepository().findAll().iterator().next();
		assertThat(account.getUserId()).isEqualTo(user.getId());
		assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal(500));

	}

	@Test
	public void testCreateAccountNegativeBalance() throws Exception {
		createAccountWithContent("{\"userId\":1,\"balance\":-1}").andExpect(status().isBadRequest());
	}

	private ResultActions makeTransferWithContent(String content) throws Exception {
		return this.mockMvc
				.perform(put("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON).content(content));
	}

	@Test
	public void testMakeTransferAccountFromNotFound() throws Exception {
		UserDTO userDTO1 = new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON);
		User user1 = userService.createUser(userDTO1);
		createAccountWithContent("{\"userId\":" + user1.getId() + ",\"balance\":200}").andExpect(status().isCreated());
		makeTransferWithContent("{\"accountFromId\":" + 9999 + ",\"accountToId\":" + user1.getId() + ",\"amount\":201}")
				.andExpect(status().isNotFound());
	}

	@Test
	public void testMakeTransferAccountToNotFound() throws Exception {
		UserDTO userDTO1 = new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON);
		User user1 = userService.createUser(userDTO1);
		createAccountWithContent("{\"userId\":" + user1.getId() + ",\"balance\":200}").andExpect(status().isCreated());
		makeTransferWithContent("{\"accountFromId\":" + user1.getId() + ",\"accountToId\":" + 9999 + ",\"amount\":201}")
				.andExpect(status().isNotFound());
	}

	@Test
	public void testMakeTransferSameAccount() throws Exception {
		UserDTO userDTO = new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON);
		User user = userService.createUser(userDTO);
		createAccountWithContent("{\"userId\":" + user.getId() + ",\"balance\":500}").andExpect(status().isCreated());
		makeTransferWithContent(
				"{\"accountFromId\":" + user.getId() + ",\"accountToId\":" + user.getId() + ",\"amount\":200}")
						.andExpect(status().isBadRequest());
	}

	@Test
	public void testMakeTransferFromLegalPerson() throws Exception {
		UserDTO userDTO1 = new UserDTO("Joe Co.", "john@john.com", "123456", "11111111111111", PersonType.LEGAL_PERSON);
		User user1 = userService.createUser(userDTO1);
		UserDTO userDTO2 = new UserDTO("Jane Doe", "jane@john.com", "123456", "22222222222", PersonType.NATURAL_PERSON);
		User user2 = userService.createUser(userDTO2);
		createAccountWithContent("{\"userId\":" + user1.getId() + ",\"balance\":10000}")
				.andExpect(status().isCreated());
		createAccountWithContent("{\"userId\":" + user2.getId() + ",\"balance\":1000}").andExpect(status().isCreated());
		makeTransferWithContent(
				"{\"accountFromId\":" + user1.getId() + ",\"accountToId\":" + user2.getId() + ",\"amount\":5000}")
						.andExpect(status().isBadRequest());
	}

	@Test
	public void testMakeTransferOverdraft() throws Exception {
		UserDTO userDTO1 = new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON);
		User user1 = userService.createUser(userDTO1);
		UserDTO userDTO2 = new UserDTO("Jane Doe", "jane@john.com", "123456", "22222222222", PersonType.NATURAL_PERSON);
		User user2 = userService.createUser(userDTO2);
		createAccountWithContent("{\"userId\":" + user1.getId() + ",\"balance\":200}").andExpect(status().isCreated());
		createAccountWithContent("{\"userId\":" + user2.getId() + ",\"balance\":1000}").andExpect(status().isCreated());
		makeTransferWithContent(
				"{\"accountFromId\":" + user1.getId() + ",\"accountToId\":" + user2.getId() + ",\"amount\":201}")
						.andExpect(status().isUnprocessableEntity());
	}

	@Test
	public void testMakeTransferNegativeAmount() throws Exception {
		UserDTO userDTO1 = new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON);
		User user1 = userService.createUser(userDTO1);
		UserDTO userDTO2 = new UserDTO("Jane Doe", "jane@john.com", "123456", "22222222222", PersonType.NATURAL_PERSON);
		User user2 = userService.createUser(userDTO2);
		createAccountWithContent("{\"userId\":" + user1.getId() + ",\"balance\":500}").andExpect(status().isCreated());
		createAccountWithContent("{\"userId\":" + user2.getId() + ",\"balance\":500}").andExpect(status().isCreated());
		makeTransferWithContent("{\"accountFromId\":1,\"accountToId\":2,\"amount\":-50}")
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testMakeTransfer() throws Exception {
		UserDTO userDTO1 = new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON);
		User user1 = userService.createUser(userDTO1);
		UserDTO userDTO2 = new UserDTO("Jane Doe", "jane@john.com", "123456", "22222222222", PersonType.NATURAL_PERSON);
		User user2 = userService.createUser(userDTO2);
		createAccountWithContent("{\"userId\":" + user1.getId() + ",\"balance\":500}").andExpect(status().isCreated());
		createAccountWithContent("{\"userId\":" + user2.getId() + ",\"balance\":500}").andExpect(status().isCreated());
		makeTransferWithContent(
				"{\"accountFromId\":" + user1.getId() + ",\"accountToId\":" + user2.getId() + ",\"amount\":201}")
						.andExpect(status().isOk());
	}

	@Test
	public void testGetAccount() throws Exception {
		UserDTO userDTO1 = new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON);
		User user1 = userService.createUser(userDTO1);
		AccountDTO account = new AccountDTO(user1.getId(), new BigDecimal(500));
		Account resp = accountService.createAccount(account);
		mockMvc.perform(get("/v1/accounts/{id}", resp.getId())).andExpect(status().isOk()).andExpect(
				content().string("{\"id\":" + resp.getId() + ",\"userId\":" + user1.getId() + ",\"balance\":500.00}"));
	}

	@Test
	public void testGetAccountNotFound() throws Exception {
		mockMvc.perform(get("/v1/accounts/{id}", 99)).andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteAccount() throws Exception {
		UserDTO userDTO1 = new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON);
		User user1 = userService.createUser(userDTO1);
		AccountDTO account = new AccountDTO(user1.getId(), new BigDecimal(500));
		Account resp = accountService.createAccount(account);
		String uri = "/v1/accounts/{id}";
		mockMvc.perform(MockMvcRequestBuilders.delete(uri, resp.getId()))
				.andExpect(MockMvcResultMatchers.status().is(200));
	}

}
