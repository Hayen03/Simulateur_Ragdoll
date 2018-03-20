package interaction;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Bouton qui change d'icone a chaque fois que l'on appui dessus
 * @author Leo Jetzer
 */
public class BoutonSwitch extends JButton {

	private static final long serialVersionUID = -2701974563808280502L;
	
	private int state = 0;
	private Icon[] icons;
	
	/**
	 * Creer un nouveau BoutonSwitch avec les icones en parametre
	 * @param icons - {@code Icon[]} : les icones du bouton
	 */
	public BoutonSwitch(Icon[] icons){
		this();
		this.icons = new Icon[icons.length];
		for (int i = 0; i < icons.length; i++){
			this.icons[i] = icons[i];
		}
	}
	/**
	 * Creer un nouveau BoutonSwitch sans icones
	 */
	public BoutonSwitch(){
		icons = new Icon[0];
		this.addActionListener(x -> {
			next();
			repaint();
		});
	}
	
	@Override
	public Icon getIcon(){
		if (icons != null && icons.length != 0)
			return icons[state];
		return null;
	}
	
	/**
	 * Retourne l'etat du bouton
	 * @return l'etat du bouton
	 */
	public int getState(){ return state; }
	
	/**
	 * Change l'etat du bouton
	 * @param state - {@code int} : le nouvel etat
	 */
	public void setState(int state){ 
		if (icons.length == 0)
			this.state = 0;
		else
			this.state = state%icons.length;
	}
	
	/**
	 * Change l'etat du bouton pour le prochain
	 */
	public void next(){
		setState(state+1);
	}
	
	/**
	 * Change les icones du bouton
	 * @param icons  - {@code Icon[]} : les nouvelles icones du bouton
	 */
	public void setIcons(Icon[] icons){
		this.icons = new Icon[icons.length];
		for (int i = 0; i < icons.length; i++)
			this.icons[i] = icons[i];
		setState(state);
	}
	
	/**
	 * Retourne les icones du bouton
	 * @return les icones du bouton
	 */
	public Icon[] getIcons(){ return icons; }
	
}
