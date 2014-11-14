/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package source.sensorsimulator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dianna_Doug
 */
public class CellDescriptionTest {
   
   public CellDescriptionTest() {
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
    * Test of sI method, of class CellDescription.
    */
   @Test
   public void testSI() {
      System.out.println("sI()");
      CellDescription instance = new CellDescription();
      instance.sI.dirtPresent = true;
      SensorInterface result = instance.sI();
      assertEquals(true, result.dirtPresent);
   }

   /**
    * Test of dirt method, of class CellDescription.
    */
   @Test
   public void testDirt_0args() {
      System.out.println("dirt()");
      CellDescription instance = new CellDescription();
      instance.dirt(3);
      int result = instance.dirt();
      assertEquals(3, result);
   }

   /**
    * Test of locX method, of class CellDescription.
    */
   @Test
   public void testLocX_0args() {
      System.out.println("locX()");
      CellDescription instance = new CellDescription();
      instance.locX(6);
      int result = instance.locX();
      assertEquals(6, result);

   }

   /**
    * Test of locY method, of class CellDescription.
    */
   @Test
   public void testLocY_0args() {
      System.out.println("locY()");
      CellDescription instance = new CellDescription();
      instance.locY(9);
      int result = instance.locY();
      assertEquals(9, result);
   }

   /**
    * Test of locX method, of class CellDescription.
    */
   @Test
   public void testLocX_int() {
      System.out.println("locX(i)");
      CellDescription instance = new CellDescription();
      instance.locX(9);
      assertEquals(instance.locX(), 9);
   }

   /**
    * Test of locY method, of class CellDescription.
    */
   @Test
   public void testLocY_int() {
      System.out.println("locY(i)");
      CellDescription instance = new CellDescription();
      instance.locY(3);
      assertEquals(instance.locY(), 3);
   }

   /**
    * Test of dirt method, of class CellDescription.
    */
   @Test
   public void testDirt_int() {
      System.out.println("dirt(i)");
      CellDescription instance = new CellDescription();
      instance.dirt(69);
      assertEquals(instance.dirt(), 69);;
   }

   /**
    * Test of isCurrentCell method, of class CellDescription.
    */
   @Test
   public void testIsCurrentCell_boolean() {
      System.out.println("isCurrentCell()");
      CellDescription instance = new CellDescription();
      instance.isCurrentCell(false);
      assertEquals(instance.isCurrentCell(), false);
   }

   /**
    * Test of isCurrentCell method, of class CellDescription.
    */
   @Test
   public void testIsCurrentCell_0args() {
      System.out.println("isCurrentCell(b)");
      CellDescription instance = new CellDescription();
      instance.isCurrentCell(false);
      assertEquals(instance.isCurrentCell(), false);
   }
}
