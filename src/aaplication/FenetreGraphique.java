package aaplication;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import physique.SVector2d;

/**
 * Fenetre charger de presenter trois graphique representant la position, la vitesse et l'acceleration de l'objet selectionne
 * @author Leo Jetzer
 *
 */
public class FenetreGraphique extends JFrame{
	
	private static final long serialVersionUID = 8306113396121180214L;
	private static final int HAUTEUR_GRAPHIQUE_DEFAUT = 200;
	private static final int HAUTEUR_GRAPHIQUE_MINIMAL = 100;
	private static final int LARGEUR_GRAPHIQUE_DEFAUT = 600;
	private static final int LARGEUR_GRAPHIQUE_MINIMAL = 200;
	private static final int GAP = 6;
	private static final Color COLOR_POSX = Color.green.brighter(), COLOR_POSY = Color.cyan, COLOR_VEL = Color.orange, COLOR_ACCEL = Color.blue.darker();

	private Graphique graphPos, graphVel, graphAccel;
	private JLabel lblPos, lblVel, lblAccel;
	
	private static final int nbValeur = 20;
	private double refreshRate = 0.5d;
	
	/**
	 * Creer une nouvelle fenetre de graphique avec le nombre de valeur par defaut
	 */
	public FenetreGraphique(){
		this(nbValeur);
	}
	
	/**
	 * Creer une nouvelle fenetre graphique avec le nombre de valeur indique
	 * @param nbValeurMax - {@code int} : le nombre de valeur affichee par un graphique
	 */
	public FenetreGraphique(int nbValeurMax){
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Graphiques");
		setAlwaysOnTop(true);
		
		graphPos = new Graphique(nbValeurMax, 2, refreshRate).setCouleurTrait(COLOR_POSX, COLOR_POSY).setPas(10).setIntervalleY(4).setAfficherEtiquetteX(false);
		graphVel = new Graphique(nbValeurMax, refreshRate).setCouleurTrait(COLOR_VEL).setAxeY(0).setMinY(-1).setMaxY(29).setPas(10).setIntervalleY(10).setAfficherEtiquetteX(false);
		graphAccel = new Graphique(nbValeurMax, refreshRate).setCouleurTrait(COLOR_ACCEL).setMinY(-1).setMaxY(29).setPas(10).setIntervalleY(5).setAfficherEtiquetteX(false);
		lblPos = new JLabel("Position (m): ");
		lblVel = new JLabel("Vitesse (m/s): ");
		lblAccel = new JLabel("Acceleration (m/s^2): ");
		
		Dimension dim = new Dimension(LARGEUR_GRAPHIQUE_DEFAUT, HAUTEUR_GRAPHIQUE_DEFAUT);
		Dimension min = new Dimension(LARGEUR_GRAPHIQUE_MINIMAL, HAUTEUR_GRAPHIQUE_MINIMAL);
		graphPos.setPreferredSize(dim);
		graphVel.setPreferredSize(dim);
		graphAccel.setPreferredSize(dim);
		graphPos.setMinimumSize(min);
		graphVel.setMinimumSize(min);
		graphAccel.setMinimumSize(min);
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.add(graphPos);
		contentPane.add(graphVel);
		contentPane.add(graphAccel);
		setContentPane(contentPane);
		generateLayout(contentPane);
		
		pack();
	}
	
	/**
	 * Genere le layout de la fenetre
	 * @param contentPane - {@code JPanel} : le JPanel qui aura ce layout
	 */
	private void generateLayout(JPanel contentPane){
		GroupLayout gp = new GroupLayout(contentPane);
		gp.setHorizontalGroup(
			gp.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					gp.createParallelGroup(Alignment.LEADING)
						.addComponent(lblPos)
						.addComponent(graphPos)
						.addComponent(lblVel)
						.addComponent(graphVel)
						.addComponent(lblAccel)
						.addComponent(graphAccel)
				)
				.addContainerGap()
		);
		gp.setVerticalGroup(
			gp.createSequentialGroup()
				.addContainerGap()
				.addComponent(lblPos)
				.addComponent(graphPos)
				.addGap(GAP)
				.addComponent(lblVel)
				.addComponent(graphVel)
				.addGap(GAP)
				.addComponent(lblAccel)
				.addComponent(graphAccel)
				.addContainerGap()
		);
		gp.setHonorsVisibility(false);
		contentPane.setLayout(gp);
	}
	
	/**
	 * Met a jour les trois graphiques avec la position, vitesse et acceleration en parametre
	 * @param pos - {@code SVector2d} : la position
	 * @param vel - {@code SVector2d} : la vitesse
	 * @param accel - {@code SVector2d} : l'acceleration
	 */
	public void push(SVector2d pos, SVector2d vel, SVector2d accel){
		graphPos.push(pos.getX(), pos.getY());
		graphVel.push(vel.modulus());
		graphAccel.push(accel.modulus());
		rp();
	}
	
	/**
	 * Vide les trois graphiques
	 */
	public void clear(){
		graphPos.vider();
		graphVel.vider();
		graphAccel.vider();
		rp();
	}
	
	/**
	 * Change le taux de rafraichissement des graphiques
	 * @param r - {@code double} : le nouveau taux de rafraichissement
	 */
	public void setRefreshRate(double r){
		refreshRate = r;
		graphPos.setIntervalleX(r);
		graphVel.setIntervalleX(r);
		graphAccel.setIntervalleX(r);
		rp();
	}
	
	public Graphique getGraphiquePosition(){ return graphPos; }
	public Graphique getGraphiqueVitesse(){ return graphVel; }
	public Graphique getGraphiqueAcceleration(){ return graphAccel; }
	
	/**
	 * Appelle {@code repaint()} sur les trois graphique
	 */
	private void rp/*repaint*/(){
		graphPos.repaint();
		graphVel.repaint();
		graphAccel.repaint();
	}
}
