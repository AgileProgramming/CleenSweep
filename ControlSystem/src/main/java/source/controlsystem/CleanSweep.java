package source.controlsystem;

import source.sensorsimulator.VirtualHouse;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 * 
 * Entry point for the Clean Sweep Robotic Vacuum Cleaner Team Project
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharpe, Doug Oda
 * @version     I3
 * @date        07Nov2014
 */
public class CleanSweep {
   
   private CleanSweep(){     
   }

   public static void main(String[] args) {
      /*Get user input, .xml file name and if graphics are desired,
       * read file and save to provide input to robot*/
      VirtualHouse cleanSweepInput = new VirtualHouse();

      /*Setup lists and private variable to get robot ready to roll */
      CleanSweepRobot robot = new CleanSweepRobot(cleanSweepInput);

      /*Move robot around unit all spaces have been visited*/
      boolean notFinished = true;
      while ( notFinished ){
         notFinished = robot.cleanSweepUpdate();
      }
      cleanSweepInput.remove();
   }
}
