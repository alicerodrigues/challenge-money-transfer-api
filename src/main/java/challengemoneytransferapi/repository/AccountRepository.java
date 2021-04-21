package challengemoneytransferapi.repository;

import org.springframework.data.repository.CrudRepository;

import challengemoneytransferapi.model.entity.Account;

public interface AccountRepository extends CrudRepository<Account, Long> {

}
