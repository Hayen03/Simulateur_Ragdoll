package corps;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ConcurrentModificationException;

import annotation.Editable;
import interaction.ZoneInteraction;
import physique.SVector2d;
import inspecteur.PropReel;

/**
 * Classe representant un Corps rond possedant un rayon.
 * @author Marcus Phan
 * @see Corps
 */
@Editable(nom="rayon", get="getRayon", set="setRayon", type=PropReel.class, id="RAYON")
public class Balle extends Corps {
	
	public static final double MIN_RAYON = 0.1;
	public static final double MAX_RAYON = 10;
	public static final double DEFAUT_RAYON = 1;
	
	private double rayon;

	/**
	 * Constructeur creant une copie d'une balle
	 * @param orig - {@code Balle} : la balle originale
	 */
	public Balle(Balle orig){
		this(orig.nom, orig.position, orig.rayon, orig.ouSuisJe);
	}
	
	/**
	 * Creer une balle
	 * @param nom - {@code String} : le nom de la balle
	 * @param position - {@code SVector2d} : la position de la balle
	 * @param rayon - {@code double} : le rayon de la balle
	 * @param ouSuisJe - {@code ZoneInteraction} : la {@code ZoneInteraction} dans laquelle la balle se trouve
	 */
	public Balle(String nom, SVector2d position, double rayon, ZoneInteraction ouSuisJe) {
		
		super(nom, position, new Ellipse2D.Double(0, 0 , 2*rayon, 2*rayon), ouSuisJe);
		this.rayon = rayon;

	}
	/**
	 * Creer une balle avec un coefficient de restitution
	 * @param nom - {@code String} : le nom de la balle
	 * @param position - {@code SVector2d} : la position de la balle
	 * @param rayon - {@code double} : le rayon de la balle
	 * @param ouSuisJe - {@code ZoneInteraction} : la {@code ZoneInteraction} dans laquelle la balle se trouve
	 * @param cRest - Le coefficient de restitution
	 */
	public Balle(String nom, SVector2d position, double rayon, ZoneInteraction ouSuisJe, double cRest) {
		
		super(nom, position, new Ellipse2D.Double(0, 0 , 2*rayon, 2*rayon), ouSuisJe, cRest);
		this.rayon = rayon;

	}
	
	/**
	 * Methode qui gere les collisions entre une balle et une autre balle ou une boite.
	 * 
	 */

	@Override
	public void gererCollisions(){
		gererCollisionsBalleBalle();
		gererCollisionBalleBoite();
	}

	/**
	 * Methode qui gere les collisions entre les balles et les boites.
	 */
	private void gererCollisionBalleBoite() {
		//CAS BALLE-BOITE
		try {

			for (Boite prochain : ouSuisJe.getToutesLesBoites().values()) {
				
				if (this.collidesWith(prochain)) {
					if (!this.getClass().equals(Mur.class)) {
					SVector2d normale = this.calculerLaNormale(prochain);
					
			

					
					try {
						normale = normale.normalize();
					} catch (Exception e) {
						System.out.println("Le vecteur ne peut etre normalisee...");
					}
					
					while (this.collidesWith(prochain)) {
						this.pushOut(prochain, prochain.position.substract(this.position));
					}
					
					

					double energie = this.calculerImpulsion(prochain, normale);
				
					this.vitesse = this.vitesse.add(normale.multiply(energie/this.getMasse()));
					if (!prochain.isStatique())
						prochain.vitesse = prochain.vitesse.add(normale.multiply(-energie/prochain.getMasse()));

				
				}
			}
		}
			} catch (ConcurrentModificationException e) {
			// TODO Auto-generated catch block
			System.out.println("WARNING :" + e.getClass().toGenericString() + " dans Balle!!!");
		}
	}
	
	/**
	 * Methode qui detecte si la Balle courante est en collision avec un Boite.
	 * @param autre la Boite.
	 * @return true si en collision, false sinon.
	 */
	public boolean collidesWith(Boite autre) {

		double transformedCircleX = Math.cos(autre.getRotation()) * (this.position.getX() - autre.getPosition().getX())
				- Math.sin(	autre.getRotation()) * (this.position.getY() - autre.getPosition().getY()) + autre.getPosition().getX();
		double transformedCircleY = Math.sin(autre.getRotation()) * (this.position.getX() - autre.getPosition().getX())
				+ Math.cos(autre.getRotation()) * (this.position.getY() - autre.getPosition().getY()) + autre.getPosition().getY();
		SVector2d positionRel = autre.position.substract(new SVector2d(transformedCircleX, transformedCircleY));

		Balle temp = new Balle("TEMP", new SVector2d(transformedCircleX, transformedCircleY), this.rayon, ouSuisJe);


		//TOPLEFT QUADRANT
		if (positionRel.getX() <= 0 && positionRel.getY() <= 0) {
			
			if (Math.abs(positionRel.getY()) >= autre.getHauteur() / 2) {
				if ( Math.abs(positionRel.getX()) >= autre.getLargeur() / 2){
					SVector2d botRightCorner = new SVector2d(autre.getPosition().getX() + autre.getLargeur()/2, autre.getPosition().getY() + autre.getHauteur()/2);
					if (botRightCorner.substract(temp.position).modulus() <= rayon) {
						return true;
					}
				}
				else {
					return positionRel.getY() + rayon + autre.getHauteur()/2 > 0;
				}
			}
			
			else {
				return positionRel.getX() + rayon + autre.getLargeur()/2 > 0;
			}


			
		}
		
		//TOPRIGHT QUADRANT
		
		else if (positionRel.getX() > 0 && positionRel.getY() <= 0) {
			//CHECKING BOTLEFT CORNER
			if (Math.abs(positionRel.getY()) >= autre.getHauteur() / 2 ) {
				if (Math.abs(positionRel.getX()) >= autre.getLargeur() / 2) {
					SVector2d botLeftCorner = new SVector2d(autre.getPosition().getX() - autre.getLargeur()/2, autre.getPosition().getY() + autre.getHauteur()/2);
					if (botLeftCorner.substract(temp.position).modulus() <= rayon) {
						return true;
					}
				}
				
				else {
					return positionRel.getY() + rayon + autre.getHauteur()/2 > 0;

				}
			}
			
			else {
					return positionRel.getX() - rayon - autre.getLargeur()/2 < 0;

			}
			

		}
		
		//BOTLEFT QUADRANT
		else if (positionRel.getX() <= 0 && positionRel.getY() > 0) {
			if (Math.abs(positionRel.getY()) >= autre.getHauteur() / 2 ) {
				if (Math.abs(positionRel.getX()) >= autre.getLargeur() / 2) {
					SVector2d topRightCorner = new SVector2d(autre.getPosition().getX() + autre.getLargeur()/2, autre.getPosition().getY() - autre.getHauteur()/2);
					if (topRightCorner.substract(temp.position).modulus() <= rayon) {
						return true;
					}
				}
				
				else {
					return positionRel.getY() - rayon - autre.getHauteur()/2 < 0;

				}
			}
			
			else {
					return positionRel.getX() + rayon + autre.getLargeur()/2 > 0;

			}
		}
		
		//BOTRIGHT QUADRANT
		else if (positionRel.getX() > 0 && positionRel.getY() > 0) {
			if (Math.abs(positionRel.getY()) >= autre.getHauteur() / 2 ) {
				if (Math.abs(positionRel.getX()) >= autre.getLargeur() / 2) {
					SVector2d topLeftCorner = new SVector2d(autre.getPosition().getX() - autre.getLargeur()/2, autre.getPosition().getY() - autre.getHauteur()/2);
					if (topLeftCorner.substract(temp.position).modulus() <= rayon) {
						return true;
					}
				}
				
				else {
					return positionRel.getY() - rayon - autre.getHauteur()/2 < 0;

				}
			}
			
			else {
					return positionRel.getX() - rayon - autre.getLargeur()/2 < 0;

			}
		}
		
		
		return false;
	}
	
	/**
	 * Methode qui retourne le Point2D.Double de collision entre la Balle et la Boite dans un systeme d'axe
	 * alligne avec la Boite. Le Point2D.Double retourne ne sera pas .
	 * @param autre la Boite.
	 * @return Le point de collision entre la Balle et la Boite sui
	 */
	public Point2D.Double intersectionPoint(Boite autre) {
	
		double transformedCircleX = Math.cos(autre.getRotation()) * (this.position.getX() - autre.getPosition().getX())
				- Math.sin(autre.getRotation()) * (this.position.getY() - autre.getPosition().getY()) + autre.getPosition().getX();
		double transformedCircleY = Math.sin(autre.getRotation()) * (this.position.getX() - autre.getPosition().getX())
				+ Math.cos(autre.getRotation()) * (this.position.getY() - autre.getPosition().getY()) + autre.getPosition().getY();
		if (autre.getRotation() != 0 && this.getNom().equals("leftArm")) {

		}
		double procheX;
		double procheY;
		
		if (transformedCircleX < autre.getPosition().getX() - autre.getLargeur()/2) {
			procheX = autre.getPosition().getX() - autre.getLargeur()/2;
		}
		
		else if (transformedCircleX  > autre.getPosition().getX() + autre.getLargeur()/2)
		{
		    procheX = autre.getPosition().getX() + autre.getLargeur()/2;
		}
		else {
		    procheX = transformedCircleX;
		}	
				
		if (transformedCircleY < autre.getPosition().getY() - autre.getHauteur()/2) {
			procheY = autre.getPosition().getY() - autre.getHauteur()/2;
		}
		
		else if (transformedCircleY  > autre.getPosition().getY() + autre.getHauteur()/2)
		{
		    procheY = autre.getPosition().getY() + autre.getHauteur()/2;
		}
		else {
		    procheY = transformedCircleY;
		}
		
		double dist = distance(transformedCircleX, transformedCircleY, procheX, procheY);
		
		if (dist < this.rayon) {
			return new Point2D.Double(transformedCircleX, transformedCircleY);
		}
		
		else {

			return null;
		}
	}
	
	/**
	 * Calcule la normale de la collision entre cette balle et une boite
	 * @param autre - {@code Boite} : la boite
	 * @return la normale de la collision
	 */
	public SVector2d calculerLaNormale(Boite autre) {
		
		Point2D.Double pointC = this.intersectionPoint(autre);
		
/*	// Coin BD
		if (pointC.getY() >= autre.position.getY() + autre.getHauteur() / 2  && pointC.getX() >= autre.position.getX() + autre.getLargeur() / 2) {			
				return autre.position.substract(new SVector2d(this.position.getX() * Math.abs(Math.cos(autre.getRotation())), this.position.getY() * Math.abs(Math.sin(autre.getRotation()))));
		}
		
	// Coin HD	
		
		else if (pointC.getY() <= autre.position.getY() -autre.getHauteur() / 2  && pointC.getX() >= autre.position.getX() + autre.getLargeur() / 2) {			
			return autre.position.substract(new SVector2d(this.position.getX() * Math.abs(Math.cos(autre.getRotation())), this.position.getY() * Math.abs(Math.sin(autre.getRotation()))));
		}
		
	// Coin BG 	
		else if (pointC.getY() >= autre.position.getY() + autre.getHauteur() / 2  && pointC.getX() <= autre.position.getX() - autre.getLargeur() / 2) {			
			return autre.position.substract(new SVector2d(this.position.getX() * Math.abs(Math.cos(autre.getRotation())), this.position.getY() * Math.abs(Math.sin(autre.getRotation()))));
		}
		
	// Coin HG
		else if (pointC.getY() <= autre.position.getY() -autre.getHauteur() / 2  && pointC.getX() <= autre.position.getX() - autre.getLargeur() / 2) {			
			return autre.position.substract(new SVector2d(this.position.getX() * Math.abs(Math.cos(autre.getRotation())), this.position.getY() * Math.abs(Math.sin(autre.getRotation()))));
		}
		*/
	// Coin BD
		
		if (pointC.getY() >= autre.position.getY() + autre.getHauteur() / 2  && pointC.getX() >= autre.position.getX() + autre.getLargeur() / 2) {			
			return new SVector2d(Math.cos(autre.getRotation()), -Math.sin(autre.getRotation())).multiply(-1).add(new SVector2d(Math.sin(autre.getRotation()), Math.cos(autre.getRotation())).multiply(-1)).normalize();
		}
		
	// Coin HD	
		
		else if (pointC.getY() <= autre.position.getY() -autre.getHauteur() / 2  && pointC.getX() >= autre.position.getX() + autre.getLargeur() / 2) {			
			return new SVector2d(Math.cos(autre.getRotation()), -Math.sin(autre.getRotation())).multiply(-1).add(new SVector2d(Math.sin(autre.getRotation()), Math.cos(autre.getRotation()))).normalize();
		}
		
	// Coin BG 	
		else if (pointC.getY() >= autre.position.getY() + autre.getHauteur() / 2  && pointC.getX() <= autre.position.getX() - autre.getLargeur() / 2) {			
			return new SVector2d(Math.cos(autre.getRotation()), -Math.sin(autre.getRotation())).add(new SVector2d(Math.sin(autre.getRotation()), Math.cos(autre.getRotation())).multiply(-1)).normalize();
		}
		
	// Coin HG
		else if (pointC.getY() <= autre.position.getY() -autre.getHauteur() / 2  && pointC.getX() <= autre.position.getX() - autre.getLargeur() / 2) {			
			return new SVector2d(Math.cos(autre.getRotation()), -Math.sin(autre.getRotation())).add(new SVector2d(Math.sin(autre.getRotation()), Math.cos(autre.getRotation()))).normalize();
		}

		//Right
		else if (pointC.getX() >= autre.position.getX() + autre.getLargeur()/2) {
				return new SVector2d(Math.cos(autre.getRotation()), -Math.sin(autre.getRotation())).multiply(-1);
		}
		
		//Left
		else if (pointC.getX() <= autre.position.getX() - autre.getLargeur()/2) {
				return new SVector2d(Math.cos(autre.getRotation()), -Math.sin(autre.getRotation()));
		}
		
		//Bot
		else if ( pointC.getY() >= autre.getPosition().getY() + autre.getHauteur()/2) {
				return new SVector2d(Math.sin(autre.getRotation()), Math.cos(autre.getRotation())).multiply(-1);
		}
		
		//Top
		else {
			return new SVector2d(Math.sin(autre.getRotation()), Math.cos(autre.getRotation()));
		}
			
		
		



	}
	
	/**
	 * Calcule la distance entre deux points
	 * @param x1 - {@code double} : la coordonee en x du premier point
	 * @param y1 - {@code double} : la coordonee en y du premier point
	 * @param x2 - {@code double} : la coordonee en x du deuxieme point
	 * @param y2 - {@code double} : la coordonee en y du deuxieme point
	 * @return la distance entre les deux points
	 */
	public double distance (double x1,double y1,double x2,double y2) {
		double a = Math.abs(x2-x1);
		double b = Math.abs(y2-y1);
		
		return Math.sqrt((a*a) + (b*b));
	}

	/**
	 * Gere la collision entre deux balle
	 */
	private void gererCollisionsBalleBalle() {
		//CAS BALLE-BALLE
		try {
			for (Balle prochain : ouSuisJe.getToutesLesBalles().values()) {
				if (this.collidesWith(prochain)) {

					Point2D.Double pointUn = this.getPositionPoint();
					Point2D.Double pointDeux = prochain.getPositionPoint();

					SVector2d normale = new SVector2d(pointDeux.getX() - pointUn.getX(), pointDeux.getY() - pointUn.getY());
					
					try {
						normale = normale.normalize();
					} catch (Exception e) {
						System.out.println("Le vecteur ne peut etre normalisee...");
					}
					
					while (this.collidesWith(prochain)) {
						pushOut(prochain, normale);
					}
					//normale orientee vers lobjet en mouvement
					//normale part de lobjet prochain et qui pointe vers this
					normale = normale.multiply(-1);
					double energie = this.calculerImpulsion(prochain, normale);
					this.vitesse = this.vitesse.add(normale.multiply(energie/this.getMasse()));
					prochain.vitesse = prochain.vitesse.add(normale.multiply(-energie/prochain.getMasse()));
					
				}
			}
		} catch (ConcurrentModificationException e) {
			// TODO Auto-generated catch block
			System.out.println("WARNING :" + e.getClass().toGenericString() + " dans Balle!!!");
		}
	}

	@Override
	public double getSurface(){
		return Math.PI*rayon*rayon;
	}
	
	/**
	 * Test si la balle est en collision avec une autre
	 * @param autre - {@code Balle} : l'autre balle
	 * @return <strong>{@code true}</strong> si il y a collision, sinon <strong>{@code false}</strong>
	 */
	public boolean collidesWith(Balle autre) {		
		SVector2d distance = autre.position.substract(this.position);		
		return distance.modulus() <= this.rayon + autre.rayon && !this.equals(autre);
	}
	
	/**
	 * Dessine la balle
	 */
	@Override
	/**
	 * Dessine la balle
	 * @param g2d Le contexte graphique
	 * @param matMC La matrice de transformation monde-composante
	 */
	public void dessiner(Graphics2D g2d, AffineTransform matMC) {
		Color couleurALentree = g2d.getColor();
		AffineTransform matALEntree = g2d.getTransform();
		
		/* *****************On dessine toujours la forme a partir du centre.*********************/
		g2d.translate(-forme.getBounds2D().getWidth()/2 * matMC.getScaleX(), -forme.getBounds2D().getHeight()/2 * matMC.getScaleY());
		/* *****************FIN CENTRE***********************************************************/	
		g2d.translate(position.getX() * matMC.getScaleX(), position.getY() * matMC.getScaleY());
		//System.out.println(nom +  " " + position);
		
		try {
		g2d.setColor(getColor());
		g2d.fill(matMC.createTransformedShape(forme));
		g2d.setColor(Color.BLACK);
		g2d.draw(matMC.createTransformedShape(forme));
		
		if (drawVectors) {
		AffineTransform transT = g2d.getTransform();
		Color colorT = g2d.getColor();
		Stroke strokeT = g2d.getStroke();
		
		g2d.translate(forme.getBounds2D().getWidth()/2 * matMC.getScaleX(), forme.getBounds2D().getHeight()/2 * matMC.getScaleY());
		g2d.setColor(Color.orange);
		g2d.setStroke(new BasicStroke(5));
		vitesse.dessiner(g2d, matMC);
		g2d.setColor(Color.blue);
		acceleration.dessiner(g2d, matMC);
		g2d.setTransform(transT);
		g2d.setColor(colorT);
		g2d.setStroke(strokeT);
		}

		} catch (NullPointerException e) {
			System.out.println("Aucune forme associe au corps " + this.nom);
		}
		

	
		aire = (new Area(forme)).createTransformedArea(matMC).createTransformedArea(g2d.getTransform());
		
		g2d.setTransform(matALEntree);
		g2d.setColor(couleurALentree);

		

		
		for (Corps prochain : ouSuisJe.getADessiner().values()) {
			if (this.collidesWith(prochain, matMC)) {
				g2d.setColor(Color.CYAN);
				Area aireTemp = new Area(aire);
				aireTemp.intersect(new Area(prochain.aire));
				g2d.fill(aireTemp);
				g2d.setColor(couleurALentree);
			}
		}
		
		

		
		
		
	}
	

	/**
	 * Retourne le rayon de la Balle.
	 * @return Le rayon de la Balle
	 */
	public double getRayon() {
		return rayon;
	}
	
	/**
	 * Modifie le rayon de la Balle.
	 * @param rayon Le nouveau rayon
	 */
	public void setRayon(double rayon) {
		this.rayon = rayon;
		setForme(new Ellipse2D.Double(0, 0, 2*rayon, 2*rayon));
	}

	@Override
	public Corps clone(){
		Balle c = new Balle(this);
		return c;
	}

	@Override
	public void collisionAvecMur() {
		if (position.getX() + forme.getBounds2D().getWidth()/2 >= ouSuisJe.getLargeurMonde()/2) {
			setPosition(new SVector2d(ouSuisJe.getLargeurMonde()/2 - forme.getBounds2D().getWidth()/2, position.getY()));
			setVitesse(new SVector2d(vitesse.getX()*-COEFFICIENT_REBONDISSEMENT_MUR, vitesse.getY()));
		}
		
		//MUR DE GAUCHE
		if (position.getX() - forme.getBounds2D().getWidth()/2 <= -ouSuisJe.getLargeurMonde()/2) {
			setPosition(new SVector2d(-ouSuisJe.getLargeurMonde()/2 + forme.getBounds2D().getWidth()/2, position.getY()));
			setVitesse(new SVector2d(vitesse.getX()*-COEFFICIENT_REBONDISSEMENT_MUR, vitesse.getY()));
		}
		
		//MUR DU BAS
		if (position.getY() + forme.getBounds2D().getHeight()/2 >= ouSuisJe.getHauteurMonde()/2) {
			setPosition(new SVector2d(position.getX(), ouSuisJe.getHauteurMonde()/2 - forme.getBounds2D().getHeight()/2));
			setVitesse(new SVector2d(vitesse.getX(), vitesse.getY()*-COEFFICIENT_REBONDISSEMENT_MUR));
			
		}
		
		//MUR DU HAUT
		if (position.getY() - forme.getBounds2D().getHeight()/2 <= -ouSuisJe.getHauteurMonde()/2) {
			setPosition(new SVector2d(position.getX(), -ouSuisJe.getHauteurMonde()/2 + forme.getBounds2D().getHeight()/2  ));
			setVitesse(new SVector2d(vitesse.getX(), vitesse.getY()*-COEFFICIENT_REBONDISSEMENT_MUR));
		}
		
	}

}


