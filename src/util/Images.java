package util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Classe de methodes utilitaires en lien avec avec les images
 * @author Leo Jetzer
 */
public class Images {
	
	public static final BufferedImage UNKNOWN = safeCharger("unknown.png");
	
	/**
	 * Methode retournant l'image correspondant au nom contenue dans le <em>buildpath</em>.
	 * @param nom : le nom du fichier de l'image
	 * @return l'image
	 * @throws IOException si l'image est introuvable
	 */
	public static BufferedImage charger(String nom) throws IOException{
		URL url = Images.class.getClassLoader().getResource(nom);
		if (url == null)
			throw new IOException("Le chemin n'existe pas");
		BufferedImage img = ImageIO.read(url);
		return img;
	}
	
	/**
	 * Methode retournant l'image correspondant au nom contenue dans le <em>buildpath</em>.
	 * @param nom : le nom du fichier de l'image
	 * @return l'image
	 * @throws IOException si l'image est introuvable
	 */
	public static BufferedImage charger(URL url) throws IOException{
		if (url == null)
			throw new IOException("Le chemin n'existe pas");
		BufferedImage img = ImageIO.read(url);
		return img;
	}
	
	/**
	 * Version "safe" de Images.charger(String) qui retourne la valeur {@code null} si l'image n'a pu etre chargee
	 * @param nom - {@code String} : le nom de l'image
	 * @return l'image, ou {@code null}
	 */
	public static BufferedImage safeCharger(String nom){
		try {
			return charger(nom);
		}
		catch (IOException ex){
			return null;
		}
	}
	
	/**
	 * Version "safe" de Images.charger(URL) qui retourne la valeur {@code null} si l'image n'a pu etre chargee
	 * @param nom - {@code URL} : l'URL de l'image
	 * @return l'image, ou {@code null}
	 */
	public static BufferedImage safeCharger(URL nom){
		try {
			return charger(nom);
		}
		catch (IOException ex){
			return null;
		}
	}
	
}
