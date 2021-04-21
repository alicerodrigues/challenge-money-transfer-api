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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import challengemoneytransferapi.exception.NotEnoughFundsException;
import challengemoneytransferapi.exception.NotFoundException;
import challengemoneytransferapi.exception.TransferBetweenSameAccountException;
import challengemoneytransferapi.exception.TransferFromLegalPersonException;
import challengemoneytransferapi.exception.UnauthorizedException;
import challengemoneytransferapi.model.dto.AccountDTO;
import challengemoneytransferapi.model.dto.TransferDTO;
import challengemoneytransferapi.model.entity.Account;
import challengemoneytransferapi.service.AccountService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountController {

	private final AccountService accountService;

	@Autowired
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
		log.info("Creating account {}", accountDTO);
		try {
			Account accountSaved = accountService.createAccount(accountDTO);
			return new ResponseEntity<>(accountSaved, HttpStatus.CREATED);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(path = "/{id}")
	public ResponseEntity<Object> getAccount(@PathVariable Long id) {
		log.info("Retrieving user for id {}", id);
		try {
			Account account = accountService.getAccount(id);
			return ResponseEntity.ok(account);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping(path = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> makeTransfer(@RequestBody @Valid TransferDTO transferDTO) {
		log.info("Making transfer {}", transferDTO);

		try {
			accountService.makeTransfer(transferDTO);
		} catch (NotFoundException a) {
			return new ResponseEntity<>(a.getMessage(), HttpStatus.NOT_FOUND);
		} catch (NotEnoughFundsException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (TransferFromLegalPersonException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (TransferBetweenSameAccountException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (UnauthorizedException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		log.info("Delete account id {}", id);
		accountService.deleteAccount(id);
	}

}
