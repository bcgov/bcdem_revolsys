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

package com.revolsys.geometry.simplify;

import java.util.Collection;
import java.util.Iterator;

/**
 * Simplifies a collection of TaggedLineStrings, preserving topology
 * (in the sense that no new intersections are introduced).
 * This class is essentially just a container for the common
 * indexes used by {@link TaggedLineStringSimplifier}.
 */
@SuppressWarnings("deprecation")
class TaggedLinesSimplifier {
  private double distanceTolerance = 0.0;

  private final LineSegmentIndex inputIndex = new LineSegmentIndex();

  private final LineSegmentIndex outputIndex = new LineSegmentIndex();

  public TaggedLinesSimplifier() {

  }

  /**
   * Sets the distance tolerance for the simplification.
   * All vertices in the simplified geometry will be within this
   * distance of the original geometry.
   *
   * @param distanceTolerance the approximation tolerance to use
   */
  public void setDistanceTolerance(final double distanceTolerance) {
    this.distanceTolerance = distanceTolerance;
  }

  /**
   * Simplify a collection of TaggedLineStrings
   *
   * @param taggedLines the collection of lines to simplify
   */
  public void simplify(final Collection taggedLines) {
    for (final Iterator i = taggedLines.iterator(); i.hasNext();) {
      this.inputIndex.add((TaggedLineString)i.next());
    }
    for (final Iterator i = taggedLines.iterator(); i.hasNext();) {
      final TaggedLineStringSimplifier tlss = new TaggedLineStringSimplifier(this.inputIndex,
        this.outputIndex);
      tlss.setDistanceTolerance(this.distanceTolerance);
      tlss.simplify((TaggedLineString)i.next());
    }
  }

}
