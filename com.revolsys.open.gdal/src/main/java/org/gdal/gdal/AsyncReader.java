/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.8
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.gdal.gdal;

public class AsyncReader {
  protected static long getCPtr(final AsyncReader obj) {
    return obj == null ? 0 : obj.swigCPtr;
  }

  protected static long getCPtrAndDisown(final AsyncReader obj) {
    if (obj != null) {
      obj.swigCMemOwn = false;
    }
    return getCPtr(obj);
  }

  private long swigCPtr;

  protected boolean swigCMemOwn;

  protected AsyncReader(final long cPtr, final boolean cMemoryOwn) {
    if (cPtr == 0) {
      throw new RuntimeException();
    }
    this.swigCMemOwn = cMemoryOwn;
    this.swigCPtr = cPtr;
  }

  /* Ensure that the GC doesn't collect any parent instance set from Java */
  protected void addReference(final Object reference) {
  }

  public synchronized void delete() {
    if (this.swigCPtr != 0) {
      if (this.swigCMemOwn) {
        this.swigCMemOwn = false;
        gdalJNI.delete_AsyncReader(this.swigCPtr);
      }
      this.swigCPtr = 0;
    }
  }

  @Override
  public boolean equals(final Object obj) {
    boolean equal = false;
    if (obj instanceof AsyncReader) {
      equal = ((AsyncReader)obj).swigCPtr == this.swigCPtr;
    }
    return equal;
  }

  @Override
  protected void finalize() {
    delete();
  }

  public int GetNextUpdatedRegion(final double timeout, final int[] xoff, final int[] yoff,
    final int[] buf_xsize, final int[] buf_ysize) {
    return gdalJNI.AsyncReader_GetNextUpdatedRegion(this.swigCPtr, this, timeout, xoff, yoff,
      buf_xsize, buf_ysize);
  }

  @Override
  public int hashCode() {
    return (int)this.swigCPtr;
  }

  public int LockBuffer(final double timeout) {
    return gdalJNI.AsyncReader_LockBuffer(this.swigCPtr, this, timeout);
  }

  public void UnlockBuffer() {
    gdalJNI.AsyncReader_UnlockBuffer(this.swigCPtr, this);
  }

}
