package inspecteur;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import annotation.Editable;
import interaction.Inspecteur;
import util.Debug;

/**
 * Propriete represente par un nombre entier allant de l'infini negatif jusqu'a l'infini positif
 * @author Leo Jetzer
 *
 */
public class PropInt extends Propriete {

	private JLabel lbl;
	private JSpinner spin;
	
	/**
	 * Creer une nouvelle propriete de type Entier
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 */
	public PropInt(Editable anno) { super(anno); }
	
	/**
	 * Retourne <strong>{@code true}</strong> si les types du setter et du getter sont == {@code int}
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 * @param classe {@code Class<?>} : la classe portant l'annotation
	 * @return <strong>{@code true}</strong> si la propriete peut etre represente par ce type
	 */
	public static boolean typeValide(Editable anno, Class<?> classe){
		try {
			Class<?> typeRetour = classe.getMethod(anno.get()).getReturnType();
			if (!(typeRetour == int.class || typeRetour == Integer.class))
				return false;
			classe.getMethod(anno.set(), typeRetour); // verifie que le setter existe
		} catch (NoSuchMethodException | NullPointerException | SecurityException e ) {
			if (Debug.STACK)
				e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean set(Object cible) {
		if (spin != null){
			try {
				cible.getClass().getMethod(annotation.set(), int.class).invoke(cible, (int)spin.getValue());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				if (Debug.STACK)
					e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean get(Object cible) {
		if (spin != null){
			try {
				spin.setValue(cible.getClass().getMethod(annotation.get()).invoke(cible));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				if (Debug.STACK)
					e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public JLabel generateLabel(Inspecteur specteur) { return (lbl == null ? lbl = new JLabel(annotation.nom()) : lbl); }

	@Override
	public Component generateComponent(Inspecteur specteur) {
		if (spin == null){
			
			int min = Integer.MIN_VALUE;
			int max = Integer.MAX_VALUE;
			int defaut = 0;
			int pas = 1;
			
			String id = annotation.id() == "" ? annotation.nom() : annotation.id();
			
			try {
				min = (int)specteur.getCible().getClass().getField("MIN_" + id).get(specteur.getCible());
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {}
			try {
				max = (int)specteur.getCible().getClass().getField("MAX_" + id).get(specteur.getCible());
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {}
			try {
				defaut = (int)specteur.getCible().getClass().getField("DEFAUT_" + id).get(specteur.getCible());
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {}
			try {
				defaut = (int)specteur.getCible().getClass().getField("PAS_" + id).get(specteur.getCible());
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {}
			
			spin = new JSpinner();
			spin.setModel(new SpinnerNumberModel(defaut, min, max, pas));
			spin.addChangeListener(x -> set(specteur.getCible()));
		}
		return spin;
	}

}
