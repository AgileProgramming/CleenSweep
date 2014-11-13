package source.controlsystem;

import java.io.FileReader;
import java.io.File;
import java.io.BufferedReader;
import source.sensorsimulator.SensorInterface.direction;
import source.sensorsimulator.SensorInterface.feature;
import source.sensorsimulator.SensorInterface.floorType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import source.sensorsimulator.SensorInterface;
import source.sensorsimulator.VirtualHouse;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 *
 * JUnit test for Control System
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharpe, Doug Oda
 * @version     I2
 * @date        25Sep2014
 */

public class CleanSweepRobotTest {

   private final static String[] floorDump = {
   "<FloorPlanDump>",
   "<cell xs='0' ys='0' ss='1' ps='1212' ds='0' cs='1' />",
   "<cell xs='1' ys='0' ss='1' ps='2112' ds='0' cs='0' />",
   "<cell xs='1' ys='1' ss='1' ps='2221' ds='0' cs='0' />",
   "<cell xs='0' ys='1' ss='4' ps='2211' ds='0' cs='0' />",
   "<cell xs='0' ys='2' ss='2' ps='2241' ds='0' cs='0' />",
   "</ FloorPlanDump>"
   };
   
   private final static String[] log = {
"Action, Loc y/x, Chrg Station, Floor Type, Dirt Present, North, East, South, West, Battery, Dirt Capacity",
"CHECK_SENSOR, 0/0, Yes, BareFloor, Yes,OPEN,OPEN,OBSTICLE,OBSTICLE,50.0,50",
"SWEEP, 0/0, Yes, BareFloor, Yes,OPEN,OPEN,OBSTICLE,OBSTICLE,49.0,49",
"CHECK_SENSOR, 0/0, Yes, BareFloor, Yes,OPEN,OPEN,OBSTICLE,OBSTICLE,49.0,49",
"SWEEP, 0/0, Yes, BareFloor, Yes,OPEN,OPEN,OBSTICLE,OBSTICLE,48.0,48",
"CHECK_SENSOR, 0/0, Yes, BareFloor, Yes,OPEN,OPEN,OBSTICLE,OBSTICLE,48.0,48",
"SWEEP, 0/0, Yes, BareFloor, Yes,OPEN,OPEN,OBSTICLE,OBSTICLE,47.0,47",
"CHECK_SENSOR, 0/0, Yes, BareFloor, No,OPEN,OPEN,OBSTICLE,OBSTICLE,47.0,47",
"MOVE, 0/1, No, BareFloor, Yes,OPEN,OBSTICLE,OBSTICLE,OPEN,46.0,47",
"CHECK_SENSOR, 0/1, No, BareFloor, Yes,OPEN,OBSTICLE,OBSTICLE,OPEN,46.0,47",
"SWEEP, 0/1, No, BareFloor, Yes,OPEN,OBSTICLE,OBSTICLE,OPEN,45.0,46",
"CHECK_SENSOR, 0/1, No, BareFloor, No,OPEN,OBSTICLE,OBSTICLE,OPEN,45.0,46",
"MOVE, 1/1, No, BareFloor, Yes,OBSTICLE,OBSTICLE,OPEN,OBSTICLE,44.0,46",
"CHECK_SENSOR, 1/1, No, BareFloor, Yes,OBSTICLE,OBSTICLE,OPEN,OBSTICLE,44.0,46",
"SWEEP, 1/1, No, BareFloor, Yes,OBSTICLE,OBSTICLE,OPEN,OBSTICLE,43.0,45",
"CHECK_SENSOR, 1/1, No, BareFloor, Yes,OBSTICLE,OBSTICLE,OPEN,OBSTICLE,43.0,45",
"SWEEP, 1/1, No, BareFloor, Yes,OBSTICLE,OBSTICLE,OPEN,OBSTICLE,42.0,44"};

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
      CleanSweepRobot robot = new CleanSweepRobot(vh);
      SensorInterface si = new SensorInterface();
      direction n = direction.NORTH;
      direction e = direction.EAST;
      direction s = direction.SOUTH;
      direction w = direction.WEST;
      /*verify that instantation of the CleanSweepRobot got it's starting location*/

      vh.getInitialLocation(si);
      assertEquals(si.startingXCoord, Integer.MAX_VALUE);
      assertEquals(si.startingYCoord, Integer.MAX_VALUE);
      /*first move return true*/

      CleanSweepRobot csr = new CleanSweepRobot(vh,0,0);
      System.out.println("--verify move 1");
      assertEquals(csr.cleanSweepUpdate(), true);
      /*have no access to robot variables but we can check Virtual house 
       * see where it has moved to*/

      vh.sensorInformation(si);
      assertEquals(si.atChargingStation, false);
      assertEquals(si.dirtPresent, true);
      assertEquals(si.floor, floorType.BareFloor);
      assertEquals(si.features[n.index()], feature.OPEN);
      assertEquals(si.features[e.index()], feature.OBSTICLE);
      assertEquals(si.features[s.index()], feature.OBSTICLE);
      assertEquals(si.features[w.index()], feature.OPEN);
      System.out.println("--verify move 2");
      assertEquals(csr.cleanSweepUpdate(), true);
      vh.sensorInformation(si);
      assertEquals(si.atChargingStation, false);
      assertEquals(si.dirtPresent, true);
      assertEquals(si.floor, floorType.BareFloor);
      assertEquals(si.features[n.index()], feature.OBSTICLE);
      assertEquals(si.features[e.index()], feature.OBSTICLE);
      assertEquals(si.features[s.index()], feature.OPEN);
      assertEquals(si.features[w.index()], feature.OBSTICLE);
      System.out.println("--verify move battery low return to charging station");
      assertEquals(csr.cleanSweepUpdate(), true);
      assertEquals(csr.cleanSweepUpdate(), true);
      vh.sensorInformation(si);
      assertEquals(si.atChargingStation, true);
      assertEquals(csr.cleanSweepUpdate(), true);
      assertEquals(csr.cleanSweepUpdate(), true);
      System.out.println("--and back to location where it ran out of battery");
      vh.sensorInformation(si);
      assertEquals(si.atChargingStation, false);
      assertEquals(si.dirtPresent, true);
      assertEquals(si.floor, floorType.BareFloor);
      assertEquals(si.features[n.index()], feature.OBSTICLE);
      assertEquals(si.features[e.index()], feature.OBSTICLE);
      assertEquals(si.features[s.index()], feature.OPEN);
      assertEquals(si.features[w.index()], feature.OBSTICLE);     
      assertEquals(csr.cleanSweepUpdate(), true);
      assertEquals(csr.cleanSweepUpdate(), true);
      assertEquals(csr.cleanSweepUpdate(), true);
      vh.sensorInformation(si);
      assertEquals(si.atChargingStation, false);
      assertEquals(si.dirtPresent, true);
      assertEquals(si.floor, floorType.HighPileCarpet);
      assertEquals(si.features[n.index()], feature.OPEN);
      assertEquals(si.features[e.index()], feature.OBSTICLE);
      assertEquals(si.features[s.index()], feature.OPEN);
      assertEquals(si.features[w.index()], feature.OBSTICLE);
      assertEquals(csr.cleanSweepUpdate(), true);
      vh.sensorInformation(si);
      assertEquals(si.atChargingStation, false);
      assertEquals(si.dirtPresent, true); 
      assertEquals(si.floor, floorType.LowPileCarpet);
      assertEquals(si.features[n.index()], feature.STAIRS);
      assertEquals(si.features[e.index()], feature.OBSTICLE);
      assertEquals(si.features[s.index()], feature.OPEN);
      assertEquals(si.features[w.index()], feature.OBSTICLE);
      assertEquals(csr.cleanSweepUpdate(), true);
      assertEquals(csr.cleanSweepUpdate(), true);
      vh.sensorInformation(si);
      assertEquals(si.atChargingStation, true);
      assertEquals(si.dirtPresent, false);
      assertEquals(si.floor, floorType.BareFloor);
      assertEquals(si.features[n.index()], feature.OPEN);
      assertEquals(si.features[e.index()], feature.OPEN);
      assertEquals(si.features[s.index()], feature.OBSTICLE);
      assertEquals(si.features[w.index()], feature.OBSTICLE);  
      assertEquals(csr.cleanSweepUpdate(), false);
      /*two files were created, check them*/

      System.out.println("--verify FloorPlanDump.xml was correctly created");
      String line = null;
      BufferedReader br = null;
      File f = new File("FloorPlanDump.xml");
      try {
         br = new BufferedReader(new FileReader(f));
      } catch (Exception t) {
         fail("FloorPlanDump.xml was not created");
      }
      for (int i = 0; i < floorDump.length; i++) {
         try {
            line = br.readLine();
         } catch (Exception t) {
            fail("FloorPlanDump.xml cannot be read (probably not closed)");
         }
         assertEquals(line, floorDump[i]);
      }
      try {
         br.close();
      } catch (Exception t) {
         fail("FloorPlanDump.xml was not created");
      }
       System.out.println("--verify ActivityLog.csv was correctly created");
      f = new File("ActivityLog.csv");
      try {
         br = new BufferedReader(new FileReader(f));
      } catch (Exception t) {
         fail("ActivityLog.txt was not created");
      }
      /*just check the first 16 lines of the log file*/
      for (int i = 0; i < 16; i++) {
         try {
            line = br.readLine();
         } catch (Exception t) {
            fail("FloorPlanDump.xml cannot be read (probably not closed)");
         }
         assertEquals(line, log[i]);
      }
      try {
         br.close();
      } catch (Exception t) {
         fail("ActivityLog.txt was not created");
      }     
   }
}
