package challengemoneytransferapi.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import challengemoneytransferapi.model.dto.Client;
import challengemoneytransferapi.model.dto.TransferDTO;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

	private final WebClient webClient;
	private final String URL_NOTIFICATION = "/b19f7b9f-9cbf-4fc6-ad22-dc30601aec04";

	@Autowired
	NotificationServiceImpl(WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public void notifyAboutTransfer(TransferDTO transferDTO, String message) {
		log.info("Sending notification to owner of {}: {}", transferDTO.getAccountToId(), message);
		this.webClient.method(HttpMethod.POST).uri(URL_NOTIFICATION)
				.body(Mono.just(message), String.class)
				.retrieve().bodyToMono(Client.class).retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(5)))
				.delaySubscription(Duration.ofSeconds(10));
	}

}
