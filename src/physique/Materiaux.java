package physique;

import inspecteur.*;

import java.awt.Color;
import java.util.Collection;
import java.util.Hashtable;

import annotation.Editable;

/**
 * Classe decrivant et regroupant tout les materiaux utilis� par l'application
 * @author Leo Jetzer
 */
@Editable(get="getFrottement", set="setFrottement", nom="Coefficient de frottement", type=PropReel.class)
@Editable(get="getChargeSurfacique", set="setChargeSurfacique", nom="Charge surfacique", type=PropReel.class)
@Editable(get="getNom", set="", nom="Nom", type=PropTexte.class)
@Editable(get="getMasseSurfacique", set="setMasseSurfacique", nom="Masse Surfacique", type=PropReel.class)
@Editable(get="getColor", set="setColor", nom="Couleur", type=PropCouleur.class)
public class Materiaux {
	private static final double FROTTEMENT_DEFAUT = 2;
	private static final Color COULEUR_DEFAUT = Color.white;
	private static final double MASSE_DEFAUT = 1;
	
	private static final Hashtable<String, Materiaux> mats = new Hashtable<String, Materiaux>();
	
	public static final String NOM_DEFAUT = "Defaut", NOM_METAL = "Metal", NOM_TEST = "Test";
	public static final Materiaux MAT_DEFAUT = enregistrerMateriaux(new Materiaux(NOM_DEFAUT, 2d, 0, 1d).setEditable(false)).setFixe();
	public static final Materiaux MAT_METAL = enregistrerMateriaux(new Materiaux(NOM_METAL, 2d, 0, 10d).setEditable(false).setColor(Color.gray)).setFixe();
	public static final Materiaux MAT_TEST = enregistrerMateriaux(new Materiaux(NOM_TEST, 2d, 0, 1d).setColor(Color.red)).setFixe();
	
	/** nom du materiau. Ne peut pas etre modifier a cause de son utilisation */
	private final String nomUnique;
	private double frottement;
	private double chargeSurfacique;
	private double masseSurfacique;
	private String nomPublique;
	private Color couleur = COULEUR_DEFAUT;
	private boolean editable = true;
	private boolean fixe = false;
	
	/**
	 * Creer un nouveau materiau ayant des valeurs par defaut
	 * @param nom - {@code String} : le nom du materiau
	 */
	public Materiaux(String nom){
		this(nom, FROTTEMENT_DEFAUT, 0, MASSE_DEFAUT);
	}
	/**
	 * Cree un nouveau materiau. <br>Il est a note que les nouveaux materiaux ne sont pas automatiquement enregistres.
	 * @param n - <code>String</code> : le nom du materiau
	 * @param f - <code>double</code> : le coefficient de frottement du materiau
	 * @param charge - {@code double} : la charge surfacique du materiau
	 * @param masse - <code>double</code> : masse surfacique (parce que tout va etre en deux dimension, donc une masse volumique ne serait pas tres pratique) du materiau (en kg/m^2)
	 */
	public Materiaux(String n, double f, double charge, double masse){
		setFrottement(f);
		chargeSurfacique = 0;
		nomUnique = n;
		masseSurfacique = masse;
		nomPublique = nomUnique;
	}
	/**
	 * Creer une copie d'un materiau, mais en changeant son nom
	 * @param n - <code>String</code> : le nouveau nom du materiau
	 * @param orig - <code>Materiaux</code> : le materiau original
	 */
	public Materiaux(String n, Materiaux orig){
		nomUnique = n;
		frottement = orig.frottement;
		chargeSurfacique = orig.chargeSurfacique;
		masseSurfacique = orig.masseSurfacique;
		nomPublique = orig.nomPublique;
	}
	
	/**
	 * Retourne le coefficient de frottement du materiau
	 * @return la valeur du coefficient de frottement
	 */
	public double getFrottement() { return frottement; }
	/**
	 * Permet d'attribuer une nouvelle valeur au coefficient de frottement du materiau
	 * @param frottement - <code>double</code> : le nouveau coefficient de frottement
	 * @return le materiau
	 */
	public Materiaux setFrottement(double frottement) { 
		this.frottement = frottement;
		return this;
	}
	/**
	 * Retourne la charge surfacique du materiau, en C/m^2
	 * @return la charge surfacique du materiau
	 */
	public double getChargeSurfacique(){ return chargeSurfacique; }
	/**
	 * Assigne une nouvelle charge surfacique au materiau
	 * @param charge - {@code double} : la nouvelle charge surfacique, en C/m^2
	 * @return le materiau
	 */
	public Materiaux setChargeSurfacique(double charge){
		chargeSurfacique = charge;
		return this;
	}
	/**
	 * Retourne le nom du materiau
	 * @return le nom du materiau
	 */
	public String getNom(){ return nomPublique; }
	/**
	 * Retourne le nom unique du materiau servant a l'identifier
	 * @return le nom unique du materiau
	 */
	public String getNomUnique(){ return nomUnique; }
	/**
	 * Permet de changer le nom publique du materiau
	 * @param nom - {@code String} : le nouveau nom
	 * @return le materiau
	 */
	public Materiaux setNom(String nom){
		nomPublique = nom;
		return this;
	}
	/**
	 * Retourne la valeur de la masse surfacique du materiau
	 * @return la masse surfacique (kg/m^2)
	 */
	public double getMasseSurfacique(){ return masseSurfacique; }
	/**
	 * Permet de modifier la masse surfacique du materiau
	 * @param masse - <code>double</code> : la nouvelle masse surfacique du materiau
	 * @return le materiau
	 */
	public Materiaux setMasseSurfacique(double masse){ 
		
		masseSurfacique = masse;
		return this;
	}
	/**
	 * Indique si le materiau peut etre modifier
	 * @return si le materiau peut etre modifier
	 */
	public boolean isEditable(){ return editable; }
	/**
	 * Permet d'indiquer si le materiau devrait pouvoir etre modifier ou non
	 * @param e - {@code boolean} : si le materiau devrait pouvoir etre modifier
	 * @return le materiau
	 */
	public Materiaux setEditable(boolean e){
		editable = e;
		return this;
	}

	@Override
	public String toString(){
		char[] n = nomPublique.toCharArray();
		n[0] = Character.toUpperCase(n[0]);
		return new String(n);
	}
	
	/**
	 * Enregistre un materiau dans le registre de la classe pour qu'il puisse etre utilise partout
	 * @param mat - <code>Materiaux</code> : le materiau a enregistrer
	 * @return le materiau
	 */
	public static Materiaux enregistrerMateriaux(Materiaux mat){ 
		mats.put(mat.nomUnique, mat);
		return mat;
	}
	/**
	 * Retourne une liste de tout les materiaux enregistre
	 * @return une liste de tout les materiaux enregistre
	 */
	public static Materiaux[] getMateriaux(){ return mats.values().toArray(new Materiaux[mats.size()]); }
	/**
	 * Retire le materiau portant le nom unique {@code nom} de la liste des materiaux enregistrer, si possible
	 * @param nom - {@code String} : le nom unique du materiaux a enlever
	 */
	public static void retirer(String nom){
		Materiaux mat = mats.get(nom);
		if (mat != null && !mat.fixe){
			mats.remove(nom, mat);
		}
	}
	/**
	 * Retourne une collection contenant tout les materiaux enregistrer. La collection est synchronisee avec la classe, donc si un materiau est rajoute ou enleve, elle subira la modification.
	 * @return une collection de tout les materiaux enregistres
	 */
	public static Collection<Materiaux> getCollection(){ return mats.values(); }
	/**
	 * Retourne le nombre de materiaux qui sont enregistres
	 * @return le nombre de materiaux enregistres
	 */
	public static int getNbMateriaux(){ return mats.size(); }
	/**
	 * Retourne le materiau enregistre nomme <code>nom</code>. <br>Si aucun materiau enregistre porte ce nom, cette methode retourne <code><strong>null</strong></code>
	 * @param nom - <code>String</code> : le nom du materiau recherche
	 * @return le materiau 
	 */
	public static Materiaux getMateriau(String nom){ return mats.get(nom); }
	/**
	 * Permet de renommer un materiau enregistre
	 * @param vieux - <code>String</code> : l'ancien nom du materiau
	 * @param nouveau - <code>String</code> : le nouveau nom du materiau
	 * @return le materiau renomme
	 */
	public static Materiaux renommer(String vieux, String nouveau){
		if (mats.containsKey(vieux)){
			Materiaux mat = mats.get(vieux);
			mats.remove(vieux);
			mats.put(nouveau, mat);
			return mat;
		}
		return null;
	}
	/**
	 * Retire le materiau indique de la liste des materiaux enregistres si possible
	 * @param mat - {@code Materiaux} : le materiau a retirer
	 * @return retourne <b>{@code true}</b> si le materiau a ete retire, <b>{@code false}</b> s'il n'etait pas dans les materiaux enregistrer
	 */
	public static boolean retirer(Materiaux mat){
		if (mats.contains(mat) && !mat.fixe){
			mats.remove(mat.nomUnique, mat);
			return true;
		}
		return false;
	}
	/**
	 * Retourne la couleur du materiau
	 * @return la couleur du materiau
	 */
	public Color getColor(){return couleur;}
	/**
	 * Change la couleur du materiau
	 * @param c - {@link Color} : la nouvelle couleur du materiau
	 * @return le materiau
	 */
	public Materiaux setColor(Color c){
		couleur = c;
		return this;
	}
	/**
	 * Indique que le materiau est fixe, AKA. qu'il est definit par cette classe et qu'il ne peut donc pas etre supprimer de la liste des materiaux enregistres
	 * @return le materiau
	 */
	private Materiaux setFixe(){
		fixe = true;
		return this;
	}
	
}
