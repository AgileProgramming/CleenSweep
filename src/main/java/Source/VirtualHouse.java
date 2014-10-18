package Source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharp, Doug Oda
 * @version     I1
 * @date        11Sep2014
 */
public class VirtualHouse {

   public class CellDescription {

      public SensorInterface sI;
      public int dirt;
      public int locX;
      public int locY;
      public boolean isCurrentCell;

      public CellDescription() {
         sI = new SensorInterface();
      }
   }
   private FloorGraphics picture;
   private boolean useGraphics;
   private CellDescription currentCell;
   private boolean hasSentInitialLocation;
   private LinkedList<CellDescription> floorPlan;
   private static final Logger logger = Logger.getLogger("Exceptions");

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
      String line = null;
      int maxDim = 0;

      hasSentInitialLocation = false;
      floorPlan = new LinkedList<CellDescription>();

      /*Get user input, xml file name and if the user wants the graphic*/

      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      for (;;) {
         System.out.println("Type Input File with floor plan (.\\floorplan.xml)");
         try {
            inputFile = in.readLine();
         } catch (Exception e) {
            logger.log(Level.SEVERE, "Input file not found", e);
         };
         if (inputFile.isEmpty()) {
            inputFile = "floorplan.xml";
         }
         File f = new File(inputFile);
         if (f.canRead()) {
            break;
         }
      }
      for (;;) {
         System.out.println("Dislay Graphics? Y/N");
         try {
            line = in.readLine();
         } catch (Exception e) {
            logger.log(Level.WARNING, "Keypad not working?", e);
         };
         if ("Y".equals(line) || "y".equals(line)) {
            useGraphics = true;
            break;
         }
         if ("N".equals(line) || "n".equals(line)) {
            useGraphics = false;
            break;
         }
      }

      /*Fead file and put information in to a list for future reference*/
      maxDim = GetFloorPlan(inputFile);

      /*Start graphics if desired*/
      if (useGraphics) {
         picture = new FloorGraphics(maxDim, maxDim, currentCell.locX,
                 currentCell.locY, floorPlan);
         picture.UpdateGraphics();
      }
   }

   /**
    * Overloaded Constructor for VirtualHouse                           
    * <p>
    * The constructor is used for JUnit testing. It reads in the test file 
    * and initializes the same variables as the regular constructor.
    */
   public VirtualHouse(boolean JUnitTesting) {
      useGraphics = false;
      hasSentInitialLocation = false;
      floorPlan = new LinkedList<CellDescription>();
      GetFloorPlan("JUnitTestFloorPlan.xml");
   }

   /**
    * Read Floor plan from .xml file                           
    * <p>
    * This method reads each line from the input file passed as a parameter
    * and each line that contains cell information is parsed and stored in
    * a CellDescription that is added to the floorPlan list
    *
    * @param  String inputFile - name of .xml file that contains floor plan
    * @return true an integer that is the maximum dimension in any direction
    *              of the floor plan.
    */
   private int GetFloorPlan(String inputFile) {
      String line = null;
      int maxDim = 0;
      BufferedReader br = null;
      File f = new File(inputFile);
      try {
         br = new BufferedReader(new FileReader(f));
      } catch (Exception e) {
         logger.log(Level.WARNING, "File not Found", e);
      };
      for (;;) {
         try {
            line = br.readLine();
         } catch (Exception e) {
            logger.log(Level.WARNING, "Cannot Read File", e);
         };
         if (line == null) {
            break;
         }
         int A;
         int B;
         if (line.contains("cell")) {
            /*make new cell*/
            CellDescription CD = new CellDescription();

            /*get x*/
            A = line.indexOf("xs") + 4;
            B = line.indexOf("'", A);
            try {
               CD.locX = Integer.parseInt(line.substring(A, B));
               if (CD.locX > maxDim) {
                  maxDim = CD.locX;
               }
            } catch (Exception e) {
               logger.log(Level.WARNING, "Bad input file format", e);
            }
            /*get Y*/
            A = line.indexOf("ys") + 4;
            B = line.indexOf("'", A);
            try {
               CD.locY = Integer.parseInt(line.substring(A, B));
               if (CD.locY > maxDim) {
                  maxDim = CD.locY;
               }
            } catch (Exception e) {
               logger.log(Level.WARNING, "Bad input file format", e);
            }
            /*get surface*/
            A = line.indexOf("ss") + 4;
            B = line.indexOf("'", A);

            if ("4".equals(line.substring(A, B))) {
               CD.sI.floor = SensorInterface.floorType.HighPileCarpet;
            } else if ("2".equals(line.substring(A, B))) {
               CD.sI.floor = SensorInterface.floorType.LowPileCarpet;
            } else {
               CD.sI.floor = SensorInterface.floorType.BareFloor;
            }
            /*get amount of dirt of floor*/
            A = line.indexOf("ds") + 4;
            B = line.indexOf("'", A);
            try {
               CD.dirt = Integer.parseInt(line.substring(A, B));
            } catch (Exception e) {
               logger.log(Level.WARNING, "Bad input file format", e);
            }
            if (CD.dirt > 0) {
               CD.sI.dirtPresent = true;
            } else {
               CD.sI.dirtPresent = false;
            }
            /*get wall sensors*/
            A = line.indexOf("ps") + 4;
            String a = line.substring(A, A + 1);
            if ("1".equals(line.substring(A, A + 1))) {
               CD.sI.east = SensorInterface.feature.OPEN;
            } else if ("2".equals(line.substring(A, A + 1))) {
               CD.sI.east = SensorInterface.feature.OBSTICLE;
            } else {
               CD.sI.east = SensorInterface.feature.STAIRS;
            }
            A++;
            if ("1".equals(line.substring(A, A + 1))) {
               CD.sI.west = SensorInterface.feature.OPEN;
            } else if ("2".equals(line.substring(A, A + 1))) {
               CD.sI.west = SensorInterface.feature.OBSTICLE;
            } else {
               CD.sI.west = SensorInterface.feature.STAIRS;
            }
            A++;
            if ("1".equals(line.substring(A, A + 1))) {
               CD.sI.north = SensorInterface.feature.OPEN;
            } else if ("2".equals(line.substring(A, A + 1))) {
               CD.sI.north = SensorInterface.feature.OBSTICLE;
            } else {
               CD.sI.north = SensorInterface.feature.STAIRS;
            }
            A++;
            if ("1".equals(line.substring(A, A + 1))) {
               CD.sI.south = SensorInterface.feature.OPEN;
            } else if ("2".equals(line.substring(A, A + 1))) {
               CD.sI.south = SensorInterface.feature.OBSTICLE;
            } else {
               CD.sI.south = SensorInterface.feature.STAIRS;
            }
            /*check if it is charging station*/
            A = line.indexOf("cs") + 4;
            B = line.indexOf("'", A);
            if ("1".equals(line.substring(A, B))) {
               CD.sI.atChargingStation = true;
               currentCell = CD;
               CD.isCurrentCell = true;
            } else {
               CD.sI.atChargingStation = false;
            }

            /*save it*/
            floorPlan.add(CD);
         }
      }
      try {
         br.close();
      } catch (Exception e) {
         logger.log(Level.WARNING, "Cannot close file", e);
      };
      return maxDim;
   }

   /**
    * Remove jPanel                          
    * <p>
    * Removes jPanel window graphic and should be called before program exits
    */
   public void Remove() {
      if (useGraphics){
         picture.Remove();
      }
   }

   /**
    * Remove 1 unit of dirt from floor at current location                        
    */
   public void Vacuum() {
      if (currentCell.dirt > 0) {
         currentCell.dirt--;
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
   public boolean Move(int newX, int newY) {
      boolean movementOK = false;
      if (currentCell.locX == newX && (currentCell.locY + 1) == newY) {
         if (currentCell.sI.north == SensorInterface.feature.OPEN) {
            movementOK = true;
         }
      }
      if ((currentCell.locX + 1) == newX && currentCell.locY == newY) {
         if (currentCell.sI.east == SensorInterface.feature.OPEN) {
            movementOK = true;
         }
      }
      if (currentCell.locX == newX && (currentCell.locY - 1) == newY) {
         if (currentCell.sI.south == SensorInterface.feature.OPEN) {
            movementOK = true;
         }
      }
      if ((currentCell.locX - 1) == newX && currentCell.locY == newY) {
         if (currentCell.sI.west == SensorInterface.feature.OPEN) {
            movementOK = true;
         }
      }
      if (movementOK) {
         currentCell.isCurrentCell = false;

         /*Get new cell information*/
         for (int i = 0; i < floorPlan.size(); i++) {
            if (floorPlan.get(i).locX == newX
                    && floorPlan.get(i).locY == newY) {
               currentCell = floorPlan.get(i);
               break;
            }
         }
         currentCell.isCurrentCell = true;

         /*update graphics if wanted*/
         if (useGraphics) {
            /*update graphics*/
            picture.UpdateGraphics();
         }
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
   public void GetInitialLocation(SensorInterface si) {
      if (si != null) {
         if (hasSentInitialLocation) {
            si.StartingXCoord = Integer.MAX_VALUE;
            si.StartingYCoord = Integer.MAX_VALUE;
         } else {
            hasSentInitialLocation = true;
            for (int i = 0; i < floorPlan.size(); i++) {
               if (floorPlan.get(i).sI.atChargingStation) {
                  si.StartingXCoord = floorPlan.get(i).locX;
                  si.StartingYCoord = floorPlan.get(i).locY;
                  break;
               }
            }
         }
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
   public void SensorInformation(SensorInterface tempSI) {
      if (tempSI != null) {
         tempSI.north = currentCell.sI.north;
         tempSI.east = currentCell.sI.east;
         tempSI.south = currentCell.sI.south;
         tempSI.west = currentCell.sI.west;
         tempSI.floor = currentCell.sI.floor;
         tempSI.atChargingStation = currentCell.sI.atChargingStation;
         if (currentCell.dirt > 0) {
            tempSI.dirtPresent = true;
         } else {
            tempSI.dirtPresent = false;
         }
      }
   }
}