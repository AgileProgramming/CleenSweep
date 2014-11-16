package source.controlsystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import source.controlsystem.CleanSweepRobot.CellDescription;
import source.sensorsimulator.SensorInterface;
import source.sensorsimulator.SensorInterface.direction;
import source.sensorsimulator.SensorInterface.floorType;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 *
 * JUnit test for BatteryAndDirtBin which tracks battery life and dirt capacity
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharpe, Doug Oda
 * @version     I3
 * @date        08Nov2014
 */
public class AStarPathFinderTest {
    
   private LinkedList<CleanSweepRobot.CellDescription> map; 
   
   public AStarPathFinderTest() {
      map =  new LinkedList<>();
      LoadTestMap();
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
    * Test of calculateCharge method, of class AStarPathFinder.
    */
   @Test
   public void testCalculateCharge() {
      System.out.println("Claculate movement charge for various locations on test floor plan");
      AStarPathFinder instance = new AStarPathFinder();
      System.out.println("-Lower left to upper right");
      int result = instance.calculateCharge(0, 0, 3, 4, map);
      assertEquals(170, result);
      System.out.println("-Lower left to around corner");
      result = instance.calculateCharge(0, 0, 2, 3, map);
      assertEquals(165, result);
      System.out.println("-upper right to lower right");
      result = instance.calculateCharge(3, 4, 3, 0, map);
      assertEquals(80, result);
   }

   /**
    * Test of shortestPath method, of class AStarPathFinder.
    */
   @Test
   public void testShortestPath() {
      System.out.println("Find Shortest Path");
      AStarPathFinder instance = new AStarPathFinder();
      LinkedList expResult = null;
      System.out.println("-Lower left to upper right");
      LinkedList result = instance.shortestPath(0, 0, 3, 4, map, false);
      assertEquals(8, result.size());
      System.out.println("-Lower left to around corner");
      result = instance.shortestPath(0, 0, 2, 3, map, false);
      assertEquals(8, result.size());
      System.out.println("-upper right to lower right");
      result = instance.shortestPath(3, 4, 3, 0, map, false);
      assertEquals(5, result.size());     
   }
   
   private void LoadTestMap( )
   {
      String line = null;
      BufferedReader br = null;
      File f = new File("JUnitTestAStarMap.xml");
      try
      {
         br = new BufferedReader(new FileReader(f));
      }
      catch (Exception e)
      {
         System.out.println( "File not Found");
      }
      for (;;)
      {
         try
         {
            line = br.readLine();
         }
         catch (Exception e)
         {
            System.out.println("Cannot Read File");
         }
         if (line == null)
         {
            break;
         }
         int A;
         int B;
         if (line.contains("cell"))
         {
            /*make new cell*/
            CleanSweepRobot.CellDescription CD = new CleanSweepRobot.CellDescription();

            /*get x*/
            A = line.indexOf("xs") + 4;
            B = line.indexOf("'", A);
            try
            {
               CD.locX = Integer.parseInt(line.substring(A, B));
            }
            catch (Exception e)
            {
               System.out.println("Bad input file format");
            }
            /*get Y*/
            A = line.indexOf("ys") + 4;
            B = line.indexOf("'", A);
            try
            {
               CD.locY = Integer.parseInt(line.substring(A, B));
            }
            catch (Exception e)
            {
               System.out.println( "Bad input file format");
            }
            /*get surface*/
            A = line.indexOf("ss") + 4;
            B = line.indexOf("'", A);
            switch (line.substring(A, B))
            {
               case "4":
                  CD.sI.floor = floorType.HIGH_PILE_CARPET;
                  break;
               case "2":
                  CD.sI.floor = floorType.LOW_PILE_CARPET;
                  break;
               default:
                  CD.sI.floor = floorType.BARE_FLOOR;
                  break;
            }
            /*get amount of dirt of floor*/
            A = line.indexOf("ds") + 4;
            B = line.indexOf("'", A);
            int dirt = 0;
            try
            {
               dirt = Integer.parseInt(line.substring(A, B));
            }
            catch (Exception e)
            {
               System.out.println("Bad input file format");
            }
            if (dirt > 0)
            {
               CD.sI.dirtPresent = true;
            }
            else
            {
               CD.sI.dirtPresent = false;
            }
            /*get wall sensors*/
            direction n = direction.NORTH;
            direction e = direction.EAST;
            direction s = direction.SOUTH;
            direction w = direction.WEST;
            A = line.indexOf("ps") + 4;
            String a = line.substring(A, A + 1);
            switch (line.substring(A, A + 1))
            {
               case "1":
                  CD.sI.features[e.index()] = SensorInterface.feature.OPEN;
                  break;
               case "2":
                  CD.sI.features[e.index()] = SensorInterface.feature.OBSTICLE;
                  break;
               default:
                  CD.sI.features[e.index()] = SensorInterface.feature.STAIRS;
                  break;
            }
            A++;
            switch (line.substring(A, A + 1))
            {
               case "1":
                  CD.sI.features[w.index()] = SensorInterface.feature.OPEN;
                  break;
               case "2":
                  CD.sI.features[w.index()] = SensorInterface.feature.OBSTICLE;
                  break;
               default:
                  CD.sI.features[w.index()] = SensorInterface.feature.STAIRS;
                  break;
            }
            A++;
            switch (line.substring(A, A + 1))
            {
               case "1":
                  CD.sI.features[n.index()] = SensorInterface.feature.OPEN;
                  break;
               case "2":
                  CD.sI.features[n.index()] = SensorInterface.feature.OBSTICLE;
                  break;
               default:
                  CD.sI.features[n.index()] = SensorInterface.feature.STAIRS;
                  break;
            }
            A++;
            switch (line.substring(A, A + 1))
            {
               case "1":
                  CD.sI.features[s.index()] = SensorInterface.feature.OPEN;
                  break;
               case "2":
                  CD.sI.features[s.index()] = SensorInterface.feature.OBSTICLE;
                  break;
               default:
                  CD.sI.features[s.index()] = SensorInterface.feature.STAIRS;
                  break;
            }
            A++;
            /*check if it is charging station*/
            A = line.indexOf("cs") + 4;
            B = line.indexOf("'", A);
            if ("1".equals(line.substring(A, B)))
            {
               CD.sI.atChargingStation = true;
               CD.sI.startingXCoord = CD.locX;
               CD.sI.startingYCoord = CD.locY;
            }
            else
            {
               CD.sI.atChargingStation = false;
            }

            /*save it*/
            map.add(CD);
         }
      }
      try
      {
         br.close();
      }
      catch (Exception e)
      {
         System.out.println("Cannot close file");
      }
   }
}
