package com.revolsys.record.query;

import com.revolsys.record.Record;
import com.revolsys.record.schema.RecordStore;

public class Parenthesis extends AbstractUnaryQueryValue implements Condition {

  public Parenthesis(final QueryValue value) {
    super(value);
  }

  @Override
  public void appendDefaultSql(final Query query, final RecordStore recordStore,
    final StringBuilder buffer) {
    buffer.append("(");
    super.appendDefaultSql(query, recordStore, buffer);
    buffer.append(")");
  }

  @Override
  public Parenthesis clone() {
    return (Parenthesis)super.clone();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof Parenthesis) {
      return super.equals(obj);
    }
    return false;
  }

  @Override
  public boolean test(final Record record) {
    final QueryValue value = getValue();
    if (value instanceof Condition) {
      final Condition condition = (Condition)value;
      return condition.test(record);
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return "(" + super.toString() + ")";
  }
}
