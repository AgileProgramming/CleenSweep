package Source;

import Source.SensorInterface.floorType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 *
 * JUnit test for InternalSensors which tracks battery life and dust bing capacity
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharp, Doug Oda
 * @version     I1
 * @date        13Sep2014
 */
public class InternalSensorsTest {

   public InternalSensorsTest() {
   }

   @BeforeClass
   public static void setUpClass() throws Exception {
   }

   @AfterClass
   public static void tearDownClass() throws Exception {
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   /**
    * Test of moved method, of class InternalSensors.
    */
   @Test
   public void testMoved() {
      System.out.println("Test moved()");
      floorType newFloorType = floorType.BareFloor;
      InternalSensors instance = new InternalSensors(newFloorType);
      System.out.println("--verify battery reduction for bare to bare");
      instance.moved(newFloorType); //500 - 10 = 490
      assertEquals(instance.charge(), 490);
      System.out.println("--verify battery reduction for bare to low");
      newFloorType = floorType.LowPileCarpet;
      instance.moved(newFloorType); //490 - 15 = 475
      assertEquals(instance.charge(), 475);
      System.out.println("--verify battery reduction for low to low");
      instance.moved(newFloorType); //475 - 20 = 455
      assertEquals(instance.charge(), 455);
      System.out.println("--verify battery reduction for bare to high");
      newFloorType = floorType.BareFloor;
      instance.moved(newFloorType); //455 - 15 = 440
      newFloorType = floorType.HighPileCarpet;
      instance.moved(newFloorType); //440 - 20 = 420
      assertEquals(instance.charge(), 420);
      System.out.println("--verify battery reduction for high to high");
      instance.moved(newFloorType); //420 - 30 = 390
      assertEquals(instance.charge(), 390);
      System.out.println("--verify battery reduction for low to high");
      newFloorType = floorType.LowPileCarpet;
      instance.moved(newFloorType); //390 - 25 = 375
      assertEquals(instance.charge(), 365);
   }

   /**
    * Test of swept method, of class InternalSensors.
    */
   @Test
   public void testSwept_0args() {
      System.out.println("Test swept()");
      System.out.println("--verify battery reduction 3 floor types");
      floorType newFloorType = floorType.BareFloor;
      InternalSensors instancea = new InternalSensors(newFloorType);
      instancea.swept();
      assertEquals(instancea.charge(), 490);
      newFloorType = floorType.LowPileCarpet;
      InternalSensors instanceb = new InternalSensors(newFloorType);
      instanceb.swept();
      assertEquals(instanceb.charge(), 480);
      newFloorType = floorType.HighPileCarpet;
      InternalSensors instancec = new InternalSensors(newFloorType);
      instancec.swept();
      assertEquals(instancec.charge(), 470);
      System.out.println("--verify dust bin capacity is reduced");
      assertEquals(instancec.dustBinCapacity(), 49);
   }

   /**
    * Test of swept method, of class InternalSensors.
    */
   @Test
   public void testSwept_SensorInterfacefloorType() {
      System.out.println("Test swept(floortype)");
      System.out.println("--verify battery reduction 3 floor types");
      floorType newFloorType = floorType.HighPileCarpet;
      InternalSensors instance = new InternalSensors(newFloorType);
      newFloorType = floorType.BareFloor;
      instance.swept(newFloorType);
      assertEquals(instance.charge(), 490);
      newFloorType = floorType.LowPileCarpet;
      instance.swept(newFloorType);
      assertEquals(instance.charge(), 470);
      newFloorType = floorType.HighPileCarpet;
      instance.swept(newFloorType);
      assertEquals(instance.charge(), 440);
      System.out.println("--verify dust bin capacity is reduced");
      assertEquals(instance.dustBinCapacity(), 47);
   }

   /**
    * Test of emptyDustBin method, of class InternalSensors.
    */
   @Test
   public void testEmptyDustBin() {
      System.out.println("Test emptyDustBin()");
      floorType newFloorType = floorType.HighPileCarpet;
      InternalSensors instance = new InternalSensors(newFloorType);
      instance.swept();
      instance.swept();
      instance.swept();
      instance.swept();
      assertEquals(instance.dustBinCapacity(), 46);
      instance.emptyDustBin();
      assertEquals(instance.dustBinCapacity(), 50);
   }

   /**
    * Test of chargeBattery method, of class InternalSensors.
    */
   @Test
   public void testChargeBattery() {
      System.out.println("Test chargeBattery()");
      floorType newFloorType = floorType.HighPileCarpet;
      InternalSensors instance = new InternalSensors(newFloorType);
      instance.swept();
      instance.swept();
      instance.swept();
      instance.swept();
      assertEquals(instance.charge(), 380);
      instance.chargeBattery();
      assertEquals(instance.charge(), 500);
   }

   /**
    * Test of dustBinCapacity method, of class InternalSensors.
    */
   @Test
   public void testDustBinCapacity() {
      System.out.println("Test dustBinCapacity()");
      floorType newFloorType = floorType.HighPileCarpet;
      InternalSensors instance = new InternalSensors(newFloorType);
      instance.swept();
      instance.swept();
      instance.swept();
      instance.swept();
      instance.swept();
      assertEquals(instance.dustBinCapacity(), 45);
      for (int i = 0; i < 50; i++) {
         instance.swept();
      }
      assertEquals(instance.dustBinCapacity(), 0);
   }

   /**
    * Test of Charge method, of class InternalSensors.
    */
   @Test
   public void testCharge() {
      System.out.println("Test charge()");
      floorType newFloorType = floorType.HighPileCarpet;
      InternalSensors instance = new InternalSensors(newFloorType);
      instance.swept();
      instance.swept();
      instance.swept();
      instance.swept();
      assertEquals(instance.charge(), 380);
      for (int i = 0; i < 50; i++) {
         instance.swept();
      }
      assertEquals(instance.charge(), 0);
   }
}
