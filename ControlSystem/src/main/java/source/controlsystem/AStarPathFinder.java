/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package source.controlsystem;

import source.controlsystem.CleanSweepRobot.CellDescription;
import source.sensorsimulator.SensorInterface.direction;
import source.sensorsimulator.SensorInterface.feature;
import java.util.LinkedList;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 *
 * This class implements the A* method of graph traversal. Peter Hart, Nils Nilsson and 
 * Bertram Raphael of Stanford Research Institute (now SRI International) first described
 * the algorithm in 1968. It is an extension of Edsger Dijkstra's 1959 algorithm. Our team 
 * takes no credit for the algorithm, just the implementation. Further credit goes to an 
 * article by Patrick Lester called 'A* Pathfinding for Beginners' that put the whole 
 * thing in idiots language for easy coding.
 *
 * The class calculates the shortest path between a starting coordinate an ending 
 * coordinate and provides a list containing the route
 *
 * @author Ilker Evernos, David LeGare, Jeffrey Sharp, Doug Oda
 * @version I3
 * @date 02Nov2014
 */
public class AStarPathFinder
{

   static class Cell
   {

      CellDescription cd;
      int parentX;
      int parentY;
      int gScore;
      int hScore;
      int fScore;

      public Cell(CellDescription c, int px, int py, int gS, int hS)
      {
         parentX = px;
         parentY = py;
         gScore = gS;
         hScore = hS;
         fScore = gS + hS;
         cd = new CellDescription();
         cd.locX = c.locX;
         cd.locY = c.locY;
         cd.sI.atChargingStation = c.sI.atChargingStation;
         cd.sI.dirtPresent = c.sI.dirtPresent;
         cd.sI.floor = c.sI.floor;
         cd.sI.features[0] = c.sI.features[0];
         cd.sI.features[1] = c.sI.features[1];
         cd.sI.features[2] = c.sI.features[2];
         cd.sI.features[3] = c.sI.features[3];
      }
   }
   private int sX;
   private int sY;
   private int eX;
   private int eY;
   private LinkedList<Cell> openList;
   private LinkedList<Cell> closedList;
   private LinkedList<CellDescription> localMap;
   private AStarGraphics graphic;

   /**
    * 
    * <p>
    * 
    */
   public LinkedList<CellDescription> shortestPath(int startingX, int startingY,
           int endingX, int endingY,
           final LinkedList<CellDescription> map)
   {
      LinkedList<CellDescription> returnPath = new LinkedList<>();
      sX = startingX;
      sY = startingY;
      eX = endingX;
      eY = endingY;
      localMap = map;
      openList = new LinkedList<>();
      closedList = new LinkedList<>();
      graphic = new AStarGraphics(localMap, openList, closedList);

      /*load intial starting cell into the open list*/
      for (int i = 0; i < map.size(); i++)
      {
         if (map.get(i).locX == startingX && map.get(i).locY == startingY)
         {
            openList.add(generateNewCell(map.get(i), new Cell(map.get(i), startingX, startingY, 0, 0)));
            break;
         }
      }

      /*loop unit finished*/
      for (;;)
      {
         graphic.UpdateGraphics();
         /*find the lowest score*/
         int lowestFScore = Integer.MAX_VALUE;
         for (int i = 0; i < openList.size(); i++)
         {
            if (openList.get(i).fScore < lowestFScore)
            {
               lowestFScore = openList.get(i).fScore;
               openList.addLast(openList.get(i));
               openList.remove(i);
            }
         }
         /*remove lowest score from the open list and add to closed list*/
         closedList.addLast(openList.removeLast());
         if (closedList.getLast().hScore == 0)
         {
            break;
         }

         /*add 4 adjacent squares to open list if applicable*/
         addAdjacentCells(closedList.getLast());
      }

      /*populate return path*/
      returnPath.addLast(closedList.getLast().cd);
      int smallestValue;
      int tempx;
      int tempy;
    /*  for (;;)
      {
         tempx = returnPath.getLast().locX;
         tempy = returnPath.getLast().locY;

         if (tempx == sX && tempy == sY)
         {
            break;
         }
         for (int i = 0; i < closedList.size(); i++)
         {
            if (tempx == closedList.get(i).parentX
                    && tempy == closedList.get(i).parentY)
            {
               returnPath.addLast(closedList.remove(i).cd);
               break;
            }
         }

      }*/
      graphic.Remove();
      return returnPath;
   }

   private void addAdjacentCells(final Cell c)
   {

      boolean okToUse;
      boolean inOpenList;
      for (direction d : direction.values())
      {
         okToUse = false;
         /*check if it is "walkable"*/
         if (c.cd.sI.features[d.index()] == feature.OPEN)
         {
            okToUse = true;
         }
         /*check if it is on the closed list*/
         for (int i = 0; i < closedList.size(); i++)
         {
            if (closedList.get(i).cd.locX == (c.cd.locX + d.xOffset())
                    && closedList.get(i).cd.locY == (c.cd.locY + d.yOffset()))
            {
               okToUse = false;
            }
         }
         if (okToUse)
         {
            /* check if that direction is already in the list*/
            inOpenList = false;
            for (int i = 0; i < openList.size(); i++)
            {
               if (openList.get(i).cd.locX == (c.cd.locX + d.xOffset()) && openList.get(i).cd.locY == (c.cd.locY + d.yOffset()))
               {
                  /*ok one has been found so save the new one if the score is higher and discard the old one*/
                  if (generateNewCell(openList.get(i).cd, c).gScore < openList.get(i).gScore)
                  {
                     openList.remove(i);
                     openList.add(generateNewCell(openList.get(i).cd, c));
                  }
                  inOpenList = true;
                  break;
               }
            }
            if (!inOpenList)
            {
               /*if not in the open list then get the cell description from the map*/
               for (int i = 0; i < localMap.size(); i++)
               {
                  if (localMap.get(i).locX == (c.cd.locX + d.xOffset()) && localMap.get(i).locY == (c.cd.locY + d.yOffset()))
                  {
                     openList.add(generateNewCell(localMap.get(i), c));
                     break;
                  }
               }

            }
         }
      }
   }

   private Cell generateNewCell( CellDescription newC, Cell parentC)
   {
      /*distance to starting cell*/
      int g = parentC.gScore + 10;
      /*distance to ending cell*/
      int deltaX = Integer.signum(newC.locX - eX) * (newC.locX - eX);
      int deltaY = Integer.signum(newC.locY - eY) * (newC.locY - eY);
      int h = (deltaX + deltaY) * 10;
      return new Cell(newC, parentC.cd.locX, parentC.cd.locY, g, h);
   }
}