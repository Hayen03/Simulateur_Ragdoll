package interaction;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import hayen.event.ColorChangeEvent;
import hayen.event.ColorChangeListener;
import physique.Materiaux;

import javax.swing.JSpinner;
import ui.BoutonCouleur;

/**
 * 
 * Composant personnalisee permettant de modifier graphiquement un materiau
 * @author Leo Jetzer
 */
public class EditeurMateriau extends JPanel {
	private static final long serialVersionUID = 1849317232405070367L;
	
	private static final double MASSE_SURFACIQUE_MAX = 50;
	private static final double CHARGE_SURFACIQUE_MAX = 50;
	private static final double COEFF_FROTTEMENT_MAX = 10;
	private static final double SPINNER_PAS = 0.1;
	
	private JLabel lblNom, lblMasse, lblFrottement, lblCharge, lblCouleur, lblUnitMasse;
	private JSpinner spinMasse, spinFrottement, spinCharge;
	private JTextField txtNom;
	private BoutonCouleur boutonCouleur;
	private JButton btnEnregistrer, btnReinitialiser;
	
	private String cible = "";
	
	private Collection<PropertyChangeListener> listeners = new LinkedList<PropertyChangeListener>();
	
	/**
	 * Creer le nouvel editeur
	 */
	public EditeurMateriau() {
		setMinimumSize(new Dimension(220, 320));
		setPreferredSize(new Dimension(220, 320));
		
		lblNom = new JLabel("Nom:");
		lblMasse = new JLabel("Masse surfacique:");
		lblFrottement = new JLabel("Coefficient de frottement:");
		lblCharge = new JLabel("Charge surfacique:");
		lblCouleur = new JLabel("Couleur:");
		lblUnitMasse = new JLabel("kg/m^2");
		
		ModificationListener mod = new ModificationListener();
		Dimension minimal = new Dimension(0, 0);
		
		spinMasse = new JSpinner(new SpinnerNumberModel(0.1, 0.1, MASSE_SURFACIQUE_MAX, SPINNER_PAS));
		spinMasse.addChangeListener(mod);
		spinMasse.setMinimumSize(minimal);
		spinFrottement = new JSpinner(new SpinnerNumberModel(0, 0, COEFF_FROTTEMENT_MAX, SPINNER_PAS));
		spinFrottement.addChangeListener(mod);
		spinFrottement.setMinimumSize(minimal);
		spinCharge = new JSpinner(new SpinnerNumberModel(0, -CHARGE_SURFACIQUE_MAX, CHARGE_SURFACIQUE_MAX, SPINNER_PAS));
		spinCharge.addChangeListener(mod);
		spinCharge.setMinimumSize(minimal);
		txtNom = new JTextField("");
		txtNom.addActionListener(mod);
		txtNom.setMinimumSize(minimal);
		boutonCouleur = new BoutonCouleur();
		boutonCouleur.addColorChangeListener(mod);
		boutonCouleur.setMinimumSize(minimal);
		
		btnEnregistrer = new JButton("Enregistrer");
		btnEnregistrer.addActionListener(x -> enregistrer());
		btnReinitialiser = new JButton("Reinitialiser");
		btnReinitialiser.addActionListener(x -> charger(cible));
		
		for (Component c : new Component[]{spinMasse, spinFrottement, spinCharge, txtNom, boutonCouleur, btnEnregistrer, btnReinitialiser})
			c.setEnabled(false);
		
		generateLayout();
	}
	/**
	 * Creer le layout du composant
	 */
	private void generateLayout(){
		GroupLayout layout = new GroupLayout(this);
		layout.setHorizontalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblNom, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMasse, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblCharge, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblCouleur, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnEnregistrer)
						.addComponent(lblFrottement))
					.addGap(10)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnReinitialiser)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(Alignment.TRAILING)
								.addComponent(spinCharge, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(spinFrottement, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(spinMasse, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
							.addGap(10)
							.addComponent(lblUnitMasse)
							.addGap(4))
						.addComponent(txtNom, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(boutonCouleur, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblNom)
						.addComponent(txtNom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblMasse)
						.addComponent(spinMasse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblUnitMasse))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFrottement)
						.addComponent(spinFrottement, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(lblCharge)
						.addComponent(spinCharge, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(lblCouleur)
						.addComponent(boutonCouleur, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(layout.createParallelGroup(Alignment.CENTER)
						.addComponent(btnEnregistrer)
						.addComponent(btnReinitialiser))
					.addGap(2))
		);
		layout.setHonorsVisibility(false);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		setLayout(layout);
	}
	
	/**
	 * Charge un materiau dans l'editeur
	 * @param nom - {@code String} : le nom unique du materiau a charger
	 */
	public void charger(String nom){
		Materiaux mat = Materiaux.getMateriau(nom);
		boolean actif = true;
		if (mat != null){
			spinMasse.setValue(mat.getMasseSurfacique());
			spinFrottement.setValue(mat.getFrottement());
			spinCharge.setValue(mat.getChargeSurfacique());
			txtNom.setText(mat.getNom());
			boutonCouleur.setCouleur(mat.getColor());
			cible = nom;
			if (!mat.isEditable())
				actif = false;
		}
		else {
			spinMasse.setValue(0.1);
			spinFrottement.setValue(0);
			spinCharge.setValue(0);
			txtNom.setText("");
			boutonCouleur.setCouleur(Color.white);
			actif = false;
			cible = "";
		}
		for (Component c : new Component[]{spinMasse, spinFrottement, spinCharge, txtNom, boutonCouleur})
			c.setEnabled(actif);
		btnEnregistrer.setEnabled(false);
		btnReinitialiser.setEnabled(false);
	}
	/**
	 * Charge un materiau dans l'editeur
	 * @param mat - {@code Materiaux} : le materiau a charger
	 */
	public void charger(Materiaux mat){
		boolean actif = true;
		if (mat != null){
			spinMasse.setValue(mat.getMasseSurfacique());
			spinFrottement.setValue(mat.getFrottement());
			spinCharge.setValue(mat.getChargeSurfacique());
			txtNom.setText(mat.getNom());
			boutonCouleur.setCouleur(mat.getColor());
			cible = mat.getNom();
			if (!mat.isEditable())
				actif = false;
		}
		else {
			spinMasse.setValue(0.1);
			spinFrottement.setValue(0);
			spinCharge.setValue(0);
			txtNom.setText("");
			boutonCouleur.setCouleur(Color.white);
			actif = false;
			cible = "";
		}
		for (Component c : new Component[]{spinMasse, spinFrottement, spinCharge, txtNom, boutonCouleur})
			c.setEnabled(actif);
		btnEnregistrer.setEnabled(false);
		btnReinitialiser.setEnabled(false);
	}
	/**
	 * Enregistre les modification apportees au materiau
	 */
	public void enregistrer(){
		Materiaux mat = Materiaux.getMateriau(cible);
		if (mat != null){
			mat.setColor(boutonCouleur.getColor());
			mat.setFrottement((Double)spinFrottement.getValue());
			mat.setChargeSurfacique((double)spinCharge.getValue());
			mat.setMasseSurfacique((Double)spinMasse.getValue());
			mat.setNom(txtNom.getText());
		}
		else{
			mat = Materiaux.enregistrerMateriaux(new Materiaux(txtNom.getText())
					.setColor(boutonCouleur.getColor())
					.setFrottement((Double)spinFrottement.getValue())
					.setChargeSurfacique((double)spinCharge.getValue())
					.setMasseSurfacique((Double)spinMasse.getValue())
			);
			cible = txtNom.getText();
		}
		btnEnregistrer.setEnabled(false);
		btnReinitialiser.setEnabled(false);
		fireProprieteChangeEvent("Materiau", mat, mat);
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
	
	/**
	 * Ecouteur multiple
	 * @author Leo Jetzer
	 */
	private class ModificationListener implements ActionListener, ChangeListener, ColorChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) { modif(); }
		@Override
		public void actionPerformed(ActionEvent e) { modif(); }
		@Override
		public void colorChanged(ColorChangeEvent e) { modif(); }
		/**
		 * Methode a invoquer lorsque l'ecouteur recoit un evenement
		 */
		private void modif(){
			btnEnregistrer.setEnabled(true);
			btnReinitialiser.setEnabled(true);
		}
	}
}
