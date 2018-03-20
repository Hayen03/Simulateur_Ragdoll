package interaction;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.JPanel;

import annotation.Editable;
import corps.Aimant;
import corps.Balle;
import corps.Boite;
import corps.Corde;
import corps.Corps;
import corps.Mur;
import corps.Personnage;
import event.ObjetSelectionneEvent;
import event.ObjetSelectionneListener;
import inspecteur.PropReel;
import physique.ModelePhysique;
import physique.SVector2d;
import util.Images;

/**
 * Zone d'interaction physique entre plusieurs corps. C'est ici que tout le plaisir ce passe
 * @author Leo Jetzer (pour les trucs de selection) et Marcus Phan (pour tout le reste)
 *
 */
@Editable(nom="Gravite", get="getG", set="setG", type=PropReel.class)
@Editable(nom="Dimension", get="getLargeurMonde", set="setDimCarre", type=PropReel.class)
public class ZoneInteraction extends JPanel implements Runnable {

	public static final int DROIT = 0,
							HAUT = 1,
							GAUCHE = 2,
							BAS = 3;
	
	private static final long serialVersionUID = 1L;
	private ModelePhysique modele;
	
	public static final double MAX_Gravite = 50, MAX_Dimension = 50, MIN_Gravite = -50, MIN_Dimension = 0.1, DEFAUT_Gravite = 5, DEFAUT_Dimension = 25;

	/* ******~CONSTANTES~*******/

	private double largeurMonde = 25;
	private double hauteurMonde = 25;
	private double g = 5;
	private double deltaT = 0.01;
	
	private final double largeurMondeMax = 50;
	private final double hauteurMondeMax = 50;


	private Thread processus = null;
	private boolean running = false;

	private boolean premiereFois = true;

	private Personnage ragdoll;
	private Corde testC;
	private Hashtable<String, Corps> aDessiner = new Hashtable<String, Corps>();
	private Hashtable<String, Balle> toutesLesBalles = new Hashtable<String, Balle>();
	private Hashtable<String, Boite> toutesLesBoites = new Hashtable<String, Boite>(); 
	private Hashtable<String, Mur> murs = new Hashtable<String, Mur>();
	private Collection<Corde> cordes = new LinkedList<Corde>();
	private final Mur[] _murs = new Mur[4];
	
	// Trucs en rapport avec la selection \\
	// note pour developpeur: toujours utiliser le setter pour modifier la valeur, ca evite bien du trouble
	private Corps selection = null;
	private boolean dragging = false;
	private double dragDampening = 0.4;
	private LinkedList<ObjetSelectionneListener> objSelListen;
	private Color couleurSelection = Color.green;
	private Stroke strokeSelection = new BasicStroke(3);

	// Truc pour rendre la zone jolie \\
	private BufferedImage fond;
	
	/**
	 * Construit une zone d'int�raction poss�dant les param�tres par d�faut.
	 * 	 */
	public ZoneInteraction() {



		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(250,250));
		setLayout(null);
		ragdoll = new Personnage(this);
		_murs[DROIT] = new Mur("MurDroite", new SVector2d(largeurMonde/2+0.5,0),1,hauteurMonde,this,0,new SVector2d(-1,0));
		_murs[HAUT] = new Mur("MurHaut", new SVector2d(0, -hauteurMonde/2 -0.5),largeurMonde,1,this,0,new SVector2d(0,1));
		_murs[GAUCHE] = new Mur("MurGauche", new SVector2d(-largeurMonde/2 -0.5,0), 1,hauteurMonde, this,0, new SVector2d(1,0));
		_murs[BAS] = new Mur("MurBas", new SVector2d(0, hauteurMonde/2 +0.5),largeurMonde,1,this,0,new SVector2d(0,-1));
		murs.put("LEFTWALL", _murs[GAUCHE]);
		murs.put("RIGHTWALL", _murs[DROIT]);
		murs.put("TOPWALL", _murs[HAUT]);
		murs.put("BOTWALL", _murs[BAS]);		
		aDessiner.putAll(murs);
		//BALLES TESTS
		
		//BALLES TESTS FIN

		for (Corps partieDeCorpsCourant : ragdoll.getLeCorps()) {
			aDessiner.put(partieDeCorpsCourant.getNom(), partieDeCorpsCourant);
		}

		for (Corps courant : aDessiner.values()) {
			if (courant.getClass().equals(Balle.class)) {
				toutesLesBalles.put(courant.getNom(), (Balle) courant);
			}

			if (courant.getClass().equals(Boite.class)) {
				toutesLesBoites.put(courant.getNom(), (Boite) courant);
			}
		}
		cordes.addAll(ragdoll.getLigaments());



		// trucs de selection \\
		objSelListen = new LinkedList<>();
		MouseAdapter ma = new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent event){
				Corps[] objs = new Corps[aDessiner.size()];
				aDessiner.values().toArray(objs);
				for (int i = objs.length-1; i >= 0; i--) // on y va a l'envers parce que les objet dessiner sur le dessus sont les derniers de la liste
					if (objs[i].getAire().contains(event.getPoint())){
						setSelection(objs[i]);
						selection.setFixe(true);
						return;
					}
				setSelection(null);
			}
			@Override
			public void mouseReleased(MouseEvent event){
				if (selection != null){
					selection.setFixe(false);
					dragging = false;
				}
			}
			@Override
			public void mouseDragged(MouseEvent event){
				if (selection != null)
					dragging = true;
			}
		};
		this.addMouseListener(ma);
		this.addMouseMotionListener(ma);

		try {
			fond = Images.charger("paysage2.png");
		} catch (IOException ex){}
		
	}

	/**
	 * Construit une zone d'int�raction personnalis�e avec des param�tres sp�cifiques.
	 * @param largeurMonde La largeur du monde
	 * @param hauteurMonde La hauteur du monde
	 * @param g La gravit� qui r�gne dans ce monde
	 * @param deltaT La variation de temps � utiliser dans les calculs d'it�ration physique.
	 */
	public ZoneInteraction(double largeurMonde, double hauteurMonde, double g, double deltaT) {

		this.largeurMonde = largeurMonde;
		this.hauteurMonde = hauteurMonde;
		this.g = g;
		this.deltaT = deltaT;

		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(900,762));
		setLayout(null);
		ragdoll = new Personnage(this);

	}

	/**
	 * Dessine tous les �l�ments pr�sent dans la zone d'int�raction dans un contexte graphique.
	 * @param g Le contexte graphique
	 */
	public void paintComponent(Graphics g) {

		if (premiereFois){
			modele = new ModelePhysique(getWidth(), getHeight(), largeurMonde/2, hauteurMonde/2, largeurMonde, hauteurMonde);
			premiereFois = false;
		}
			
		AffineTransform matMC = modele.getMatMondeVersComposant();


		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (fond != null)
			g2d.drawImage(fond, 0, 0, getWidth(), getHeight(), null);
		
		for (Corps courant : aDessiner.values()) {
			courant.dessiner(g2d, matMC);
			if (courant == selection){
				g2d.setColor(couleurSelection);
				Stroke buffer = g2d.getStroke();
				g2d.setStroke(strokeSelection);
				g2d.draw(courant.getAire());
				g2d.setStroke(buffer);
			}
		}

		/*for (Corde courant : ragdoll.getLigaments()) {
			courant.dessiner(g2d, matMC);
		}
*/


		if (testC != null)
			testC.dessiner(g2d, matMC);






	}


	@Override
	/**
	 * Cette m�thode est appel�e par les <code>Thread</code> vivants.
	 */
	public void run() {

		while (processus != null) {
			if (running){
			try {
				for (Corps courant : aDessiner.values()) {
					courant.prochaineIterationPhysique();
					
				
					
				}
				for (Corde corde : cordes)
					corde.appliquerForce();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}

			// dragging
			if (dragging){
				Point2D mousePos = MouseInfo.getPointerInfo().getLocation(), componentPos = this.getLocationOnScreen();
				Point2D point = modele.getMatComposantVersMonde().transform(new Point2D.Double(mousePos.getX() - componentPos.getX(), mousePos.getY() - componentPos.getY()), null);
				SVector2d pointVec = new SVector2d(point.getX(), point.getY());
				SVector2d posOrig = selection.getPosition();
				selection.setPosition(pointVec);
				selection.collisionAvecMur();
				selection.setVitesse(selection.getPosition().substract(posOrig).multiply(1/deltaT * dragDampening));
			}

			repaint();

			try {
				Thread.sleep((long) (1000*deltaT));
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}

		}//fin while

	}

	/**
	 * D�marre l'animation.
	 */
	public void demarrer() {

		running = true;
		if (processus == null) {
			processus = new Thread(this);
			processus.start();
			System.out.println("Animation d�mar�e...");
		}

	}

	/**
	 * Arr�te l'animation
	 */
	public void arreter () {
		running = false;
	}
	/**
	 * Tue le thread et arrete completement la simulation
	 */
	public void arretComplet(){
		running = false;
		processus = null;
	}
	
	/**
	 * Red�marre l'animation
	 * (M�thode temporaire pour des fins de tests. S'il y a une fonction semblable dans la version
	 * finale, elle ne sera pas impl�ment�e de cette fa�on statique)
	 */
	public void redemarrer () {
		arreter();
		ragdoll = new Personnage(this);
		//BALLES TESTS
		aDessiner.put("TEST", new Balle("Balle1", new SVector2d(0,5),  2, this));
		aDessiner.put("TEST2", new Balle("Balle2", new SVector2d(5,0),  2, this));
		aDessiner.put("TEST3", new Boite("Boite1", new SVector2d(-7,-7), 1, 3, this, 0));
		aDessiner.put("TEST4", new Boite("Boite2", new SVector2d(7,7), 3, 1, this, 0));
		//BALLES TESTS FIN

		for (Corps partieDeCorpsCourant : ragdoll.getLeCorps()) {
			aDessiner.put(partieDeCorpsCourant.getNom(), partieDeCorpsCourant);
		}

		for (Corps courant : aDessiner.values()) {
			if (courant.getClass().equals(Balle.class)) {
				toutesLesBalles.put(courant.getNom(), (Balle) courant);
			}

			if (courant.getClass().equals(Boite.class)) {
				toutesLesBoites.put(courant.getNom(), (Boite) courant);
			}
		}
		testC = aDessiner.get("TEST").attacher(new Corde[1], aDessiner.get("TEST2"))[0];

		repaint();


	}

	/**
	 * Attache deux corps ensemble et enregistre la corde
	 * @param a - {@code Corps} : le premier corps
	 * @param b - {@code Corps} : le deuxieme corps
	 * @param l - {@code double} : la longueur de la corde
	 * @param t - {@code double} : la tension de la corde
	 * @return la corde creer
	 */
	public Corde attacher(Corps a, Corps b, double l, double t){
		Corde corde = new Corde(a, b, l, t);
		cordes.add(corde);
		return corde;
	}

	/**
	 * TEMPORAIRE
	 * @return La premi�re Balle TEST. Voir key Balle1 dans le Hashtable.
	 */


	/**
	 * Retourne un <code>Hashtable</code> contenant tous les corps � �tre dessiner dans le contexte
	 * graphique.
	 * @return Un <code>Hashtable</code> contenant tous les corps � �tre dessiner dans le contexte
	 * graphique
	 */
	public synchronized Hashtable<String, Corps> getADessiner () {
		return aDessiner;

	}

	/**
	 * Retourne un <code>Hashtable</code> contenant toutes les balles pr�sentes dans l'environnement physique
	 * @return Un <code>Hashtable</code> contenant toutes les balles pr�sentes dans l'environnement physique.
	 */
	public Hashtable<String, Balle> getToutesLesBalles() {
		return toutesLesBalles;
	}

	/**
	 * Retourne la largeur de l'environnement physique.
	 * @return La largeur de l'environnement physique.
	 */
	public double getLargeurMonde() {
		return largeurMonde;
	}

	/**
	 * Modifie la largeur de l'environnement physique.
	 * @param largeurMonde La nouvelle largeur.
	 */
	public void setLargeurMonde(double largeurMonde) {
		Dimension old = new Dimension((int)Math.ceil(largeurMonde), (int)Math.ceil(hauteurMonde));
		
		if (largeurMonde <= largeurMondeMax) {
		this.largeurMonde = largeurMonde;
		}
		
		else {
			this.largeurMonde = largeurMondeMax;
		}
		aDessiner.remove("LEFTWALL");
		aDessiner.remove("RIGHTWALL");
		aDessiner.remove("TOPWALL");
		aDessiner.remove("BOTWALL");
		_murs[DROIT] = new Mur("MurDroite", new SVector2d(largeurMonde/2+0.5,0),1,hauteurMonde,this,0,new SVector2d(-1,0));
		_murs[HAUT] = new Mur("MurHaut", new SVector2d(0, -hauteurMonde/2 -0.5),largeurMonde,1,this,0,new SVector2d(0,1));
		_murs[GAUCHE] = new Mur("MurGauche", new SVector2d(-largeurMonde/2 -0.5,0), 1,hauteurMonde, this,0, new SVector2d(1,0));
		_murs[BAS] = new Mur("MurBas", new SVector2d(0, hauteurMonde/2 +0.5),largeurMonde,1,this,0,new SVector2d(0,-1));
		murs.put("LEFTWALL", _murs[GAUCHE]);
		murs.put("RIGHTWALL", _murs[DROIT]);
		murs.put("TOPWALL", _murs[HAUT]);
		murs.put("BOTWALL", _murs[BAS]);	
		
		aDessiner.putAll(murs);
		premiereFois = true;
		
		Dimension nouveau = new Dimension((int)Math.ceil(largeurMonde), (int)Math.ceil(hauteurMonde));
		this.firePropertyChange("size", old, nouveau);
	}

	/**
	 * Retourne la hauteur de l'environnement physique.
	 * @return La hauteur de l'environnement physique.
	 */
	public double getHauteurMonde() {
		return hauteurMonde;
	}

	/**
	 * Modifie la hauteur de l'environnement physique.
	 * @param hauteurMonde La nouvelle hauteur.
	 */
	public void setHauteurMonde(double hauteurMonde) {
		Dimension old = new Dimension((int)Math.ceil(largeurMonde), (int)Math.ceil(hauteurMonde));
		
		if (hauteurMonde <= hauteurMondeMax) {
		this.hauteurMonde = hauteurMonde;
		}
		
		else {
			this.hauteurMonde = hauteurMondeMax;
		}
		
		aDessiner.remove("LEFTWALL");
		aDessiner.remove("RIGHTWALL");
		aDessiner.remove("TOPWALL");
		aDessiner.remove("BOTWALL");
		_murs[DROIT] = new Mur("MurDroite", new SVector2d(largeurMonde/2+0.5,0),1,hauteurMonde,this,0,new SVector2d(-1,0));
		_murs[HAUT] = new Mur("MurHaut", new SVector2d(0, -hauteurMonde/2 -0.5),largeurMonde,1,this,0,new SVector2d(0,1));
		_murs[GAUCHE] = new Mur("MurGauche", new SVector2d(-largeurMonde/2 -0.5,0), 1,hauteurMonde, this,0, new SVector2d(1,0));
		_murs[BAS] = new Mur("MurBas", new SVector2d(0, hauteurMonde/2 +0.5),largeurMonde,1,this,0,new SVector2d(0,-1));
		murs.put("LEFTWALL", _murs[GAUCHE]);
		murs.put("RIGHTWALL", _murs[DROIT]);
		murs.put("TOPWALL", _murs[HAUT]);
		murs.put("BOTWALL", _murs[BAS]);			
		
		aDessiner.putAll(murs);
		
		premiereFois = true;
		
		Dimension nouveau = new Dimension((int)Math.ceil(largeurMonde), (int)Math.ceil(hauteurMonde));
		this.firePropertyChange("size", old, nouveau);
	}
	
	/**
	 * Modifie la dimension du monde de sorte qu'il soit carre
	 * @param dim La dimension
	 */
	public void setDimCarre (double dim) {
		setLargeurMonde(dim);
		setHauteurMonde(dim);
	}

	/**
	 * Retourne la gravit� de l'environnement physique.
	 * @return La gravit� de l'environnement physique.
	 */
	public double getG() {
		return g;
	}

	/**
	 * Modifie la gravit� de l'environnement physique (C'EST MAGIQUE)
	 * @param g La nouvelle gravit�.
	 */
	public void setG(double g) {
		this.g = g;
	}


	/**
	 * Retourne le <code>Personnage</code> unique pr�sent dans l'environnement physique.
	 * @return Le <code>Personnage</code> unique pr�sent dans l'environnement physique.
	 */
	public Personnage getRagdoll() {
		return ragdoll;
	}

	/**
	 * � NE PAS UTILISER POUR L'INSTANT, CAR UN SEUL TYPE DE PERSONNAGE EST POSSIBLE.
	 * @param ragdoll Le personnage unique
	 */
	public void setRagdoll(Personnage ragdoll) {
		this.ragdoll = ragdoll;
	}

	// trucs de selection \\
	/**
	 * Retourne l'objet qui est presentement selectionne dans cette {@code ZoneInteraction}
	 * @return l'objet selectionne
	 */
	public Corps getSelection(){
		return selection;
	}
	// note pour les developpeurs: toujours utiliser cette methode pour changer la selection, ca evite bien du trouble
	/**
	 * Change l'objet presentement selectionner par cette {@code ZoneInteraction}
	 * @param selection - {@link corps.Corps} : la nouvelle selection
	 */
	public void setSelection(Corps selection){
		this.selection = selection;
		ObjetSelectionneEvent event = new ObjetSelectionneEvent(selection);
		for (ObjetSelectionneListener listener : objSelListen)
			listener.objetSelectionne(event);
		repaint();
	}
	/**
	 * Ajoute un ecouteur qui reagit lorsque la selection change
	 * @param listener - {@link event.ObjetSelectionneListener} : l'ecouteur a ajouter
	 */
	public void addObjetSelectionneListener(ObjetSelectionneListener listener){
		objSelListen.add(listener);
	}

	/**
	 * Retourne la variation de temps � utiliser dans les calculs physiques.
	 * @return La variation de temps � utiliser dans les calculs physiques
	 */
	public double getDeltaT() {
		// TODO Auto-generated method stub
		return deltaT;
	}

	/**
	 * Retourne toutes les boites pr�sentes dans l'environnement physique
	 * @return Toutes les bo�tes pr�sentes dans l'environnement physique
	 */
	public Hashtable<String, Boite> getToutesLesBoites() {
		return toutesLesBoites;
	}

	/**
	 * 
	 * @param toutesLesBoites
	 */
	public void setToutesLesBoites(Hashtable<String, Boite> toutesLesBoites) {
		this.toutesLesBoites = toutesLesBoites;
	}

	/**
	 * Ajoute un objet a la zone d'interaction
	 * @param cle - {@code String} : nom qui identifiera l'objt
	 * @param corps - {@code Corps} : le corps a rajouter
	 */
	public void ajouter(String cle, Corps corps){
		aDessiner.put(cle, corps);
		if (corps instanceof Balle){
			toutesLesBalles.put(cle, (Balle)corps);
		}
		else if (corps instanceof Boite){
			toutesLesBoites.put(cle, (Boite)corps);
		}
	}
	/**
	 * Supprime l'objet selectionne
	 */
	public void supprimerSelection(){
		if (selection != null){
			System.out.println("Tentative d'enlevement (" + selection.getNom() + ") -> " + aDessiner.containsKey(selection.getNom()));
			aDessiner.values().remove(selection);
			if (selection.getClass().equals(Balle.class)){
				toutesLesBalles.values().remove(selection);
			}
			
			else if (selection.getClass().equals(Aimant.class)) {
				toutesLesBalles.values().remove(selection);
			}
			else if (selection.getClass().equals(Boite.class)){
				toutesLesBoites.values().remove(selection);
			}
			cordes.removeAll(selection.getCordesAttachees());
			if (selection.getCordesAttachees().contains(testC))
				testC = null;
			// enlever les cordes
			selection.detacherTout();
			selection.setOuSuisJe(null);
			setSelection(null);
		}
	}

	/**
	 * Change l'image de fond
	 * @param f - {@code BufferedImage} : la nouvelle image de fond
	 */
	public void setImageFond(BufferedImage f){
		fond = f;
	}
	
	/**
	 * Retourne l'image de fond
	 * @return l'image de fond
	 */
	public BufferedImage getImageFond(){
		return fond;
	}
	
	// *************** TRUCS DES CLASSES PARENTES *************** \\
	@Override
	public void setSize(int w, int h){
		super.setSize(w, h);
		modele = new ModelePhysique(w, h, largeurMonde/2, hauteurMonde/2, largeurMonde, hauteurMonde);
		
	}
	@Override
	public void setBounds(int x, int y, int w, int h){
		super.setBounds(x, y, w, h);
		modele = new ModelePhysique(w, h, largeurMonde/2, hauteurMonde/2, largeurMonde, hauteurMonde);
		
	}
}
