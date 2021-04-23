package challengemoneytransferapi.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import challengemoneytransferapi.enums.PersonType;
import challengemoneytransferapi.exception.NotEnoughFundsException;
import challengemoneytransferapi.exception.TransferBetweenSameAccountException;
import challengemoneytransferapi.exception.TransferFromLegalPersonException;
import challengemoneytransferapi.model.dto.TransferDTO;
import challengemoneytransferapi.model.entity.Account;
import challengemoneytransferapi.model.entity.User;
import challengemoneytransferapi.repository.UserRepository;

@Service
public class TransferService {
	private final UserRepository userRepository;

	@Autowired
	public TransferService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void validate(Account currAccountFrom, Account currAccountTo, TransferDTO transferDTO)
			throws TransferFromLegalPersonException, NotEnoughFundsException, TransferBetweenSameAccountException {

		if (transferFromLegalPerson(currAccountFrom)) {
			throw new TransferFromLegalPersonException(
					"Transfer from account " + currAccountFrom.getId() + " not permitted.");
		}
		if (sameAccount(transferDTO)) {
			throw new TransferBetweenSameAccountException("Transfer to self not permitted.");
		}

		if (!enoughFunds(currAccountFrom, transferDTO.getAmount())) {
			throw new NotEnoughFundsException("Not enough funds on account " + currAccountFrom.getId() + " balance="
					+ currAccountFrom.getBalance());
		}

	}

	private boolean sameAccount(TransferDTO transferDTO) {
		return transferDTO.getAccountFromId().equals(transferDTO.getAccountToId());
	}

	private boolean enoughFunds(Account account, BigDecimal amount) {
		return account.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) >= 0;
	}

	private boolean transferFromLegalPerson(Account account) {
		User user = userRepository.findById(account.getUserId()).get();
		return user.getPersonType().equals(PersonType.LEGAL_PERSON);
	}

}
