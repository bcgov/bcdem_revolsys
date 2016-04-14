/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.8
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.gdal.osr;

public class CoordinateTransformation {
  /* New in GDAL 1.10 */
  public static CoordinateTransformation CreateCoordinateTransformation(final SpatialReference src,
    final SpatialReference dst) {
    return osr.CreateCoordinateTransformation(src, dst);
  }

  public static long getCPtr(final CoordinateTransformation obj) {
    return obj == null ? 0 : obj.swigCPtr;
  }

  private long swigCPtr;

  protected boolean swigCMemOwn;

  public CoordinateTransformation(final long cPtr, final boolean cMemoryOwn) {
    this.swigCMemOwn = cMemoryOwn;
    this.swigCPtr = cPtr;
  }

  public CoordinateTransformation(final SpatialReference src, final SpatialReference dst) {
    this(osrJNI.new_CoordinateTransformation(SpatialReference.getCPtr(src), src,
      SpatialReference.getCPtr(dst), dst), true);
  }

  public synchronized void delete() {
    if (this.swigCPtr != 0) {
      if (this.swigCMemOwn) {
        this.swigCMemOwn = false;
        osrJNI.delete_CoordinateTransformation(this.swigCPtr);
      }
      this.swigCPtr = 0;
    }
  }

  @Override
  protected void finalize() {
    delete();
  }

  public double[] TransformPoint(final double x, final double y) {
    return TransformPoint(x, y, 0);
  }

  public double[] TransformPoint(final double x, final double y, final double z) {
    final double[] ret = new double[3];
    TransformPoint(ret, x, y, z);
    return ret;
  }

  public void TransformPoint(final double[] inout) {
    osrJNI.CoordinateTransformation_TransformPoint__SWIG_0(this.swigCPtr, this, inout);
  }

  public void TransformPoint(final double[] argout, final double x, final double y) {
    osrJNI.CoordinateTransformation_TransformPoint__SWIG_2(this.swigCPtr, this, argout, x, y);
  }

  public void TransformPoint(final double[] argout, final double x, final double y,
    final double z) {
    osrJNI.CoordinateTransformation_TransformPoint__SWIG_1(this.swigCPtr, this, argout, x, y, z);
  }

  public void TransformPoints(final double[][] nCount) {
    osrJNI.CoordinateTransformation_TransformPoints(this.swigCPtr, this, nCount);
  }

}
