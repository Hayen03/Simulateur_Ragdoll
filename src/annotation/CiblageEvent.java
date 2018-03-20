package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 01/03/2016<br>
 * Identifie une methode qui va etre appelee lorsqu'un inspecteur cible ou "decible" un objet.
 * <p>
 * <strong>Comment utiliser? :</strong><br>
 * Simplement ajouter l'annotation au dessus de la methode. Le nom de la methode peut etre n'importe quoi.<br>
 * Si la methode possede aucun parametre, elle sera appelee lorsque l'objet sera "decible". Si elle a un parametre de type <code>{@link Inspecteur}</code>,
 * elle sera appelee lorsque l'objet se fait cibler.<br>
 * Il peut y avoir autant de methode remplissant ces critere que vous voulez, elles seront tous appelees.<br>
 * Les methodes identifiees doivent etre {@code public}
 * </p>
 * <p>
 * <strong>EX</strong>:<br>
 * Cette methode sera appelee lorsque l'{@code Inspecteur} cesse de cibler l'objet<br>
 * {@code @CiblageEvent}<br>
 * {@code public void nomDeLaMethode1()}<br>
 * Celle-ci sera appelee lorsqu'un {@code Inspecteur} commence a cibler l'objet<br>
 * {@code @CiblageEvent}<br>
 * {@code public void leNomAVraimentPasDImportance(Inspecteur specteur)}
 * </p>
 * 
 * @author Leo Jetzer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CiblageEvent {}
