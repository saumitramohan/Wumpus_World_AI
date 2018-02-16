
public class MyAI extends Agent {
	private AgentCordinates agentCordinates;
	public MyAI() {
		setAgentCordinates(new AgentCordinates(0, 0, Direction.EAST));
	}

	public Action getAction(boolean stench, boolean breeze, boolean glitter, boolean bump, boolean scream) {
		Action result = null;
		if (Action.FORWARD!=null)
		updateAgentCordinates(Action.FORWARD);
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

		switch(action) {

		case FORWARD :
			System.out.println("Moving forward");
			updateAgentCordinates();
			break;
			
		case TURN_LEFT :
			System.out.println("turning left");
			updateAgentDirection(direction, action);
			break;
			
		case TURN_RIGHT :
			System.out.println("turning right");
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
		}
		else if (oldDirection == Direction.WEST) {
			if (action == Action.TURN_LEFT)
				newdirection = Direction.SOUTH;
			else
				newdirection = Direction.NORTH;
		}
		else if (oldDirection == Direction.SOUTH) {
			if (action == Action.TURN_LEFT)
				newdirection = Direction.EAST;
			else
				newdirection = Direction.WEST;
		}
		else {
			if (action == Action.TURN_LEFT)
				newdirection = Direction.NORTH;
			else
				newdirection = Direction.SOUTH;
		}
		agentCordinates = new AgentCordinates (getAgentCordinates().getAgentXCordinate(),getAgentCordinates().getAgentYCordinate(),newdirection);
		System.out.println(agentCordinates.toString());
		
	}
	private void updateAgentCordinates() {
		int xCordinate = getAgentCordinates().getAgentXCordinate();
		int yCordinate = getAgentCordinates().getAgentYCordinate();
		if (getAgentCordinates().getDirection() == Direction.EAST)
			xCordinate+=1;
		else if (getAgentCordinates().getDirection() == Direction.NORTH)
			yCordinate+=1;
		else if (getAgentCordinates().getDirection() == Direction.WEST)
			xCordinate-=1;
		else
			yCordinate-=1;
		agentCordinates = new AgentCordinates (xCordinate,yCordinate,getAgentCordinates().getDirection());
		System.out.println(agentCordinates.toString());

	}
		
}