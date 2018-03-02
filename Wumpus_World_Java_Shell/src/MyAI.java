import java.util.Stack;

public class MyAI extends Agent {

	private AgentWorldCordinates agentWorldCordinates;
	private AgentState agentState;
	private AgentWorldCordinates agentCordinatesArray[][];
	Stack<AgentWorldCordinates> agentWorldStack = new Stack<>();
	static boolean turnFlag = false;
	static boolean khatraFlag = false;
	static int maximumXCordinate = 9;
	static int maximumYCordinate = 9;

	public MyAI() {

		setAgentWorldCordinates(new AgentWorldCordinates(0, 0, true, true));
		setAgentWorldCordinates(new AgentWorldCordinates(0, 0));

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
		// gold

		if ((stench || breeze) && (getAgentState().getxCordinate() == 0 && getAgentState().getyCordinate() == 0))
			result = Action.CLIMB;

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

		if (!stench && !breeze) {

			System.out.println("Inside !stench && !breeze");
			int xCordinate = getAgentState().getxCordinate();
			int yCordinate = getAgentState().getyCordinate();

			Direction direction = getAgentState().getDirection();

			markSafeAndVisited(xCordinate, yCordinate);
			neighboursAreSafe(xCordinate, yCordinate);

			
			AgentWorldCordinates agentWorldCordinates = new AgentWorldCordinates();
			agentWorldCordinates = getNextUnVisitedState(agentWorldCordinates);
			if (agentWorldCordinates == null) {
				khatraFlag = true;
				agentWorldCordinates = agentWorldStack.peek();
			}
			result = getNextAction(agentWorldCordinates, getAgentState());
			
			System.out.print("Get action"+result);
			
			switch (result) {

			case FORWARD:
				if (!khatraFlag) {
					agentWorldStack.push(new AgentWorldCordinates(agentWorldCordinates.getWorldYCordinate(),
							agentWorldCordinates.getWorldXCordinate()));

				}
				setAgentState(new AgentState(agentWorldCordinates.getWorldXCordinate(),
						agentWorldCordinates.getWorldYCordinate(), getAgentState().getDirection()));
				khatraFlag = false;
				break;

			case TURN_LEFT:
				System.out.println("**turen left**");

				updateAgentDirection(getAgentState().getDirection(), result);
				break;

			case TURN_RIGHT:
				updateAgentDirection(getAgentState().getDirection(), result);
				break;
			default:
				break;

			}


		} else if (stench || breeze) {
			
			System.out.println("stench || breeze");


			AgentWorldCordinates agentWorldCordinates = new AgentWorldCordinates();

			agentWorldCordinates = getNextUnVisitedState(agentWorldCordinates);

			if (agentWorldCordinates == null) {
				khatraFlag = true;
				agentWorldCordinates = agentWorldStack.peek();
			}

			result = getNextAction(agentWorldCordinates, getAgentState());

			agentCordinatesArray[getAgentState().getxCordinate()][getAgentState().getyCordinate()].isSafeCell = true;
			agentCordinatesArray[getAgentState().getxCordinate()][getAgentState().getyCordinate()].isVisited = true;

			switch (result) {

			case FORWARD:
				turnFlag = false;
				if (!khatraFlag) {
					agentWorldStack.push(new AgentWorldCordinates(agentWorldCordinates.getWorldYCordinate(),
							agentWorldCordinates.getWorldXCordinate()));

				}
				setAgentState(new AgentState(agentWorldCordinates.getWorldXCordinate(),
						agentWorldCordinates.getWorldYCordinate(), getAgentState().getDirection()));
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

		} else {
			result = Action.CLIMB;
		}

		//printAgentWorldStackContent();

		return result;
	}

	private AgentWorldCordinates getNextUnVisitedState(AgentWorldCordinates agentWorldCordinates) {
		// TODO Auto-generated method stub
		AgentWorldCordinates newAgentWorldCordinates = new AgentWorldCordinates();
		// IsVisted // Bounds check // isSafe
		// Bounds first
		// Y+1, Y-1 , X+1, X-1 ,
		int presentXAxis = agentWorldCordinates.getWorldXCordinate();
		int presentYAxis = agentWorldCordinates.getWorldYCordinate();
		if (presentXAxis - 1 >= 0) {
			if (!agentCordinatesArray[presentXAxis - 1][presentYAxis].isVisited()
					&& agentCordinatesArray[presentXAxis - 1][presentYAxis].isSafeCell()) {
				return agentCordinatesArray[presentXAxis - 1][presentYAxis];
			}
		}
		if (presentXAxis + 1 <= maximumXCordinate) {
			if (!agentCordinatesArray[presentXAxis + 1][presentYAxis].isVisited()
					&& agentCordinatesArray[presentXAxis + 1][presentYAxis].isSafeCell()) {
				return agentCordinatesArray[presentXAxis + 1][presentYAxis];
			}
		}
		if (presentYAxis - 1 >= 0) {
			if (!agentCordinatesArray[presentXAxis][presentYAxis - 1].isVisited()
					&& agentCordinatesArray[presentXAxis][presentYAxis - 1].isSafeCell()) {
				return agentCordinatesArray[presentXAxis][presentYAxis - 1];
			}
		}
		if (presentYAxis + 1 <= maximumYCordinate) {
			if (!agentCordinatesArray[presentXAxis][presentYAxis + 1].isVisited()
					&& agentCordinatesArray[presentXAxis][presentYAxis + 1].isSafeCell()) {
				return agentCordinatesArray[presentXAxis][presentYAxis + 1];
			}
		}
		return null;
	}

	private void markSafeAndVisited(int xCordinate, int yCordinate) {
		// TODO Auto-generated method stub
		AgentWorldCordinates agentWorld = agentCordinatesArray[xCordinate][yCordinate];
		agentWorld.isSafeCell = true;
		agentWorld.isVisited = true;

	}

	private void updateAgentDirection(Direction oldDirection, Action action) {
		Direction newdirection;

		if (oldDirection == Direction.NORTH) {   ////
			if (action == Action.TURN_LEFT)
				newdirection = Direction.WEST;
			else
				newdirection = Direction.EAST;
		} else if (oldDirection == Direction.WEST) {   ////
			if (action == Action.TURN_LEFT)
				newdirection = Direction.SOUTH;
			else
				newdirection = Direction.NORTH;
		} else if (oldDirection == Direction.SOUTH) {   ////
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

		} //else if (direction.ordinal() - agentState.getDirection().ordinal() == 1 && !turnFlag) {
//			khatraFlag = true;
//			result = Action.TURN_RIGHT;
//		} else {
		else {
			result = Action.TURN_LEFT;
			turnFlag = true;
		}

		return result;
	}

//	private void printAgentWorldStackContent() {
//		System.out.println("AgentWorld :: " + agentWorldStack.peek().toString());
//	}

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

		private int worldRowCordinate;
		private int worldColumnCordinate;
		private boolean isSafeCell;
		private boolean isVisited;
		// private boolean isDanger;

		public AgentWorldCordinates() {

		}

		public AgentWorldCordinates(int agentXCordinate, int agentYCordinate, boolean isSafeCell, boolean isVisted) {
			this.worldXCordinate = agentXCordinate;
			this.worldYCordinate = agentYCordinate;
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

		@Override
		public String toString() {
			return String.format("Xcordinate :: " + getWorldXCordinate() + "  YCordiante ::" + getWorldYCordinate());
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
