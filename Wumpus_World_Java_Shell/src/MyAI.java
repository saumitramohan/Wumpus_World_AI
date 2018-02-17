
public class MyAI extends Agent {
	private AgentCordinates agentCordinates;

	public MyAI() {
		setAgentCordinates(new AgentCordinates(0, 0, Direction.EAST));
		setisGoldGrabbed(false);
		setDanger(false);
		setGharVaapasiFlag(false);
		
	}

	public Action getAction(boolean stench, boolean breeze, boolean glitter, boolean bump, boolean scream) {
		Action result = null;
		
		if (isGharVaapasiFlag() && (getAgentCordinates().getAgentYCordinate() == 0) && (getAgentCordinates().getAgentXCordinate() == 0)) {
			return Action.CLIMB;
		}

		else if (glitter && !isGoldGrabbed()) {
			result = Action.GRAB;
			setisGoldGrabbed(true);
			leftTurnCounter = 2;
			setGharVaapasiFlag(true);
		}

		else if ((isGoldGrabbed() && leftTurnCounter > 0)) {
			result = Action.TURN_LEFT;
			leftTurnCounter--;
		} else if ((isDanger() && leftTurnCounter > 0) && !isGoldGrabbed()) {
			result = Action.TURN_LEFT;
			leftTurnCounter--;
		} else if ((breeze || stench || bump) && !isDanger() && !isGoldGrabbed()) {
			setDanger(true);
			leftTurnCounter = 1;
			setGharVaapasiFlag(true);
			if ((getAgentCordinates().getAgentYCordinate() == 0) && (getAgentCordinates().getAgentXCordinate() == 0)) {
				return Action.CLIMB;
			} else {
				result = Action.TURN_LEFT;
			}
		} else {
			if ((isGoldGrabbed() || isDanger()) && getAgentCordinates().getAgentYCordinate() == 0
					&& getAgentCordinates().getAgentXCordinate() == 0) {
				return Action.CLIMB;
			} else
				result = Action.FORWARD;
		}

		if (result != null) {
			updateAgentCordinates(result);
		}
		return result;
	}

	public AgentCordinates getAgentCordinates() {
		return agentCordinates;
	}

	public void setAgentCordinates(AgentCordinates agentCordinates) {
		this.agentCordinates = agentCordinates;
	}

	public void updateAgentCordinates(Action action) {

		Direction direction = getAgentCordinates().getDirection();

		switch (action) {

		case FORWARD:
			updateAgentCordinates();
			break;

		case TURN_LEFT:
			updateAgentDirection(direction, action);
			break;

		case TURN_RIGHT:
			updateAgentDirection(direction, action);
			break;
		default:
			break;

		}
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
		agentCordinates = new AgentCordinates(getAgentCordinates().getAgentXCordinate(),
				getAgentCordinates().getAgentYCordinate(), newdirection);

	}

	private void updateAgentCordinates() {
		int xCordinate = getAgentCordinates().getAgentXCordinate();
		int yCordinate = getAgentCordinates().getAgentYCordinate();
		if (getAgentCordinates().getDirection() == Direction.EAST)
			xCordinate += 1;
		else if (getAgentCordinates().getDirection() == Direction.NORTH)
			yCordinate += 1;
		else if (getAgentCordinates().getDirection() == Direction.WEST)
			xCordinate -= 1;
		else
			yCordinate -= 1;
		agentCordinates = new AgentCordinates(xCordinate, yCordinate, getAgentCordinates().getDirection());

	}

	private boolean isgoldGrabbed;
	private static int leftTurnCounter;
	private boolean isDanger;
	private boolean gharVaapasiFlag;

	public boolean isGharVaapasiFlag() {
		return gharVaapasiFlag;
	}

	public void setGharVaapasiFlag(boolean gharVaapasiFlag) {
		this.gharVaapasiFlag = gharVaapasiFlag;
	}

	public boolean isDanger() {
		return isDanger;
	}

	public void setDanger(boolean isDanger) {
		this.isDanger = isDanger;
	}

	public boolean isGoldGrabbed() {
		return this.isgoldGrabbed;
	}

	public void setisGoldGrabbed(boolean goldGrabbed) {
		this.isgoldGrabbed = goldGrabbed;
	}
	// public int getLeftTurnCounter() {
	// return this.leftTurnCounter;
	// }
	//
	// public void setLeftTurnCounter(int leftTurnCounter) {
	// this.leftTurnCounter = leftTurnCounter;
	// }

	// else if (bump) {
	// if (getAgentCordinates().getAgentYCordinate() == 0 &&
	// getAgentCordinates().getAgentXCordinate() > 0) {
	// System.out.println("Inside breeze case");
	// result = Action.TURN_LEFT;
	// }
	// else if (getAgentCordinates().getAgentYCordinate() ==
	// getAgentCordinates().getAgentYCordinate() &&
	// getAgentCordinates().getAgentYCordinate() > 0) {
	// result = Action.TURN_RIGHT;
	// }
	// else if (getAgentCordinates().getAgentYCordinate() ==
	// getAgentCordinates().getAgentYCordinate() &&
	// getAgentCordinates().getAgentYCordinate() == 0) {
	// result = Action.TURN_LEFT;
	// }
	// else {
	// result = Action.TURN_LEFT;
	// }
	// }

}