package us.ihmc.euclid.geometry;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import us.ihmc.euclid.tools.EuclidCoreRandomTools;
import us.ihmc.euclid.tools.EuclidCoreTestTools;
import us.ihmc.euclid.tuple3D.Point3D;
import us.ihmc.euclid.tuple3D.Vector3D;

public class Sphere3DTest
{

   @Test
   public void testCommonShape3dFunctionality()
   {
      Shape3DTestHelper testHelper = new Shape3DTestHelper();
      Random random = new Random(1776L);

      int numberOfShapes = 1000;
      int numberOfPoints = 1000;

      for (int i = 0; i < numberOfShapes; i++)
      {
         Point3D center = EuclidCoreRandomTools.generateRandomPoint3D(random, 1.0, 1.0, 1.0);
         double radius = EuclidCoreRandomTools.generateRandomDouble(random, 0.01, 10.0);
         Sphere3D sphere3d = new Sphere3D(center, radius);
         testHelper.runSimpleTests(sphere3d, random, numberOfPoints);
      }
   }

   @Test
   public void testSimpleConstructor()
   {
      Sphere3D sphere3d = new Sphere3D();

      double epsilon = 1e-14;
      assertEquals(sphere3d.getRadius(), 1.0, epsilon);
      Point3D centerCheck = new Point3D();
      sphere3d.getPosition(centerCheck);
      EuclidCoreTestTools.assertTuple3DEquals(new Point3D(), centerCheck, epsilon);
   }

   @Test
   public void testIsInside()
   {
      Sphere3D sphere3d = new Sphere3D();

      double justInside = 0.999;
      double justOutside = 1.001;
      assertTrue(sphere3d.isInsideOrOnSurface(new Point3D(justInside, 0.0, 0.0)));
      assertTrue(sphere3d.isInsideOrOnSurface(new Point3D(0.0, justInside, 0.0)));
      assertTrue(sphere3d.isInsideOrOnSurface(new Point3D(0.0, 0.0, justInside)));

      assertFalse(sphere3d.isInsideOrOnSurface(new Point3D(justOutside, 0.0, 0.0)));
      assertFalse(sphere3d.isInsideOrOnSurface(new Point3D(0.0, justOutside, 0.0)));
      assertFalse(sphere3d.isInsideOrOnSurface(new Point3D(0.0, 0.0, justOutside)));

      assertTrue(sphere3d.isInsideOrOnSurface(new Point3D(Math.sqrt(1.999) / 2.0, Math.sqrt(1.999) / 2.0, 0.0)));
      assertFalse(sphere3d.isInsideOrOnSurface(new Point3D(Math.sqrt(2.001) / 2.0, Math.sqrt(2.001) / 2.0, 0.0)));

   }

   @Test
   public void testOrthogonalProjection()
   {
      Point3D center = new Point3D(1.0, 2.1, 3.2);
      double radius = 0.7634;

      Sphere3D sphere3d = new Sphere3D(center, radius);

      Point3D randomPoint = new Point3D(17.3, 19.2, 11.4);
      Point3D orthogonalProjection = new Point3D(randomPoint);
      sphere3d.orthogonalProjection(orthogonalProjection);

      assertEquals(radius, orthogonalProjection.distance(center), 1e-7);
      Vector3D vector1 = new Vector3D(randomPoint);
      vector1.sub(center);

      Vector3D vector2 = new Vector3D(orthogonalProjection);
      vector2.sub(center);

      vector1.normalize();
      vector2.normalize();

      EuclidCoreTestTools.assertTuple3DEquals(vector1, vector2, 1e-7);
   }

}