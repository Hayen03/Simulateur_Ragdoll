package interaction;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
/**
 * Une interface dont impl�mente les objets qui peuvent se dessiner (et donc, qui sont soumis
 * au lois physiques de l'environnement physique)
 * @author Marcus Phan
 *
 */
public interface Dessinable {
	
	/**
	 * Cette m�thode dessine un objet dessinable selon sa classe.
	 * @param g2d L'environnement graphique o� se dessinera l'objet
	 * @param matMC La matrice de transformation monde vers composant de l'environnement
	 */
	void dessiner(Graphics2D g2d, AffineTransform matMC);
	
	/**
	 * Calcule la prochaine iteration physique
	 */
	void prochaineIterationPhysique();
	/**
	 * Calcule et retourne le centre de l'objet
	 * @return le centre de l'objet
	 */
	Point2D.Double computeCenter();
	/**
	 * Retourne le point d'origine de l'objet
	 * @return le point d'origine
	 */
	Point2D.Double getPositionPoint();
	
}
