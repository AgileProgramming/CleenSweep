package source.sensorsimulator;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * This class has absolutely nothing to do with the requirements of the 
 * Clean Sweep Project...it is just fun and aids in troubleshooting navigation.
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharpe, Doug Oda
 * @version     I1
 * @date        11Sep2014
 */
public class FloorGraphics {

   private JFrame floorFrame;
   private BufferedImage floorBI;
   private FloorJPanel cSJP;
   private int floorYdimension;
   private List<VirtualHouse.CellDescription> floor;
   private static final Logger LOGGER = Logger.getLogger("Exceptions");

   /*
    * Graphic instatiation, I'm not going into detail since this
    * is not actually part of the project requriements
    */
   public FloorGraphics(List<VirtualHouse.CellDescription> fp) {
      floor = fp;
      int floorXdimension = 0;
      floorYdimension = 0;
      for (int i = 0; i < fp.size(); i++) {
         if (fp.get(i).locX() > floorXdimension) {
            floorXdimension = fp.get(i).locX();
         }
         if (fp.get(i).locY() > floorYdimension) {
            floorYdimension = fp.get(i).locY();
         }
      }
      cSJP = new FloorJPanel();
      floorBI = new BufferedImage(floorXdimension * 50, floorYdimension * 50, BufferedImage.TYPE_BYTE_BINARY);
      cSJP.paintComponent(floorBI.createGraphics());
      floorFrame = new JFrame("Clean Sweep Tracker");
      floorFrame.isAlwaysOnTop();
      floorFrame.setMinimumSize(new Dimension(floorXdimension * 60 + 16, floorYdimension * 60 + 38));
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
    * Note Many Many Magic Numbers, again this is not part of the project requirements
    * so I'll leave 'em alone
    */
   private class FloorJPanel extends JPanel {

      @Override
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         Graphics2D g2d = (Graphics2D) g;
         for (int a = 0; a < floor.size(); a++) {
            int xCoordinate = (floor.get(a).locX() * 50) + 5;
            int yCoordinate = (floorYdimension * 50 + 5) - (floor.get(a).locY() * 50);
            if (floor.get(a).sI.features[3] == SensorInterface.feature.OBSTICLE) {
               g2d.fillRect(xCoordinate - 2, yCoordinate, 5, 50);
            } else {
               g2d.fillRect(xCoordinate, yCoordinate, 1, 50);
            }
            if (floor.get(a).sI.features[1] == SensorInterface.feature.OBSTICLE) {
               g2d.fillRect(xCoordinate + 48, yCoordinate, 5, 50);
            } else {
               g2d.fillRect(xCoordinate + 50, yCoordinate, 1, 50);
            }
            if (floor.get(a).sI.features[2] == SensorInterface.feature.OBSTICLE) {
               g2d.fillRect(xCoordinate, yCoordinate + 48, 50, 5);
            } else {
               g2d.fillRect(xCoordinate, yCoordinate + 50, 50, 1);
            }
            if (floor.get(a).sI.features[0] == SensorInterface.feature.OBSTICLE) {
               g2d.fillRect(xCoordinate, yCoordinate - 2, 50, 5);
            } else {
               g2d.fillRect(xCoordinate, yCoordinate, 50, 1);
            }

            if (floor.get(a).sI.atChargingStation) {
               g2d.drawString("Charge", xCoordinate + 5, yCoordinate + 21);
               g2d.drawString("Station", xCoordinate + 6, yCoordinate + 35);
            }
            if (floor.get(a).isCurrentCell()) {
               g2d.fillRect(xCoordinate + 5, yCoordinate + 5, 40, 40);
            }
         }
      }
   }

   /*
    * Updates jpanel graphics after witing 250 milliseconds
    */
   public void updateGraphics() {
      try {
         Thread.sleep(250);
      } catch (Exception e) {
         LOGGER.log(Level.WARNING, "Just to shutup SonarA", e);
      }
      cSJP.paintComponent(floorBI.createGraphics());
      floorFrame.repaint();
   }

   /*
    * Removes jpanel after waiting 2 seconds
    */
   public void remove() {
      try {
         Thread.sleep(2000);
      } catch (Exception e) {
         LOGGER.log(Level.WARNING, "Just to shutup SonarB", e);
      }
      floorFrame.dispose();
   }
}
