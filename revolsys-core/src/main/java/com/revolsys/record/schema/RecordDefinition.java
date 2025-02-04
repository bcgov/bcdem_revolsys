package com.revolsys.record.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jeometry.common.data.type.DataType;

import com.revolsys.geometry.model.BoundingBox;
import com.revolsys.geometry.model.ClockDirection;
import com.revolsys.geometry.model.GeometryFactory;
import com.revolsys.geometry.model.GeometryFactoryProxy;
import com.revolsys.io.map.MapSerializer;
import com.revolsys.record.Record;
import com.revolsys.record.RecordFactory;
import com.revolsys.record.code.CodeTable;
import com.revolsys.util.CaseConverter;

public interface RecordDefinition extends Cloneable, GeometryFactoryProxy, RecordStoreSchemaElement,
  MapSerializer, RecordDefinitionProxy, RecordFactory<Record> {

  static RecordDefinitionBuilder builder() {
    return new RecordDefinitionBuilder();
  }

  static RecordDefinition newRecordDefinition(final GeometryFactory geometryFactory,
    final DataType dataType) {
    final String name = dataType.getName();
    return new RecordDefinitionBuilder(name) //
      .addField(dataType) //
      .setGeometryFactory(geometryFactory) //
      .getRecordDefinition() //
    ;
  }

  default void addCodeTable(final String fieldName, final CodeTable codeTable) {
    final FieldDefinition field = getField(fieldName);
    if (field != null) {
      field.setCodeTable(codeTable);
    }
  }

  void addDefaultValue(String fieldName, Object defaultValue);

  void deleteRecord(Record record);

  void destroy();

  BoundingBox getBoundingBox();

  <CT extends CodeTable> CT getCodeTable();

  default CodeTable getCodeTable(final String codeTableName) {
    CodeTable codeTable = getCodeTableByFieldName(codeTableName);
    if (codeTable == null) {
      final RecordStore recordStore = getRecordStore();
      if (recordStore != null) {
        codeTable = recordStore.getCodeTable(codeTableName);
      }
    }
    return codeTable;
  }

  CodeTable getCodeTableByFieldName(CharSequence fieldName);

  Object getDefaultValue(String fieldName);

  Map<String, Object> getDefaultValues();

  FieldDefinition getField(CharSequence name);

  FieldDefinition getField(int index);

  Class<?> getFieldClass(CharSequence name);

  Class<?> getFieldClass(int index);

  /**
   * Get the number of fields supported by the type.
   *
   * @return The number of fields.
   */
  @Override
  int getFieldCount();

  /**
   * Get the index of the named field within the list of fields for the
   * type.
   *
   * @param name The field name.
   * @return The index.
   */
  int getFieldIndex(String name);

  /**
   * Get the maximum length of the field.
   *
   * @param index The field index.
   * @return The maximum length.
   */
  int getFieldLength(int index);

  int getFieldLength(String valueFieldName);

  /**
   * Get the name of the field at the specified index.
   *
   * @param index The field index.
   * @return The field name.
   */
  @Override
  String getFieldName(int index);

  /**
   * Get the names of all the fields supported by the type.
   *
   * @return The field names.
   */
  @Override
  List<String> getFieldNames();

  Set<String> getFieldNamesSet();

  List<FieldDefinition> getFields();

  /**
   * Get the maximum number of decimal places of the field
   *
   * @param index The field index.
   * @return The maximum number of decimal places.
   */
  int getFieldScale(int index);

  @Override
  default String getFieldTitle(final String fieldName) {
    final FieldDefinition field = getField(fieldName);
    if (field == null) {
      return CaseConverter.toCapitalizedWords(fieldName);
    } else {
      return field.getTitle();
    }
  }

  List<String> getFieldTitles();

  DataType getFieldType(CharSequence name);

  /**
   * Get the type name of the field at the specified index.
   *
   * @param index The field index.
   * @return The field type name.
   */
  DataType getFieldType(int index);

  default DataType getGeometryDataType() {
    final FieldDefinition field = getGeometryField();
    if (field == null) {
      return null;
    } else {
      return field.getDataType();
    }
  }

  @Override
  FieldDefinition getGeometryField();

  /**
   * Get the index of the primary Geometry field.
   *
   * @return The primary geometry index.
   */
  int getGeometryFieldIndex();

  /**
   * Get the index of all Geometry fields.
   *
   * @return The geometry indexes.
   */
  List<Integer> getGeometryFieldIndexes();

  /**
   * Get the name of the primary Geometry field.
   *
   * @return The primary geometry name.
   */
  @Override
  String getGeometryFieldName();

  /**
   * Get the name of the all Geometry fields.
   *
   * @return The geometry names.
   */
  List<String> getGeometryFieldNames();

  default List<FieldDefinition> getGeometryFields() {
    final List<FieldDefinition> fields = new ArrayList<>();
    for (final int fieldIndex : getGeometryFieldIndexes()) {
      final FieldDefinition field = getField(fieldIndex);
      fields.add(field);
    }
    return fields;
  }

  FieldDefinition getIdField();

  /**
   * Get the index of the Unique identifier field.
   *
   * @return The unique id index.
   */
  int getIdFieldIndex();

  /**
   * Get the index of all ID fields.
   *
   * @return The ID indexes.
   */
  List<Integer> getIdFieldIndexes();

  /**
   * Get the name of the Unique identifier field.
   *
   * @return The unique id name.
   */
  @Override
  String getIdFieldName();

  /**
   * Get the name of the all ID fields.
   *
   * @return The id names.
   */
  @Override
  List<String> getIdFieldNames();

  List<FieldDefinition> getIdFields();

  ClockDirection getPolygonRingDirection();

  @Override
  default RecordDefinition getRecordDefinition() {
    return this;
  }

  RecordDefinitionFactory getRecordDefinitionFactory();

  @Override
  <R extends Record> RecordFactory<R> getRecordFactory();

  @Override
  <V extends RecordStore> V getRecordStore();

  /**
   * Check to see if the type has the specified field name.
   *
   * @param name The name of the field.
   * @return True id the type has the field, false otherwise.
   */
  @Override
  boolean hasField(CharSequence name);

  boolean hasGeometryField();

  @Override
  boolean hasIdField();

  boolean isFieldRequired(CharSequence name);

  /**
   * Return true if a value for the field is required.
   *
   * @param index The field index.
   * @return True if the field is required, false otherwise.
   */
  boolean isFieldRequired(int index);

  boolean isInstanceOf(RecordDefinition classDefinition);

  Record newRecord();

  default Record newRecord(final Record record) {
    final Record newRecord = newRecord();
    newRecord.setValues(record);
    return newRecord;
  }

  @Override
  default Record newRecord(final RecordDefinition recordDefinition) {
    return newRecord();
  }

  void setDefaultValues(Map<String, ? extends Object> defaultValues);

  void setGeometryFactory(com.revolsys.geometry.model.GeometryFactory geometryFactory);
}
