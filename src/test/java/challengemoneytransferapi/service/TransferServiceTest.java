package challengemoneytransferapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import challengemoneytransferapi.enums.PersonType;
import challengemoneytransferapi.exception.NotEnoughFundsException;
import challengemoneytransferapi.exception.TransferBetweenSameAccountException;
import challengemoneytransferapi.exception.TransferFromLegalPersonException;
import challengemoneytransferapi.model.dto.TransferDTO;
import challengemoneytransferapi.model.dto.UserDTO;
import challengemoneytransferapi.model.entity.Account;
import challengemoneytransferapi.model.entity.User;
import challengemoneytransferapi.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
public class TransferServiceTest {

	@Autowired
	private TransferService transferService;

	@Autowired
	UserRepository userRepository;

	@Test
	public void testTransferFromLegalPerson() {
		User userFrom = userRepository.save(
				new UserDTO("John Co.", "john@co.com", "123456", "11111111111111", PersonType.LEGAL_PERSON).build());
		Account accountFrom = new Account(2L, userFrom.getId(), new BigDecimal(200));
		Account accountTo = new Account(1L, 1L, new BigDecimal(500));
		TransferDTO transferDTO = new TransferDTO(2L, accountTo.getId(), new BigDecimal(200));
		try {
			transferService.validate(accountFrom, accountTo, transferDTO);
		} catch (TransferFromLegalPersonException e) {
			assertThat(e.getMessage()).isEqualTo("Transfer from account 2 not permitted.");
		}
	}

	@Test
	public void testTransferNotEnoughFunds() {
		User userFrom = userRepository.save(
				new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON).build());
		Account accountFrom = new Account(2L, userFrom.getId(), new BigDecimal(100));
		Account accountTo = new Account(1L, 1L, new BigDecimal(500));
		TransferDTO transferDTO = new TransferDTO(2L, accountTo.getId(), new BigDecimal(200));
		try {
			transferService.validate(accountFrom, accountTo, transferDTO);
		} catch (NotEnoughFundsException e) {
			assertThat(e.getMessage()).isEqualTo("Not enough funds on account 2 balance=100");
		}
	}

	@Test
	public void testTransferBetweenSameAccount() {
		User userFrom = userRepository.save(
				new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON).build());
		Account account = new Account(1L, userFrom.getId(), new BigDecimal(100));
		TransferDTO transferDTO = new TransferDTO(account.getId(), account.getId(), new BigDecimal(10));
		try {
			transferService.validate(account, account, transferDTO);
		} catch (TransferBetweenSameAccountException e) {
			assertThat(e.getMessage()).isEqualTo("Transfer to self not permitted.");
		}
	}

	@Test
	public void testTransferBetweenAccounts() throws Exception {
		User userFrom = userRepository.save(
				new UserDTO("John Doe", "john@john.com", "123456", "11111111111", PersonType.NATURAL_PERSON).build());
		Account accountFrom = new Account(1L, userFrom.getId(), new BigDecimal(100));
		Account accountTo = new Account(2L, 2L, new BigDecimal(100));
		TransferDTO transferDTO = new TransferDTO(accountFrom.getId(), accountTo.getId(), new BigDecimal(10));
		transferService.validate(accountFrom, accountTo, transferDTO);
	}

}
