import java.util.Stack;

public class MyAI extends Agent {

	private AgentWorldCordinates agentWorldCordinates;
	private AgentState agentState;
	private AgentWorldCordinates agentCordinatesArray[][];
	Stack<AgentWorldCordinates> agentWorldStack = new Stack<>();
	Stack<AgentWorldCordinates> unExploredStack = new Stack<>();
	static boolean turnFlag = false;

	public MyAI() {

		setAgentWorldCordinates(new AgentWorldCordinates(0, 0, Direction.EAST, true, true));
		setAgentWorldCordinates(new AgentWorldCordinates(0, 0));

		setAgentState(new AgentState(0, 0, Direction.EAST));
		agentCordinatesArray = new AgentWorldCordinates[10][10];

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				agentCordinatesArray[i][j] = new AgentWorldCordinates(0, 0);
			}
		}

		// Ajoobo bajoo true
	}

	public Action getAction(boolean stench, boolean breeze, boolean glitter, boolean bump, boolean scream) {

		Action result = null;
		// gold
		
		if((stench || breeze) && (getAgentState().getxCordinate() == 0 && getAgentState().getyCordinate() == 0) )
			result = Action.CLIMB;

		if (!stench && !breeze) {

			int xCordinate = getAgentState().getxCordinate();
			int yCordinate = getAgentState().getyCordinate();

			Direction direction = getAgentState().getDirection();
			neighboursAreSafe(xCordinate, yCordinate);

			agentWorldStack.push(new AgentWorldCordinates(xCordinate, yCordinate));
			unExploredStack.push(new AgentWorldCordinates(xCordinate + 1, yCordinate));

			setAgentState(new AgentState(xCordinate + 1, yCordinate, direction));
			result = Action.FORWARD;

		} else if (stench || breeze) {
			AgentWorldCordinates agentWorldCordinates = new AgentWorldCordinates();
			agentWorldCordinates = agentWorldStack.peek();
			result = getNextAction(agentWorldCordinates, getAgentState());
			switch (result) {

			case FORWARD:
//				setAgentState(new AgentState(getAgentState().getxCordinate() + 1, getAgentState().getyCordinate(),
//						getAgentState().getDirection()));
				//updateAgentCordinates();
				setAgentState(new AgentState(agentWorldCordinates.getWorldXCordinate(),agentWorldCordinates.getWorldYCordinate(), getAgentState().getDirection()));
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
		} else {
			result = Action.CLIMB;
		}

		return result;
	}

	private void updateAgentDirection(Direction oldDirection, Action action) {
		Direction newdirection;

		if (oldDirection == Direction.NORTH) {
			if (action == Action.TURN_LEFT)
				newdirection = Direction.WEST;
			else
				newdirection = Direction.EAST;
		} else if (oldDirection == Direction.WEST) {
			if (action == Action.TURN_LEFT)
				newdirection = Direction.SOUTH;
			else
				newdirection = Direction.NORTH;
		} else if (oldDirection == Direction.SOUTH) {
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
		setAgentState(new AgentState(getAgentState().getxCordinate(), getAgentState().getyCordinate(), newdirection));

	}

	private Action getNextAction(AgentWorldCordinates agentWorldCordinates, AgentState agentState) {

		Action result = null;
		Direction direction = null;

		if (agentWorldCordinates.getWorldXCordinate() != agentState.getxCordinate()) {
			if (agentWorldCordinates.getWorldXCordinate() > agentState.getxCordinate()) {
				direction = Direction.EAST;
			} else
				direction = Direction.WEST;

		}
		if (agentWorldCordinates.getWorldYCordinate() != agentState.getyCordinate()) {
			if (agentWorldCordinates.getWorldYCordinate() > agentState.getyCordinate()) {
				direction = Direction.NORTH;
			} else
				direction = Direction.SOUTH;

		}
		if (direction == agentState.getDirection()) {
			result = Action.FORWARD;

		}
		else if (direction.ordinal() - agentState.getDirection().ordinal() == 1 && !turnFlag) {
			result = Action.TURN_RIGHT;
		}
		else {
			result = Action.TURN_LEFT;
			turnFlag = true;
		}

		return result;
	}
	
	private void updateAgentCordinates() {

		int xCordinate = getAgentState().getxCordinate();
		int yCordinate = getAgentState().getxCordinate();
		if (getAgentState().getDirection() == Direction.EAST)
			xCordinate += 1;
		else if (getAgentState().getDirection() == Direction.NORTH)
			yCordinate += 1;
		else if (getAgentState().getDirection() == Direction.WEST)
			xCordinate -= 1;
		else
			yCordinate -= 1;
		agentState = new AgentState(xCordinate, yCordinate, getAgentState().getDirection());

	}

	private void neighboursAreSafe(int getxCordinate, int getyCordinate) {
		// TODO Auto-generated method stub

		AgentWorldCordinates agentWorld = agentCordinatesArray[getxCordinate][getyCordinate];
		agentWorld.isSafeCell = true;

		if (getxCordinate + 1 < 10 && getyCordinate < 10) {
			agentCordinatesArray[getxCordinate + 1][getyCordinate].isSafeCell = true;
		}
		if (getxCordinate < 10 && getyCordinate + 1 < 10) {
			agentCordinatesArray[getxCordinate][getyCordinate + 1].isSafeCell = true;
		}
		if (getxCordinate - 1 >= 0 && getyCordinate >= 0) {
			agentCordinatesArray[getxCordinate - 1][getyCordinate].isSafeCell = true;
		}
		if (getxCordinate >= 0 && getyCordinate - 1 >= 0) {
			agentCordinatesArray[getxCordinate][getyCordinate - 1].isSafeCell = true;
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

		private Direction direction;
		private int worldXCordinate;
		private int worldYCordinate;
		private boolean isSafeCell;
		private boolean isVisited;

		public AgentWorldCordinates() {

		}

		public AgentWorldCordinates(int agentXCordinate, int agentYCordinate, Direction direction, boolean isSafeCell,
				boolean isVisted) {
			this.worldXCordinate = agentXCordinate;
			this.worldYCordinate = agentYCordinate;
			this.direction = direction;
			this.isSafeCell = isSafeCell;
			this.isVisited = isVisted;
		}

		public AgentWorldCordinates(int xCordinate, int yCordinate) {
			this.worldXCordinate = xCordinate;
			this.worldYCordinate = yCordinate;
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

		public int getWorldXCordinate() {
			return worldXCordinate;
		}

		public int getWorldYCordinate() {
			return worldYCordinate;
		}

		public Direction getDirection() {
			return direction;
		}

		@Override
		public String toString() {
			return String.format("Xcordinate :: " + getWorldXCordinate() + "  YCordiante ::" + getWorldYCordinate()
					+ "  Direction :: " + getDirection());
		}

	}

	class AgentState {

		private int xCordinate;
		private int yCordinate;
		private Direction direction;

		public AgentState(int xCordinate, int yCordinate, Direction direction) {
			this.xCordinate = xCordinate;
			this.yCordinate = yCordinate;
			this.direction = direction;
		}

		public Direction getDirection() {
			return direction;
		}

		public void setDirection(Direction direction) {
			this.direction = direction;
		}

		public int getxCordinate() {
			return xCordinate;
		}

		public void setxCordinate(int xCordinate) {
			this.xCordinate = xCordinate;
		}

		public int getyCordinate() {
			return yCordinate;
		}

		public void setyCordinate(int yCordinate) {
			this.yCordinate = yCordinate;
		}

	}
}
