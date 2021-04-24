package challengemoneytransferapi.model.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Min;

import challengemoneytransferapi.model.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {

	private Long userId;
	@Min(value = 0, message = "Balance should not be less than 0")
	private BigDecimal balance;

	public Account build() {
		Account account = new Account().setUserId(this.userId).setBalance(this.balance);
		return account;
	}
}
