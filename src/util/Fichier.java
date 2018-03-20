package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Classe contenant des methodes utilitaire pour gerer les fichiers
 * @author Leo Jetzer
 *
 */
public class Fichier {
	/**
	 * Ouvre le fichier "n" dans les ressources de l'application
	 * @param n - {@code String} le nom du fichier a ouvrir
	 * @return le fichier
	 */
	public static InputStream ouvrir(String n){
		return Fichier.class.getClassLoader().getResourceAsStream(n);
	}
	
	/**
	 * Retourne un reader pour le fichier nommer n
	 * @param n - {@code String} : le nom du fichier
	 * @return un reader pour le fichier
	 * @throws FileNotFoundException si le fichier n'est pas trouve
	 */
	public static BufferedReader getReader(String n) throws FileNotFoundException{
		InputStream file = ouvrir(n);
//		Debug.log(file + "\n");
		if (file != null)
			return new BufferedReader(new InputStreamReader(file));
		else
			return null;
	}
	
	/**
	 * Retourne une chaine de caractere representant le contenu du fichier nomme n
	 * @param n - {@code String} : le nom du fichier a lire
	 * @return le contenu du fichier
	 * @throws IOException s'il y a une erreur
	 */
	public static String read(String n) throws IOException{
		BufferedReader reader = Fichier.getReader(n);
		if (reader != null){
			StringBuffer string = new StringBuffer();
			String ln = reader.readLine();
			while (ln != null){
				string.append(ln);
				string.append('\n');
				ln = reader.readLine();
			}
			reader.close();
			return string.toString();
		}
		else
			throw new IOException("Impossible d'ouvrir le fichier \"" + n + "\"");
	}

	public static URL getURL(String n){
		return Fichier.class.getClassLoader().getResource(n);
	}
}
