package com.revolsys.record.query;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jeometry.common.data.type.DataType;
import org.jeometry.common.data.type.DataTypes;

import com.revolsys.geometry.model.BoundingBox;
import com.revolsys.geometry.model.BoundingBoxProxy;
import com.revolsys.geometry.model.editor.BoundingBoxEditor;
import com.revolsys.record.Record;
import com.revolsys.record.query.functions.EnvelopeIntersects;
import com.revolsys.record.query.functions.WithinDistance;
import com.revolsys.record.query.parser.JSqlParser;
import com.revolsys.record.query.parser.SqlParser;
import com.revolsys.record.schema.FieldDefinition;
import com.revolsys.record.schema.RecordDefinition;
import com.revolsys.record.schema.RecordStore;
import com.revolsys.util.Property;

public interface QueryValue extends Cloneable {
  static <V extends QueryValue> List<V> cloneQueryValues(final List<V> values) {
    final List<V> clonedValues = new ArrayList<>();
    for (final QueryValue value : values) {
      if (value == null) {
        clonedValues.add(null);
      } else {
        @SuppressWarnings("unchecked")
        final V clonedValue = (V)value.clone();
        clonedValues.add(clonedValue);
      }
    }
    return clonedValues;
  }

  static QueryValue[] cloneQueryValues(final QueryValue[] oldValues) {
    if (oldValues == null || oldValues.length == 0) {
      return oldValues;
    } else {
      final QueryValue[] clonedValues = new QueryValue[oldValues.length];
      for (int i = 0; i < oldValues.length; i++) {
        final QueryValue value = oldValues[i];
        clonedValues[i] = value.clone();
      }
      return clonedValues;
    }
  }

  static BoundingBox getBoundingBox(final Query query) {
    final Condition whereCondition = query.getWhereCondition();
    return getBoundingBox(whereCondition);
  }

  static BoundingBox getBoundingBox(final QueryValue queryValue) {
    boolean hasBbox = false;
    final BoundingBoxEditor boundingBox = new BoundingBoxEditor();
    if (queryValue != null) {
      for (final QueryValue childValue : queryValue.getQueryValues()) {
        if (childValue instanceof EnvelopeIntersects) {
          final EnvelopeIntersects intersects = (EnvelopeIntersects)childValue;
          final BoundingBox boundingBox1 = getBoundingBox(intersects.getBoundingBox1Value());
          final BoundingBox boundingBox2 = getBoundingBox(intersects.getBoundingBox2Value());
          hasBbox = true;
          boundingBox.addAllBbox(boundingBox1, boundingBox2);
        } else if (childValue instanceof WithinDistance) {
          final WithinDistance withinDistance = (WithinDistance)childValue;
          final BoundingBox boundingBox1 = getBoundingBox(withinDistance.getGeometry1Value());
          final BoundingBox boundingBox2 = getBoundingBox(withinDistance.getGeometry2Value());
          final BoundingBoxEditor withinBoundingBox = BoundingBox.bboxEditor(boundingBox1,
            boundingBox2);
          final double distance = ((Number)((Value)withinDistance.getDistanceValue()).getValue())
            .doubleValue();

          hasBbox = true;
          boundingBox.addBbox(withinBoundingBox.expandDelta(distance));
        } else if (childValue instanceof Value) {
          final Value valueContainer = (Value)childValue;
          final Object value = valueContainer.getValue();
          if (value instanceof BoundingBoxProxy) {
            hasBbox = true;
            boundingBox.addBbox((BoundingBox)value);
          }
        }
      }
    }
    if (hasBbox) {
      return boundingBox;
    } else {
      return null;
    }
  }

  static Condition parseWhere(final RecordDefinition recordDefinition, final String whereClause) {
    if (Property.hasValue(whereClause)) {
      final SqlParser parser = new JSqlParser(recordDefinition);
      return parser.whereToCondition(whereClause);
    } else {
      return null;
    }
  }

  void appendDefaultSql(Query query, RecordStore recordStore, StringBuilder sql);

  // TODO wrap in a more generic structure
  int appendParameters(int index, PreparedStatement statement);

  default void appendSql(final Query query, final RecordStore recordStore,
    final StringBuilder sql) {
    if (recordStore == null) {
      appendDefaultSql(query, null, sql);
    } else {
      recordStore.appendQueryValue(query, sql, this);
    }
  }

  QueryValue clone();

  default List<QueryValue> getQueryValues() {
    return Collections.emptyList();
  }

  default String getStringValue(final Record record) {
    final Object value = getValue(record);
    return DataTypes.toString(value);
  }

  <V> V getValue(Record record);

  default <V> V getValue(final Record record, final DataType dataType) {
    final Object value = getValue(record);
    return dataType.toObject(value);
  }

  default void setFieldDefinition(final FieldDefinition fieldDefinition) {
  }

  default void setRecordDefinition(final RecordDefinition recordDefinition) {
    for (final QueryValue queryValue : getQueryValues()) {
      if (queryValue != null) {
        queryValue.setRecordDefinition(recordDefinition);
      }
    }
  }

  default String toFormattedString() {
    return toString();
  }

  @SuppressWarnings("unchecked")
  default <QV extends QueryValue> QV updateQueryValues(
    final java.util.function.Function<QueryValue, QueryValue> valueHandler) {
    return (QV)this;
  }

}
