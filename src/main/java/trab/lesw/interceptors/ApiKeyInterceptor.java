package trab.lesw.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import trab.lesw.annotations.*;
 
import java.util.Set;
 
@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

	private final Set<String> CHAVES_VALIDAS = Set.of(
			"MUTTLEY3#5!4$@!!345MUTTLEY"
	);
	
	@Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {

        if (handler instanceof HandlerMethod handlerMethod) {
            if (handlerMethod.getMethodAnnotation(PublicRoute.class) != null) {
                return true;
            }
        }
        String apiKey = request.getHeader("X-API-KEY");
        if (apiKey == null || !isValidApiKey(apiKey)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("API Key invalida");
            return false;
        }
        return true;
    }
    private boolean isValidApiKey(String apiKey) {
    	if (!CHAVES_VALIDAS.contains(apiKey))
			return false;
        return true;
    }  }