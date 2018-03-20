package inspecteur;

import java.awt.Component;

import javax.swing.JLabel;

import annotation.Editable;
import interaction.Inspecteur;

/**
 * Type de propriete representant une propriete n'ayant pas de valeur
 * @author Leo Jetzer
 */
public class PropVoid extends Propriete {

	private JLabel lbl;
	
	/**
	 * Creer une nouvelle propriete de type void
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 */
	public PropVoid(Editable anno) {
		super(anno);
	}

	/**
	 * Toute les proprietes peuvent etres representees par un type void
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 * @param classe {@code Class<?>} : la classe portant l'annotation
	 * @return <strong>{@code true}</strong>
	 */
	public static boolean typeValide(Editable anno, Class<?> classe){ return true; }
	
	@Override
	public boolean set(Object cible) { return true; }

	@Override
	public boolean get(Object cible) { return true; }

	@Override
	public JLabel generateLabel(Inspecteur specteur) {
		return (lbl == null ? lbl = new JLabel(annotation.nom()) : lbl);
	}

	@Override
	public Component generateComponent(Inspecteur specteur) { return null; }

}
