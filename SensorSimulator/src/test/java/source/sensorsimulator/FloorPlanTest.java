package source.sensorsimulator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 *
 * JUnit test for FloorPlan
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharpe, Doug Oda
 * @version     I3
 * @date        15Nov2014
 */
public class FloorPlanTest {
   
   public FloorPlanTest() {
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
    * Test of floorPlan method, of class FloorPlan.
    */
   @Test
   public void testFloorPlan() {
      System.out.println("floorPlan");
       FloorPlan instance = new FloorPlan("JUnitTestFloorPlan.xml");
       CellDescription result = instance.getCell(0, 1);
       assertEquals(SensorInterface.floorType.HIGH_PILE_CARPET, result.sI().floor);
   }

   /**
    * Test of getCell method, of class FloorPlan.
    */
   @Test
   public void testGetCell() {
      System.out.println("getCell");
      FloorPlan instance = new FloorPlan("JUnitTestFloorPlan.xml");
      CellDescription result = instance.getCell(1, 1);
      assertEquals(43, result.dirt());
      assertEquals(1, result.locX());
      assertEquals(1, result.locY());
      assertEquals(true, result.sI().dirtPresent);
      assertEquals(SensorInterface.floorType.BARE_FLOOR, result.sI().floor);
      assertEquals(false, result.sI().atChargingStation);
      assertEquals(SensorInterface.feature.OBSTICLE, result.sI().features[0]);
      assertEquals(SensorInterface.feature.OBSTICLE, result.sI().features[1]);
      assertEquals(SensorInterface.feature.OPEN, result.sI().features[2]);
      assertEquals(SensorInterface.feature.OBSTICLE, result.sI().features[3]);
   }

   /**
    * Test of getInitialX method, of class FloorPlan.
    */
   @Test
   public void testGetInitialX() {
      System.out.println("getInitialX");
      FloorPlan instance = new FloorPlan("JUnitTestFloorPlan.xml");
      int result = instance.getInitialX();
      assertEquals(0, result);
   }

   /**
    * Test of getInitialY method, of class FloorPlan.
    */
   @Test
   public void testGetInitialY() {
      System.out.println("getInitialY");
      FloorPlan instance = new FloorPlan("JUnitTestFloorPlan.xml");
      int result = instance.getInitialY();
      assertEquals(0, result);
   }
}
