package ei;

import actions.UnavailableActionException;
import eis.AbstractEISEntityAction;
import eis.exceptions.ActException;
import eis.iilang.Action;
import eis.iilang.Percept;

public class EisClean extends AbstractEISEntityAction {

	public EisClean() {
		super("clean");
		addType("vacbot");
	}

	@Override
	public Percept act(String entity, Action action) throws ActException {
		VacBotEntity vacBotEntity = getEntity(entity, VacBotEntity.class);
		if (vacBotEntity == null) throw new ActException(ActException.WRONGENTITY);

		try {
			vacBotEntity.bot.clean();
		} catch (InterruptedException e) {
			throw new ActException(ActException.FAILURE, "Clean interrupted!");
		} catch (UnavailableActionException e) {
			return new Percept("Nothing to clean here!");
		}
		return new Percept("success");
	}

}
