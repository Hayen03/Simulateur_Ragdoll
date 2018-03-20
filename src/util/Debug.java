package util;

import ui.MessageBoard;

/**
 * Classe utilitaire contenant des methodes utiles pour le debuggage
 * Elle n'est pas encapsule, je le sais, c'est pas grave puisque c'est pour du debuggage et qu'on veut que ça soit simple
 * @author Leo Jetzer
 */
public class Debug {
	public static boolean DEBUG = false;
	public static boolean STACK = true;
	public static MessageBoard board = null;
	
	/**
	 * Imprime un message dans la console uniquement si {@code Debug.DEBUG} est vrai
	 * @param msg - {@code String} : le message a imprimer
	 */
	public static void log(Object msg){
		if (DEBUG)
			System.out.print(msg);
	}
	
	/**
	 * Tente d'afficher un message sur un MessageBoard
	 * @param message - {@code String} : le message
	 */
	public static void message(Object message){
		if (board != null)
			board.println(message.toString());
	}
}
