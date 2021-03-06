package source.sensorsimulator;

import source.sensorsimulator.SensorInterface.feature;
import source.sensorsimulator.SensorInterface.floorType;
import source.sensorsimulator.SensorInterface.direction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 *
 * JUnit test for VirtualHouse which is the Sensor Simulator for the project
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharpe, Doug Oda
 * @version     I1
 * @date        13Sep2014
 */

public class VirtualHouseTest {

   public VirtualHouseTest() {
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
    * Test of Remove method, of class VirtualHouse.
    */

   @Test
   public void testRemove() {
      System.out.println("Test Remove()\n--Graphics and therefore not tested");
   }

   /**
    * Test of Vacuum method, of class VirtualHouse.
    */

   @Test
   public void testVacuum() {
      System.out.println("Test Vacuum()");
      SensorInterface tempSI = new SensorInterface();
      VirtualHouse instance = new VirtualHouse(true);
      System.out.println("--Call 3 times to remove dirt from 0,0");
      instance.vacuum();
      instance.sensorInformation(tempSI);
      assertEquals(tempSI.dirtPresent, true);
      instance.vacuum(); //remove 1 unit of dirt from 2
      instance.sensorInformation(tempSI);
      assertEquals(tempSI.dirtPresent, true);
      instance.vacuum(); //remove 1 unit of dirt from 1
      instance.sensorInformation(tempSI);
      assertEquals(tempSI.dirtPresent, false);
      instance.vacuum(); //remove 1 unit of dirt from 0
      instance.sensorInformation(tempSI);
      assertEquals(tempSI.dirtPresent, false);
   }

   /**
    * Test of Move method, of class VirtualHouse.
    */

   @Test
   public void testMove() {
      System.out.println("Test Move()");
      int newX = 0;
      int newY = 0;
      VirtualHouse instance = new VirtualHouse(true);
      boolean result;
      System.out.println("--Test diagonal moves");
      newX = 1;
      newY = 1;
      result = instance.move(newX, newY);
      assertEquals(result, false);
      newX = 0;
      newY = 1;
      result = instance.move(newX, newY);
      assertEquals(result, true);
      newX = 1;
      newY = 0;
      result = instance.move(newX, newY);
      assertEquals(result, false);
      newX = 0;
      newY = 0;
      result = instance.move(newX, newY);
      newX = 1;
      newY = 0;
      result = instance.move(newX, newY);     
      newX = 0;
      newY = 1;
      result = instance.move(newX, newY);
      assertEquals(result, false);
      newX = 1;
      newY = 1;
      result = instance.move(newX, newY);
      assertEquals(result, true);
      newX = 0;
      newY = 0;
      result = instance.move(newX, newY);
      assertEquals(result, false);
      System.out.println("--Test moving to non-OPEN locations");
      newX = 2;
      newY = 1;
      result = instance.move(newX, newY);
      assertEquals(result, false);
      System.out.println("--Test for teleportation (totally non-adjacent cells)");
      newX = 0;
      newY = 3;
      result = instance.move(newX, newY);
      assertEquals(result, false);

   }

   /**
    * Test of GetInitialLocation method, of class VirtualHouse.
    */

   @Test
   public void testGetInitialLocation() {
      System.out.println("Test GetInitialLocation()");
      SensorInterface si = new SensorInterface();
      VirtualHouse instance = new VirtualHouse(true);
      instance.getInitialLocation(si);
      /*check if initial location is at 0,0 where charge station is located*/

      System.out.println("--Give it the first time");
      assertEquals(si.startingXCoord, 0);
      assertEquals(si.startingYCoord, 0);
      instance.getInitialLocation(si);
      /*verify that the second time a radically bad value is passed*/

      System.out.println("--Give garbage second time");
      instance.getInitialLocation(si);
      assertEquals(si.startingXCoord, Integer.MAX_VALUE);
      assertEquals(si.startingYCoord, Integer.MAX_VALUE);
   }

   /**
    * Test of SensorInformation method, of class VirtualHouse.
    */

   @Test
   public void testSensorInformation() {
      System.out.println("Test SensorInformation()");
      SensorInterface tempSI = new SensorInterface();
      VirtualHouse instance = new VirtualHouse(true);
      direction n = direction.NORTH;
      direction e = direction.EAST;
      direction s = direction.SOUTH;
      direction w = direction.WEST;

      /*move around the very small test house and verify sensor information*/

      System.out.println("--Test starting cell of 0,0");
      instance.sensorInformation(tempSI);
      assertEquals(tempSI.atChargingStation, true);
      assertEquals(tempSI.dirtPresent, true);
      assertEquals(tempSI.floor, floorType.BARE_FLOOR);
      assertEquals(tempSI.features[n.index()], feature.OPEN);
      assertEquals(tempSI.features[e.index()], feature.OPEN);
      assertEquals(tempSI.features[w.index()], feature.OBSTICLE);
      assertEquals(tempSI.features[w.index()], feature.OBSTICLE);
      System.out.println("--Test starting cell of 0,1");
      instance.move(0, 1);
      instance.sensorInformation(tempSI);
      assertEquals(tempSI.atChargingStation, false);            
      assertEquals(tempSI.dirtPresent, true);
      assertEquals(tempSI.floor, floorType.HIGH_PILE_CARPET);
      assertEquals(tempSI.features[n.index()], feature.OPEN);
      assertEquals(tempSI.features[e.index()], feature.OBSTICLE);
      assertEquals(tempSI.features[s.index()], feature.OPEN);
      assertEquals(tempSI.features[w.index()], feature.OBSTICLE);
      System.out.println("--Test starting cell of 1,1");
      instance.move(0, 2);
      instance.sensorInformation(tempSI);
      assertEquals(tempSI.atChargingStation, false);
      assertEquals(tempSI.dirtPresent, true);
      assertEquals(tempSI.floor, floorType.LOW_PILE_CARPET);
      assertEquals(tempSI.features[n.index()], feature.STAIRS);
      assertEquals(tempSI.features[e.index()], feature.OBSTICLE);
      assertEquals(tempSI.features[s.index()], feature.OPEN);
      assertEquals(tempSI.features[w.index()], feature.OBSTICLE);
   }
}

