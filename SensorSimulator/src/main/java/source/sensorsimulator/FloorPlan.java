package source.sensorsimulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import source.sensorsimulator.SensorInterface.direction;
import source.sensorsimulator.SensorInterface.floorType;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 *
 * This class reads a floorplan from a file and stores it for use in the 
 * project
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharpe, Doug Oda
 * @version     I3
 * @date        13Nov2014
 */
public class FloorPlan {

   private List<CellDescription> floorPlan;
   private static final Logger LOGGER = Logger.getLogger("Exceptions");
   private int ChargingStationY;
   private int ChargingStationX;

   public FloorPlan(String fileName) {
      floorPlan = new LinkedList<>();
      getFloorPlan(fileName);

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
   private void getFloorPlan(String inputFile) {
      String line = null;
      BufferedReader br = null;
      File f = new File(inputFile);
      try {
         br = new BufferedReader(new FileReader(f));
      } catch (Exception e) {
         LOGGER.log(Level.WARNING, "File not Found", e);
      }
      for (;;) {
         try {
            line = br.readLine();
         } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Cannot Read File", e);
         }
         if (line == null) {
            break;
         }
         if (line.contains("cell")) {
            /*make new cell*/
            CellDescription cellDescription = new CellDescription();
            cellDescription.sI.atChargingStation = true;
            getFloorPlanGetXY(line, cellDescription);
            getFloorPlanGetFloor(line, cellDescription);
            getFloorPlanGetDirt(line, cellDescription);
            getFloorPlanWS(line, cellDescription);
            getFloorPlanChargingStation(line, cellDescription);
            /*save it*/
            floorPlan.add(cellDescription);
         }
      }
      try {
         br.close();
      } catch (Exception e) {
         LOGGER.log(Level.WARNING, "Cannot close file", e);
      }
   }

   /**
    * Get X and Y
    * 
    * @param line -a single line of the input file
    * @param cellDescription - where to place the newly acquired info
    */
   private void getFloorPlanGetXY(String line, CellDescription cellDescription) {
      int startIndex;
      int endIndex;
      /*get x*/
      startIndex = line.indexOf("xs") + 4;
      endIndex = line.indexOf('\'', startIndex);
      try {
         cellDescription.locX(Integer.parseInt(line.substring(startIndex, endIndex)));
      } catch (Exception e) {
         LOGGER.log(Level.WARNING, "Invalid File, x not an int", e);
      }
      /*get Y*/
      startIndex = line.indexOf("ys") + 4;
      endIndex = line.indexOf('\'', startIndex);
      try {
         cellDescription.locY(Integer.parseInt(line.substring(startIndex, endIndex)));
      } catch (Exception e) {
         LOGGER.log(Level.WARNING, "Invalid File, Y not an int", e);
      }
   }

   /**
    * Get FloorType
    * 
    * @param line -a single line of the input file
    * @param cellDescription - where to place the newly acquired info
    */
   private void getFloorPlanGetFloor(String line, CellDescription cellDescription) {
      int startIndex;
      int endIndex;
      /*get surface*/
      startIndex = line.indexOf("ss") + 4;
      endIndex = line.indexOf('\'', startIndex);
      switch (line.substring(startIndex, endIndex)) {
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
   }

   /**
    * Get Dirt Qty
    * 
    * @param line -a single line of the input file
    * @param cellDescription - where to place the newly acquired info
    */
   private void getFloorPlanGetDirt(String line, CellDescription cellDescription) {
      int startIndex;
      int endIndex;
      /*get amount of dirt of floor*/
      startIndex = line.indexOf("ds") + 4;
      endIndex = line.indexOf('\'', startIndex);
      try {
         cellDescription.dirt(Integer.parseInt(line.substring(startIndex, endIndex)));
      } catch (Exception e) {
         LOGGER.log(Level.WARNING, "Invalid file dirt not an int", e);
      }
      if (cellDescription.dirt() > 0) {
         cellDescription.sI.dirtPresent = true;
      } else {
         cellDescription.sI.dirtPresent = false;
      }
   }

   /**
    * Get Wall Sensor Info
    * 
    * @param line -a single line of the input file
    * @param cellDescription - where to place the newly acquired info
    */
   private void getFloorPlanWS(String line, CellDescription cellDescription) {
      int startIndex;
      /*get wall sensors*/
      direction n = direction.NORTH;
      direction e = direction.EAST;
      direction s = direction.SOUTH;
      direction w = direction.WEST;
      startIndex = line.indexOf("ps") + 4;
      switch (line.substring(startIndex, startIndex + 1)) {
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
      switch (line.substring(startIndex, startIndex + 1)) {
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
      switch (line.substring(startIndex, startIndex + 1)) {
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
      switch (line.substring(startIndex, startIndex + 1)) {
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
   }

   /**
    * Get charging station
    *
    * @param line -a single line of the input file
    * @param cellDescription - where to place the newly acquired info
    */
   private void getFloorPlanChargingStation(String line, CellDescription cellDescription) {
      int startIndex;
      int endIndex;
      /*check if it is charging station*/
      startIndex = line.indexOf("cs") + 4;
      endIndex = line.indexOf('\'', startIndex);
      if ("1".equals(line.substring(startIndex, endIndex))) {
         cellDescription.sI.atChargingStation = true;
         cellDescription.isCurrentCell(true);
         ChargingStationX = cellDescription.locX();
         ChargingStationY = cellDescription.locY();
      } else {
         cellDescription.sI.atChargingStation = false;
      }
   }

   /**
    * Return a copy of the floor plan for use with graphics
    * 
    * @return entire floorPlan for use with graphics
    */
   public List<CellDescription> floorPlan() {
      List<CellDescription> returnList = new LinkedList<>();
      for (int i = 0; i < floorPlan.size(); i++) {
         returnList.add(floorPlan.get(i));
      }
      return returnList;
   }

   /**
    * Return a copy of the cell description at the parameter coords
    * 
    * @param newX X location of desired cell
    * @param newY Y location of desired cell
    * @return cell
    */
   public CellDescription getCell(int newX, int newY) {
      for (int i = 0; i < floorPlan.size(); i++) {
         if (floorPlan.get(i).locX() == newX
                 && floorPlan.get(i).locY() == newY) {
            return new CellDescription(floorPlan.get(i));
         }
      }
      return null;
   }

   /**
    * Accessor for charging station X coord
    * 
    * @return X where charging station resides
    */
   public int getInitialX() {
      return ChargingStationX;
   }

   /**
    * Accessor for charging station Y coord
    * 
    * @return Y where charging station resides
    */
   public int getInitialY() {
      return ChargingStationY;
   }
}
