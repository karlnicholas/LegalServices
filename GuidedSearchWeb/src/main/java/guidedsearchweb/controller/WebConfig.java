package guidedsearchweb.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.view.HttpMessageWriterView;
import org.springframework.web.reactive.result.view.ViewResolver;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {
/*
	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { 
			"/public/", 
			"/webjars/"
			};

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
    new InternalResourceViewResolver
    ViewResolver vr;;
        registry.jsp("/WEB-INF/views/", ".jsp");
    }
    
    @Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
		.addResourceHandler("/**", "/webjars/**")
		.addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS)
		;
	}
*/
}

/*
<bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
<property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
<property name="prefix" value="/WEB-INF/jsp/"/>
<property name="suffix" value=".jsp"/>
</bean>
*/
