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
package com.revolsys.jts.geom.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.revolsys.data.equals.NumberEquals;
import com.revolsys.data.types.DataType;
import com.revolsys.data.types.DataTypes;
import com.revolsys.gis.cs.CoordinateSystem;
import com.revolsys.io.wkt.EWktWriter;
import com.revolsys.jts.algorithm.Centroid;
import com.revolsys.jts.algorithm.ConvexHull;
import com.revolsys.jts.algorithm.InteriorPointArea;
import com.revolsys.jts.algorithm.InteriorPointLine;
import com.revolsys.jts.algorithm.InteriorPointPoint;
import com.revolsys.jts.algorithm.PointLocator;
import com.revolsys.jts.geom.BoundingBox;
import com.revolsys.jts.geom.Geometry;
import com.revolsys.jts.geom.GeometryCollection;
import com.revolsys.jts.geom.GeometryFactory;
import com.revolsys.jts.geom.IntersectionMatrix;
import com.revolsys.jts.geom.LineString;
import com.revolsys.jts.geom.Point;
import com.revolsys.jts.geom.Polygon;
import com.revolsys.jts.geom.Polygonal;
import com.revolsys.jts.geom.TopologyException;
import com.revolsys.jts.geom.util.GeometryCollectionMapper;
import com.revolsys.jts.geom.util.GeometryMapper;
import com.revolsys.jts.geom.vertex.Vertex;
import com.revolsys.jts.operation.IsSimpleOp;
import com.revolsys.jts.operation.buffer.Buffer;
import com.revolsys.jts.operation.distance.DistanceOp;
import com.revolsys.jts.operation.linemerge.LineMerger;
import com.revolsys.jts.operation.overlay.OverlayOp;
import com.revolsys.jts.operation.overlay.snap.SnapIfNeededOverlayOp;
import com.revolsys.jts.operation.predicate.RectangleContains;
import com.revolsys.jts.operation.predicate.RectangleIntersects;
import com.revolsys.jts.operation.relate.RelateOp;
import com.revolsys.jts.operation.union.UnaryUnionOp;
import com.revolsys.jts.operation.valid.IsValidOp;

/**
 * A representation of a planar, linear vector geometry.
 * <P>
 *
 *  <H3>Binary Predicates</H3>
 * Because it is not clear at this time
 * what semantics for spatial
 * analysis methods involving <code>GeometryCollection</code>s would be useful,
 * <code>GeometryCollection</code>s are not supported as arguments to binary
 * predicates or the <code>relate</code>
 * method.
 *
 * <H3>Overlay Methods</H3>
 *
 * The overlay methods
 * return the most specific class possible to represent the result. If the
 * result is homogeneous, a <code>Point</code>, <code>LineString</code>, or
 * <code>Polygon</code> will be returned if the result contains a single
 * element; otherwise, a <code>MultiPoint</code>, <code>MultiLineString</code>,
 * or <code>MultiPolygon</code> will be returned. If the result is
 * heterogeneous a <code>GeometryCollection</code> will be returned. <P>
 *
 * Because it is not clear at this time what semantics for set-theoretic
 * methods involving <code>GeometryCollection</code>s would be useful,
 * <code>GeometryCollections</code>
 * are not supported as arguments to the set-theoretic methods.
 *
 *  <H4>Representation of Computed Geometries </H4>
 *
 *  The SFS states that the result
 *  of a set-theoretic method is the "point-set" result of the usual
 *  set-theoretic definition of the operation (SFS 3.2.21.1). However, there are
 *  sometimes many ways of representing a point set as a <code>Geometry</code>.
 *  <P>
 *
 *  The SFS does not specify an unambiguous representation of a given point set
 *  returned from a spatial analysis method. One goal of JTS is to make this
 *  specification precise and unambiguous. JTS uses a canonical form for
 *  <code>Geometry</code>s returned from overlay methods. The canonical
 *  form is a <code>Geometry</code> which is simple and noded:
 *  <UL>
 *    <LI> Simple means that the Geometry returned will be simple according to
 *    the JTS definition of <code>isSimple</code>.
 *    <LI> Noded applies only to overlays involving <code>LineString</code>s. It
 *    means that all intersection points on <code>LineString</code>s will be
 *    present as endpoints of <code>LineString</code>s in the result.
 *  </UL>
 *  This definition implies that non-simple geometries which are arguments to
 *  spatial analysis methods must be subjected to a line-dissolve process to
 *  ensure that the results are simple.
 *
 *  <H4> Constructed Point And The Precision Model </H4>
 *
 *  The results computed by the set-theoretic methods may
 *  contain constructed points which are not present in the input <code>Geometry</code>
 *  s. These new points arise from intersections between line segments in the
 *  edges of the input <code>Geometry</code>s. In the general case it is not
 *  possible to represent constructed points exactly. This is due to the fact
 *  that the coordinates of an intersection point may contain twice as many bits
 *  of precision as the coordinates of the input line segments. In order to
 *  represent these constructed points explicitly, JTS must truncate them to fit
 *  the <code>PrecisionModel</code>. <P>
 *
 *  Unfortunately, truncating coordinates moves them slightly. Line segments
 *  which would not be coincident in the exact result may become coincident in
 *  the truncated representation. This in turn leads to "topology collapses" --
 *  situations where a computed element has a lower dimension than it would in
 *  the exact result. <P>
 *
 *  When JTS detects topology collapses during the computation of spatial
 *  analysis methods, it will throw an exception. If possible the exception will
 *  report the location of the collapse. <P>
 *
 * <h3>Geometry Equality</h3>
 *
 * There are two ways of comparing geometries for equality:
 * <b>structural equality</b> and <b>topological equality</b>.
 *
 * <h4>Structural Equality</h4>
 *
 * Structural Equality is provided by the
 * {@link #equals(2,Geometry)} method.
 * This implements a comparison based on exact, structural pointwise
 * equality.
 * The {@link #equals(Object)} is a synonym for this method,
 * to provide structural equality semantics for
 * use in Java collections.
 * It is important to note that structural pointwise equality
 * is easily affected by things like
 * ring order and component order.  In many situations
 * it will be desirable to normalize geometries before
 * comparing them (using the {@link #norm()}
 * or {@link #normalize()} methods).
 * {@link #equalsNorm(Geometry)} is provided
 * as a convenience method to compute equality over
 * normalized geometries, but it is expensive to use.
 * Finally, {@link #equalsExact(Geometry, double)}
 * allows using a tolerance value for point comparison.
 *
 *
 * <h4>Topological Equality</h4>
 *
 * Topological Equality is provided by the
 * {@link #equalsTopo(Geometry)} method.
 * It implements the SFS definition of point-set equality
 * defined in terms of the DE-9IM matrix.
 * To support the SFS naming convention, the method
 * {@link #equals(Geometry)} is also provided as a synonym.
 * However, due to the potential for confusion with {@link #equals(Object)}
 * its use is discouraged.
 * <p>
 * Since {@link #equals(Object)} and {@link #hashCode()} are overridden,
 * Geometries can be used effectively in Java collections.
 *
 *@version 1.7
 */
public abstract class AbstractGeometry implements Geometry {
  public static int[] createVertexId(final int[] partId, final int vertexIndex) {
    final int[] vertexId = new int[partId.length + 1];
    System.arraycopy(partId, 0, vertexId, 0, partId.length);
    vertexId[partId.length] = vertexIndex;
    return vertexId;
  }

  public static int getVertexIndex(final int[] index) {
    final int length = index.length;
    final int lastIndex = length - 1;
    return index[lastIndex];
  }

  /**
   * Returns true if the array contains any non-empty <code>Geometry</code>s.
   *
   *@param  geometries  an array of <code>Geometry</code>s; no elements may be
   *      <code>null</code>
   *@return             <code>true</code> if any of the <code>Geometry</code>s
   *      <code>isEmpty</code> methods return <code>false</code>
   */
  protected static boolean hasNonEmptyElements(final Geometry... geometries) {
    for (final Geometry geometry : geometries) {
      if (!geometry.isEmpty()) {
        return true;
      }
    }
    return false;
  }

  /**
   *  Returns true if the array contains any <code>null</code> elements.
   *
   *@param  array  an array to validate
   *@return        <code>true</code> if any of <code>array</code>s elements are
   *      <code>null</code>
   */
  protected static boolean hasNullElements(final Object[] array) {
    for (int i = 0; i < array.length; i++) {
      if (array[i] == null) {
        return true;
      }
    }
    return false;
  }

  public static int[] setVertexIndex(final int[] vertexId, final int vertexIndex) {
    final int length = vertexId.length;
    final int lastIndex = length - 1;
    final int[] newVertextId = new int[length];
    System.arraycopy(vertexId, 0, newVertextId, 0, lastIndex);
    newVertextId[lastIndex] = vertexIndex;
    return newVertextId;
  }

  private static final long serialVersionUID = 8763622679187376702L;

  private static final List<String> sortedGeometryTypes = Arrays.asList(
    "Point", "MultiPoint", "LineString", "LinearRing", "MultiLineString",
    "Polygon", "MultiPolygon", "GeometryCollection");

  /**
   * Computes a buffer area around this geometry having the given width. The
   * buffer of a Geometry is the Minkowski sum or difference of the geometry
   * with a disc of radius <code>abs(distance)</code>.
   * <p>
   * Mathematically-exact buffer area boundaries can contain circular arcs.
   * To represent these arcs using linear geometry they must be approximated with line segments.
   * The buffer geometry is constructed using 8 segments per quadrant to approximate
   * the circular arcs.
   * The end cap style is <code>CAP_ROUND</code>.
   * <p>
   * The buffer operation always returns a polygonal result. The negative or
   * zero-distance buffer of lines and points is always an empty {@link Polygon}.
   * This is also the result for the buffers of degenerate (zero-area) polygons.
   *
   * @param distance
   *          the width of the buffer (may be positive, negative or 0)
   * @return a polygonal geometry representing the buffer region (which may be
   *         empty)
   *
   * @throws TopologyException
   *           if a robustness error occurs
   *
   * @see #buffer(double, int)
   * @see #buffer(double, int, int)
   */
  @Override
  public Geometry buffer(final double distance) {
    return Buffer.buffer(this, distance);
  }

  /**
   * Computes a buffer area around this geometry having the given width and with
   * a specified accuracy of approximation for circular arcs.
   * <p>
   * Mathematically-exact buffer area boundaries can contain circular arcs.
   * To represent these arcs
   * using linear geometry they must be approximated with line segments. The
   * <code>quadrantSegments</code> argument allows controlling the accuracy of
   * the approximation by specifying the number of line segments used to
   * represent a quadrant of a circle
   * <p>
   * The buffer operation always returns a polygonal result. The negative or
   * zero-distance buffer of lines and points is always an empty {@link Polygon}.
   * This is also the result for the buffers of degenerate (zero-area) polygons.
   *
   * @param distance
   *          the width of the buffer (may be positive, negative or 0)
   * @param quadrantSegments
   *          the number of line segments used to represent a quadrant of a
   *          circle
   * @return a polygonal geometry representing the buffer region (which may be
   *         empty)
   *
   * @throws TopologyException
   *           if a robustness error occurs
   *
   * @see #buffer(double)
   * @see #buffer(double, int, int)
   */
  @Override
  public Geometry buffer(final double distance, final int quadrantSegments) {
    return Buffer.buffer(this, distance, quadrantSegments);
  }

  /**
   * Computes a buffer area around this geometry having the given
   * width and with a specified accuracy of approximation for circular arcs,
   * and using a specified end cap style.
   * <p>
   * Mathematically-exact buffer area boundaries can contain circular arcs.
   * To represent these arcs using linear geometry they must be approximated with line segments.
   * The <code>quadrantSegments</code> argument allows controlling the
   * accuracy of the approximation
   * by specifying the number of line segments used to represent a quadrant of a circle
   * <p>
   * The end cap style specifies the buffer geometry that will be
   * created at the ends of linestrings.  The styles provided are:
   * <ul>
   * <li><code>Buffer.CAP_ROUND</code> - (default) a semi-circle
   * <li><code>Buffer.CAP_BUTT</code> - a straight line perpendicular to the end segment
   * <li><code>Buffer.CAP_SQUARE</code> - a half-square
   * </ul>
   * <p>
   * The buffer operation always returns a polygonal result. The negative or
   * zero-distance buffer of lines and points is always an empty {@link Polygon}.
   * This is also the result for the buffers of degenerate (zero-area) polygons.
   *
   *@param  distance  the width of the buffer (may be positive, negative or 0)
   *@param quadrantSegments the number of line segments used to represent a quadrant of a circle
   *@param endCapStyle the end cap style to use
   *@return a polygonal geometry representing the buffer region (which may be empty)
   *
   * @throws TopologyException if a robustness error occurs
   *
   * @see #buffer(double)
   * @see #buffer(double, int)
   * @see Buffer
   */
  @Override
  public Geometry buffer(final double distance, final int quadrantSegments,
    final int endCapStyle) {
    return Buffer.buffer(this, distance, quadrantSegments, endCapStyle);
  }

  /**
   *  Throws an exception if <code>g</code>'s class is <code>GeometryCollection</code>
   *  . (Its subclasses do not trigger an exception).
   *
   *@param  geometry                          the <code>Geometry</code> to check
   *@throws  IllegalArgumentException  if <code>g</code> is a <code>GeometryCollection</code>
   *      but not one of its subclasses
   */
  protected void checkNotGeometryCollection(final Geometry geometry) {
    final DataType dataType = geometry.getDataType();
    if (dataType.equals(DataTypes.GEOMETRY_COLLECTION)) {
      throw new IllegalArgumentException(
          "This method does not support GeometryCollection arguments");
    }
  }

  /**
   * Creates and returns a full copy of this {@link Geometry} object
   * (including all coordinates contained by it).
   * Subclasses are responsible for overriding this method and copying
   * their internal data.  Overrides should call this method first.
   *
   * @return a clone of this instance
   */
  @Override
  public AbstractGeometry clone() {
    try {
      final AbstractGeometry clone = (AbstractGeometry)super.clone();
      return clone;
    } catch (final CloneNotSupportedException e) {
      return null;
    }
  }

  /**
   *  Returns the first non-zero result of <code>compareTo</code> encountered as
   *  the two <code>Collection</code>s are iterated over. If, by the time one of
   *  the iterations is complete, no non-zero result has been encountered,
   *  returns 0 if the other iteration is also complete. If <code>b</code>
   *  completes before <code>a</code>, a positive number is returned; if a
   *  before b, a negative number.
   *
   *@param  a  a <code>Collection</code> of <code>Comparable</code>s
   *@param  b  a <code>Collection</code> of <code>Comparable</code>s
   *@return    the first non-zero <code>compareTo</code> result, if any;
   *      otherwise, zero
   */
  @SuppressWarnings({
    "rawtypes", "unchecked"
  })
  protected int compare(final Collection a, final Collection b) {
    final Iterator i = a.iterator();
    final Iterator j = b.iterator();
    while (i.hasNext() && j.hasNext()) {
      final Comparable aElement = (Comparable)i.next();
      final Comparable bElement = (Comparable)j.next();
      final int comparison = aElement.compareTo(bElement);
      if (comparison != 0) {
        return comparison;
      }
    }
    if (i.hasNext()) {
      return 1;
    }
    if (j.hasNext()) {
      return -1;
    }
    return 0;
  }

  /**
   *  Returns whether this <code>Geometry</code> is greater than, equal to,
   *  or less than another <code>Geometry</code>. <P>
   *
   *  If their classes are different, they are compared using the following
   *  ordering:
   *  <UL>
   *    <LI> Point (lowest)
   *    <LI> MultiPoint
   *    <LI> LineString
   *    <LI> LinearRing
   *    <LI> MultiLineString
   *    <LI> Polygon
   *    <LI> MultiPolygon
   *    <LI> GeometryCollection (highest)
   *  </UL>
   *  If the two <code>Geometry</code>s have the same class, their first
   *  elements are compared. If those are the same, the second elements are
   *  compared, etc.
   *
   *@param  other  a <code>Geometry</code> with which to compare this <code>Geometry</code>
   *@return    a positive number, 0, or a negative number, depending on whether
   *      this object is greater than, equal to, or less than <code>o</code>, as
   *      defined in "Normal Form For Geometry" in the JTS Technical
   *      Specifications
   */
  @Override
  public int compareTo(final Object other) {
    if (other instanceof Geometry) {
      final Geometry geometry = (Geometry)other;
      if (getClassSortIndex() != geometry.getClassSortIndex()) {
        return getClassSortIndex() - geometry.getClassSortIndex();
      } else if (isEmpty() && geometry.isEmpty()) {
        return 0;
      } else if (isEmpty()) {
        return -1;
      } else if (geometry.isEmpty()) {
        return 1;
      } else {
        return compareToSameClass(geometry);
      }
    } else {
      return -1;
    }
  }

  /**
   *  Returns whether this <code>Geometry</code> is greater than, equal to,
   *  or less than another <code>Geometry</code> having the same class.
   *
   *@param  o  a <code>Geometry</code> having the same class as this <code>Geometry</code>
   *@return    a positive number, 0, or a negative number, depending on whether
   *      this object is greater than, equal to, or less than <code>o</code>, as
   *      defined in "Normal Form For Geometry" in the JTS Technical
   *      Specifications
   */
  @Override
  public abstract int compareToSameClass(Geometry o);

  /**
   *  Returns the minimum and maximum x and y values in this <code>Geometry</code>
   *  , or a null <code>BoundingBoxDoubleGf</code> if this <code>Geometry</code> is empty.
   *  Unlike <code>getEnvelopeInternal</code>, this method calculates the <code>BoundingBoxDoubleGf</code>
   *  each time it is called; <code>getEnvelopeInternal</code> caches the result
   *  of this method.
   *
   *@return    this <code>Geometry</code>s bounding box; if the <code>Geometry</code>
   *      is empty, <code>BoundingBoxDoubleGf#isNull</code> will return <code>true</code>
   */
  protected abstract BoundingBox computeBoundingBox();

  /**
   * Tests whether this geometry contains the
   * argument geometry.
   * <p>
   * The <code>contains</code> predicate has the following equivalent definitions:
   * <ul>
   * <li>Every point of the other geometry is a point of this geometry,
   * and the interiors of the two geometries have at least one point in common.
   * <li>The DE-9IM Intersection Matrix for the two geometries matches
   * the pattern
   * <code>[T*****FF*]</code>
   * <li><code>g.within(this) = true</code>
   * <br>(<code>contains</code> is the converse of {@link #within} )
   * </ul>
   * An implication of the definition is that "Geometries do not
   * contain their boundary".  In other words, if a geometry A is a subset of
   * the points in the boundary of a geometry B, <code>B.contains(A) = false</code>.
   * (As a concrete example, take A to be a LineString which lies in the boundary of a Polygon B.)
   * For a predicate with similar behaviour but avoiding
   * this subtle limitation, see {@link #covers}.
   *
   *@param  g  the <code>Geometry</code> with which to compare this <code>Geometry</code>
   *@return        <code>true</code> if this <code>Geometry</code> contains <code>g</code>
   *
   * @see Geometry#within
   * @see Geometry#covers
   */
  @Override
  public boolean contains(final Geometry g) {
    // short-circuit test
    final BoundingBox boundingBox = getBoundingBox();
    final BoundingBox otherBoundingBox = g.getBoundingBox();
    if (!boundingBox.covers(otherBoundingBox)) {
      return false;
    }
    // optimization for rectangle arguments
    if (isRectangle()) {
      return RectangleContains.contains((Polygon)this, g);
    }
    // general case
    return relate(g).isContains();
  }

  @Override
  public boolean containsProperly(final Geometry geometry) {
    if (getBoundingBox().covers(geometry.getBoundingBox())) {
      return relate(geometry, "T**FF*FF*");
    } else {
      return false;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V extends Geometry> V convert(final GeometryFactory geometryFactory) {
    final GeometryFactory sourceGeometryFactory = getGeometryFactory();
    if (geometryFactory == null || sourceGeometryFactory == geometryFactory) {
      return (V)this;
    } else {
      return (V)copy(geometryFactory);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <V extends Geometry> V convert(GeometryFactory geometryFactory,
    final int axisCount) {
    if (geometryFactory != null) {
      geometryFactory = geometryFactory.convertAxisCount(axisCount);
    }
    final GeometryFactory sourceGeometryFactory = getGeometryFactory();
    boolean copy = false;
    if (geometryFactory != null && sourceGeometryFactory != geometryFactory) {
      final int srid = getSrid();
      final int srid2 = geometryFactory.getSrid();
      if (srid <= 0) {
        if (srid2 > 0) {
          copy = true;
        }
      } else if (srid != srid2) {
        copy = true;
      }
      if (!copy) {
        for (int axisIndex = 0; axisIndex < axisCount; axisIndex++) {
          final double scale = sourceGeometryFactory.getScale(axisIndex);
          final double scale1 = geometryFactory.getScale(axisIndex);
          if (!NumberEquals.equal(scale, scale1)) {
            copy = true;
          }
        }
      }
    }
    if (copy) {
      return (V)copy(geometryFactory);
    } else {
      return (V)this;
    }
  }

  /**
   *  Computes the smallest convex <code>Polygon</code> that contains all the
   *  points in the <code>Geometry</code>. This obviously applies only to <code>Geometry</code>
   *  s which contain 3 or more points; the results for degenerate cases are
   *  specified as follows:
   *  <TABLE>
   *    <TR>
   *      <TH>    Number of <code>Point</code>s in argument <code>Geometry</code>   </TH>
   *      <TH>    <code>Geometry</code> class of result     </TH>
   *    </TR>
   *    <TR>
   *      <TD>        0      </TD>
   *      <TD>        empty <code>GeometryCollection</code>      </TD>
   *    </TR>
   *    <TR>  <TD>      1     </TD>
   *      <TD>     <code>Point</code>     </TD>
   *    </TR>
   *    <TR>
   *      <TD>      2     </TD>
   *      <TD>     <code>LineString</code>     </TD>
   *    </TR>
   *    <TR>
   *      <TD>       3 or more     </TD>
   *      <TD>      <code>Polygon</code>     </TD>
   *    </TR>
   *  </TABLE>
   *
   *@return    the minimum-area convex polygon containing this <code>Geometry</code>'
   *      s points
   */
  @Override
  public Geometry convexHull() {
    final ConvexHull convexHull = new ConvexHull(this);
    return convexHull.getConvexHull();
  }

  /**
   * Tests whether this geometry is covered by the
   * argument geometry.
   * <p>
   * The <code>coveredBy</code> predicate has the following equivalent definitions:
   * <ul>
   * <li>Every point of this geometry is a point of the other geometry.
   * <li>The DE-9IM Intersection Matrix for the two geometries matches
   * at least one of the following patterns:
   *  <ul>
   *   <li><code>[T*F**F***]</code>
   *   <li><code>[*TF**F***]</code>
   *   <li><code>[**FT*F***]</code>
   *   <li><code>[**F*TF***]</code>
   *  </ul>
   * <li><code>g.covers(this) = true</code>
   * <br>(<code>coveredBy</code> is the converse of {@link #covers})
   * </ul>
   * If either geometry is empty, the value of this predicate is <code>false</code>.
   * <p>
   * This predicate is similar to {@link #within},
   * but is more inclusive (i.e. returns <code>true</code> for more cases).
   *
   *@param  g  the <code>Geometry</code> with which to compare this <code>Geometry</code>
   *@return        <code>true</code> if this <code>Geometry</code> is covered by <code>g</code>
   *
   * @see Geometry#within
   * @see Geometry#covers
   */
  @Override
  public boolean coveredBy(final Geometry g) {
    return g.covers(this);
  }

  /**
   * Tests whether this geometry covers the
   * argument geometry.
   * <p>
   * The <code>covers</code> predicate has the following equivalent definitions:
   * <ul>
   * <li>Every point of the other geometry is a point of this geometry.
   * <li>The DE-9IM Intersection Matrix for the two geometries matches
   * at least one of the following patterns:
   *  <ul>
   *   <li><code>[T*****FF*]</code>
   *   <li><code>[*T****FF*]</code>
   *   <li><code>[***T**FF*]</code>
   *   <li><code>[****T*FF*]</code>
   *  </ul>
   * <li><code>g.coveredBy(this) = true</code>
   * <br>(<code>covers</code> is the converse of {@link #coveredBy})
   * </ul>
   * If either geometry is empty, the value of this predicate is <code>false</code>.
   * <p>
   * This predicate is similar to {@link #contains},
   * but is more inclusive (i.e. returns <code>true</code> for more cases).
   * In particular, unlike <code>contains</code> it does not distinguish between
   * points in the boundary and in the interior of geometries.
   * For most situations, <code>covers</code> should be used in preference to <code>contains</code>.
   * As an added benefit, <code>covers</code> is more amenable to optimization,
   * and hence should be more performant.
   *
   *@param  g  the <code>Geometry</code> with which to compare this <code>Geometry</code>
   *@return        <code>true</code> if this <code>Geometry</code> covers <code>g</code>
   *
   * @see Geometry#contains
   * @see Geometry#coveredBy
   */
  @Override
  public boolean covers(final Geometry g) {
    // short-circuit test
    if (!getBoundingBox().covers(g.getBoundingBox())) {
      return false;
    }
    // optimization for rectangle arguments
    if (isRectangle()) {
      // since we have already tested that the test boundingBox is covered
      return true;
    }
    return relate(g).isCovers();
  }

  /**
   * Tests whether this geometry crosses the
   * argument geometry.
   * <p>
   * The <code>crosses</code> predicate has the following equivalent definitions:
   * <ul>
   * <li>The geometries have some but not all interior points in common.
   * <li>The DE-9IM Intersection Matrix for the two geometries matches
   * one of the following patterns:
   *   <ul>
   *    <li><code>[T*T******]</code> (for P/L, P/A, and L/A situations)
   *    <li><code>[T*****T**]</code> (for L/P, A/P, and A/L situations)
   *    <li><code>[0********]</code> (for L/L situations)
   *   </ul>
   * </ul>
   * For any other combination of dimensions this predicate returns <code>false</code>.
   * <p>
   * The SFS defined this predicate only for P/L, P/A, L/L, and L/A situations.
   * In order to make the relation symmetric,
   * JTS extends the definition to apply to L/P, A/P and A/L situations as well.
   *
   *@param  g  the <code>Geometry</code> with which to compare this <code>Geometry</code>
   *@return        <code>true</code> if the two <code>Geometry</code>s cross.
   */
  @Override
  public boolean crosses(final Geometry g) {
    // short-circuit test
    if (!getBoundingBox().intersects(g.getBoundingBox())) {
      return false;
    }
    return relate(g).isCrosses(getDimension(), g.getDimension());
  }

  /**
   * Computes a <code>Geometry</code> representing the closure of the point-set
   * of the points contained in this <code>Geometry</code> that are not contained in
   * the <code>other</code> Geometry.
   * <p>
   * If the result is empty, it is an atomic geometry
   * with the dimension of the left-hand input.
   * <p>
   * Non-empty {@link GeometryCollection} arguments are not supported.
   *
   *@param  other  the <code>Geometry</code> with which to compute the
   *      difference
   *@return a Geometry representing the point-set difference of this <code>Geometry</code> with
   *      <code>other</code>
   * @throws TopologyException if a robustness error occurs
   * @throws IllegalArgumentException if either input is a non-empty GeometryCollection
   */
  @Override
  public Geometry difference(final Geometry other) {
    // special case: if A.isEmpty ==> empty; if B.isEmpty ==> A
    if (this.isEmpty()) {
      return OverlayOp.createEmptyResult(OverlayOp.DIFFERENCE, this, other,
        getGeometryFactory());
    }
    if (other.isEmpty()) {
      return clone();
    }

    checkNotGeometryCollection(this);
    checkNotGeometryCollection(other);
    return SnapIfNeededOverlayOp.overlayOp(this, other, OverlayOp.DIFFERENCE);
  }

  /**
   * Tests whether this geometry is disjoint from the argument geometry.
   * <p>
   * The <code>disjoint</code> predicate has the following equivalent definitions:
   * <ul>
   * <li>The two geometries have no point in common
   * <li>The DE-9IM Intersection Matrix for the two geometries matches
   * <code>[FF*FF****]</code>
   * <li><code>! g.intersects(this) = true</code>
   * <br>(<code>disjoint</code> is the inverse of <code>intersects</code>)
   * </ul>
   *
   *@param  g  the <code>Geometry</code> with which to compare this <code>Geometry</code>
   *@return        <code>true</code> if the two <code>Geometry</code>s are
   *      disjoint
   *
   * @see Geometry#intersects
   */
  @Override
  public boolean disjoint(final Geometry g) {
    return !intersects(g);
  }

  /**
   *  Returns the minimum distance between this <code>Geometry</code>
   *  and another <code>Geometry</code>.
   *
   * @param  geometry the <code>Geometry</code> from which to compute the distance
   * @return the distance between the geometries
   * @return 0 if either input geometry is empty
   * @throws IllegalArgumentException if g is null
   */
  @Override
  public double distance(Geometry geometry) {
    final GeometryFactory geometryFactory = getGeometryFactory();
    geometry = geometry.convert(geometryFactory, 2);
    return DistanceOp.distance(this, geometry);
  }

  protected abstract boolean doEquals(int axisCount, Geometry geometry);

  public boolean envelopeCovers(final Geometry geometry) {
    if (getBoundingBox().covers(geometry.getBoundingBox())) {
      return true;
    } else {
      return false;
    }
  }

  public boolean envelopesIntersect(final Geometry geometry) {
    if (getBoundingBox().intersects(geometry.getBoundingBox())) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean equal(final Point a, final Point b, final double tolerance) {
    if (tolerance == 0) {
      return a.equals(b);
    } else {
      return a.distance(b) <= tolerance;
    }
  }

  /**
   * Tests whether this geometry is
   * topologically equal to the argument geometry.
   * <p>
   * This method is included for backward compatibility reasons.
   * It has been superseded by the {@link #equalsTopo(Geometry)} method,
   * which has been named to clearly denote its functionality.
   * <p>
   * This method should NOT be confused with the method
   * {@link #equals(Object)}, which implements
   * an exact equality comparison.
   *
   *@param  g  the <code>Geometry</code> with which to compare this <code>Geometry</code>
   *@return true if the two <code>Geometry</code>s are topologically equal
   *
   *@see #equalsTopo(Geometry)
   */
  @Override
  public boolean equals(final Geometry g) {
    if (g == null) {
      return false;
    }
    return equalsTopo(g);
  }

  @Override
  public boolean equals(final int axisCount, final Geometry geometry) {
    if (geometry == this) {
      return true;
    } else if (geometry == null) {
      return false;
    } else if (axisCount < 2) {
      throw new IllegalArgumentException("Axis Count must be >=2");
    } else if (isEquivalentClass(geometry)) {
      if (isEmpty()) {
        return geometry.isEmpty();
      } else if (geometry.isEmpty()) {
        return false;
      } else {
        return doEquals(axisCount, geometry);
      }
    }
    return false;
  }

  /**
   * Tests whether this geometry is structurally and numerically equal
   * to a given <code>Object</code>.
   * If the argument <code>Object</code> is not a <code>Geometry</code>,
   * the result is <code>false</code>.
   * Otherwise, the result is computed using
   * {@link #equals(2,Geometry)}.
   * <p>
   * This method is provided to fulfill the Java contract
   * for value-based object equality.
   * In conjunction with {@link #hashCode()}
   * it provides semantics which are most useful
   * for using
   * <code>Geometry</code>s as keys and values in Java collections.
   * <p>
   * Note that to produce the expected result the input geometries
   * should be in normal form.  It is the caller's
   * responsibility to perform this where required
   * (using {@link Geometry#norm()
   * or {@link #normalize()} as appropriate).
   *
   * @param other the Object to compare
   * @return true if this geometry is exactly equal to the argument
   *
   * @see #equals(2,Geometry)
   * @see #hashCode()
   * @see #norm()
   * @see #normalize()
   */
  @Override
  public boolean equals(final Object other) {
    if (other instanceof Geometry) {
      final Geometry geometry = (Geometry)other;
      return equals(2, geometry);
    } else {
      return false;
    }
  }

  @Override
  public boolean equalsExact(final Geometry geometry) {
    if (geometry == null) {
      return false;
    } else {
      final int axisCount = getAxisCount();
      final int axisCount2 = geometry.getAxisCount();
      if (axisCount == axisCount2) {
        final int srid = getSrid();
        final int otherSrid = geometry.getSrid();
        if (srid == 0 || otherSrid == 0 || srid == otherSrid) {
          return equals(axisCount, geometry);
        }
      }
    }
    return false;
  }

  /**
   * Returns true if the two <code>Geometry</code>s are exactly equal,
   * up to a specified distance tolerance.
   * Two Geometries are exactly equal within a distance tolerance
   * if and only if:
   * <ul>
   * <li>they have the same structure
   * <li>they have the same values for their vertices,
   * within the given tolerance distance, in exactly the same order.
   * </ul>
   * This method does <i>not</i>
   * test the values of the <code>GeometryFactory</code>, the <code>SRID</code>,
   * or the <code>userData</code> fields.
   * <p>
   * To properly test equality between different geometries,
   * it is usually necessary to {@link #normalize()} them first.
   *
   * @param other the <code>Geometry</code> with which to compare this <code>Geometry</code>
   * @param tolerance distance at or below which two <code>Coordinate</code>s
   *   are considered equal
   * @return <code>true</code> if this and the other <code>Geometry</code>
   *   have identical structure and point values, up to the distance tolerance.
   *
   * @see #equals(2,Geometry)
   * @see #normalize()
   * @see #norm()
   */
  @Override
  public abstract boolean equalsExact(Geometry other, double tolerance);

  /**
   * Tests whether two geometries are exactly equal
   * in their normalized forms.
   * This is a convenience method which creates normalized
   * versions of both geometries before computing
   * {@link #equals(2,Geometry)}.
   * <p>
   * This method is relatively expensive to compute.
   * For maximum performance, the client
   * should instead perform normalization on the individual geometries
   * at an appropriate point during processing.
   *
   * @param g a Geometry
   * @return true if the input geometries are exactly equal in their normalized form
   */
  @Override
  public boolean equalsNorm(final Geometry g) {
    if (g == null) {
      return false;
    }
    return normalize().equals(2, g.normalize());
  }

  /**
   * Tests whether this geometry is topologically equal to the argument geometry
   * as defined by the SFS <code>equals</code> predicate.
   * <p>
   * The SFS <code>equals</code> predicate has the following equivalent definitions:
   * <ul>
   * <li>The two geometries have at least one point in common,
   * and no point of either geometry lies in the exterior of the other geometry.
   * <li>The DE-9IM Intersection Matrix for the two geometries matches
   * the pattern <code>T*F**FFF*</code>
   * <pre>
   * T*F
   * **F
   * FF*
   * </pre>
   * </ul>
   * <b>Note</b> that this method computes <b>topologically equality</b>.
   * For structural equality, see {@link #equals(2,Geometry)}.
   *
   *@param g the <code>Geometry</code> with which to compare this <code>Geometry</code>
   *@return <code>true</code> if the two <code>Geometry</code>s are topologically equal
   *
   *@see #equals(2,Geometry)
   */
  @Override
  public boolean equalsTopo(final Geometry g) {
    // short-circuit test
    if (!getBoundingBox().equals(g.getBoundingBox())) {
      return false;
    }
    return relate(g).isEquals(getDimension(), g.getDimension());
  }

  @Override
  public Iterable<Geometry> geometries() {
    return getGeometries();
  }

  /**
   *  Returns the area of this <code>Geometry</code>.
   *  Areal Geometries have a non-zero area.
   *  They override this function to compute the area.
   *  Others return 0.0
   *
   *@return the area of the Geometry
   */
  @Override
  public double getArea() {
    return 0.0;
  }

  @Override
  public int getAxisCount() {
    return getGeometryFactory().getAxisCount();
  }

  /**
   * Returns the boundary, or an empty geometry of appropriate dimension
   * if this <code>Geometry</code>  is empty.
   * (In the case of zero-dimensional geometries, '
   * an empty GeometryCollection is returned.)
   * For a discussion of this function, see the OpenGIS Simple
   * Features Specification. As stated in SFS Section 2.1.13.1, "the boundary
   * of a Geometry is a set of Geometries of the next lower dimension."
   *
   *@return    the closure of the combinatorial boundary of this <code>Geometry</code>
   */
  @Override
  public abstract Geometry getBoundary();

  /**
   *  Returns the dimension of this <code>Geometry</code>s inherent boundary.
   *
   *@return    the dimension of the boundary of the class implementing this
   *      interface, whether or not this object is the empty geometry. Returns
   *      <code>Dimension.FALSE</code> if the boundary is the empty geometry.
   */
  @Override
  public abstract int getBoundaryDimension();

  /**
   * Gets an {@link BoundingBoxDoubleGf} containing
   * the minimum and maximum x and y values in this <code>Geometry</code>.
   * If the geometry is empty, an empty <code>BoundingBoxDoubleGf</code>
   * is returned.
   * <p>
   * The returned object is a copy of the one maintained internally,
   * to avoid aliasing issues.
   * For best performance, clients which access this
   * boundingBox frequently should cache the return value.
   *
   *@return the boundingBox of this <code>Geometry</code>.
   *@return an empty BoundingBox if this Geometry is empty
   */
  @Override
  public BoundingBox getBoundingBox() {
    return computeBoundingBox();
  }

  /**
   * Computes the centroid of this <code>Geometry</code>.
   * The centroid
   * is equal to the centroid of the set of component Geometries of highest
   * dimension (since the lower-dimension geometries contribute zero
   * "weight" to the centroid).
   * <p>
   * The centroid of an empty geometry is <code>POINT EMPTY</code>.
   *
   * @return a {@link Point} which is the centroid of this Geometry
   */
  @Override
  public Point getCentroid() {
    if (isEmpty()) {
      return getGeometryFactory().point();
    }
    final Point centPt = Centroid.getCentroid(this);
    return getGeometryFactory().point(centPt);
  }

  @Override
  public int getClassSortIndex() {
    final String geometryType = getGeometryType();
    final int index = sortedGeometryTypes.indexOf(geometryType);
    return index;
  }

  /**
   *
   * @author Paul Austin <paul.austin@revolsys.com>
   * @return
   */
  @Override
  public CoordinateSystem getCoordinateSystem() {
    return getGeometryFactory().getCoordinateSystem();
  }

  @Override
  public DataType getDataType() {
    return DataTypes.GEOMETRY;
  }

  /**
   * Returns the dimension of this geometry.
   * The dimension of a geometry is is the topological
   * dimension of its embedding in the 2-D Euclidean plane.
   * In the JTS spatial model, dimension values are in the set {0,1,2}.
   * <p>
   * Note that this is a different concept to the dimension of
   * the vertex {@link Coordinates}s.
   * The geometry dimension can never be greater than the coordinate dimension.
   * For example, a 0-dimensional geometry (e.g. a Point)
   * may have a coordinate dimension of 3 (X,Y,Z).
   *
   *@return the topological dimension of this geometry.
   */
  @Override
  public abstract int getDimension();

  /**
   *  Gets a Geometry representing the boundingBox (bounding box) of
   *  this <code>Geometry</code>.
   *  <p>
   *  If this <code>Geometry</code> is:
   *  <ul>
   *  <li>empty, returns an empty <code>Point</code>.
   *  <li>a point, returns a <code>Point</code>.
   *  <li>a line parallel to an axis, a two-vertex <code>LineString</code>
   *  <li>otherwise, returns a
   *  <code>Polygon</code> whose vertices are (minx miny, maxx miny,
   *  maxx maxy, minx maxy, minx miny).
   *  </ul>
   *
   *@return a Geometry representing the boundingBox of this Geometry
   *
   * @see GeometryFactory#toLineString(BoundingBoxDoubleGf)
   */
  @Override
  public Geometry getEnvelope() {
    return getBoundingBox().toGeometry();
  }

  /**
   * @author Paul Austin <paul.austin@revolsys.com>
   */
  @Override
  @SuppressWarnings("unchecked")
  public <V extends Geometry> List<V> getGeometries() {
    return (List<V>)Arrays.asList(this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V extends Geometry> List<V> getGeometries(final Class<V> geometryClass) {
    final List<V> geometries = new ArrayList<V>();
    if (geometryClass.isAssignableFrom(getClass())) {
      geometries.add((V)this);
    }
    return geometries;
  }

  /**
   * Returns an element {@link Geometry} from a {@link GeometryCollection}
   * (or <code>this</code>, if the geometry is not a collection).
   *
   * @param partIndex the index of the geometry element
   * @return the n'th geometry contained in this geometry
   */
  @Override
  @SuppressWarnings("unchecked")
  public <V extends Geometry> V getGeometry(final int partIndex) {
    return (V)this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V extends Geometry> List<V> getGeometryComponents(
    final Class<V> geometryClass) {
    final List<V> geometries = new ArrayList<V>();
    if (geometryClass.isAssignableFrom(getClass())) {
      geometries.add((V)this);
    }
    return geometries;
  }

  /**
   * Returns the number of {@link Geometry}s in a {@link GeometryCollection}
   * (or 1, if the geometry is not a collection).
   *
   * @return the number of geometries contained in this geometry
   */
  @Override
  public int getGeometryCount() {
    if (isEmpty()) {
      return 0;
    } else {
      return 1;
    }
  }

  /**
   *
   * @author Paul Austin <paul.austin@revolsys.com>
   * @return
   */
  @Override
  public GeometryFactory getGeometryFactory() {
    return GeometryFactory.floating3();
  }

  /**
   * Returns the name of this Geometry's actual class.
   *
   *@return the name of this <code>Geometry</code>s actual class
   */
  @Override
  public String getGeometryType() {
    return getDataType().toString();
  }

  /**
   * Computes an interior point of this <code>Geometry</code>.
   * An interior point is guaranteed to lie in the interior of the Geometry,
   * if it possible to calculate such a point exactly. Otherwise,
   * the point may lie on the boundary of the geometry.
   * <p>
   * The interior point of an empty geometry is <code>POINT EMPTY</code>.
   *
   * @return a {@link Point} which is in the interior of this Geometry
   */
  @Override
  public Point getInteriorPoint() {
    if (isEmpty()) {
      return getGeometryFactory().point();
    }
    Point interiorPt = null;
    final int dim = getDimension();
    if (dim == 0) {
      final InteriorPointPoint intPt = new InteriorPointPoint(this);
      interiorPt = intPt.getInteriorPoint();
    } else if (dim == 1) {
      final InteriorPointLine intPt = new InteriorPointLine(this);
      interiorPt = intPt.getInteriorPoint();
    } else {
      final InteriorPointArea intPt = new InteriorPointArea(this);
      interiorPt = intPt.getInteriorPoint();
    }
    return getGeometryFactory().point(interiorPt);
  }

  /**
   *  Returns the length of this <code>Geometry</code>.
   *  Linear geometries return their length.
   *  Areal geometries return their perimeter.
   *  They override this function to compute the area.
   *  Others return 0.0
   *
   *@return the length of the Geometry
   */
  @Override
  public double getLength() {
    return 0.0;
  }

  protected GeometryFactory getNonZeroGeometryFactory(
    GeometryFactory geometryFactory) {
    if (geometryFactory == null) {
      return GeometryFactory.floating3();
    } else {
      final int geometrySrid = getSrid();
      final int srid = geometryFactory.getSrid();
      if (srid == 0 && geometrySrid != 0) {
        geometryFactory = geometryFactory.convertSrid(geometrySrid);
      }
      return geometryFactory;
    }
  }

  /**
   *  Returns a vertex of this <code>Geometry</code>
   *  (usually, but not necessarily, the first one).
   *  The returned coordinate should not be assumed
   *  to be an actual Point object used in
   *  the internal representation.
   *
   *@return    a {@link Coordinates} which is a vertex of this <code>Geometry</code>.
   *@return null if this Geometry is empty
   */
  @Override
  public abstract Point getPoint();

  /**
   *  Returns the ID of the Spatial Reference System used by the <code>Geometry</code>.
   *  <P>
   *
   *  JTS supports Spatial Reference System information in the simple way
   *  defined in the SFS. A Spatial Reference System ID (SRID) is present in
   *  each <code>Geometry</code> object. <code>Geometry</code> provides basic
   *  accessor operations for this field, but no others. The SRID is represented
   *  as an integer.
   *
   *@return    the ID of the coordinate space in which the <code>Geometry</code>
   *      is defined.
   *
   */
  @Override
  public int getSrid() {
    return getGeometryFactory().getSrid();
  }

  /**
   * Gets the user data object for this geometry, if any.
   *
   * @return the user data object, or <code>null</code> if none set
   */
  @Override
  public Object getUserData() {
    return null;
  }

  /**
   * Gets a hash code for the Geometry.
   *
   * @return an integer value suitable for use as a hashcode
   */
  @Override
  public int hashCode() {
    return getBoundingBox().hashCode();
  }

  @Override
  public boolean hasInvalidXyCoordinates() {
    for (final Vertex vertex : vertices()) {
      for (int axisIndex = 0; axisIndex < 2; axisIndex++) {
        final double value = vertex.getCoordinate(axisIndex);
        if (Double.isNaN(value)) {
          return true;
        } else if (Double.isInfinite(value)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Computes a <code>Geometry</code> representing the point-set which is
   * common to both this <code>Geometry</code> and the <code>other</code> Geometry.
   * <p>
   * The intersection of two geometries of different dimension produces a result
   * geometry of dimension less than or equal to the minimum dimension of the input
   * geometries.
   * The result geometry may be a heterogenous {@link GeometryCollection}.
   * If the result is empty, it is an atomic geometry
   * with the dimension of the lowest input dimension.
   * <p>
   * Intersection of {@link GeometryCollection}s is supported
   * only for homogeneous collection types.
   * <p>
   * Non-empty heterogeneous {@link GeometryCollection} arguments are not supported.
   *
   * @param  other the <code>Geometry</code> with which to compute the intersection
   * @return a Geometry representing the point-set common to the two <code>Geometry</code>s
   * @throws TopologyException if a robustness error occurs
   * @throws IllegalArgumentException if the argument is a non-empty heterogeneous <code>GeometryCollection</code>
   */
  @Override
  public Geometry intersection(final Geometry other) {
    /**
     * TODO: MD - add optimization for P-A case using Point-In-Polygon
     */
    // special case: if one input is empty ==> empty
    if (this.isEmpty() || other.isEmpty()) {
      return OverlayOp.createEmptyResult(OverlayOp.INTERSECTION, this, other,
        getGeometryFactory());
    }

    // compute for GCs
    if (this.isGeometryCollection()) {
      final Geometry g2 = other;
      return GeometryCollectionMapper.map((GeometryCollection)this,
        new GeometryMapper.MapOp() {
        @Override
        public Geometry map(final Geometry g) {
          return g.intersection(g2);
        }
      });
    }
    // if (isGeometryCollection(other))
    // return other.intersection(this);

    checkNotGeometryCollection(this);
    checkNotGeometryCollection(other);
    return SnapIfNeededOverlayOp.overlayOp(this, other, OverlayOp.INTERSECTION);
  }

  /**
   * Tests whether this geometry intersects the argument geometry.
   * <p>
   * The <code>intersects</code> predicate has the following equivalent definitions:
   * <ul>
   * <li>The two geometries have at least one point in common
   * <li>The DE-9IM Intersection Matrix for the two geometries matches
   * at least one of the patterns
   *  <ul>
   *   <li><code>[T********]</code>
   *   <li><code>[*T*******]</code>
   *   <li><code>[***T*****]</code>
   *   <li><code>[****T****]</code>
   *  </ul>
   * <li><code>! g.disjoint(this) = true</code>
   * <br>(<code>intersects</code> is the inverse of <code>disjoint</code>)
   * </ul>
   *
   *@param  g  the <code>Geometry</code> with which to compare this <code>Geometry</code>
   *@return        <code>true</code> if the two <code>Geometry</code>s intersect
   *
   * @see Geometry#disjoint
   */
  @Override
  public boolean intersects(final Geometry g) {

    // short-circuit boundingBox test
    if (!getBoundingBox().intersects(g.getBoundingBox())) {
      return false;
    }

    /**
     * TODO: (MD) Add optimizations:
     *
     * - for P-A case:
     * If P is in env(A), test for point-in-poly
     *
     * - for A-A case:
     * If env(A1).overlaps(env(A2))
     * test for overlaps via point-in-poly first (both ways)
     * Possibly optimize selection of point to test by finding point of A1
     * closest to centre of env(A2).
     * (Is there a test where we shouldn't bother - e.g. if env A
     * is much smaller than env B, maybe there's no point in testing
     * pt(B) in env(A)?
     */

    // optimization for rectangle arguments
    if (isRectangle()) {
      return RectangleIntersects.intersects((Polygon)this, g);
    }
    if (g.isRectangle()) {
      return RectangleIntersects.intersects((Polygon)g, this);
    }
    // general case
    return relate(g).isIntersects();
  }

  /**
   * Tests whether any representative of the target geometry
   * intersects the test geometry.
   * This is useful in A/A, A/L, A/P, L/P, and P/P cases.
   *
   * @param geom the test geometry
   * @param repPts the representative points of the target geometry
   * @return true if any component intersects the areal test geometry
   */
  protected boolean isAnyTargetComponentInTest(final Geometry testGeom) {
    final PointLocator locator = new PointLocator();
    for (final Vertex vertex : vertices()) {
      if (locator.intersects(vertex, testGeom)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Tests whether the set of points covered by this <code>Geometry</code> is
   * empty.
   *
   *@return <code>true</code> if this <code>Geometry</code> does not cover any points
   */
  @Override
  public abstract boolean isEmpty();

  /**
   *  Returns whether the two <code>Geometry</code>s are equal, from the point
   *  of view of the <code>equalsExact</code> method. Called by <code>equalsExact</code>
   *  . In general, two <code>Geometry</code> classes are considered to be
   *  "equivalent" only if they are the same class. An exception is <code>LineString</code>
   *  , which is considered to be equivalent to its subclasses.
   *
   *@param  other  the <code>Geometry</code> with which to compare this <code>Geometry</code>
   *      for equality
   *@return        <code>true</code> if the classes of the two <code>Geometry</code>
   *      s are considered to be equal by the <code>equalsExact</code> method.
   */
  protected boolean isEquivalentClass(final Geometry other) {
    return this.getClass().getName().equals(other.getClass().getName());
  }

  /**
   * Tests whether this is an instance of a general {@link GeometryCollection},
   * rather than a homogeneous subclass.
   *
   * @return true if this is a hetereogeneous GeometryCollection
   */
  protected boolean isGeometryCollection() {
    return getClass().equals(com.revolsys.jts.geom.GeometryCollection.class);
  }

  @Override
  public boolean isRectangle() {
    // Polygon overrides to check for actual rectangle
    return false;
  }

  /**
   * Tests whether this {@link Geometry} is simple.
   * The SFS definition of simplicity
   * follows the general rule that a Geometry is simple if it has no points of
   * self-tangency, self-intersection or other anomalous points.
   * <p>
   * Simplicity is defined for each {@link Geometry} subclass as follows:
   * <ul>
   * <li>Valid polygonal geometries are simple, since their rings
   * must not self-intersect.  <code>isSimple</code>
   * tests for this condition and reports <code>false</code> if it is not met.
   * (This is a looser test than checking for validity).
   * <li>Linear rings have the same semantics.
   * <li>Linear geometries are simple iff they do not self-intersect at points
   * other than boundary points.
   * <li>Zero-dimensional geometries (points) are simple iff they have no
   * repeated points.
   * <li>Empty <code>Geometry</code>s are always simple.
   * <ul>
   *
   * @return <code>true</code> if this <code>Geometry</code> is simple
   * @see #isValid
   */
  @Override
  public boolean isSimple() {
    final IsSimpleOp op = new IsSimpleOp(this);
    return op.isSimple();
  }

  /**
   * Tests whether this <code>Geometry</code>
   * is topologically valid, according to the OGC SFS specification.
   * <p>
   * For validity rules see the Javadoc for the specific Geometry subclass.
   *
   *@return <code>true</code> if this <code>Geometry</code> is valid
   *
   * @see IsValidOp
   */
  @Override
  public boolean isValid() {
    return IsValidOp.isValid(this);
  }

  /**
   * Tests whether the distance from this <code>Geometry</code>
   * to another is less than or equal to a specified value.
   *
   * @param geom the Geometry to check the distance to
   * @param distance the distance value to compare
   * @return <code>true</code> if the geometries are less than <code>distance</code> apart.
   */
  @Override
  public boolean isWithinDistance(final Geometry geom, final double distance) {
    final double envDist = getBoundingBox().distance(geom.getBoundingBox());
    if (envDist > distance) {
      return false;
    }
    return DistanceOp.isWithinDistance(this, geom, distance);
    /*
     * double geomDist = this.distance(geom); if (geomDist > distance) return
     * false; return true;
     */
  }

  /**
   *  Converts this <code>Geometry</code> to <b>normal form</b> (or <b>
   *  canonical form</b> ). Normal form is a unique representation for <code>Geometry</code>
   *  s. It can be used to test whether two <code>Geometry</code>s are equal
   *  in a way that is independent of the ordering of the coordinates within
   *  them. Normal form equality is a stronger condition than topological
   *  equality, but weaker than pointwise equality. The definitions for normal
   *  form use the standard lexicographical ordering for coordinates. "Sorted in
   *  order of coordinates" means the obvious extension of this ordering to
   *  sequences of coordinates.
   *
   * @return a normalized copy of this geometry.
   * @see #normalize()
   */
  @Override
  public abstract Geometry normalize();

  /**
   * Tests whether this geometry overlaps the
   * specified geometry.
   * <p>
   * The <code>overlaps</code> predicate has the following equivalent definitions:
   * <ul>
   * <li>The geometries have at least one point each not shared by the other
   * (or equivalently neither covers the other),
   * they have the same dimension,
   * and the intersection of the interiors of the two geometries has
   * the same dimension as the geometries themselves.
   * <li>The DE-9IM Intersection Matrix for the two geometries matches
   *   <code>[T*T***T**]</code> (for two points or two surfaces)
   *   or <code>[1*T***T**]</code> (for two curves)
   * </ul>
   * If the geometries are of different dimension this predicate returns <code>false</code>.
   * This predicate is symmetric.
   *
   *@param  g  the <code>Geometry</code> with which to compare this <code>Geometry</code>
   *@return        <code>true</code> if the two <code>Geometry</code>s overlap.
   */
  @Override
  public boolean overlaps(final Geometry g) {
    // short-circuit test
    if (!getBoundingBox().intersects(g.getBoundingBox())) {
      return false;
    }
    return relate(g).isOverlaps(getDimension(), g.getDimension());
  }

  /**
   *  Returns the DE-9IM {@link IntersectionMatrix} for the two <code>Geometry</code>s.
   *
   *@param  g  the <code>Geometry</code> with which to compare this <code>Geometry</code>
   *@return        an {@link IntersectionMatrix} describing the intersections of the interiors,
   *      boundaries and exteriors of the two <code>Geometry</code>s
   */
  @Override
  public IntersectionMatrix relate(final Geometry g) {
    checkNotGeometryCollection(this);
    checkNotGeometryCollection(g);
    return RelateOp.relate(this, g);
  }

  /**
   * Tests whether the elements in the DE-9IM
   * {@link IntersectionMatrix} for the two <code>Geometry</code>s match the elements in <code>intersectionPattern</code>.
   * The pattern is a 9-character string, with symbols drawn from the following set:
   *  <UL>
   *    <LI> 0 (dimension 0)
   *    <LI> 1 (dimension 1)
   *    <LI> 2 (dimension 2)
   *    <LI> T ( matches 0, 1 or 2)
   *    <LI> F ( matches FALSE)
   *    <LI> * ( matches any value)
   *  </UL>
   *  For more information on the DE-9IM, see the <i>OpenGIS Simple Features
   *  Specification</i>.
   *
   *@param  g                the <code>Geometry</code> with which to compare
   *      this <code>Geometry</code>
   *@param  intersectionPattern  the pattern against which to check the
   *      intersection matrix for the two <code>Geometry</code>s
   *@return                      <code>true</code> if the DE-9IM intersection
   *      matrix for the two <code>Geometry</code>s match <code>intersectionPattern</code>
   * @see IntersectionMatrix
   */
  @Override
  public boolean relate(final Geometry g, final String intersectionPattern) {
    return relate(g).matches(intersectionPattern);
  }

  /**
   * Computes a new geometry which has all component coordinate sequences
   * in reverse order (opposite orientation) to this one.
   *
   * @return a reversed geometry
   */
  @Override
  public abstract Geometry reverse();

  /**
   * A simple scheme for applications to add their own custom data to a Geometry.
   * An example use might be to add an object representing a Point Reference System.
   * <p>
   * Note that user data objects are not present in geometries created by
   * construction methods.
   *
   * @param userData an object, the semantics for which are defined by the
   * application using this Geometry
   */
  @Override
  public void setUserData(final Object userData) {
    throw new UnsupportedOperationException("User data not supported");
  }

  /**
   * Computes a <coe>Geometry </code> representing the closure of the point-set
   * which is the union of the points in this <code>Geometry</code> which are not
   * contained in the <code>other</code> Geometry,
   * with the points in the <code>other</code> Geometry not contained in this
   * <code>Geometry</code>.
   * If the result is empty, it is an atomic geometry
   * with the dimension of the highest input dimension.
   * <p>
   * Non-empty {@link GeometryCollection} arguments are not supported.
   *
   *@param  other the <code>Geometry</code> with which to compute the symmetric
   *      difference
   *@return a Geometry representing the point-set symmetric difference of this <code>Geometry</code>
   *      with <code>other</code>
   * @throws TopologyException if a robustness error occurs
   * @throws IllegalArgumentException if either input is a non-empty GeometryCollection
   */
  @Override
  public Geometry symDifference(final Geometry other) {
    // handle empty geometry cases
    if (this.isEmpty() || other.isEmpty()) {
      // both empty - check dimensions
      if (this.isEmpty() && other.isEmpty()) {
        return OverlayOp.createEmptyResult(OverlayOp.SYMDIFFERENCE, this,
          other, getGeometryFactory());
      }

      // special case: if either input is empty ==> result = other arg
      if (this.isEmpty()) {
        return other.clone();
      }
      if (other.isEmpty()) {
        return clone();
      }
    }

    checkNotGeometryCollection(this);
    checkNotGeometryCollection(other);
    return SnapIfNeededOverlayOp.overlayOp(this, other, OverlayOp.SYMDIFFERENCE);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <G extends Geometry> G toClockwise() {
    return (G)this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <G extends Geometry> G toCounterClockwise() {
    return (G)this;
  }

  @Override
  public String toString() {
    return toWkt();
  }

  /**
   * Tests whether this geometry touches the
   * argument geometry.
   * <p>
   * The <code>touches</code> predicate has the following equivalent definitions:
   * <ul>
   * <li>The geometries have at least one point in common,
   * but their interiors do not intersect.
   * <li>The DE-9IM Intersection Matrix for the two geometries matches
   * at least one of the following patterns
   *  <ul>
   *   <li><code>[FT*******]</code>
   *   <li><code>[F**T*****]</code>
   *   <li><code>[F***T****]</code>
   *  </ul>
   * </ul>
   * If both geometries have dimension 0, the predicate returns <code>false</code>,
   * since points have only interiors.
   * This predicate is symmetric.
   *
   *
   *@param  g  the <code>Geometry</code> with which to compare this <code>Geometry</code>
   *@return        <code>true</code> if the two <code>Geometry</code>s touch;
   *      Returns <code>false</code> if both <code>Geometry</code>s are points
   */
  @Override
  public boolean touches(final Geometry g) {
    // short-circuit test
    if (!getBoundingBox().intersects(g.getBoundingBox())) {
      return false;
    }
    return relate(g).isTouches(getDimension(), g.getDimension());
  }

  /**
   *  <p>Returns the Extended Well-known Text representation of this <code>Geometry</code>.
   *  For a definition of the Well-known Text format, see the OpenGIS Simple
   *  Features Specification.</p>
   *
   *@return    the Well-known Text representation of this <code>Geometry</code>
   *@author Paul Austin <paul.austin@revolsys.com>
   */
  @Override
  public String toWkt() {
    return EWktWriter.toString(this, true);
  }

  /**
   * Computes the union of all the elements of this geometry.
   * <p>
   * This method supports
   * {@link GeometryCollection}s
   * (which the other overlay operations currently do not).
   * <p>
   * The result obeys the following contract:
   * <ul>
   * <li>Unioning a set of {@link LineString}s has the effect of fully noding
   * and dissolving the linework.
   * <li>Unioning a set of {@link Polygon}s always
   * returns a {@link Polygonal} geometry (unlike {@link #union(Geometry)},
   * which may return geometries of lower dimension if a topology collapse occurred).
   * </ul>
   *
   * @return the union geometry
   * @throws TopologyException if a robustness error occurs
   *
   * @see UnaryUnionOp
   */
  @Override
  public Geometry union() {
    return UnaryUnionOp.union(this);
  }

  /**
   * Computes a <code>Geometry</code> representing the point-set
   * which is contained in both this
   * <code>Geometry</code> and the <code>other</code> Geometry.
   * <p>
   * The union of two geometries of different dimension produces a result
   * geometry of dimension equal to the maximum dimension of the input
   * geometries.
   * The result geometry may be a heterogenous
   * {@link GeometryCollection}.
   * If the result is empty, it is an atomic geometry
   * with the dimension of the highest input dimension.
   * <p>
   * Unioning {@link LineString}s has the effect of
   * <b>noding</b> and <b>dissolving</b> the input linework. In this context
   * "noding" means that there will be a node or endpoint in the result for
   * every endpoint or line segment crossing in the input. "Dissolving" means
   * that any duplicate (i.e. coincident) line segments or portions of line
   * segments will be reduced to a single line segment in the result.
   * If <b>merged</b> linework is required, the {@link LineMerger}
   * class can be used.
   * <p>
   * Non-empty {@link GeometryCollection} arguments are not supported.
   *
   * @param other
   *          the <code>Geometry</code> with which to compute the union
   * @return a point-set combining the points of this <code>Geometry</code> and the
   *         points of <code>other</code>
   * @throws TopologyException
   *           if a robustness error occurs
   * @throws IllegalArgumentException
   *           if either input is a non-empty GeometryCollection
   * @see LineMerger
   */
  @Override
  public Geometry union(final Geometry other) {
    // handle empty geometry cases
    if (this.isEmpty() || other.isEmpty()) {
      if (this.isEmpty() && other.isEmpty()) {
        return OverlayOp.createEmptyResult(OverlayOp.UNION, this, other,
          getGeometryFactory());
      }

      // special case: if either input is empty ==> other input
      if (this.isEmpty()) {
        return other.clone();
      }
      if (other.isEmpty()) {
        return clone();
      }
    }

    // TODO: optimize if envelopes of geometries do not intersect

    checkNotGeometryCollection(this);
    checkNotGeometryCollection(other);
    return SnapIfNeededOverlayOp.overlayOp(this, other, OverlayOp.UNION);
  }

  /**
   * Tests whether this geometry is within the
   * specified geometry.
   * <p>
   * The <code>within</code> predicate has the following equivalent definitions:
   * <ul>
   * <li>Every point of this geometry is a point of the other geometry,
   * and the interiors of the two geometries have at least one point in common.
   * <li>The DE-9IM Intersection Matrix for the two geometries matches
   * <code>[T*F**F***]</code>
   * <li><code>g.contains(this) = true</code>
   * <br>(<code>within</code> is the converse of {@link #contains})
   * </ul>
   * An implication of the definition is that
   * "The boundary of a Geometry is not within the Geometry".
   * In other words, if a geometry A is a subset of
   * the points in the boundary of a geomtry B, <code>A.within(B) = false</code>
   * (As a concrete example, take A to be a LineString which lies in the boundary of a Polygon B.)
   * For a predicate with similar behaviour but avoiding
   * this subtle limitation, see {@link #coveredBy}.
   *
   *@param  g  the <code>Geometry</code> with which to compare this <code>Geometry</code>
   *@return        <code>true</code> if this <code>Geometry</code> is within
   *      <code>g</code>
   *
   * @see Geometry#contains
   * @see Geometry#coveredBy
   */
  @Override
  public boolean within(final Geometry g) {
    return g.contains(this);
  }

}
