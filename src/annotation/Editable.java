package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import inspecteur.PropVoid;
import inspecteur.Propriete;
import interaction.Inspecteur;

/**
 * Annotation permettant d'identifier une propriete a afficher ou modifier pour un {@link Inspecteur}
 * @author Leo Jetzer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Editables.class)
public @interface Editable {
	/**
	 * Le nom de la propriete
	 * @return le nom de la propriete
	 */
	String nom();
	/**
	 * le nom du setter qui permet de modifier la propriete
	 * @return le nom du setter
	 */
	String get() default "";
	/**
	 * le nom de la methode qui permet d'acceder a la propriete
	 * @return le nom du getter
	 */
	String set() default "";
	/**
	 * le type de la propriete
	 * @return le type de la propriete
	 */
	Class<? extends Propriete> type() default PropVoid.class;
	/**
	 * un autre nom pour la propriete
	 * <br>
	 * <b>{@code default ""}</b>
	 * @return L,identificateur, s'il a lieu, de la propriete
	 */
	String id() default "";
}
