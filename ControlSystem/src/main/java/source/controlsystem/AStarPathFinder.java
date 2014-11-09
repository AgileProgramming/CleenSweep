package source.controlsystem;

import java.util.LinkedList;
import source.controlsystem.CleanSweepRobot.CellDescription;
import source.controlsystem.CleanSweepRobot.CellToVisit;
import source.sensorsimulator.SensorInterface.direction;
import source.sensorsimulator.SensorInterface.feature;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 *
 * This class implements the A* method of graph traversal. Peter Hart, Nils Nilsson and
 * Bertram Raphael of Stanford Research Institute (now SRI International) first described
 * the algorithm in 1968. It is an extension of Edsger Dijkstra's 1959 algorithm. Our team
 * takes no credit for the algorithm, just the implementation. Further credit goes to an
 * article by Patrick Lester called 'A* Pathfinding for Beginners' that put the whole
 * thing in idiots language for easy coding.
 *
 * The class calculates the shortest path between a starting coordinate an ending
 * coordinate and provides a list containing the route
 *
 * @author Ilker Evernos, David LeGare, Jeffrey Sharpe, Doug Oda
 * @version I3
 * @date 08Nov2014
 */
public class AStarPathFinder {

   /*
    * Everything needed for graphics as well as for the A* algorithm
    */
   static class Cell {

      CellDescription cd;
      int parentX;
      int parentY;
      int gScore;
      int hScore;
      int fScore;

      public Cell(CellDescription c, int px, int py, int gS, int hS) {
         parentX = px;
         parentY = py;
         gScore = gS;
         hScore = hS;
         fScore = gS + hS;
         cd = new CellDescription();
         cd.locX = c.locX;
         cd.locY = c.locY;
         cd.sI.atChargingStation = c.sI.atChargingStation;
         cd.sI.dirtPresent = c.sI.dirtPresent;
         cd.sI.floor = c.sI.floor;
         cd.sI.features[0] = c.sI.features[0];
         cd.sI.features[1] = c.sI.features[1];
         cd.sI.features[2] = c.sI.features[2];
         cd.sI.features[3] = c.sI.features[3];
      }
   }

   /*private variables*/
   private int sX;
   private int sY;
   private int eX;
   private int eY;
   private LinkedList<Cell> openList;
   private LinkedList<Cell> closedList;
   private LinkedList<CellDescription> localMap;
   private AStarGraphics graphic;
   private boolean graphics;


   /**
   * Provides amount of battery charge to follow a know shortes path from A to B
   * <p>
   * Calculates shortest path and and then calculates charges to follow that path
   *
   * @param int startingX - The "From" x
   * @param int startingY - The "From" y
   * @param int endingX - The "To" x
   * @param int endingY - The "To" y
   * @param LinkedList<CellDescription> map - know floor plan ( previously traversed)
   * @return Charge that would be used to follow the shortest path
   */
   public int calculateCharge(int startingX, int startingY,int endingX,
           int endingY, final LinkedList<CellDescription> map) {

      /*initialize some variables*/
      int chargeRequiredForReturnTrip;
      graphics = false;
      LinkedList<CellDescription> returnCells = new LinkedList<>();
      sX = startingX;
      sY = startingY;
      eX = endingX;
      eY = endingY;
      localMap = map;
      openList = new LinkedList<>();
      closedList = new LinkedList<>();

      generateOpenAndClosedLists();

       /*calcualte battery charge by tracing the return path following the
        * parent cell back to starting coordinate*/
      returnCells.addLast(closedList.getLast().cd);
      BatteryAndDirtBin badb = new BatteryAndDirtBin(closedList.getLast().cd.sI.floor);
      Cell tempC = closedList.getLast();
      int tempx;
      int tempy;
      do {

         tempx = tempC.parentX;
         tempy = tempC.parentY;

         for (int i = 0; i < closedList.size(); i++) {
            if (tempx == closedList.get(i).cd.locX
                    && tempy == closedList.get(i).cd.locY) {
               returnCells.addLast(closedList.get(i).cd);
               badb.moved(closedList.get(i).cd.sI.floor);
               tempC = closedList.get(i);
               break;
            }
         }
      } while (tempx != sX || tempy != sY);

      chargeRequiredForReturnTrip = 500 - badb.charge();
      return chargeRequiredForReturnTrip;
   }

/**
 * Provides shortest path list
 * <p>
 * Calculates shortest path and returns a list of floorplan cells that should
 * be followed to get From A To B
 *
 * @param int startingX - The "From" x
 * @param int startingY - The "From" y
 * @param int endingX - The "To" x
 * @param int endingY - The "To" y
 * @param LinkedList<CellDescription> map - know floor plan ( previously traversed)
 * @param boolean g - if true then pretty graphics will ensue
 * @return LinkedList<CellToVisit> which is a list of XY pairs that can
 *              be followed to trace path from starting XY to ending XY
 */
   public LinkedList<CellToVisit> shortestPath(int startingX, int startingY,
           int endingX, int endingY,
           final  LinkedList<CellDescription> map, boolean g) {
      /*setup some private variables*/
      graphics = g;
      LinkedList<CellDescription> returnCells = new LinkedList<>();
      LinkedList<CellToVisit> returnValue = new LinkedList<>();
      sX = startingX;
      sY = startingY;
      eX = endingX;
      eY = endingY;
      localMap = map;
      openList = new LinkedList<>();
      closedList = new LinkedList<>();

      /*setup graphics if desired*/
      if (graphics) {
         graphic = new AStarGraphics(localMap, openList, closedList, returnCells);
      }

      /*find path from starting coordinates to ending coordinates*/
      generateOpenAndClosedLists();

      /*populate return path by tracing paretn cell back to starting coordinate*/
      returnCells.addLast(closedList.getLast().cd);
      returnValue.addLast(new CellToVisit(closedList.getLast().cd.locX, closedList.getLast().cd.locY));
      Cell tempC = closedList.getLast();
      int tempx;
      int tempy;
      do {

         tempx = tempC.parentX;
         tempy = tempC.parentY;

         for (int i = 0; i < closedList.size(); i++) {
            if (tempx == closedList.get(i).cd.locX
                    && tempy == closedList.get(i).cd.locY) {
               returnCells.addLast(closedList.get(i).cd);
               returnValue.addLast(new CellToVisit(closedList.get(i).cd.locX, closedList.get(i).cd.locY));
               tempC = closedList.get(i);
               break;
            }
         }
         if (graphics) {
            graphic.updateGraphics();
         }
      } while (tempx != sX || tempy != sY);
      if (graphics) {
         graphic.remove();
      }
      return returnValue;
   }


   /**
    * Finds path to destination
    * <p>
    * This method is the one that actually searches the floor plan to chart a
    * path from the starting coordinate to the destination coordinate. It does
    * this by continually adding adjacent cells to the open list of the cell
    * that has the lowest f score. It then moves that cell from the open
    * list to the closed list.
    */
   private void generateOpenAndClosedLists() {

      /*load intial starting cell into the open list*/
      for (int i = 0; i < localMap.size(); i++) {
         if (localMap.get(i).locX == sX && localMap.get(i).locY == sY) {
            openList.add(generateNewCell(localMap.get(i), new Cell(localMap.get(i), sX, sY, 0, 0)));
            break;
         }
      }
      /*loop unit finished*/
      for (;;) {
         if (graphics) {
            graphic.updateGraphics();
         }
         /*find the lowest score*/
         int lowestFScore = Integer.MAX_VALUE;
         for (int i = 0; i < openList.size(); i++) {
            if (openList.get(i).fScore < lowestFScore) {
               lowestFScore = openList.get(i).fScore;
               openList.addLast(openList.get(i));
               openList.remove(i);
            }
         }
         /*remove lowest score from the open list and add to closed list*/
         closedList.addLast(openList.removeLast());

         /*if this is has a h score of zero then we are at our destination*/
         if (closedList.getLast().hScore == 0) {
            break;
         }
         /*add 4 adjacent squares to open list if applicable*/
         addAdjacentCells(closedList.getLast());
      }
   }

   /**
    * Add cells adjacent to the parameter to the open list
    * <p>
    * This method evaluates each of 4 directions and determines if it is open,
    * if the next cell in that direction is on the closed list. If it is open
    * and not on the closed list then add it to the open list. If it is duplicated
    * in the open list then the duplicate with the higher g score is discarded.
    *
    * @param Cell c - The new parent to all the cells created (4 possible)
    */
   private void addAdjacentCells(final Cell c) {

      boolean okToUse;
      boolean inOpenList;
      for (direction d : direction.values()) {
         okToUse = false;
         /*check if it is "walkable"*/
         if (c.cd.sI.features[d.index()] == feature.OPEN) {
            okToUse = true;
         }
         /*check if it is on the closed list*/
         for (int i = 0; i < closedList.size(); i++) {
            if (closedList.get(i).cd.locX == (c.cd.locX + d.xOffset())
                    && closedList.get(i).cd.locY == (c.cd.locY + d.yOffset())) {
               okToUse = false;
            }
         }
         if (okToUse) {
            /* check if that direction is already in the list*/
            inOpenList = false;
            for (int i = 0; i < openList.size(); i++) {
               if (openList.get(i).cd.locX == (c.cd.locX + d.xOffset()) && openList.get(i).cd.locY == (c.cd.locY + d.yOffset())) {
                  /*ok one has been found so save the new one if the score is lower and discard the old one*/
                  if (generateNewCell(openList.get(i).cd, c).gScore < openList.get(i).gScore) {
                     openList.add(generateNewCell(openList.get(i).cd, c));
                     openList.remove(i);
                  }
                  inOpenList = true;
                  break;
               }
            }
            if (!inOpenList) {
               /*if not in the open list then get the cell description from the map*/
               for (int i = 0; i < localMap.size(); i++) {
                  if (localMap.get(i).locX == (c.cd.locX + d.xOffset()) && localMap.get(i).locY == (c.cd.locY + d.yOffset())) {
                     openList.add(generateNewCell(localMap.get(i), c));
                     break;
                  }
               }

            }
         }
      }
   }

   /**
    * Generate and populate new cell
    * <p>
    * This method creates a new call complete with all information in the internal
    * map of the clean sweep robot. It also calculates and populates the G and
    * H scores required for the A* algorithm
    *
    * @param CellDescription newC - Cell information such as features, floortype etc.
    * @param Cell parentC - The cell adjacent to this one that called for its creation
    * @return Fresh new Cell
    */
   private Cell generateNewCell(CellDescription newC, Cell parentC) {
      /*distance to starting cell*/
      int g = parentC.gScore + 10;
      /*distance to ending cell*/
      int deltaX = Integer.signum(newC.locX - eX) * (newC.locX - eX);
      int deltaY = Integer.signum(newC.locY - eY) * (newC.locY - eY);
      int h = (deltaX + deltaY) * 10;
      /*generate and return new cell*/
      return new Cell(newC, parentC.cd.locX, parentC.cd.locY, g, h);
   }
}