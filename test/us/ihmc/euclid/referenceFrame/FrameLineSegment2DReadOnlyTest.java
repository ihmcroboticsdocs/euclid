package us.ihmc.euclid.referenceFrame;

import org.junit.Test;
import us.ihmc.euclid.geometry.interfaces.LineSegment2DReadOnly;
import us.ihmc.euclid.geometry.tools.EuclidGeometryRandomTools;
import us.ihmc.euclid.referenceFrame.interfaces.FrameLineSegment2DReadOnly;
import us.ihmc.euclid.referenceFrame.tools.EuclidFrameAPITestTools;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.function.Predicate;

public abstract class FrameLineSegment2DReadOnlyTest<T extends FrameLineSegment2DReadOnly>
{
   public abstract T createFrameLineSegment(ReferenceFrame referenceFrame, LineSegment2DReadOnly segment);

   public final T createRandomLineSegment(Random random)
   {
      return createRandomFrameLineSegment(random, ReferenceFrame.getWorldFrame());
   }

   public final T createRandomFrameLineSegment(Random random, ReferenceFrame referenceFrame)
   {
      return createFrameLineSegment(referenceFrame, EuclidGeometryRandomTools.nextLineSegment2D(random));
   }

   @Test
   public void testOverloading() throws Exception
   {
      EuclidFrameAPITestTools.assertOverloadingWithFrameObjects(FrameLineSegment2DReadOnly.class, LineSegment2DReadOnly.class, true);
   }

   @Test
   public void testReferenceFrameChecks() throws Throwable
   {
      Random random = new Random(234);
      Predicate<Method> methodFilter = m -> !m.getName().contains("IncludingFrame") && !m.getName().equals("equals") && !m.getName().equals("epsilonEquals");
      EuclidFrameAPITestTools.assertMethodsOfReferenceFrameHolderCheckReferenceFrame(frame -> createRandomFrameLineSegment(random, frame), false, true,
                                                                                     methodFilter);
   }
}
