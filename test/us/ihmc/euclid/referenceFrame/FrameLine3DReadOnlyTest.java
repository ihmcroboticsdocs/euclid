package us.ihmc.euclid.referenceFrame;

import org.junit.Test;
import us.ihmc.euclid.geometry.interfaces.Line3DReadOnly;
import us.ihmc.euclid.geometry.tools.EuclidGeometryRandomTools;
import us.ihmc.euclid.referenceFrame.interfaces.FrameLine3DReadOnly;
import us.ihmc.euclid.referenceFrame.tools.EuclidFrameAPITestTools;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.function.Predicate;

public abstract class FrameLine3DReadOnlyTest<T extends FrameLine3DReadOnly>
{
   public abstract T createFrameLine(ReferenceFrame referenceFrame, Line3DReadOnly line);

   public final T createRandomLine(Random random)
   {
      return createRandomFrameLine(random, ReferenceFrame.getWorldFrame());
   }

   public final T createRandomFrameLine(Random random, ReferenceFrame referenceFrame)
   {
      return createFrameLine(referenceFrame, EuclidGeometryRandomTools.nextLine3D(random));
   }

   @Test
   public void testOverloading() throws Exception
   {
      EuclidFrameAPITestTools.assertOverloadingWithFrameObjects(FrameLine3DReadOnly.class, Line3DReadOnly.class, true);
   }

   @Test
   public void testReferenceFrameChecks() throws Throwable
   {
      Random random = new Random(234);
      Predicate<Method> methodFilter = m -> !m.getName().contains("IncludingFrame") && !m.getName().equals("equals") && !m.getName().equals("epsilonEquals");
      EuclidFrameAPITestTools.assertMethodsOfReferenceFrameHolderCheckReferenceFrame(frame -> createRandomFrameLine(random, frame), false, true,
                                                                                     methodFilter);
   }
}
