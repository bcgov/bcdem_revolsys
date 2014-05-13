/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 *
 * Copyright (C) 2001 Vivid Solutions
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, contact:
 *
 *     Vivid Solutions
 *     Suite #1A
 *     2328 Government Street
 *     Victoria BC  V8T 5G5
 *     Canada
 *
 *     (250)385-6040
 *     www.vividsolutions.com
 */

package com.revolsys.jts.noding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.revolsys.gis.model.coordinates.list.CoordinatesListUtil;
import com.revolsys.gis.model.coordinates.list.DoubleCoordinatesList;
import com.revolsys.jts.geom.Point;
import com.revolsys.jts.geom.CoordinatesList;

/**
 * Wraps a {@link Noder} and transforms its input
 * into the integer domain.
 * This is intended for use with Snap-Rounding noders,
 * which typically are only intended to work in the integer domain.
 * Offsets can be provided to increase the number of digits of available precision.
 * <p>
 * Clients should be aware that rescaling can involve loss of precision,
 * which can cause zero-length line segments to be created.
 * These in turn can cause problems when used to build a planar graph.
 * This situation should be checked for and collapsed segments removed if necessary.
 *
 * @version 1.7
 */
public class ScaledNoder implements Noder {
  private final Noder noder;

  private final double scaleFactor;

  private double offsetX;

  private double offsetY;

  private boolean isScaled = false;

  public ScaledNoder(final Noder noder, final double scaleFactor) {
    this(noder, scaleFactor, 0, 0);
  }

  public ScaledNoder(final Noder noder, final double scaleFactor,
    final double offsetX, final double offsetY) {
    this.noder = noder;
    this.scaleFactor = scaleFactor;
    // no need to scale if input precision is already integral
    isScaled = !isIntegerPrecision();
  }

  @Override
  public void computeNodes(final Collection<NodedSegmentString> inputSegStrings) {
    Collection<NodedSegmentString> intSegStrings = inputSegStrings;
    if (isScaled) {
      intSegStrings = scale(inputSegStrings);
    }
    noder.computeNodes(intSegStrings);
  }

  @Override
  public Collection<NodedSegmentString> getNodedSubstrings() {
    final Collection<NodedSegmentString> segments = noder.getNodedSubstrings();
    if (isScaled) {
      return rescale(segments);
    }
    return segments;
  }

  public boolean isIntegerPrecision() {
    return scaleFactor == 1.0;
  }

  private Collection<NodedSegmentString> rescale(
    final Collection<NodedSegmentString> segments) {
    final List<NodedSegmentString> newSegments = new ArrayList<NodedSegmentString>();
    for (final NodedSegmentString segment : segments) {
      final NodedSegmentString newSegment = rescale(segment);
      newSegments.add(newSegment);
    }
    return newSegments;
  }

  private NodedSegmentString rescale(final NodedSegmentString segment) {
    final CoordinatesList points = segment.getPoints();
    final int axisCount = points.getAxisCount();
    final int vertexCount = points.size();
    final double[] coordinates = new double[vertexCount * axisCount];
    for (int i = 0; i < vertexCount; i++) {
      final double x = points.getX(i) / scaleFactor + offsetX;
      final double y = points.getY(i) / scaleFactor + offsetY;
      CoordinatesListUtil.setCoordinates(coordinates, axisCount, i, x, y);
      for (int axisIndex = 2; axisIndex < axisCount; axisIndex++) {
        final double value = points.getValue(i, axisIndex);
        coordinates[i * axisCount + axisIndex] = value;
      }
    }
    final DoubleCoordinatesList newPoints = new DoubleCoordinatesList(
      axisCount, coordinates);
    final Object data = segment.getData();
    return new NodedSegmentString(newPoints, data);
  }

  private Collection<NodedSegmentString> scale(
    final Collection<NodedSegmentString> segments) {
    final List<NodedSegmentString> result = new ArrayList<>();
    for (final NodedSegmentString segment : segments) {
      final Object data = segment.getData();
      final CoordinatesList scale = scale(segment);
      final NodedSegmentString nodedSegmentString = new NodedSegmentString(
        scale, data);
      result.add(nodedSegmentString);
    }
    return result;
  }

  private CoordinatesList scale(final NodedSegmentString segment) {
    final int vertexCount = segment.size();
    final int axisCount = segment.getPoints().getAxisCount();
    final double[] coordinates = new double[vertexCount * axisCount];
    double previousX = Double.NaN;
    double previousY = Double.NaN;
    int j = 0;
    for (int i = 0; i < vertexCount; i++) {
      final Point point = segment.getCoordinate(i);
      final double x = Math.round((point.getX() - offsetX) * scaleFactor);
      final double y = Math.round((point.getY() - offsetY) * scaleFactor);
      final double z = point.getZ();
      if (i == 0 || x != previousX && y != previousY) {
        CoordinatesListUtil.setCoordinates(coordinates, axisCount, j++, x, y, z);
      }
      previousX = x;
      previousY = y;
    }
    final CoordinatesList points = new DoubleCoordinatesList(axisCount, j,
      coordinates);
    return points;
  }
}
