package challengemoneytransferapi.service;

import java.math.BigDecimal;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import challengemoneytransferapi.exception.NotFoundException;
import challengemoneytransferapi.exception.UnauthorizedException;
import challengemoneytransferapi.model.dto.AccountDTO;
import challengemoneytransferapi.model.dto.Client;
import challengemoneytransferapi.model.dto.TransferDTO;
import challengemoneytransferapi.model.entity.Account;
import challengemoneytransferapi.model.entity.User;
import challengemoneytransferapi.repository.AccountRepository;
import challengemoneytransferapi.repository.UserRepository;
import reactor.core.publisher.Mono;

@Service
public class AccountService {

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final TransferService transferService;
	private WebClient webClient;
	private final String URL_AUTHORIZED = "/8fafdd68-a090-496f-8c9a-3442cf30dae6";
	private final String URL_NOTIFICATION = "/b19f7b9f-9cbf-4fc6-ad22-dc30601aec04";

	@Autowired
	public AccountService(AccountRepository accountRepository, UserRepository userRepository,
			TransferService transferService, WebClient webClient) {
		this.accountRepository = accountRepository;
		this.transferService = transferService;
		this.userRepository = userRepository;
		this.webClient = webClient;
	}

	public Account createAccount(AccountDTO accountDTO) {
		Optional<User> optionalUser = userRepository.findById(accountDTO.getUserId());
		if (!optionalUser.isPresent()) {
			throw new NotFoundException("User " + accountDTO.getUserId() + " not fount.");
		}
		return accountRepository.save(accountDTO.build());
	}

	public Account getAccount(Long id) {
		Optional<Account> optionalAccount = accountRepository.findById(id);
		if (!optionalAccount.isPresent()) {
			throw new NotFoundException("Account " + id + " not found.");
		}
		return optionalAccount.get();
	}

	public void deleteAccount(Long id) {
		accountRepository.deleteById(id);
	}

	@Transactional
	public void makeTransfer(TransferDTO transferDTO) {

		Account accountFrom = getAccount(transferDTO.getAccountFromId());
		Account accountTo = getAccount(transferDTO.getAccountToId());
		BigDecimal amount = transferDTO.getAmount();

		transferService.validate(accountFrom, accountTo, transferDTO);

		if (!authorized()) {
			throw new UnauthorizedException("Tranfer unauthorized.");
		}

		BigDecimal balanceAccountFrom = accountFrom.getBalance().subtract(amount);
		BigDecimal balanceAccountTo = accountTo.getBalance().add(amount);

		accountFrom.setBalance(balanceAccountFrom);
		accountTo.setBalance(balanceAccountTo);

		accountRepository.save(accountFrom);
		accountRepository.save(accountTo);

		sendNotification();

	}

	private boolean authorized() {
		Mono<Client> mono = this.webClient.method(HttpMethod.GET).uri(URL_AUTHORIZED).retrieve()
				.bodyToMono(Client.class);
		Client authorize = mono.block();
		return authorize.getMessage().equals("Autorizado");

	}

	private void sendNotification() {
		Mono<Client> mono = this.webClient.method(HttpMethod.GET).uri(URL_NOTIFICATION).retrieve()
				.bodyToMono(Client.class);
		mono.subscribe(m -> {
			System.out.println(m.getMessage());
		});

	}

}
