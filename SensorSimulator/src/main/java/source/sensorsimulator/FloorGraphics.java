package source.sensorsimulator;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * This class has absolutely nothing to do with the requirements of the 
 * Clean Sweep Project...it is just fun and aids in troubleshooting navigation.
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharp, Doug Oda
 * @version     I1
 * @date        11Sep2014
 */
public class FloorGraphics {

   private JFrame FloorFrame;
   private BufferedImage FloorBI;
   private FloorJPanel CSJP;
   private int floorYdimension;
   private int floorXdimension;
   private List<VirtualHouse.CellDescription> Floor;

   /*
    * Graphic instatiation, I'm not going into detail since this
    * is not actually part of the project requriements
    */
   public FloorGraphics(int x, int y, int ix, int iy, List<VirtualHouse.CellDescription> FP) {
      Floor = FP;
      floorXdimension = x;
      floorYdimension = y;
      CSJP = new FloorJPanel();
      FloorBI = new BufferedImage((floorXdimension * 50), (floorYdimension * 50), BufferedImage.TYPE_BYTE_BINARY);
      CSJP.paintComponent(FloorBI.createGraphics());
      FloorFrame = new JFrame("Clean Sweep Tracker");
      FloorFrame.isAlwaysOnTop();
      FloorFrame.setMinimumSize(new Dimension(floorXdimension * 60 + 16, floorYdimension * 60 + 38));
      FloorFrame.add(CSJP);
      FloorFrame.pack();
      FloorFrame.setVisible(true);
      try {
         Thread.sleep(1000);
      } catch (Exception e) {
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
         for (int a = 0; a < Floor.size(); a++) {
            int X = (Floor.get(a).locX * 50) + 5;
            int Y = (floorYdimension * 50 + 5) - (Floor.get(a).locY * 50);
            if (Floor.get(a).sI.features[3] == SensorInterface.feature.OBSTICLE) {
               g2d.fillRect(X - 2, Y, 5, 50);
            } else {
               g2d.fillRect(X, Y, 1, 50);
            }
            if (Floor.get(a).sI.features[1] == SensorInterface.feature.OBSTICLE) {
               g2d.fillRect(X + 48, Y, 5, 50);
            } else {
               g2d.fillRect(X + 50, Y, 1, 50);
            }
            if (Floor.get(a).sI.features[2] == SensorInterface.feature.OBSTICLE) {
               g2d.fillRect(X, Y + 48, 50, 5);
            } else {
               g2d.fillRect(X, Y + 50, 50, 1);
            }
            if (Floor.get(a).sI.features[0] == SensorInterface.feature.OBSTICLE) {
               g2d.fillRect(X, Y - 2, 50, 5);
            } else {
               g2d.fillRect(X, Y, 50, 1);
            }

            if (Floor.get(a).sI.atChargingStation) {
               g2d.drawString("Charge", X + 5, Y + 21);
               g2d.drawString("Station", X + 6, Y + 35);
            }
            if (Floor.get(a).isCurrentCell) {
               g2d.fillRect(X + 5, Y + 5, 40, 40);
            }
         }
      }
   }

   /*
    * Updates jpanel graphics after witing 250 milliseconds
    */
   public void UpdateGraphics() {
      try {
         Thread.sleep(250);
      } catch (Exception e) {
      }
      CSJP.paintComponent(FloorBI.createGraphics());
      FloorFrame.repaint();
   }

   /*
    * Removes jpanel after waiting 2 seconds
    */
   public void Remove() {
      try {
         Thread.sleep(2000);
      } catch (Exception e) {
      }
      FloorFrame.dispose();
   }
}
