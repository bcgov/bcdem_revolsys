%module "EsriFileGdb"

%{

#include <stdexcept>
#include <sstream>
#include "time.h"
#include "FileGDBAPI.h"

std::string wstring2string(std::wstring wstr) {
  std::string str(wstr.length(),' ');
  copy(wstr.begin(),wstr.end(),str.begin());
  return str;
}

  
fgdbError checkResult(fgdbError error) {
  if (error) {
     std::wstring errorString;
     FileGDBAPI::ErrorInfo::GetErrorDescription(error, errorString);
     std::stringstream message;
     message << error << "\t" << wstring2string(errorString);
     throw std::runtime_error(message.str());
  }
  return error;
}

void handleRuntimeError(JNIEnv *jenv, const std::runtime_error e) {
  std::stringstream message;
  message << e.what();
  int count;
  FileGDBAPI::ErrorInfo::GetErrorRecordCount(count);
  for (int i = 0; i < count; i++) {
    int num;
    std::wstring description;
    FileGDBAPI::ErrorInfo::GetErrorRecord(i, num, description);
    message << "\n" << num << "\t" << wstring2string(description);
    FileGDBAPI::ErrorInfo::ClearErrors();
    
  }
  jclass clazz = jenv->FindClass("java/lang/RuntimeException");
  jenv->ThrowNew(clazz, message.str().c_str());
}
  
%}

%pragma(java) jniclassimports=%{
import com.revolsys.jar.ClasspathNativeLibraryUtil;
%}

%pragma(java) jniclasscode=%{
  static {
    ClasspathNativeLibraryUtil.loadLibrary("EsriFileGdbJni");
  }
%}
%define EXT_FILEGDB_API
%enddef

%include "std_vector.i"
%include "std_string.i"
%include "std_wstring.i"
%include "typemaps.i"
%include "enums.swg"
%include "OutParam.i"
%include "OutArrayParam.i"
%javaconst(1);

%template(VectorOfString) std::vector<std::string>;
%template(VectorOfWString) std::vector<std::wstring>;

%include "Array.i"
ARRAY_OUT(float, FloatArray)
ARRAY_OUT(int, IntArray)
ARRAY_OUT(double, DoubleArray)
ARRAY_OUT(unsigned char, UnsignedCharArray)

%define linux
%enddef

%exception {
  try {
    $action;
  } catch (const std::runtime_error& e) {
    handleRuntimeError(jenv, e);
  }
}

%ignore FileGDBAPI::CreateGeodatabase;
%ignore FileGDBAPI::OpenGeodatabase;
%ignore FileGDBAPI::ErrorInfo::GetErrorRecordCount;
%ignore FileGDBAPI::ErrorInfo::GetErrorDescription;
%ignore FileGDBAPI::ErrorInfo::GetErrorRecord;
%ignore FileGDBAPI::ErrorInfo::ClearErrors;
%newobject createGeodatabase;
%newobject openGeodatabase;
%inline {
  FileGDBAPI::Geodatabase* createGeodatabase(const std::wstring& path) {
    FileGDBAPI::Geodatabase* value = new FileGDBAPI::Geodatabase();
    checkResult(FileGDBAPI::CreateGeodatabase(path, *value));
    return value;
  }
  FileGDBAPI::Geodatabase* openGeodatabase(const std::wstring& path) {
    FileGDBAPI::Geodatabase* value = new FileGDBAPI::Geodatabase();
    checkResult(FileGDBAPI::OpenGeodatabase(path, *value));
    return value;
  }
  
  std::wstring getSpatialReferenceWkt(int srid) {
    FileGDBAPI::SpatialReferenceInfo value;
    FileGDBAPI::SpatialReferences::FindSpatialReferenceBySRID(srid, value);
    return value.srtext;
  }
}

%ignore FileGDBAPI::ShapeModifiers;

%include "FileGDBCore.h"
%include "GeodatabaseManagement.h"

%rename(createGeodatabase2) FileGDBAPI::CreateGeodatabase;
%rename(openGeodatabase2) FileGDBAPI::OpenGeodatabase;
%rename(closeGeodatabase2) FileGDBAPI::CloseGeodatabase;
%rename(deleteGeodatabase2) FileGDBAPI::DeleteGeodatabase;
%ignore FileGDBAPI::Geodatabase::GetDatasetDefinition;
%ignore FileGDBAPI::Geodatabase::GetDatasetDocumentation;
%ignore FileGDBAPI::Geodatabase::GetQueryName;
%ignore FileGDBAPI::Geodatabase::OpenTable;
%ignore FileGDBAPI::Geodatabase::CreateTable;
%ignore FileGDBAPI::Geodatabase::CreateDomain;
%ignore FileGDBAPI::Geodatabase::AlterDomain;
%ignore FileGDBAPI::Geodatabase::DeleteDomain;
%ignore FileGDBAPI::Geodatabase::GetDomainDefinition;
%ignore FileGDBAPI::Geodatabase::GetDomains;
%ignore FileGDBAPI::Geodatabase::GetChildDatasets;
%ignore FileGDBAPI::Geodatabase::CreateFeatureDataset;
%newobject FileGDBAPI::Geodatabase::createTable;
%newobject FileGDBAPI::Geodatabase::openTable;
%extend FileGDBAPI::Geodatabase {
  void createFeatureDataset(std::string featureDatasetDef) {
    checkResult(self->CreateFeatureDataset(featureDatasetDef));
  }

  std::vector<std::wstring> getChildDatasets(std::wstring parentPath, std::wstring datasetType) {
    std::vector<std::wstring> value;
    checkResult(self->GetChildDatasets(parentPath, datasetType, value));
    return value;
  }
  
  std::string getDatasetDefinition(std::wstring path, std::wstring datasetType) {
    std::string value;
    checkResult(self->GetDatasetDefinition(path, datasetType, value));
    return value;
  }
  
  std::string getDatasetDocumentation(std::wstring path, std::wstring datasetType) {
    std::string value;
    checkResult(self->GetDatasetDocumentation(path, datasetType, value));
    return value;
  }
  
  std::vector<std::wstring> getDomains() {
    std::vector<std::wstring> value;
    checkResult(self->GetDomains(value));
    return value;
  }
  
  std::string getDomainDefinition(std::wstring domainName) {
    std::string value;
    checkResult(self->GetDomainDefinition(domainName, value));
    return value;
  }
  void createDomain(const std::string& domainDefinition) {
    checkResult(self->CreateDomain(domainDefinition));
  }
  void alterDomain(const std::string& domainDefinition) {
    checkResult(self->AlterDomain(domainDefinition));
  }
  void deleteDomain(const std::wstring& domainName) {
    checkResult(self->DeleteDomain(domainName));
  }
  
  std::wstring getQueryName(std::wstring path) {
    std::wstring value;
    checkResult(self->GetQueryName(path, value));
    return value;
  }
  FileGDBAPI::Table* openTable(const std::wstring& path) {
    FileGDBAPI::Table* value = new FileGDBAPI::Table();
    checkResult(self->OpenTable(path, *value));
    return value;
  }
  FileGDBAPI::Table* createTable(const std::string& tableDefinition, const std::wstring& parent) {
    FileGDBAPI::Table* value = new FileGDBAPI::Table();
    checkResult(self->CreateTable(tableDefinition, parent, *value));
    return value;
  }
}
%include "Geodatabase.h"

%ignore FileGDBAPI::Table::IsEditable;
%ignore FileGDBAPI::Table::GetDefinition;
%ignore FileGDBAPI::Table::GetDocumentation;
%ignore FileGDBAPI::Table::GetRowCount;
%ignore FileGDBAPI::Table::GetDefaultSubtypeCode;
%ignore FileGDBAPI::Table::CreateRowObject;
%ignore FileGDBAPI::Table::GetIndexes;
%ignore FileGDBAPI::Table::Search;
%ignore FileGDBAPI::Table::Insert;
%ignore FileGDBAPI::Table::Update;
%ignore FileGDBAPI::Table::Delete;
%newobject FileGDBAPI::Table::createRowObject;
%newobject FileGDBAPI::Table::search;
%extend FileGDBAPI::Table {
  bool isEditable() {
    bool value;
    checkResult(self->IsEditable(value));
    return value;
  }
  std::string getDefinition() {
    std::string value;
    checkResult(self->GetDefinition(value));
    return value;
  }
  std::string getDocumentation() {
    std::string value;
    checkResult(self->GetDocumentation(value));
    return value;
  }
  int getRowCount() {
    int value;
    checkResult(self->GetRowCount(value));
    return value;
  }
  int getDefaultSubtypeCode() {
    int value;
    checkResult(self->GetDefaultSubtypeCode(value));
    return value;
  }
  std::vector<std::string> getIndexes() {
    std::vector<std::string> value;
    checkResult(self->GetIndexes(value));
    return value;
  }
  FileGDBAPI::Row* createRowObject() {
    FileGDBAPI::Row* value = new FileGDBAPI::Row();
    checkResult(self->CreateRowObject(*value));
    return value;
  }
  void insertRow(FileGDBAPI::Row& row) {
    checkResult(self->Insert(row));
  }
  void updateRow(FileGDBAPI::Row& row) {
    checkResult(self->Update(row));
  }
  void deleteRow(FileGDBAPI::Row& row) {
    checkResult(self->Delete(row));
  }

  FileGDBAPI::EnumRows* search(const std::wstring& subfields, const std::wstring& whereClause, Envelope envelope, bool recycling) {
    FileGDBAPI::EnumRows* rows = new FileGDBAPI::EnumRows();
    checkResult(self->Search(subfields, whereClause, envelope, recycling, *rows));
    return rows;
  }

  FileGDBAPI::EnumRows* search(const std::wstring& subfields, const std::wstring& whereClause, bool recycling) {
    FileGDBAPI::EnumRows* rows = new FileGDBAPI::EnumRows();
    checkResult(self->Search(subfields, whereClause, recycling, *rows));
    return rows;
  }

}
%include "Table.h"

%ignore FileGDBAPI::Row::IsNull;
%ignore FileGDBAPI::Row::SetNull;
%ignore FileGDBAPI::Row::GetDate;
%ignore FileGDBAPI::Row::SetDate;
%ignore FileGDBAPI::Row::GetDouble;
%ignore FileGDBAPI::Row::SetDouble;
%ignore FileGDBAPI::Row::GetFloat;
%ignore FileGDBAPI::Row::SetFloat;
%ignore FileGDBAPI::Row::GetGeometry;
%ignore FileGDBAPI::Row::SetGeometry;
%ignore FileGDBAPI::Row::GetGUID;
%ignore FileGDBAPI::Row::SetGUID;
%ignore FileGDBAPI::Row::GetGlobalID;
%ignore FileGDBAPI::Row::SetGlobalID;
%ignore FileGDBAPI::Row::GetInteger;
%ignore FileGDBAPI::Row::SetInteger;
%ignore FileGDBAPI::Row::GetOID;
%ignore FileGDBAPI::Row::SetOID;
%ignore FileGDBAPI::Row::GetRaster;
%ignore FileGDBAPI::Row::SetRaster;
%ignore FileGDBAPI::Row::GetShort;
%ignore FileGDBAPI::Row::SetShort;
%ignore FileGDBAPI::Row::GetString;
%ignore FileGDBAPI::Row::SetString;
%ignore FileGDBAPI::Row::GetXML;
%ignore FileGDBAPI::Row::SetXML;
%newobject FileGDBAPI::Row::getGeometry;
%extend FileGDBAPI::Row {
  bool isNull(std::wstring name) {
    bool value;
    checkResult(self->IsNull(name,value));
    return value;
  }
  void setNull(std::wstring name) {
    checkResult(self->SetNull(name));
  }
 
  long long getDate(const std::wstring& name) {
    struct tm value;
    checkResult(self->GetDate(name,value));
    return mktime(&value);
  }
  void setDate(const std::wstring& name, long long date) {
    const time_t time = (time_t)date;
    struct tm* tm_time = localtime(&time);
    if (tm_time == 0) {
      throw std::runtime_error("Invalid date " + date);
    } else {
      struct tm value;
      value = *tm_time;
      checkResult(self->SetDate(name, value));
    }
  }

  double getDouble(const std::wstring& name) {
    double value;
    checkResult(self->GetDouble(name,value));
    return value;
  }
  
  void setDouble(const std::wstring& name, double value) {
    checkResult(self->SetDouble(name, value));
  }
 
  float getFloat(const std::wstring& name) {
    float value;
    checkResult(self->GetFloat(name,value));
    return value;
  }
  
  void setFloat(const std::wstring& name, double value) {
    checkResult(self->SetFloat(name, value));
  }

  FileGDBAPI::Guid getGuid(std::wstring name) {
    FileGDBAPI::Guid value;
    checkResult(self->GetGUID(name,value));
    return value;
  }
 
  FileGDBAPI::Guid getGlobalId() {
    FileGDBAPI::Guid value;
    checkResult(self->GetGlobalID(value));
    return value;
  }
  
  void setGuid(const std::wstring& name, const FileGDBAPI::Guid& value) {
    checkResult(self->SetGUID(name, value));
  }

  int getOid() {
    int value;
    checkResult(self->GetOID(value));
    return value;
  }

  short getShort(const std::wstring& name) {
    short value;
    checkResult(self->GetShort(name,value));
    return value;
  }
  
  void setShort(const std::wstring& name, short value) {
    checkResult(self->SetShort(name, value));
  }

  int32 getInteger(const std::wstring& name) {
    int value;
    checkResult(self->GetInteger(name,value));
    return value;
  }
    
  void setInteger(const std::wstring& name, int32 value) {
    checkResult(self->SetInteger(name, value));
  }
  
  std::wstring getString(const std::wstring& name) {
    std::wstring value;
    checkResult(self->GetString(name,value));
    return value;
  }
  
  void setString(const std::wstring& name, const std::wstring& value) {
    checkResult(self->SetString(name, value));
  }

  std::string getXML(const std::wstring& name) {
    std::string value;
    checkResult(self->GetXML(name,value));
    return value;
  }
  
  void setXML(const std::wstring& name, const std::string& value) {
    checkResult(self->SetXML(name, value));
  }

  FileGDBAPI::ShapeBuffer* getGeometry() {
    FileGDBAPI::ShapeBuffer* geometry = new FileGDBAPI::ShapeBuffer();
    checkResult(self->GetGeometry(*geometry));
     return geometry;
  }
  
  void setGeometry(const FileGDBAPI::ShapeBuffer& value) {
    checkResult(self->SetGeometry(value));
  }
}

%include "Row.h"

%rename(equal) FileGDBAPI::Guid::operator==;
%ignore FileGDBAPI::Guid::data1;
%ignore FileGDBAPI::Guid::data2;
%ignore FileGDBAPI::Guid::data3;
%ignore FileGDBAPI::Guid::data4;
%rename(notEqual) FileGDBAPI::Guid::operator!=;

%ignore FileGDBAPI::FieldInfo::GetFieldCount;
%ignore FileGDBAPI::FieldInfo::GetFieldName;
%ignore FileGDBAPI::FieldInfo::GetFieldLength;
%ignore FileGDBAPI::FieldInfo::GetFieldType;
%ignore FileGDBAPI::FieldInfo::GetFieldIsNullable;
%extend FileGDBAPI::FieldInfo {
  int getFieldCount() {
    int count;
    checkResult(self->GetFieldCount(count));
    return count;
  }

  std::wstring getFieldName(int i) {
    std::wstring name;
    checkResult(self->GetFieldName(i, name));
    return name;
  }

  int getFieldLength(int i) {
    int length;
    checkResult(self->GetFieldLength(i, length));
    return length;
  }
  bool isNullable(int i) {
    bool nullable;
    checkResult(self->GetFieldIsNullable(i, nullable));
    return nullable;
  }

  FileGDBAPI::FieldType getFieldType(int i) {
    FileGDBAPI::FieldType type;
    checkResult(self->GetFieldType(i, type));
    return type;
  }

}

%ignore FileGDBAPI::Guid::ToString;
%extend FileGDBAPI::Guid {
  std::wstring toString() {
    std::wstring value;
    checkResult(self->ToString(value));
    return value;
  }
}

%ignore FileGDBAPI::ByteArray::byteArray;
%extend FileGDBAPI::ByteArray {
  unsigned char get(int i) {
    return $self->byteArray[i];
  }

  void set(int i, unsigned char c) {
    $self->byteArray[i] = c;
  }
}

%ignore FileGDBAPI::ShapeBuffer::shapeBuffer;
%ignore FileGDBAPI::ShapeBuffer::GetShapeType;
%ignore FileGDBAPI::ShapeBuffer::GetGeometryType;
%extend FileGDBAPI::ShapeBuffer {
  unsigned char get(int i) {
    return $self->shapeBuffer[i];
  }

  void set(int i, unsigned char c) {
    $self->shapeBuffer[i] = c;
  }
 
  byte* getShapeBuffer() {
    return $self->shapeBuffer;
  }
  
  FileGDBAPI::ShapeType getShapeType() {
    FileGDBAPI::ShapeType value;
    checkResult(self->GetShapeType(value));
    return value;
  }
  
  FileGDBAPI::GeometryType getGeometryType() {
    FileGDBAPI::GeometryType value;
    checkResult(self->GetGeometryType(value));
    return value;
  }
}

%ignore FileGDBAPI::EnumSpatialReferenceInfo;
%ignore FileGDBAPI::SpatialReferenceInfo;
%ignore FileGDBAPI::SpatialReferences::FindSpatialReferenceByName;
%ignore FileGDBAPI::SpatialReferences::FindSpatialReferenceBySRID;

%ignore FileGDBAPI::Point;
%ignore FileGDBAPI::PointShapeBuffer;
%ignore FileGDBAPI::MultiPointShapeBuffer;
%ignore FileGDBAPI::MultiPartShapeBuffer;
%ignore FileGDBAPI::MultiPatchShapeBuffer;

%ignore FileGDBAPI::EnumRows::Next;
%newobject FileGDBAPI::EnumRows::next;
%extend FileGDBAPI::EnumRows {
  FileGDBAPI::Row* next() {
    FileGDBAPI::Row* value = new FileGDBAPI::Row();
    int hr = self->Next(*value);
    if (hr == S_OK) {
      return value;
    } else {
      delete value;
      return NULL;
    }
  }
}
%include "Util.h"

%include "Raster.h"


