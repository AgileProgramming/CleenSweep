package Source;

import Source.SensorInterface.direction;
import Source.SensorInterface.feature;
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
 * JUnit test for Control System
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharp, Doug Oda
 * @version     I1
 * @date        17Sep2014
 */
public class CleanSweepRobotTest {
   
   public CleanSweepRobotTest() {
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
    * Test of cleanSweepUpdate method, of class CleanSweepRobot.
    * 
    * NOTE that this is making the assumption that the preferences of movement are 
    * FIRST to the west, if avaliable and not already visited
    * SECOND to the south, if avaliable and not already visited
    * THIRD to the east, if avaliable and not already visited
    * FOURTH to the north, if avaliable and not already visited
    */
   @Test
   public void testCleanSweepUpdate() {
      System.out.println("Test cleanSweepUpdate()");
      VirtualHouse vh = new VirtualHouse(true);
      CleanSweepRobot csr = new CleanSweepRobot(vh);
      SensorInterface si = new SensorInterface();
      direction n = direction.NORTH;
      direction e = direction.EAST;
      direction s = direction.SOUTH;
      direction w = direction.WEST;     
      /*verify that instantation of the CleanSweepRobot got it's starting location*/
      vh.GetInitialLocation(si);
      assertEquals(si.StartingXCoord, Integer.MAX_VALUE);
      assertEquals(si.StartingYCoord, Integer.MAX_VALUE);
      /*first move return true*/
      System.out.println("--verify move 1");
      assertEquals(csr.cleanSweepUpdate(), true);
      /*have no access to robot variables but we can check Virtual house 
       * see where it has moved to*/
      vh.SensorInformation(si);
      assertEquals(si.atChargingStation, false);
      assertEquals(si.dirtPresent, true);
      assertEquals(si.floor, floorType.LowPileCarpet);
      assertEquals(si.features[n.index()], feature.OPEN);
      assertEquals(si.features[e.index()], feature.OBSTICLE);
      assertEquals(si.features[s.index()], feature.OBSTICLE);
      assertEquals(si.features[w.index()], feature.OPEN);
      /*second move return true*/
      System.out.println("--verify move 2");
      assertEquals(csr.cleanSweepUpdate(), true);
      vh.SensorInformation(si);
      assertEquals(si.atChargingStation, false);
      assertEquals(si.dirtPresent, false);
      assertEquals(si.floor, floorType.HighPileCarpet);
      assertEquals(si.features[n.index()], feature.OBSTICLE);
      assertEquals(si.features[e.index()], feature.OBSTICLE);
      assertEquals(si.features[s.index()], feature.OPEN);
      assertEquals(si.features[w.index()], feature.OPEN);   
       /*third and final move*/
      System.out.println("--verify move 3");
      assertEquals(csr.cleanSweepUpdate(), true);
      vh.SensorInformation(si);
      assertEquals(si.atChargingStation, false);
      assertEquals(si.dirtPresent, true);
      assertEquals(si.floor, floorType.BareFloor);
      assertEquals(si.features[n.index()], feature.OBSTICLE);
      assertEquals(si.features[e.index()], feature.OPEN);
      assertEquals(si.features[s.index()], feature.OPEN);
      assertEquals(si.features[w.index()], feature.OBSTICLE);  
      /*cannot move so all information should be the same*/
       System.out.println("--verify attempted move 4");
      assertEquals(csr.cleanSweepUpdate(), false);
      vh.SensorInformation(si);
      assertEquals(si.atChargingStation, false);
      assertEquals(si.dirtPresent, true);
      assertEquals(si.floor, floorType.BareFloor);
      assertEquals(si.features[n.index()], feature.OBSTICLE);
      assertEquals(si.features[e.index()], feature.OPEN);
      assertEquals(si.features[s.index()], feature.OPEN);
      assertEquals(si.features[w.index()], feature.OBSTICLE);       
   }
}
