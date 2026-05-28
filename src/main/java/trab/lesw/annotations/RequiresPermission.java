package trab.lesw.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    String value() default ""; // Permissão específica exigida
    String[] any() default {}; // Qualquer uma dessas permissões
    String[] all() default {}; // Todas essas permissões
}