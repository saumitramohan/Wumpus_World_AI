
enum Direction {
		NORTH,
		SOUTH,
		EAST,
		WEST;
}

public class AgentCordinates {
	public AgentCordinates(int agentXCordinate, int agentYCordinate, Direction direction) {
		setAgentXCordinate(agentXCordinate);
		setAgentYCordinate(agentYCordinate);
		setDirection(direction);
		
	}
	private Direction direction;
	
	private int agentXCordinate;
	private int agentYCordinate;
	public int getAgentXCordinate() {
		return agentXCordinate;
	}
	public void setAgentXCordinate(int agentXCordinate) {
		this.agentXCordinate = agentXCordinate;
	}
	public int getAgentYCordinate() {
		return agentYCordinate;
	}
	public void setAgentYCordinate(int agentYCordinate) {
		this.agentYCordinate = agentYCordinate;
	}
	public Direction getDirection() {
		return direction;
	}
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	   @Override
	    public String toString() {
	        return String.format("Xcordinate :: "+getAgentXCordinate()+"YCordiante ::" +getAgentYCordinate()
	        +"Direction :: "+getDirection());
	    }

	
	

}

