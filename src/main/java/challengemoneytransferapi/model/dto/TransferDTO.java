package challengemoneytransferapi.model.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO {

	@NotNull(message = "AccountFromId is mandatory")
	private Long accountFromId;
	@NotNull(message = "AccountToId is mandatory")
	private Long accountToId;
	@NotNull(message = "Amount is mandatory")
	private BigDecimal amount;

}
