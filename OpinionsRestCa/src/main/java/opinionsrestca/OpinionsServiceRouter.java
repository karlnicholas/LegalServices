package opinionsrestca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import opinions.service.OpinionsService;

@Configuration
public class OpinionsServiceRouter {
	@Bean
	public RouterFunction<ServerResponse> route(OpinionsServiceHandler opinionsServiceHandler) {
		return RouterFunctions
			.route(RequestPredicates.POST(OpinionsService.OPINIONCITATIONS)
				.and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)).and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), opinionsServiceHandler::getOpinionsWithStatuteCitations)
			;
	}

}
