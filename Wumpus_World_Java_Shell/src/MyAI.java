import java.util.HashMap;
import java.util.Stack;
import java.util.Random;
import java.util.*;

class DirectionsEnum {
	
	public String value;
	public int id;
	static int i = 0;
	
	public DirectionsEnum(int id, String value) {
		this.id = id;
		this.value = value;
		map.put(id, this);
	}
	static HashMap<Integer, DirectionsEnum> map = new HashMap<Integer, DirectionsEnum>();
	
	static DirectionsEnum EAST = new DirectionsEnum(0, "EAST");
	static DirectionsEnum NORTH = new DirectionsEnum(1, "NORTH");
	static DirectionsEnum WEST = new DirectionsEnum(2, "WEST");
	static DirectionsEnum SOUTH = new DirectionsEnum(3, "SOUTH");
	
	public static DirectionsEnum getById(int id) {
		return map.get(id);
	}
}

class Cell {
	int x;
	int y;
	private Boolean isVisited = Boolean.FALSE;
	private Boolean isSafe = Boolean.FALSE;

	Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Boolean getIsVisited() {
		return isVisited;
	}

	public void setIsVisited(Boolean isVisited) {
		this.isVisited = isVisited;
	}

	public Boolean getIsSafe() {
		return isSafe;
	}

	public void setIsSafe(Boolean isSafe) {
		this.isSafe = isSafe;
	}
	
	public void setSafeAndVisited() {
		this.setIsSafe(Boolean.TRUE);
		this.setIsVisited(Boolean.TRUE);
	}
	
	public String toString() {
		return  " X cordinate  " + this.x + "  Y cordinates  " + this.y;
		
	}
}

class MyAgentImpl {
	int x;
	int y;
	
	DirectionsEnum currentDirection;

	MyAgentImpl() {
		this.x = 0;
		this.y = 0;
		this.currentDirection = DirectionsEnum.EAST;
	}

	public String toString() {
		return "x:" + this.x + " y:" + this.y + " dir:" + this.currentDirection;
	}
}

class Grid {
	private Cell[][] cells;

	public Grid() {
		cells = new Cell[10][10];
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 10; x++) {
				cells[y][x] = new Cell(y, x);
			}
		}
	}

	public Cell get(int i, int j) {
		return cells[i][j];
	}
}

class Path {
	private Stack<Cell> cells;
	public Path() {
		cells = new Stack<Cell>();
	}
	
	public void addCell(Cell cell){
		cells.push(cell);
	}
	
	public Cell getLastVisitedCell() {
		return cells.pop();
	}
	
	public Cell peekLastVisitedCell() {
		return cells.peek();
	}
	
	public Boolean isEmpty() {
		return cells.isEmpty();
	}
}

public class MyAI extends Agent {
	int agentXLimit;
	int agentYLimit;
	boolean isGoldGrabbed;
	boolean isArrowShot;
	boolean isScreamPerceived;

	private Grid grid;
	private MyAgentImpl agent;
	private Path agentPath = new Path();
	
	public boolean isAgentAtStartingLocation() {
		return agent.x == 0 && agent.y == 0;
	}

	public MyAI() {
		this.grid = new Grid();
		agent = new MyAgentImpl();
		isGoldGrabbed = false;
		isArrowShot = false;
		isScreamPerceived = false;
		agentXLimit = 10;
		agentYLimit = 10;
	}

	public void reduceAgentBoundaries(){
		Cell cell = agentPath.getLastVisitedCell();

			if (agent.currentDirection == DirectionsEnum.NORTH) {
				agentYLimit = agent.y;
			}

			else if (agent.currentDirection == DirectionsEnum.EAST) {
				agentXLimit = agent.x;
			}

			agent.x = cell.x;
			agent.y = cell.y;
	}

	public Action backtrack(){
		// reached exit point
		if (agentPath.isEmpty()) {
			return Action.CLIMB;
		}

		Cell cell = agentPath.peekLastVisitedCell();
		Action action = nextAction(cell, agent);

		if (action == Action.FORWARD) {
			agent.x = cell.x;
			agent.y = cell.y;
			agentPath.getLastVisitedCell();
		} else {
			changeAgentDirection(agent, action);
		}

		return action;
	}

	public Action getAction(boolean stench, boolean breeze, boolean glitter, boolean bump, boolean scream) {

		if(scream) {
			isScreamPerceived = true;
		}
		if (isScreamPerceived) {
			stench = false;
		}
		boolean safeCondition = (!stench && !breeze);
		boolean unsafeCondition = (stench || breeze);

		if (glitter) {
			isGoldGrabbed = true;
			return Action.GRAB;
		} else {
			if (bump) {
				reduceAgentBoundaries();
			}

			if (isGoldGrabbed) {
				return backtrack();
			}

			if (safeCondition || unsafeCondition) {
				if (safeCondition) {
					// mark neighnors as safe
					markNeighborinfCellsSafeForTraversal(agent.x, agent.y);
				}

				this.grid.get(agent.x, agent.y).setSafeAndVisited();

				Cell nextUnvisitedSafeCell = getNextUnvisitedSafeCell(agent.x, agent.y);
				
				if (nextUnvisitedSafeCell != null)
					System.out.println("Get next univisited cell"+nextUnvisitedSafeCell.toString());

				if (nextUnvisitedSafeCell == null) {
					if (agentPath.isEmpty()) {
						return Action.CLIMB;
					}
					System.out.println("Value of arrow shot :: "+isArrowShot);
					 if (stench && !isArrowShot && !breeze) {
						this.grid.get(agent.x, agent.y).setSafeAndVisited();
						markForwardCellsSafeForTraversal(agent, agent.x,agent.y);
						System.out.println("Arrow shot");
						isArrowShot = true;
						return Action.SHOOT;
						
					}
					Cell destinationCell = agentPath.peekLastVisitedCell();
					Action currAction = nextAction(destinationCell, agent);

					if (currAction == Action.FORWARD) {
						agent.x = destinationCell.x;
						agent.y = destinationCell.y;
						agentPath.getLastVisitedCell();
					}
					else {
						changeAgentDirection(agent, currAction);
					}
					return currAction;
				} else {
					Action currAction = nextAction(nextUnvisitedSafeCell, agent);

					if (currAction == Action.FORWARD) {
						agentPath.addCell(grid.get(agent.x, agent.y));
						agent.x = nextUnvisitedSafeCell.x;
						agent.y = nextUnvisitedSafeCell.y;
					}
					else {
						changeAgentDirection(agent, currAction);
					}
					return currAction;
				}
			}
			return Action.CLIMB;
		}
	}

	private void markNeighborinfCellsSafeForTraversal(int x, int y) {	
		if (x - 1 < agentXLimit && x - 1 >= 0) {
			grid.get(x - 1, y).setIsSafe(Boolean.TRUE);
		}
		if (y + 1 < agentYLimit && y + 1 >= 0) {
			grid.get(x, y + 1).setIsSafe(Boolean.TRUE);
		}
		if (y - 1 < agentYLimit && y - 1 >= 0) {
			grid.get(x, y - 1).setIsSafe(Boolean.TRUE);
		}
		if (x + 1 < agentXLimit && x + 1 >= 0) {
			grid.get(x + 1, y).setIsSafe(Boolean.TRUE);
		}
	}
	
	private void markForwardCellsSafeForTraversal(MyAgentImpl myAgent,int x, int y) {	
		if (myAgent.currentDirection == DirectionsEnum.NORTH) {
			if (y + 1 < agentYLimit && y + 1 >= 0) {
				grid.get(x, y+1).setIsSafe(Boolean.TRUE);
		}
		if (myAgent.currentDirection == DirectionsEnum.EAST) {
			if (x - 1 < agentXLimit && x - 1 >= 0)
				grid.get(x-1, y).setIsSafe(Boolean.TRUE);
		}
		if (myAgent.currentDirection == DirectionsEnum.WEST) {
			if (x + 1 < agentXLimit && x + 1 >= 0)
				grid.get(x+1, y).setIsSafe(Boolean.TRUE);
		}
			
		}
		if (myAgent.currentDirection == DirectionsEnum.SOUTH) {
			if (y - 1 < agentYLimit && y - 1 >= 0)
				grid.get(x, y-1).setIsSafe(Boolean.TRUE);
		}
		
	}

	private void changeAgentDirection(MyAgentImpl myAgent, Action currAction) {
		int val = myAgent.currentDirection.id;
		if (currAction == Action.TURN_LEFT) {
			val++;
			if (val > 3) {
				myAgent.currentDirection = DirectionsEnum.EAST;
			} else {
				myAgent.currentDirection = DirectionsEnum.getById(val);
			}
		} else {
			val--;
			if (val < 0) {
				myAgent.currentDirection = DirectionsEnum.SOUTH;
			} else {
				myAgent.currentDirection = DirectionsEnum.getById(val);
			}
		}

	}

	private Action nextAction(Cell cell, MyAgentImpl myAgent) {
		DirectionsEnum desiredDirection = null;

		if (cell.y != myAgent.y) {
			if (cell.y > myAgent.y) {
				desiredDirection = DirectionsEnum.NORTH;
			} else {
				desiredDirection = DirectionsEnum.SOUTH;
			}
		}
		
		if (cell.x != myAgent.x) {
			if (cell.x > myAgent.x) {
				desiredDirection = DirectionsEnum.EAST;
			} else {
				desiredDirection = DirectionsEnum.WEST;
			}
		}

		if (desiredDirection == myAgent.currentDirection) {
			return Action.FORWARD;
		}
		
		if (myAgent.currentDirection == DirectionsEnum.NORTH) {
			if (desiredDirection == DirectionsEnum.WEST) {
				return Action.TURN_LEFT;
			}
		}

		if (myAgent.currentDirection == DirectionsEnum.EAST) {
			if (desiredDirection == DirectionsEnum.NORTH) {
				return Action.TURN_LEFT;
			}
		}
		
		if (myAgent.currentDirection == DirectionsEnum.SOUTH) {
			if (desiredDirection == DirectionsEnum.EAST) {
				return Action.TURN_LEFT;
			}
		}

		if (myAgent.currentDirection == DirectionsEnum.WEST) {
			if (desiredDirection == DirectionsEnum.SOUTH) {
				return Action.TURN_LEFT;
			}
		}

		return Action.TURN_RIGHT;
	}

	private Cell getNextUnvisitedSafeCell(int x, int y) {

	List<Cell> nextVertices = new ArrayList<Cell>();
	if (y + 1 >= 0 && y + 1 < agentYLimit) {
		if ((grid.get(x, y + 1).getIsSafe()) && (!grid.get(x, y + 1).getIsVisited())) {
			//nextVertices.add(grid.get(x, y + 1));
			Cell cell = new Cell(x, y + 1);
			return cell;
		}
	}
	
	if (x + 1 >= 0 && x + 1 < agentXLimit) {
		if ((grid.get(x + 1, y).getIsSafe()) && (!grid.get(x + 1, y).getIsVisited())) {
			//nextVertices.add(grid.get(x + 1, y));
			Cell cell = new Cell(x + 1, y);
			return cell;
		}
	}

	if (y - 1 >= 0 && y - 1 < agentYLimit) {
		if ((grid.get(x, y - 1).getIsSafe()) && (!grid.get(x, y - 1).getIsVisited())) {
			//nextVertices.add(grid.get(x, y - 1));
			Cell cell = new Cell(x , y-1);
			return cell;
		}
	}
	
	if (x - 1 >= 0 && x - 1 < agentXLimit) {
		if ((grid.get(x - 1, y).getIsSafe()) && (!grid.get(x - 1, y).getIsVisited())) {
			//nextVertices.add(grid.get(x - 1, y));
			Cell cell = new Cell(x -1 , y);
			return cell;
		}
	}
	if(nextVertices.size() == 0)
		return null;
	return nextVertices.get(new Random().nextInt(nextVertices.size()));
	} 

}
