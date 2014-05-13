package com.revolsys.gis.model.coordinates.filter;

import com.revolsys.filter.Filter;
import com.revolsys.gis.model.coordinates.LineSegmentUtil;
import com.revolsys.jts.geom.Point;
import com.revolsys.jts.geom.LineSegment;

public class PointOnLineSegment implements Filter<Point> {

  private final LineSegment lineSegment;

  private final double maxDistance;

  public PointOnLineSegment(final LineSegment lineSegment,
    final double maxDistance) {
    this.lineSegment = lineSegment;
    this.maxDistance = maxDistance;
  }

  @Override
  public boolean accept(final Point point) {
    final Point start = lineSegment.get(0);
    final Point end = lineSegment.get(1);
    final boolean onLine = LineSegmentUtil.isPointOnLine(start, end, point,
      maxDistance);
    return onLine;
  }
}
