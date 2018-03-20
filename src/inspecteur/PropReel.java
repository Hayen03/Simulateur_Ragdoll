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
 * Propriete representant un nombre reel
 * @author Leo Jetzer
 */
public class PropReel extends Propriete {
	private static final double MAX_SPIN = 1000;
	private static final double PAS_SPIN = 0.1;
	
	private JLabel lbl;
	private JSpinner spin;

	/**
	 * Creer une nouvelle propriete de type reel. Ce constructeur considere que les tests de compatibilite ont deja ete effectues
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 */
	public PropReel(Editable anno) { super(anno); }

	/**
	 * Retourne <strong>{@code true}</strong> si les types du setter et du getter sont == {@code double}
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 * @param classe {@code Class<?>} : la classe portant l'annotation
	 * @return <strong>{@code true}</strong> si la propriete peut etre represente par ce type
	 */
	public static boolean typeValide(Editable anno, Class<?> classe){
		try {
			Class<?> typeRetour = classe.getMethod(anno.get()).getReturnType();
			if (!(typeRetour == double.class || typeRetour == Double.class))
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
				cible.getClass().getMethod(annotation.set(), double.class).invoke(cible, (double)spin.getValue());
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
			double min = -MAX_SPIN;
			double max = MAX_SPIN;
			double defaut = 0;
			double pas = PAS_SPIN;
			
			String id = annotation.id() == "" ? annotation.nom() : annotation.id();
			
			try {
				min = (double)specteur.getCible().getClass().getField("MIN_" + id).get(specteur.getCible());
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {}
			try {
				max = (double)specteur.getCible().getClass().getField("MAX_" + id).get(specteur.getCible());
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {}
			try {
				defaut = (double)specteur.getCible().getClass().getField("DEFAUT_" + id).get(specteur.getCible());
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {}
			try {
				defaut = (double)specteur.getCible().getClass().getField("PAS_" + id).get(specteur.getCible());
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {}
			
			spin = new JSpinner();
			spin.setModel(new SpinnerNumberModel(defaut, min, max, pas));
			spin.addChangeListener(x -> set(specteur.getCible()));
		}
		return spin;
	}

}
