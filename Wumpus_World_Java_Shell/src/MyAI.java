import java.util.HashMap;
import java.util.Stack;

class DirectionsEnum{
	
	public String value;
	public int id;
	
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
		agentXLimit = 10;
		agentYLimit = 10;
	}

	public Action getAction(boolean stench, boolean breeze, boolean glitter, boolean bump, boolean scream) {
		if (glitter) {
			isGoldGrabbed = true;
			return Action.GRAB;
		}

		if (isGoldGrabbed) {
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
				editAgentDirection(agent, action);
			}

			return action;
		}

		if (bump) {
			Cell cell = agentPath.getLastVisitedCell();

			if (agent.currentDirection == DirectionsEnum.EAST) {
				agentXLimit = agent.x;
			}
			if (agent.currentDirection == DirectionsEnum.NORTH) {
				agentYLimit = agent.y;
			}

			agent.x = cell.x;
			agent.y = cell.y;
		}

		boolean safeCondition = (!stench && !breeze);
		boolean unsafeCondition = (stench || breeze);
		if (safeCondition || unsafeCondition) {
			if (safeCondition) {
				markNeighborinfCellsSafeForTraversal(agent.x, agent.y);
			}

			this.grid.get(agent.x, agent.y).setSafeAndVisited();

			Cell nextUnvisitedSafeCell = getNextUnvisitedSafeCell(agent.x, agent.y);

			if (agentPath.isEmpty() && (nextUnvisitedSafeCell == null)) {
				return Action.CLIMB;
			}

			if (nextUnvisitedSafeCell == null) {
				Cell destinationCell = agentPath.peekLastVisitedCell();
				Action currAction = nextAction(destinationCell, agent);

				if (currAction != Action.FORWARD) {
					editAgentDirection(agent, currAction);
				}
				else {
					agent.x = destinationCell.x;
					agent.y = destinationCell.y;
					agentPath.getLastVisitedCell();
				}
				return currAction;
			} else {
				Action currAction = nextAction(nextUnvisitedSafeCell, agent);

				if (currAction != Action.FORWARD) {
					editAgentDirection(agent, currAction);
				}
				else {
					agentPath.addCell(grid.get(agent.x, agent.y));
					agent.x = nextUnvisitedSafeCell.x;
					agent.y = nextUnvisitedSafeCell.y;
				}
				return currAction;
			}
		}

		if (agentPath.isEmpty()) {
			return Action.CLIMB;
		}
		return Action.CLIMB;
	}

	private Cell getNextUnvisitedSafeCell(int x, int y) {
		
		if (y + 1 < agentYLimit && y + 1 >= 0) {
			if ((grid.get(x, y + 1).getIsSafe()) && (!grid.get(x, y + 1).getIsVisited())) {
				return grid.get(x, y + 1);
			}
		}
		
		if (x + 1 < agentXLimit && x + 1 >= 0) {
			if ((grid.get(x + 1, y).getIsSafe()) && (!grid.get(x + 1, y).getIsVisited())) {
				return grid.get(x + 1, y);
			}
		}

		if (y - 1 < agentYLimit && y - 1 >= 0) {
			if ((grid.get(x, y - 1).getIsSafe()) && (!grid.get(x, y - 1).getIsVisited())) {
				return grid.get(x, y - 1);
			}
		}
		
		if (x - 1 < agentXLimit && x - 1 >= 0) {
			if ((grid.get(x - 1, y).getIsSafe()) && (!grid.get(x - 1, y).getIsVisited())) {
				return grid.get(x - 1, y);
			}
		}

		return null;
	}

	private void editAgentDirection(MyAgentImpl myAgent, Action currAction) {
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
}
