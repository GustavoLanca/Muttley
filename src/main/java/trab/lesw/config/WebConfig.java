package trab.lesw.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import trab.lesw.interceptors.ApiKeyInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Autowired
	private ApiKeyInterceptor apiKeyInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(apiKeyInterceptor).addPathPatterns("/eventos/**")
		.excludePathPatterns("/eventos/public/**").addPathPatterns("/usuarios/**")
		.excludePathPatterns("/usuarios/public/**").addPathPatterns("/tags/**")
		.excludePathPatterns("tags/public/**").addPathPatterns("/disciplinas/**")
		.addPathPatterns("/participacoes/**");
	}
}