/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.revolsys.gis.esri.gdb.file.capi.swig;

import com.revolsys.jar.ClasspathNativeLibraryUtil;
import com.revolsys.util.OS;

public class EsriFileGdbJNI {

  static {
    if (OS.isUnix() || OS.isMac()) {
      ClasspathNativeLibraryUtil.loadLibrary("fgdbunixrtl");
    } else if (OS.isWindows()) {
      ClasspathNativeLibraryUtil.loadLibrary("Esri.FILEGDBAPI");
    }

    ClasspathNativeLibraryUtil.loadLibrary("FileGDBAPI");
    ClasspathNativeLibraryUtil.loadLibrary("EsriFileGdbJni");
    if (OS.isWindows()) {
      EsriFileGdb.setMaxOpenFiles(2048);
    }
  }

  public final static native long new_VectorOfString__SWIG_0();
  public final static native long new_VectorOfString__SWIG_1(long jarg1);
  public final static native long VectorOfString_size(long jarg1, VectorOfString jarg1_);
  public final static native long VectorOfString_capacity(long jarg1, VectorOfString jarg1_);
  public final static native void VectorOfString_reserve(long jarg1, VectorOfString jarg1_, long jarg2);
  public final static native boolean VectorOfString_isEmpty(long jarg1, VectorOfString jarg1_);
  public final static native void VectorOfString_clear(long jarg1, VectorOfString jarg1_);
  public final static native void VectorOfString_add(long jarg1, VectorOfString jarg1_, String jarg2);
  public final static native String VectorOfString_get(long jarg1, VectorOfString jarg1_, int jarg2);
  public final static native void VectorOfString_set(long jarg1, VectorOfString jarg1_, int jarg2, String jarg3);
  public final static native void delete_VectorOfString(long jarg1);
  public final static native long new_VectorOfWString__SWIG_0();
  public final static native long new_VectorOfWString__SWIG_1(long jarg1);
  public final static native long VectorOfWString_size(long jarg1, VectorOfWString jarg1_);
  public final static native long VectorOfWString_capacity(long jarg1, VectorOfWString jarg1_);
  public final static native void VectorOfWString_reserve(long jarg1, VectorOfWString jarg1_, long jarg2);
  public final static native boolean VectorOfWString_isEmpty(long jarg1, VectorOfWString jarg1_);
  public final static native void VectorOfWString_clear(long jarg1, VectorOfWString jarg1_);
  public final static native void VectorOfWString_add(long jarg1, VectorOfWString jarg1_, String jarg2);
  public final static native String VectorOfWString_get(long jarg1, VectorOfWString jarg1_, int jarg2);
  public final static native void VectorOfWString_set(long jarg1, VectorOfWString jarg1_, int jarg2, String jarg3);
  public final static native void delete_VectorOfWString(long jarg1);
  public final static native long new_VectorOfFieldDef__SWIG_0();
  public final static native long new_VectorOfFieldDef__SWIG_1(long jarg1);
  public final static native long VectorOfFieldDef_size(long jarg1, VectorOfFieldDef jarg1_);
  public final static native long VectorOfFieldDef_capacity(long jarg1, VectorOfFieldDef jarg1_);
  public final static native void VectorOfFieldDef_reserve(long jarg1, VectorOfFieldDef jarg1_, long jarg2);
  public final static native boolean VectorOfFieldDef_isEmpty(long jarg1, VectorOfFieldDef jarg1_);
  public final static native void VectorOfFieldDef_clear(long jarg1, VectorOfFieldDef jarg1_);
  public final static native void VectorOfFieldDef_add(long jarg1, VectorOfFieldDef jarg1_, long jarg2, FieldDef jarg2_);
  public final static native long VectorOfFieldDef_get(long jarg1, VectorOfFieldDef jarg1_, int jarg2);
  public final static native void VectorOfFieldDef_set(long jarg1, VectorOfFieldDef jarg1_, int jarg2, long jarg3, FieldDef jarg3_);
  public final static native void delete_VectorOfFieldDef(long jarg1);
  public final static native void setMaxOpenFiles(int jarg1);
  public final static native long createGeodatabase(String jarg1);
  public final static native long openGeodatabase(String jarg1);
  public final static native String getSpatialReferenceWkt(int jarg1);
  public final static native int CloseGeodatabase(long jarg1, Geodatabase jarg1_);
  public final static native int DeleteGeodatabase(String jarg1);
  public final static native int Geodatabase_GetDatasetTypes(long jarg1, Geodatabase jarg1_, long jarg2, VectorOfWString jarg2_);
  public final static native int Geodatabase_GetDatasetRelationshipTypes(long jarg1, Geodatabase jarg1_, long jarg2, VectorOfWString jarg2_);
  public final static native int Geodatabase_GetRelatedDatasets(long jarg1, Geodatabase jarg1_, String jarg2, String jarg3, String jarg4, long jarg5, VectorOfWString jarg5_);
  public final static native int Geodatabase_GetChildDatasetDefinitions(long jarg1, Geodatabase jarg1_, String jarg2, String jarg3, long jarg4, VectorOfString jarg4_);
  public final static native int Geodatabase_GetRelatedDatasetDefinitions(long jarg1, Geodatabase jarg1_, String jarg2, String jarg3, String jarg4, long jarg5, VectorOfString jarg5_);
  public final static native int Geodatabase_Rename(long jarg1, Geodatabase jarg1_, String jarg2, String jarg3, String jarg4);
  public final static native int Geodatabase_Move(long jarg1, Geodatabase jarg1_, String jarg2, String jarg3);
  public final static native int Geodatabase_Delete(long jarg1, Geodatabase jarg1_, String jarg2, String jarg3);
  public final static native int Geodatabase_CompactDatabase(long jarg1, Geodatabase jarg1_);
  public final static native long new_Geodatabase();
  public final static native void delete_Geodatabase(long jarg1);
  public final static native int createGeodatabase2(String jarg1, long jarg2, Geodatabase jarg2_);
  public final static native int openGeodatabase2(String jarg1, long jarg2, Geodatabase jarg2_);
  public final static native int closeGeodatabase2(long jarg1, Geodatabase jarg1_);
  public final static native int deleteGeodatabase2(String jarg1);
  public final static native void Geodatabase_createFeatureDataset(long jarg1, Geodatabase jarg1_, String jarg2);
  public final static native long Geodatabase_query(long jarg1, Geodatabase jarg1_, String jarg2, boolean jarg3);
  public final static native long Geodatabase_getChildDatasets(long jarg1, Geodatabase jarg1_, String jarg2, String jarg3);
  public final static native String Geodatabase_getDatasetDefinition(long jarg1, Geodatabase jarg1_, String jarg2, String jarg3);
  public final static native String Geodatabase_getDatasetDocumentation(long jarg1, Geodatabase jarg1_, String jarg2, String jarg3);
  public final static native long Geodatabase_getDomains(long jarg1, Geodatabase jarg1_);
  public final static native String Geodatabase_getDomainDefinition(long jarg1, Geodatabase jarg1_, String jarg2);
  public final static native void Geodatabase_createDomain(long jarg1, Geodatabase jarg1_, String jarg2);
  public final static native void Geodatabase_alterDomain(long jarg1, Geodatabase jarg1_, String jarg2);
  public final static native void Geodatabase_deleteDomain(long jarg1, Geodatabase jarg1_, String jarg2);
  public final static native String Geodatabase_getQueryName(long jarg1, Geodatabase jarg1_, String jarg2);
  public final static native long Geodatabase_openTable(long jarg1, Geodatabase jarg1_, String jarg2);
  public final static native void Geodatabase_closeTable(long jarg1, Geodatabase jarg1_, long jarg2, Table jarg2_);
  public final static native String Geodatabase_getTableDefinition(long jarg1, Geodatabase jarg1_, String jarg2);
  public final static native long Geodatabase_createTable(long jarg1, Geodatabase jarg1_, String jarg2, String jarg3);
  public final static native int Table_SetDocumentation(long jarg1, Table jarg1_, String jarg2);
  public final static native int Table_GetFieldInformation(long jarg1, Table jarg1_, long jarg2, FieldInfo jarg2_);
  public final static native int Table_AddField__SWIG_0(long jarg1, Table jarg1_, String jarg2);
  public final static native int Table_AddField__SWIG_1(long jarg1, Table jarg1_, long jarg2, FieldDef jarg2_);
  public final static native int Table_AlterField(long jarg1, Table jarg1_, String jarg2);
  public final static native int Table_DeleteField(long jarg1, Table jarg1_, String jarg2);
  public final static native int Table_AddIndex__SWIG_0(long jarg1, Table jarg1_, String jarg2);
  public final static native int Table_AddIndex__SWIG_1(long jarg1, Table jarg1_, long jarg2, IndexDef jarg2_);
  public final static native int Table_DeleteIndex(long jarg1, Table jarg1_, String jarg2);
  public final static native int Table_CreateSubtype(long jarg1, Table jarg1_, String jarg2);
  public final static native int Table_AlterSubtype(long jarg1, Table jarg1_, String jarg2);
  public final static native int Table_DeleteSubtype(long jarg1, Table jarg1_, String jarg2);
  public final static native int Table_EnableSubtypes(long jarg1, Table jarg1_, String jarg2, String jarg3);
  public final static native int Table_SetDefaultSubtypeCode(long jarg1, Table jarg1_, int jarg2);
  public final static native int Table_DisableSubtypes(long jarg1, Table jarg1_);
  public final static native int Table_GetExtent(long jarg1, Table jarg1_, long jarg2, Envelope jarg2_);
  public final static native long new_Table();
  public final static native void delete_Table(long jarg1);
  public final static native boolean Table_isEditable(long jarg1, Table jarg1_);
  public final static native String Table_getDefinition(long jarg1, Table jarg1_);
  public final static native String Table_getDocumentation(long jarg1, Table jarg1_);
  public final static native int Table_getRowCount(long jarg1, Table jarg1_);
  public final static native int Table_getDefaultSubtypeCode(long jarg1, Table jarg1_);
  public final static native long Table_getIndexes(long jarg1, Table jarg1_);
  public final static native long Table_createRowObject(long jarg1, Table jarg1_);
  public final static native void Table_insertRow(long jarg1, Table jarg1_, long jarg2, Row jarg2_);
  public final static native void Table_updateRow(long jarg1, Table jarg1_, long jarg2, Row jarg2_);
  public final static native void Table_deleteRow(long jarg1, Table jarg1_, long jarg2, Row jarg2_);
  public final static native long Table_search__SWIG_0(long jarg1, Table jarg1_, String jarg2, String jarg3, long jarg4, Envelope jarg4_, boolean jarg5);
  public final static native long Table_search__SWIG_1(long jarg1, Table jarg1_, String jarg2, String jarg3, boolean jarg4);
  public final static native void Table_setLoadOnlyMode(long jarg1, Table jarg1_, boolean jarg2);
  public final static native void Table_setWriteLock(long jarg1, Table jarg1_);
  public final static native void Table_freeWriteLock(long jarg1, Table jarg1_);
  public final static native long Table_getFields(long jarg1, Table jarg1_);
  public final static native int Row_GetFieldInformation(long jarg1, Row jarg1_, long jarg2, FieldInfo jarg2_);
  public final static native long new_Row();
  public final static native void delete_Row(long jarg1);
  public final static native boolean Row_isNull(long jarg1, Row jarg1_, String jarg2);
  public final static native void Row_setNull(long jarg1, Row jarg1_, String jarg2);
  public final static native long Row_getDate(long jarg1, Row jarg1_, String jarg2);
  public final static native void Row_setDate(long jarg1, Row jarg1_, String jarg2, long jarg3);
  public final static native double Row_getDouble(long jarg1, Row jarg1_, String jarg2);
  public final static native void Row_setDouble(long jarg1, Row jarg1_, String jarg2, double jarg3);
  public final static native float Row_getFloat(long jarg1, Row jarg1_, String jarg2);
  public final static native void Row_setFloat(long jarg1, Row jarg1_, String jarg2, double jarg3);
  public final static native long Row_getGuid(long jarg1, Row jarg1_, String jarg2);
  public final static native long Row_getGlobalId(long jarg1, Row jarg1_);
  public final static native void Row_setGuid(long jarg1, Row jarg1_, String jarg2, long jarg3, Guid jarg3_);
  public final static native int Row_getOid(long jarg1, Row jarg1_);
  public final static native short Row_getShort(long jarg1, Row jarg1_, String jarg2);
  public final static native void Row_setShort(long jarg1, Row jarg1_, String jarg2, short jarg3);
  public final static native int Row_getInteger(long jarg1, Row jarg1_, String jarg2);
  public final static native void Row_setInteger(long jarg1, Row jarg1_, String jarg2, int jarg3);
  public final static native String Row_getString(long jarg1, Row jarg1_, String jarg2);
  public final static native void Row_setString(long jarg1, Row jarg1_, String jarg2, String jarg3);
  public final static native String Row_getXML(long jarg1, Row jarg1_, String jarg2);
  public final static native void Row_setXML(long jarg1, Row jarg1_, String jarg2, String jarg3);
  public final static native byte[] Row_getGeometry(long jarg1, Row jarg1_);
  public final static native void Row_setGeometry(long jarg1, Row jarg1_, byte[] jarg2);
  public final static native long Row_getFields(long jarg1, Row jarg1_);
  public final static native void EnumRows_Close(long jarg1, EnumRows jarg1_);
  public final static native int EnumRows_GetFieldInformation(long jarg1, EnumRows jarg1_, long jarg2, FieldInfo jarg2_);
  public final static native long new_EnumRows();
  public final static native void delete_EnumRows(long jarg1);
  public final static native long EnumRows_next(long jarg1, EnumRows jarg1_);
  public final static native long EnumRows_getFields(long jarg1, EnumRows jarg1_);
  public final static native long new_FieldDef();
  public final static native void delete_FieldDef(long jarg1);
  public final static native int FieldDef_SetName(long jarg1, FieldDef jarg1_, String jarg2);
  public final static native int FieldDef_SetAlias(long jarg1, FieldDef jarg1_, String jarg2);
  public final static native int FieldDef_SetType(long jarg1, FieldDef jarg1_, int jarg2);
  public final static native int FieldDef_SetLength(long jarg1, FieldDef jarg1_, int jarg2);
  public final static native int FieldDef_SetIsNullable(long jarg1, FieldDef jarg1_, boolean jarg2);
  public final static native String FieldDef_getAlias(long jarg1, FieldDef jarg1_);
  public final static native String FieldDef_getName(long jarg1, FieldDef jarg1_);
  public final static native boolean FieldDef_isNullable(long jarg1, FieldDef jarg1_);
  public final static native int FieldDef_getLength(long jarg1, FieldDef jarg1_);
  public final static native int FieldDef_getType(long jarg1, FieldDef jarg1_);
  public final static native long new_IndexDef__SWIG_0();
  public final static native long new_IndexDef__SWIG_1(String jarg1, String jarg2, boolean jarg3);
  public final static native long new_IndexDef__SWIG_2(String jarg1, String jarg2);
  public final static native void delete_IndexDef(long jarg1);
  public final static native int IndexDef_SetName(long jarg1, IndexDef jarg1_, String jarg2);
  public final static native int IndexDef_SetFields(long jarg1, IndexDef jarg1_, String jarg2);
  public final static native int IndexDef_SetIsUnique(long jarg1, IndexDef jarg1_, boolean jarg2);
  public final static native boolean IndexDef_isUnique(long jarg1, IndexDef jarg1_);
  public final static native String IndexDef_getName(long jarg1, IndexDef jarg1_);
  public final static native String IndexDef_getFields(long jarg1, IndexDef jarg1_);
  public final static native long new_FieldInfo();
  public final static native void delete_FieldInfo(long jarg1);
  public final static native int FieldInfo_getFieldCount(long jarg1, FieldInfo jarg1_);
  public final static native String FieldInfo_getFieldName(long jarg1, FieldInfo jarg1_, int jarg2);
  public final static native int FieldInfo_getFieldLength(long jarg1, FieldInfo jarg1_, int jarg2);
  public final static native boolean FieldInfo_isNullable(long jarg1, FieldInfo jarg1_, int jarg2);
  public final static native int FieldInfo_getFieldType(long jarg1, FieldInfo jarg1_, int jarg2);
  public final static native boolean Envelope_IsEmpty(long jarg1, Envelope jarg1_);
  public final static native void Envelope_SetEmpty(long jarg1, Envelope jarg1_);
  public final static native long new_Envelope__SWIG_0();
  public final static native long new_Envelope__SWIG_1(double jarg1, double jarg2, double jarg3, double jarg4);
  public final static native void delete_Envelope(long jarg1);
  public final static native void Envelope_xMin_set(long jarg1, Envelope jarg1_, double jarg2);
  public final static native double Envelope_xMin_get(long jarg1, Envelope jarg1_);
  public final static native void Envelope_yMin_set(long jarg1, Envelope jarg1_, double jarg2);
  public final static native double Envelope_yMin_get(long jarg1, Envelope jarg1_);
  public final static native void Envelope_xMax_set(long jarg1, Envelope jarg1_, double jarg2);
  public final static native double Envelope_xMax_get(long jarg1, Envelope jarg1_);
  public final static native void Envelope_yMax_set(long jarg1, Envelope jarg1_, double jarg2);
  public final static native double Envelope_yMax_get(long jarg1, Envelope jarg1_);
  public final static native void Envelope_zMin_set(long jarg1, Envelope jarg1_, double jarg2);
  public final static native double Envelope_zMin_get(long jarg1, Envelope jarg1_);
  public final static native void Envelope_zMax_set(long jarg1, Envelope jarg1_, double jarg2);
  public final static native double Envelope_zMax_get(long jarg1, Envelope jarg1_);
  public final static native long new_Guid();
  public final static native void delete_Guid(long jarg1);
  public final static native void Guid_SetNull(long jarg1, Guid jarg1_);
  public final static native void Guid_Create(long jarg1, Guid jarg1_);
  public final static native int Guid_FromString(long jarg1, Guid jarg1_, String jarg2);
  public final static native boolean Guid_equal(long jarg1, Guid jarg1_, long jarg2, Guid jarg2_);
  public final static native boolean Guid_notEqual(long jarg1, Guid jarg1_, long jarg2, Guid jarg2_);
  public final static native String Guid_toString(long jarg1, Guid jarg1_);
  public final static native long new_Raster();
  public final static native void delete_Raster(long jarg1);
}
