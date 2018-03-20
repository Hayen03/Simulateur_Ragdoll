package interaction;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import annotation.CiblageEvent;
import annotation.Editable;
import annotation.Obstruct;
import inspecteur.PropMessage;
import inspecteur.PropVoid;
import inspecteur.Propriete;
import util.Debug;
import util.Reflection;

/**
 * Classe representant l'inspecteur. L'inspecteur est un panneau qui affiche de l'information et permet de modifier des parametres specifiques a l'objet observe</br>
 * Pour identifier une propriete, il faut ajouter l'annotation {@link Editable} a la classe desire
 * @author Leo Jetzer
 */
public class Inspecteur extends JPanel{

	/**
	 * Pour enlever l'erreur et juste pour etre plus pratique en generale
	 */
	private static final long serialVersionUID = 3676329273874982261L;
	
	/**
	 * L'objet cible par l'inspecteur
	 */
	private Object cible;
	/**
	 * Dictionnaire de toutes les proprietes presentement dans l'inspecteur
	 */
	private Hashtable<String, Propriete> proprietes;
	/**
	 * Donnee pour les espaces occupes par les proprietes sur l'interface
	 */
	private int gap = 5;
	
	private Collection<PropertyChangeListener> listeners = new LinkedList<PropertyChangeListener>();
	
	/**
	 * Cree un nouvel inspecteur depourvu de cible
	 */
	public Inspecteur(){
		super();
		proprietes = new Hashtable<String, Propriete>();
		cible = null;
		setLayout(null);
	}
	
	/**
	 * Retourne l'objet cible par l'inspecteur
	 * @return l'objet cible par l'inspecteur
	 */
	public Object getCible(){ return cible; }
	/**
	 * Attribut une nouvelle cible a l'inspecteur, modifiant ainsi son interface
	 * @param c - <code>Object</code> : la nouvelle cible
	 */
	public void setCible(Object c) {
		/*
		 * Utilise le concept de reflection afin de trouver les getters/setters d'une classe
		 * et ainsi dynamiquement generer un layout pour l'inspecteur
		 */
		if (cible == c) // l'objet cible est le meme, donc il n'y a aucun changement a apporter
			return;
		
		removeAll();
		proprietes.clear();
		
		if (cible != null)
			fireCiblageEvent(cible, false); // essai d'envoyer un message a l'objet l'informant qu'il n'est plus cible
		cible = c;
		
		if (c != null){ // il n'y a pas d'objet cible, donc on a pas besoins de chercher des proprietes
			
			for (Editable edit : Reflection.getAnnotationRecursif(Editable.class, c.getClass())){
				Propriete p = createPropriete(edit, c.getClass());
				if (p != null)
					proprietes.put(p.getEditable().nom(), p);
			}
			for (Obstruct obstruct : Reflection.getAnnotationRecursif(Obstruct.class, c.getClass())){
				proprietes.remove(obstruct.value());
			}
			genererLayout();
			updateAll();
			fireCiblageEvent(cible, true); // tente d'envoyer un message a l'objet pour l'informer qu'il est maintenant cible
		}
		repaint();
	}
	
	/**
	 * Place les composants generer par les proprietes a leur place
	 */
	private void genererLayout(){
		GroupLayout gl = new GroupLayout(this);
		Group groupeHorizontalLbl = gl.createParallelGroup(Alignment.LEADING);
		Group groupeHorizontalComponent = gl.createParallelGroup(Alignment.LEADING);
		SequentialGroup groupeVertical = gl.createSequentialGroup();
		
		groupeVertical.addContainerGap();
		for (Propriete prop : proprietes.values()){
			groupeHorizontalLbl.addComponent(prop.generateLabel(this));
			groupeHorizontalComponent.addComponent(prop.generateComponent(this), 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
			groupeVertical.addGroup(
					gl.createParallelGroup(Alignment.CENTER)
					.addComponent(prop.generateLabel(this))
					.addComponent(prop.generateComponent(this), 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			);
			groupeVertical.addGap(gap);
		}
		groupeVertical.addContainerGap();
		
		gl.setHorizontalGroup(gl.createSequentialGroup()
				.addContainerGap()
				.addGroup(groupeHorizontalLbl)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(groupeHorizontalComponent)
				.addContainerGap()
		);
		gl.setVerticalGroup(groupeVertical);
		
		this.setLayout(gl);
	}
	
	/**
	 * Creer une nouvelle propriete en suivant les directives de l'annotation
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 * @param classe - {@code Class<?>} : la classe portant l'annotation
	 * @return
	 */
	private Propriete createPropriete(Editable anno, Class<?> classe){
		try {
			if ((boolean)anno.type().getMethod("typeValide", Editable.class, Class.class).invoke(null, anno, classe))
				return (Propriete)anno.type().getConstructor(Editable.class).newInstance(anno);
			else if (PropMessage.typeValide(anno, classe))
				return new PropMessage(anno);
			else
				return new PropVoid(anno);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | InstantiationException e) {// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void setSize(int w, int h){
		super.setSize(w, h);
		genererLayout();
	}
	@Override
	public void setBounds(int x, int y, int w, int h){
		super.setBounds(x, y, w, h);
		genererLayout();
	}
	
	/**
	 * Synchronise toutes les proprietes de l'inspecteur avec celles de la cible
	 */
	public void updateAll(){
		for (Propriete p : proprietes.values())
			p.get(cible);
	}
	/**
	 * Synchronise la propriete de l'inspecteur identifiee avec celle de l'objet cible
	 * @param propriete - {@code String} : le nom de la propriete a synchronisee
	 */
	public void update(String propriete){
		Propriete prop = proprietes.get(propriete);
		if (prop != null)
			prop.get(cible);
	}
	
	/**
	 * Envoi un message a l'objet indique pour lui indiquer s'il vient de ce faire cibler({@code true}), ou si l'inspecteur vient d'arreter de le cibler({@code false})
	 * @param obj : l'objet auquel envoyer les messages
	 * @param on : {@code true} si l'objet se fait cibler, sinon {@code false}
	 */
	private void fireCiblageEvent(Object obj, boolean on){
		Method[] methods = obj.getClass().getMethods();
		for (Method meth : methods){
			if (meth.isAnnotationPresent(CiblageEvent.class)){
				try {
					if (on)
						meth.invoke(obj, this);
					else
						meth.invoke(obj);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
			}
		}
	}
	
	/**
	 * Retourne la propriete relie au nom {@code prop}
	 * @param prop - {@code String} : le nom de la propriete
	 * @return la propriete
	 */
	public Propriete getPropriete(String prop){
		Debug.log("" + proprietes.get(prop) + "\n");
		return proprietes.get(prop);
	}
	
	/**
	 * Rajoute un listener qui se fera envoyer un evenement chaque fois que le materiau sera modifier
	 * @param listener - {@code PropertyChangeListener} : l'ecouteur a ajouter
	 */
	public void addProprieteChangeListener(PropertyChangeListener listener){
		listeners.add(listener);
	}
	/**
	 * Enleve un ecouteur qui etait sur cet objet
	 * @param listener - {@code PropertyChangeListener} : l'ecouteur a enlever
	 */
	public void removeProprieteChangeListener(PropertyChangeListener listener){
		listeners.remove(listener);
	}
	/**
	 * Envoi un evenement a tout les ecouteurs enregistre
	 * @param propertyName - {@code String} : le nom de la propriete modifiee
	 * @param val_old - {@code} : l'ancienne valeur de la propriete
	 * @param val_new - {@code} : la nouvelle valeur de la propriete
	 */
	public void fireProprieteChangeEvent(String propertyName, Object val_old, Object val_new){
		PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, val_old, val_new);
		for (PropertyChangeListener list : listeners)
			list.propertyChange(event);
	}
	
}
