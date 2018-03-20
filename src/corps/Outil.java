package corps;

import java.awt.image.BufferedImage;

/**
 * Classe representant un outil (un objet <code>Corps</code> ayant une utilit� d�finie).
 * @author aucun(pour l'instant) TODO
 * @see Corps
 */
public class Outil{

	private BufferedImage icone;
	private Corps objet;
	
	/**
	 * Creer un nouvel outil baser sur l'objet en parametre
	 * @param objet - {@code Corps} : le corps representant l'outil dans une {@code ZoneAnimation}
	 */
	public Outil(Corps objet){
		this.objet = objet;
	}

	/**
	 * Retourne l'icone de l'objet
	 * @return l'icone
	 */
	public BufferedImage getIcone(){ return icone; }
	/**
	 * Assigne une nouvelle icone a l'outil
	 * @param icone - {@code BufferedImage} : la nouvelle icone
	 * @return l'outil
	 */
	public Outil setIcone(BufferedImage icone){
		this.icone = icone;
		return this;
	}

	/**
	 * Duplique le corps associe a l'outil et retourne la copie
	 * @return une instance du corps de l'objet
	 */
	public Corps instance(){
		return objet.clone();
	}
	
}
