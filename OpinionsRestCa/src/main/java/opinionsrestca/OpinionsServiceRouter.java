package opinionsrestca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import opinions.service.ReactiveOpinionsService;

@Configuration
public class OpinionsServiceRouter {
	@Bean
	public RouterFunction<ServerResponse> route(OpinionsServiceHandler opinionsServiceHandler) {
		return RouterFunctions
			.route(RequestPredicates.POST(ReactiveOpinionsService.OPINIONCITATIONS)
				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), opinionsServiceHandler::getOpinionsWithStatuteCitations)
			.andRoute(RequestPredicates.GET("")
					.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), opinionsServiceHandler::exampleOpinionKeys)
			;
	}

}
