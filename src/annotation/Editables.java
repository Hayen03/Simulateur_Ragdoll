package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Obligatoire pour que l'annotation {@link Editable} puisse etre mise plusieurs fois sur une meme classe
 * @author Hayen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Editables{
	Editable[] value();
}
