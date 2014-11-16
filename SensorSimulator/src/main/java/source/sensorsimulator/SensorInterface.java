package source.sensorsimulator;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 * 
 * This is a container class to pass legal, within the parameters
 * of the project assignment, data from the VirtualHouse (Sensor Simulator) to
 * the Clean Sweep Robot (Control System)
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharpe, Doug Oda
 * @version     I1
 * @date        11Sep2014
 */
public class SensorInterface {
   /*
    * Enumerator for features defined in the project as well as one
    * that my be useful to aid in navigation.
    */

   public enum feature {

      UNKNOWN(0),
      OPEN(1),
      OBSTICLE(2),
      DOOR(3),
      STAIRS(4);
      private int f;

      private feature(int i) {
         this.f = i;
      }

      public int feature() {
         return f;
      }
   }

   /*
    * Enumerator for floor types defined in the project. The battery charge
    * required to clean and move across the surfaces are included NOTE that 
    * the charges are in 10x. This avoids doubles and the comparision issues
    * that result
    */
   public enum floorType {

      BARE_FLOOR(1, 10),
      LOW_PILE_CARPET(2, 20),
      HIGH_PILE_CARPET(4, 30);
      private int ft;
      private int c;

      private floorType(int ft, int c) {
         this.ft = ft;
         this.c = c;
      }

      public int floorType() {
         return ft;
      }

      public int charge() {
         return c;
      }
   }

   /*
    * Enumerator for direction. the enums include an index into the feature
    * array as well as X and Y offsets for those directions
    */
   public enum direction {
      NORTH(0, 0, 1),
      EAST(1, 1, 0),
      SOUTH(2, 0, -1),
      WEST(3, -1, 0);
      private int index;
      private int xOffset;
      private int yOffset;

      private direction(int l, int x, int y) {
         this.index = l;
         this.xOffset = x;
         this.yOffset = y;
      }

      public int index() {
         return index;
      }

      public int xOffset() {
         return xOffset;
      }

      public int yOffset() {
         return yOffset;
      }
   }
   /*
    * Varibles represent each sensor present on the Clean Sweep Robot
    */
   public floorType floor;
   public feature[] features;
   public boolean dirtPresent;
   public boolean atChargingStation;
   /*
    * Constructor instantiates the feature array
    */
   public static final int NUMBEROFDIRECTIONS = 4;

   public SensorInterface() {
      features = new feature[NUMBEROFDIRECTIONS];
   }
   /*
    * Variables do not belong here since they do not represent sensors but 
    * do represent data that must be passed from the .xml stored in the 
    * VirtualHouse that are required by the CleanSweepRobot
    */
   public int startingXCoord;
   public int startingYCoord;
}
