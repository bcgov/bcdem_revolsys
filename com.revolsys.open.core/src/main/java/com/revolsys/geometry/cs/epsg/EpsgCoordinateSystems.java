package com.revolsys.geometry.cs.epsg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.revolsys.collection.map.IntHashMap;
import com.revolsys.collection.set.Sets;
import com.revolsys.geometry.cs.AngularUnit;
import com.revolsys.geometry.cs.Area;
import com.revolsys.geometry.cs.Authority;
import com.revolsys.geometry.cs.Axis;
import com.revolsys.geometry.cs.CoordinateSystem;
import com.revolsys.geometry.cs.Datum;
import com.revolsys.geometry.cs.GeographicCoordinateSystem;
import com.revolsys.geometry.cs.HorizontalCoordinateSystem;
import com.revolsys.geometry.cs.LinearUnit;
import com.revolsys.geometry.cs.PrimeMeridian;
import com.revolsys.geometry.cs.ProjectedCoordinateSystem;
import com.revolsys.geometry.cs.Projection;
import com.revolsys.geometry.cs.Spheroid;
import com.revolsys.geometry.model.Geometry;
import com.revolsys.geometry.model.impl.BoundingBoxDoubleGf;
import com.revolsys.record.io.format.csv.CsvIterator;
import com.revolsys.record.io.format.json.Json;
import com.revolsys.spring.resource.UrlResource;
import com.revolsys.util.Property;

public final class EpsgCoordinateSystems {
  private static Set<CoordinateSystem> coordinateSystems;

  private static IntHashMap<List<CoordinateSystem>> coordinateSystemsByCoordinateSystem = new IntHashMap<>();

  private static Map<Integer, CoordinateSystem> coordinateSystemsById = new TreeMap<>();

  private static Map<String, CoordinateSystem> coordinateSystemsByName = new TreeMap<>();

  private static boolean initialized = false;

  private static final IntHashMap<LinearUnit> linearUnits = new IntHashMap<>();

  private static Map<String, LinearUnit> linearUnitsByName = new TreeMap<>();

  private static int nextSrid = 2000000;

  private static Map<Integer, Projection> projectionsByCode = new TreeMap<>();

  private static Map<String, Projection> projectionsByName = new TreeMap<>();

  private static final Set<OpenOption> OPEN_OPTIONS_READ_SET = Sets
    .newHash(StandardOpenOption.READ);

  private static final FileAttribute<?>[] FILE_ATTRIBUTES_NONE = new FileAttribute[0];

  private static void addCoordinateSystem(final CoordinateSystem coordinateSystem) {
    final Integer id = coordinateSystem.getCoordinateSystemId();
    final String name = coordinateSystem.getCoordinateSystemName();
    coordinateSystemsById.put(id, coordinateSystem);
    final int hashCode = coordinateSystem.hashCode();
    List<CoordinateSystem> coordinateSystems = coordinateSystemsByCoordinateSystem.get(hashCode);
    if (coordinateSystems == null) {
      coordinateSystems = new ArrayList<>();
      coordinateSystemsByCoordinateSystem.put(hashCode, coordinateSystems);
    }
    coordinateSystems.add(coordinateSystem);
    coordinateSystemsByName.put(name, coordinateSystem);
  }

  public static void clear() {
    coordinateSystems = null;
    coordinateSystemsByCoordinateSystem.clear();
    coordinateSystemsById.clear();
    coordinateSystemsByName.clear();
  }

  public synchronized static CoordinateSystem getCoordinateSystem(
    final CoordinateSystem coordinateSystem) {
    initialize();
    if (coordinateSystem == null) {
      return null;
    } else {
      int srid = coordinateSystem.getCoordinateSystemId();
      CoordinateSystem matchedCoordinateSystem = coordinateSystemsById.get(srid);
      if (matchedCoordinateSystem == null) {
        matchedCoordinateSystem = coordinateSystemsByName
          .get(coordinateSystem.getCoordinateSystemName());
        if (matchedCoordinateSystem == null) {
          final int hashCode = coordinateSystem.hashCode();
          int matchCoordinateSystemId = 0;
          final List<CoordinateSystem> coordinateSystems = coordinateSystemsByCoordinateSystem
            .get(hashCode);
          if (coordinateSystems != null) {
            for (final CoordinateSystem coordinateSystem3 : coordinateSystems) {
              if (coordinateSystem3.equals(coordinateSystem)) {
                final int srid3 = coordinateSystem3.getCoordinateSystemId();
                if (matchedCoordinateSystem == null) {
                  matchedCoordinateSystem = coordinateSystem3;
                  matchCoordinateSystemId = srid3;
                } else if (srid3 < matchCoordinateSystemId) {
                  if (!coordinateSystem3.isDeprecated() || matchedCoordinateSystem.isDeprecated()) {
                    matchedCoordinateSystem = coordinateSystem3;
                    matchCoordinateSystemId = srid3;
                  }
                }
              }
            }
          }

          if (matchedCoordinateSystem == null) {
            if (srid <= 0) {
              srid = nextSrid++;
            }
            final String name = coordinateSystem.getCoordinateSystemName();
            final List<Axis> axis = coordinateSystem.getAxis();
            final Area area = coordinateSystem.getArea();
            final Authority authority = coordinateSystem.getAuthority();
            final boolean deprecated = coordinateSystem.isDeprecated();
            if (coordinateSystem instanceof GeographicCoordinateSystem) {
              final GeographicCoordinateSystem geographicCs = (GeographicCoordinateSystem)coordinateSystem;
              final Datum datum = geographicCs.getDatum();
              final PrimeMeridian primeMeridian = geographicCs.getPrimeMeridian();
              final AngularUnit angularUnit = geographicCs.getAngularUnit();
              final GeographicCoordinateSystem newCs = new GeographicCoordinateSystem(srid, name,
                datum, primeMeridian, angularUnit, axis, area, authority, deprecated);
              addCoordinateSystem(newCs);
              return newCs;
            } else if (coordinateSystem instanceof ProjectedCoordinateSystem) {
              final ProjectedCoordinateSystem projectedCs = (ProjectedCoordinateSystem)coordinateSystem;
              GeographicCoordinateSystem geographicCs = projectedCs.getGeographicCoordinateSystem();
              geographicCs = (GeographicCoordinateSystem)getCoordinateSystem(geographicCs);
              final Projection projection = projectedCs.getProjection();
              final Map<String, Object> parameters = projectedCs.getParameters();
              final LinearUnit linearUnit = projectedCs.getLinearUnit();
              final ProjectedCoordinateSystem newCs = new ProjectedCoordinateSystem(srid, name,
                geographicCs, area, projection, parameters, linearUnit, axis, authority,
                deprecated);
              addCoordinateSystem(newCs);
              return newCs;
            }
            return coordinateSystem;
          }
        }
      }
      return matchedCoordinateSystem;
    }
  }

  @SuppressWarnings("unchecked")
  public static <C extends CoordinateSystem> C getCoordinateSystem(final Geometry geometry) {
    return (C)getCoordinateSystem(geometry.getCoordinateSystemId());
  }

  @SuppressWarnings("unchecked")
  public static <C extends CoordinateSystem> C getCoordinateSystem(final int crsId) {
    initialize();
    final CoordinateSystem coordinateSystem = coordinateSystemsById.get(crsId);
    return (C)coordinateSystem;
  }

  public static CoordinateSystem getCoordinateSystem(final String name) {
    initialize();
    return coordinateSystemsByName.get(name);
  }

  public static Set<CoordinateSystem> getCoordinateSystems() {
    initialize();
    return coordinateSystems;
  }

  /**
   * Get the coordinate systems for the list of coordinate system identifiers.
   * Null identifiers will be ignored. If the coordinate system does not exist
   * then it will be ignored.
   *
   * @param coordinateSystemIds The coordinate system identifiers.
   * @return The list of coordinate systems.
   */
  public static List<CoordinateSystem> getCoordinateSystems(
    final Collection<Integer> coordinateSystemIds) {
    final List<CoordinateSystem> coordinateSystems = new ArrayList<>();
    for (final Integer coordinateSystemId : coordinateSystemIds) {
      if (coordinateSystemId != null) {
        final CoordinateSystem coordinateSystem = getCoordinateSystem(coordinateSystemId);
        if (coordinateSystem != null) {
          coordinateSystems.add(coordinateSystem);
        }
      }
    }
    return coordinateSystems;
  }

  public static Map<Integer, CoordinateSystem> getCoordinateSystemsById() {
    initialize();
    return new TreeMap<>(coordinateSystemsById);
  }

  public static int getCrsId(final CoordinateSystem coordinateSystem) {
    final Authority authority = coordinateSystem.getAuthority();
    if (authority != null) {
      final String name = authority.getName();
      final String code = authority.getCode();
      if (name.equals("EPSG")) {
        return Integer.parseInt(code);
      }
    }
    return 0;
  }

  public static String getCrsName(final CoordinateSystem coordinateSystem) {
    final Authority authority = coordinateSystem.getAuthority();
    final String name = authority.getName();
    final String code = authority.getCode();
    if (name.equals("EPSG")) {
      return name + ":" + code;
    } else {
      return null;
    }
  }

  private static double getDouble(final String value) {
    if (value == null || value.equals("") || value.equals("NaN")) {
      return Double.NaN;
    } else {
      return Double.valueOf(value);
    }
  }

  public static List<GeographicCoordinateSystem> getGeographicCoordinateSystems() {
    final List<GeographicCoordinateSystem> coordinateSystems = new ArrayList<>();
    for (final CoordinateSystem coordinateSystem : coordinateSystemsByName.values()) {
      if (coordinateSystem instanceof GeographicCoordinateSystem) {
        final GeographicCoordinateSystem geographicCoordinateSystem = (GeographicCoordinateSystem)coordinateSystem;
        coordinateSystems.add(geographicCoordinateSystem);
      }
    }
    return coordinateSystems;
  }

  public static HorizontalCoordinateSystem getHorizontalCoordinateSystem(
    final int coordinateSystemId) {
    return (HorizontalCoordinateSystem)getCoordinateSystem(coordinateSystemId);
  }

  private static Integer getInteger(final String value) {
    if (value == null || value.equals("")) {
      return null;
    } else {
      return Integer.valueOf(value);
    }
  }

  public static LinearUnit getLinearUnit(final int id) {
    initialize();
    return linearUnits.get(id);
  }

  public static LinearUnit getLinearUnit(final String name) {
    initialize();
    return linearUnitsByName.get(name);
  }

  private static Map<String, Object> getParameters(final String parametersString) {
    final Map<String, Object> parameters = new TreeMap<>();
    final Map<String, Object> jsonParams = Json.toObjectMap(parametersString);
    for (final Entry<String, Object> parameter : jsonParams.entrySet()) {
      final String key = parameter.getKey();
      final Object value = parameter.getValue();
      if (value instanceof BigDecimal) {
        final BigDecimal decimal = (BigDecimal)value;
        parameters.put(key, decimal.doubleValue());
      } else {
        parameters.put(key, value);
      }
    }
    return parameters;
  }

  public static List<ProjectedCoordinateSystem> getProjectedCoordinateSystems() {
    final List<ProjectedCoordinateSystem> coordinateSystems = new ArrayList<>();
    for (final CoordinateSystem coordinateSystem : coordinateSystemsByName.values()) {
      if (coordinateSystem instanceof ProjectedCoordinateSystem) {
        final ProjectedCoordinateSystem projectedCoordinateSystem = (ProjectedCoordinateSystem)coordinateSystem;
        coordinateSystems.add(projectedCoordinateSystem);
      }
    }
    return coordinateSystems;
  }

  private static Projection getProjection(final int code, final String name) {
    Projection projection = projectionsByCode.get(code);
    if (projection == null) {
      final EpsgAuthority authority = new EpsgAuthority(code);
      projection = new Projection(name, authority);
      projectionsByCode.put(code, projection);
      projectionsByName.put(name, projection);
    } else {
      if (!projection.getName().equals(name)) {
        final EpsgAuthority authority = new EpsgAuthority(code);
        return new Projection(name, authority);
      }
    }
    return projection;
  }

  public static synchronized Projection getProjection(final String name) {
    initialize();
    Projection projection = projectionsByName.get(name);
    if (projection == null) {
      projection = new Projection(name);
      projectionsByName.put(name, projection);
    }
    return projection;
  }

  private static String getString(final String string) {
    return new String(string);
  }

  public synchronized static void initialize() {
    if (!initialized) {
      try {
        final Map<Integer, List<Axis>> axisMap = loadAxis();
        final Map<Integer, Area> areas = loadAreas();
        final Map<Integer, AngularUnit> angularUnits = loadAngularUnits();
        final Map<Integer, LinearUnit> linearUnits = loadLinearUnits();
        loadGeographicCoordinateSystems(angularUnits, axisMap, areas);
        loadProjectedCoordinateSystems(axisMap, areas, linearUnits);
        final ProjectedCoordinateSystem worldMercator = (ProjectedCoordinateSystem)coordinateSystemsById
          .get(3857);
        coordinateSystemsById.put(900913, worldMercator);
        coordinateSystems = Collections
          .unmodifiableSet(new LinkedHashSet<>(coordinateSystemsById.values()));
        initialized = true;
      } catch (final Throwable t) {
        t.printStackTrace();
      }
    }
  }

  private static Map<Integer, AngularUnit> loadAngularUnits() throws IOException {
    final Map<Integer, AngularUnit> angularUnits = new LinkedHashMap<>();
    try (
      java.io.Reader reader = newReader(EpsgCoordinateSystems.class,
        "/com/revolsys/gis/cs/epsg/angularunit.csv")) {
      if (reader != null) {
        final CsvIterator csv = new CsvIterator(reader);
        if (csv.hasNext()) {
          csv.next();
          while (csv.hasNext()) {
            final List<String> values = csv.next();
            final int id = getInteger(values.get(0));
            final String name = values.get(1);
            final Integer baseId = getInteger(values.get(2));
            final double conversionFactor = getDouble(values.get(3));
            final boolean deprecated = Boolean.parseBoolean(values.get(4));
            final AngularUnit baseUnit = angularUnits.get(baseId);
            final EpsgAuthority authority = new EpsgAuthority(id);
            final AngularUnit unit = new AngularUnit(name, baseUnit, conversionFactor, authority,
              deprecated);
            angularUnits.put(id, unit);
          }
        }
      }
    }
    return angularUnits;
  }

  private static Map<Integer, Area> loadAreas() throws IOException {
    final Map<Integer, Area> areas = new LinkedHashMap<>();

    try (
      java.io.Reader reader = newReader(EpsgCoordinateSystems.class,
        "/com/revolsys/gis/cs/epsg/area.csv")) {
      if (reader != null) {
        final CsvIterator csv = new CsvIterator(reader);
        if (csv.hasNext()) {
          csv.next();
          while (csv.hasNext()) {
            final List<String> values = csv.next();
            final Integer code = getInteger(values.get(0));
            final String name = getString(values.get(1));
            final Double minX = getDouble(values.get(2));
            final Double minY = getDouble(values.get(3));
            final Double maxX = getDouble(values.get(4));
            final Double maxY = getDouble(values.get(5));
            final boolean deprecated = Boolean.parseBoolean(values.get(6));
            final Authority authority = new EpsgAuthority(code);

            final Area area = new Area(name, new BoundingBoxDoubleGf(2, minX, minY, maxX, maxY),
              authority, deprecated);
            areas.put(code, area);
          }
        }
      }
    }
    return areas;

  }

  private static Map<Integer, List<Axis>> loadAxis() throws IOException {
    final Map<Integer, List<Axis>> axisMap = new LinkedHashMap<>();

    try (
      java.io.Reader reader = newReader(EpsgCoordinateSystems.class,
        "/com/revolsys/gis/cs/epsg/axis.csv")) {
      if (reader != null) {
        final CsvIterator csv = new CsvIterator(reader);
        if (csv.hasNext()) {
          csv.next();
          while (csv.hasNext()) {
            final List<String> values = csv.next();
            final Integer id = getInteger(values.get(0));
            final List<Axis> axisList = new ArrayList<>();
            for (int i = 1; i < values.size(); i += 2) {
              final String name = values.get(i);
              if (Property.hasValue(name)) {
                final String direction = values.get(i + 1);
                final Axis axis = new Axis(name, direction);
                axisList.add(axis);
              }
            }
            axisMap.put(id, axisList);
          }
        }
      }
    }
    return axisMap;

  }

  private static Map<Integer, Datum> loadDatums() throws IOException {
    final Map<Integer, Spheroid> spheroids = loadSpheroids();
    final Map<Integer, PrimeMeridian> primeMeridians = loadPrimeMeridians();
    final Map<Integer, Datum> datums = new LinkedHashMap<>();

    try (
      java.io.Reader reader = newReader(EpsgCoordinateSystems.class,
        "/com/revolsys/gis/cs/epsg/datum.csv")) {
      if (reader != null) {
        final CsvIterator csv = new CsvIterator(reader);
        if (csv.hasNext()) {
          csv.next();
          while (csv.hasNext()) {
            final List<String> values = csv.next();
            final int id = Integer.parseInt(values.get(0));
            final String name = values.get(1);
            final int spheroidId = Integer.parseInt(values.get(2));
            final int primeMeridianId = Integer.parseInt(values.get(3));
            final boolean deprecated = Boolean.parseBoolean(values.get(4));
            final Spheroid spheroid = spheroids.get(spheroidId);
            final PrimeMeridian primeMeridian = primeMeridians.get(primeMeridianId);
            final EpsgAuthority authority = new EpsgAuthority(id);
            final Datum datum = new Datum(name, spheroid, primeMeridian, authority, deprecated);
            datums.put(id, datum);
          }
        }
      }
    }
    return datums;
  }

  private static void loadGeographicCoordinateSystems(final Map<Integer, AngularUnit> angularUnits,
    final Map<Integer, List<Axis>> axisMap, final Map<Integer, Area> areas) throws IOException {
    final Map<Integer, Datum> datums = loadDatums();

    try (
      java.io.Reader reader = newReader(EpsgCoordinateSystems.class,
        "/com/revolsys/gis/cs/epsg/geographic.csv")) {
      if (reader != null) {
        final CsvIterator csv = new CsvIterator(reader);
        if (csv.hasNext()) {
          csv.next();
          while (csv.hasNext()) {
            final List<String> values = csv.next();
            final int id = Integer.parseInt(values.get(0));
            final String name = values.get(1);
            final Integer datumId = getInteger(values.get(2));
            final Integer unitId = getInteger(values.get(3));
            final Integer axisId = getInteger(values.get(4));
            final Integer areaId = getInteger(values.get(5));
            final boolean deprecated = Boolean.parseBoolean(values.get(6));
            final Datum datum = datums.get(datumId);
            final EpsgAuthority authority = new EpsgAuthority(id);
            final AngularUnit angularUnit = angularUnits.get(unitId);
            final List<Axis> axis = axisMap.get(axisId);
            final Area area = areas.get(areaId);
            final GeographicCoordinateSystem coordinateSystem = new GeographicCoordinateSystem(id,
              name, datum, angularUnit, axis, area, authority, deprecated);
            addCoordinateSystem(coordinateSystem);
          }
        }
      }
    }
  }

  private static Map<Integer, LinearUnit> loadLinearUnits() throws IOException {

    try (
      java.io.Reader reader = newReader(EpsgCoordinateSystems.class,
        "/com/revolsys/gis/cs/epsg/linearunit.csv")) {
      if (reader != null) {
        final CsvIterator csv = new CsvIterator(reader);
        if (csv.hasNext()) {
          csv.next();
          while (csv.hasNext()) {
            final List<String> values = csv.next();
            final int id = getInteger(values.get(0));
            final String name = values.get(1);
            final Integer baseId = getInteger(values.get(2));
            final double conversionFactor = getDouble(values.get(3));
            final boolean deprecated = Boolean.parseBoolean(values.get(4));
            final LinearUnit baseUnit = linearUnits.get(baseId);
            final EpsgAuthority authority = new EpsgAuthority(id);
            final LinearUnit unit = new LinearUnit(name, baseUnit, conversionFactor, authority,
              deprecated);
            linearUnits.put(id, unit);
            linearUnitsByName.put(name, unit);
          }
        }
      }
    }
    return linearUnits;
  }

  private static Map<Integer, PrimeMeridian> loadPrimeMeridians() throws IOException {
    final Map<Integer, PrimeMeridian> primeMeridians = new LinkedHashMap<>();

    try (
      java.io.Reader reader = newReader(EpsgCoordinateSystems.class,
        "/com/revolsys/gis/cs/epsg/primemeridian.csv")) {
      if (reader != null) {
        final CsvIterator csv = new CsvIterator(reader);
        if (csv.hasNext()) {
          csv.next();
          while (csv.hasNext()) {
            final List<String> values = csv.next();
            final int id = Integer.parseInt(values.get(0));
            final String name = values.get(1);
            final double longitude = getDouble(values.get(2));
            final boolean deprecated = Boolean.parseBoolean(values.get(3));
            final EpsgAuthority authority = new EpsgAuthority(id);
            final PrimeMeridian primeMeridian = new PrimeMeridian(name, longitude, authority,
              deprecated);
            primeMeridians.put(id, primeMeridian);
          }
        }
      }
    }
    return primeMeridians;
  }

  private static void loadProjectedCoordinateSystems(final Map<Integer, List<Axis>> axisMap,
    final Map<Integer, Area> areas, final Map<Integer, LinearUnit> linearUnits) throws IOException {

    try (
      java.io.Reader reader = newReader(EpsgCoordinateSystems.class,
        "/com/revolsys/gis/cs/epsg/projected.csv")) {
      if (reader != null) {
        final CsvIterator csv = new CsvIterator(reader);
        if (csv.hasNext()) {
          csv.next();
          while (csv.hasNext()) {
            final List<String> values = csv.next();
            final int id = Integer.parseInt(values.get(0));
            final String name = values.get(1);
            final Integer geoCsId = getInteger(values.get(2));
            final Integer unitId = getInteger(values.get(3));
            final Integer methodCode = getInteger(values.get(4));
            final String methodName = values.get(5);
            final String parametersString = values.get(6);
            final Integer axisId = getInteger(values.get(7));
            final Integer areaId = getInteger(values.get(8));
            final boolean deprecated = Boolean.parseBoolean(values.get(9));
            final CoordinateSystem referencedCoordinateSystem = coordinateSystemsById.get(geoCsId);
            if (referencedCoordinateSystem instanceof GeographicCoordinateSystem) {
              final GeographicCoordinateSystem geographicCoordinateSystem = (GeographicCoordinateSystem)referencedCoordinateSystem;
              EpsgAuthority authority = new EpsgAuthority(id);
              final LinearUnit linearUnit = linearUnits.get(unitId);
              final Projection projection = getProjection(methodCode, methodName);
              final Map<String, Object> parameters = getParameters(parametersString);
              final List<Axis> axis = axisMap.get(axisId);
              final Area area = areas.get(areaId);
              final ProjectedCoordinateSystem coordinateSystem = new ProjectedCoordinateSystem(id,
                name, geographicCoordinateSystem, area, projection, parameters, linearUnit, axis,
                authority, deprecated);

              addCoordinateSystem(coordinateSystem);
              if (id == 3857) {
                authority = new EpsgAuthority(102100);
                final ProjectedCoordinateSystem webMercator = new ProjectedCoordinateSystem(102100,
                  name, geographicCoordinateSystem, area, projection, parameters, linearUnit, axis,
                  authority, deprecated);

                addCoordinateSystem(webMercator);
              }
            }
          }
        }
      }
    }
  }

  private static Map<Integer, Spheroid> loadSpheroids() throws IOException {
    final Map<Integer, Spheroid> spheroids = new LinkedHashMap<>();

    try (
      java.io.Reader reader = newReader(EpsgCoordinateSystems.class,
        "/com/revolsys/gis/cs/epsg/spheroid.csv")) {
      final CsvIterator csv = new CsvIterator(reader);
      if (csv.hasNext()) {
        csv.next();
        while (csv.hasNext()) {
          final List<String> values = csv.next();
          final int id = Integer.parseInt(values.get(0));
          final String name = values.get(1);
          final double semiMajorAxis = getDouble(values.get(2));
          final double semiMinorAxis = getDouble(values.get(3));
          final double inverseFlattening = getDouble(values.get(4));
          final boolean deprecated = Boolean.parseBoolean(values.get(5));
          final EpsgAuthority authority = new EpsgAuthority(id);
          final Spheroid spheroid = new Spheroid(name, semiMajorAxis, semiMinorAxis,
            inverseFlattening, authority, deprecated);
          spheroids.put(id, spheroid);
        }
      }
    }
    return spheroids;
  }

  public static java.io.Reader newReader(final Class<?> clazz, final String fileName)
    throws IOException {
    final URL url = clazz.getResource(fileName);
    if (url == null) {
      return null;
    } else {
      final UrlResource resource = new UrlResource(url);
      ReadableByteChannel channel;
      try {
        final File file = resource.getFile();
        final Path path = file.toPath();
        channel = FileChannel.open(path, OPEN_OPTIONS_READ_SET, FILE_ATTRIBUTES_NONE);
      } catch (final Throwable e) {
        final InputStream in = url.openStream();
        channel = Channels.newChannel(in);
      }
      return Channels.newReader(channel, StandardCharsets.UTF_8.newDecoder(), 8196);
    }
  }

  public static CoordinateSystem wgs84() {
    return EpsgCoordinateSystems.<CoordinateSystem> getCoordinateSystem(4326);
  }

  private EpsgCoordinateSystems() {
  }
}
