package ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Bouton qui change d'icone lorsque la souris le survole
 * @author Leo Jetzer
 *
 */
public class BoutonSemiAnime extends JButton {

	private static final long serialVersionUID = -3855240448676012299L;
	private Icon icone_fermee;
	private Icon icone_ouverte;
	
	/**
	 * Creer un nouveau bouton semi-anime compose des deux icones en parametre
	 * @param iconeA - {@code Icon} : l'icone affichee normalement
	 * @param iconeB - {@code Icon} : l'icone affichee lorsque la souris survole le bouton
	 */
	public BoutonSemiAnime(Icon iconeA, Icon iconeB){
		super();
		icone_fermee = iconeA;
		icone_ouverte = iconeB;
		addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {
				repaint();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				repaint();
			}
		});
	}
	
	@Override
	public Icon getIcon(){
		if (this.isEnabled() && this.getMousePosition() != null)
			return (icone_ouverte != null ? icone_ouverte : icone_fermee);
		return (icone_fermee != null ? icone_fermee : icone_ouverte);	
	}
	
}
