package com.revolsys.record.query.functions;

import java.util.Arrays;
import java.util.List;

import org.jeometry.common.data.type.DataTypes;

import com.revolsys.geometry.model.Geometry;
import com.revolsys.record.Record;
import com.revolsys.record.query.AbstractBinaryQueryValue;
import com.revolsys.record.query.Condition;
import com.revolsys.record.query.Query;
import com.revolsys.record.query.QueryValue;
import com.revolsys.record.schema.RecordStore;

public class GeometryEqual2d extends AbstractBinaryQueryValue implements Condition, Function {

  public static final String NAME = "ST_EQUALS";

  public GeometryEqual2d(final QueryValue geometry1Value, final QueryValue geometry2Value) {
    super(geometry1Value, geometry2Value);
  }

  @Override
  public void appendDefaultSql(final Query query, final RecordStore recordStore,
    final StringBuilder buffer) {
    buffer.append(NAME);
    buffer.append("(");
    appendLeft(buffer, query, recordStore);
    buffer.append(", ");
    appendRight(buffer, query, recordStore);
    buffer.append(")");
  }

  @Override
  public GeometryEqual2d clone() {
    final GeometryEqual2d clone = (GeometryEqual2d)super.clone();
    return clone;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof GeometryEqual2d) {
      final GeometryEqual2d condition = (GeometryEqual2d)obj;
      return super.equals(condition);
    }
    return false;
  }

  public QueryValue getGeometry1Value() {
    return getLeft();
  }

  public QueryValue getGeometry2Value() {
    return getRight();
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public int getParameterCount() {
    return 2;
  }

  @Override
  public List<QueryValue> getParameters() {
    return Arrays.asList(getLeft(), getRight());
  }

  @Override
  public boolean test(final Record record) {
    final Geometry geometry1 = getLeft().getValue(record);
    final Geometry geometry2 = getRight().getValue(record);
    if (geometry1 == null || geometry2 == null) {
      return false;
    } else {
      return geometry1.equals(2, geometry2);
    }
  }

  @Override
  public String toString() {
    final Object left = getLeft();
    final Object right = getRight();
    return NAME + "(" + DataTypes.toString(left) + "," + DataTypes.toString(right) + ")";
  }

}
