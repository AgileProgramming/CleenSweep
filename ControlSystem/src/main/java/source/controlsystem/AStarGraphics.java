package source.controlsystem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import source.controlsystem.AStarPathFinder.Cell;
import source.controlsystem.CleanSweepRobot.CellDescription;
import source.sensorsimulator.SensorInterface;

/**
 *
 * This class has absolutely nothing to do with the requirements of the
 * Clean Sweep Project. It aids in troubleshooting the A* search class
 *
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharpe, Doug Oda
 * @version     I3
 * @date        08Nov2014
 */
public class AStarGraphics{

   private JFrame floorFrame;
   private BufferedImage floorBI;
   private FloorJPanel cSJP;
   private int floorYdimension;
   private List<CellDescription> knownCells;
   private List<Cell> openList;
   private List<Cell> closedList;
   private List<CellDescription> returnPath;
   private static final Logger LOGGER = Logger.getLogger("Exceptions");

   /*
    * Graphic instatiation, I'm not going into detail since this
    * is not actually part of the project requriements
    */
   public AStarGraphics(List<CellDescription> cd,
                        List<Cell> ol, List<Cell> cl,
                        List<CellDescription> rp ) {
      returnPath = rp;
      knownCells = cd;
      openList = ol;
      closedList = cl;
      int floorXdimension = 1;
      floorYdimension = 1;
      for ( int i = 0; i < cd.size(); i ++ ) {
         if ( cd.get(i).locX > floorXdimension ){
            floorXdimension = cd.get(i).locX;
         }
         if ( cd.get(i).locY > floorYdimension ){
            floorYdimension = cd.get(i).locY;
         }
      }
      cSJP = new FloorJPanel();
      floorBI = new BufferedImage(floorXdimension * 50,
                      floorYdimension * 50, BufferedImage.TYPE_BYTE_BINARY);
      cSJP.paintComponent(floorBI.createGraphics());
      floorFrame = new JFrame("A* tracking");
      floorFrame.isAlwaysOnTop();
      floorFrame.setMinimumSize(new Dimension(floorXdimension * 62 + 100,
                                                   floorYdimension * 62 + 100));
      floorFrame.add(cSJP);
      floorFrame.pack();
      floorFrame.setVisible(true);
      try {
         Thread.sleep(1000);
      } catch (Exception e) {
         LOGGER.log(Level.WARNING, "Just to shutup Sonar", e);
      }
   }

   /*
    * Jpanel extenstion, I'm not going into detail since this
    * is not actually part of the project requriements
    *
    * Note Many Many Magic Numbers, again this is not part of the project
    * requirements so I'll leave 'em alone
    */
   private class FloorJPanel extends JPanel {

      @Override
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         Graphics2D g2d = (Graphics2D) g;
         g2d.setColor(Color.BLACK);
         for (int a = 0; a < knownCells.size(); a++) {
            int x = (knownCells.get(a).locX * 50) + 5;
            int y = (floorYdimension * 50 + 5) - (knownCells.get(a).locY * 50);
            if (knownCells.get(a).sI.features[3] == SensorInterface.feature.OBSTICLE) {
               g2d.fillRect(x - 2, y, 5, 50);
            } else {
               g2d.fillRect(x, y, 1, 50);
            }
            if (knownCells.get(a).sI.features[1] == SensorInterface.feature.OBSTICLE) {
               g2d.fillRect(x + 48, y, 5, 50);
            } else {
               g2d.fillRect(x + 50, y, 1, 50);
            }
            if (knownCells.get(a).sI.features[2] == SensorInterface.feature.OBSTICLE) {
               g2d.fillRect(x, y + 48, 50, 5);
            } else {
               g2d.fillRect(x, y + 50, 50, 1);
            }
            if (knownCells.get(a).sI.features[0] == SensorInterface.feature.OBSTICLE) {
               g2d.fillRect(x, y - 2, 50, 5);
            } else {
               g2d.fillRect(x, y, 50, 1);
            }

            if (knownCells.get(a).sI.atChargingStation) {
               g2d.drawString("Charge", x + 5, y + 21);
               g2d.drawString("Station", x + 6, y + 35);
            }
         }
         g2d.setColor(Color.ORANGE);
         for ( int i = 0; i < openList.size(); i ++ ){
            g2d.fillRect ( openList.get(i).cd.locX* 50 + 10 ,
            (floorYdimension * 50 + 10) - (openList.get(i).cd.locY * 50),40,40);
         }

         g2d.setColor(Color.YELLOW);
         for ( int i = 0; i < closedList.size(); i ++ ){
            g2d.fillRect ( closedList.get(i).cd.locX* 50 + 10 ,
            (floorYdimension * 50 + 10) - (closedList.get(i).cd.locY * 50),40,40);
         }

         g2d.setColor(Color.GREEN);
         for ( int i = 0; i < returnPath.size(); i ++ ){
            g2d.fillRect ( returnPath.get(i).locX * 50 + 10 ,
            (floorYdimension * 50 + 10) - (returnPath.get(i).locY * 50),40,40);
         }
      }
   }

   /*
    * Updates jpanel graphics after witing 100 milliseconds
    */
   public void updateGraphics() {
      try {
         Thread.sleep(100);
      } catch (Exception e) {
         LOGGER.log(Level.WARNING, "Just to shutup SonarA", e);
      }
      cSJP.paintComponent(floorBI.createGraphics());
      floorFrame.repaint();
   }

   /*
    * Removes jpanel
    */
   public void remove() {
      floorFrame.dispose();
   }
}
