package challengemoneytransferapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import challengemoneytransferapi.enums.PersonType;
import challengemoneytransferapi.exception.NotFoundException;
import challengemoneytransferapi.model.dto.AccountDTO;
import challengemoneytransferapi.model.dto.TransferDTO;
import challengemoneytransferapi.model.dto.UserDTO;
import challengemoneytransferapi.model.entity.Account;
import challengemoneytransferapi.model.entity.User;

@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

	@Autowired
	private AccountService accountService;
	@Autowired
	private UserService userService;

	@BeforeEach
	public void setUp() {
		accountService.getAccountRepository().deleteAll();
		userService.getUserRepository().deleteAll();
	}

	@Test
	public void testCreateAccount() throws Exception {
		User user = userService.createUser(
				new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON));
		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setUserId(user.getId());
		accountDTO.setBalance(new BigDecimal(1));
		Account account = accountService.createAccount(accountDTO);
		assertThat(accountService.getAccount(account.getId()).getUserId()).isEqualTo(user.getId());
		assertThat(accountService.getAccount(account.getId()).getBalance()).isEqualByComparingTo(account.getBalance());
	}

	@Test
	public void testCreateAccountWithoutUser() {
		AccountDTO accountDTO = new AccountDTO(1L, new BigDecimal(500));
		try {
			accountService.createAccount(accountDTO);
		} catch (NotFoundException e) {
			assertThat(e.getMessage()).isEqualTo("User 1 not found.");
		}
	}

	@Test
	public void testGetAccountNotFound() throws Exception {
		try {
			accountService.getAccount(1L);
		} catch (NotFoundException e) {
			assertThat(e.getMessage()).isEqualTo("Account 1 not found.");
		}
	}

	@Test
	public void testTransferAccountFromNotFound() {
		User user = userService.createUser(
				new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON));
		Account accounTo = accountService.createAccount(new AccountDTO(user.getId(), new BigDecimal(100)));
		TransferDTO transferDTO = new TransferDTO(1L, accounTo.getId(), new BigDecimal(200));
		try {
			accountService.makeTransfer(transferDTO);
		} catch (NotFoundException e) {
			assertThat(e.getMessage()).isEqualTo("Account 1 not found.");
		}
	}

	@Test
	public void testTransferAccountToNotFound() {
		User user = userService.createUser(
				new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON));
		Account accounFrom = accountService.createAccount(new AccountDTO(user.getId(), new BigDecimal(100)));
		TransferDTO transferDTO = new TransferDTO(accounFrom.getId(), 2L, new BigDecimal(200));
		try {
			accountService.makeTransfer(transferDTO);
		} catch (NotFoundException e) {
			assertThat(e.getMessage()).isEqualTo("Account 2 not found.");
		}
	}

}
