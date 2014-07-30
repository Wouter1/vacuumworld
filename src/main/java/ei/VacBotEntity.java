package ei;

import java.util.LinkedList;

import actions.Action;
import actions.Move;
import actions.Turn;
import eis.IEISEntity;
import grid.GridObject;
import vac.Clean;
import vac.Dust;
import vac.PerceptSquare;
import vac.VacBot;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Percept;

public class VacBotEntity implements IEISEntity {
	
	public static final String PERCEPT_LIGHT = "light";
	public static final String PERCEPT_TASK = "task";
	public static final String PERCEPT_LOCATION = "location";
	public static final String PERCEPT_DIRECTION = "direction";
	public static final String PERCEPT_SQUARE = "square";
	
	public static final String STATE_ON = "on";
	public static final String STATE_OFF = "off";
	public static final String TASK_TURN = "turn";
	public static final String TASK_MOVE = "move";
	public static final String TASK_CLEAN = "clean";
	public static final String TASK_NONE = "none";
	public static final String ITEM_OBSTACLE = "obstacle";
	public static final String ITEM_VAC = "vac";
	public static final String ITEM_DUST = "dust";
	public static final String ITEM_EMPTY = "empty";
	
	private static final Identifier taskTurn = new Identifier(TASK_TURN);
	private static final Identifier taskMove = new Identifier(TASK_MOVE);
	private static final Identifier taskClean = new Identifier(TASK_CLEAN);
	private static final Identifier taskNone = new Identifier(TASK_NONE);
	
	VacBot bot;

	public VacBotEntity(VacBot bot) {
		this.bot = bot;
	}
	@Override
	public String getName() {
		return bot.getName();
	}

	@Override
	public String getType() {
		return "vacbot";
	}

	@Override
	public LinkedList<Percept> perceive() {
		LinkedList<Percept> perceptList = new LinkedList<Percept>();

		perceptList.add(getLightPercept(bot));
		Percept taskPercept = getTaskPercept(bot);
		perceptList.add(taskPercept);
		if (!taskPercept.getParameters().get(0).equals(taskMove)) {
			perceptList.add(getLocationPercept(bot));
		}
		if (!taskPercept.getParameters().get(0).equals(taskTurn) && !taskPercept.getParameters().get(0).equals(taskClean)) {
			perceptList.add(getDirectionPercept(bot));
		}
		if (taskPercept.getParameters().get(0).equals(taskNone)) {
			// Status of the six squares the VacBot can see
			for (PerceptSquare perceptSquare : bot.getFieldOfVision()) {
				String squareName = perceptSquare.getRelativeDirection().getName();
				// Question: how to represent a square that contains both a VacBot and a Dust?
				// Options: two single-parameter percepts; one single-parameter percept; a ParameterList?
				// Tristan & Rem agree that list processing is awkward in APLs and causes poor performance.
				// For now, we use one single-parameter percept, and assume that a perceiving VacBot cannot
				// 'see' whether a square is clean or dusty, if there is already another VacBot on it.
				if (perceptSquare.getSquare() == null || perceptSquare.getSquare().has(GridObject.class))
					perceptList.add(new Percept(PERCEPT_SQUARE, new Identifier(squareName), new Identifier(ITEM_OBSTACLE)));
				else if (perceptSquare.getSquare().has(VacBot.class) 
						&& !perceptSquare.getSquare().get(VacBot.class).equals(bot))
					perceptList.add(new Percept(PERCEPT_SQUARE, new Identifier(squareName), new Identifier(ITEM_VAC)));
				else if (perceptSquare.getSquare().has(Dust.class))
					perceptList.add(new Percept(PERCEPT_SQUARE, new Identifier(squareName), new Identifier(ITEM_DUST)));
				else
					perceptList.add(new Percept(PERCEPT_SQUARE, new Identifier(squareName), new Identifier(ITEM_EMPTY)));
			}

		}
		return perceptList;
	}

	private Percept getLightPercept(VacBot vacBot) {
		if (vacBot.isLightOn())
			return new Percept(PERCEPT_LIGHT, new Identifier(STATE_ON));
		else
			return new Percept(PERCEPT_LIGHT, new Identifier(STATE_OFF));
	}

	private Percept getTaskPercept(VacBot vacBot) {
		Action action = vacBot.getAction();
		if (action != null) {
			Class actionClass = action.getClass();
			if (actionClass.equals(Turn.class)) {
				return new Percept(PERCEPT_TASK, taskTurn);
			} else if (actionClass.equals(Move.class)) {
				return new Percept(PERCEPT_TASK, taskMove);
			} else if (actionClass.equals(Clean.class)) {
				return new Percept(PERCEPT_TASK, taskClean);
			}
		}
		return new Percept(PERCEPT_TASK, taskNone);
	}

	private Percept getLocationPercept(VacBot vacBot) {
		// Rounded to nearest integer location - move may start while percept list is being generated.
		return new Percept(PERCEPT_LOCATION, 
				new Numeral(Math.round(vacBot.getX())), new Numeral(Math.round(vacBot.getY())));
	}

	private Percept getDirectionPercept(VacBot vacBot) {
		// Rounded to n/s/e/w - turn may start while percept list is being generated.
		return new Percept(PERCEPT_DIRECTION, new Identifier(vacBot.getDirection().round().toName()));
	}
}

