package Source;

/**
 * SE-359/459 Clean Sweep Robotic Vacuum Cleaner Team Project
 * 
 * Entry point for the Clean Sweep Robotic Vacuum Cleaner Team Project
 * 
 * @author      Ilker Evrenos, David LeGare, Jeffrey Sharp, Doug Oda
 * @version     I1
 * @date        11Sep2014
 */
public class CleanSweep {

   public static void main(String[] args) {
      /*Get user input, .xml file name and if graphics are desired,
       * read file and save to provide input to Robot*/
      VirtualHouse CleanSweepInput = new VirtualHouse();

      /*Setup lists and private variable to get Robot ready to roll */
      CleanSweepRobot Robot = new CleanSweepRobot(CleanSweepInput);

      /*Move robot around unit all spaces have been visited*/
      while (Robot.cleanSweepUpdate()) {
      }

      CleanSweepInput.Remove();
   }
}
