package corps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import annotation.Editable;
import annotation.Obstruct;
import inspecteur.PropReel;
import interaction.ZoneInteraction;
import physique.Materiaux;
import physique.SVector2d;
import util.Images;
import util.MathUtil;

/**
 * Corps constament fixe qui genere un champ magnetique autour de lui
 * @author Leo Jetzer
 */
@Obstruct(Corps.PROPRIETE_ACCELERATION)
@Obstruct(Corps.PROPRIETE_MASSE)
@Obstruct(Corps.PROPRIETE_MATERIAU)
@Obstruct(Corps.PROPRIETE_STATIQUE)
@Obstruct(Corps.PROPRIETE_VITESSE)
@Editable(nom="Force", get="getForceMagnetique", set="setForceMagnetique", type=PropReel.class)
public class Aimant extends Balle{
	private static final SVector2d ZERO = new SVector2d();
	private static final BufferedImage IMG_AIMANT = Images.safeCharger("aimant.png");
	private static final Color COULEUR = new Color(0, 0, 0, 0);

	private double forceMagnetique = 0;
	
	/**
	 * Creer un nouvel aimant
	 * @param nom - {@code String} : le nom de l'objet
	 * @param position - {@code SVector2d} : la position de l'objet
	 * @param rayon - {@code double} : le rayon de l'aimant
	 * @param ouSuisJe - {@link ZoneInteraction} : la zone d'interaction dans laquelle se trouve l'aimant
	 */
	public Aimant(String nom, SVector2d position, double rayon, ZoneInteraction ouSuisJe) {
		super(nom, position, rayon, ouSuisJe);
	}
	
	/**
	 * Change l'intensite de l'aimant
	 * @param force - {@code double} : la nouvelle intensite
	 */
	public void setForceMagnetique(double force){ forceMagnetique = force; }
	/**
	 * Retourne l'intensite de l'aimant
	 * @return l'intensite de l'aimant
	 */
	public double getForceMagnetique(){ return forceMagnetique; }
	
	@Override
	public void prochaineIterationPhysique(){
		for (Corps obj : ouSuisJe.getADessiner().values()){
			double charge = obj.getCharge();
			if (!MathUtil.nearlyEquals(charge, 0)){
				SVector2d vecDistance = obj.position.substract(this.position);
				double champ = forceMagnetique / vecDistance.dot(vecDistance);
				// F = B|q|v*sin(a)
				// sin(a) == 1 parce que dans notre cas, la vitesse va toujours etre perpendiculaire au champ magnetique
				double module = champ * charge * obj.vitesse.modulus();
				SVector2d force = new SVector2d(-obj.vitesse.getY(), obj.vitesse.getX()).multiply(module);
				obj.setForce(force);
			}
		}
	}

	@Override
	public void dessiner(Graphics2D g2d, AffineTransform matMC){
		
		AffineTransform m = (AffineTransform)g2d.getTransform().clone();
		g2d.translate(-forme.getBounds2D().getWidth()/2 * matMC.getScaleX(), -forme.getBounds2D().getHeight()/2 * matMC.getScaleY());
		g2d.translate(position.getX() * matMC.getScaleX(), position.getY() * matMC.getScaleY());
		
		Rectangle bounds = matMC.createTransformedShape(getForme()).getBounds();
		g2d.drawImage(IMG_AIMANT, bounds.x, bounds.y, bounds.width, bounds.height, null);
		
		aire = (new Area(forme)).createTransformedArea(matMC).createTransformedArea(g2d.getTransform());
		
		g2d.setTransform(m);
	}
	
	// Override de methode de Corps
	@Override
	public void appliquerForce(SVector2d force){}
	@Override
	public SVector2d getQuantiteDeMouvement(){ return ZERO; }
	@Override
	public SVector2d getAcceleration(){ return ZERO; }
	@Override
	public SVector2d getVitesse(){ return ZERO; }
	@Override
	public SVector2d getForce(){ return ZERO; }
	@Override
	public void setVitesse(SVector2d vitesse){}
	@Override
	public void setAcceleration(SVector2d vitesse){}
	@Override
	public void setForce(SVector2d vitesse){}
	@Override
	public double getMasse(){ return 0; }
	@Override
	public double getCharge(){ return 0; }
	@Override
	public boolean isStatique(){ return true; }
	@Override
	public Materiaux getMateriau(){ return null; }
	@Override
	public Corps setMateriau(Materiaux mat){ return this; }
	@Override
	public Color getColor(){ return COULEUR; }
	@Override
	public Aimant clone(){
		Aimant aimant = new Aimant(this.nom, this.position, this.getRayon(), this.ouSuisJe);
		aimant.forceMagnetique = this.forceMagnetique;
		return aimant;
	}
}
