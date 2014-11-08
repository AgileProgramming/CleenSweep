package source.controlsystem;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import source.sensorsimulator.SensorInterface;
import source.sensorsimulator.SensorInterface.direction;
import source.sensorsimulator.SensorInterface.feature;
import source.sensorsimulator.VirtualHouse;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 *
 * This class implements the Control System portion of the Clean Sweep Robotic
 * Vacuum Cleaner Team Project (Clean Sweep). The purpose is to provide the
 * required functionality of the Clean Sweep.
 *
 * @author Ilker Evrenos, David LeGare, Jeffrey Sharpe, Doug Oda
 * @version I3
 * @date 03Nov2014
 */
public class CleanSweepRobot {

   static class CellToVisit {

      private int notVisitedX;
      private int notVisitedY;

      public CellToVisit(int x, int y) {
         notVisitedX = x;
         notVisitedY = y;
      }
   }

   public static class CellDescription {

      public SensorInterface sI;
      public int locX;
      public int locY;

      public CellDescription() {
         sI = new SensorInterface();
      }
   }

   private enum Log{
      CHECK_SENSOR,
      MOVE,
      SWEEP
   }
   
   private enum Movement {

      CLEANING,
      TO_CHARGING_STATION,
      FROM_CHARGING_STATION,
      FINAL_TRIP_TO_CHARGING_STATION;
   }
   /*Private Variables*/
   private Component frame;
   private VirtualHouse vH;
   private LinkedList<CellDescription> internalMap;
   private LinkedList<CellToVisit> destinations;
   private LinkedList<CellToVisit> shortestPath;
   private List<String> tasksCompleted;
   private BatteryAndDirtBin guages;
   private int currentX;
   private int currentY;
   private int chargingStationX;
   private int chargingStationY;
   private int beforeChargingTripX;
   private int beforeChargingTripY;
   private boolean stuck;
   private Movement movement;
   private boolean displayGraphics;
   
   /*Exception log for IO*/
   private static final Logger logger = Logger.getLogger("Exceptions");

   /**
    * Constructor for CleanSweepRobot <p> This method instantiates private
    * lists, and saves a reference to the Sensor Simulator
    *
    * @param VirtualHouse virtualHouse - A Sensor Simulator reference
    */
   public CleanSweepRobot(VirtualHouse virtualHouse) {

      /*save a reference to the sensor simulator*/
      vH = virtualHouse;

      /*instantiate a few lists*/
      internalMap = new LinkedList<>();
      destinations = new LinkedList<>();
      shortestPath = new LinkedList<>();
      tasksCompleted = new LinkedList<>();


      /*get some initial information from the sensor simulator*/
      SensorInterface ci = new SensorInterface();
      vH.getInitialLocation(ci);
      currentX = ci.StartingXCoord;
      chargingStationX = ci.StartingXCoord;
      currentY = ci.StartingYCoord;
      chargingStationY = ci.StartingYCoord;
      vH.sensorInformation(ci);

      /*setup internal sensors*/
      guages = new BatteryAndDirtBin(ci.floor);

      /*intialize other variables*/
      movement = Movement.CLEANING;
      stuck = false;
      displayGraphics = true;
   }

   /**
    * Overloaded Constructor for CleanSweepRobot <p> Used for unit testing
    *
    * @param VirtualHouse virtualHouse - A Sensor Simulator reference
    * @param int x - starting location of robot
    * @param int y - starting location of robot
    */
   public CleanSweepRobot(VirtualHouse virtualHouse, int x, int y) {
      /*save a reference to the sensor simulator*/
      vH = virtualHouse;

      /*instantiate a few lists*/
      internalMap = new LinkedList<>();
      destinations = new LinkedList<>();
      shortestPath = new LinkedList<>();
      tasksCompleted = new LinkedList<>();

      /*get some initial information from the sensor simulator*/
      SensorInterface ci = new SensorInterface();
      currentX = x;
      currentY = y;
      vH.sensorInformation(ci);

      /*setup internal sensors*/
      guages = new BatteryAndDirtBin(ci.floor);

      /*intialize other variables*/
      movement = Movement.CLEANING;
      stuck = false;
      displayGraphics = false;
   }

   /**
    * Update the Clean Sweep Control System <p> This method: -Gets new cell
    * information and adds the internally stored XY coord -Adds the cell to the
    * internal map -Adds to a list of places to visit -Moves the robot to one of
    * the places not visited if it exists
    *
    * @return false if all locations have been visited, true if more
    */
 public boolean cleanSweepUpdate() {
      boolean moved = false;
      CellDescription currentCell = new CellDescription();

      /*Check Sensors*/
      vH.sensorInformation(currentCell.sI);
      addCompletedTask(currentCell.sI,Log.CHECK_SENSOR);
      currentCell.locX = currentX;
      currentCell.locY = currentY;

      /*Execute the desired movment*/
      while (!moved) {
         
         /*This is the normal movement for the clean sweep robot*/
         if (movement == Movement.CLEANING){
            /*Save cell description*/
            addToInternalMap(currentCell);
            /*Update destinations*/
            updateNotVisitedList(currentCell);

            /*Sweep if necessary*/
            while (currentCell.sI.dirtPresent && !timeToReturntoChargingStation(currentCell)) {
               vH.vacuum();
               guages.swept();
               addCompletedTask(currentCell.sI,Log.SWEEP);
               vH.sensorInformation(currentCell.sI);
               addCompletedTask(currentCell.sI,Log.CHECK_SENSOR);
            }

            /*Move*/
             /*If all locations have been visited then return to charging station*/
            if (!destinations.isEmpty()) {                   
               if (!timeToReturntoChargingStation(currentCell)) {
                  moveToNext(currentCell);
                  moved = true;
               } else {
                  movement = Movement.TO_CHARGING_STATION;
               }
            } else {
               movement = Movement.FINAL_TRIP_TO_CHARGING_STATION;
            } 
         }
         
         if (movement == Movement.TO_CHARGING_STATION){
            if (currentX == chargingStationX && currentY == chargingStationY) {
               if (guages.dirtBinCapacity() == 0) {
                  JOptionPane.showMessageDialog(frame,
                          "EMPTY ME",
                          "Clean Sweep Alert",
                          JOptionPane.WARNING_MESSAGE);
                  guages.emptyDirtBin();
               }
               guages.chargeBattery();
               movement = Movement.FROM_CHARGING_STATION;
            } else {
               moveToRechargeStation(currentCell);
               moved = true;
            }
         }
         
         if (movement == Movement.FROM_CHARGING_STATION){
            if (currentX == beforeChargingTripX && currentY == beforeChargingTripY) {
               movement = Movement.CLEANING;
            } else {
               moveFromRechargeStation(currentCell);
               moved = true;
            }
         }
         
         if (movement == Movement.FINAL_TRIP_TO_CHARGING_STATION){
            if (currentX == chargingStationX && currentY == chargingStationY) {
               /*All done so save internal map to file*/
               writeInternalMap();
               /*Save task list*/
               writeTaskList();
               return false;
            } else {
                moveToRechargeStation(currentCell);
                moved = true;
            }
         }
      }
      return true;
   }

   /**
    * Determine if the robot should return to charging station to re-charge or empty
    * <p>
    * This method determines if the total battery charge requirements dictate
    * that the robot should return to the charge station or if the dirt capacity
    * necessitates the return for emptying
    *
    * @param CellDescription current- name of cell from which to move
    * @return true if return is necessary, false if not
    */
   private boolean timeToReturntoChargingStation(CellDescription current) {
      AStarPathFinder pf = new AStarPathFinder();
      int FudgeFactor = current.sI.floor.charge() * 2;
      int battRequiredToGetBack =
              pf.calculateCharge(currentX, currentY, chargingStationX, chargingStationY, internalMap);
      if ((guages.dirtBinCapacity() == 0)
              || (guages.charge() <= (battRequiredToGetBack + FudgeFactor))) {

         return true;
      }
      return false;
   }

   /**
    * Move the robot along pre-chosen path 
    * <p> 
    * This method follows the path provided by the A* path finding object
    *
    * @param CellDescription Current- name of cell from which to move
    */
   private void moveFromRechargeStation(CellDescription current) {
      if (shortestPath.isEmpty()) {
         AStarPathFinder pf = new AStarPathFinder();
         shortestPath = pf.shortestPath(currentX, currentY, beforeChargingTripX,
                 beforeChargingTripY, internalMap, false);
         shortestPath.removeLast();
      }
      /* move if desired and possible */
      move(current, shortestPath.getLast().notVisitedX, shortestPath.getLast().notVisitedY);

      /* Since we know that the path is valid remove it*/
      shortestPath.removeLast();
   }

   /**
    * Move the robot along pre-chosen path 
    * <p> 
    * This method follows the path provided by the A* path finding object
    *
    * @param CellDescription Current- name of cell from which to move
    */
   private void moveToRechargeStation(CellDescription current) {
      if (shortestPath.isEmpty()) {
         beforeChargingTripX = currentX;
         beforeChargingTripY = currentY;
         AStarPathFinder pf = new AStarPathFinder();
         shortestPath = pf.shortestPath(currentX, currentY, chargingStationX, chargingStationY, internalMap, displayGraphics);
         shortestPath.removeLast(); 
      }
      /* move if desired and possible */
      move(current, shortestPath.getLast().notVisitedX, shortestPath.getLast().notVisitedY);

      /* Since we know that the path is valid */
      shortestPath.removeLast();
   }

   /**
    * Move the robot to or toward the last location in the destinations list
    * <p>
    * This method obtains the xy coordinates from the destinations list and
    * moves the robot one legal move from the parameter location to or toward
    * the destination. If moving toward the destination is not possible then try
    * the next to last location on the destinations list.
    *
    * @param CellDescription Current- name of cell from which to move
    */
   private void moveToNext(CellDescription current) {
      if (stuck && destinations.size() > 1) {
            destinations.addLast(destinations.get(destinations.size() - 2));
         }
      /* Load place we want to be*/
      int targetx = destinations.getLast().notVisitedX;
      int targety = destinations.getLast().notVisitedY;

      /* Check in all 4 directions and move if desired and possible */
      stuck = !move(current, targetx, targety);

      /*If we have moved to the last location on the list then remove it
       * since it is no longer a destination
       */
      if (currentX == destinations.getLast().notVisitedX
              && currentX == destinations.getLast().notVisitedX) {
         destinations.removeLast();
      }
   }

   /**
    * Moves the robot
    * <p>
    * This method moves the robot from it's current position to the targetx/y
    * sent as parameters after checking in the correct direction to verify
    * that the path is open. If there is a path open then the method returns true
    * If the path is blocked then the method returns false
    * 
    * @param CellDescription current - current cell description 
    * @param int targetx - desired location in the x direction
    * @param int targety - desired location in the y direction
    * @return true if location has been changed
    */
   private boolean move(CellDescription current, int targetx, int targety) {
      boolean moved = false;
      for (SensorInterface.direction d : SensorInterface.direction.values()) {
         if ((((targety > current.locY) && (d.index() == direction.NORTH.index()))
                 || ((targetx > current.locX) && (d.index() == direction.EAST.index()))
                 || ((targety < current.locY) && (d.index() == direction.SOUTH.index()))
                 || ((targetx < current.locX) && (d.index() == direction.WEST.index())))
                 && (current.sI.features[d.index()] == SensorInterface.feature.OPEN)) {
            /*If the mess above is true then actually move the darn thing*/
            if (vH.move(current.locX + d.xOffset(), current.locY + d.yOffset())) {
               currentX = current.locX + d.xOffset();
               currentY = current.locY + d.yOffset();
               moved = true;
               SensorInterface ci = new SensorInterface();
               vH.sensorInformation(ci);
               guages.moved(ci.floor);
               addCompletedTask(ci, Log.MOVE);
               break;
            }
         }
      }
      return moved;
   }

   /**
    * Add cells to list of cells that need to be visited
    * <p>
    * This method checks to see if, from the perspective of the current xy 
    * location, a cell in any of the 4 directions is obtainable (feature=open), 
    * not already in the internalMap and not already in the destinations list.
    *
    * @param CellDescription current- current cell
    */
   private void updateNotVisitedList(CellDescription current) {
      boolean wantToGoThere;
      for (direction d : direction.values()) {
         /*Check each of 4 directions*/
         if (current.sI.features[d.index()] == feature.OPEN) {
            /*If there is not an obsitcle in each direction then check space has
             * already been visited*/
            wantToGoThere = true;
            for (int i = 0; i < internalMap.size(); i++) {
               if ((current.locX + d.xOffset()) == internalMap.get(i).locX
                       && (current.locY + d.yOffset()) == internalMap.get(i).locY) {
                  wantToGoThere = false;
                  /*this is funny but it makes sonar happy*/
                  i = internalMap.size();
               }
            }
            if (wantToGoThere) {
               /*If here then there is no obsitcle and not been visited
               so remove any old occurances of that location*/
               for (int i = 0; i < destinations.size(); i++) {
                  if ((current.locX + d.xOffset()) == destinations.get(i).notVisitedX
                          && (current.locY + d.yOffset()) == destinations.get(i).notVisitedY) {
                     destinations.remove(i);
                  /*this is funny but it makes sonar happy*/
                  i = destinations.size();
                  }
               }
               /*Add to places that need visited*/
               destinations.add(new CellToVisit(current.locX + d.xOffset(), current.locY + d.yOffset()));
            }
         }
      }
   }

   /**
    * Add cell descriptions to internal floor plan <p> This method adds only
    * original cells to the floor plan by iterating through the list of cells
    * and discarding parameter if the xy coordinates are already in the list
    *
    * @param CellDescription Current- name of cell to add to the floor plan
    */
   private void addToInternalMap(CellDescription Current) {
      boolean isNewLocation = true;
      for (int i = 0; i < internalMap.size(); i++) {
         if (Current.locX == internalMap.get(i).locX
                 && Current.locY == internalMap.get(i).locY) {
            isNewLocation = false;
            break;
         }
      }
      if (isNewLocation) {
         internalMap.add(Current);
      }
   }

   /**
    * Write internal map to file 
    * <p> 
    * This method writes the saved internal map
    * to a file called "FloorPlanDump.xml" in the same format as the project
    * input file.
    */
   private void writeInternalMap() {
      BufferedWriter bw = null;
      File f = new File("FloorPlanDump.xml");
      try {
         bw = new BufferedWriter(new FileWriter(f));
      } catch (Exception e) {
         logger.log(Level.WARNING, "This file not created", e);
      }
      try {
         bw.write("<FloorPlanDump>\n");
         int chargeStation;
         for (int i = 0; i < internalMap.size(); i++) {
            if (internalMap.get(i).sI.atChargingStation) {
               chargeStation = 1;

            } else {
               chargeStation = 0;
            }
            bw.write("<cell xs='" + internalMap.get(i).locX
                    + "' ys='" + internalMap.get(i).locY
                    + "' ss='" + internalMap.get(i).sI.floor.floorType()
                    + "' ps='"
                    + internalMap.get(i).sI.features[direction.EAST.index()].feature()
                    + internalMap.get(i).sI.features[direction.WEST.index()].feature()
                    + internalMap.get(i).sI.features[direction.NORTH.index()].feature()
                    + internalMap.get(i).sI.features[direction.SOUTH.index()].feature()
                    + "' ds='0' cs='" + chargeStation + "' />\n");
         }
         bw.write("</ FloorPlanDump>");
         bw.close();
      } catch (Exception e) {
         logger.log(Level.WARNING, "Cannot Write to That File", e);
      }
   }

   /**
    * Write log if activities to file
    * <p> 
    * This method writes the activity log to a file called ActivityLog.csv.
    */
   private void writeTaskList() {
      BufferedWriter bw = null;
      File f = new File("ActivityLog.csv");
      try {
         bw = new BufferedWriter(new FileWriter(f));
      } catch (Exception e) {
         logger.log(Level.WARNING, "File not created", e);
      }
      try {
         bw.write("Action, Loc y/x, Chrg Station, Floor Type, Dirt Present, North, East, South, West, Battery, Dirt Capacity\n");
         for (int i = 0; i < tasksCompleted.size(); i++) {
            bw.write(tasksCompleted.get(i));
         }
         bw.close();
      } catch (Exception e) {
         logger.log(Level.WARNING, "Cannot Write to File", e);
      }
   }

   
   /**
    * Add all sensor information to the log
    * 
    * @param SensorInterface current - contains all current sensor information
    * @param Log action - what is being done
    */
   private void addCompletedTask(SensorInterface current, Log action) {
      String dp;
      String cs;
      if (current.dirtPresent) {
         dp = "Yes";
      } else {
         dp = "No";
      }
      if (current.atChargingStation) {
         cs = "Yes";
      } else {
         cs = "No";
      }
      tasksCompleted.add( action 
              + ", " + currentY+ "/" +currentX
              + ", " + cs
              + ", " + current.floor
              + ", " + dp
              + "," + current.features[direction.NORTH.index()]
              + "," + current.features[direction.EAST.index()]
              + "," + current.features[direction.SOUTH.index()]
              + "," + current.features[direction.WEST.index()]
              + "," + guages.charge() / 10 + "." + guages.charge() % 10
              + "," + guages.dirtBinCapacity() + "\n");

   }
}