package trab.lesw.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // Pode ser usada em métodos
@Retention(RetentionPolicy.RUNTIME) // Disponível em runtime via reflection
public @interface ApiKeyRequired {
	String[] permissions() default {};
	boolean required() default true;
	AccessLevel accessLevel() default AccessLevel.USER;
	String description() default "";
}