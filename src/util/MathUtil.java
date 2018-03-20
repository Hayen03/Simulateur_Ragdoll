package util;

import physique.SVector2d;

/**
 * Classe utilitaire contenant des methodes etant utile avec des nombres;
 * @author Leo Jetzer et Marcus Phan
 */
public class MathUtil {
	
	private static double tolerance = 0.0001;
	
	/**
	 * Auteur : Leo Jetzer
	 * "Coupe" un nombre decimal au nombre indique de chiffre apres a virgule
	 * @param n - {@code double} : le nombre a couper
	 * @param decimal - {@code int} : le nombre de chiffre apres la virgule a garder
	 * @return le nombre coupe
	 */
	public static double cut(double n, int decimal){
		int f = (int)Math.pow(10, decimal);
		return ((int)(n*f))/(double)f;
	}
	/**
	 * Auteur : Leo Jetzer
	 * "Coupe" un nombre decimal au nombre indique de chiffre apres a virgule
	 * @param n - {@code float} : le nombre a couper
	 * @param decimal - {@code int} : le nombre de chiffre apres la virgule a garder
	 * @return le nombre coupe
	 */
	public static float cut(float n, int decimal){
		int f = (int)Math.pow(10, decimal);
		return ((int)(n*f))/(float)f;
	}
	
	/**
	 * Auteur : Marcus Phan
	 * Verifie si deux double ont des valeurs tres proches
	 * @param a1 Le premier double
	 * @param a2 Le deuxieme double
	 * @return true si -1E10-5 &lt; a2-a1 &gt; 1E10-5, false sinon
	 */
	public static boolean nearlyEquals(double a1, double a2) {
		if (Math.abs(a2-a1) < tolerance) 
			return true;
		
		else return false;
		
	}
	
	
	/**
	 * Auteur : Marcus Phan
	 * Effectue la projection orthogonale du vecteur a sur b
	 * @param a Le vecteur a
	 * @param b Le vecteur b
	 * @return La projection de a sur b
	 */
	public static SVector2d projectionOrthogonale(SVector2d a, SVector2d b) {	
		return b.multiply(a.dot(b)/Math.pow(b.modulus(), 2));
	}
	
	/**
	 * Retourne le vecteur separant deux vecteurs
	 * @param a Le premier vecteur
	 * @param b Le deuxieme vecteur
	 * @return Le vecteur distance
	 */
	public static SVector2d distance (SVector2d a, SVector2d b) {
		return b.substract(a);
	}
}
