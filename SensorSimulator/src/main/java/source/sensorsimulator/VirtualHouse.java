package source.sensorsimulator;

import source.sensorsimulator.SensorInterface.direction;
import source.sensorsimulator.SensorInterface.feature;
import source.sensorsimulator.SensorInterface.floorType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 *
 * This class implements the Sensor Simulator portion of the Clean Sweep Robotic
 * Vacuum Cleaner Team Project (Clean Sweep). The purpose is to isolate the
 * Control System portion of the project from the input file containing
 * test information. All interfaces to gather legal, within the parameters
 * of the project assignment, data are included as well as interfaces
 * required to update the data.
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharp, Doug Oda
 * @version     I1
 * @date        11Sep2014
 */
public class VirtualHouse
{

   public class CellDescription
   {

      public SensorInterface sI;
      public int dirt;
      public int locX;
      public int locY;
      public boolean isCurrentCell;

      public CellDescription()
      {
         sI = new SensorInterface();
      }
   }
   private FloorGraphics picture;
   private boolean useGraphics;
   private CellDescription currentCell;
   private boolean hasSentInitialLocation;
   private LinkedList<CellDescription> floorPlan;
   private static final Logger logger = Logger.getLogger("Exceptions");

   /**
    * Constructor for VirtualHouse                           
    * <p>
    * The constructor initializes all private variables, prompts the user to 
    * input a map file and determines if the user wants graphics. The constructor
    * then iterates through the map file, extracts relevant information and
    * pushes it into a list of CellDescription. The information in this
    * list will be used to provided sensor information to the CleanSweepRobot.
    * <p>
    * The constructor also instantiates the graphics if applicable.
    */
   public VirtualHouse()
   {
      String inputFile = null;
      useGraphics = true;
      hasSentInitialLocation = false;
      floorPlan = new LinkedList<>();

      /*Get user input, xml file name and if the user wants the graphic*/

      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      for (;;)
      {
         System.out.println("Type Input File with floor plan (.\\floorplan.xml)");
         try
         {
            inputFile = in.readLine();
         }
         catch (Exception e)
         {
            logger.log(Level.SEVERE, "Input file not found", e);
         }
         if (inputFile.isEmpty())
         {
            inputFile = "floorplan.xml";
         }
         File f = new File(inputFile);
         if (f.canRead())
         {
            break;
         }
      }

      /*Fead file and put information in to a list for future reference*/
      GetFloorPlan(inputFile);

      /*Start graphics if desired*/
      if (useGraphics)
      {
         picture = new FloorGraphics(floorPlan);
         picture.UpdateGraphics();
      }
   }

   /**
    * Overloaded Constructor for VirtualHouse                           
    * <p>
    * The constructor is used for JUnit testing. It reads in the test file 
    * and initializes the same variables as the regular constructor.
    */
   public VirtualHouse(boolean JUnitTesting)
   {
      useGraphics = false;
      hasSentInitialLocation = false;
      floorPlan = new LinkedList<>();
      GetFloorPlan("JUnitTestFloorPlan.xml");
   }

   /**
    * Read Floor plan from .xml file                           
    * <p>
    * This method reads each line from the input file passed as a parameter
    * and each line that contains cell information is parsed and stored in
    * a CellDescription that is added to the floorPlan list
    *
    * @param  String inputFile - name of .xml file that contains floor plan
    */
   private void GetFloorPlan(String inputFile)
   {
      String line = null;
      BufferedReader br = null;
      File f = new File(inputFile);
      try
      {
         br = new BufferedReader(new FileReader(f));
      }
      catch (Exception e)
      {
         logger.log(Level.WARNING, "File not Found", e);
      }
      for (;;)
      {
         try
         {
            line = br.readLine();
         }
         catch (Exception e)
         {
            logger.log(Level.WARNING, "Cannot Read File", e);
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
            CellDescription CD = new CellDescription();

            /*get x*/
            A = line.indexOf("xs") + 4;
            B = line.indexOf("'", A);
            try
            {
               CD.locX = Integer.parseInt(line.substring(A, B));
            }
            catch (Exception e)
            {
               logger.log(Level.WARNING, "Bad input file format", e);
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
               logger.log(Level.WARNING, "Bad input file format", e);
            }
            /*get surface*/
            A = line.indexOf("ss") + 4;
            B = line.indexOf("'", A);
            switch (line.substring(A, B))
            {
               case "4":
                  CD.sI.floor = floorType.HighPileCarpet;
                  break;
               case "2":
                  CD.sI.floor = floorType.LowPileCarpet;
                  break;
               default:
                  CD.sI.floor = floorType.BareFloor;
                  break;
            }
            /*get amount of dirt of floor*/
            A = line.indexOf("ds") + 4;
            B = line.indexOf("'", A);
            try
            {
               CD.dirt = Integer.parseInt(line.substring(A, B));
            }
            catch (Exception e)
            {
               logger.log(Level.WARNING, "Bad input file format", e);
            }
            if (CD.dirt > 0)
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
               currentCell = CD;
               CD.isCurrentCell = true;
            }
            else
            {
               CD.sI.atChargingStation = false;
            }

            /*save it*/
            floorPlan.add(CD);
         }
      }
      try
      {
         br.close();
      }
      catch (Exception e)
      {
         logger.log(Level.WARNING, "Cannot close file", e);
      }
   }

   /**
    * Remove jPanel                          
    * <p>
    * Removes jPanel window graphic and should be called before program exits
    */
   public void Remove()
   {
      if (useGraphics)
      {
         picture.Remove();
      }
   }

   /**
    * Remove 1 unit of dirt from floor at current location                        
    */
   public void Vacuum()
   {
      if (currentCell.dirt > 0)
      {
         currentCell.dirt--;
      }
   }

   /**
    * Moves the Robot                           
    * <p>
    * This method provides the a way for the CleanSweepRobot (Control System) to
    * inform the VirtualHouse (Sensor Simulator) where it intends to move.
    * The new coordinates are checked to make sure that the new coordinates
    * do not violate the virtual laws of physics when moving from the old
    * coordinates. If the move is valid then the new coordinates are stored
    * and subsequent queries and actions are based on the new coordinates.
    *
    * @param  int newX - Desired new x coordinate
    * @param  int newY - Desired new y coordinate
    * @return true if movement is legal, false if new coordinates would move the
    *      robot though walls or coordinates are not adjacent to old coordinates
    */
   public boolean Move(int newX, int newY)
   {
      /*update graphics if wanted*/
      if (useGraphics)
      {
         picture.UpdateGraphics();
      }
      
      boolean movementOK = false;
      for (direction d : direction.values())
      {
         if ((currentCell.locX + d.xOffset()) == newX
                 && (currentCell.locY + d.yOffset()) == newY)
         {
            if (currentCell.sI.features[d.index()] == feature.OPEN)
            {
               movementOK = true;
            }
         }
      }
      if (movementOK)
      {
         currentCell.isCurrentCell = false;

         /*Get new cell information*/
         for (int i = 0; i < floorPlan.size(); i++)
         {
            if (floorPlan.get(i).locX == newX
                    && floorPlan.get(i).locY == newY)
            {
               currentCell = floorPlan.get(i);
               break;
            }
         }
         currentCell.isCurrentCell = true;
      }
      return movementOK;
   }

   /**
    * provides initial XY coordinate                          
    * <p>
    * This method provides the X and Y coordinates of the charging station which
    * is where the CleanSweepRobot will start. Since this information is not
    * sensor information, it will pass the information only once.
    *
    * @param  SensorInterface si - reference to sensor interface for passing xy
    */
   public void GetInitialLocation(SensorInterface si)
   {
      if (si != null)
      {
         if (hasSentInitialLocation)
         {
            si.StartingXCoord = Integer.MAX_VALUE;
            si.StartingYCoord = Integer.MAX_VALUE;
         }
         else
         {
            hasSentInitialLocation = true;
            for (int i = 0; i < floorPlan.size(); i++)
            {
               if (floorPlan.get(i).sI.atChargingStation)
               {
                  si.StartingXCoord = floorPlan.get(i).locX;
                  si.StartingYCoord = floorPlan.get(i).locY;
                  break;
               }
            }
         }
      }
   }

   /**
    * Provides all sensor information                        
    * <p>
    * This method provides all legal, within the parameters of the project 
    * assignment, data from the VirtualHouse (Sensor Simulator) and the
    * CleanSweepRobot (Control System)
    *
    * @param  SensorInterface si - reference to sensor interface for passing data
    */
   public void SensorInformation(SensorInterface tempSI)
   {
      if (tempSI != null)
      {
         for (direction d : direction.values())
         {
            tempSI.features[d.index()] = currentCell.sI.features[d.index()];
         }
         tempSI.floor = currentCell.sI.floor;
         tempSI.atChargingStation = currentCell.sI.atChargingStation;
         if (currentCell.dirt > 0)
         {
            tempSI.dirtPresent = true;
         }
         else
         {
            tempSI.dirtPresent = false;
         }
      }
   }
}