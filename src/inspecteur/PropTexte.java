package inspecteur;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JLabel;
import javax.swing.JTextField;

import annotation.Editable;
import interaction.Inspecteur;
import util.Debug;

/**
 * Propriete representant une ligne de texte
 * @author Hayen
 *
 */
public class PropTexte extends Propriete {
	
	private JLabel lbl;
	private JTextField txt;

	/**
	 * Creer une nouvelle propriete de type String. Ce constructeur considere que les tests de compatibilite ont deja ete effectues
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 */
	public PropTexte(Editable anno) { super(anno); }

	/**
	 * Pour que les types soient valides, il faut que le getter soit valide et que le parametre du setter soit du type String 
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 * @param classe {@code Class<?>} : la classe portant l'annotation
	 * @return <strong>{@code true}</strong> si l'annotation fournit un getter et un setter valide, sinon <strong>{@code false}</strong>
	 */
	public static boolean typeValide(Editable anno, Class<?> classe){ 
		Method getter;
		try {
			getter = classe.getMethod(anno.get());
			classe.getMethod(anno.set(), String.class);
		} catch (NoSuchMethodException | SecurityException e) {
			if (Debug.STACK)
				e.printStackTrace();
			return false;
		}
		if (getter.getReturnType() == void.class)
			return false;
		return true;
	}
	
	@Override
	public boolean set(Object cible) {
		if (txt != null){
			try {
				cible.getClass().getMethod(annotation.set(), String.class).invoke(cible, txt.getText());
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
		if (txt != null){
			try {
				txt.setText((String)cible.getClass().getMethod(annotation.get()).invoke(cible));
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
		if (txt == null){
			txt = new JTextField();
			txt.addActionListener(x -> set(specteur.getCible()) );
		}
		return txt;
	}

}
