package inspecteur;

import java.awt.Component;

import javax.swing.JLabel;

import annotation.Editable;
import interaction.Inspecteur;

/**
 * Classe abstraite decrivant les methodes et champs communs a tout les types de proprietes pour l'inspecteur
 * @author Leo Jetzer
 */
public abstract class Propriete {
	
	protected final Editable annotation;
	
	/**
	 * Creer une nouvelle propriete selon la description de l'{@code Editable} passee en parametre.</br>
	 * Ce constructeur suppose que les type de l'annotation ont ete verifie au prealable.
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 */
	public Propriete(Editable anno){
		annotation = anno;
	}
	
	/**
	 * Verifie si les types des methodes indiquees par l'annotation sont valide pour ce type de propriete.</br>
	 * Doit etre redefinie par les classe qui herite de propriete pour avoir un effet 
	 * @param prop - {@link Editable} : l'annotation a verifier
	 * @param classe - {@code Class<?>} : la classe portant l'annotation
	 * @return  <strong>{@code true}</strong> si les methodes de la propriete sont du bon type, sinon <strong>{@code false}</strong>
	 */
	public static boolean typeValide(Editable prop, Class<?> classe){ return false; }
	/**
	 * Change la valeur de l'objet possedant la propriete
	 * @param cible - {@code Object} : l'objet possedant la propriete
	 * @return <strong>{@code true}</strong> si la valeur a pu etre modifier, <strong>{@code false}</strong> s'il y a eu une erreur
	 */
	public abstract boolean set(Object cible);
	/**
	 * Change la valeur du composant representant la propriete
	 * @param cible - {@code Object} : l'objet possedant la propriete
	 * @return <strong>{@code true}</strong> si le changement s'est bien deroule, <strong>{@code false}</strong> s'il y a eu une erreur
	 */
	public abstract boolean get(Object cible);
	/**
	 * Creer le JLabel identifiant la propriete dans l'inspecteur
	 * @param specteur - {@code Inspecteur} : l'inspecteur inspectant la propriete
	 * @return un JLabel identifiant la propriete
	 */
	public abstract JLabel generateLabel(Inspecteur specteur);
	/**
	 * Creer le composant qui represente la propriete dans l'inspecteur
	 * @param specteur - {@code Inspecteur} : l'inspecteur inspectant la propriete
	 * @return le composant qui represente la propriete
	 */
	public abstract Component generateComponent(Inspecteur specteur);
	/**
	 * Retourne l'annotation decrivant la propriete
	 * @return l'annotation decrivant la propriete
	 */
	public Editable getEditable(){ return annotation; }
}
