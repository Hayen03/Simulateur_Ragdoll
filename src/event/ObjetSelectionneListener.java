package event;

/**
 * 02/03/2016</br>
 * Ecouteur personnaliser servant a ecouter les changements de selection dans une {@link interaction.ZoneInteraction}
 * @author Leo Jetzer
 */
public interface ObjetSelectionneListener {
	/**
	 * Invoque lorsqu'un changement de selection est effectue
	 * @param event - {@link ObjetSelectionneEvent} : l'evenement lance
	 */
	public void objetSelectionne(ObjetSelectionneEvent event);
}
