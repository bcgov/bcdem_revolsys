/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.8
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.gdal.gdal;

public class Dataset extends MajorObject {
  protected static long getCPtr(final Dataset obj) {
    return obj == null ? 0 : obj.swigCPtr;
  }

  private transient long swigCPtr;

  protected Dataset(final long cPtr, final boolean cMemoryOwn) {
    super(gdalJNI.Dataset_SWIGUpcast(cPtr), cMemoryOwn);
    this.swigCPtr = cPtr;
  }

  public int AddBand() {
    return gdalJNI.Dataset_AddBand__SWIG_2(this.swigCPtr, this);
  }

  public int AddBand(final int datatype) {
    return gdalJNI.Dataset_AddBand__SWIG_1(this.swigCPtr, this, datatype);
  }

  public int AddBand(final int datatype, final java.util.Vector options) {
    return gdalJNI.Dataset_AddBand__SWIG_0(this.swigCPtr, this, datatype, options);
  }

  public int BuildOverviews(final int[] overviewlist) {
    return BuildOverviews(null, overviewlist, null);
  }

  public int BuildOverviews(final int[] overviewlist, final ProgressCallback callback) {
    return BuildOverviews(null, overviewlist, callback);
  }

  public int BuildOverviews(final String resampling, final int[] overviewlist) {
    return gdalJNI.Dataset_BuildOverviews__SWIG_2(this.swigCPtr, this, resampling, overviewlist);
  }

  public int BuildOverviews(final String resampling, final int[] overviewlist,
    final ProgressCallback callback) {
    return gdalJNI.Dataset_BuildOverviews__SWIG_0(this.swigCPtr, this, resampling, overviewlist,
      callback);
  }

  public int CreateMaskBand(final int nFlags) {
    return gdalJNI.Dataset_CreateMaskBand(this.swigCPtr, this, nFlags);
  }

  @Override
  public synchronized void delete() {
    if (this.swigCPtr != 0) {
      if (this.swigCMemOwn) {
        this.swigCMemOwn = false;
        gdalJNI.delete_Dataset(this.swigCPtr);
      }
      this.swigCPtr = 0;
    }
    super.delete();
  }

  @Override
  protected void finalize() {
    delete();
  }

  public void FlushCache() {
    gdalJNI.Dataset_FlushCache(this.swigCPtr, this);
  }

  public Driver GetDriver() {
    final long cPtr = gdalJNI.Dataset_GetDriver(this.swigCPtr, this);
    return cPtr == 0 ? null : new Driver(cPtr, false);
  }

  public java.util.Vector GetFileList() {
    return gdalJNI.Dataset_GetFileList(this.swigCPtr, this);
  }

  public int GetGCPCount() {
    return gdalJNI.Dataset_GetGCPCount(this.swigCPtr, this);
  }

  public String GetGCPProjection() {
    return gdalJNI.Dataset_GetGCPProjection(this.swigCPtr, this);
  }

  public java.util.Vector GetGCPs() {
    final java.util.Vector gcps = new java.util.Vector();
    GetGCPs(gcps);
    return gcps;
  }

  public void GetGCPs(final java.util.Vector nGCPs) {
    gdalJNI.Dataset_GetGCPs(this.swigCPtr, this, nGCPs);
  }

  public double[] GetGeoTransform() {
    final double adfGeoTransform[] = new double[6];
    GetGeoTransform(adfGeoTransform);
    return adfGeoTransform;
  }

  public void GetGeoTransform(final double[] argout) {
    gdalJNI.Dataset_GetGeoTransform(this.swigCPtr, this, argout);
  }

  public String GetProjection() {
    return gdalJNI.Dataset_GetProjection(this.swigCPtr, this);
  }

  public String GetProjectionRef() {
    return gdalJNI.Dataset_GetProjectionRef(this.swigCPtr, this);
  }

  public Band GetRasterBand(final int nBand) {
    final long cPtr = gdalJNI.Dataset_GetRasterBand(this.swigCPtr, this, nBand);
    Band ret = null;
    if (cPtr != 0) {
      ret = new Band(cPtr, false);
      ret.addReference(this);
    }
    return ret;
  }

  public int getRasterCount() {
    return gdalJNI.Dataset_RasterCount_get(this.swigCPtr, this);
  }

  // Preferred name to match C++ API
  public int GetRasterCount() {
    return getRasterCount();
  }

  public int getRasterXSize() {
    return gdalJNI.Dataset_RasterXSize_get(this.swigCPtr, this);
  }

  // Preferred name to match C++ API
  public int GetRasterXSize() {
    return getRasterXSize();
  }

  public int getRasterYSize() {
    return gdalJNI.Dataset_RasterYSize_get(this.swigCPtr, this);
  }

  // Preferred name to match C++ API
  public int GetRasterYSize() {
    return getRasterYSize();
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final byte[] regularArrayOut,
    final int[] band_list) {
    return gdalJNI.Dataset_ReadRaster__SWIG_3(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final byte[] regularArrayOut,
    final int[] band_list, final int nPixelSpace) {
    return gdalJNI.Dataset_ReadRaster__SWIG_2(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list, nPixelSpace);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final byte[] regularArrayOut,
    final int[] band_list, final int nPixelSpace, final int nLineSpace) {
    return gdalJNI.Dataset_ReadRaster__SWIG_1(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list, nPixelSpace, nLineSpace);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final byte[] regularArrayOut,
    final int[] band_list, final int nPixelSpace, final int nLineSpace, final int nBandSpace) {
    return gdalJNI.Dataset_ReadRaster__SWIG_0(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list, nPixelSpace, nLineSpace,
      nBandSpace);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final double[] regularArrayOut,
    final int[] band_list) {
    return gdalJNI.Dataset_ReadRaster__SWIG_19(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final double[] regularArrayOut,
    final int[] band_list, final int nPixelSpace) {
    return gdalJNI.Dataset_ReadRaster__SWIG_18(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list, nPixelSpace);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final double[] regularArrayOut,
    final int[] band_list, final int nPixelSpace, final int nLineSpace) {
    return gdalJNI.Dataset_ReadRaster__SWIG_17(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list, nPixelSpace, nLineSpace);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final double[] regularArrayOut,
    final int[] band_list, final int nPixelSpace, final int nLineSpace, final int nBandSpace) {
    return gdalJNI.Dataset_ReadRaster__SWIG_16(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list, nPixelSpace, nLineSpace,
      nBandSpace);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final float[] regularArrayOut,
    final int[] band_list) {
    return gdalJNI.Dataset_ReadRaster__SWIG_15(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final float[] regularArrayOut,
    final int[] band_list, final int nPixelSpace) {
    return gdalJNI.Dataset_ReadRaster__SWIG_14(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list, nPixelSpace);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final float[] regularArrayOut,
    final int[] band_list, final int nPixelSpace, final int nLineSpace) {
    return gdalJNI.Dataset_ReadRaster__SWIG_13(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list, nPixelSpace, nLineSpace);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final float[] regularArrayOut,
    final int[] band_list, final int nPixelSpace, final int nLineSpace, final int nBandSpace) {
    return gdalJNI.Dataset_ReadRaster__SWIG_12(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list, nPixelSpace, nLineSpace,
      nBandSpace);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final int[] regularArrayOut,
    final int[] band_list) {
    return gdalJNI.Dataset_ReadRaster__SWIG_11(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final int[] regularArrayOut,
    final int[] band_list, final int nPixelSpace) {
    return gdalJNI.Dataset_ReadRaster__SWIG_10(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list, nPixelSpace);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final int[] regularArrayOut,
    final int[] band_list, final int nPixelSpace, final int nLineSpace) {
    return gdalJNI.Dataset_ReadRaster__SWIG_9(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list, nPixelSpace, nLineSpace);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final int[] regularArrayOut,
    final int[] band_list, final int nPixelSpace, final int nLineSpace, final int nBandSpace) {
    return gdalJNI.Dataset_ReadRaster__SWIG_8(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list, nPixelSpace, nLineSpace,
      nBandSpace);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final short[] regularArrayOut,
    final int[] band_list) {
    return gdalJNI.Dataset_ReadRaster__SWIG_7(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final short[] regularArrayOut,
    final int[] band_list, final int nPixelSpace) {
    return gdalJNI.Dataset_ReadRaster__SWIG_6(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list, nPixelSpace);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final short[] regularArrayOut,
    final int[] band_list, final int nPixelSpace, final int nLineSpace) {
    return gdalJNI.Dataset_ReadRaster__SWIG_5(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list, nPixelSpace, nLineSpace);
  }

  public int ReadRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final short[] regularArrayOut,
    final int[] band_list, final int nPixelSpace, final int nLineSpace, final int nBandSpace) {
    return gdalJNI.Dataset_ReadRaster__SWIG_4(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayOut, band_list, nPixelSpace, nLineSpace,
      nBandSpace);
  }

  public int ReadRaster_Direct(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type,
    final java.nio.ByteBuffer nioBuffer, final int[] band_list) {
    return gdalJNI.Dataset_ReadRaster_Direct__SWIG_3(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, nioBuffer, band_list);
  }

  public int ReadRaster_Direct(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type,
    final java.nio.ByteBuffer nioBuffer, final int[] band_list, final int nPixelSpace) {
    return gdalJNI.Dataset_ReadRaster_Direct__SWIG_2(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, nioBuffer, band_list, nPixelSpace);
  }

  public int ReadRaster_Direct(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type,
    final java.nio.ByteBuffer nioBuffer, final int[] band_list, final int nPixelSpace,
    final int nLineSpace) {
    return gdalJNI.Dataset_ReadRaster_Direct__SWIG_1(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, nioBuffer, band_list, nPixelSpace, nLineSpace);
  }

  public int ReadRaster_Direct(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type,
    final java.nio.ByteBuffer nioBuffer, final int[] band_list, final int nPixelSpace,
    final int nLineSpace, final int nBandSpace) {
    return gdalJNI.Dataset_ReadRaster_Direct__SWIG_0(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, nioBuffer, band_list, nPixelSpace, nLineSpace, nBandSpace);
  }

  public int SetGCPs(final GCP[] nGCPs, final String pszGCPProjection) {
    return gdalJNI.Dataset_SetGCPs(this.swigCPtr, this, nGCPs, pszGCPProjection);
  }

  public int SetGeoTransform(final double[] argin) {
    return gdalJNI.Dataset_SetGeoTransform(this.swigCPtr, this, argin);
  }

  public int SetProjection(final String prj) {
    return gdalJNI.Dataset_SetProjection(this.swigCPtr, this, prj);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final byte[] regularArrayIn,
    final int[] band_list) {
    return gdalJNI.Dataset_WriteRaster__SWIG_3(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final byte[] regularArrayIn,
    final int[] band_list, final int nPixelSpace) {
    return gdalJNI.Dataset_WriteRaster__SWIG_2(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list, nPixelSpace);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final byte[] regularArrayIn,
    final int[] band_list, final int nPixelSpace, final int nLineSpace) {
    return gdalJNI.Dataset_WriteRaster__SWIG_1(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list, nPixelSpace, nLineSpace);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final byte[] regularArrayIn,
    final int[] band_list, final int nPixelSpace, final int nLineSpace, final int nBandSpace) {
    return gdalJNI.Dataset_WriteRaster__SWIG_0(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list, nPixelSpace, nLineSpace,
      nBandSpace);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final double[] regularArrayIn,
    final int[] band_list) {
    return gdalJNI.Dataset_WriteRaster__SWIG_19(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final double[] regularArrayIn,
    final int[] band_list, final int nPixelSpace) {
    return gdalJNI.Dataset_WriteRaster__SWIG_18(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list, nPixelSpace);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final double[] regularArrayIn,
    final int[] band_list, final int nPixelSpace, final int nLineSpace) {
    return gdalJNI.Dataset_WriteRaster__SWIG_17(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list, nPixelSpace, nLineSpace);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final double[] regularArrayIn,
    final int[] band_list, final int nPixelSpace, final int nLineSpace, final int nBandSpace) {
    return gdalJNI.Dataset_WriteRaster__SWIG_16(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list, nPixelSpace, nLineSpace,
      nBandSpace);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final float[] regularArrayIn,
    final int[] band_list) {
    return gdalJNI.Dataset_WriteRaster__SWIG_15(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final float[] regularArrayIn,
    final int[] band_list, final int nPixelSpace) {
    return gdalJNI.Dataset_WriteRaster__SWIG_14(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list, nPixelSpace);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final float[] regularArrayIn,
    final int[] band_list, final int nPixelSpace, final int nLineSpace) {
    return gdalJNI.Dataset_WriteRaster__SWIG_13(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list, nPixelSpace, nLineSpace);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final float[] regularArrayIn,
    final int[] band_list, final int nPixelSpace, final int nLineSpace, final int nBandSpace) {
    return gdalJNI.Dataset_WriteRaster__SWIG_12(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list, nPixelSpace, nLineSpace,
      nBandSpace);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final int[] regularArrayIn,
    final int[] band_list) {
    return gdalJNI.Dataset_WriteRaster__SWIG_11(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final int[] regularArrayIn,
    final int[] band_list, final int nPixelSpace) {
    return gdalJNI.Dataset_WriteRaster__SWIG_10(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list, nPixelSpace);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final int[] regularArrayIn,
    final int[] band_list, final int nPixelSpace, final int nLineSpace) {
    return gdalJNI.Dataset_WriteRaster__SWIG_9(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list, nPixelSpace, nLineSpace);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final int[] regularArrayIn,
    final int[] band_list, final int nPixelSpace, final int nLineSpace, final int nBandSpace) {
    return gdalJNI.Dataset_WriteRaster__SWIG_8(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list, nPixelSpace, nLineSpace,
      nBandSpace);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final short[] regularArrayIn,
    final int[] band_list) {
    return gdalJNI.Dataset_WriteRaster__SWIG_7(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final short[] regularArrayIn,
    final int[] band_list, final int nPixelSpace) {
    return gdalJNI.Dataset_WriteRaster__SWIG_6(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list, nPixelSpace);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final short[] regularArrayIn,
    final int[] band_list, final int nPixelSpace, final int nLineSpace) {
    return gdalJNI.Dataset_WriteRaster__SWIG_5(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list, nPixelSpace, nLineSpace);
  }

  public int WriteRaster(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type, final short[] regularArrayIn,
    final int[] band_list, final int nPixelSpace, final int nLineSpace, final int nBandSpace) {
    return gdalJNI.Dataset_WriteRaster__SWIG_4(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, regularArrayIn, band_list, nPixelSpace, nLineSpace,
      nBandSpace);
  }

  public int WriteRaster_Direct(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type,
    final java.nio.ByteBuffer nioBuffer, final int[] band_list) {
    return gdalJNI.Dataset_WriteRaster_Direct__SWIG_3(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, nioBuffer, band_list);
  }

  public int WriteRaster_Direct(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type,
    final java.nio.ByteBuffer nioBuffer, final int[] band_list, final int nPixelSpace) {
    return gdalJNI.Dataset_WriteRaster_Direct__SWIG_2(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, nioBuffer, band_list, nPixelSpace);
  }

  public int WriteRaster_Direct(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type,
    final java.nio.ByteBuffer nioBuffer, final int[] band_list, final int nPixelSpace,
    final int nLineSpace) {
    return gdalJNI.Dataset_WriteRaster_Direct__SWIG_1(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, nioBuffer, band_list, nPixelSpace, nLineSpace);
  }

  public int WriteRaster_Direct(final int xoff, final int yoff, final int xsize, final int ysize,
    final int buf_xsize, final int buf_ysize, final int buf_type,
    final java.nio.ByteBuffer nioBuffer, final int[] band_list, final int nPixelSpace,
    final int nLineSpace, final int nBandSpace) {
    return gdalJNI.Dataset_WriteRaster_Direct__SWIG_0(this.swigCPtr, this, xoff, yoff, xsize, ysize,
      buf_xsize, buf_ysize, buf_type, nioBuffer, band_list, nPixelSpace, nLineSpace, nBandSpace);
  }

}
