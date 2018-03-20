package corps;

import java.util.Arrays;

import interaction.ZoneInteraction;
import physique.SVector2d;

public class Mur extends Boite {
	
	private SVector2d normale;

	public Mur(String nom, SVector2d position, double largeur, double hauteur, ZoneInteraction ouSuisJe,
			double rotation, SVector2d normale) {
		super(nom, position, largeur, hauteur, ouSuisJe, rotation);
		this.normale = normale;
	}
	
	

	/**
	 * Cette methode est presente pour blinder l'application et met la vitesse du mur a 0. Elle s'assure que le mur ne bouge jamais.
	 * @param vitesse N/A
	 */
	@Override
	public void setVitesse(SVector2d vitesse) {
		this.vitesse = new SVector2d(0,0);
	}
	

	/**
	 * Cette methode est presente pour blinder l'application et ne fait rien. Elle s'assure que le mur ne bouge jamais.
	 * @param position N/A
	 *
	 *
	 */
	@Override
	public void setPosition(SVector2d position) {
		//Do nothing
	}
	
	@Override
	/**
	 * Gere une collision entre un mur et une boite
	 */
	public void gererCollisionsBoiteBoite() {
		try {
			for (Boite prochain : ouSuisJe.getToutesLesBoites().values()) {
				if (this.collidesWith(prochain) && !this.getNom().equals("boite") && !prochain.getNom().equals("boite")) { 
					
					
					if (prochain.rotation == 0 && !prochain.getClass().equals(Mur.class)) {
						
						this.gererCollisionSansRotation(prochain);
					}
	
					else {
					for (int i = 0 ; i < 5; i++ ) {
							this.pushOut(prochain, normale);
					}
					
					if (this.getNom().equals("MurGauche")) {
					prochain.setVitesse(new SVector2d(prochain.getVitesse().getX()*-COEFFICIENT_REBONDISSEMENT_MUR, prochain.getVitesse().getY()));
					}
					
					if (this.getNom().equals("MurDroite")) {
					prochain.setVitesse(new SVector2d(prochain.getVitesse().getX()*-COEFFICIENT_REBONDISSEMENT_MUR, prochain.getVitesse().getY()));	
					}
					
					if (this.getNom().equals("MurHaut")) {
						prochain.setVitesse(new SVector2d(prochain.getVitesse().getX(), prochain.getVitesse().getY()*-COEFFICIENT_REBONDISSEMENT_MUR));				
					}
					
					if (this.getNom().equals("MurBas")) {
						prochain.setVitesse(new SVector2d(prochain.getVitesse().getX(), prochain.getVitesse().getY()*-COEFFICIENT_REBONDISSEMENT_MUR));				
					}
				}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gere une collision entre un mur et une boite sans rotation.
	 * @param prochain La boite
	 */
	protected void gererCollisionSansRotation (Boite prochain) {
	SVector2d normale = new SVector2d();
		
		double distanceXD = Math.abs((prochain.getPosition().getX() - prochain.getLargeur()/2) - (this.getPosition().getX() + this.getLargeur()/2));
		double distanceXG = Math.abs((this.getPosition().getX() - this.getLargeur()/2) - (prochain.getPosition().getX() + prochain.getLargeur()/2));
		double distanceYB = Math.abs((prochain.getPosition().getY() - prochain.getHauteur()/2) - (this.getPosition().getY() + this.getHauteur()/2));
		double distanceYH = Math.abs((this.getPosition().getY() - this.getHauteur()/2) - (prochain.getPosition().getY() + prochain.getHauteur()/2));

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
		


		for (int i = 0 ; i < 10; i++ ) {
			this.pushOut(prochain, normale);
		}
		
		if (this.getNom().equals("MurGauche")) {
		prochain.setVitesse(new SVector2d(prochain.getVitesse().getX()*-COEFFICIENT_REBONDISSEMENT_MUR, prochain.getVitesse().getY()));
		}
		
		if (this.getNom().equals("MurDroite")) {
		prochain.setVitesse(new SVector2d(prochain.getVitesse().getX()*-COEFFICIENT_REBONDISSEMENT_MUR, prochain.getVitesse().getY()));	
		}
		
		if (this.getNom().equals("MurHaut")) {
			prochain.setVitesse(new SVector2d(prochain.getVitesse().getX(), prochain.getVitesse().getY()*-COEFFICIENT_REBONDISSEMENT_MUR));				
		}
		
		if (this.getNom().equals("MurBas")) {
			prochain.setVitesse(new SVector2d(prochain.getVitesse().getX(), prochain.getVitesse().getY()*-COEFFICIENT_REBONDISSEMENT_MUR));				
		}
		
	}

}
