package challengemoneytransferapi.service;

import challengemoneytransferapi.model.dto.TransferDTO;

public interface NotificationService {

	void notifyAboutTransfer(TransferDTO transferDTO, String message);

}
