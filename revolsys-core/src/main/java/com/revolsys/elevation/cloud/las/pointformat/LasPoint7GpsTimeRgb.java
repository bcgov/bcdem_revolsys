package com.revolsys.elevation.cloud.las.pointformat;

import com.revolsys.elevation.cloud.las.LasPointCloud;
import com.revolsys.io.channels.ChannelReader;
import com.revolsys.io.channels.ChannelWriter;
import com.revolsys.record.io.format.json.JsonObject;

public class LasPoint7GpsTimeRgb extends LasPoint6GpsTime implements LasPointRgb {
  private static final long serialVersionUID = 1L;

  private int red;

  private int green;

  private int blue;

  public LasPoint7GpsTimeRgb(final LasPointCloud pointCloud) {
    super(pointCloud);
  }

  @Override
  public int getBlue() {
    return this.blue;
  }

  @Override
  public int getGreen() {
    return this.green;
  }

  @Override
  public LasPointFormat getPointFormat() {
    return LasPointFormat.ExtendedGpsTimeRgb;
  }

  @Override
  public int getRed() {
    return this.red;
  }

  @Override
  public void read(final LasPointCloud pointCloud, final ChannelReader reader) {
    super.read(pointCloud, reader);
    this.red = reader.getUnsignedShort();
    this.green = reader.getUnsignedShort();
    this.blue = reader.getUnsignedShort();
  }

  @Override
  public LasPoint7GpsTimeRgb setBlue(final int blue) {
    if (blue >= 0 && blue <= 65535) {
      this.blue = blue;
    } else {
      throw new IllegalArgumentException("blue must be in range 0..65535: " + blue);
    }
    return this;
  }

  @Override
  public LasPoint7GpsTimeRgb setGreen(final int green) {
    if (green >= 0 && green <= 65535) {
      this.green = green;
    } else {
      throw new IllegalArgumentException("green must be in range 0..65535: " + green);
    }
    return this;
  }

  @Override
  public LasPoint7GpsTimeRgb setRed(final int red) {
    if (red >= 0 && red <= 65535) {
      this.red = red;
    } else {
      throw new IllegalArgumentException("red must be in range 0..65535: " + red);
    }
    return this;
  }

  @Override
  public JsonObject toMap() {
    final JsonObject map = super.toMap();
    addToMap(map, "red", this.red);
    addToMap(map, "green", this.green);
    addToMap(map, "blue", this.blue);
    return map;
  }

  @Override
  public void writeLasPoint(final ChannelWriter out) {
    super.writeLasPoint(out);
    out.putUnsignedShort(this.red);
    out.putUnsignedShort(this.green);
    out.putUnsignedShort(this.blue);
  }
}
