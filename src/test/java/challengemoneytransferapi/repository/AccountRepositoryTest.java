package challengemoneytransferapi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import challengemoneytransferapi.model.dto.AccountDTO;
import challengemoneytransferapi.model.entity.Account;

@SpringBootTest
@ActiveProfiles("test")
public class AccountRepositoryTest {

	@Autowired
	private AccountRepository accountRepository;

	@AfterEach
	public final void tearDown() {
		this.accountRepository.deleteAll();
	}

	@Test
	public void testInsert() {
		Account request = accountRepository.save(new AccountDTO(1L, new BigDecimal(500)).build());
		assertThat(request).hasFieldOrPropertyWithValue("id", request.getId());
		assertThat(request).hasFieldOrPropertyWithValue("userId", 1L);
		assertThat(request).hasFieldOrPropertyWithValue("balance", new BigDecimal(500));
	}

	@Test
	public void testGetAllEmpty() {
		Iterable<Account> requests = accountRepository.findAll();
		assertThat(requests).hasSize(0);
	}

	@Test
	public void testGetAll() {
		Account account1 = accountRepository
				.save(new AccountDTO(1L, new BigDecimal(500).setScale(2, RoundingMode.DOWN)).build());
		Account account2 = accountRepository
				.save(new AccountDTO(2L, new BigDecimal(500).setScale(2, RoundingMode.DOWN)).build());

		Iterable<Account> requests = accountRepository.findAll();
		assertThat(requests).hasSize(2).contains(account1, account2);
	}

	@Test
	public void testGetById() {
		Account account = accountRepository
				.save(new AccountDTO(1L, new BigDecimal(500).setScale(2, RoundingMode.DOWN)).build());
		Account foundRequest = accountRepository.findById(account.getId()).get();
		assertThat(foundRequest).isEqualTo(account);
	}

	@Test
	public void updateBalance() {
		Account account1 = accountRepository
				.save(new AccountDTO(1L, new BigDecimal(500).setScale(2, RoundingMode.DOWN)).build());
		Account account2 = accountRepository
				.save(new AccountDTO(2L, new BigDecimal(500).setScale(2, RoundingMode.DOWN)).build());
		account1.setBalance(account1.getBalance().subtract(new BigDecimal(200)));
		account2.setBalance(account2.getBalance().add(new BigDecimal(200)));
		Account account1Updated = accountRepository.save(account1);
		Account account2Updated = accountRepository.save(account2);
		assertThat(account1Updated.getBalance()).isEqualByComparingTo(new BigDecimal(300));
		assertThat(account2Updated.getBalance()).isEqualByComparingTo(new BigDecimal(700));

	}

	@Test
	public void testDelete() {
		Account account1 = accountRepository
				.save(new AccountDTO(1L, new BigDecimal(500).setScale(2, RoundingMode.DOWN)).build());
		Account account2 = accountRepository
				.save(new AccountDTO(2L, new BigDecimal(500).setScale(2, RoundingMode.DOWN)).build());
		accountRepository.deleteById(account2.getId());

		Iterable<Account> requests = accountRepository.findAll();
		assertThat(requests).hasSize(1).contains(account1);
	}

}
