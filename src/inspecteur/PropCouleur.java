package inspecteur;

import java.awt.Color;
import java.awt.Component;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JLabel;

import annotation.Editable;
import interaction.Inspecteur;
import ui.BoutonCouleur;
import util.Debug;

/**
 * Propriete representant une couleur
 * @author Leo Jetzer
 */
public class PropCouleur extends Propriete {
	
	private JLabel lbl;
	private BoutonCouleur btnCouleur;

	/**
	 * Creer une nouvelle propriete de type Color. Ce constructeur considere que les tests de compatibilite ont deja ete effectues
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 */
	public PropCouleur(Editable anno) { super(anno); }

	/**
	 * Verifie que le setter et le getter indique par l'annotation demande et retourne un objet de type {@code Color} respectivement
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 * @param classe {@code Class<?>} : la classe portant l'annotation
	 * @return <strong>{@code true}</strong> si la propriete peut etre represente par ce type
	 */
	public static boolean typeValide(Editable anno, Class<?> classe){
		try {
			if (classe.getMethod(anno.get()).getReturnType() != Color.class)
				return false;
			classe.getMethod(anno.set(), Color.class); // verifie que le setter existe
		} catch (NoSuchMethodException | NullPointerException | SecurityException e ) {
			if (Debug.STACK)
				e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean set(Object cible) {
		if (btnCouleur != null){
			try {
				cible.getClass().getMethod(annotation.set(), Color.class).invoke(cible, btnCouleur.getColor());
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
		if (btnCouleur != null){
			try {
				btnCouleur.setCouleur((Color)cible.getClass().getMethod(annotation.get()).invoke(cible));
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
		if (btnCouleur == null){
			btnCouleur = new BoutonCouleur();
			btnCouleur.addColorChangeListener(x -> set(specteur.getCible()));
		}
		return btnCouleur;
	}

}
