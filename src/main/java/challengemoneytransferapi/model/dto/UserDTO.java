package challengemoneytransferapi.model.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import challengemoneytransferapi.enums.PersonType;
import challengemoneytransferapi.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	@NotBlank(message = "Name is mandatory")
	private String name;
	@Email
	@NotBlank(message = "Email is mandatory")
	private String email;
	@NotBlank(message = "Password is mandatory")
	private String password;
	@NotBlank(message = "Document is mandatory")
	private String document;
	@NotNull(message = "PersonType is mandatory")
	private PersonType personType;

	public User build() {
		User user = new User().setName(this.name).setEmail(this.email).setPassword(this.password)
				.setDocument(this.document).setPersonType(this.personType);
		return user;
	}

}
