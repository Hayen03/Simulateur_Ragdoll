package util;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe utilitaire offrant des methodes pour faciliter la reflection
 * @author Leo Jetzer
 */
public class Reflection {
	
	/**
	 * Extrait les setters d'un tableau de methodes
	 * @param methodes - <code>Method[]</code> : le tableau de methodes a filtrer
	 * @return un tableau contenant les setters
	 */
	public static Method[] getSetters(Method[] methodes){
		List<Method> meths = new LinkedList<Method>();
		for (Method meth : methodes)
			if (isSetter(meth))
				meths.add(meth);
		return meths.toArray(new Method[meths.size()]);
	}
	/**
	 * Extrait les getters d'un tableau de methodes
	 * @param methodes - <code>Method[]</code> : le tableau de methodes a filtrer
	 * @return un tableau contenant les getters
	 */
	public static Method[] getGetters(Method[] methodes){
		List<Method> meths = new LinkedList<Method>();
		for (Method meth : methodes)
			if (isGetter(meth))
				meths.add(meth);
		return meths.toArray(new Method[meths.size()]);
	}
	
	/**
	 * Verifie si la methode passee en parametre est un <em>setter</em>.<br>
	 * Un <em>setter</em> est definit comme une methode commencant par le prefixe 'set' et possedant un seul parametre
	 * @param meth - <code>Method</code> : la methode a verifier
	 * @return <code><strong>true</strong></code> si la methode est un <em>setter</em>, sinon <code><strong>false</strong></code>
	 */
	public static boolean isSetter(Method meth){
		return meth.getName().startsWith("set") && meth.getParameterTypes().length == 1;
	}
	/**
	 * Verifie si la methode passee en parametre est un <em>setter</em>.<br>
	 * Un <em>setter</em> est definit comme une methode possedant un seul parametre
	 * @param meth - <code>Method</code> : la methode a verifier
	 * @return <code><strong>true</strong></code> si la methode est un <em>setter</em>, sinon <code><strong>false</strong></code>
	 */
	public static boolean isSetterNonConventionnel(Method meth){
		return meth.getParameterTypes().length == 1;
	}
	/**
	 * Verifie si la methode passee en parametre est un <em>getter</em>.<br>
	 * Un <em>getter</em> est definit comme une methode commencant par le prefixe 'get', ne possedant aucun parametre et possedant un type de retour autre que <code><strong>void</strong></code>
	 * @param meth - <code>Method</code> : la methode a verifier
	 * @return <code><strong>true</strong></code> si la methode est un <em>getter</em>, sinon <code><strong>false</strong></code>
	 */
	public static boolean isGetter(Method meth){
		return meth.getName().startsWith("get") && !meth.getReturnType().equals(void.class) && meth.getParameterTypes().length == 0;
	}
	/**
	 * Verifie si la methode passee en parametre est un <em>getter</em>.<br>
	 * Un <em>getter</em> est definit comme une methode ne possedant aucun parametre et possedant un type de retour autre que <code><strong>void</strong></code>
	 * @param meth - <code>Method</code> : la methode a verifier
	 * @return <code><strong>true</strong></code> si la methode est un <em>getter</em>, sinon <code><strong>false</strong></code>
	 */
	public static boolean isGetterNonConventionnel(Method meth){
		return !meth.getReturnType().equals(void.class) && meth.getParameterTypes().length == 0;
	}
	
	/**
	 * Filtre les champs passe en parametre et retourne un tableau de ceux qui porte l'annotation designee.<br>
	 * Si aucun champ ne porte l'annotation, le tableau retourne sera vide (taille 0)
	 * @param champs - <code>Field[]</code> : les champs a filtrer
	 * @param annotation - <code>T extends Annotation</code> : l'annotation a verifie
	 * @return un tableau qui contient les champs portant l'annotation designee
	 */
	public static Field[] getChampAvecAnnotation(Field[] champs, Class<? extends Annotation> annotation){
		List<Field> fields = new LinkedList<Field>();
		for (Field f : champs)
			if (f.isAnnotationPresent(annotation))
				fields.add(f);
		return fields.toArray(new Field[fields.size()]);
	}
	
	/**
	 * Retourne les instance de l'annotation desiree presentes sur les champs passes en parametre.
	 * @param <T> : la classe d'annotation
	 * @param annotation - Le type d'annotation recherche
	 * @param champs - La liste des champs a fouille
	 * @return un tableau contenant toute les annotations trouvees
	 */
	@SuppressWarnings("unchecked") // ca c'est parce que je sais exactement le type des elements de la liste mais java pense que je ne le sais pas...
	public static <T extends Annotation> T[] getAnnotationDesChamps(Class<T> annotation, Field... champs){
		List<T> anno = new LinkedList<T>();
		
		for (Field field : champs){
			for (T a : field.getAnnotationsByType(annotation))
				anno.add(a);
		}
		return anno.toArray((T[]) Array.newInstance(annotation, anno.size()));
	}
	
	/**
	 * Retourne une liste comprenant toute les annotations du type desire portees par la classe et ses super classes
	 * @param <A> : la classe d'annotation
	 * @param annotation - {@code Class<A extends Annotation>} : le type de l'annotation a rechercher
	 * @param classe - {@code Class<?>} : la classe dans laquelle chercher les annotations
	 * @return une liste contenant les annotations
	 */
	public static <A extends Annotation> List<A> getAnnotationRecursif(Class<A> annotation, Class<?> classe){
		LinkedList<A> liste = new LinkedList<A>();
		getAnnotationRecursif(annotation, classe, liste);
		return liste;
	}
	/**
	 * Ajoute les annotations du type desire portees par la classe indiquee et ses super classes a la liste
	 * @param <A> : la classe d'annotation
	 * @param annotation - {@code Class<A extends Annotation>} : le type de l'annotation a rechercher
	 * @param classe - {@code Class<?>} : la classe dans laquelle chercher les annotations
	 * @param liste - {@code List<A extends Annotation>} : la liste a modifier
	 */
	public static <A extends Annotation> void getAnnotationRecursif(Class<A> annotation, Class<?> classe, List<A> liste){
		for (A anno : classe.getAnnotationsByType(annotation))
			liste.add(anno);
		Class<?> superClass = classe.getSuperclass();
		if (superClass != null)
			getAnnotationRecursif(annotation, superClass, liste);
	}
	
}
