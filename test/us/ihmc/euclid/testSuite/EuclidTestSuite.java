package us.ihmc.euclid.testSuite;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.pitest.mutationtest.commandline.MutationCoverageReport;

import us.ihmc.euclid.AxisTest;
import us.ihmc.euclid.geometry.BoundingBox2DTest;
import us.ihmc.euclid.geometry.BoundingBox3DTest;
import us.ihmc.euclid.geometry.Box3DTest;
import us.ihmc.euclid.geometry.ConvexPolygon2DTest;
import us.ihmc.euclid.geometry.Cylinder3DTest;
import us.ihmc.euclid.geometry.Ellipsoid3DTest;
import us.ihmc.euclid.geometry.Line2DTest;
import us.ihmc.euclid.geometry.Line3DTest;
import us.ihmc.euclid.geometry.LineSegment1DTest;
import us.ihmc.euclid.geometry.LineSegment2DTest;
import us.ihmc.euclid.geometry.LineSegment3DTest;
import us.ihmc.euclid.geometry.Orientation2DTest;
import us.ihmc.euclid.geometry.Plane3DTest;
import us.ihmc.euclid.geometry.Pose2DTest;
import us.ihmc.euclid.geometry.Pose3DTest;
import us.ihmc.euclid.geometry.Ramp3DTest;
import us.ihmc.euclid.geometry.Sphere3DTest;
import us.ihmc.euclid.geometry.Torus3DTest;
import us.ihmc.euclid.geometry.exceptions.BoundingBoxExceptionTest;
import us.ihmc.euclid.geometry.tools.EuclidGeometryPolygonToolsTest;
import us.ihmc.euclid.geometry.tools.EuclidGeometryTestToolsTest;
import us.ihmc.euclid.geometry.tools.EuclidGeometryToolsTest;
import us.ihmc.euclid.referenceFrame.FrameLine2DTest;
import us.ihmc.euclid.referenceFrame.FrameLine3DTest;
import us.ihmc.euclid.referenceFrame.FrameLineSegment2DTest;
import us.ihmc.euclid.referenceFrame.FrameLineSegment3DTest;
import us.ihmc.euclid.referenceFrame.FrameOrientation2DTest;
import us.ihmc.euclid.referenceFrame.FramePoint2DTest;
import us.ihmc.euclid.referenceFrame.FramePoint3DTest;
import us.ihmc.euclid.referenceFrame.FramePose2DTest;
import us.ihmc.euclid.referenceFrame.FramePose3DTest;
import us.ihmc.euclid.referenceFrame.FrameQuaternionTest;
import us.ihmc.euclid.referenceFrame.FrameVector2DTest;
import us.ihmc.euclid.referenceFrame.FrameVector3DTest;
import us.ihmc.euclid.referenceFrame.FrameVector4DTest;
import us.ihmc.euclid.referenceFrame.ReferenceFrameTest;
import us.ihmc.euclid.referenceFrame.tools.EuclidFrameTestToolsTest;
import us.ihmc.euclid.referenceFrame.tools.EuclidFrameToolsTest;
import us.ihmc.euclid.utils.NameBasedHashCodeToolsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
      // Geometry class tests
      // 1D
      LineSegment1DTest.class,
      // 2D
      Line2DTest.class, LineSegment2DTest.class, ConvexPolygon2DTest.class, BoundingBox2DTest.class, Orientation2DTest.class, Pose2DTest.class,
      // 3D
      Line3DTest.class, LineSegment3DTest.class, BoundingBox3DTest.class, Box3DTest.class, Cylinder3DTest.class, Ellipsoid3DTest.class, Ramp3DTest.class,
      Sphere3DTest.class, Torus3DTest.class, Pose3DTest.class, Plane3DTest.class,
      // Tools tests
      EuclidGeometryToolsTest.class, EuclidGeometryPolygonToolsTest.class, EuclidGeometryTestToolsTest.class,
      // Reference frame framework tests
      ReferenceFrameTest.class,
      // 1D
      // 2D
      FramePoint2DTest.class, FrameVector2DTest.class, FramePose2DTest.class, FrameOrientation2DTest.class, FrameLineSegment2DTest.class, FrameLine2DTest.class,
      // 3D
      FramePoint3DTest.class, FrameVector3DTest.class, FramePose3DTest.class, FrameLineSegment3DTest.class, FrameLine3DTest.class,
      // 4D
      FrameQuaternionTest.class, FrameVector4DTest.class,
      // Tools tests
      EuclidFrameToolsTest.class, NameBasedHashCodeToolsTest.class, EuclidFrameTestToolsTest.class,
      // Exceptions tests
      BoundingBoxExceptionTest.class,
      // Axis test
      AxisTest.class})

public class EuclidTestSuite
{
   public static void main(String[] args) throws URISyntaxException, IOException
   {
      String targetTests = EuclidTestSuite.class.getName();
      String targetClasses = "us.ihmc.euclid.*";
      doPITMutationTestAndOpenResult(targetTests, targetClasses);
   }

   public static void doPITMutationTestAndOpenResult(String targetTests, String targetClasses)
   {
      String reportDirectoryName = "pit-reports";
      MutationCoverageReport.main(new String[] {"--reportDir", reportDirectoryName, "--targetClasses", targetClasses, "--targetTests", targetTests,
            "--sourceDirs", "src,test"});

      File reportDirectory = new File(reportDirectoryName);
      if (reportDirectory.isDirectory() && reportDirectory.exists())
      {
         String[] list = reportDirectory.list();
         String lastDirectoryName = list[list.length - 1];

         System.out.println("Found last directory " + lastDirectoryName);

         File reportFile = new File(reportDirectory, lastDirectoryName + "/index.html");
         String absolutePath;
         try
         {
            absolutePath = reportFile.getCanonicalPath();

            absolutePath = absolutePath.replace("\\", "/");
            System.out.println("Opening " + "file://" + absolutePath);

            URI uri = new URI("file://" + absolutePath);
            Desktop.getDesktop().browse(uri);
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         catch (URISyntaxException e)
         {
            e.printStackTrace();
         }
      }
   }
}