package challengemoneytransferapi.repository;

import org.springframework.data.repository.CrudRepository;

import challengemoneytransferapi.model.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {

	User findByEmail(String email);

	User findByDocument(String Document);

}
