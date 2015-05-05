/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.gdal.ogr;

import org.gdal.osr.SpatialReference;

public interface ogrConstants {
  public final static int wkb25DBit = 0x80000000;
  public final static int wkb25Bit = 0x80000000;
  public final static int wkbUnknown = 0;
  public final static int wkbPoint = 1;
  public final static int wkbLineString = 2;
  public final static int wkbPolygon = 3;
  public final static int wkbMultiPoint = 4;
  public final static int wkbMultiLineString = 5;
  public final static int wkbMultiPolygon = 6;
  public final static int wkbGeometryCollection = 7;
  public final static int wkbNone = 100;
  public final static int wkbLinearRing = 101;
  public final static int wkbPoint25D = wkbPoint+wkb25DBit;
  public final static int wkbLineString25D = wkbLineString+wkb25DBit;
  public final static int wkbPolygon25D = wkbPolygon+wkb25DBit;
  public final static int wkbMultiPoint25D = wkbMultiPoint+wkb25DBit;
  public final static int wkbMultiLineString25D = wkbMultiLineString+wkb25DBit;
  public final static int wkbMultiPolygon25D = wkbMultiPolygon+wkb25DBit;
  public final static int wkbGeometryCollection25D = wkbGeometryCollection+wkb25DBit;
  public final static int OFTInteger = 0;
  public final static int OFTIntegerList = 1;
  public final static int OFTReal = 2;
  public final static int OFTRealList = 3;
  public final static int OFTString = 4;
  public final static int OFTStringList = 5;
  public final static int OFTWideString = 6;
  public final static int OFTWideStringList = 7;
  public final static int OFTBinary = 8;
  public final static int OFTDate = 9;
  public final static int OFTTime = 10;
  public final static int OFTDateTime = 11;
  public final static int OJUndefined = 0;
  public final static int OJLeft = 1;
  public final static int OJRight = 2;
  public final static int wkbXDR = 0;
  public final static int wkbNDR = 1;
  public final static int NullFID = -1;
  public final static int ALTER_NAME_FLAG = 1;
  public final static int ALTER_TYPE_FLAG = 2;
  public final static int ALTER_WIDTH_PRECISION_FLAG = 4;
  public final static int ALTER_ALL_FLAG = 1+2+4;
  public final static String OLCRandomRead = "RandomRead";
  public final static String OLCSequentialWrite = "SequentialWrite";
  public final static String OLCRandomWrite = "RandomWrite";
  public final static String OLCFastSpatialFilter = "FastSpatialFilter";
  public final static String OLCFastFeatureCount = "FastFeatureCount";
  public final static String OLCFastGetExtent = "FastGetExtent";
  public final static String OLCCreateField = "CreateField";
  public final static String OLCDeleteField = "DeleteField";
  public final static String OLCReorderFields = "ReorderFields";
  public final static String OLCAlterFieldDefn = "AlterFieldDefn";
  public final static String OLCTransactions = "Transactions";
  public final static String OLCDeleteFeature = "DeleteFeature";
  public final static String OLCFastSetNextByIndex = "FastSetNextByIndex";
  public final static String OLCStringsAsUTF8 = "StringsAsUTF8";
  public final static String OLCIgnoreFields = "IgnoreFields";
  public final static String OLCCreateGeomField = "CreateGeomField";
  public final static String ODsCCreateLayer = "CreateLayer";
  public final static String ODsCDeleteLayer = "DeleteLayer";
  public final static String ODsCCreateGeomFieldAfterCreateLayer = "CreateGeomFieldAfterCreateLayer";
  public final static String ODrCCreateDataSource = "CreateDataSource";
  public final static String ODrCDeleteDataSource = "DeleteDataSource";
  public final static int OGRERR_NONE = 0;
  public final static int OGRERR_NOT_ENOUGH_DATA = 1;
  public final static int OGRERR_NOT_ENOUGH_MEMORY = 2;
  public final static int OGRERR_UNSUPPORTED_GEOMETRY_TYPE = 3;
  public final static int OGRERR_UNSUPPORTED_OPERATION = 4;
  public final static int OGRERR_CORRUPT_DATA = 5;
  public final static int OGRERR_FAILURE = 6;
  public final static int OGRERR_UNSUPPORTED_SRS = 7;
}
