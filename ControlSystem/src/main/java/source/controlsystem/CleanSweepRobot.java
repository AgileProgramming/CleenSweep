package source.controlsystem;

import source.sensorsimulator.SensorInterface.direction;
import source.sensorsimulator.SensorInterface.feature;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import source.sensorsimulator.InternalSensors;
import source.sensorsimulator.SensorInterface;
import source.sensorsimulator.VirtualHouse;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 *
 * This class implements the Control System portion of the Clean Sweep Robotic
 * Vacuum Cleaner Team Project (Clean Sweep). The purpose is to provide the 
 * required functionality of the Clean Sweep. 
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharp, Doug Oda
 * @version     I2
 * @date        25Sep2014
 */
public class CleanSweepRobot {

   private class LocationOfNotVisited {

      public int notVisitedX;
      public int notVisitedY;

      public LocationOfNotVisited(int x, int y) {
         notVisitedX = x;
         notVisitedY = y;
      }
   }

   public class CellDescription {

      public SensorInterface sI;
      public int locX;
      public int locY;

      public CellDescription() {
         sI = new SensorInterface();
      }
   }

   private enum Tasks {

      SWEEP,
      CHECK_SENSORS,
      MOVE;
   }
   /*Private Variables*/
   private VirtualHouse vH;
   private LinkedList<CellDescription> internalMap;
   private LinkedList<LocationOfNotVisited> destinations;
   private LinkedList<Tasks> tasksCompleted;
   private InternalSensors guages;
   private int currentX;
   private int currentY;
   private boolean stuck;
   /*Exception log for IO*/
   private static final Logger logger = Logger.getLogger("Exceptions");

   /**
    * Constructor for CleanSweepRobot                        
    * <p>
    * This method instantiates private lists, and saves a reference to the
    * Sensor Simulator
    *
    * @param  VirtualHouse virtualHouse - A Sensor Simulator reference
    */
   public CleanSweepRobot(VirtualHouse virtualHouse) {

      /*save a reference to the sensor simulator*/
      vH = virtualHouse;

      /*instantiate a few lists*/
      internalMap = new LinkedList<CellDescription>();
      destinations = new LinkedList<LocationOfNotVisited>();
      tasksCompleted = new LinkedList<Tasks>();

      /*get som initial information from the sensor simulator*/
      SensorInterface ci = new SensorInterface();
      vH.GetInitialLocation(ci);
      currentX = ci.StartingXCoord;
      currentY = ci.StartingYCoord;
      vH.SensorInformation(ci);

      /*setup internal sensors*/
      guages = new InternalSensors(ci.floor);

      /*intialize other variables*/
      stuck = false;
   }

      /**
    * Overloaded Constructor for CleanSweepRobot                        
    * <p>
    * used for unit testing
    *
    * @param  VirtualHouse virtualHouse - A Sensor Simulator reference
    * @param  VirtualHouse virtualHouse - A Sensor Simulator reference
    * @param  VirtualHouse virtualHouse - A Sensor Simulator reference
    */
   public CleanSweepRobot(VirtualHouse virtualHouse, int x, int y) {

      /*save a reference to the sensor simulator*/
      vH = virtualHouse;

      /*instantiate a few lists*/
      internalMap = new LinkedList<CellDescription>();
      destinations = new LinkedList<LocationOfNotVisited>();
      tasksCompleted = new LinkedList<Tasks>();

      /*get som initial information from the sensor simulator*/
      SensorInterface ci = new SensorInterface();
      currentX = x;
      currentY = y;
      vH.SensorInformation(ci);

      /*setup internal sensors*/
      guages = new InternalSensors(ci.floor);

      /*intialize other variables*/
      stuck = false;
   }
   
   /**
    * Update the Clean Sweep Control System                        
    * <p>
    * This method:
    * -Gets new cell information and adds the internally stored XY coord 
    * -Adds the cell to the internal map
    * -Adds to a list of places to visit
    * -Moves the robot to one of the places not visited if it exists
    *
    * @return false if all locations have been visited, true if more 
    */
   public boolean cleanSweepUpdate() {
      CellDescription Current = new CellDescription();

      /*Check Sensors*/
      tasksCompleted.addLast(Tasks.CHECK_SENSORS);
      vH.SensorInformation(Current.sI);
      Current.locX = currentX;
      Current.locY = currentY;

      /*Sweep if necessary*/
      while (Current.sI.dirtPresent) {
         vH.Vacuum();
         tasksCompleted.addLast(Tasks.SWEEP);
         guages.swept();
         tasksCompleted.addLast(Tasks.CHECK_SENSORS);
         vH.SensorInformation(Current.sI);
      }

      /*Save cell description*/
      addToInternalMap(Current);

      /*Update destinations*/
      updateNotVisitedList(Current);

      /*Move Clean Sweep*/
      if (destinations.size() > 0) {
         tasksCompleted.addLast(Tasks.MOVE);
         moveCleanSweep(Current);
         SensorInterface ci = new SensorInterface();
         vH.SensorInformation(ci);
         guages.moved(ci.floor);
      } else {
         /*All done so save internal map to file*/
         writeInternalMap();

         /*Save task list*/
         writeTaskList();
         return false;
      }
      return true;
   }

   /**
    * Move the robot to or toward the last location in the destinations list                         
    * <p>
    * This method obtains the xy coordinates from the destinations list and
    * moves the robot one legal move from the parameter location to or toward 
    * the destination. If moving toward the destination is not possible then
    * try the next to last location on the destinations list.
    *
    * @param  CellDescription Current- name of cell from which to move
    */
   private void moveCleanSweep(CellDescription Current) {
      if (stuck) {
         /* The size will always be greater than 1 if the robot is stuck
         but should check anyway */
         if (destinations.size() > 1) {
            destinations.addLast(destinations.get(destinations.size() - 2));
         }
      }

      /* Load place we want to be*/
      int targetx = destinations.getLast().notVisitedX;
      int targety = destinations.getLast().notVisitedY;

      /* Check in all 4 directions and move if desired and possible */
      stuck = true;
      for (SensorInterface.direction d : SensorInterface.direction.values()) {
         if ((((targety > Current.locY) && (d.index() == direction.NORTH.index()))
                 || ((targetx > Current.locX) && (d.index() == direction.EAST.index()))
                 || ((targety < Current.locY) && (d.index() == direction.SOUTH.index()))
                 || ((targetx < Current.locX) && (d.index() == direction.WEST.index())))
                 && (Current.sI.features[d.index()] == SensorInterface.feature.OPEN)) {
            /*If the mess above is true then actually move the darn thing*/
            if (vH.Move(Current.locX + d.xOffset(), Current.locY + d.yOffset())) {
               currentX = Current.locX + d.xOffset();
               currentY = Current.locY + d.yOffset();
               stuck = false;
               break;
            }
         }
      }
      /*If we have moved to the last location on the list then remove it
       * since it is no longer a destination
       */
      if (currentX
              == destinations.getLast().notVisitedX
              && currentX == destinations.getLast().notVisitedX) {
         destinations.removeLast();
      }
   }

   /**
    * Add cells to list of cells that need to be visited                         
    * <p>
    * This method check to see if, from the perspective of the current xy 
    * location, a cell in any of the 4 directions is obtainable (feature=open),
    * not already in the internalMap and not already in the destinations list.
    *
    * @param  CellDescription Current- current cell
    */
   private void updateNotVisitedList(CellDescription Current) {
      boolean WantToGoThere;
      for (direction d : direction.values()) {
         /*Check each of 4 directions*/
         if (Current.sI.features[d.index()] == feature.OPEN) {
            /*If there is not an obsitcle north then check space has
             * already been visited*/
            WantToGoThere = true;
            for (int i = 0; i < internalMap.size(); i++) {
               if ((Current.locX + d.xOffset()) == internalMap.get(i).locX
                       && (Current.locY + d.yOffset()) == internalMap.get(i).locY) {
                  WantToGoThere = false;
                  break;
               }
            }
            if (WantToGoThere) {
               /*If here then there is no obsitcle and not been visited
               so remove any old occurances of that location*/
               for (int i = 0; i < destinations.size(); i++) {
                  if ((Current.locX + d.xOffset()) == destinations.get(i).notVisitedX
                          && (Current.locY + d.yOffset()) == destinations.get(i).notVisitedY) {
                     destinations.remove(i);
                     break;
                  }
               }
               /*Add to places that need visited*/
               destinations.add(new LocationOfNotVisited((Current.locX + d.xOffset()), (Current.locY + d.yOffset())));
            }
         }
      }
   }

   /**
    * Add cell descriptions to internal floor plan                          
    * <p>
    * This method adds only original cells to the floor plan by iterating through
    * the list of cells and discarding parameter if the xy coordinates are 
    * already in the list
    *
    * @param  CellDescription Current- name of cell to add to the floor plan
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
    * This method writes the saved internal map to a file called "FloorPlanDump.xml" in
    * the same format as the project input file.
    */
   private void writeInternalMap() {
      BufferedWriter bw = null;
      File f = new File("FloorPlanDump.xml");
      try {
         bw = new BufferedWriter(new FileWriter(f));
      } catch (Exception e) {
         logger.log(Level.WARNING, "File not created", e);
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
         logger.log(Level.WARNING, "Cannot Write to File", e);
      }
   }

   /**
    * Write log if activities to file                         
    * <p>
    * This method writes the activity log to a file called ActivityLog.txt.
    */
   private void writeTaskList() {
      BufferedWriter bw = null;
      File f = new File("ActivityLog.txt");
      try {
         bw = new BufferedWriter(new FileWriter(f));
      } catch (Exception e) {
         logger.log(Level.WARNING, "File not created", e);
      }
      try {

         for (int i = 0; i < tasksCompleted.size(); i++) {
            bw.write(tasksCompleted.get(i).name() + ",\n");
         }
         bw.close();
      } catch (Exception e) {
         logger.log(Level.WARNING, "Cannot Write to File", e);
      }
   }
}
