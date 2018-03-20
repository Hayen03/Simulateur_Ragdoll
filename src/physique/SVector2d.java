package physique;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Cette classe est une version 2d simplifiee de la classe SVector3d ecrite par Simon Vezina.
 * Elle permet de realiser les operations de base sur un vecteur en deux dimensions (x,y)
 */
public class SVector2d {
	//champs de base
	private static final double EPSILON = 1e-10; //tolerance utilisee dans les comparaisons reeles avec zero
	private double x;	//composante x du vecteur 2d
	private double y;	//composante y du vecteur 2d
	
	//champs necessaires a la representation graphique
	private Line2D.Double corps, traitDeTete;
	private double angleRotTete = 0.3; //angle entre le vecteur et un des segments formant la tete (en radians)
	private double longueurTete = 1; //longueur des segments formant la tete
	private boolean premiereFois=true;
	
	/**
	 * Constructeur representant un vecteur 2d aux composantes nulles
	 */
	public SVector2d(){
		x = 0;
		y = 0;
	}
	
	/**
	 * Constructeur avec composantes x,y
	 * @param x - La composante x du vecteur.
	 * @param y - La composante y du vecteur.
	 */
	public SVector2d(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	private void creerRepresentationGeometrique() {
		corps = new Line2D.Double(0, 0, x, y); //ligne format le corps du vecteur
		
		//par trigonometrie, former un petit trait qui formera chacune des pointes de la tete du vecteur
		double moduleVec  = modulus();
		double ratio = (moduleVec - longueurTete)/moduleVec;
		//le trait est initialement confondu avec le corps de la fleche
		traitDeTete = new Line2D.Double( x*(ratio), y*(ratio), x, y);
	}
	
	
	public void dessiner(Graphics2D g2d, AffineTransform matMC) {	
		if (premiereFois) {
			creerRepresentationGeometrique();
			premiereFois = false;
		}
		//sauvegarde des transformations courantes
		AffineTransform mat = g2d.getTransform();
		AffineTransform matT = new AffineTransform(matMC);
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.draw(matMC.createTransformedShape(corps));  //ligne formant le vecteur lui-meme

		matT.rotate(angleRotTete,x , y);
		g2d.draw(matT.createTransformedShape(traitDeTete));  //un des deux traits qui forment la tete du vecteur
		
		matT.rotate(-2*angleRotTete,x , y);
		g2d.draw(matT.createTransformedShape(traitDeTete));  //un des deux traits qui forment la tete du vecteur
		
		g2d.setTransform(mat);  //restauration des transformations initiales (annule les rotations)

	}// fin
	
	/**
	 * Methode qui donne acces a la coordonnee x du vecteur.
	 * @return La coordonnee x.
	 */
	public double getX(){ 
	  return x;
	}
	
	/**
	 * Methode qui donne acces a la coordonnee y du vecteur.
	 * @return La coordonnee y.
	 */
	public double getY(){ 
	  return y;
	}
	
	@Override
	public String toString(){
		return "[ x = " + x + ", y = " + y + "]";		
	}	
	
	  @Override
	  public boolean equals(Object obj) {
	    if(this == obj)
	      return true;
	    
	    if(obj == null)
	      return false;
	    
	    if(!(obj instanceof SVector2d))
	      return false;
	    
	    SVector2d other = (SVector2d) obj;
	    
	    //Comparaison des valeurs x,y et z en utilisant la precision de EPSILON modulee par la valeur a comparer
	    if(Math.abs(x - other.x) > Math.abs(x)*EPSILON)
	      return false;
	    
	    if(Math.abs(y - other.y) > Math.abs(y)*EPSILON)
	      return false;
	    
	    return true;
	  }
	
	/**
	 * Methode qui retourne l'addition de deux vecteurs. 
	 * @param v - Le vecteur a ajouter au vecteur actuel
	 * @return La somme des deux vecteurs
	 */
	public SVector2d add(SVector2d v){	
		return new SVector2d(x + v.x, y + v.y);
	}
	
	/**
	 * Methode qui retourne la soustraction de deux vecteurs. 
	 * @param v - Le vecteur a soustraire au vecteur actuel.
	 * @return La soustraction des deux vecteurs.
	 */
	public SVector2d substract(SVector2d v){
		return new SVector2d(x - v.x, y - v.y);
	}
	
	/**
	 * Methode qui effectue la multiplication du vecteur actuel par une scalaire.
	 * @param m - Le muliplicateur.
	 * @return Le resultat de la multiplication par un scalaire m sur le vecteur.
	 */
	public SVector2d multiply(double m){
		return new SVector2d(m*x, m*y);
	}
	
	/**
	 * Methode pour obtenir le module d'un vecteur.
	 * @return Le module du vecteur.
	 */
	public double modulus(){
		return Math.sqrt((x*x) + (y*y));
	}
	

	/* @throws Exception Si le vecteur ne peut pas etre normalise etant trop petit (modulus() {@code <}  EPSILON) ou de longueur nulle. */
	/**
	 * Methode pour normaliser un vecteur 2d
	 * Un vecteur normalise possede la meme orientation, mais possede une longeur unitaire.
	 * Si le module du vecteur est nul, le vecteur normalise sera le vecteur nul (0.0, 0.0).
	 * @return Le vecteur normalise.
	 */
	public SVector2d normalize()
	{
		double mod = modulus();			//obtenir le module du vecteur
		
		//Verification du module. S'il est trop petit, nous ne pouvons pas numeriquement normaliser ce vecteur
		if(mod < EPSILON) 
		  throw new RuntimeException("Erreur SVector2d 001 : Le vecteur " + this.toString() + " etant nul ou presque nul ne peut pas etre numeriquement normalise.");
		else
			return new SVector2d(x/mod, y/mod);
	}
	
	/**
	 * Methode pour effectuer le produit scalaire avec un autre vecteur v.
	 * @param v - L'autre vecteur en produit scalaire.
	 * @return Le produit scalaire entre les deux vecteurs.
	 */
	public double dot(SVector2d v){
		return (x*v.x + y*v.y);
	}
	
	public Point2D.Double vectorToPoint () {
		return new Point2D.Double(this.x, this.y);
		
	}
	
	public SVector2d calculerNormale () {
		return new SVector2d (y,-x);
	}

 
}//fin SVector2d
