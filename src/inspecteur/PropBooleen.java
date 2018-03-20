package inspecteur;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import annotation.Editable;
import interaction.Inspecteur;
import util.Debug;

/**
 * Propriete representant une valeur booleenne
 * @author Leo Jetzer
 */
public class PropBooleen extends Propriete {

	private JLabel lbl;
	private JCheckBox chkBox;
	
	/**
	 * Creer une nouvelle propriete de type booleenne. Ce constructeur considere que les tests de compatibilite ont deja ete effectues
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 */
	public PropBooleen(Editable anno) { super(anno); }
	
	/**
	 * Retourne <strong>{@code true}</strong> si les types du setter et du getter sont == {@code boolean}
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 * @param classe {@code Class<?>} : la classe portant l'annotation
	 * @return <strong>{@code true}</strong> si la propriete peut etre represente par ce type
	 */
	public static boolean typeValide(Editable anno, Class<?> classe){
		try {
			if (classe.getMethod(anno.get()).getReturnType() != boolean.class)
				return false;
			classe.getMethod(anno.set(), boolean.class); // verifie que le setter existe
		} catch (NoSuchMethodException | NullPointerException | SecurityException e ) {
			if (Debug.STACK)
				e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean set(Object cible) {
		if (chkBox != null){
			try {
				cible.getClass().getMethod(annotation.set(), boolean.class).invoke(cible, chkBox.isSelected());
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
		if (chkBox != null){
			try {
				chkBox.setSelected((boolean)cible.getClass().getMethod(annotation.get()).invoke(cible));
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
		if (chkBox == null){
			chkBox = new JCheckBox();
			chkBox.addActionListener(x -> set(specteur.getCible()));
		}
		return chkBox;
	}

}
