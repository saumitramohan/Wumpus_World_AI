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
	private int riskFactor = 0;

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

	public int getCellRiskFactor() {
		return this.riskFactor;
	}

	public void incrementCellRiskFactor() {
		this.riskFactor += 1;
	}

	public void decrementCellRiskFactor() {
		this.riskFactor -= 1;
	}

	public String toString() {
		return " X cordinate  " + this.x + "  Y cordinates  " + this.y;

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

	public void addCell(Cell cell) {
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
	int wumpusXCordinate = -15;
	int wumpusYCordinate = -15;
	boolean isGoldGrabbed;
	boolean isArrowShot;
	boolean isScreamPerceived;
	boolean isWumpusAlive;
	boolean clearIsVisitedFlag;
	boolean skipItOnce;

	private Grid grid;
	private MyAgentImpl agent;
	private Path agentPath = new Path();
	private Path backTracePath = new Path();

	public boolean isAgentAtStartingLocation() {
		return agent.x == 0 && agent.y == 0;
	}

	public MyAI() {
		this.grid = new Grid();
		agent = new MyAgentImpl();
		isGoldGrabbed = false;
		isArrowShot = false;
		isScreamPerceived = false;
		isWumpusAlive = true;
		clearIsVisitedFlag = false;
		skipItOnce = false;
		agentXLimit = 10;
		agentYLimit = 10;

	}

	public void reduceAgentBoundaries() {
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

	public Action backtrack() {
		// reached exit point

		if (agent.x == 0 && agent.y == 0) {
			return Action.CLIMB;
		}

		Cell nextVisitedSafeCell = getNextVisitedSafeCell(agent.x, agent.y);

		if (nextVisitedSafeCell == null) {
			if (backTracePath.isEmpty()) {
				return Action.CLIMB;
			}

			Cell destinationCell = backTracePath.peekLastVisitedCell();
			Action currAction = nextAction(destinationCell, agent);

			if (currAction == Action.FORWARD) {
				agent.x = destinationCell.x;
				agent.y = destinationCell.y;
				this.grid.get(agent.x, agent.y).setSafeAndVisited();
				backTracePath.getLastVisitedCell();
			} else {
				changeAgentDirection(agent, currAction);
			}
			return currAction;
		} else {
			Action currAction = nextAction(nextVisitedSafeCell, agent);
			if (currAction == Action.FORWARD) {
				backTracePath.addCell(grid.get(agent.x, agent.y));
				agent.x = nextVisitedSafeCell.x;
				agent.y = nextVisitedSafeCell.y;
				this.grid.get(agent.x, agent.y).setSafeAndVisited();
			} else {
				changeAgentDirection(agent, currAction);
			}
			return currAction;
		}
	}

	public Action getAction(boolean stench, boolean breeze, boolean glitter, boolean bump, boolean scream) {

		if (scream) {
			isScreamPerceived = true;
		}
		if (isScreamPerceived) {
			stench = false;
			isWumpusAlive = false;
		}
		if (isArrowShot && agent.x == 0 && agent.y == 0 && !isScreamPerceived) {
			wumpusXCordinate = 0;
			wumpusYCordinate = 1;
			isScreamPerceived = true;
			skipItOnce = true;
			stench = false;
			isArrowShot = false;
		}

		if (stench && !breeze && (agent.x == 0 && agent.y == 0) && !grid.get(0, 0).getIsVisited()) {
			grid.get(0, 0).setSafeAndVisited();
			isArrowShot = true;
			return Action.SHOOT;
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

				if (!clearIsVisitedFlag)
					for (int y = 0; y < agentYLimit; y++) {
						for (int x = 0; x < agentXLimit; x++) {
							grid.get(x, y).setIsVisited(false);
						}

					}
				clearIsVisitedFlag = true;
				return backtrack();
			}

			if (safeCondition || unsafeCondition) {
				if (safeCondition) {
					Cell currentCell = grid.get(agent.x, agent.y);
					markNeighborinfCellsSafeForTraversal(agent.x, agent.y);
					if (!currentCell.getIsVisited()) {
						decrementNeighbouringRiskFactoroftheNeighbours(agent);
					}

				}

				if (unsafeCondition && stench && !breeze) {
					Cell currentCell = grid.get(agent.x, agent.y);
					if (!currentCell.getIsVisited()) {
						incrementNeighbouringRiskFactoroftheNeighbours(agent);

					}
				}

				this.grid.get(agent.x, agent.y).setSafeAndVisited();

				Cell nextUnvisitedSafeCell = getNextUnvisitedSafeCell(agent.x, agent.y);

				if (nextUnvisitedSafeCell == null) {
					if (agentPath.isEmpty() && !skipItOnce) {
						return Action.CLIMB;
					}
					skipItOnce = false;

					if (stench && isWumpusAlive && !isArrowShot && !breeze) {
						Cell cell = triangulateWumpus(agent);
						if (cell != null) {
							Action currAction = nextActionToKillWumpus(cell, agent);
							if (currAction == Action.SHOOT) {
								grid.get(cell.x, cell.y).setIsSafe(Boolean.TRUE);
								isArrowShot = true;
							} else {
								changeAgentDirection(agent, currAction);
							}
							return currAction;

						}

					}

					Cell destinationCell = agentPath.peekLastVisitedCell();
					Action currAction = nextAction(destinationCell, agent);

					if (currAction == Action.FORWARD) {
						agent.x = destinationCell.x;
						agent.y = destinationCell.y;
						agentPath.getLastVisitedCell();
					} else {
						changeAgentDirection(agent, currAction);
					}
					return currAction;
				} else {
					Action currAction = nextAction(nextUnvisitedSafeCell, agent);

					if (currAction == Action.FORWARD) {
						agentPath.addCell(grid.get(agent.x, agent.y));
						agent.x = nextUnvisitedSafeCell.x;
						agent.y = nextUnvisitedSafeCell.y;
					} else {
						changeAgentDirection(agent, currAction);
					}
					return currAction;
				}
			}
			return Action.CLIMB;
		}
	}

	private Cell triangulateWumpus(MyAgentImpl myAgent) {

		if (!falseTriangulation(myAgent)) {
			if (myAgent.y + 1 < agentYLimit && myAgent.y + 1 >= 0) {
				if (grid.get(myAgent.x, myAgent.y + 1).getCellRiskFactor() >= 2) {
					return new Cell(myAgent.x, myAgent.y + 1);

				}
				if ((myAgent.x - 1 < agentXLimit) && (myAgent.x - 1 >= 0))
					if (grid.get(myAgent.x - 1, myAgent.y).getCellRiskFactor() >= 2) {
						return new Cell(myAgent.x - 1, myAgent.y);

					}

				if (myAgent.x + 1 < agentXLimit && myAgent.x + 1 >= 0)
					if (grid.get(myAgent.x + 1, myAgent.y).getCellRiskFactor() >= 2) {
						return new Cell(myAgent.x + 1, myAgent.y);

					}

			}
			if (myAgent.y - 1 < agentYLimit && myAgent.y - 1 >= 0)
				if (grid.get(myAgent.x, myAgent.y - 1).getCellRiskFactor() >= 2) {
					return new Cell(myAgent.x, myAgent.y - 1);

				}
		}

		return null;
	}

	boolean falseTriangulation(MyAgentImpl myAgent) {
		int count = 0;
		if (myAgent.y + 1 < agentYLimit && myAgent.y + 1 >= 0) {

			if (grid.get(myAgent.x, myAgent.y + 1).getCellRiskFactor() > 2) {
				count++;
			}
		}

		if (myAgent.x + 1 < agentYLimit && myAgent.x + 1 >= 0) {

			if (grid.get(myAgent.x + 1, myAgent.y).getCellRiskFactor() > 2) {
				count++;
			}
		}

		if (myAgent.x - 1 < agentXLimit && myAgent.x - 1 >= 0) {

			if (grid.get(myAgent.x - 1, myAgent.y).getCellRiskFactor() > 2) {
				count++;
			}
		}
		if (myAgent.y - 1 < agentYLimit && myAgent.y - 1 >= 0) {

			if (grid.get(myAgent.x, myAgent.y - 1).getCellRiskFactor() > 2) {
				count++;
			}
		}
		if (count > 2) {
			return true;
		}
		return false;
	}

	private void incrementNeighbouringRiskFactoroftheNeighbours(MyAgentImpl myAgent) {

		if (myAgent.x - 1 < agentXLimit && myAgent.x - 1 >= 0) {
			if (grid.get(myAgent.x - 1, myAgent.y).getCellRiskFactor() > -1) {
				grid.get(myAgent.x - 1, myAgent.y).incrementCellRiskFactor();
			}
		}

		if (myAgent.y + 1 < agentYLimit && myAgent.y + 1 >= 0) {
			if (grid.get(myAgent.x, myAgent.y + 1).getCellRiskFactor() > -1) {
				grid.get(myAgent.x, myAgent.y + 1).incrementCellRiskFactor();
			}
		}

		if (myAgent.y - 1 < agentYLimit && myAgent.y - 1 >= 0) {
			if (grid.get(myAgent.x, myAgent.y - 1).getCellRiskFactor() > -1) {
				grid.get(myAgent.x, myAgent.y - 1).incrementCellRiskFactor();
			}
		}

		if (myAgent.x + 1 < agentYLimit && myAgent.x + 1 >= 0) {
			if (grid.get(myAgent.x + 1, myAgent.y).getCellRiskFactor() > -1) {
				grid.get(myAgent.x + 1, myAgent.y).incrementCellRiskFactor();
			}
		}
	}

	private void decrementNeighbouringRiskFactoroftheNeighbours(MyAgentImpl myAgent) {

		if (grid.get(myAgent.x, myAgent.y).getCellRiskFactor() > -1)
			grid.get(myAgent.x, myAgent.y).decrementCellRiskFactor();

		if (myAgent.x - 1 < agentXLimit && myAgent.x - 1 >= 0) {
			if (grid.get(myAgent.x - 1, myAgent.y).getCellRiskFactor() > -1) {
				grid.get(myAgent.x - 1, myAgent.y).decrementCellRiskFactor();
			}
		}

		if (myAgent.y + 1 < agentYLimit && myAgent.y + 1 >= 0) {
			if (grid.get(myAgent.x, myAgent.y + 1).getCellRiskFactor() > -1) {
				grid.get(myAgent.x, myAgent.y + 1).decrementCellRiskFactor();
			}
		}

		if (myAgent.y - 1 < agentYLimit && myAgent.y - 1 >= 0) {
			if (grid.get(myAgent.x, myAgent.y - 1).getCellRiskFactor() > -1) {
				grid.get(myAgent.x, myAgent.y - 1).decrementCellRiskFactor();
			}
		}

		if (myAgent.x + 1 < agentYLimit && myAgent.x + 1 >= 0) {
			if (grid.get(myAgent.x + 1, myAgent.y).getCellRiskFactor() > -1) {
				grid.get(myAgent.x + 1, myAgent.y).decrementCellRiskFactor();
			}
		}
	}

	private void markNeighborinfCellsSafeForTraversal(int x, int y) {
		if (x - 1 < agentXLimit && x - 1 >= 0) {
			if (x - 1 != wumpusXCordinate && y != wumpusYCordinate)
				grid.get(x - 1, y).setIsSafe(Boolean.TRUE);
		}
		if (y + 1 < agentYLimit && y + 1 >= 0) {
			if (x != wumpusXCordinate && y + 1 != wumpusYCordinate)

				grid.get(x, y + 1).setIsSafe(Boolean.TRUE);

		}
		if (y - 1 < agentYLimit && y - 1 >= 0) {
			if (x != wumpusXCordinate && y - 1 != wumpusYCordinate)

				grid.get(x, y - 1).setIsSafe(Boolean.TRUE);
		}
		if (x + 1 < agentXLimit && x + 1 >= 0) {
			if (x + 1 != wumpusXCordinate && y != wumpusYCordinate)

				grid.get(x + 1, y).setIsSafe(Boolean.TRUE);

		}
	}

	private void markForwardCellsSafeForTraversal(MyAgentImpl myAgent, int x, int y) {
		if (myAgent.currentDirection == DirectionsEnum.NORTH) {
			if (y + 1 < agentYLimit && y + 1 >= 0) {
				grid.get(x, y + 1).setIsSafe(Boolean.TRUE);
			}
			if (myAgent.currentDirection == DirectionsEnum.EAST) {
				if (x - 1 < agentXLimit && x - 1 >= 0)
					grid.get(x - 1, y).setIsSafe(Boolean.TRUE);
			}
			if (myAgent.currentDirection == DirectionsEnum.WEST) {
				if (x + 1 < agentXLimit && x + 1 >= 0)
					grid.get(x + 1, y).setIsSafe(Boolean.TRUE);
			}

		}
		if (myAgent.currentDirection == DirectionsEnum.SOUTH) {
			if (y - 1 < agentYLimit && y - 1 >= 0)
				grid.get(x, y - 1).setIsSafe(Boolean.TRUE);
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

	private Action nextActionToKillWumpus(Cell cell, MyAgentImpl myAgent) {
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
			return Action.SHOOT;
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
				Cell cell = new Cell(x, y + 1);
				return cell;
			}
		}

		if (x + 1 >= 0 && x + 1 < agentXLimit) {
			if ((grid.get(x + 1, y).getIsSafe()) && (!grid.get(x + 1, y).getIsVisited())) {
				Cell cell = new Cell(x + 1, y);
				return cell;
			}
		}

		if (y - 1 >= 0 && y - 1 < agentYLimit) {
			if ((grid.get(x, y - 1).getIsSafe()) && (!grid.get(x, y - 1).getIsVisited())) {
				Cell cell = new Cell(x, y - 1);
				return cell;
			}
		}

		if (x - 1 >= 0 && x - 1 < agentXLimit) {
			if ((grid.get(x - 1, y).getIsSafe()) && (!grid.get(x - 1, y).getIsVisited())) {
				Cell cell = new Cell(x - 1, y);
				return cell;
			}
		}
		if (nextVertices.size() == 0)
			return null;
		return nextVertices.get(new Random().nextInt(nextVertices.size()));
	}

	private Cell getNextVisitedSafeCell(int x, int y) {

		List<Cell> nextVertices = new ArrayList<Cell>();

		if (x - 1 >= 0 && x - 1 < agentXLimit) {
			if ((grid.get(x - 1, y).getIsSafe()) && (!grid.get(x - 1, y).getIsVisited())) {
				Cell cell = new Cell(x - 1, y);
				return cell;
			}
		}

		if (y - 1 >= 0 && y - 1 < agentYLimit) {
			if ((grid.get(x, y - 1).getIsSafe()) && (!grid.get(x, y - 1).getIsVisited())) {
				Cell cell = new Cell(x, y - 1);
				return cell;
			}
		}

		if (y + 1 >= 0 && y + 1 < agentYLimit) {
			if ((grid.get(x, y + 1).getIsSafe()) && (!grid.get(x, y + 1).getIsVisited())) {
				Cell cell = new Cell(x, y + 1);
				return cell;
			}
		}

		if (x + 1 >= 0 && x + 1 < agentXLimit) {
			if ((grid.get(x + 1, y).getIsSafe()) && (!grid.get(x + 1, y).getIsVisited())) {
				Cell cell = new Cell(x + 1, y);
				return cell;
			}
		}
		if (nextVertices.size() == 0)
			return null;
		return nextVertices.get(new Random().nextInt(nextVertices.size()));
	}

}
