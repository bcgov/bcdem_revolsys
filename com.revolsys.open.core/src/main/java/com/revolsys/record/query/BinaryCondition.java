package com.revolsys.record.query;

import com.revolsys.datatype.DataType;
import com.revolsys.datatype.DataTypes;
import com.revolsys.record.schema.RecordStore;

public class BinaryCondition extends AbstractBinaryQueryValue implements Condition {

  private final String operator;

  public BinaryCondition(final QueryValue left, final String operator, final QueryValue right) {
    super(left, right);
    this.operator = operator;
  }

  public BinaryCondition(final String name, final String operator, final Object value) {
    this(new Column(name), operator, new Value(value));
  }

  @Override
  public void appendDefaultSql(final Query query, final RecordStore recordStore,
    final StringBuilder buffer) {
    appendLeft(buffer, query, recordStore);
    buffer.append(this.operator);
    appendRight(buffer, query, recordStore);
  }

  @Override
  public BinaryCondition clone() {
    final BinaryCondition clone = (BinaryCondition)super.clone();
    return clone;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof BinaryCondition) {
      final BinaryCondition condition = (BinaryCondition)obj;
      if (DataType.equal(condition.getOperator(), this.getOperator())) {
        return super.equals(condition);
      }
    }
    return false;
  }

  public String getOperator() {
    return this.operator;
  }

  @Override
  public String toString() {
    final Object value = getLeft();
    final Object value1 = getRight();
    return DataTypes.toString(value) + " " + this.operator + " " + DataTypes.toString(value1);
  }
}
