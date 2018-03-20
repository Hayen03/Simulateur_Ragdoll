package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JFrame;

import hayen.event.ColorChangeEvent;
import hayen.event.ColorChangeListener;
import hayen.ui.ColorWheel;
import util.Debug;

/**
 * Petit composant servant a selectionner une couleur facilement
 * @author Leo Jetzer
 */
public class BoutonCouleur extends Component implements FocusListener, WindowListener, MouseListener, ColorChangeListener{
	
	private static final long serialVersionUID = 2059143240868198216L;
	private static final int OFFSET_BOUTON = 2;
	private static final int ARC = 4;
	private static final int WIDTH = 250;
	
	private Color couleur;
	private JFrame frameWheel;
	private ColorWheel colorWheel;
	private Shape bouton;
	private boolean pressed = false;
	
	private Collection<ColorChangeListener> listeners;
	
	/**
	 * Creer un {@code BoutonCouleur} avec {@code Color.white} comme couleur initiale
	 */
	public BoutonCouleur(){
		this(Color.white);
	}
	/**
	 * Creer un {@code BoutonCouleur} avec la couleur indiquee 
	 * @param c - {@code Color} : la couleur initiale du composant
	 */
	public BoutonCouleur(Color c){
		super();
		couleur = c;
		colorWheel = new ColorWheel();
		colorWheel.setPreferredSize(new Dimension(WIDTH, WIDTH));
		colorWheel.setColor(c);
		colorWheel.addColorChangeListener(x -> couleur = x.color);
		listeners = new LinkedList<ColorChangeListener>();
		colorWheel.addColorChangeListener(this);
		addMouseListener(this);
	}
	
	/**
	 * Initialise la partie graphique du bouton
	 */
	private void init(){
		int width = getWidth() - 2*OFFSET_BOUTON;
		int height = getHeight() - 2*OFFSET_BOUTON;
		bouton = new RoundRectangle2D.Double(OFFSET_BOUTON, OFFSET_BOUTON, width, height, ARC, ARC);
	}
	
	/**
	 * Initialise la fenetre de selection de couleur
	 */
	private void initFrame(){
		frameWheel = new JFrame();
		frameWheel.setUndecorated(true);
		frameWheel.getContentPane().add(colorWheel);
		frameWheel.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frameWheel.addFocusListener(this);
		frameWheel.addWindowListener(this);
		frameWheel.setAlwaysOnTop(true);
		frameWheel.pack();
	}
	
	/**
	 * Change la couleur selectionee par ce composant
	 * @param couleur - {@code Color} : la nouvelle couleur
	 */
	public void setCouleur(Color couleur){
		this.couleur = couleur;
		colorWheel.setColor(couleur);
	}
	
	/**
	 * Retourne la couleur selectionee par ce composant
	 * @return la valeur selectionee par ce comosant
	 */
	public Color getColor(){ return couleur; }
	
	// *************** EVENTS STUFF ************** \\
	/**
	 * Envoie un evenement lorsque la couleur du composant change
	 */
	private void fireColorChangeEvent(){
		ColorChangeEvent event = new ColorChangeEvent(this, couleur);
		for (ColorChangeListener listener : listeners)
			listener.colorChanged(event);
	}//TODO
	/**
	 * Rajoute un {@code ColorChangeListener} au composant
	 * @param c - {@code ColorChangeListener} : l'ecouteur a rajouter
	 */
	public void addColorChangeListener(ColorChangeListener c){
		listeners.add(c);
	}
	/**
	 * Retire un {@code ColorChangeListener} du composant
	 * @param c - {@code ColorChangeListener} : l'ecouteur a enlever
	 */
	public void removeColorChangeListener(ColorChangeListener c){
		listeners.remove(c);
	}
	
	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void windowClosing(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) { fireColorChangeEvent(); }
	@Override
	public void windowIconified(WindowEvent e) { frameWheel.dispose(); }
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) { frameWheel.dispose(); }
	
	@Override
	public void focusGained(FocusEvent e) {}
	@Override
	public void focusLost(FocusEvent e) {
		Debug.log("lost focus...");
		frameWheel.setVisible(false);
		frameWheel.dispose();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {
		pressed = true;
		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		pressed = false;
		if (getMousePosition() != null && isEnabled()){
			if (frameWheel == null)
				initFrame();
//			Debug.log(";u;\n");
			frameWheel.setLocation(getLocationOnScreen().x + getWidth(), getLocationOnScreen().y);
			frameWheel.setVisible(true);
		}
		repaint();
	}
	@Override
	public void mouseEntered(MouseEvent e) { repaint(); }
	@Override
	public void mouseExited(MouseEvent e) { repaint(); }
	
	@Override
	public void colorChanged(ColorChangeEvent e) {
		couleur = e.color;
		repaint();
	}
	// *************** EVENTS END *************** \\
	
	// *************** COMPONENT STUFF *************** \\
	@Override
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		if (bouton == null)
			init();
		Color c1, c2;
		if (!isEnabled()){ // bouton desactive
			c1 = couleur.brighter();
			c2 = Color.gray;
		}
		else {
			if (this.getMousePosition() != null && pressed)
				c1 = couleur.darker();
			else
				c1 = couleur;
			c2 = Color.black;
		}
		g.setColor(c1);
		g2.fill(bouton);
		g.setColor(c2);
		g2.draw(bouton);
	}
	@Override
	public void setSize(int w, int h){
		super.setSize(w, h);
		bouton = null;
	}
	@Override
	public void setBounds(int x, int y, int w, int h){
		super.setBounds(x, y, w, h);
		bouton = null;
	}
	// *************** COMPONENT END *************** \\
}
