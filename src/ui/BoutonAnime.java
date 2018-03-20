package ui;

import javax.swing.Timer;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Bouton qui possede une animation comme icone. La difference entre utiliser un gif et un bouton anime est qu'avec ce composant, l'animation n'avance que lorsque la souris le survole
 * @author Leo Jetzer
 *
 */
public class BoutonAnime extends JButton {
	
	private static final long serialVersionUID = 6092299824584067674L;
	private Icon[] icones;
	private int sel = 0;
	private Timer timer;
	
	/**
	 * Creer un bouton anime qui cyclera entre les icones passees en parametre
	 * @param icons - {@code Icon[]} : les icones
	 */
	public BoutonAnime(Icon... icons){
		this(100, icons);
	}
	/**
	 * Creer un bouton anime qui cyclera entre les icones passees en parametre
	 * @param refresh - {@code int} : le nombre de milliseconde entre chaque image
	 * @param icons - {@code Icon[]} : les icones
	 */
	public BoutonAnime(int refresh, Icon[] icons){
		super();
		icones = icons;
		timer = new Timer(refresh, x -> {
			if (isEnabled() && getMousePosition() != null){
				sel = (sel+1)%icones.length;
				repaint();
			}
		});
		timer.start();
	}
	
	@Override
	public Icon getIcon(){
		if (icones != null && icones.length > 0)
			return icones[sel];
		return null;
	}
	
}
