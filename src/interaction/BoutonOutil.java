package interaction;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import corps.Outil;

/**
 * Bouton servant a rajouter un outil dans la zone d'interaction
 * @author Leo Jetzer
 *
 */
public class BoutonOutil extends Component {

	/**
	 * Pour la serialisation
	 */
	private static final long serialVersionUID = -217850718327700947L;
	private static final int ARC_WIDTH = 15, ARC_HEIGHT = 15;
	private static final int OFFSET_ICONE = 5, OFFSET_BOUTON = 2;
	private static final Color COULEUR_ACTIF = Color.white, COULEUR_HOVER = new Color(220, 220, 220), COULEUR_PRESSED = Color.lightGray, COULEUR_DISABLED = new Color(230, 230, 230);
	private static final int FLAG_ACTIF = 0b1, FLAG_HOVER = 0b10, FLAG_PRESSED = 0b100;

	private Outil outil;
	private int etat;
	
	private List<ActionListener> listeners;
	
	/**
	 * Creer un nouveau bouton pour l'outil indique
	 * @param outil
	 */
	public BoutonOutil(Outil outil){
		this.outil = outil;
		etat = FLAG_ACTIF;
		BoutonOutil btn = this;
		
		MouseAdapter mouse = new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent e){
				etat = etat | FLAG_HOVER;
				if ((etat & FLAG_ACTIF) == FLAG_ACTIF)
					repaint();
			}
			@Override
			public void mouseExited(MouseEvent e){
				etat = etat & ~FLAG_HOVER;
				if ((etat & FLAG_ACTIF) == FLAG_ACTIF)
					repaint();
			}
			@Override
			public void mousePressed(MouseEvent e){
				etat = etat | FLAG_PRESSED;
				if ((etat & FLAG_ACTIF) == FLAG_ACTIF)
					repaint();
			}
			@Override
			public void mouseReleased(MouseEvent e){
				etat = etat & ~FLAG_PRESSED;
				if ((etat & FLAG_ACTIF) == FLAG_ACTIF){
					repaint();
					if ((etat & FLAG_HOVER) == FLAG_HOVER){
						ActionEvent event = new ActionEvent(btn, ActionEvent.ACTION_PERFORMED, "click");
						for (ActionListener listener : listeners)
							listener.actionPerformed(event);
					}
				}
			}
		};
		this.addMouseListener(mouse);
		
		listeners = new LinkedList<>();
	}

	@Override
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		Shape bouton = new RoundRectangle2D.Double(OFFSET_BOUTON, OFFSET_BOUTON, getWidth()-2*OFFSET_BOUTON, getHeight()-2*OFFSET_BOUTON, ARC_WIDTH, ARC_HEIGHT);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		BufferedImage icone = (outil != null ? outil.getIcone() : null);
		
		if ((etat & FLAG_ACTIF) != FLAG_ACTIF){ // le bouton n'est pas actif (=disabled)
			g2.setColor(COULEUR_DISABLED);
			g2.fill(bouton);
			if (icone != null)
				g2.drawImage(icone, OFFSET_BOUTON+OFFSET_ICONE, OFFSET_BOUTON+OFFSET_ICONE, getWidth()-2*(OFFSET_BOUTON+OFFSET_ICONE), getHeight()-2*(OFFSET_BOUTON+OFFSET_ICONE), null);
		}
		else { // le bouton est actif
			if ((etat & FLAG_HOVER) == FLAG_HOVER){ // la souris se trouve au dessus du bouton
				if ((etat & FLAG_PRESSED) == FLAG_PRESSED) // le bouton est appuye
					g2.setColor(COULEUR_PRESSED);
				else
					g2.setColor(COULEUR_HOVER);
			}
			else
				g2.setColor(COULEUR_ACTIF);
			g2.fill(bouton);
			if (icone != null)
				g2.drawImage(icone, OFFSET_BOUTON+OFFSET_ICONE, OFFSET_BOUTON+OFFSET_ICONE, getWidth()-2*(OFFSET_BOUTON+OFFSET_ICONE), getHeight()-2*(OFFSET_BOUTON+OFFSET_ICONE), null);
			g2.setColor(Color.black);
			g2.draw(bouton);
		}

	}
	
	@Override
	public void setEnabled(boolean val){
		if (val)
			etat = etat | FLAG_ACTIF;
		else
			etat = etat & ~FLAG_ACTIF;
		super.setEnabled(val);
	}

	/**
	 * Assigne un outil au bouton
	 * @param outil - {@code Outil} : le nouvel outil
	 */
	public void setOutil(Outil outil){
		this.outil = outil;
		repaint();
	}
	/**
	 * Retourne l'outil associe au bouton
	 * @return l'outil associe
	 */
	public Outil getOutil(){
		return outil;
	}
	
	/**
	 * Rajoute un {@code ActionListener} au bouton
	 * @param listener - {@code ActionListener} : l'ecouteur a rajouter
	 */
	public void addActionListener(ActionListener listener){
		listeners.add(listener);
	}
	/**
	 * Retire un {@code ActionListener} au bouton
	 * @param listener - {@code ActionListener} : l'ecouteur a retirer
	 */
	public void removeActionListener(ActionListener listener){
		listeners.remove(listener);
	}
	
}
