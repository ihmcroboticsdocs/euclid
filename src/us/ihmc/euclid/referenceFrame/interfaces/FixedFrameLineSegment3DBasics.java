package us.ihmc.euclid.referenceFrame.interfaces;

import us.ihmc.euclid.geometry.interfaces.LineSegment3DBasics;
import us.ihmc.euclid.referenceFrame.exceptions.ReferenceFrameMismatchException;
import us.ihmc.euclid.tuple3D.interfaces.Point3DReadOnly;
import us.ihmc.euclid.tuple3D.interfaces.Vector3DReadOnly;

public interface FixedFrameLineSegment3DBasics extends FrameLineSegment3DReadOnly, LineSegment3DBasics
{
   /**
    * Gets the reference to the first endpoint of this line segment.
    *
    * @return the reference to the first endpoint of this line segment.
    */
   FixedFramePoint3DBasics getFirstEndpoint();

   /**
    * Gets the reference to the second endpoint of this line segment.
    *
    * @return the reference to the second endpoint of this line segment.
    */
   FixedFramePoint3DBasics getSecondEndpoint();

   /**
    * Changes the first endpoint of this line segment.
    *
    * @param firstEndpoint new endpoint of this line segment. Not modified.
    * @throws ReferenceFrameMismatchException if {@code this} and {@code firstEndpoint} are not
    *            expressed in the same reference frame.
    */
   default void setFirstEndpoint(FramePoint3DReadOnly firstEndpoint)
   {
      getFirstEndpoint().set(firstEndpoint);
   }

   /**
    * Changes the second endpoint of this line segment.
    *
    * @param secondEndpoint new second endpoint of this line segment. Not modified.
    * @throws ReferenceFrameMismatchException if {@code this} and {@code secondEndpoint} are not
    *            expressed in the same reference frame.
    */
   default void setSecondEndpoint(FramePoint3DReadOnly secondEndpoint)
   {
      getSecondEndpoint().set(secondEndpoint);
   }

   /**
    * Sets this line segment to be same as the given line segment.
    *
    * @param other the other line segment to copy. Not modified.
    * @throws ReferenceFrameMismatchException if {@code this} and {@code other} are not expressed in
    *            the same reference frame.
    */
   default void set(FrameLineSegment3DReadOnly other)
   {
      checkReferenceFrameMatch(other);
      LineSegment3DBasics.super.set(other.getFirstEndpoint(), other.getSecondEndpoint());
   }

   /**
    * Redefines this line segment with new endpoints.
    *
    * @param firstEndpoint new endpoint of this line segment. Not modified
    * @param secondEndpoint new second endpoint of this line segment. Not modified.
    * @throws ReferenceFrameMismatchException if {@code this} and {@code firstEndpoint} are not
    *            expressed in the same reference frame.
    */
   default void set(FramePoint3DReadOnly firstEndpoint, Point3DReadOnly secondEndpoint)
   {
      checkReferenceFrameMatch(firstEndpoint);
      LineSegment3DBasics.super.set(firstEndpoint, secondEndpoint);
   }

   /**
    * Redefines this line segment with new endpoints.
    *
    * @param firstEndpoint new endpoint of this line segment. Not modified
    * @param secondEndpoint new second endpoint of this line segment. Not modified.
    * @throws ReferenceFrameMismatchException if {@code this} and {@code secondEndpoint} are not
    *            expressed in the same reference frame.
    */
   default void set(Point3DReadOnly firstEndpoint, FramePoint3DReadOnly secondEndpoint)
   {
      checkReferenceFrameMatch(secondEndpoint);
      LineSegment3DBasics.super.set(firstEndpoint, secondEndpoint);
   }

   /**
    * Redefines this line segment with new endpoints.
    *
    * @param firstEndpoint new endpoint of this line segment. Not modified
    * @param secondEndpoint new second endpoint of this line segment. Not modified.
    * @throws ReferenceFrameMismatchException if {@code this}, {@code firstEndpoint}, and
    *            {@code secondEndpoint} are not expressed in the same reference frame.
    */
   default void set(FramePoint3DReadOnly firstEndpoint, FramePoint3DReadOnly secondEndpoint)
   {
      checkReferenceFrameMatch(firstEndpoint);
      checkReferenceFrameMatch(secondEndpoint);
      LineSegment3DBasics.super.set(firstEndpoint, secondEndpoint);
   }

   /**
    * Redefines this line segment with a new first endpoint and a vector going from the first to the
    * second endpoint.
    *
    * @param firstEndpoint new first endpoint. Not modified.
    * @param fromFirstToSecondEndpoint vector going from the first to the second endpoint. Not
    *           modified.
    * @throws ReferenceFrameMismatchException if {@code this} and {@code firstEndpoint} are not
    *            expressed in the same reference frame.
    */
   default void set(FramePoint3DReadOnly firstEndpoint, Vector3DReadOnly fromFirstToSecondEndpoint)
   {
      checkReferenceFrameMatch(firstEndpoint);
      LineSegment3DBasics.super.set(firstEndpoint, fromFirstToSecondEndpoint);
   }

   /**
    * Redefines this line segment with a new first endpoint and a vector going from the first to the
    * second endpoint.
    *
    * @param firstEndpoint new first endpoint. Not modified.
    * @param fromFirstToSecondEndpoint vector going from the first to the second endpoint. Not
    *           modified.
    * @throws ReferenceFrameMismatchException if {@code this} and {@code fromFirstToSecondEndpoint}
    *            are not expressed in the same reference frame.
    */
   default void set(Point3DReadOnly firstEndpoint, FrameVector3DReadOnly fromFirstToSecondEndpoint)
   {
      checkReferenceFrameMatch(fromFirstToSecondEndpoint);
      LineSegment3DBasics.super.set(firstEndpoint, fromFirstToSecondEndpoint);
   }

   /**
    * Redefines this line segment with a new first endpoint and a vector going from the first to the
    * second endpoint.
    *
    * @param firstEndpoint new first endpoint. Not modified.
    * @param fromFirstToSecondEndpoint vector going from the first to the second endpoint. Not
    *           modified.
    * @throws ReferenceFrameMismatchException if {@code this}, {@code firstEndpoint}, and
    *            {@code fromFirstToSecondEndpoint} are not expressed in the same reference frame.
    */
   default void set(FramePoint3DReadOnly firstEndpoint, FrameVector3DReadOnly fromFirstToSecondEndpoint)
   {
      checkReferenceFrameMatch(firstEndpoint);
      checkReferenceFrameMatch(fromFirstToSecondEndpoint);
      LineSegment3DBasics.super.set(firstEndpoint, fromFirstToSecondEndpoint);
   }

   /**
    * Translates this line segment by the given (x, y, z) contained in {@code translation}.
    * <p>
    * Note that the length and direction of this line segment remains unchanged.
    * </p>
    *
    * @param translation the translation to add to each endpoint of this line segment. Not modified.
    * @throws ReferenceFrameMismatchException if {@code this} and {@code translation}
    *            are not expressed in the same reference frame.
    */
   default void translate(FrameTuple3DReadOnly translation)
   {
      checkReferenceFrameMatch(translation);
      LineSegment3DBasics.super.translate(translation);
   }
}
