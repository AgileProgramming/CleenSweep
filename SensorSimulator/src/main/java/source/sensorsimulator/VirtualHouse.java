package source.sensorsimulator;

import source.sensorsimulator.SensorInterface.direction;
import source.sensorsimulator.SensorInterface.feature;
import source.sensorsimulator.SensorInterface.floorType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.util.*;

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
public class VirtualHouse{
   
   public class CellDescription{
      SensorInterface sI;
      private int dirt;
      private int locX;
      private int locY;
      private boolean isCurrentCell;

      public CellDescription(){
         sI = new SensorInterface();
      }
      public SensorInterface sI(){
         return sI;
      }
      public int dirt(){
         return dirt;
      }
      public int locX(){
         return locX;
      }
      public int locY(){
         return locY;
      }
      public boolean isCurrentCell(){
         return isCurrentCell;
      }
      
   }
   private FloorGraphics picture;
   private boolean useGraphics;
   private CellDescription currentCell;
   private boolean hasSentInitialLocation;
   private List<CellDescription> floorPlan;
   private static final Logger LOGGER = Logger.getLogger("Exceptions");
   private static final String INVALID_FILE_ERROR_MESSAGE = "Bad input file format";
   
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
   public VirtualHouse(){
      String inputFile = null;
      useGraphics = true;
      hasSentInitialLocation = false;
      floorPlan = new LinkedList<>();

      for (;;){
       /*prompt user for inptu file*/
       inputFile = JOptionPane.showInputDialog("Type Input File with floor plan (default: .\\floorplan.xml)",".\\floorplan.xml");
       
       /*user hit cancel so get out*/
       if (inputFile == null){
           return;
       }
       /*verify that the file is valid*/
       File f = new File(inputFile);
       if (f.canRead()){
         break;
       }else{
          JOptionPane.showMessageDialog(null, "Input file not found");
       }
      
      }

      /*Fead file and put information in to a list for future reference*/
      getFloorPlan(inputFile);

      /*Start graphics if desired*/
      if (useGraphics){
         picture = new FloorGraphics(floorPlan);
         picture.updateGraphics();
      }
      
   }

   /**
    * Overloaded Constructor for VirtualHouse                           
    * <p>
    * The constructor is used for JUnit testing. It reads in the test file 
    * and initializes the same variables as the regular constructor.
    */
   public VirtualHouse(boolean jUnitTesting){
      useGraphics = false;
      hasSentInitialLocation = false;
      floorPlan = new LinkedList<>();
      getFloorPlan("JUnitTestFloorPlan.xml");
   }

   /**
    * Read Floor plan from .xml file                           
    * <p>
    * This method reads each line from the input file passed as a parameter
    * and each line that contains cell information is parsed and stored in
    * a CellDescription that is added to the floorPlan list
    *
    * @param  String inputFile - name of .xml file that contains floor plan
    */
   private void getFloorPlan(String inputFile){
      String line = null;
      BufferedReader br = null;
      File f = new File(inputFile);
      try{
         br = new BufferedReader(new FileReader(f));
      }catch (Exception e){
         LOGGER.log(Level.WARNING, "File not Found", e);
      }
      for (;;){
         try{
            line = br.readLine();
         }catch (Exception e){
            LOGGER.log(Level.WARNING, "Cannot Read File", e);
         }
         if (line == null){
            break;
         }
         int startIndex;
         int endIndex;
         if (line.contains("cell")){
            /*make new cell*/
            CellDescription cellDescription = new CellDescription();

            /*get x*/
            startIndex = line.indexOf("xs") + 4;
            endIndex = line.indexOf('\'', startIndex);
            try{
               cellDescription.locX = Integer.parseInt(line.substring(startIndex, endIndex));
            }catch (Exception e){
               LOGGER.log(Level.WARNING, INVALID_FILE_ERROR_MESSAGE, e);
            }
            /*get Y*/
            startIndex = line.indexOf("ys") + 4;
            endIndex = line.indexOf('\'', startIndex);
            try{
               cellDescription.locY = Integer.parseInt(line.substring(startIndex, endIndex));
            }catch (Exception e){
               LOGGER.log(Level.WARNING, INVALID_FILE_ERROR_MESSAGE, e);
            }
            /*get surface*/
            startIndex = line.indexOf("ss") + 4;
            endIndex = line.indexOf('\'', startIndex);
            switch (line.substring(startIndex, endIndex))
            {
               case "4":
                  cellDescription.sI.floor = floorType.HighPileCarpet;
                  break;
               case "2":
                  cellDescription.sI.floor = floorType.LowPileCarpet;
                  break;
               default:
                  cellDescription.sI.floor = floorType.BareFloor;
                  break;
            }
            /*get amount of dirt of floor*/
            startIndex = line.indexOf("ds") + 4;
            endIndex = line.indexOf('\'', startIndex);
            try{
               cellDescription.dirt = Integer.parseInt(line.substring(startIndex, endIndex));
            }catch (Exception e){
               LOGGER.log(Level.WARNING, INVALID_FILE_ERROR_MESSAGE, e);
            }
            if (cellDescription.dirt > 0){
               cellDescription.sI.dirtPresent = true;
            }else{
               cellDescription.sI.dirtPresent = false;
            }
            /*get wall sensors*/
            direction n = direction.NORTH;
            direction e = direction.EAST;
            direction s = direction.SOUTH;
            direction w = direction.WEST;
            startIndex = line.indexOf("ps") + 4;
            switch (line.substring(startIndex, startIndex + 1))
            {
               case "1":
                  cellDescription.sI.features[e.index()] = SensorInterface.feature.OPEN;
                  break;
               case "2":
                  cellDescription.sI.features[e.index()] = SensorInterface.feature.OBSTICLE;
                  break;
               default:
                  cellDescription.sI.features[e.index()] = SensorInterface.feature.STAIRS;
                  break;
            }
            startIndex++;
            switch (line.substring(startIndex, startIndex + 1))
            {
               case "1":
                  cellDescription.sI.features[w.index()] = SensorInterface.feature.OPEN;
                  break;
               case "2":
                  cellDescription.sI.features[w.index()] = SensorInterface.feature.OBSTICLE;
                  break;
               default:
                  cellDescription.sI.features[w.index()] = SensorInterface.feature.STAIRS;
                  break;
            }
            startIndex++;
            switch (line.substring(startIndex, startIndex + 1))
            {
               case "1":
                  cellDescription.sI.features[n.index()] = SensorInterface.feature.OPEN;
                  break;
               case "2":
                  cellDescription.sI.features[n.index()] = SensorInterface.feature.OBSTICLE;
                  break;
               default:
                  cellDescription.sI.features[n.index()] = SensorInterface.feature.STAIRS;
                  break;
            }
            startIndex++;
            switch (line.substring(startIndex, startIndex + 1))
            {
               case "1":
                  cellDescription.sI.features[s.index()] = SensorInterface.feature.OPEN;
                  break;
               case "2":
                  cellDescription.sI.features[s.index()] = SensorInterface.feature.OBSTICLE;
                  break;
               default:
                  cellDescription.sI.features[s.index()] = SensorInterface.feature.STAIRS;
                  break;
            }
            startIndex++;
            /*check if it is charging station*/
            startIndex = line.indexOf("cs") + 4;
            endIndex = line.indexOf('\'', startIndex);
            if ("1".equals(line.substring(startIndex, endIndex))){
               cellDescription.sI.atChargingStation = true;
               currentCell = cellDescription;
               cellDescription.isCurrentCell = true;
            }else{
               cellDescription.sI.atChargingStation = false;
            }

            /*save it*/
            floorPlan.add(cellDescription);
         }
      }
      try{
         br.close();
      }catch (Exception e){
         LOGGER.log(Level.WARNING, "Cannot close file", e);
      }
   }

   /**
    * Remove jPanel                          
    * <p>
    * Removes jPanel window graphic and should be called before program exits
    */
   public void remove(){
      if (useGraphics){
         picture.remove();
      }
   }

   /**
    * Remove 1 unit of dirt from floor at current location                        
    */
   public void vacuum(){
      if (currentCell.dirt > 0){
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
   public boolean move(int newX, int newY){
      /*update graphics if wanted*/
      if (useGraphics){
         picture.updateGraphics();
      }
      
      boolean movementOK = false;
      for (direction d : direction.values()){
         if ((currentCell.locX + d.xOffset()) == newX
                 && (currentCell.locY + d.yOffset()) == newY
                 && currentCell.sI.features[d.index()] == feature.OPEN){
           movementOK = true;
        }
      }
      if (movementOK){
         currentCell.isCurrentCell = false;

         /*Get new cell information*/
         for (int i = 0; i < floorPlan.size(); i++){
            if (floorPlan.get(i).locX == newX
                    && floorPlan.get(i).locY == newY){
               currentCell = floorPlan.get(i);
               break;
            }
         }
         currentCell.isCurrentCell = true;
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
   public void getInitialLocation(SensorInterface si){
      if (si != null){
         if (hasSentInitialLocation){
            si.StartingXCoord = Integer.MAX_VALUE;
            si.StartingYCoord = Integer.MAX_VALUE;
         }else{
            hasSentInitialLocation = true;
            for (int i = 0; i < floorPlan.size(); i++){
               if (floorPlan.get(i).sI.atChargingStation){
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
   public void sensorInformation(SensorInterface tempSI){
      if (tempSI != null){
         for (direction d : direction.values()){
            tempSI.features[d.index()] = currentCell.sI.features[d.index()];
         }
         tempSI.floor = currentCell.sI.floor;
         tempSI.atChargingStation = currentCell.sI.atChargingStation;
         if (currentCell.dirt > 0){
            tempSI.dirtPresent = true;
         }else{
            tempSI.dirtPresent = false;
         }
      }
   }
}