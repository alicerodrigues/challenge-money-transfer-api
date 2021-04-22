package challengemoneytransferapi.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import challengemoneytransferapi.exception.DuplicateUserDataException;
import challengemoneytransferapi.exception.NotFoundException;
import challengemoneytransferapi.model.dto.UserDTO;
import challengemoneytransferapi.model.entity.User;
import challengemoneytransferapi.repository.UserRepository;
import lombok.Getter;

@Service
public class UserService {
	@Getter
	private final UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User createUser(UserDTO userDTO) {
		validate(userDTO);
		return userRepository.save(userDTO.build());
	}

	public User getUser(Long id) {
		Optional<User> optionalUser = userRepository.findById(id);
		if (!optionalUser.isPresent()) {
			throw new NotFoundException("User " + id + " not found.");
		}
		return optionalUser.get();

	}

	public Iterable<User> getAllUsers() {
		Iterable<User> list = userRepository.findAll();
		return list;
	}

	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	private void validate(UserDTO userDTO) {
		if (userRepository.findByDocument(userDTO.getDocument()) != null) {
			throw new DuplicateUserDataException("Document " + userDTO.getDocument() + " already exists.");
		}
		if (userRepository.findByEmail(userDTO.getEmail()) != null) {
			throw new DuplicateUserDataException("Email " + userDTO.getEmail() + " already exists.");
		}
	}

}
