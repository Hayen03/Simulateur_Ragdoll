package inspecteur;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JLabel;

import annotation.Editable;
import interaction.Inspecteur;
import util.Debug;

/**
 * Type de propriete affichant une valeur qui ne peut etre modifie par l'inspecteur
 * @author Leo Jetzer
 *
 */
public class PropMessage extends Propriete {

	private JLabel lbl, msg;
	
	/**
	 * Creer une nouvelle propriete de type Message
	 * @param anno
	 */
	public PropMessage(Editable anno) { super(anno); }

	/**
	 * Toute les proprietes peuvent etres representees par un type message si elles fournissent un getter valide 
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 * @param classe {@code Class<?>} : la classe portant l'annotation
	 * @return <strong>{@code true}</strong> si l'annotation fournit un getter valide, sinon <strong>{@code false}</strong>
	 */
	public static boolean typeValide(Editable anno, Class<?> classe){ 
		Method getter;
		try {
			getter = classe.getMethod(anno.get());
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
	public boolean set(Object cible) { return true; }

	@Override
	public boolean get(Object cible) {
		if (msg != null){
			try {
				String txt = cible.getClass().getMethod(annotation.get()).invoke(cible).toString();
				msg.setText(txt);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
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
	public Component generateComponent(Inspecteur specteur) { return (msg == null ? msg = new JLabel() : msg); }

}
