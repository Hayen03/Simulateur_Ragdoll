package aaplication;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import util.MathUtil;

/**
 * Classe representant un graphique
 * @author Leo Jetzer
 *
 */
public class Graphique extends JPanel {
	private static final long serialVersionUID = 8919496609091466514L;
	
	private final double[][] valeurs;
	private int debut, fin;
	private final int nbValeurMax;
	private int nbValeurs, taille;
	private double intervallePoint, intervalleY;
	private int pas;
	private double minY, maxY, axeY;
	private int n;
	private Color[] couleurLigne;
	private int grosseurTrait, grosseurCurseur;
	private AffineTransform trans;
	private boolean afficherEtiquetteX;

	/**
	 * Creer un nouveau graphique pouvant contenir {@code nbValeurs} valeurs
	 * @param nbValeurs - {@code int} : le nombre de valeur que peut contenir le graphique
	 * @param intervalle - {@code double} : l'intervalle entre deux valeurs
	 */
	public Graphique(int nbValeurs, int nbLigne, double intervalle){
		nbValeurMax = nbValeurs;
		taille = nbValeurs+1;
		valeurs = new double[nbLigne][nbValeurMax+1];
		this.nbValeurs = 0;
		debut = fin = 0;
		intervallePoint = intervalle;
		intervalleY = 1;
		minY = -10;
		maxY = 10;
		n = 0;
		pas = 1;
		couleurLigne = new Color[nbLigne];
		for (int i = 0; i < nbLigne; i++)
			couleurLigne[i] = Color.green;
		setBackground(Color.white);
		grosseurTrait = 3;
		grosseurCurseur = 5;
		afficherEtiquetteX = true;
	}
	
	/**
	 * Creer un nouveau graphique affichant {@code nbValeurs} points separer par {@code intervalle} unitees
	 * @param nbValeurs - {@code int} : le nombre de points affiches par le graphique
	 * @param intervalle - {@code double} : la distance entre deux points
	 */
	public Graphique(int nbValeurs, double intervalle){
		this(nbValeurs, 1, intervalle);
	}

	/**
	 * Initialise certain parametre du graphique, comme sa matrice de transformation
	 */
	private void init(){
		double transX = 0;
		double transY = minY + (maxY - minY)/2;
		double scaleX = getWidth()/(nbValeurMax*intervallePoint);
		double scaleY = getHeight()/(maxY - minY);
		trans = new AffineTransform();
		trans.translate(0, getHeight()/2);
		trans.scale(1,  -1);
		trans.scale(scaleX, scaleY);
		trans.translate(transX, -transY);
	}

	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		g2.clearRect(0,  0, getWidth(), getHeight());
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (trans == null)
			init();

		// dessiner le graphique
		if (nbValeurs >= 1){

			for (int m = 0; m < valeurs.length; m++){
				double[] vals =  valeurs[m];
				Path2D graph = new Path2D.Double();
				Point2D point = trans.transform(new Point2D.Double(0, vals[debut]), null);
				graph.moveTo(point.getX(), point.getY());
				for (int i = 1; i < nbValeurs; i++){
					point = trans.transform(new Point2D.Double(i*intervallePoint, vals[(debut+i)%taille]), null);
					graph.lineTo(point.getX(), point.getY());
				}
				g2.setColor(couleurLigne[m]);
				Stroke buffer = g2.getStroke();
				g2.setStroke(new BasicStroke(grosseurTrait));
				g2.draw(graph);
				g2.setStroke(buffer);
			}
			// dessiner le curseur
			if (grosseurCurseur > 0){
				Point2D point;
				Ellipse2D curseur;
				for (int i = 0; i < valeurs.length; i++){
//					System.out.println("\"DESSIN CURSEUR\" " + i);
					point = trans.transform(new Point2D.Double((nbValeurs - 1) * intervallePoint, valeurs[i][(fin-1+taille)%taille]), null);
					curseur = new Ellipse2D.Double(point.getX() - grosseurCurseur/2, point.getY() - grosseurCurseur/2, grosseurCurseur, grosseurCurseur);
					g2.setColor(couleurLigne[i]);
					g2.fill(curseur);
					g2.setColor(Color.black);
					g2.draw(curseur);
				}
			}
		}
		
		g2.setColor(Color.black);

		// dessiner les axes
		Line2D axeHorizontal = new Line2D.Double(0, axeY, nbValeurMax*intervallePoint, axeY);
		Line2D axeVertical = new Line2D.Double(intervallePoint, maxY, intervallePoint, minY);
		g2.draw(trans.createTransformedShape(axeVertical));
		g2.draw(trans.createTransformedShape(axeHorizontal));

		// dessiner les etiquettes
		if (afficherEtiquetteX)
			for (int i = 0; i < nbValeurMax; i += 1){
				if ((n+i)%pas == 0){
					Point2D point = trans.transform(new Point2D.Double(i*intervallePoint, axeY), null);
					g2.drawString(Double.toString(MathUtil.cut((n+i)*intervallePoint, 2)), (int)point.getX(), (int)point.getY());
				}
			}
		int i = (int)((axeY+minY)/intervalleY);
		while (axeY + i*intervalleY <= maxY){
			Point2D point = trans.transform(new Point2D.Double(intervallePoint, axeY + i*intervalleY), null);
			g2.drawString(Double.toString(axeY + i*intervalleY), (int)point.getX(), (int)point.getY());
			i++;
		}

	}

	/**
	 * Rajoute une valeur au graphique.</br>
	 * Puisque le graphique ne peut contenir qu'un certain nombre de valeur, si cette limite est atteinte, la valeur la plus ancienne sera supprimee.
	 * @param val - {@code double} : la valeur a ajoutee
	 * @return le graphique
	 */
	public Graphique push(double... vals){
		int i = 0;
		for (; i < vals.length && i < valeurs.length; i++)
			valeurs[i][fin] = vals[i];
		for (; i < valeurs.length; i++)
			valeurs[i][fin] = 0;
		fin = (fin+1)%taille;
		if (fin == debut){
			debut = (debut+1)%taille;
			n++;
		}
		if (nbValeurs < nbValeurMax)
			nbValeurs++;
		return this;
	}
	
	/**
	 * Retire toute les donnees du graphique
	 * @return le graphique
	 */
	public Graphique vider(){
		for (double[] graphs : valeurs)
			for (int i = 0; i < graphs.length; i++)
				graphs[i] = 0;
		debut = fin = 0;
		nbValeurs = 0;
		return this;
	}

	/**
	 * Retourne un tableau contenant toute les valeurs affichees par le graphique
	 * @return les valeurs affichees
	 */
	public double[][] getValeurs(){
		double[][] vals = new double[valeurs.length][nbValeurs];
		for (int n = 0; n < vals.length; n++)
			for (int i = 0; i < nbValeurs; i++)
				vals[n][i] = valeurs[n][(debut+i)%nbValeurMax];
		return vals;
	}
	/**
	 * Retourne le nombre maximal de valeur pouvant etre affichees par le graphique
	 * @return le nombre de valeur maximale
	 */
	public int getNbValeurMax(){ return nbValeurMax; }
	/**
	 * Retourne le nombre de valeur presentement affiche par le graphique
	 * @return le nombre de valeur affiche
	 */
	public int getNbValeurs(){ return nbValeurs; }
	/**
	 * retourne l'intervalle sur les abscisses entre deux points
	 * @return l'intervalle entre deux points
	 */
	public double getIntervalleX(){ return intervallePoint; }
	/**
	 * Retourne l'intervalle entre deux etiquettes sur l'axe des ordonnees
	 * @return l'intervalle entre deux etiquettes en y
	 */
	public double getIntervalleY(){ return intervalleY; }
	/**
	 * Retourne la valeur minimale en y affichee
	 * @return la valeur minimale
	 */
	public double getMinY(){ return minY; }
	/**
	 * Retourne la valeur maximale en y affichee
	 * @return la valeur maximale
	 */
	public double getMaxY(){ return maxY; }
	/**
	 * Retourne la coordonnee en y sur laquelle se trouve l'axe horizontale
	 * @return la coordonnee de l'axe horizontale
	 */
	public double getAxeY(){ return axeY; }
	/**
	 * Retourne le nombre de points se trouvant entre chaque etiquette sur l'axe des abscisse
	 * @return le nombre de point par etiquette en x
	 */
	public int getPas(){ return pas; }
	/**
	 * Retourne un tableau contenant les couleurs de toutes les lignes du graphique
	 * @return les couleurs des lignes du graphique
	 */
	public Color[] getCouleurTrait(){ return couleurLigne.clone(); }
	/**
	 * Retourne la grosseur des traits du graphique
	 * @return la grosseur des traits
	 */
	public int getGrosseurTrait(){ return grosseurTrait; }
	/**
	 * Retourne le diametre des curseurs
	 * @return le diametre des curseurs
	 */
	public int getGrosseurCurseur(){ return grosseurCurseur; }
	/**
	 * Verifie si les etiquettes sur l'axe des x doivent etre affichees
	 * @return <strong>{@code true}</strong> si les etiquettes doivent etre affichees, sinon <strong>{@code false}</strong>
	 */
	public boolean getAfficherEtiquetteEnX(){ return afficherEtiquetteX; }

	/**
	 * Change la distance en X entre deux points
	 * @param ix - {@code double} : l'intervalle en x entre deux points
	 * @return le graphique
	 */
	public Graphique setIntervalleX(double ix){ 
		intervallePoint = ix;
		trans = null;
		return this;
	}
	/**
	 * Change la distance entre deux etiquettes sur l'axe des y
	 * @param iy - {@code double} : la distance en y entre deux etiquettes
	 * @return le graphique
	 */
	public Graphique setIntervalleY(double iy){
		intervalleY = iy;
		trans = null;
		return this;
	}
	/**
	 * Change la valeur minimale en y affichee
	 * @param my - {@code double} : la valeur minimale affichee
	 * @return le graphique
	 */
	public Graphique setMinY(double my){
		minY = my;
		trans = null;
		return this;
	}
	/**
	 * Change la valeur en y maximale affichee
	 * @param my - {@code double} : la valeur maximale affichee
	 * @return le graphique
	 */
	public Graphique setMaxY(double my){
		maxY = my;
		trans = null;
		return this;
	}
	/**
	 * Change la position de l'axe horizontale
	 * @param ay - {@code double} : la nouvelle position
	 * @return le graphique
	 */
	public Graphique setAxeY(double ay){
		axeY = ay;
		return this;
	}
	/**
	 * Change le nombre de point entre chaque etiquette sur l'axe des x
	 * @param pas - {@code int} : le nombre de point entre chaque etiquette
	 * @return le graphique
	 */
	public Graphique setPas(int pas){
		this.pas = pas;
		return this;
	}
	/**
	 * Change la couleur des traits
	 * @param couleur - {@code Color[]} : une liste contenant les nouvelles couleurs
	 * @return le graphique
	 */
	public Graphique setCouleurTrait(Color... couleur){
		for (int i = 0; i < couleur.length && i < couleurLigne.length; i++)
			couleurLigne[i] = couleur[i];
		return this;
	}
	/**
	 * Change la grosseur des traits sur le graphique
	 * @param g - {@code int} : la nouvelle grosseur
	 * @return le graphique
	 */
	public Graphique setGrosseurTrait(int g){
		grosseurTrait = g;
		return this;
	}
	/**
	 * Change le diametre des curseurs du graphique
	 * @param g - {@code int} : le nouveau diametre
	 * @return le graphique
	 */
	public Graphique setGrosseurCurseur(int g){
		grosseurCurseur = g;
		return this;
	}
	/**
	 * Permet d'indiquer au graphique s'il doit afficher les etiquettes sur l'axe des x
	 * @param val - {@code boolean} : la nouvelle valeur
	 * @return le graphique
	 */
	public Graphique setAfficherEtiquetteX(boolean val){
		afficherEtiquetteX = val;
		return this;
	}
	
	@Override
	public String toString(){
		StringBuffer s = new StringBuffer();
		
		s.append("n=");
		s.append(n);
		s.append("; ");
		for (double[] graph : valeurs){
			s.append("{");
			for (int i = 0; i < nbValeurs; i++){
				s.append(" ");
				if (i == debut)
					s.append("<");
				if (i == fin)
					s.append("[");
				s.append(graph[i]);
				if (i == debut)
					s.append(">");
				if (i == fin)
					s.append("]");
			}
			s.append(" }");
		}
		
		return s.toString();
	}
	@Override
	public void setSize(int w, int h){
		super.setSize(w, h);
		trans = null;
	}
	@Override
	public void setBounds(int x, int y, int w, int h){
		super.setBounds(x, y, w, h);
		trans = null;
	}
}
