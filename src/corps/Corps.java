package corps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import annotation.Editable;
import interaction.Dessinable;
import interaction.ZoneInteraction;
import inspecteur.*;
import physique.Materiaux;
import physique.SVector2d;
import util.MathUtil;

/**
 * Classe representant un corps pouvant reagir aux forces qui lui sont exercees et qui interagit avec d'autres <code>Corps</code>
 * @author Marcus Phan
 */

@Editable(nom=Corps.PROPRIETE_MATERIAU, get="getMateriau", set="setMateriau", type=PropMateriaux.class)
@Editable(nom=Corps.PROPRIETE_NOM, get="getNom", set="setNom", type=PropTexte.class)
@Editable(nom=Corps.PROPRIETE_MASSE, get="getMasse", type=PropMessage.class)
@Editable(nom=Corps.PROPRIETE_POSITION, get="getShortPosition", set="setPosition", type=PropMessage.class)
@Editable(nom=Corps.PROPRIETE_VITESSE, get="getShortVitesse", set="setVitesse", type=PropMessage.class)
@Editable(nom=Corps.PROPRIETE_ACCELERATION, get="getShortAcceleration", set="setAcceleration", type=PropMessage.class)
@Editable(nom=Corps.PROPRIETE_STATIQUE, get="isStatique", set="setStatique", type=PropBooleen.class)
@Editable(nom=Corps.PROPRIETE_COEFFRESTI, get="getcRestitution", set="setcRestitution", type=PropReel.class, id="RESTITUTION")
public abstract class Corps implements Dessinable {
	
	public static final String PROPRIETE_MASSE = "Masse";
	public static final String PROPRIETE_NOM = "Nom";
	public static final String PROPRIETE_MATERIAU = "Materiau";
	public static final String PROPRIETE_POSITION = "Position";
	public static final String PROPRIETE_VITESSE = "Vitesse";
	public static final String PROPRIETE_ACCELERATION = "Acceleration";
	public static final String PROPRIETE_STATIQUE = "Statique";
	public static final String PROPRIETE_COEFFRESTI = "Coeff. restitution";
	
	public static final double MIN_RESTITUTION = 0;
	public static final double MAX_RESTITUTION = 1;
	public static final double DEFAUT_RESTITUTION = 1;
	
	public static final double VITESSE_MAX_VALUE = 100; // m/s
	
	protected String nom;

	// liste des cordes attacher a cet objet
	protected LinkedList<Corde> cordes;
	
	protected SVector2d position;
	protected SVector2d vitesse;
	protected SVector2d acceleration;
	protected SVector2d force;
	
	protected ZoneInteraction ouSuisJe;

	protected Shape forme;
	protected Area aire;
	protected Point2D.Double centerPoint;
	
	private Materiaux materiau;
	
	private boolean statique = false, fixe = false;
	/**
	 * Cette variable definit la distance sur laquelle deux balles seront pousses si jamais leurs aires
	 * s'intersectent.
	 */

	protected static boolean drawVectors = false;
	protected final double PUSHER_STRENGTH = 0.01;
	protected final double SPEED_LIMIT = 1;
	
	protected final double COEFFICIENT_REBONDISSEMENT_MUR = 0.9;
	
	/**
	 * Coefficient de restitution
	 */
	private double cRestitution;
	/**
	 * Ce constructeur permet de construire un <code>Corps</code> qui aura le nom passe en parametre et la masse passee en parametre.
	 * Le vecteur position de ce <code>Corps</code> correspond e son point central. Sa forme correspondra e la <code>Shape</code>
	 * passee en parametre. La <code>ZoneInteraction</code> passee en parametre permettra au <code>Corps</code> d'acceder e des informations
	 * relatifs e l'environnement physique dans lequel il se retrouve.
	 * @param nom Le nom
	 * @param position Le vecteur representant le point central
	 * @param forme La forme
	 * @param ouSuisJe La ZoneInteraction ou se retrouvera le <code>Corps</code>.
	 */
	public Corps (String nom, SVector2d position, Shape forme, ZoneInteraction ouSuisJe) {
		
		this.nom = nom;
		this.position = position;
		this.vitesse = new SVector2d(0,0);
		this.acceleration = new SVector2d(0,0);
		this.force = new SVector2d(0,0);
		this.forme = forme;
		this.aire = new Area(forme);
		this.ouSuisJe = ouSuisJe;
		centerPoint = computeCenter();
		this.cRestitution = 1;
		this.materiau = Materiaux.getMateriau(Materiaux.NOM_DEFAUT);
		this.cordes = new LinkedList<Corde>();
	}
	
	/**
	 * 	 * Ce constructeur permet de construire un <code>Corps</code> qui aura le nom passe en parametre et la masse passee en parametre.
	 * Le vecteur position de ce <code>Corps</code> correspond e son point central. Sa forme correspondra e la <code>Shape</code>
	 * passee en parametre. La <code>ZoneInteraction</code> passee en parametre permettra au <code>Corps</code> d'acceder e des informations
	 * relatifs e l'environnement physique dans lequel il se retrouve.
	 * @param nom Le nom
	 * @param position Le vecteur representant le point central
	 * @param forme La forme
	 * @param ouSuisJe La ZoneInteraction ou se retrouvera le <code>Corps</code>.
	 * @param cRest Le coefficient de restitution, variable utilisee dans le calcul des collisions.
	 */
	
	public Corps (String nom, SVector2d position, Shape forme, ZoneInteraction ouSuisJe, double cRest) {
		
		this.nom = nom;
		this.position = position;
		this.vitesse = new SVector2d(0,0);
		this.acceleration = new SVector2d(0,0);
		this.force = new SVector2d(0,0);
		this.forme = forme;
		this.aire = new Area(forme);
		this.ouSuisJe = ouSuisJe;
		centerPoint = computeCenter();
		this.cRestitution = cRest;
		this.materiau = Materiaux.getMateriau(Materiaux.NOM_DEFAUT);
		this.cordes = new LinkedList<Corde>();
	}
	
	
	/**
	 * Auteur : Marco13 (StackOverflow)
	 * http://stackoverflow.com/questions/21973875/java-pathiterator-how-do-i-accurately-calculate-center-of-shape-object
	 * Retourne le point central du <code>Corps</code>. Le point central est defini comme etant le point situe e 1/2 la largeur et 1/2
	 * la hauteur du <code>Corps</code>.
	 * @return Le point central du <code>Corps</code>.
	 */
    public Point2D.Double computeCenter()
    {

        final double flatness = 0.1;
        PathIterator pi = forme.getPathIterator(null, flatness);
        double coords[] = new double[6];
        double sumX = 0;
        double sumY = 0;
        int numPoints = 0;
        while (!pi.isDone())
        {
            int s = pi.currentSegment(coords);
            switch (s)
            {
                case PathIterator.SEG_MOVETO:
                    // Ignore
                    break;

                case PathIterator.SEG_LINETO:
                    sumX += coords[0]; 
                    sumY += coords[1]; 
                    numPoints++;
                    break;

                case PathIterator.SEG_CLOSE:
                    // Ignore
                    break;

                case PathIterator.SEG_QUADTO:
                    throw new AssertionError(
                        "SEG_QUADTO in flattening path iterator");
                case PathIterator.SEG_CUBICTO:
                    throw new AssertionError(
                        "SEG_CUBICTO in flattening path iterator");
            }
            pi.next();
        }
        double x = sumX / numPoints;
        double y = sumY / numPoints;
        return new Point2D.Double(x,y);
    }
    
    /**
     * Dessine le <code>Corps</code> dans le contexte graphique. Elle aura une echelle determinee par
     * la matrice de transformation monde-composant passee en parametre.
     * @param g2d Le contexte graphique ou se dessinera le <code>Corps</code>
     */
	public void dessiner(Graphics2D g2d, AffineTransform matMC) {

	}
	
	/**
	 * Additionne le vecteur passe en parametre au vecteur position du <code>Corps</code>.
	 * @param deplacement Le vecteur deplacement
	 */
	public void deplacer(SVector2d deplacement) {
		//TODO
		setPosition(position.add(deplacement));
	}
	
	/**
	 * Applique un vecteur force sur un corps (sommatif avec les autres vecteurs forces).
	 * @param force Le vecteur force e appliquer
	 */
	
	public void appliquerForce (SVector2d force) {
		setForce(this.force.add(force));
	}
	
	/**
	 * Calcule un nouveau vecteur acceleration pour le <code>Corps</code> selon le vecteur force
	 * qui agit presentement dessus.
	 * 
	 */
	public void accelSelonForce() {
		double masse = getMasse();
		if (masse != 0){
			SVector2d newAccel = new SVector2d(force.getX() / masse, force.getY() / masse);
			newAccel = newAccel.add(new SVector2d (0, ouSuisJe.getG()));
			setAcceleration(newAccel);
		}
	}
	/**
	 * Calcule un nouveau vecteur vitesse pour le <code>Corps</code> selon son vecteur acceleration
	 * et la variation de temps passee en parametre.
	 * @param deltaT La variation de temps
	 */
	public void vitesseSelonAccel (double deltaT) {
		setVitesse(vitesse.add(acceleration.multiply(deltaT)));
	}
	
	/**
	 * Calcule un nouveau vecteur position pour le <code>Corps</code> selon son vecteur vitesse
	 * et la variation de temps passee en parametre.
	 * @param deltaT La variation de temps
	 */
	public void positionSelonVitesse (double deltaT) {
		setPosition(position.add(vitesse.multiply(deltaT)));
	}
	
	/**
	 * Calcule la prochaine iteration physique du <code>Corps</code> selon l'algorithme d'Euler et detecte
	 * les collisions avec les parois de l'environnement physique.
	 * Cette methode, pour la classe <code>Corps</code>, est incomplete.
	
	 */
	public synchronized void prochaineIterationPhysique () {
		if (!isStatique()){
			euler();
			collisionAvecMur();
			gererCollisions();
		}
		force = new SVector2d();
	}

	/**
	 * Deplace deux Corps dans la direction du vecteur specifie, dans deux sens opposes.
	 * 
	 * @param prochain Le deuxieme Corps
	 * @param normale le vecteur
	 */
		
	protected void pushOut(Corps prochain, SVector2d normale) {
		SVector2d nT = new SVector2d(normale.getX(), normale.getY());
		nT = normale.normalize();
		SVector2d pusher = nT.multiply(PUSHER_STRENGTH);
		if (!MathUtil.nearlyEquals(this.getPosition().getX(), prochain.position.getX()) && 
				!MathUtil.nearlyEquals(this.getPosition().getY(), prochain.position.getY())) {
				this.setPosition(this.position.substract(pusher));
			if (!prochain.isStatique())
				prochain.setPosition(prochain.position.add(pusher));
		}
		else {
				this.setPosition(this.position.substract(new SVector2d(pusher.getX() - PUSHER_STRENGTH, pusher.getY() - PUSHER_STRENGTH)));
			if (!prochain.isStatique())
				prochain.setPosition(prochain.position.add(new SVector2d(pusher.getX() - PUSHER_STRENGTH, pusher.getY() - PUSHER_STRENGTH)));
		}
	}

	// TODO: utiliser les nouveaux murs?
	/**
	 * Cette methode detecte la collision entre une paroi de l'environnement physique et le <code>Corps</code>.
	 * S'il y a collision, cette methode ajuste la position et la vitesse du <code>Corps</code> de faeon e simuler
	 * un rebondissement.
	 * 
	 */
	public abstract void collisionAvecMur();

	/**
	 * Cette methode appelle les methodes calculant l'acceleration selon la force, la vitesse selon l'acceleration
	 * et la position selon la vitesse d'un <code>Corps</code>. (Algorithme d'Euler)

	 */
	protected void euler() {
		accelSelonForce();
		vitesseSelonAccel(ouSuisJe.getDeltaT());
		positionSelonVitesse(ouSuisJe.getDeltaT());
	}
	

	
	/**
	 * Gere une collision
	 */
	public abstract void gererCollisions ();
	
	/**
	 * Retourne si un Corps entre en collision avec un autre Corps
	 * @param prochain l'autre Corps
	 * @return true s'il y a collision, false sinon
	 */
	public boolean collidesWith (Corps prochain) {
		if (this.getClass().equals(Balle.class)) {
			Balle balle = (Balle) this;
			if (prochain.getClass().equals(Balle.class)) {
				//BALLE-BALLE
				return balle.collidesWith((Balle) prochain);
			}
			
			else {
				//BALLE-BOITE
				
				return balle.collidesWith((Boite) prochain);
			}
		}
		
		else {
			Boite boite = (Boite) this;
			if (prochain.getClass().equals(Balle.class)) {
				//BOITE-BALLE
				return ((Balle)prochain).collidesWith(boite);
				
			}
			
			else {
				//BOITE-BOITE
				return ((Balle)prochain).collidesWith(boite);
			}
		}

		
	}
	
	/**
	 * Collision avec aires...
	 * @param autre : l'autre corps
	 * @param matMC : la matrice de transformation monde vers composant
	 * @return true s'il y a collision, sinon false
	 */
	public boolean collidesWith(Corps autre, AffineTransform matMC) {
		
		Area tempArea = new Area(this.aire);
		Area autreArea = new Area(autre.aire);
		tempArea.intersect(autreArea);
		return !tempArea.isEmpty() && !this.equals(autre);
	}	

	/**
	 * Calcule l'impulsion entre deux <code>Corps</code> en collision elastique selon la normale e leur surface.
	 * @param autre L'autre <code>Corps</code>
	 * @param normale La normale e leur surface
	 * @return l'impulsion entre les deux <code>Corps</code> en joules.
	 */
	public double calculerImpulsion (Corps autre, SVector2d normale) {
	
	
		
		
		
	if (!this.isStatique() && !autre.isStatique()) {	
		if (this.getMasse() == 0 ) {
			SVector2d t = (this.getVitesse().substract(autre.getVitesse())).multiply((-(1 + (this.cRestitution + autre.cRestitution) / 2) / 1/autre.getMasse()));	
			double j = t.dot(normale);
			return j;
		}
		
		else if (autre.getMasse() == 0) {
			SVector2d t = (this.getVitesse().substract(autre.getVitesse())).multiply((-(1 + (this.cRestitution + autre.cRestitution) / 2) / (1/this.getMasse() )));	
			double j = t.dot(normale);
			return j;
		}
		
		else {
			SVector2d t = (this.getVitesse().substract(autre.getVitesse())).multiply((-(1 + (this.cRestitution + autre.cRestitution) / 2) / (1/this.getMasse() + 1/autre.getMasse())));	
			double j = t.dot(normale);
			return j;
		}
	}
	
	else if (this.isStatique()) {
		if (this.getMasse() == 0 ) {
			SVector2d t = autre.getVitesse().multiply((-(1 + (this.cRestitution + autre.cRestitution) / 2) / (1/autre.getMasse()))).multiply(1);	
			double j = t.dot(normale);
			return j;
		}
		
		else if (autre.getMasse() == 0) {
			SVector2d t = autre.getVitesse().multiply((-(1 + (this.cRestitution + autre.cRestitution) / 2) / (1/this.getMasse()))).multiply(1);	
			double j = t.dot(normale);
			return j;
		}
		
		else {

			SVector2d t =autre.getVitesse().multiply((-(1 + (this.cRestitution + autre.cRestitution) / 2) / (1/autre.getMasse()))).multiply(1);	
			double j = t.dot(normale);
			return j;
		}
	}
	
	else if (autre.isStatique()) {
		if (this.getMasse() == 0 ) {
			SVector2d t = this.getVitesse().multiply((-(1 + (this.cRestitution + autre.cRestitution) / 2) / (1/autre.getMasse()))).multiply(1);	
			double j = t.dot(normale);
			return j;
		}
		
		else if (autre.getMasse() == 0) {
			SVector2d t = this.getVitesse().multiply((-(1 + (this.cRestitution + autre.cRestitution) / 2) / (1/this.getMasse()))).multiply(1);	
			double j = t.dot(normale);
			return j;
		}
		
		else {

			SVector2d t =this.getVitesse().multiply((-(1 + (this.cRestitution + autre.cRestitution) / 2) / (1/this.getMasse()))).multiply(1);	
			double j = t.dot(normale);
			return j;
		}
	}
	
	else {
		return 0;
	}

	}
	/**
	 * Auteur: Marcus Phan
	 * Retourne le vecteur representant la quantite de mouvement du corps, obtenu en multipliant 
	 * son vecteur vitesse par sa masse.
	 * @return Le vecteur quantite de mouvement du corps.
	 */
	public SVector2d getQuantiteDeMouvement() {
		return vitesse.multiply(getMasse());		
	}

	/* ****************************************GETTERS AND SETTERS*****************************************/
	/**
	 * Retourne le nom du <code>Corps</code>.
	 * @return Le nom du <code>Corps</code>
	 */
	public String getNom() {
		return nom;
	}
	/**
	 * Associe un nouveau nom e un <code>Corps</code>.
	 * @param nom Le nouveau nom
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 *
	 *	Retourne le point representant la position du <code>Corps</code>
	 *	@return Un <code>Point2D.Double</code> representant la position du <code>Corps</code>
	 */
	public Point2D.Double getPositionPoint() {
		return position.vectorToPoint();
	}
	
	/**
	 * Retourne le vecteur representant la position du <code>Corps</code>
	 * @return Le vecteur representant la position du <code>Corps</code>
	 */
	public SVector2d getPosition () {
		return position;
	}
	/**
	 * Modifie le vecteur position du <code>Corps</code>.
	 * @param position Le nouveau vecteur position
	 */
	public void setPosition(SVector2d position) {
		this.position = position;
	}
	/**
	 * Retourne le vecteur representant la vitesse du <code>Corps</code>
	 * @return Le vecteur representant la vitesse du <code>Corps</code>
	 */
	public SVector2d getVitesse() {
		return vitesse;
	}
	
	/**
	 * Modifie le vecteur vitesse du <code>Corps</code>.
	 * @param vitesse Le nouveau vecteur vitesse
	 */
	public void setVitesse(SVector2d vitesse) {
		if (!statique) {
			if (vitesse.dot(vitesse) > VITESSE_MAX_VALUE*VITESSE_MAX_VALUE)
				vitesse = vitesse.normalize().multiply(VITESSE_MAX_VALUE);
			this.vitesse = vitesse;
		}
		else {
			this.vitesse = new SVector2d(0,0);
		}
		
	}
	
	/**
	 * Retourne le vecteur representant l'acceleration du <code>Corps</code>.
	 * @return Le vecteur representant l'acceleration du <code>Corps</code>
	 */
	public SVector2d getAcceleration() {
		return acceleration;
	}
	
	/**
	 * Modifie le vecteur acceleration du <code>Corps</code>.
	 * @param acceleration Le nouveau vecteur acceleration
	 */
	public void setAcceleration(SVector2d acceleration) {
		this.acceleration = acceleration;
	}

	/**
	 * Retourne la force agissante sur le <code>Corps</code>.
	 * @return La force agissante sur le <code>Corps</code>
	 */
	public SVector2d getForce() {
		return force;
	}

	/**
	 * Modifie la force agissante sur le <code>Corps</code>.
	 * @param force La force agissante sur le <code>Corps</code>
	 */
	public void setForce(SVector2d force) {
		this.force = force;
	}
	/**
	 * Retourne la forme du <code>Corps</code>
	 * @return La forme du <code>Corps</code> (Un objet implementant l'interface <code>Shape</code>)
	 */
	public Shape getForme() {
		return forme;
	}

	/**
	 * Modifie la forme du <code>Corps</code>. Cette methode recalcule le point central du <code>Corps</code>.
	 * @param forme La nouvelle forme du <code>Corps</code>.
	 */
	public void setForme(Shape forme) {
		this.forme = forme;
		centerPoint = computeCenter();
	}
	
	/**
	 * Retourne un <code>Area</code> representant la forme du <code>Corps</code>.
	 * @return Un <code>Area</code>  representant la forme du <code>Corps</code>
	 */
	public Area getAire() {
		return aire;
	}
	
	/**
	 * Retourne la masse du <code>Corps</code>.
	 * @return La masse du <code>Corps</code>
	 */
	public double getMasse() {
		return materiau.getMasseSurfacique() * getSurface();
	}
	/**
	 * Retourne la charge de l'objet
	 * @return la charge
	 */
	public double getCharge(){
		return materiau.getChargeSurfacique() * getSurface();
	}
	
	/**
	 * Retourne l'aire occupee par le corps, en m^2
	 * @return l'aire occupee par le corps
	 */
	public abstract double getSurface();
	
	/**
	 * Retourne le point central du <code>Corps</code> sans la recalculer.
	 * @return Le point central du <code>Corps</code>.
	 */
	public Point2D.Double getCenterPoint () {
		return centerPoint;
		
	}
	
	/**
	 * Retourne <strong>{@code true}</strong> si l'objet est statique, i.e. qu'il n'est pas affecter par les forces.
	 * @return <strong>{@code true}</strong> si l'objet est statique, sinon <strong>{@code false}</strong>
	 */
	public boolean isStatique(){
		return statique | fixe;
	}
	/**
	 * Permet de modifier si l'objet est statique ou non
	 * @param s - {@code boolean} : le nouvel etat de "staticiter" de l'objet
	 */
	public void setStatique(boolean s){
		vitesse = new SVector2d(0,0);
		this.statique = s;
	}
	/**
	 * Indique si l'objet est fixe ou non<br>utilise par la souris
	 * @param val - {@code boolean} : l'objet est-il fixe?
	 */
	public void setFixe(boolean val){ fixe = val; }
	/* ****************************************GETTERS AND SETTERS*****************************************/

	/**
	 * Retourne le coefficient de restitution
	 * @return Le coefficient de restitution
	 */
	public double getcRestitution() {
		return cRestitution;
	}

	/**
	 * Modifie le coefficient de restitution.
	 * @param cRestitution Le nouveau coefficient de restitution
	 */
	public void setcRestitution(double cRestitution) {
		if (cRestitution <= 1 && cRestitution >= 0) {
		this.cRestitution = cRestitution;
		} 
		
		else if (cRestitution > 1) {
			this.cRestitution = 1;
			JOptionPane.showMessageDialog(ouSuisJe, "Le coefficient de restitution doit etre entre 0 et 1");
		}
		
		else {
			this.cRestitution = 0;
			JOptionPane.showMessageDialog(ouSuisJe, "Le coefficient de restitution doit etre entre 0 et 1");
		}
	}

	/**
	 * Retourne la couleur de l'objet
	 * @return la couleur de l'objet
	 */
	public Color getColor() {
		return materiau.getColor();
	}
	
	/**
	 * Modifie la zone d'interaction ou se trouve l'objet.
	 * @param zone La nouvelle zone d'interaction.
	 */
	public void setOuSuisJe(ZoneInteraction zone){
		ouSuisJe = zone;
	}
	
	/**
	 * Indique si les vecteurs de vitesse et acceleration doivent etre dessines
	 * @return si les vecteurs doivent etre dessines
	 */
	public static boolean isDrawVectors() {
		return drawVectors;
	}

	/**
	 * Permet d'indiquer si les vecteurs de vitesse et acceleration doivent etre dessines
	 * @param drawVectors - {@code boolean} : si les vecteurs doivent etre dessines
	 */
	public static void setDrawVectors(boolean drawVectors) {
		Corps.drawVectors = drawVectors;
	}


	@Override
	public abstract Corps clone();
	
	/**
	 * Retourne le materiau associe a cet objet
	 * @return le materiau de l'objet
	 */
	public Materiaux getMateriau(){ return materiau; }
	/**
	 * Assigne un nouveau materiau a l'objet
	 * @param mat - {@code Materiaux} : le nouveau materiau
	 * @return le corps
	 */
	public Corps setMateriau(Materiaux mat){
		materiau = mat;
		return this;
	}

	// ********** IMPLEMENTATION DE truc de corde ********** \\
	/**
	 * Detache toutes les cordes et tous les objets de celui-ci
	 * @return l'objet
	 */
	public Corps detacherTout(){
		for (Corde corde : cordes){
			corde.detacher(this);
		}
		return this;
	}
	/**
	 * retourne une collection contenant toute les cordes attachees a cet objet
	 * @return toutes les cordes attachees
	 */
	@SuppressWarnings("unchecked") // Je sais que LinkedList<Corde>.clone() va retourner un LinkedList<Corde>, pas besoins de verifier
	public Collection<Corde> getCordesAttachees(){
		return (LinkedList<Corde>)cordes.clone();
	}
	/**
	 * Verifie si l'objet est relie a {@code autre}
	 * @param autre - {@code Corps} : l'objet avec lequel celui-ci sera teste
	 * @return <strong>{@code true}</strong> si les objets sont relies, sinon <strong>{@code false}</strong>
	 */
	public boolean estRelieA(Corps autre) {
		for (Corde corde : cordes)
			if (corde.getAutre(this) == autre)
				return true;
		return false;
	}
	/**
	 * Verifie si l'objet est attache a la corde en parametre
	 * @param corde - {@code Corde} : la corde a verifier
	 * @return <strong>{@code true}</strong> si la corde est attachee a l'objet, sinon <strong>{@code false}</strong>
	 */
	public boolean estAttacheA(Corde corde) {
		for (Corde c : cordes)
			if (c == corde)
				return true;
		return false;
	}
	/**
	 * Attache cet objet a tout ceux passes en parametre
	 * @param autres - {@code Corps[]} : les objets a attacher
	 * @return l'objet
	 */
	public Corps attacher(Corps... autres) {
		for (Corps autre : autres){
			new Corde(this, autre);
		}
		return this;
	}
	/**
	 * Attache cet objet a tout ceux passes en parametre avec des cordes de longueurs indique
	 * @param autres - {@code Corps[]} : les objets a attacher
	 * @param longueur - {@code double[]} : listes contenant les longueurs des cordes
	 * @return l'objet
	 */
	public Corps attacher(Corps[] autres, double[] longueur){
		int i = 0;
		for (Corps autre : autres){
			new Corde(this, autre, i < longueur.length ? longueur[i] : 0);
			i++;
		}
		return this;
	}
	/**
	 * Attache les objets passes en parametre a l'objet et retourne les cordes ainsi creees<br>
	 * Si {@code retour} est <strong>{@code null}</strong> ou qu'il est trop petit, un nouveau tableau sera cree
	 * @param retour - {@code Corde[]} : une liste qui contiendra les cordes creees
	 * @param autres - {@code Corps[]} : les objets a attacher
	 * @return la liste de corde creees
	 */
	public Corde[] attacher(Corde[] retour, Corps... autres){
		if (retour == null || retour.length < autres.length)
			retour = new Corde[autres.length];
		for (int i = 0; i < autres.length; i++){
			Corps autre = autres[i];
			retour[i] = new Corde(this, autre);
		}
		return retour;
	}
	/**
	 * Attache les cordes passees en parametre a cet objet
	 * @param cordes - {@code Corde[]} : les cordes a attacher
	 * @return l'objet
	 */
	public Corps attacher(Corde... cordes) {
		for (Corde corde : cordes){
			if (!this.cordes.contains(corde))
				this.cordes.add(corde);
		}
		return this;
	}
	/**
	 * Detache, si possible, les cordes passees en parametre
	 * @param cordes - {@code Corde[]} : les cordes a detacher
	 * @return l'objet
	 */
	public Corps detacher(Corde... cordes) {
		for (Corde corde : cordes){
			this.cordes.remove(corde);
		}
		return this;
	}
	/**
	 * Detache, si possible, les objets passes en parametre
	 * @param autres - {@code Corps[]} : les objets a detacher
	 * @return l'objet
	 */
	public Corps detacher(Corps... autres) {
		for (Corps autre : autres){
			for (Corde corde : cordes){
				if (corde.getAutre(this) == autre){
					corde.detacher();
					break;
				}
			}
		}
		return this;
	}
	
	// FIN IMPLENTATION \\
	
	// trucs d'inspecteurs \\
	/**
	 * Retourne une chaine representant la position de l'objet
	 * @return une chaine representant la position de l'objet
	 */
	public String getShortPosition(){	
		return "["+String.format("%.2f", position.getX()) + "; " + String.format("%.2f", position.getY()) + "]";
	}
	/**
	 * Retourne une chaine representant la vitesse de l'objet
	 * @return une chaine representant la vitesse de l'objet
	 */
	public String getShortVitesse(){
		return "["+String.format("%.2f", vitesse.getX()) + "; " + String.format("%.2f", vitesse.getY()) + "]";
	}
	/**
	 * Retourne une chaine representant l'acceleration de l'objet
	 * @return une chaine representant l'acceleration de l'objet
	 */
	public String getShortAcceleration(){
		return "["+String.format("%.2f", acceleration.getX()) + "; " + String.format("%.2f", acceleration.getY()) + "]";
	}
	// fin inspecteur \\
}
