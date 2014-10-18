package Source;

import java.util.LinkedList;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 *
 * This class implements the Control System portion of the Clean Sweep Robotic
 * Vacuum Cleaner Team Project (Clean Sweep). The purpose is to provide the 
 * required functionality of the Clean Sweep. 
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharp, Doug Oda
 * @version     I1
 * @date        11Sep2014
 */
public class CleanSweepRobot
{

   private class LocationOfNotVisited
   {

      public int NotVisitedX;
      public int NotVisitedY;

      public LocationOfNotVisited(int x, int y)
      {
         NotVisitedX = x;
         NotVisitedY = y;
      }
   }

   public class CellDescription
   {

      public SensorInterface SI;
      public int locX;
      public int locY;

      public CellDescription()
      {
         SI = new SensorInterface();
      }
   }
   VirtualHouse VH;
   LinkedList<CellDescription> InternalMap;
   LinkedList<LocationOfNotVisited> Destinations;
   int CurrentX;
   int CurrentY;

   public CleanSweepRobot(VirtualHouse virtualHouse)
   {
      VH = virtualHouse;
      InternalMap = new LinkedList<CellDescription>();
      Destinations = new LinkedList<LocationOfNotVisited>();
      SensorInterface ci = new SensorInterface();
      VH.GetInitialLocation(ci);
      CurrentX = ci.StartingXCoord;
      CurrentY = ci.StartingYCoord;
   }

   public boolean CleanSweepUpdate()
   {
      CellDescription Current = new CellDescription();
      /*Check Sensors*/
      VH.SensorInformation(Current.SI);
      Current.locX = CurrentX;
      Current.locY = CurrentY;
      AddToInternalMap(Current);

      /*Update Destinations*/
      UpdateNotVisitedList(Current);

      /*Move Clean Sweep*/
      if (Destinations.size() > 0)
      {
         MoveCleanSweep(Current);
      }
      else
      {
         return false;
      }
      return true;
   }

   private void MoveCleanSweep(CellDescription Current)
   {
      int targetx = Destinations.getLast().NotVisitedX;
      int targety = Destinations.getLast().NotVisitedY;
      /*Check if it is an adjacent cell to teh east or west*/
      if ((targetx == Current.locX + 1 || targetx == Current.locX - 1) && targety == Current.locY)
      {
         if (VH.Move(targetx, Current.locY))
         {
            CurrentX = targetx;
            CurrentY = Current.locY;
            Destinations.removeLast();
         }
      }
      if ((targety == Current.locY + 1 || targety == Current.locY - 1) && targetx == Current.locX)
      {
         if (VH.Move(Current.locX, targety))
         {
            CurrentX = Current.locX;
            CurrentY = targety;
            Destinations.removeLast();
         }
      }
      /*If not adjacent then move toward the cell*/
      else
      {
         if (targetx < Current.locX && Current.SI.west == SensorInterface.feature.OPEN)
         {
            if (VH.Move(Current.locX - 1, Current.locY))
            {
               CurrentX = Current.locX - 1;
               CurrentY = Current.locY;
            }
         }
         else if (targety < Current.locY && Current.SI.south == SensorInterface.feature.OPEN)
         {
            if (VH.Move(Current.locX, Current.locY- 1))
            {
               CurrentX = Current.locX;
               CurrentY = Current.locY- 1;
            }
         }
         else if (targetx > Current.locX && Current.SI.east == SensorInterface.feature.OPEN)
         {
            if (VH.Move(Current.locX + 1, Current.locY))
            {
               CurrentX = Current.locX + 1;
               CurrentY = Current.locY;
            }
         }
         else if (targety > Current.locY && Current.SI.north == SensorInterface.feature.OPEN)
         {
            if (VH.Move(Current.locX, Current.locY + 1))
            {
               CurrentX = Current.locX;
               CurrentY = Current.locY + 1;
            }
         }
      }
   }

   private void UpdateNotVisitedList(CellDescription Current)
   {
      boolean WantToGoThere;
      /*Check North*/
      if (Current.SI.north == SensorInterface.feature.OPEN)
      {
         /*If there is not an obsitcle north then check space has
          * already been visited*/
         WantToGoThere = true;
         for (int i = 0; i < InternalMap.size(); i++)
         {
            if (Current.locX == InternalMap.get(i).locX
                    && (Current.locY + 1) == InternalMap.get(i).locY)
            {
               WantToGoThere = false;
               break;
            }
         }
         if (WantToGoThere)
         {
            /*If here then there is no obsitcle and not been visited
            so remove any old occurances of that location*/
            for (int i = 0; i < Destinations.size(); i++)
            {
               if (Current.locX == Destinations.get(i).NotVisitedX
                       && (Current.locY + 1) == Destinations.get(i).NotVisitedY)
               {
                  Destinations.remove(i);
                  break;
               }
            }
            /*Add to places that need visited*/
            Destinations.add(new LocationOfNotVisited(Current.locX, (Current.locY + 1)));
         }
      }
      /*Check East*/
      if (Current.SI.east == SensorInterface.feature.OPEN)
      {
         WantToGoThere = true;
         for (int i = 0; i < InternalMap.size(); i++)
         {
            if ((Current.locX + 1) == InternalMap.get(i).locX
                    && Current.locY == InternalMap.get(i).locY)
            {
               WantToGoThere = false;
               break;
            }
         }
         if (WantToGoThere)
         {
            for (int i = 0; i < Destinations.size(); i++)
            {
               if ((Current.locX + 1) == Destinations.get(i).NotVisitedX
                       && Current.locY == Destinations.get(i).NotVisitedY)
               {
                  Destinations.remove(i);
                  break;
               }
            }
            Destinations.add(new LocationOfNotVisited((Current.locX + 1), Current.locY));
         }
      }
      /*Check South*/
      if (Current.SI.south == SensorInterface.feature.OPEN)
      {
         WantToGoThere = true;
         for (int i = 0; i < InternalMap.size(); i++)
         {
            if (Current.locX == InternalMap.get(i).locX
                    && (Current.locY - 1) == InternalMap.get(i).locY)
            {
               WantToGoThere = false;
               break;
            }
         }
         if (WantToGoThere)
         {
            for (int i = 0; i < Destinations.size(); i++)
            {
               if (Current.locX == Destinations.get(i).NotVisitedX
                       && (Current.locY - 1) == Destinations.get(i).NotVisitedY)
               {
                  Destinations.remove(i);
                  break;
               }
            }
            Destinations.add(new LocationOfNotVisited(Current.locX, (Current.locY - 1)));
         }
      }
      /*Check West*/
      if (Current.SI.west == SensorInterface.feature.OPEN)
      {
         WantToGoThere = true;
         for (int i = 0; i < InternalMap.size(); i++)
         {
            if ((Current.locX - 1) == InternalMap.get(i).locX
                    && Current.locY == InternalMap.get(i).locY)
            {
               WantToGoThere = false;
               break;
            }
         }
         if (WantToGoThere)
         {
            for (int i = 0; i < Destinations.size(); i++)
            {
               if ((Current.locX - 1) == Destinations.get(i).NotVisitedX
                       && Current.locY == Destinations.get(i).NotVisitedY)
               {
                  Destinations.remove(i);
                  break;
               }
            }
            Destinations.add(new LocationOfNotVisited((Current.locX - 1), Current.locY));
         }
      }
   }

   private void AddToInternalMap(CellDescription Current)
   {
      boolean isNewLocation = true;
      for (int i = 0; i < InternalMap.size(); i++)
      {
         if (Current.locX == InternalMap.get(i).locX
                 && Current.locY == InternalMap.get(i).locY)
         {
            isNewLocation = false;
            break;
         }
      }
      if (isNewLocation)
      {
         InternalMap.add(Current);
      }
   }
}
