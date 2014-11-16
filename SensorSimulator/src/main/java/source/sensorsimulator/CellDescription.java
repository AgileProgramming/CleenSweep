package source.sensorsimulator;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 *
 * This class is a container for the cell descriptions used on the sensor
 * simulator side of the project
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharpe, Doug Oda
 * @version     I3
 * @date        13Nov2014
 */
public class CellDescription {

   SensorInterface sI;
   private int dirt;
   private int locX;
   private int locY;
   private boolean isCurrentCell;

   
   /**
    * Constructor that creates a bare bones CellDescription
    */
   public CellDescription() {
      sI = new SensorInterface();
   }

   /**
    * Constructor that initializes all this members with parameter values
    * @param cd CellDescription to be copied
    */
   public CellDescription(CellDescription cd) {
      this.dirt = cd.dirt;
      this.isCurrentCell = cd.isCurrentCell;
      this.locX = cd.locX;
      this.locY = cd.locY;
      sI = new SensorInterface();
      sI.atChargingStation = cd.sI.atChargingStation;
      sI.dirtPresent = cd.sI.dirtPresent;
      sI.floor = cd.sI.floor;
      sI.features[0] = cd.sI.features[0];
      sI.features[1] = cd.sI.features[1];
      sI.features[2] = cd.sI.features[2];
      sI.features[3] = cd.sI.features[3];
      sI.startingXCoord = cd.sI.startingXCoord;
      sI.startingYCoord = cd.sI.startingYCoord;
   }

      /**
    * Copy parameter to this
    * @param cd CellDescription to be copied
    */
   public void copy(CellDescription cd) {
      this.dirt = cd.dirt;
      this.isCurrentCell = cd.isCurrentCell;
      this.locX = cd.locX;
      this.locY = cd.locY;
      sI = new SensorInterface();
      sI.atChargingStation = cd.sI.atChargingStation;
      sI.dirtPresent = cd.sI.dirtPresent;
      sI.floor = cd.sI.floor;
      sI.features[0] = cd.sI.features[0];
      sI.features[1] = cd.sI.features[1];
      sI.features[2] = cd.sI.features[2];
      sI.features[3] = cd.sI.features[3];
      sI.startingXCoord = cd.sI.startingXCoord;
      sI.startingYCoord = cd.sI.startingYCoord;
   }
   
   /**
    * Get sensor interface
    * @return sensor Interface
    */
   public SensorInterface sI() {
      return sI;
   }

   /**
    * Get amount of dirt
    * @return amount of dirt
    */
   public int dirt() {
      return dirt;
   }

   /**
    * Get X coordinate
    * @return X coordinate
    */
   public int locX() {
      return locX;
   }

   /**
    * Get Y coordinate
    * @return Y coordinate
    */
   public int locY() {
      return locY;
   }
   
   /**
    * Set X coordinate
    * @param x =X coordinate
    */
   public void locX(int x) {
      locX = x;
   }
 
   /**
    * Set Y coordinate
    * @param y =y coordinate
    */
   public void locY(int y) {
      locY = y;
   }

   /**
    * Set amount of dirt
    * @param d = amount of dirt
    */
   public void dirt(int d) {
      dirt = d;
   }

   /**
    * Set current cell
    * @param b 
    */
   public void isCurrentCell(boolean b) {
      isCurrentCell = b;
   }

   /**
    * Is this the current cell?
    * @return true if this is the current cell
    */
   public boolean isCurrentCell() {
      return isCurrentCell;
   }
}
