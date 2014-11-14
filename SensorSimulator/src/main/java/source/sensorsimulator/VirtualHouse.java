package source.sensorsimulator;

import source.sensorsimulator.SensorInterface.direction;
import source.sensorsimulator.SensorInterface.feature;
import java.io.File;
import javax.swing.JOptionPane;


/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 *
 * This class implements the Sensor Simulator portion of the Clean Sweep Robotic
 * Vacuum Cleaner Team Project (Clean Sweep). The purpose is to isolate the
 * Control System portion of the project from the input file containing
 * test information. All interfaces to gather legal, within the parameters
 * of the project assignment, data are included as well as interfaces
 * required to update the data.
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharpe, Doug Oda
 * @version     I1
 * @date        11Sep2014
 */
public class VirtualHouse {

   private FloorGraphics picture;
   private boolean useGraphics;
   private CellDescription currentCell;
   private boolean hasSentInitialLocation;
   private FloorPlan floorPlan;

   /**
    * Constructor for VirtualHouse                           
    * <p>
    * The constructor initializes all private variables, prompts the user to 
    * input a map file and determines if the user wants graphics. The constructor
    * then iterates through the map file, extracts relevant information and
    * pushes it into a list of CellDescription. The information in this
    * list will be used to provided sensor information to the CleanSweepRobot.
    * <p>
    * The constructor also instantiates the graphics if applicable.
    */
   public VirtualHouse() {
      String inputFile = null;
      useGraphics = true;
      hasSentInitialLocation = false;

      for (;;) {
         /*prompt user for inptu file*/
         inputFile = JOptionPane.showInputDialog("Type Input File with floor plan (default: .\\floorplan.xml)", ".\\floorplan.xml");

         /*user hit cancel so get out*/
         if (inputFile == null) {
            return;
         }
         /*verify that the file is valid*/
         File f = new File(inputFile);
         if (f.canRead()) {
            break;
         } else {
            JOptionPane.showMessageDialog(null, "Input file not found");
         }
      }

      /*Fead file and put information in to a list for future reference*/
      floorPlan = new FloorPlan(inputFile);
      currentCell = new CellDescription(floorPlan.getCell ( floorPlan.getInitialX(), floorPlan.getInitialY()));

      /*Start graphics if desired*/
      if (useGraphics) {
         picture = new FloorGraphics(floorPlan.floorPlan());
         picture.updateGraphics();
      }

   }

   /**
    * Overloaded Constructor for VirtualHouse                           
    * <p>
    * The constructor is used for JUnit testing. It reads in the test file 
    * and initializes the same variables as the regular constructor.
    */
   public VirtualHouse(boolean jUnitTesting) {
      if (jUnitTesting) {
         useGraphics = false;
      }
      hasSentInitialLocation = false;
      floorPlan = new FloorPlan("JUnitTestFloorPlan.xml");
      currentCell = new CellDescription(floorPlan.getCell ( floorPlan.getInitialX(), floorPlan.getInitialY()));
   }



   /**
    * Remove jPanel                          
    * <p>
    * Removes jPanel window graphic and should be called before program exits
    */
   public void remove() {
      if (useGraphics) {
         picture.remove();
      }
   }

   /**
    * Remove 1 unit of dirt from floor at current location                        
    */
   public void vacuum() {
      if (currentCell.dirt() > 0) {
         currentCell.dirt( currentCell.dirt() - 1);
      }
   }

   /**
    * Moves the Robot                           
    * <p>
    * This method provides the a way for the CleanSweepRobot (Control System) to
    * inform the VirtualHouse (Sensor Simulator) where it intends to move.
    * The new coordinates are checked to make sure that the new coordinates
    * do not violate the virtual laws of physics when moving from the old
    * coordinates. If the move is valid then the new coordinates are stored
    * and subsequent queries and actions are based on the new coordinates.
    *
    * @param  int newX - Desired new x coordinate
    * @param  int newY - Desired new y coordinate
    * @return true if movement is legal, false if new coordinates would move the
    *      robot though walls or coordinates are not adjacent to old coordinates
    */
   public boolean move(int newX, int newY) {
      /*update graphics if wanted*/
      if (useGraphics) {
         picture.updateGraphics();
      }

      boolean movementOK = false;
      for (direction d : direction.values()) {
         if ((currentCell.locX() + d.xOffset()) == newX
                 && (currentCell.locY() + d.yOffset()) == newY
                 && currentCell.sI.features[d.index()] == feature.OPEN) {
            movementOK = true;
         }
      }
      if (movementOK) {
         currentCell.isCurrentCell(false);

         /*Get new cell information*/
         if ( floorPlan.getCell ( newX, newY ) != null ){
         currentCell = floorPlan.getCell ( newX, newY );
         }
         currentCell.isCurrentCell(true);
      }
      return movementOK;
   }

   /**
    * provides initial XY coordinate                          
    * <p>
    * This method provides the X and Y coordinates of the charging station which
    * is where the CleanSweepRobot will start. Since this information is not
    * sensor information, it will pass the information only once.
    *
    * @param  SensorInterface si - reference to sensor interface for passing xy
    */
   public void getInitialLocation(SensorInterface si) {
      if (si == null) {
         return;
      }
      if (!hasSentInitialLocation) {
         hasSentInitialLocation = true;
               si.startingXCoord = floorPlan.getInitialX();
               si.startingYCoord = floorPlan.getInitialY();
      } else {
         si.startingXCoord = Integer.MAX_VALUE;
         si.startingYCoord = Integer.MAX_VALUE;
      }
   }

   /**
    * Provides all sensor information                        
    * <p>
    * This method provides all legal, within the parameters of the project 
    * assignment, data from the VirtualHouse (Sensor Simulator) and the
    * CleanSweepRobot (Control System)
    *
    * @param  SensorInterface si - reference to sensor interface for passing data
    */
   public void sensorInformation(SensorInterface tempSI) {
      if (tempSI != null) {
         for (direction d : direction.values()) {
            tempSI.features[d.index()] = currentCell.sI.features[d.index()];
         }
         tempSI.floor = currentCell.sI.floor;
         tempSI.atChargingStation = currentCell.sI.atChargingStation;
         if (currentCell.dirt() > 0) {
            tempSI.dirtPresent = true;
         } else {
            tempSI.dirtPresent = false;
         }
      }
   }
}