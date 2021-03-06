package us.ihmc.euclid.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import us.ihmc.euclid.axisAngle.AxisAngle;
import us.ihmc.euclid.matrix.RotationMatrix;
import us.ihmc.euclid.tools.EuclidCoreRandomTools;
import us.ihmc.euclid.tools.EuclidCoreTestTools;
import us.ihmc.euclid.tools.EuclidCoreTools;
import us.ihmc.euclid.transform.RigidBodyTransform;
import us.ihmc.euclid.tuple3D.Point3D;
import us.ihmc.euclid.tuple3D.Vector3D;
import us.ihmc.euclid.tuple4D.Quaternion;

public class Ramp3DTest
{
   private static final boolean DEBUG = false;
   private static final int ITERATIONS = 1000;

   /**
    * Ramp3d needs a little more work and the tests improve. It's hard to do really good surface normal
    * tests at the corners.
    */
   @Test
   public void testCommonShape3dFunctionality()
   {
      Shape3DTestHelper testHelper = new Shape3DTestHelper();
      Random random = new Random(1776L);

      int numberOfShapes = 1000;
      int numberOfPoints = 1000;

      for (int i = 0; i < numberOfShapes; i++)
      {
         RigidBodyTransform transform = EuclidCoreRandomTools.nextRigidBodyTransform(random);
         double length = EuclidCoreRandomTools.nextDouble(random, 0.01, 10.0);
         double width = EuclidCoreRandomTools.nextDouble(random, 0.01, 10.0);
         double height = EuclidCoreRandomTools.nextDouble(random, 0.01, 10.0);
         Ramp3D box3d = new Ramp3D(transform, length, width, height);
         testHelper.runSimpleTests(box3d, random, numberOfPoints);
      }
   }

   @Test
   public void testExampleUsage()
   {
      RigidBodyTransform transform = new RigidBodyTransform();
      transform.setRotationYawAndZeroTranslation(Math.PI / 2.0);
      transform.setTranslation(new Vector3D(2.0, 0.0, 3.0));

      // example use
      Ramp3D ramp3d = new Ramp3D(transform, 1.0, 1.0, 1.0);
      Point3D pointToCheck = new Point3D(2.0, 0.0, 4.0);
      assertFalse(ramp3d.isInsideOrOnSurface(pointToCheck));
      assertEquals(Math.toRadians(45.0), ramp3d.getRampIncline(), 1e-7);
   }

   @Test
   public void testGetAndSet()
   {
      Random random = new Random(1024L);
      Ramp3D ramp1 = createRandomRamp(random);

      Ramp3D ramp2 = new Ramp3D(1.0, 1.0, 1.0);
      ramp2.setWidth(ramp1.getWidth());
      ramp2.setLength(ramp1.getLength());
      ramp2.setHeight(ramp1.getHeight());
      ramp2.setPose(ramp1);

      assertTrue(ramp1.getWidth() == ramp2.getWidth());
      assertTrue(ramp1.getLength() == ramp2.getLength());
      assertTrue(ramp1.getHeight() == ramp2.getHeight());
      ramp1.epsilonEquals(ramp2, 1e-7);
   }

   @Test
   public void testSurfaceNormal()
   {
      Ramp3D ramp = new Ramp3D(1.0, 1.0, 1.0);
      Vector3D surfaceNormal = new Vector3D();
      ramp.getRampSurfaceNormal(surfaceNormal);
      assertEquals("not equal", surfaceNormal.getX(), -1.0 / Math.sqrt(2.0), 1e-14);
      assertEquals("not equal", surfaceNormal.getY(), 0.0, 1e-14);
      assertEquals("not equal", surfaceNormal.getZ(), 1.0 / Math.sqrt(2.0), 1e-14);
   }

   @Test
   public void testSimpleOrthogonalProjection()
   {
      Ramp3D ramp3d = new Ramp3D(1.0, 1.0, 1.0);
      Point3D pointToProject = new Point3D(0.0, 0.0, 1.0);
      ramp3d.orthogonalProjection(pointToProject);
      assertEquals(pointToProject.getX(), 0.5, 1e-14);
      assertEquals(pointToProject.getY(), 0.0, 1e-14);
      assertEquals(pointToProject.getZ(), 0.5, 1e-14);

      RigidBodyTransform transform = new RigidBodyTransform();
      transform.setRotationYawAndZeroTranslation(0.5 * Math.PI);
      ramp3d.applyTransform(transform);
      pointToProject.set(0.0, 0.0, 1.0);
      ramp3d.orthogonalProjection(pointToProject);
      assertEquals(pointToProject.getX(), 0.0, 1e-14);
      assertEquals(pointToProject.getY(), 0.5, 1e-14);
      assertEquals(pointToProject.getZ(), 0.5, 1e-14);
   }

   @Test
   public void testSimplePointOutside()
   {
      Ramp3D ramp3d = new Ramp3D(1.0, 1.0, 1.0);
      assertFalse(ramp3d.isInsideOrOnSurface(new Point3D(new double[] {0.0, 0.0, 1.0})));
      assertTrue(ramp3d.isInsideOrOnSurface(new Point3D(new double[] {0.5, 0.0, 0.1})));
   }

   @Test
   public void testSimpleMethodCalls()
   {
      Ramp3D ramp3d = new Ramp3D(1.0, 1.0, 1.0);

      // can apply transform and test a point that switches sides of the ramp
      RigidBodyTransform transform = new RigidBodyTransform();
      transform.setRotationYawAndZeroTranslation(Math.PI / 2.0);

      Point3D p1 = new Point3D(new double[] {0.4, 0.2, 0.0});
      Point3D p2 = new Point3D(new double[] {0.4, -0.2, 0.0});

      assertTrue(ramp3d.isInsideOrOnSurface(p1));
      assertTrue(ramp3d.isInsideOrOnSurface(p2));

      ramp3d.applyTransform(transform);
      assertTrue(ramp3d.isInsideOrOnSurface(p1));
      assertFalse(ramp3d.isInsideOrOnSurface(p2));
   }

   @Test
   public void testIsInsideOrOnSurface()
   {
      // With default epsilon
      Ramp3D ramp3d = new Ramp3D(new RigidBodyTransform(), 1.0, 1.0, 1.0);
      assertFalse(ramp3d.isInsideOrOnSurface(new Point3D(new double[] {0.0, 0.0, 1.0})));
      assertFalse(ramp3d.isInsideOrOnSurface(new Point3D(new double[] {0.0, 5.0, 1.0})));
      assertFalse(ramp3d.isInsideOrOnSurface(new Point3D(new double[] {0.0, -5.0, 1.0})));
      assertTrue(ramp3d.isInsideOrOnSurface(new Point3D(new double[] {0.5, 0.0, 0.2})));
      assertTrue(ramp3d.isInsideOrOnSurface(new Point3D(new double[] {1.0, 0.3, 0.8})));

      // With finite epsilon
      assertTrue(ramp3d.isInsideEpsilon(new Point3D(new double[] {0.0, 0.0, 1.0}), 1.0 / Math.sqrt(2.0) + 0.001));
      assertFalse(ramp3d.isInsideEpsilon(new Point3D(new double[] {0.0, 0.0, 1.0}), 1.0 / Math.sqrt(2.0) - 0.001));

      // With default epsilon and translation
      RigidBodyTransform transform = new RigidBodyTransform();
      transform.setRotationYawAndZeroTranslation(0.75 * Math.PI);
      transform.setTranslation(new Vector3D(1.0, -1.0, 2.0));
   }

   @Test
   public void testProjectionPerpNormal()
   {
      int iterations = 1000;

      Random random = new Random(8462L);

      // make sure the projection of a point is perpendicular to the surface, when it is above the surface.
      // normal
      for (int i = 0; i < iterations; i++)
      {
         Ramp3D ramp = createRandomRamp(random);
         Vector3D surfaceNormal = new Vector3D();
         ramp.getRampSurfaceNormal(surfaceNormal);

         double insideRamp = Math.min(0.1, 0.1 * EuclidCoreTools.min(ramp.getHeight(), ramp.getLength(), ramp.getWidth()));
         double minX = insideRamp;
         double maxX = ramp.getLength() - insideRamp;
         double minY = -(ramp.getWidth() / 2.0) + insideRamp;
         double maxY = ramp.getWidth() / 2.0 - insideRamp;
         double minZ = 0.0;
         double maxZ = 1.0;

         Point3D pointToTestOnRamp = EuclidCoreRandomTools.nextPoint3D(random, minX, maxX, minY, maxY, minZ, maxZ);
         pointToTestOnRamp = transformFromAngledToWorldFrame(ramp, pointToTestOnRamp);
         ramp.orthogonalProjection(pointToTestOnRamp);

         Point3D rampOrigin = new Point3D();
         rampOrigin = transformFromAngledToWorldFrame(ramp, rampOrigin);

         Vector3D vectorFromOriginToPointOnRamp = new Vector3D(pointToTestOnRamp);
         vectorFromOriginToPointOnRamp.sub(rampOrigin);

         double dotProduct = vectorFromOriginToPointOnRamp.dot(surfaceNormal);
         if (Math.abs(dotProduct) > 1e-14)
         {
            System.out.println(" ramp = " + ramp);
            System.out.println(" pointToTestOnRamp = " + pointToTestOnRamp);
            System.out.println(" rampOrigin = " + rampOrigin);
            System.out.println(" vectorFromOriginToPointOnRamp = " + vectorFromOriginToPointOnRamp);
            System.out.println(" surfaceNormal = " + surfaceNormal);
         }
         assertEquals(0.0, dotProduct, 1e-14);
      }
   }

   /**
    * Ramp3d needs a little more work and the tests improve. It's hard to do really good surface normal
    * tests at the corners.
    */
   @Test
   public void testIsInsideOrOnSurfaceRandomOrientations()
   {
      int iterations = 1000;

      Random random = new Random(314159L);
      Ramp3D ramp = new Ramp3D(1.0, 1.0, 1.0);
      Point3D pointToTest = new Point3D();
      double rampLength;
      double epsilon;

      for (int i = 0; i < iterations; i++)
      {
         ramp = createRandomRamp(random);
         rampLength = Math.sqrt(EuclidCoreTools.normSquared(ramp.getLength(), ramp.getHeight()));
         epsilon = random.nextDouble();

         // z > 0 (in angled frame) means it's outside the ramp
         pointToTest.set(random.nextDouble(), random.nextDouble(), Math.abs(random.nextDouble()) + 2e-7);
         assertFalse(ramp.isInsideOrOnSurface(transformFromAngledToWorldFrame(ramp, pointToTest)));

         // negative x (in angled frame) means it's outside the ramp
         pointToTest.set(-Math.abs(random.nextDouble()) - 2e-7, random.nextDouble(), random.nextDouble());
         assertFalse(ramp.isInsideOrOnSurface(transformFromAngledToWorldFrame(ramp, pointToTest)));

         // x > ramp length (in angled frame) means it's outside the ramp
         pointToTest.set(Math.abs(random.nextDouble()) + rampLength + 2e-7, random.nextDouble(), random.nextDouble());
         assertFalse(ramp.isInsideOrOnSurface(transformFromAngledToWorldFrame(ramp, pointToTest)));

         // |y| > 0.5 * width (in both angled and ramp frames) means it's
         // outside the ramp
         pointToTest.set(random.nextDouble(), 0.5 * ramp.getWidth() + Math.abs(random.nextDouble()) + 2e-7, random.nextDouble());
         assertFalse(ramp.isInsideOrOnSurface(transformFromAngledToWorldFrame(ramp, pointToTest)));

         pointToTest.set(random.nextDouble(), -0.5 * ramp.getWidth() - Math.abs(random.nextDouble()) + 2e-7, random.nextDouble());
         assertFalse(ramp.isInsideOrOnSurface(transformFromAngledToWorldFrame(ramp, pointToTest)));

         // x < 0 (in ramp frame) means it's outside the ramp
         pointToTest.set(-random.nextDouble() - 2e-7, random.nextDouble(), random.nextDouble());
         ramp.transformToWorld(pointToTest);
         assertFalse(ramp.isInsideOrOnSurface(pointToTest));

         // x > length (in ramp frame) means it's outside the ramp
         pointToTest.set(Math.abs(random.nextDouble()) + ramp.getLength() + 2e-7, random.nextDouble(), random.nextDouble());
         ramp.transformToWorld(pointToTest);
         assertFalse(ramp.isInsideOrOnSurface(pointToTest));

         // z < 0 (in ramp frame) means it's outside the ramp
         pointToTest.set(random.nextDouble(), random.nextDouble(), -random.nextDouble() - 2e-7);
         ramp.transformToWorld(pointToTest);
         assertFalse(ramp.isInsideOrOnSurface(pointToTest));

         // z > height (in ramp frame) means it's outside the ramp
         pointToTest.set(random.nextDouble(), random.nextDouble(), Math.abs(random.nextDouble()) + ramp.getHeight() + 2e-7);
         ramp.transformToWorld(pointToTest);
         assertFalse(ramp.isInsideOrOnSurface(pointToTest));

         // points below the ramp surface (z < 0 in angled frame) are inside
         pointToTest.set(random.nextDouble() * rampLength, (random.nextDouble() - 0.5) * ramp.getWidth(), random.nextDouble() * epsilon);
         assertTrue(ramp.isInsideEpsilon(transformFromAngledToWorldFrame(ramp, pointToTest), epsilon));

         // points barely inside the side (y < 0.5*width in ramp frame and x and z so that they fit into the triangular sides)
         pointInsideRampSide(pointToTest, ramp, epsilon);
         assertTrue(ramp.isInsideEpsilon(pointToTest, epsilon));

         // points barely above the base (small positive z in ramp frame and x and z so that they fit into the rectangular sides) are inside
         pointToTest.set(random.nextDouble() * ramp.getLength(), 0.5 * random.nextDouble() * ramp.getWidth(), random.nextDouble() * epsilon);
         ramp.transformToWorld(pointToTest);
         assertTrue(ramp.isInsideEpsilon(pointToTest, epsilon));

         // points barely inside of the 'backboard', ie x = length
         pointToTest.set(random.nextDouble() * epsilon + ramp.getLength(), 0.5 * random.nextDouble() * ramp.getWidth(), random.nextDouble() * ramp.getHeight());
         ramp.transformToWorld(pointToTest);
         assertTrue(ramp.isInsideEpsilon(pointToTest, epsilon));
      }
   }

   /**
    * Ramp3d needs a little more work and the tests improve. It's hard to do really good surface normal
    * tests at the corners.
    */

   @Test
   public void testDistance()
   {
      int iterations = 1000;

      Random random = new Random(26535L);

      for (int i = 0; i < iterations; i++)
      {
         Ramp3D ramp = createRandomRamp(random);

         double insideRamp = 0.02;
         double x = EuclidCoreRandomTools.nextDouble(random, insideRamp, ramp.getRampLength() - insideRamp);
         double y = EuclidCoreRandomTools.nextDouble(random, -ramp.getWidth() / 2.0 + insideRamp, ramp.getWidth() / 2.0 - insideRamp);
         double z = EuclidCoreRandomTools.nextDouble(random, insideRamp, 1.0);
         Point3D pointToTestAboveRamp = new Point3D(x, y, z);
         Point3D pointOnRampBelowTestPoint = new Point3D(pointToTestAboveRamp);
         pointOnRampBelowTestPoint.setZ(0.0);
         double heightAboveRamp = pointToTestAboveRamp.getZ();

         pointToTestAboveRamp = transformFromAngledToWorldFrame(ramp, pointToTestAboveRamp);
         pointOnRampBelowTestPoint = transformFromAngledToWorldFrame(ramp, pointOnRampBelowTestPoint);

         double distanceUsingPoints = pointToTestAboveRamp.distance(pointOnRampBelowTestPoint);

         double distance = ramp.distance(pointToTestAboveRamp);

         if (Math.abs(distance - heightAboveRamp) > 1e-7)
         {
            System.out.println("distanceUsingPoints = " + distanceUsingPoints);
            System.out.println("distance = " + distance);
            System.out.println("heightAboveRamp = " + heightAboveRamp);

            distance = ramp.distance(pointToTestAboveRamp);

         }
         assertEquals(distance, heightAboveRamp, 1e-3);
      }
   }

   /**
    * Ramp3d needs a little more work and the tests improve. It's hard to do really good surface normal
    * tests at the corners.
    */
   @Test
   public void testGetClosestPointAndNormalAt()
   {
      int iterations = 1000;

      Random random = new Random(897932L);
      Point3D pointToPack = new Point3D();
      Vector3D normalToPack = new Vector3D();

      // points that can be projected directly (ie just set z=0 in angled frame) give surface normal
      for (int i = 0; i < iterations; i++)
      {
         Ramp3D ramp = createRandomRamp(random);
         printIfDebug("\nramp = " + ramp);

         double insideRamp = 0.02;
         double x = EuclidCoreRandomTools.nextDouble(random, insideRamp, ramp.getRampLength() - insideRamp);
         double y = EuclidCoreRandomTools.nextDouble(random, -ramp.getWidth() / 2.0 + insideRamp, ramp.getWidth() / 2.0 - insideRamp);
         double z = EuclidCoreRandomTools.nextDouble(random, insideRamp, 1.0);
         Point3D pointToTestAboveRamp = new Point3D(x, y, z);
         pointToTestAboveRamp = transformFromAngledToWorldFrame(ramp, pointToTestAboveRamp);

         printIfDebug("rampLength = " + ramp.getRampLength());
         printIfDebug("pointToTestAboveRamp = " + pointToTestAboveRamp);

         boolean isInside = ramp.checkIfInside(pointToTestAboveRamp, pointToPack, normalToPack);
         assertFalse(isInside);

         double distanceToPointToTest = ramp.distance(pointToTestAboveRamp);
         normalToPack.scale(distanceToPointToTest);

         Point3D pointOnRamp = new Point3D(pointToTestAboveRamp);
         pointOnRamp.sub(normalToPack);

         printIfDebug("pointOnRamp = " + pointOnRamp);
         double distanceToPointOnRamp = ramp.distance(pointOnRamp);
         printIfDebug("distanceToPointOnRamp = " + distanceToPointOnRamp);

         Point3D testPointOnRamp = new Point3D(pointToTestAboveRamp);
         ramp.orthogonalProjection(testPointOnRamp);

         Point3D testPointOnRampAgain = new Point3D(testPointOnRamp);
         ramp.orthogonalProjection(testPointOnRampAgain);
         printIfDebug("testPointOnRampAgain = " + testPointOnRampAgain);

         printIfDebug("testPointOnRamp = " + testPointOnRamp);
         double distanceToTestPointOnRamp = ramp.distance(testPointOnRamp);
         printIfDebug("distanceToTestPointOnRamp = " + distanceToTestPointOnRamp);

         EuclidCoreTestTools.assertTuple3DEquals(testPointOnRamp, pointOnRamp, 1e-7);

         printIfDebug("isInside = " + isInside);
         printIfDebug("distanceToPointToTest = " + distanceToPointToTest);

         assertEquals(Math.max(0.0, distanceToPointOnRamp), 0.0, 1e-7);
      }
   }

   @Test
   public void testIndependenceOfCopiedTransforms()
   {
      RigidBodyTransform transform = new RigidBodyTransform();
      transform.setRotationRollAndZeroTranslation(Math.PI / 6);
      Ramp3D ramp = new Ramp3D(transform, 1.0, 1.0, 1.0);

      Ramp3D rampCopy = new Ramp3D(ramp);
      RigidBodyTransform transformAppliedOnlyToCopy = new RigidBodyTransform();
      transformAppliedOnlyToCopy.setRotationPitchAndZeroTranslation(Math.PI / 4);
      rampCopy.applyTransform(transformAppliedOnlyToCopy);
      assertFalse(rampCopy.equals(ramp));

      Ramp3D rampCopyBySet = new Ramp3D(6.0, 5.0, 7.0);
      rampCopyBySet.set(ramp);
      RigidBodyTransform transformAppliedOnlyToCopyBySet = new RigidBodyTransform();
      transformAppliedOnlyToCopyBySet.setRotationYawAndZeroTranslation(Math.PI / 5);
      rampCopyBySet.applyTransform(transformAppliedOnlyToCopyBySet);
      assertFalse(rampCopyBySet.equals(ramp));
   }

   @Test
   public void testSetMethodSetsUpAllFieldsOfNewRampAccurately()
   {
      RigidBodyTransform transform = new RigidBodyTransform();
      transform.setRotationRollAndZeroTranslation(Math.PI / 6);
      Ramp3D ramp = new Ramp3D(transform, 3.0, 2.0, 4.0);
      Ramp3D rampCopyBySet = new Ramp3D(6.0, 5.0, 7.0);
      rampCopyBySet.set(ramp);
      Point3D pointProjectedOntoRamp = new Point3D(1.0, 0.0, 5.0);
      Point3D pointProjectedOntoRampCopy = new Point3D(pointProjectedOntoRamp);
      ramp.orthogonalProjection(pointProjectedOntoRamp);
      rampCopyBySet.orthogonalProjection(pointProjectedOntoRampCopy);
      assertEquals(pointProjectedOntoRamp, pointProjectedOntoRampCopy);
   }

   @Test
   public void testGeometricallyEquals()
   {
      Random random = new Random(34201L);
      Ramp3D firstRamp, secondRamp;
      double lengthX, widthY, heightZ;
      double epsilon = 1e-7;

      lengthX = random.nextDouble();
      widthY = random.nextDouble();
      heightZ = random.nextDouble();

      firstRamp = new Ramp3D(lengthX, widthY, heightZ);
      secondRamp = new Ramp3D(lengthX, widthY, heightZ);

      assertTrue(firstRamp.geometricallyEquals(secondRamp, epsilon));
      assertTrue(secondRamp.geometricallyEquals(firstRamp, epsilon));
      assertTrue(firstRamp.geometricallyEquals(firstRamp, epsilon));
      assertTrue(secondRamp.geometricallyEquals(secondRamp, epsilon));

      secondRamp = new Ramp3D(lengthX + epsilon * 0.99, widthY, heightZ);
      assertTrue(firstRamp.geometricallyEquals(secondRamp, epsilon));
      secondRamp = new Ramp3D(lengthX, widthY + epsilon * 0.99, heightZ);
      assertTrue(firstRamp.geometricallyEquals(secondRamp, epsilon));
      secondRamp = new Ramp3D(lengthX, widthY, heightZ + epsilon * 0.99);
      assertTrue(firstRamp.geometricallyEquals(secondRamp, epsilon));
      secondRamp = new Ramp3D(lengthX - epsilon * 0.99, widthY, heightZ);
      assertTrue(firstRamp.geometricallyEquals(secondRamp, epsilon));
      secondRamp = new Ramp3D(lengthX, widthY - epsilon * 0.99, heightZ);
      assertTrue(firstRamp.geometricallyEquals(secondRamp, epsilon));
      secondRamp = new Ramp3D(lengthX, widthY, heightZ - epsilon * 0.99);
      assertTrue(firstRamp.geometricallyEquals(secondRamp, epsilon));

      secondRamp = new Ramp3D(lengthX + epsilon * 1.01, widthY, heightZ);
      assertFalse(firstRamp.geometricallyEquals(secondRamp, epsilon));
      secondRamp = new Ramp3D(lengthX, widthY + epsilon * 1.01, heightZ);
      assertFalse(firstRamp.geometricallyEquals(secondRamp, epsilon));
      secondRamp = new Ramp3D(lengthX, widthY, heightZ + epsilon * 1.01);
      assertFalse(firstRamp.geometricallyEquals(secondRamp, epsilon));
      secondRamp = new Ramp3D(lengthX - epsilon * 1.01, widthY, heightZ);
      assertFalse(firstRamp.geometricallyEquals(secondRamp, epsilon));
      secondRamp = new Ramp3D(lengthX, widthY - epsilon * 1.01, heightZ);
      assertFalse(firstRamp.geometricallyEquals(secondRamp, epsilon));
      secondRamp = new Ramp3D(lengthX, widthY, heightZ - epsilon * 1.01);
      assertFalse(firstRamp.geometricallyEquals(secondRamp, epsilon));

      for (int i = 0; i < ITERATIONS; ++i)
      {
         Vector3D translationVector = EuclidCoreRandomTools.nextRotationVector(random);
         firstRamp = new Ramp3D(new RigidBodyTransform(new RotationMatrix(), translationVector), lengthX, widthY, heightZ);
         secondRamp = new Ramp3D(firstRamp);

         translationVector = EuclidCoreRandomTools.nextVector3DWithFixedLength(random, 0.99 * epsilon);
         secondRamp.appendTranslation(translationVector);

         assertTrue(firstRamp.geometricallyEquals(secondRamp, epsilon));

         secondRamp = new Ramp3D(firstRamp);

         translationVector = EuclidCoreRandomTools.nextVector3DWithFixedLength(random, 1.01 * epsilon);
         secondRamp.appendTranslation(translationVector);

         assertFalse(firstRamp.geometricallyEquals(secondRamp, epsilon));
      }

      for (int i = 0; i < ITERATIONS; ++i)
      {
         Quaternion rotation = EuclidCoreRandomTools.nextQuaternion(random);
         Point3D position = EuclidCoreRandomTools.nextPoint3D(random, 10.0);
         Pose3D pose = new Pose3D(position, rotation);
         firstRamp = new Ramp3D(pose, lengthX, widthY, heightZ);
         secondRamp = new Ramp3D(firstRamp);

         RigidBodyTransform transform = new RigidBodyTransform();
         AxisAngle axisAngle = new AxisAngle();

         axisAngle.set(EuclidCoreRandomTools.nextVector3DWithFixedLength(random, 1.0), 0.99 * epsilon);
         transform.setRotation(axisAngle);
         secondRamp.appendTransform(transform);
         assertTrue(firstRamp.geometricallyEquals(secondRamp, epsilon));

         secondRamp = new Ramp3D(firstRamp);

         axisAngle.set(EuclidCoreRandomTools.nextVector3DWithFixedLength(random, 1.0), 1.01 * epsilon);
         transform.setRotation(axisAngle);
         secondRamp.appendTransform(transform);
         assertFalse(firstRamp.geometricallyEquals(secondRamp, epsilon));
      }
   }

   private static Ramp3D createRandomRamp(Random random)
   {
      RigidBodyTransform configuration = createRandomTransform(random);

      double width = EuclidCoreRandomTools.nextDouble(random, 0.05, 1.0);
      double length = EuclidCoreRandomTools.nextDouble(random, 0.05, 1.0);
      double height = EuclidCoreRandomTools.nextDouble(random, 0.05, 1.0);

      return new Ramp3D(configuration, width, length, height);
   }

   private static RigidBodyTransform createRandomTransform(Random random)
   {
      RigidBodyTransform transformReturn = new RigidBodyTransform();
      RigidBodyTransform transformTemp = new RigidBodyTransform();

      transformReturn.setRotationRollAndZeroTranslation(random.nextDouble());
      transformTemp.setRotationPitchAndZeroTranslation(random.nextDouble());
      transformReturn.multiply(transformTemp);
      transformTemp.setRotationYawAndZeroTranslation(random.nextDouble());
      transformReturn.multiply(transformTemp);
      transformReturn.setTranslation(new Vector3D(random.nextDouble(), random.nextDouble(), random.nextDouble()));

      return transformReturn;
   }

   private static Point3D transformFromAngledToWorldFrame(Ramp3D ramp, Point3D point)
   {
      RigidBodyTransform angleTransform = new RigidBodyTransform();
      angleTransform.setRotationPitchAndZeroTranslation(-ramp.getRampIncline());
      angleTransform.transform(point);

      ramp.transformToWorld(point);

      return new Point3D(point);
   }

   private static void pointInsideRampSide(Point3D pointToPack, Ramp3D ramp, double epsilon)
   {
      Random random = new Random(97932L);

      double xVal = random.nextDouble() * ramp.getLength();
      double yVal = (2 * random.nextInt(1) - 1) * 0.5 * ramp.getWidth() + random.nextDouble() * epsilon;
      double zVal = random.nextDouble() * ramp.getHeight();

      if (zVal > xVal * (ramp.getHeight() / ramp.getLength()))
         zVal = xVal * (ramp.getHeight() / ramp.getLength());

      pointToPack.set(xVal, yVal, zVal);
      ramp.transformToWorld(pointToPack);
   }

   private void printIfDebug(String string)
   {
      if (DEBUG)
         System.out.println(string);
   }

}
