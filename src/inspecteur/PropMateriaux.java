package inspecteur;

import java.awt.Component;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import annotation.Editable;
import interaction.Inspecteur;
import physique.Materiaux;
import util.Debug;
import util.Images;

/**
 * Propriete representant un materiau
 * @author Leo Jetzer
 *
 */
public class PropMateriaux extends Propriete {

	private JLabel lbl;
	private JPanel pnl;
	private JButton btnPlus, btnMoins;
	private JComboBox<Materiaux> choix;
	
	/**
	 * Creer une nouvelle propriete de type Materiaux. Ce constructeur considere que les tests de compatibilite ont deja ete effectues
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 */
	public PropMateriaux(Editable anno) { super(anno); }
	
	/**
	 * Verifie que le setter et le getter indique par l'annotation demande et retourne un objet de type {@code Color} respectivement
	 * @param anno - {@code Editable} : l'annotation decrivant la propriete
	 * @param classe {@code Class<?>} : la classe portant l'annotation
	 * @return <strong>{@code true}</strong> si la propriete peut etre represente par ce type
	 */
	public static boolean typeValide(Editable anno, Class<?> classe){
		try {
			if (classe.getMethod(anno.get()).getReturnType() != Materiaux.class)
				return false;
			classe.getMethod(anno.set(), Materiaux.class); // verifie que le setter existe
		} catch (NoSuchMethodException | NullPointerException | SecurityException e ) {
			if (Debug.STACK)
				e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean set(Object cible) {
		if (pnl != null){
			try {
				cible.getClass().getMethod(annotation.set(), Materiaux.class).invoke(cible, (Materiaux)choix.getSelectedItem());
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
		try {
			choix.setSelectedItem((Materiaux)cible.getClass().getMethod(annotation.get()).invoke(cible));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			if (Debug.STACK)
				e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public JLabel generateLabel(Inspecteur specteur) { return (lbl == null ? lbl = new JLabel(annotation.nom()) : lbl); }

	@Override
	public Component generateComponent(Inspecteur specteur) {
		if (pnl == null){
			pnl = new JPanel();
			pnl.setLayout(new BoxLayout(pnl, BoxLayout.X_AXIS));
			pnl.setBackground(specteur.getBackground());
			
			choix = new JComboBox<Materiaux>(Materiaux.getMateriaux()){
				private static final long serialVersionUID = 6094846851021457280L;

				@Override
				public void setSelectedItem(Object mat){
					Object old = getSelectedItem();
					super.setSelectedItem(mat);
					if (old != mat)
						specteur.fireProprieteChangeEvent(annotation.nom(), old, mat);
				}
			};
			choix.addActionListener(x -> set(specteur.getCible()) );
			pnl.add(choix);
			
			btnPlus = new JButton();
			btnPlus.setMaximumSize(new Dimension(40, 40));
			btnPlus.setIcon(new ImageIcon(Images.safeCharger("plus.png")));
			btnPlus.addActionListener(x -> {
				Materiaux mat = Materiaux.enregistrerMateriaux(new Materiaux("Materiaux " + Materiaux.getNbMateriaux()));
				choix.addItem(mat);
				choix.setSelectedItem(mat);
			});
			pnl.add(btnPlus);
			
			btnMoins = new JButton();
			btnMoins.setMaximumSize(new Dimension(40, 40));
			btnMoins.setIcon(new ImageIcon(Images.safeCharger("moins.png")));
			btnMoins.addActionListener(x -> {
				Materiaux mat = (Materiaux)choix.getSelectedItem();
				choix.setSelectedItem(Materiaux.MAT_DEFAUT);
				if (Materiaux.retirer(mat))
					choix.removeItem(mat);
			});
			pnl.add(btnMoins);
		}
		return pnl;
	}
	
	/**
	 * Retourne la JComboBox contenant les differents materiaux
	 * @return
	 */
	public JComboBox<Materiaux> getComboBox(){ return choix; }
	
}
