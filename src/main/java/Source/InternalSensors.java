package Source;

import Source.SensorInterface.floorType;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 * 
 * The purpose if this class is to track and store battery charge information
 * and dust bin capacity.
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharp, Doug Oda
 * @version     I2
 * @date        25Sep2014
 */
public class InternalSensors {

   private int dustBinCapacity;
   private int batteryCharge;
   private floorType currentFloorType;

   /**
    * Constructor for InternalSensors
    * <p>
    * Constructor sets the dust bin capacity and the battery life to 50 per
    * the project requirements. I also saves the parameter as the current floor
    * type. Note that the battery is 10x
    * 
    * @param SensorInterface.floorType initialFloorType
    */
   public InternalSensors(floorType initialFloorType) {
      dustBinCapacity = 50;
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
    * Update battery charge and dust bin capacity based on the last floor
    * type sent to this object
    */
   public void swept() {
      if (batteryCharge > currentFloorType.charge()) {
         batteryCharge -= currentFloorType.charge();
      } else {
         batteryCharge = 0;
      }
      if (dustBinCapacity > 0) {
         dustBinCapacity--;
      }
   }

   /**
    * Update battery charge and dust bin capacity base on parameter
    * 
    * @param SensorInterface.floorType floorType - type of floor being swept
    */
   public void swept(floorType floorType) {
      if (batteryCharge > floorType.charge()) {
         batteryCharge -= floorType.charge();
      } else {
         batteryCharge = 0;
      }

      if (dustBinCapacity > 0) {
         dustBinCapacity--;
      }
   }

   /**
    * Set dust bin capacity to 50  
    */
   public void emptyDustBin() {
      dustBinCapacity = 50;
   }

   /**
    * Set battery charge back to full which for this project if 50
    */
   public void chargeBattery() {
      batteryCharge = 500;
   }

   /**
    * Returns remaining space in dust bin                        
    *
    * @return integer - remaining capacity
    */
   public int dustBinCapacity() {
      return dustBinCapacity;
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
