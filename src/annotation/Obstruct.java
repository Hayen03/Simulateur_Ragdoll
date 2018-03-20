package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation qui cache une propriete dans l'inspecteur
 * <p>
 * Par exemple, si un classe A definit, a l'aide de l'annotation Editable, une propriete "Nom", une classe B ne desirant pas que cette
 * propriete soit visible peut la cacher avec l'anotation {@code @Obstruct("Nom") }.<br>
 * La propriete "Nom" ne sera donc plus affiche lorsqu'un objet de type B est inspecte
 * </p>
 * @author Leo Jetzer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Obstructs.class)
public @interface Obstruct {
	String value();
}
