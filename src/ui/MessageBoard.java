package ui;

import javax.swing.JTextField;

/**
 * JTextField modifie pour servir de pseudo-console
 * @author Leo Jetzer
 */
public class MessageBoard extends JTextField {
	private static final long serialVersionUID = -8170954129571467740L;
	private static final int NB_COLONNE_DEFAUT = 10;

	/**
	 * Construit un {@code MessageBoard} avec {@code nbColonne} colonnes
	 * @param nbColonne - {@code int} : le nombre de colonne
	 */
	public MessageBoard(int nbColonne){
		super(nbColonne);
		setEditable(false);
	}
	/**
	 * Creer un {@code MessageBoard} avec le nombre de colonne par defaut
	 */
	public MessageBoard(){
		this(NB_COLONNE_DEFAUT);
	}
	
	/**
	 * Ajoute le message passe en parametre a celui deja affiche
	 * @param msg - {@code Sring} : le message a ajouter
	 */
	public void print(String msg){
		setText(getText() + msg);
	}
	/**
	 * Efface le message affiche par le {@code MessageBoard}
	 */
	public void clear(){
		setText("");
	}
	/**
	 * Remplace le message affiche par ce {@code MessageBoard}
	 * @param msg - {@code String} : le nouveau message a affiche
	 */
	public void println(String msg){
		setText(msg);
	}
	
}
