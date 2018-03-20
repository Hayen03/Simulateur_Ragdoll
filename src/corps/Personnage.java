package corps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import interaction.Dessinable;
import interaction.ZoneInteraction;
import physique.Materiaux;
import physique.SVector2d;

/**
 * Classe representant l'ensemble du Ragdoll (poupee de chiffon)
 * @author Marcus Phan
 */
public class Personnage implements Dessinable {

	private Balle leftArm;
	private Balle rightArm;
	private Balle leftLeg;
	private Balle rightLeg;
	private Balle body;
	private Balle head;
	private ArrayList<Balle> leCorps = new ArrayList<Balle>();
	
	private Collection<Corde> ligaments;
	
	private final double LONGUEUR_JAMBE = 1.5;
	private final double GROSSEUR_CORPS = 1;
	private final double LONGUEUR_COU = 1.7;
	private final double RAYON_JAMBE = 0.5;
	private final double RAYON_MAIN = 0.3;
	private final double RAYON_TETE = 0.8;
	private final double RAYON_MEMBRES = 0.5;
	
	/**
	 * Construit un <code>Personnage</code>. Un personnage possede 6 objets de type <code>Balle</code>.
	 * Ces objets sont predefinis et representent chacun de ses membres (parties du corps).
	 * @param ouSuisJe La <code>ZoneInteraction</code> dans laquelle se retrouvera le personnage
	 */
	public Personnage(ZoneInteraction ouSuisJe) {
		
		// Le personnage est initialement centre sur le systeme d'axes. La position donnee est celle du centre du cercle.
		
		body = new Balle("body", new SVector2d(0,0), GROSSEUR_CORPS, ouSuisJe, 0);
		leftArm = new Balle("leftArm", new SVector2d(-GROSSEUR_CORPS-RAYON_MAIN,0), RAYON_MAIN, ouSuisJe, 0.5);
		rightArm = new Balle("rightArm", new SVector2d(GROSSEUR_CORPS+RAYON_MAIN,0), RAYON_MAIN, ouSuisJe, 0.5); //e l'autre bout du corps
		leftLeg = new Balle("leftLeg", new SVector2d(-GROSSEUR_CORPS/2, LONGUEUR_JAMBE), RAYON_JAMBE, ouSuisJe, 0.5);
		rightLeg = new Balle("rightLeg", new SVector2d(GROSSEUR_CORPS/2, LONGUEUR_JAMBE), RAYON_JAMBE, ouSuisJe, 0.5); // e l'autre bout du corps
		head = new Balle("head", new SVector2d(0,-LONGUEUR_COU), RAYON_TETE, ouSuisJe, 0.5);
		leCorps.add(body);
		leCorps.add(leftArm);
		leCorps.add(rightArm);
		leCorps.add(leftLeg);
		leCorps.add(rightLeg);
		leCorps.add(head);
		
		double longueur = GROSSEUR_CORPS+RAYON_MEMBRES+0.2;
		body.attacher(new Corps[]{leftArm, rightArm, leftLeg, rightLeg, head}, new double[]{longueur, longueur, longueur, longueur, longueur});
		ligaments = body.getCordesAttachees();
		
		// allourdi les petits membres -> Va etre changer plus tard
		leftArm.setMateriau(Materiaux.MAT_METAL);
		rightArm.setMateriau(Materiaux.MAT_METAL);
		leftLeg.setMateriau(Materiaux.MAT_METAL);
		rightLeg.setMateriau(Materiaux.MAT_METAL);
		head.setMateriau(Materiaux.MAT_METAL);
		
	}

	
	/**
	 * Dessine les 6 parties de corps du <code>Personnage</code> ainsi que 5 <code>Corde</code> liant ces parties de corps.
	 * @param g2d Le contexte graphique dans lequel le sera dessine le personnage
	 * @param matMC La matrice de transformation monde-composante
	 */
	@Override
	public void dessiner (Graphics2D g2d, AffineTransform matMC) {
		Color couleurALentree = g2d.getColor();
		
		for (Balle courant : leCorps) {
			courant.dessiner(g2d, matMC);
		}

		Iterator<Corde> itCorde = ligaments.iterator();
		Corde courant;
		while(itCorde.hasNext()){
			courant = itCorde.next();
			Dessinable[] tmp = (Dessinable[])courant.getCorps();
			if (tmp[0] != null && tmp[1] != null)
				courant.dessiner(g2d, matMC);
			else
				itCorde.remove(); // enleve la corde si elle ne relie plus deux membres du corps
		}
		
		g2d.setColor(couleurALentree);
	}
	
	
	/**
	 * Non-implementee.
	 * @return null
	 */
	public SVector2d getCentreMasse() {
		return null;
		
	}
	
	/**
	 *
	 * Retourne la somme des masses de chacune des parties du corps.
	 * @return La somme des masses de chacune des parties du corps.
	 */
	public double getMasse() {
		return leftArm.getMasse() + rightArm.getMasse() + leftLeg.getMasse() + rightLeg.getMasse() + body.getMasse() + head.getMasse();
		
	}

	
	@Override
	/**
	 * Calcule la prochaine iteration physique pour chacune des 6 parties de corps de du <code>Personnage</code>.
	 */
	public void prochaineIterationPhysique() {
		// TODO Auto-generated method stub
		for (Balle courant : leCorps) {
			courant.prochaineIterationPhysique();
			
		}
	}

	@Override
	/**
	 * Retourne le point central du <code>Personnage</code> (incomplete)
	 * @return Le point central du <code>Personnage</code>
	 */
	public Point2D.Double computeCenter() {
		return body.computeCenter();
	}

	
	/**
	 * Pour l'instant, la position du ragdoll est le centre de son body.
	 */
	@Override
	public Double getPositionPoint() {
		return body.getPositionPoint();
	}

	/**
	 * Retourne un <code>ArrayList</code> contenant toutes les parties du corps du <code>Personnage</code>.
	 * @return Un <code>ArrayList</code> contenant toutes les parties du corps du <code>Personnage</code>
	 */
	public ArrayList<Balle> getLeCorps() {
		return leCorps;
	}

	/**
	 * Modifie toutes les parties de corps du <code>Personnage</code>.
	 * @param leCorps Un <code>ArrayList</code> contenant toutes les nouvelles parties de corps du <code>Personnage</code>
	 */
	public void setLeCorps(ArrayList<Balle> leCorps) {
		this.leCorps = leCorps;
	}

	/**
	 * Retourne un <code>ArrayList</code> contenant les objets <code>Corde</code> rattachant les parties de corps du <code>Personnage</code>
	 * @return Le <code>ArrayList</code> contenant les objets <code>Corde</code> rattachant les parties de corps <code>Personnage</code>
	 */
	public Collection<Corde> getLigaments() {
		return ligaments;
	}
	
}
