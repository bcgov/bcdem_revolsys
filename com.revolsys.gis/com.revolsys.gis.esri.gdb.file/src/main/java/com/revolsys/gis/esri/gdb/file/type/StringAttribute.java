package com.revolsys.gis.esri.gdb.file.type;

import com.revolsys.gis.data.model.types.DataTypes;
import com.revolsys.gis.esri.gdb.file.swig.Row;
import com.revolsys.gis.esri.gdb.xml.model.Field;

public class StringAttribute extends AbstractFileGdbAttribute {
  public StringAttribute(final Field field) {
    super(field.getName(), DataTypes.STRING, field.getLength(),
      field.getRequired() == Boolean.TRUE || !field.isIsNullable());
  }

  @Override
  public Object getValue(final Row row) {
    final String name = getName();
    if (row.isNull(name)) {
      return null;
    } else {
      return row.getString(name);
    }
  }

  @Override
  public void setValue(final Row row, final Object value) {
    final String name = getName();
    if (value == null) {
      if (isRequired()) {
        throw new IllegalArgumentException(name
          + " is required and cannot be null");
      } else {
        row.setNull(name);
      }
    } else {
      final String string = value.toString();
      row.setString(name, string);
    }
  }
}
