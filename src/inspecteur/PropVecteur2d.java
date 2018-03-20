package inspecteur;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import annotation.Editable;
import interaction.Inspecteur;
import physique.SVector2d;
import util.Debug;

/**
 * Propriete representant un vecteur bidimensionnel
 * @author Leo Jetzer
 *
 */
public class PropVecteur2d extends Propriete {
	public static final double LIMITE_SPINNER = 100;

	private JLabel lbl;
	private JPanel pnlVecteur;
	private JSpinner spinX, spinY;
	/**
	 * Creer une nouvelle propriete de type SVector2d. Ce constructeur considere que les tests de compatibilite ont deja ete effectues
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 */
	public PropVecteur2d(Editable anno) { super(anno); }
	
	/**
	 * Verifie que le setter et le getter indique par l'annotation demande et retourne un objet de type {@code SVector2d} respectivement
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 * @param classe {@code Class<?>} : la classe portant l'annotation
	 * @return <strong>{@code true}</strong> si la propriete peut etre represente par ce type
	 */
	public static boolean typeValide(Editable anno, Class<?> classe){
		try {
			if (classe.getMethod(anno.get()).getReturnType() != SVector2d.class)
				return false;
			classe.getMethod(anno.set(), SVector2d.class); // verifie que le setter existe
		} catch (NoSuchMethodException | NullPointerException | SecurityException e ) {
			if (Debug.STACK)
				e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean set(Object cible) {
		if (pnlVecteur != null){ // puisqu'ils sont generes en meme temps, si le JPanel existe, alors les JSpinner existent egalement
			SVector2d vecteur = new SVector2d((double)spinX.getValue(), (double)spinY.getValue());
			try {
				cible.getClass().getMethod(annotation.set(), SVector2d.class).invoke(cible, vecteur);
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
		if (pnlVecteur != null){
			try {
				SVector2d vecteur = (SVector2d)cible.getClass().getMethod(annotation.get()).invoke(cible);
				spinX.setValue(vecteur.getX());
				spinY.setValue(vecteur.getY());
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
		if (pnlVecteur == null){
			pnlVecteur = new JPanel();
			pnlVecteur.setLayout(new BoxLayout(pnlVecteur, BoxLayout.X_AXIS));
			pnlVecteur.setBackground(specteur.getBackground());
			
			ChangeListener cl = x -> set(specteur.getCible());
			
			spinX = new JSpinner();
			spinX.setModel(createSpinnerModel());
			spinX.addChangeListener(cl);
			pnlVecteur.add(spinX);
			
			spinY = new JSpinner();
			spinY.setModel(createSpinnerModel());
			spinY.addChangeListener(cl);
			pnlVecteur.add(spinY);
		}
		return pnlVecteur;
	}
	
	protected SpinnerModel createSpinnerModel(){
		return new SpinnerNumberModel(0, -LIMITE_SPINNER, LIMITE_SPINNER, 0.1d);
	}

}
