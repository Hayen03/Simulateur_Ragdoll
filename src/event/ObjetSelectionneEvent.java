package event;

/**
 * 02/03/2016</br>
 * Evenement envoyer par une {@link interaction.ZoneInteraction} lorsqu'un nouvel objet est selectionne
 * @author Leo Jetzer
 */
public class ObjetSelectionneEvent {
	public final Object selection;
	/**
	 * Cree un nouvel ObjetSelectionneEvent
	 * @param sel - {@code Object} : la nouvelle selection
	 */
	public ObjetSelectionneEvent(Object sel){
		selection = sel;
	}
}
