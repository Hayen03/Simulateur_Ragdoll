package corps;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.ConcurrentModificationException;

import annotation.Editable;
import interaction.ZoneInteraction;
import inspecteur.PropReel;
import physique.SVector2d;
import util.MathUtil;

@Editable(nom="largeur", get="getLargeur", set="setLargeur", type=PropReel.class)
@Editable(nom="hauteur", get="getHauteur", set="setHauteur", type=PropReel.class)
@Editable(nom="rotation", get="getRotation", set="setRotation", type=PropReel.class)
public class Boite extends Corps {
	
	protected double rotation = 0; //rad
	
	private double largeur;
	private double hauteur;
	private Bounds bounds;

	/*
	 * Les noms sont bizarre mais c'est pour l'inspecteur
	 */
	public static final double MIN_largeur = 0.1, MIN_hauteur = 0.1, MIN_rotation = 0;
	public static final double MAX_largeur = 10, MAX_hauteur = 10, MAX_rotation = 2*Math.PI;
	public static final double DEFAUT_largeur = 0.1, DEFAUT_hauteur = 0.1, DEFAUT_rotation = 0;
	
	/**
	 * La classe <code>Boite</code> represente un <code>Corps</code> rectangulaire possedant
	 * une largeur et une hauteur ainsi qu'une rotation. Ce constructeur construit une <code>Boite</code>
	 * selon les parametres specifies.
	 * 
	 * @param nom le nom de la Boite.
	 * @param position la position de la Boite.
	 * @param largeur la largeur de la Boite.
	 * @param hauteur l'hauteur de la Boite.
	 * @param ouSuisJe la <code>ZoneInteraction</code>) ou se trouve la Boite.
	 * @param rotation la rotation de la Boite.
	 */

	public Boite(String nom, SVector2d position, double largeur, double hauteur, ZoneInteraction ouSuisJe, double rotation) {
		super(nom, position, new Rectangle2D.Double(0, 0, largeur, hauteur), ouSuisJe);

		AffineTransform matTemp = new AffineTransform();
		this.rotation = rotation;
		this.forme = matTemp.createTransformedShape(forme);
		this.largeur = largeur;
		this.hauteur = hauteur;
		initializeBounds();
	}


	/**
	 * Methode qui initialise les vecteurs representant les coins d'une boite
	 */
	private void initializeBounds() {
		this.bounds = new Bounds(this);
	}
	
	/**
	 * Methode qui gere les collisions entre une boite et une autre boite ou une balle
	 */
	@Override
	public void gererCollisions(){
		gererCollisionsBoiteBoite();
		gererCollisionBoiteBalle();
	}

	/**
	 * Methode qui gere les collisions entre deux boites (s'il y a lieu)
	 * 
	 */
	protected void gererCollisionsBoiteBoite() {
		//CAS BOITE-BOITE
		try {
			for (Boite prochain : ouSuisJe.getToutesLesBoites().values()) {
				if (this.collidesWith(prochain)) { 
					

					
					
					if (this.rotation == 0 && prochain.rotation == 0) {
						
						SVector2d normale = new SVector2d();
						
						double distanceXD = Math.abs((prochain.getPosition().getX() - prochain.getLargeur()/2) - (this.getPosition().getX() + this.largeur/2));
						double distanceXG = Math.abs((this.getPosition().getX() - this.largeur/2) - (prochain.getPosition().getX() + prochain.getLargeur()/2));
						double distanceYB = Math.abs((prochain.getPosition().getY() - prochain.getHauteur()/2) - (this.getPosition().getY() + this.hauteur/2));
						double distanceYH = Math.abs((this.getPosition().getY() - this.hauteur/2) - (prochain.getPosition().getY() + prochain.getHauteur()/2));

						double distances[] = new double[4];
						distances[0] = distanceXG;
						distances[1] = distanceXD;
						distances[2] = distanceYH;
						distances[3] = distanceYB;
						

						
						
						Arrays.sort(distances);
						if (distances[0] == distanceXG) {

							normale = new SVector2d(-1,0);
						}
						
						else if (distances[0] == distanceXD) {

							normale = new SVector2d(1,0);
						}
						
						else if (distances[0] == distanceYH) {

							normale = new SVector2d(0,-1);
						}
						
						else  {

							normale = new SVector2d(0,1);
						}
						
						normale = normale.normalize();


						for (int i = 0 ; i < 25; i++) {
							this.pushOut(prochain, normale);
						}
						
						



						double energie = this.calculerImpulsion(prochain, normale);


						this.vitesse = this.vitesse.add(normale.multiply(energie/this.getMasse()));
						prochain.vitesse = prochain.vitesse.substract(normale.multiply(energie/prochain.getMasse()));
								
					}
					
					else {
						System.out.println ("@Boite 144");
						SVector2d normale = prochain.position.substract(this.position);
						try {
							normale = normale.normalize();
						} catch (Exception e) {
							System.out.println("Le vecteur ne peut etre normalisee...");
						}
						

						for (int i = 0 ; i < 25; i++) {
							this.pushOut(prochain, normale);
						}
 


						double energie = this.calculerImpulsion(prochain, normale);


						this.vitesse = this.vitesse.add(normale.multiply(energie/this.getMasse()));
						prochain.vitesse = prochain.vitesse.add(normale.multiply(-energie/prochain.getMasse()));
					}
	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Methode qui gere les collisions entre une boite et une balle (s'il y a lieu)
	 */
	private void gererCollisionBoiteBalle() {
		//CAS BALLE-BOITE
		try {

			for (Balle prochain : ouSuisJe.getToutesLesBalles().values()) {
				
				if (prochain.collidesWith(this)) {
					SVector2d normale = prochain.calculerLaNormale(this).multiply(-1);
			

					
					try {
						normale = normale.normalize();
					} catch (Exception e) {
						System.out.println("Le vecteur ne peut etre normalisee...");
					}
					
					while (prochain.collidesWith(this)) {
						this.pushOut(prochain, prochain.position.substract(this.position));
					}
					
					
					

					double energie = this.calculerImpulsion(prochain, normale);
				
					this.vitesse = this.vitesse.add(normale.multiply(energie/this.getMasse()));
					if (!prochain.isStatique())
						prochain.vitesse = prochain.vitesse.add(normale.multiply(-energie/prochain.getMasse()));

				
				}
			}
		} catch (ConcurrentModificationException e) {
			// TODO Auto-generated catch block
			System.out.println("WARNING :" + e.getClass().toGenericString() + " dans Balle!!!");
		}
	}
	
	@Override
	public synchronized void prochaineIterationPhysique (){
		super.prochaineIterationPhysique();
		this.bounds.update();
	}

	

	/**
	 * Methode qui retourne l'aire surfacique d'un rectangle
	 */
	@Override
	public double getSurface(){
		return largeur*hauteur;
	}
	


	/**
	 * Test si la boite est en collision avec une autre boite
	 * @param autre - {@code Balle} : La balle
	 * @return <strong>{@code true}</strong> si il y a collision, sinon <strong>{@code false}</strong>
	 */
	public boolean collidesWith(Boite autre) {
		
		double distance = Math.abs(autre.position.substract(this.position).modulus());
		double rayon1 = Math.abs(this.position.substract(this.bounds.getUR()).modulus());
		double rayon2 = Math.abs(autre.position.substract(autre.bounds.getUR()).modulus());
		if (distance > rayon1 + rayon2) {
			return false;
		}
		
		SVector2d[] axis = new SVector2d[4];
		axis[0] = new SVector2d(this.bounds.getUR().getX() - this.bounds.getUL().getX(), this.bounds.getUR().getY() - this.bounds.getUL().getY());
		axis[1] = new SVector2d(this.bounds.getUR().getX() - this.bounds.getLR().getX(), this.bounds.getUR().getY() - this.bounds.getLR().getY());
		axis[2] = new SVector2d(autre.bounds.getUL().getX() - autre.bounds.getLL().getX(), autre.bounds.getUL().getY() - autre.bounds.getLL().getY());	
		axis[3] = new SVector2d(autre.bounds.getUL().getX() - autre.bounds.getUR().getX(), autre.bounds.getUL().getY() - autre.bounds.getUR().getY());
		
		SVector2d[] cornersThis = new SVector2d[4];
		cornersThis[0] = this.bounds.getUL();
		cornersThis[1] = this.bounds.getUR();
		cornersThis[2] = this.bounds.getLL();
		cornersThis[3] = this.bounds.getLR();
		
		SVector2d[] cornersAutre = new SVector2d[4];
		cornersAutre[0] = autre.bounds.getUL();
		cornersAutre[1] = autre.bounds.getUR();
		cornersAutre[2] = autre.bounds.getLL();
		cornersAutre[3] = autre.bounds.getLR();
		
		//AXIS 1
		SVector2d[] projectedThisOnAxis1 = new SVector2d[4];
		double projectedTA1[] = new double[4];
		for (int i = 0 ; i < 4 ; i++) {
			projectedThisOnAxis1[i] = MathUtil.projectionOrthogonale(cornersThis[i], axis[0]);
			projectedTA1[i] = projectedThisOnAxis1[i].dot(axis[0]);
		}
		
		Arrays.sort(projectedTA1);
		double minTA1 = projectedTA1[0];
		double maxTA1 = projectedTA1[3];
		
		
		SVector2d[] projectedAutreOnAxis1 = new SVector2d[4];
		double projectedAA1[] = new double[4];
		for (int i = 0 ; i < 4 ; i++) {
			projectedAutreOnAxis1[i] = MathUtil.projectionOrthogonale(cornersAutre[i], axis[0]);
			projectedAA1[i] = projectedAutreOnAxis1[i].dot(axis[0]);
		}
		
		Arrays.sort(projectedAA1);
		double minAA1 = projectedAA1[0];
		double maxAA1 = projectedAA1[3];
		//AXIS 1 FIN
		
		//AXIS 2
		SVector2d[] projectedThisOnAxis2 = new SVector2d[4];
		double projectedTA2[] = new double[4];
		for (int i = 0 ; i < 4 ; i++) {
			projectedThisOnAxis2[i] = MathUtil.projectionOrthogonale(cornersThis[i], axis[1]);
			projectedTA2[i] = projectedThisOnAxis2[i].dot(axis[1]);
		}
		
		Arrays.sort(projectedTA2);
		double minTA2 = projectedTA2[0];
		double maxTA2 = projectedTA2[3];
		
		
		SVector2d[] projectedAutreOnAxis2 = new SVector2d[4];
		double projectedAA2[] = new double[4];
		for (int i = 0 ; i < 4 ; i++) {
			projectedAutreOnAxis2[i] = MathUtil.projectionOrthogonale(cornersAutre[i], axis[1]);
			projectedAA2[i] = projectedAutreOnAxis2[i].dot(axis[1]);
		}
		
		Arrays.sort(projectedAA2);
		double minAA2 = projectedAA2[0];
		double maxAA2 = projectedAA2[3];
		//AXIS 2 FIN
		
		//AXIS 3
		SVector2d[] projectedThisOnAxis3 = new SVector2d[4];
		double projectedTA3[] = new double[4];
		for (int i = 0 ; i < 4 ; i++) {
			projectedThisOnAxis3[i] = MathUtil.projectionOrthogonale(cornersThis[i], axis[2]);
			projectedTA3[i] = projectedThisOnAxis3[i].dot(axis[2]);
		}
		
		Arrays.sort(projectedTA3);
		double minTA3 = projectedTA3[0];
		double maxTA3 = projectedTA3[3];
		
		
		SVector2d[] projectedAutreOnAxis3 = new SVector2d[4];
		double projectedAA3[] = new double[4];
		for (int i = 0 ; i < 4 ; i++) {
			projectedAutreOnAxis3[i] = MathUtil.projectionOrthogonale(cornersAutre[i], axis[2]);
			projectedAA3[i] = projectedAutreOnAxis3[i].dot(axis[2]);
		}
		
		Arrays.sort(projectedAA3);
		double minAA3 = projectedAA3[0];
		double maxAA3 = projectedAA3[3];
		//AXIS 3 FIN
		
		//AXIS 4
		SVector2d[] projectedThisOnAxis4 = new SVector2d[4];
		double projectedTA4[] = new double[4];
		for (int i = 0 ; i < 4 ; i++) {
			projectedThisOnAxis4[i] = MathUtil.projectionOrthogonale(cornersThis[i], axis[3]);
			projectedTA4[i] = projectedThisOnAxis4[i].dot(axis[3]);
		}
		
		Arrays.sort(projectedTA4);
		double minTA4 = projectedTA4[0];
		double maxTA4 = projectedTA4[3];
		
		
		SVector2d[] projectedAutreOnAxis4 = new SVector2d[4];
		double projectedAA4[] = new double[4];
		for (int i = 0 ; i < 4 ; i++) {
			projectedAutreOnAxis4[i] = MathUtil.projectionOrthogonale(cornersAutre[i], axis[3]);
			projectedAA4[i] = projectedAutreOnAxis4[i].dot(axis[3]);
		}
		
		Arrays.sort(projectedAA4);
		double minAA4 = projectedAA4[0];
		double maxAA4 = projectedAA4[3];
		//AXIS 4 FIN
		
		
		if ((minAA1 <= maxTA1 && maxAA1 >= minTA1) &&
			(minAA2 <= maxTA2 && maxAA2 >= minTA2) &&
			(minAA3 <= maxTA3 && maxAA3 >= minTA3) &&
			(minAA4 <= maxTA4 && maxAA4 >= minTA4) &&
			!this.equals(autre)
				) {
			return true;
		}
	
		
		else {
		return false;		
		}
		
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
		//TODO
		Color couleurALentree = g2d.getColor();
		AffineTransform matALEntree = g2d.getTransform();

		/* *****************On dessine toujours la forme a partir du centre.*********************/
		g2d.translate(-forme.getBounds2D().getWidth()/2 * matMC.getScaleX(), -forme.getBounds2D().getHeight()/2 * matMC.getScaleY());
		/* *****************FIN CENTRE***********************************************************/	
		g2d.translate(position.getX() * matMC.getScaleX(), position.getY() * matMC.getScaleY());
		g2d.rotate(-rotation, (ouSuisJe.getLargeurMonde()/2 + largeur/2) * matMC.getScaleX(), (ouSuisJe.getHauteurMonde()/2 + hauteur/2) * matMC.getScaleY());
		try {
			g2d.setColor(getMateriau().getColor());

			g2d.fill(matMC.createTransformedShape(forme));
			g2d.setColor(Color.BLUE);
			g2d.draw(matMC.createTransformedShape(new Line2D.Double(centerPoint.getX() -0.5, centerPoint.getY(), centerPoint.getX() + 0.5,centerPoint.getY())));
			g2d.draw(matMC.createTransformedShape(new Line2D.Double(centerPoint.getX(), centerPoint.getY() - 0.5, centerPoint.getX(), centerPoint.getY() + 0.5)));
			g2d.setColor(Color.BLACK);
			g2d.draw(matMC.createTransformedShape(forme));
//			System.out.println(matMC.getScaleX());
			
			if (drawVectors) {
				AffineTransform transT = g2d.getTransform();
				Color colorT = g2d.getColor();
				Stroke strokeT = g2d.getStroke();
				g2d.rotate(rotation, (ouSuisJe.getLargeurMonde()/2 + largeur/2) * matMC.getScaleX(), (ouSuisJe.getHauteurMonde()/2 + hauteur/2) * matMC.getScaleY());
				g2d.setStroke(new BasicStroke(5));
		
				g2d.translate(forme.getBounds2D().getWidth()/2 * matMC.getScaleX(), forme.getBounds2D().getHeight()/2 * matMC.getScaleY());
				//g2d.translate(-position.getX() * matMC.getScaleX(), -position.getY() * matMC.getScaleY());
				g2d.setColor(Color.orange);
				vitesse.dessiner(g2d, matMC);
				g2d.setColor(Color.blue);
				acceleration.dessiner(g2d, matMC);
				g2d.setColor(Color.RED);
		
			
				
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




		/*for (Corps prochain : ouSuisJe.getADessiner().values()) {
			if (this.collidesWith(prochain, matMC)) {
				g2d.setColor(Color.CYAN);
				Area aireTemp = new Area(aire);
				aireTemp.intersect(new Area(prochain.aire));
				g2d.fill(aireTemp);
				g2d.setColor(couleurALentree);
			}
		}*/


		for (Boite prochain : ouSuisJe.getToutesLesBoites().values()) {
			if (this.collidesWith(prochain)) {
				g2d.setColor(Color.CYAN);
				Area aireTemp = new Area(aire);
				aireTemp.intersect(new Area(prochain.aire));
				g2d.fill(aireTemp);
				g2d.setColor(couleurALentree);
			}
		}



	}
	/**
	 * Retourne la largeur de la boite.
	 * @return la largeur de la boite.
	 */

	public double getLargeur() {
		return largeur;
	}
	
	/**
	 * Modifie la largeur de la boite
	 * @param largeur la nouvelle largeur de la boite.
	 */

	public void setLargeur(double largeur) {
		this.largeur = largeur;
		this.forme = new Rectangle2D.Double(0,0,largeur,hauteur);
		this.centerPoint = computeCenter();
	}
	
	/**
	 * Retourne la hauteur de la boite.
	 * @return la hauteur de la boite.
	 */

	public double getHauteur() {
		return hauteur;
	}
	
	/**
	 * Modifie la hauteur de la boite.
	 * @param hauteur la nouvelle hauteur.
	 */

	public void setHauteur(double hauteur) {
		this.hauteur = hauteur;
		this.forme = new Rectangle2D.Double(0,0,largeur,hauteur);
		this.centerPoint = computeCenter();
	}
	
	/**
	 * Retourne la rotation du <code>Corps</code>.
	 * @return La rotation du <code>Corps</code> en rad.
	 */
	public double getRotation() {
		return rotation;
	}
	/**
	 * Modifie la rotation du <code>Corps</code>.
	 * @param rotation La nouvelle rotation du <code>Corps</code> en rad.
	 */
	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
	
	@Override
	public Boite clone(){
		return new Boite(this.nom, this.position, this.largeur, this.hauteur, this.ouSuisJe, this.rotation);
	}
	
	/**
	 * La classe <code> Bounds </code> implemente une representation vectorielle des positions des quatre coins d'une boite.
	 * @author Marcus Phan
	 *
	 */
	
	private class Bounds {
		SVector2d UL;
		SVector2d UR;
		SVector2d LL;
		SVector2d LR;
		
		Boite boite;
		
		/**
		 * Cree un nouveau Bounds a partir de la boite passee en parametre.
		 * @param boite La boite 
		 */
		
		Bounds (Boite boite) {
			this.boite = boite;
			SVector2d ULt = new SVector2d(boite.getPosition().getX() - boite.getLargeur()/2, boite.getPosition().getY() - boite.getHauteur()/2);
			SVector2d URt = new SVector2d(boite.getPosition().getX() + boite.getLargeur()/2, boite.getPosition().getY() - boite.getHauteur()/2);
			SVector2d LLt = new SVector2d(boite.getPosition().getX() - boite.getLargeur()/2, boite.getPosition().getY() + boite.getHauteur()/2);
			SVector2d LRt = new SVector2d(boite.getPosition().getX() + boite.getLargeur()/2, boite.getPosition().getY() + boite.getHauteur()/2);
			AffineTransform rot = new AffineTransform();
			double[] matriceTrans = new double[6];
			rot.rotate(boite.getRotation());
			rot.getMatrix(matriceTrans);
	
			
			UL = new SVector2d(ULt.getX() * matriceTrans[0] + ULt.getY() * matriceTrans[1], ULt.getX() * matriceTrans[2] + ULt.getY() * matriceTrans[3]);
			UR = new SVector2d(URt.getX() * matriceTrans[0] + URt.getY() * matriceTrans[1], URt.getX() * matriceTrans[2] + URt.getY() * matriceTrans[3]);
			LL = new SVector2d(LLt.getX() * matriceTrans[0] + LLt.getY() * matriceTrans[1], LLt.getX() * matriceTrans[2] + LLt.getY() * matriceTrans[3]);
			LR = new SVector2d(LRt.getX() * matriceTrans[0] + LRt.getY() * matriceTrans[1], LRt.getX() * matriceTrans[2] + LRt.getY() * matriceTrans[3]);
//			String tostring = "";
//			for (int i = 0 ; i<matriceTrans.length; i++) {
//			tostring += matriceTrans[i] + " ";
//			}

		}

		/**
		 * 
		 * @return Un vecteur representant le coin en haut a gauche
		 */
		public SVector2d getUL() {
			return UL;
		}

		/**
		 * 
		 * @return Un vecteur representant le coin en haut a droite
		 */
		public SVector2d getUR() {
			return UR;
		}

		/**
		 * 
		 * @return Un vecteur representant le coin en bas a gauche
		 */
		public SVector2d getLL() {
			return LL;
		}

		/**
		 * 
		 * @return Un vecteur representant le coin en bas a droite
		 */
		public SVector2d getLR() {
			return LR;
		}


		
		/**
		 * Reconstruit l'objet <code>Bounds</code> en fonction de la boite qui lui est associe
		 */
		public void update () {
			
			
			//ordre des matrices : scale, rotation, translation.
			
			//SCALE
			SVector2d ULt = new SVector2d(-boite.getLargeur()/2, - boite.getHauteur()/2);
			SVector2d URt = new SVector2d(boite.getLargeur()/2, - boite.getHauteur()/2);
			SVector2d LLt = new SVector2d(-boite.getLargeur()/2, boite.getHauteur()/2);
			SVector2d LRt = new SVector2d(boite.getLargeur()/2, boite.getHauteur()/2);
			AffineTransform rot = new AffineTransform();
			double[] matriceTrans = new double[6];

			rot.rotate(boite.getRotation());
			rot.getMatrix(matriceTrans);
			//ROTATION
			UL = new SVector2d(ULt.getX()  * matriceTrans[0] + ULt.getY() * matriceTrans[1], ULt.getX() * matriceTrans[2] + ULt.getY() * matriceTrans[3]).multiply(1);
			UR = new SVector2d(URt.getX() * matriceTrans[0] + URt.getY() * matriceTrans[1], URt.getX() * matriceTrans[2] + URt.getY() * matriceTrans[3]).multiply(1);
			LL = new SVector2d(LLt.getX() * matriceTrans[0] + LLt.getY() * matriceTrans[1], LLt.getX() * matriceTrans[2] + LLt.getY() * matriceTrans[3]).multiply(1);
			LR = new SVector2d(LRt.getX() * matriceTrans[0] + LRt.getY() * matriceTrans[1], LRt.getX() * matriceTrans[2] + LRt.getY() * matriceTrans[3]).multiply(1);
		
			//TRANSLATION
			UL = UL.add(boite.getPosition());
			UR = UR.add(boite.getPosition());
			LL = LL.add(boite.getPosition());
			LR = LR.add(boite.getPosition());
			
			/*UL = new SVector2d(ULt.getX() * Math.cos(boite.getRotation()) - ULt.getY() * Math.sin(rotation), ULt.getX() * Math.sin(boite.getRotation() + ULt.getY() * Math.cos(boite.getRotation())));
			UR = new SVector2d(URt.getX() * Math.cos(boite.getRotation()) - URt.getY() * Math.sin(rotation), URt.getX() * Math.sin(boite.getRotation() + URt.getY() * Math.cos(boite.getRotation())));
			LL = new SVector2d(LLt.getX() * Math.cos(boite.getRotation()) - LLt.getY() * Math.sin(rotation), LLt.getX() * Math.sin(boite.getRotation() + LLt.getY() * Math.cos(boite.getRotation())));
			LR = new SVector2d(LRt.getX() * Math.cos(boite.getRotation()) - LRt.getY() * Math.sin(rotation), LRt.getX() * Math.sin(boite.getRotation() + LRt.getY() * Math.cos(boite.getRotation())));
			*/
		}
		
		
	}

	@Override
	public void collisionAvecMur() {
		// TODO Auto-generated method stub
		
	}

}
