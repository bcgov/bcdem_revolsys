package com.revolsys.raster.io.format.tiff.directory.entry;

import java.util.Arrays;

import com.revolsys.io.channels.ChannelReader;
import com.revolsys.io.channels.ChannelWriter;
import com.revolsys.raster.io.format.tiff.code.TiffFieldType;

public class TiffDirectoryEntryDoubleArray extends AbstractTiffDirectoryEntry<double[]> {

  @Override
  public double getDouble(final int index) {
    return this.value[index];
  }

  @Override
  public double[] getDoubleArray() {
    return getValue();
  }

  @Override
  public Number getNumber() {
    if (getCount() == 1) {
      return this.value[0];
    } else {
      throw new IllegalStateException("Cannot get single value from array of size " + getCount());
    }
  }

  @Override
  public Number getNumber(final int index) {
    return this.value[index];
  }

  @Override
  public String getString() {
    return Arrays.toString(this.value);
  }

  @Override
  public TiffFieldType getType() {
    return TiffFieldType.DOUBLE;
  }

  @Override
  protected double[] loadValueDo(final ChannelReader in, final int count) {
    final double[] value = new double[count];
    for (int i = 0; i < count; i++) {
      value[i] = in.getDouble();
    }
    return value;
  }

  @Override
  public void writeValueDo(final ChannelWriter out) {
    for (final double number : this.value) {
      out.putDouble(number);
    }
  }
}
