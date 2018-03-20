package corps;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import interaction.Dessinable;
import physique.SVector2d;

/**
 * Classe representant une corde (rigide) qui lie deux objet de type <code>Corps</code> ensemble et qui limitent
 * leur mouvement un par rapport � l'autre
 * @author Marcus Phan, Leo Jetzer
 * @see Corps
 */
public class Corde {
	
	private Corps corpsUn;
	private Corps corpsDeux;
	
	private Point2D.Double pointUn;
	private Point2D.Double pointDeux;
	private SVector2d vecteurLongueur;
	private double longueurMax = 0;
	private double coeffTension = 500;
	private boolean ressort = false;
	
	/**
	 * Attacher un corps a un autre par une nouvelle corde (Construire la corde).
	 * @param corpsUn Un corps
	 * @param corpsDeux L'autre corps
	 */
	
	public Corde (Corps corpsUn, Corps corpsDeux) {
		if (corpsUn != null)
			this.corpsUn = corpsUn.attacher(this);
		if (corpsDeux != null)
			this.corpsDeux = corpsDeux.attacher(this);
	}
	public Corde(Corps corpsUn, Corps corpsDeux, double l){
		this(corpsUn, corpsDeux);
		longueurMax = l;
	}
	public Corde(Corps corpsUn, Corps corpsDeux, double l, double t){
		this(corpsUn, corpsDeux, l);
		coeffTension = t;
	}

	/**
	 * Dessiner la corde
	 * @param g2d Le contexte graphique o� la corde se dessinera
	 * @param matMC La matrice de transformation monde-composant � appliquer
	 */
	public void dessiner(Graphics2D g2d, AffineTransform matMC) {
		if (corpsUn instanceof Dessinable && corpsDeux instanceof Dessinable){
			// TODO Auto-generated method stub
			Color couleurALentree = g2d.getColor();

			pointUn = ((Dessinable)corpsUn).getPositionPoint();
			pointDeux = ((Dessinable)corpsDeux).getPositionPoint();
			setVecteurLongueur(new SVector2d(pointDeux.getX() - pointUn.getX(), pointDeux.getY() - pointUn.getY()));

			Stroke strokeALentree = g2d.getStroke();

			g2d.setColor(Color.GRAY);
			g2d.setStroke(new BasicStroke(4));
			g2d.draw(matMC.createTransformedShape(new Line2D.Double(pointUn, pointDeux)));
			g2d.setColor(couleurALentree);
			g2d.setStroke(strokeALentree);
		}
		
	}
	
	@Override
	/**
	 * Retourne un String contenant de l'information textuel sur les positions des deux objets attach�s par la corde.
	 */
	public String toString () {
		return ("Corps un" + corpsUn + "; Corps deux" + corpsDeux);
		
	}

	/**
	 * Retourne le vecteur repr�sentant la <code>Corde</code>.
	 * @return Le vecteur repr�sentant la <code>Corde</code>
	 */
	public SVector2d getVecteurLongueur() {
		return vecteurLongueur;
	}

	/**
	 * Modifie le vecteur repr�sentant la <code>Corde</code>
	 * @param vecteurLongueur Le nouveau vecteur.
	 */
	public void setVecteurLongueur(SVector2d vecteurLongueur) {
		this.vecteurLongueur = vecteurLongueur;
	}
	
	/**
	 * Retourne l'objet attache a l'autre bout de la corde si {@code corps} y est attache. Dans l'autre cas, cette methode retourne <strong>{@code null}</strong>
	 * @param corps - {@code Corps} : l'objet a un bout de la corde
	 * @return l'objet attache a l'autre bout de la corde
	 */
	public Corps getAutre(Corps corps){
		if (corpsUn == corps)
			return corpsDeux;
		if (corpsDeux == corps)
			return corpsUn;
		return null;
	}

	/**
	 * Verifie si la corde est attachee a l'objet passe en parametre
	 * @param corps - {@code Corps} : l'objet a tester
	 * @return <strong>{@code true}</strong> si l'objet est attache, sinon <strong>{@code false}</strong>
	 */
	public boolean estAttacheeA(Corps corps){
		return corpsUn == corps || corpsDeux == corps;
	}
	
	/**
	 * Retourne une liste contenant les deux corps attache a cette corde
	 * @return les corps attache a la corde
	 */
	public Corps[] getCorps(){
		return new Corps[] {corpsUn, corpsDeux};
	}
	
	/**
	 * Change les objets auquels la corde est attachee
	 * @param a - {@code Corps} : le premier objet
	 * @param b - {@code Corps} : le deuxieme objet
	 * @return la corde
	 */
	public Corde set(Corps a, Corps b){
		if (corpsUn != null)
			corpsUn.detacher(this);
		if (corpsDeux != null)
			corpsDeux.detacher(this);
		corpsUn = a;
		corpsDeux = b;
		if (a != null)
			a.attacher(this);
		if (b != null)
			b.attacher(this);
		return this;
	}
	
	/**
	 * Detache l'objet en parametre de la corde s'il y etait attache
	 * @param corps - {@code Corps} : l'objet a detacher
	 * @return la corde
	 */
	public Corde detacher(Corps corps){
		if (corps == corpsUn){
			corpsUn = null;
			if (corps != null)
				corps.detacher(this);
		}
		if (corps == corpsDeux){
			corpsDeux = null;
			if (corps != null)
				corps.detacher(this);
		}
		return this;
	}
	
	/**
	 * Detache les deux objets de la corde
	 * @return la corde
	 */
	public Corde detacher(){
		return set(null, null);
	}
	
	/**
	 * Retourne la longueur maximale de la corde
	 * @return la longueur maximale de la corde
	 */
	public double getLongueurMax() { return longueurMax; }
	/**
	 * Assigne une nouvelle longueur maximale a la corde
	 * @param longueurMax - {@code double} : la nouvelle longueur maximale
	 */
	public void setLongueurMax(double longueurMax) { this.longueurMax = longueurMax; }
	/**
	 * Retourne la constante de rappel de la corde/ressort
	 * @return la constante de rappel
	 */
	public double getCoefficientTension(){ return coeffTension; }
	/**
	 * Change la valeur de la constante de rappel
	 * @param t - {@code double} : la nouvelle constante de rappel
	 */
	public void setCoefficientTension(double t){ coeffTension = t; }
	/**
	 * Verifie si la corde serait en fait un ressort
	 * @return <strong>{@code true}</strong> si la corde est un ressort, sinon <strong>{@code false}</strong>
	 */
	public boolean isRessort(){ return ressort; }
	/**
	 * Change si cette corde serait en fait un ressort
	 * @param r - {@code double} : la corde devrait-elle agir comme un ressort?
	 */
	public void setIsRessort(boolean r){ ressort = r; }
	
	/**
	 * Applique les forces de tension sur les deux objets attaches
	 */
	public void appliquerForce(){
		SVector2d l = corpsUn.getPosition().substract(corpsDeux.getPosition());
		double dl = longueurMax - l.modulus();
		if (ressort || dl < 0){
			double force = dl*coeffTension;
			l = l.normalize();
			corpsUn.appliquerForce(l.multiply(force));
			corpsDeux.appliquerForce(l.multiply(-force));
		}
	}
	
}
