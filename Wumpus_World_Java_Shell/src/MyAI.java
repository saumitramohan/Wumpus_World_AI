import java.util.Stack;

public class MyAI extends Agent {

	private AgentWorldCordinates agentWorldCordinates;

	private AgentState agentState;
	private AgentWorldCordinates agentCordinatesArray[][];

	Stack<AgentWorldCordinates> agentWorldStack = new Stack<AgentWorldCordinates>();
	static boolean turnFlag = false;
	static boolean khatraFlag = false;
	static int maximumRowCordinate = 11;
	static int maximumColumnCordinate = 11;
	static boolean goldGrabFlag = false;

	public MyAI() {

//		setAgentWorldCordinates(new AgentWorldCordinates(false, false));
		setAgentWorldCordinates(new AgentWorldCordinates(0,0));

		setAgentState(new AgentState(0, 0, Direction.EAST));
		agentCordinatesArray = new AgentWorldCordinates[10][10];

		for (int row = 0; row < 10; row++) {
			for (int column = 0; column < 10; column++) {

				agentCordinatesArray[row][column] = new AgentWorldCordinates(row, column);

			}
		}
	}

	public Action getAction(boolean stench, boolean breeze, boolean glitter, boolean bump, boolean scream) {

		Action result = null;

		if ((stench || breeze) && (getAgentState().getrowCordinate() == 0 && getAgentState().getcolumnCordinate() == 0)) {
			result = Action.CLIMB;
			return result;
		}

		if (glitter) {
			goldGrabFlag = true;
			result = Action.GRAB;
			return result;
		}
		if (goldGrabFlag) {
			if (agentWorldStack.isEmpty()) {
				result = Action.CLIMB;
				return result;
			}

			AgentWorldCordinates agentWorldCordinates = agentWorldStack.peek();
			result = getNextAction(agentWorldCordinates, getAgentState());

			switch (result) {

			case FORWARD:
				agentWorldStack.pop();
				setAgentState(new AgentState(agentWorldCordinates.getworldRowCordinate(),
						agentWorldCordinates.getworldColumnCordinate(), getAgentState().getDirection()));
				break;

			case TURN_LEFT:
				updateAgentDirection(getAgentState().getDirection(), result);
				break;

			case TURN_RIGHT:
				updateAgentDirection(getAgentState().getDirection(), result);
				break;
			default:
				break;

			}
			return result;

		}
		if (bump) {
			AgentWorldCordinates agentWorldCordinates = agentWorldStack.pop();

			setAgentState(new AgentState(agentWorldCordinates.getworldRowCordinate(),
					agentWorldCordinates.getworldColumnCordinate(), getAgentState().getDirection()));

			if (getAgentState().getDirection() == Direction.EAST) {
				maximumColumnCordinate = getAgentState().getcolumnCordinate() + 1;
			}
			if (getAgentState().getDirection() == Direction.NORTH) {
				maximumRowCordinate = getAgentState().getrowCordinate() + 1;
			}
		}

		if (!stench && !breeze) {

			int rowCordinate = getAgentState().getrowCordinate();
			int columnCordinate = getAgentState().getcolumnCordinate();

			Direction direction = getAgentState().getDirection();

			markSafeAndVisited(rowCordinate, columnCordinate);
			neighboursAreSafe(rowCordinate, columnCordinate);

			AgentWorldCordinates agentWorldCordinates = getNextUnVisitedState();

			if (agentWorldCordinates == null) {

				khatraFlag = true;
				if (agentWorldStack.isEmpty()) {
					return Action.CLIMB;
				}
				agentWorldCordinates = agentWorldStack.peek();
			}

			result = getNextAction(agentWorldCordinates, getAgentState());

			switch (result) {

			case FORWARD:
				if (!khatraFlag) {
					AgentWorldCordinates currentAgentCordinates = new AgentWorldCordinates(
							getAgentState().getrowCordinate(), getAgentState().getcolumnCordinate());
					
					agentWorldStack.push(currentAgentCordinates);
				}
				else {
						agentWorldStack.pop();
				}
				setAgentState(new AgentState(agentWorldCordinates.getworldRowCordinate(),
						agentWorldCordinates.getworldColumnCordinate(), getAgentState().getDirection()));

				khatraFlag = false;
				break;

			case TURN_LEFT:
				updateAgentDirection(getAgentState().getDirection(), result);
				break;

			case TURN_RIGHT:
				updateAgentDirection(getAgentState().getDirection(), result);
				break;
			default:
				break;

			}

		} else if (stench || breeze) {

			int rowCordinate = getAgentState().getrowCordinate();
			int columnCordinate = getAgentState().getcolumnCordinate();

			Direction direction = getAgentState().getDirection();

			markSafeAndVisited(rowCordinate, columnCordinate);

			AgentWorldCordinates agentWorldCordinates = getNextUnVisitedState();

			if (agentWorldCordinates == null) {

				khatraFlag = true;
				if (agentWorldStack.isEmpty()) {
					return Action.CLIMB;
				}
				agentWorldCordinates = agentWorldStack.peek();
			}

			result = getNextAction(agentWorldCordinates, getAgentState());

			switch (result) {

			case FORWARD:
				if (!khatraFlag) {
					AgentWorldCordinates currentAgentCordinates = new AgentWorldCordinates(
							getAgentState().getrowCordinate(), getAgentState().getcolumnCordinate());
					
					agentWorldStack.push(currentAgentCordinates);
				} else {
					agentWorldStack.pop();
				}
				setAgentState(new AgentState(agentWorldCordinates.getworldRowCordinate(),
						agentWorldCordinates.getworldColumnCordinate(), getAgentState().getDirection()));

				khatraFlag = false;
				break;

			case TURN_LEFT:
				updateAgentDirection(getAgentState().getDirection(), result);
				break;

			case TURN_RIGHT:
				updateAgentDirection(getAgentState().getDirection(), result);
				break;
			default:
				break;

			}
		} 
		if (agentWorldStack.isEmpty()) {
			return Action.CLIMB;
		}
			result = Action.CLIMB;
		

		return result;
	}

	private AgentWorldCordinates getNextUnVisitedState() {

		int presentRowAxis = getAgentState().getrowCordinate();
		int presentColumnAxis = getAgentState().getcolumnCordinate();
		
		if (presentColumnAxis + 1 < maximumColumnCordinate) {
			if ((!agentCordinatesArray[presentRowAxis][presentColumnAxis + 1].isVisited()
					&& agentCordinatesArray[presentRowAxis][presentColumnAxis + 1].isSafeCell())) {
				return agentCordinatesArray[presentRowAxis][presentColumnAxis + 1];
			}
		}

		
		if ((presentRowAxis + 1) < maximumRowCordinate) {

			if ((!agentCordinatesArray[presentRowAxis + 1][presentColumnAxis].isVisited()
					&& agentCordinatesArray[presentRowAxis + 1][presentColumnAxis].isSafeCell())) {
				return agentCordinatesArray[presentRowAxis + 1][presentColumnAxis];

			}
		}
		if (presentColumnAxis - 1 >= 0) {
			if ((!agentCordinatesArray[presentRowAxis][presentColumnAxis - 1].isVisited()
					&& agentCordinatesArray[presentRowAxis][presentColumnAxis - 1].isSafeCell())) {
				return agentCordinatesArray[presentRowAxis][presentColumnAxis - 1];
			}
		}
		
		if (presentRowAxis - 1 >= 0) {
			if ((!agentCordinatesArray[presentRowAxis - 1][presentColumnAxis].isVisited()
					&& agentCordinatesArray[presentRowAxis - 1][presentColumnAxis].isSafeCell())) {
				return agentCordinatesArray[presentRowAxis - 1][presentColumnAxis];
			}

		}
		
		return null;
	}

	private void markSafeAndVisited(int rowCordinate, int columnCordinate) {
		// TODO Auto-generated method stub
		agentCordinatesArray[rowCordinate][columnCordinate].setSafeCell(true);
		agentCordinatesArray[rowCordinate][columnCordinate].setVisited(true);

	}

	private void updateAgentDirection(Direction oldDirection, Action action) {
		Direction newdirection;

		if (oldDirection == Direction.NORTH) { ////
			if (action == Action.TURN_LEFT)
				newdirection = Direction.WEST;
			else
				newdirection = Direction.EAST;
		} else if (oldDirection == Direction.WEST) { ////
			if (action == Action.TURN_LEFT)
				newdirection = Direction.SOUTH;
			else
				newdirection = Direction.NORTH;
		} else if (oldDirection == Direction.SOUTH) { ////
			if (action == Action.TURN_LEFT)
				newdirection = Direction.EAST;
			else
				newdirection = Direction.WEST;
		} else {
			if (action == Action.TURN_LEFT)
				newdirection = Direction.NORTH;
			else
				newdirection = Direction.SOUTH;
		}
		setAgentState(
				new AgentState(getAgentState().getrowCordinate(), getAgentState().getcolumnCordinate(), newdirection));

	}

	private Action getNextAction(AgentWorldCordinates agentWorldCordinates, AgentState agentState) {

		Action result = null;
		Direction direction = null;

		if (agentWorldCordinates.getworldRowCordinate() != agentState.getrowCordinate()) {
			if (agentWorldCordinates.getworldRowCordinate() > agentState.getrowCordinate()) {
				direction = Direction.NORTH;
			} else
				direction = Direction.SOUTH;

		}
		if (agentWorldCordinates.getworldColumnCordinate() != agentState.getcolumnCordinate()) {
			if (agentWorldCordinates.getworldColumnCordinate() > agentState.getcolumnCordinate()) {
				direction = Direction.EAST;
			} else
				direction = Direction.WEST;

		}
		if (direction == agentState.getDirection()) {
			result = Action.FORWARD;

		} else {
			result = Action.TURN_LEFT;
		}

		return result;
	}

	private void neighboursAreSafe(int getrowCordinate, int getcolumnCordinate) {
		// TODO Auto-generated method stub

		if (getrowCordinate + 1 < maximumRowCordinate && getcolumnCordinate < maximumColumnCordinate) {
			agentCordinatesArray[getrowCordinate + 1][getcolumnCordinate].setSafeCell(true);
		}
		if (getrowCordinate < maximumRowCordinate && getcolumnCordinate + 1 < maximumColumnCordinate) {
			agentCordinatesArray[getrowCordinate][getcolumnCordinate + 1].setSafeCell(true);
		}
		if (getrowCordinate - 1 >= 0 && getcolumnCordinate >= 0) {
			agentCordinatesArray[getrowCordinate - 1][getcolumnCordinate].setSafeCell(true);
		}
		if (getrowCordinate >= 0 && getcolumnCordinate - 1 >= 0) {
			agentCordinatesArray[getrowCordinate][getcolumnCordinate - 1].setSafeCell(true);
		}
	}

	public AgentWorldCordinates getAgentWorldCordinates() {
		return agentWorldCordinates;
	}

	public void setAgentWorldCordinates(AgentWorldCordinates agentWorldCordinates) {
		this.agentWorldCordinates = agentWorldCordinates;
	}

	public void setAgentState(AgentState agentState) {
		this.agentState = agentState;
	}

	public AgentState getAgentState() {
		return agentState;
	}

	enum Direction {
		EAST, NORTH, WEST, SOUTH;
	}

	public class AgentWorldCordinates {

		private int worldRowCordinate;
		private int worldColumnCordinate;
		private boolean isSafeCell;
		private boolean isVisited;

		public AgentWorldCordinates(boolean b, boolean c) {
			this.isSafeCell = b;
			this.isVisited = c;
		}

		public AgentWorldCordinates(int worldRowCordinate, int worldColumnCordinate, boolean isSafeCell,
				boolean isVisted) {
			this.worldRowCordinate = worldRowCordinate;
			this.worldColumnCordinate = worldColumnCordinate;
			this.isSafeCell = isSafeCell;
			this.isVisited = isVisted;
		}

		public AgentWorldCordinates(int worldRowCordinate, int worldColumnCordinate) {
			this.worldRowCordinate = worldRowCordinate;
			this.worldColumnCordinate = worldColumnCordinate;
		}

		public boolean isSafeCell() {
			return isSafeCell;
		}

		public void setSafeCell(boolean isSafeCell) {
			this.isSafeCell = isSafeCell;
		}

		public boolean isVisited() {
			return isVisited;
		}

		public void setVisited(boolean isVisited) {
			this.isVisited = isVisited;
		}

		public int getworldRowCordinate() {
			return this.worldRowCordinate;
		}

		public int getworldColumnCordinate() {
			return this.worldColumnCordinate;
		}

		// @Override
		// public String toString() {
		// return String.format("Rowcordinate :: " + getworldRowCordinate() + "
		// ColumnCordiante ::"
		// + getworldColumnCordinate() + "Safe " + isSafeCell + "Visisted ::" +
		// isVisited());
		// }

	}

	class AgentState {

		private int rowCordinate;
		private int columnCordinate;
		private Direction direction;

		public AgentState(int rowCordinate, int columnCordinate, Direction direction) {
			this.rowCordinate = rowCordinate;
			this.columnCordinate = columnCordinate;
			this.direction = direction;
		}

		public Direction getDirection() {
			return direction;
		}

		public void setDirection(Direction direction) {
			this.direction = direction;
		}

		public int getrowCordinate() {
			return rowCordinate;
		}

		public void setrowCordinatee(int rowCordinate) {
			this.rowCordinate = rowCordinate;
		}

		public int getcolumnCordinate() {
			return columnCordinate;
		}

		public void setcolumnCordinate(int columnCordinate) {
			this.columnCordinate = columnCordinate;
		}

		// @Override
		// public String toString() {
		// return String.format("Rowcordinate :: " + getrowCordinate() + "
		// ColumnCordiant ::" + getcolumnCordinate());
		// }

	}
}

// if (khatraFlag) {
//
// result = getNextAction(agentWorldCordinates, getAgentState());
// switch (result) {
//
// case FORWARD:
//
// khatraFlag = false;
// turnFlag = false;
//
// setAgentState(new AgentState(agentWorldCordinates.getWorldXCordinate(),
// agentWorldCordinates.getWorldYCordinate(), getAgentState().getDirection()));
// break;
//
// case TURN_LEFT:
// updateAgentDirection(getAgentState().getDirection(), result);
// break;
//
// case TURN_RIGHT:
// updateAgentDirection(getAgentState().getDirection(), result);
// break;
// default:
// break;
// }
