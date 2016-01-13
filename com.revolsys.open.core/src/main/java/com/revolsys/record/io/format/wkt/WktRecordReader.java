package com.revolsys.record.io.format.wkt;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;

import com.revolsys.collection.iterator.AbstractIterator;
import com.revolsys.geometry.model.Geometry;
import com.revolsys.geometry.model.GeometryFactory;
import com.revolsys.io.FileUtil;
import com.revolsys.io.IoConstants;
import com.revolsys.record.Record;
import com.revolsys.record.RecordFactory;
import com.revolsys.record.Records;
import com.revolsys.record.io.RecordReader;
import com.revolsys.record.property.FieldProperties;
import com.revolsys.record.schema.FieldDefinition;
import com.revolsys.record.schema.RecordDefinition;
import com.revolsys.spring.resource.Resource;

public class WktRecordReader extends AbstractIterator<Record>implements RecordReader {

  private RecordFactory factory;

  private BufferedReader in;

  private RecordDefinition recordDefinition;

  private WktParser wktParser;

  public WktRecordReader(final RecordFactory factory, final Resource resource) {
    this.factory = factory;
    this.in = resource.newBufferedReader();
    this.recordDefinition = Records.newGeometryRecordDefinition();
  }

  @Override
  protected void closeDo() {
    FileUtil.closeSilent(this.in);
    this.factory = null;
    this.in = null;
    this.wktParser = null;
    this.recordDefinition = null;
  }

  @Override
  protected Record getNext() {
    try {
      final String wkt = this.in.readLine();
      final Geometry geometry = this.wktParser.parseGeometry(wkt, false);
      if (geometry == null) {
        throw new NoSuchElementException();
      } else {
        final Record object = this.factory.newRecord(getRecordDefinition());
        object.setGeometryValue(geometry);
        return object;
      }
    } catch (final IOException e) {
      throw new RuntimeException("Error reading geometry ", e);
    }

  }

  @Override
  public RecordDefinition getRecordDefinition() {
    return this.recordDefinition;
  }

  @Override
  protected void initDo() {
    GeometryFactory geometryFactory;
    final FieldDefinition geometryField = this.recordDefinition.getGeometryField();
    if (geometryField == null) {
      geometryFactory = GeometryFactory.floating3();
    } else {
      geometryFactory = geometryField.getProperty(FieldProperties.GEOMETRY_FACTORY);
      if (geometryFactory == null) {
        geometryFactory = getProperty(IoConstants.GEOMETRY_FACTORY);
        if (geometryFactory == null) {
          geometryFactory = GeometryFactory.floating3();
        }
        geometryField.setProperty(FieldProperties.GEOMETRY_FACTORY, geometryFactory);
      }
    }
    this.wktParser = new WktParser(geometryFactory);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
