package source.controlsystem;

import source.sensorsimulator.SensorInterface.floorType;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 * 
 * The purpose if this class is to track and store battery charge information
 * and dirt bin capacity.
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharp, Doug Oda
 * @version     I2
 * @date        25Sep2014
 */
public class BatteryAndDirtBin {

   private int dirtBinCapacity;
   private int batteryCharge;
   private floorType currentFloorType;

   /**
    * Constructor for BatteryAndDirtBin
    * <p>
    * Constructor sets the dirt bin capacity and the battery life to 50 per
    * the project requirements. I also saves the parameter as the current floor
    * type. Note that the battery is 10x
    * 
    * @param SensorInterface.floorType initialFloorType
    */
   public BatteryAndDirtBin(floorType initialFloorType) {
      dirtBinCapacity = 50;
      batteryCharge = 500;
      currentFloorType = initialFloorType;
   }

   /**
    * Update battery charge for movement
    * <p>
    * Method calculates battery charge used by averaging the charge between 
    * the last floor surface and the parameter passed. It then updates the 
    * reaming battery life based on the result. The Method also saves the 
    * parameter as the current floor type.
    * 
    * @param SensorInterface.floorType newFloorType
    */
   public void moved(floorType newFloorType) {
      int drained = ((currentFloorType.charge() + newFloorType.charge()) / 2);
      if (batteryCharge > drained) {
         batteryCharge -= drained;
      } else {
         batteryCharge = 0;
      }

      currentFloorType = newFloorType;
   }

   /**
    * Update battery charge and dirt bin capacity based on the last floor
    * type sent to this object
    */
   public void swept() {
      if (batteryCharge > currentFloorType.charge()) {
         batteryCharge -= currentFloorType.charge();
      } else {
         batteryCharge = 0;
      }
      if (dirtBinCapacity > 0) {
         dirtBinCapacity--;
      }
   }

   /**
    * Update battery charge and dirt bin capacity base on parameter
    * 
    * @param SensorInterface.floorType floorType - type of floor being swept
    */
   public void swept(floorType floorType) {
      if (batteryCharge > floorType.charge()) {
         batteryCharge -= floorType.charge();
      } else {
         batteryCharge = 0;
      }

      if (dirtBinCapacity > 0) {
         dirtBinCapacity--;
      }
   }

   /**
    * Set dirt bin capacity to 50  
    */
   public void emptyDirtBin() {
      dirtBinCapacity = 50;
   }

   /**
    * Set battery charge back to full which for this project if 50
    */
   public void chargeBattery() {
      batteryCharge = 500;
   }

   /**
    * Returns remaining space in dirt bin                        
    *
    * @return integer - remaining capacity
    */
   public int dirtBinCapacity() {
      return dirtBinCapacity;
   }

   /**
    * Returns remaining battery life                          
    *
    * @return integer - number of charges left in tenth of a charge
    */
   public int charge() {
      return batteryCharge;
   }
}
