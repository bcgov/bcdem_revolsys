package com.revolsys.record.io.format.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jeometry.common.data.type.DataType;
import org.jeometry.common.data.type.DataTypes;
import org.jeometry.common.data.type.FunctionDataType;
import org.jeometry.common.exception.Exceptions;

import com.revolsys.io.AbstractIoFactory;
import com.revolsys.io.FileUtil;
import com.revolsys.io.IoConstants;
import com.revolsys.io.file.Paths;
import com.revolsys.io.map.MapReader;
import com.revolsys.io.map.MapReaderFactory;
import com.revolsys.io.map.MapWriter;
import com.revolsys.io.map.MapWriterFactory;
import com.revolsys.record.ArrayRecord;
import com.revolsys.record.Record;
import com.revolsys.record.io.RecordWriter;
import com.revolsys.record.io.RecordWriterFactory;
import com.revolsys.record.schema.RecordDefinition;
import com.revolsys.record.schema.RecordDefinitionProxy;
import com.revolsys.spring.resource.PathResource;
import com.revolsys.spring.resource.Resource;
import com.revolsys.util.JavaBeanUtil;
import com.revolsys.util.Property;

public class Json extends AbstractIoFactory
  implements MapReaderFactory, MapWriterFactory, RecordWriterFactory {
  public static final String FILE_EXTENSION = "json";

  public static final String MIME_TYPE = "application/json";

  @SuppressWarnings({
    "rawtypes", "unchecked"
  })
  public static final DataType JSON_OBJECT = new FunctionDataType("JsonObject", JsonObject.class,
    true, value -> {
      if (value instanceof JsonObject) {
        return (JsonObject)value;
      } else if (value instanceof Map) {
        return new JsonObjectHash((Map)value);
      } else if (value instanceof String) {
        final JsonObject map = Json.toObjectMap((String)value);
        if (map == null) {
          return null;
        } else {
          return new JsonObjectHash(map);
        }
      } else {
        return JsonParser.read(value);
      }
    }, (value) -> {
      if (value instanceof Map) {
        return Json.toString((Map)value);
      } else if (value == null) {
        return null;
      } else {
        return value.toString();
      }

    }, FunctionDataType.MAP_EQUALS, FunctionDataType.MAP_EQUALS_EXCLUDES);

  @SuppressWarnings({
    "rawtypes", "unchecked"
  })
  public static final DataType JSON_TYPE = new FunctionDataType("JsonType", JsonType.class, true,
    value -> {
      if (value instanceof JsonType) {
        return (JsonType)value;
      } else if (value instanceof Map) {
        return new JsonObjectHash((Map)value);
      } else if (value instanceof List) {
        return JsonList.array((List)value);
      } else if (value instanceof String) {
        final Object read = JsonParser.read((String)value);
        if (read instanceof JsonType) {
          return (JsonType)read;
        } else {
          return value;
        }
      } else {
        return value;
      }
    }, (value) -> Json.toString(value), DataType::equal, DataType::equal);

  public static DataType JSON_LIST = new FunctionDataType("JsonList", JsonList.class, true,
    (value) -> {
      if (value instanceof JsonList) {
        return (JsonList)value;
      } else if (value instanceof Collection<?>) {
        return JsonList.array((Collection<?>)value);
      } else {
        final Object json = JsonParser.read(value);
        if (json instanceof JsonList) {
          return (JsonList)json;
        } else {
          return JsonList.array(json);
        }
      }
    }, (value) -> {
      if (value instanceof List<?>) {
        return Json.toString(value);
      } else if (value == null) {
        return null;
      } else {
        return value.toString();
      }

    }, (o1, o2) -> o1.equals(o2), DataType::equal);

  static {
    DataTypes.registerDataTypes(Json.class);
  }

  public static JsonObject clone(final JsonObject object) {
    if (object == null) {
      return null;
    } else {
      final JsonObject clone = new JsonObjectHash();
      for (final Entry<String, Object> entry : object.entrySet()) {
        final String key = entry.getKey();
        final Object originalValue = entry.getValue();
        final Object cloneValue = clone(originalValue);
        clone.put(key, cloneValue);
      }
      return clone;
    }
  }

  @SuppressWarnings("unchecked")
  public static <V> V clone(final Object value) {
    if (value == null) {
      return null;
    } else if (value instanceof JsonObject) {
      return (V)clone((JsonObject)value);
    } else if (value instanceof Map) {
      final Map<Object, Object> originalMap = (Map<Object, Object>)value;
      final JsonObject clone = new JsonObjectHash();
      for (final Entry<Object, Object> entry : originalMap.entrySet()) {
        final String key = entry.getKey().toString();
        final Object originalValue = entry.getValue();
        final Object cloneValue = clone(originalValue);
        clone.put(key, cloneValue);
      }
      return (V)clone;
    } else if (value instanceof List) {
      final List<?> list = (List<?>)value;
      final JsonList clone = JsonList.array();
      for (final Object object : list) {
        final Object cloneValue = clone(object);
        clone.add(cloneValue);
      }
      return (V)clone;
    } else if (value instanceof Cloneable) {
      try {
        final Class<? extends Object> valueClass = value.getClass();
        final Method method = valueClass.getMethod("clone", JavaBeanUtil.ARRAY_CLASS_0);
        if (method == null) {
          return (V)value;
        } else {
          return (V)method.invoke(value, JavaBeanUtil.ARRAY_OBJECT_0);
        }
      } catch (final Throwable e) {
        return Exceptions.throwUncheckedException(e);
      }
    } else {
      return (V)value;
    }
  }

  public static Map<String, Object> getMap(final Map<String, Object> record,
    final String fieldName) {
    final String value = (String)record.get(fieldName);
    return toObjectMap(value);
  }

  public static JsonObject toMap(final File directory, final String path) {
    if (directory == null || path == null) {
      return new JsonObjectHash();
    } else {
      final File file = FileUtil.getFile(directory, path);
      if (file.exists() && !file.isDirectory()) {
        final PathResource resource = new PathResource(file);
        return toMap(resource);
      } else {
        return new JsonObjectHash();
      }
    }
  }

  public static JsonObject toMap(final Object source) {
    final Resource resource = Resource.getResource(source);
    return toMap(resource);
  }

  public static JsonObject toMap(final Path directory, final String path) {
    if (directory == null || path == null) {
      return new JsonObjectHash();
    } else {
      final Path file = directory.resolve(path);
      if (Paths.exists(file) && !Files.isDirectory(file)) {
        final PathResource resource = new PathResource(file);
        return toMap(resource);
      } else {
        return new JsonObjectHash();
      }
    }
  }

  public static JsonObject toMap(final Reader in) {
    try (
      Reader inClosable = in;
      final JsonMapIterator iterator = new JsonMapIterator(in, true)) {
      if (iterator.hasNext()) {
        return iterator.next();
      } else {
        return null;
      }
    } catch (final IOException e) {
      throw new RuntimeException("Unable to read JSON map", e);
    }
  }

  public static final JsonObject toMap(final Resource resource) {
    if (resource != null && (!(resource instanceof PathResource) || resource.exists())) {
      final Reader reader = resource.newBufferedReader();
      return toMap(reader);
    }
    return new JsonObjectHash();
  }

  public static Map<String, String> toMap(final String string) {
    final JsonObject map = toObjectMap(string);
    if (map.isEmpty()) {
      return new LinkedHashMap<>();
    } else {
      final Map<String, String> stringMap = new LinkedHashMap<>();
      for (final Entry<String, Object> entry : map.entrySet()) {
        final String key = entry.getKey();
        final Object value = entry.getValue();
        if (value == null) {
          stringMap.put(key, null);
        } else {
          stringMap.put(key, value.toString());
        }
      }
      return stringMap;
    }
  }

  public static final List<JsonObject> toMapList(final Object source) {
    final Resource resource = Resource.getResource(source);
    if (resource != null && (!(resource instanceof PathResource) || resource.exists())) {
      try (
        final BufferedReader in = resource.newBufferedReader();
        final JsonObjectReader jsonReader = new JsonObjectReader(in)) {
        return jsonReader.toList();
      } catch (final IOException e) {
        Exceptions.throwUncheckedException(e);
      }
    }
    return new ArrayList<>();
  }

  public static List<JsonObject> toMapList(final String string) {
    final StringReader in = new StringReader(string);
    try (
      final JsonObjectReader reader = new JsonObjectReader(in)) {
      return reader.toList();
    }
  }

  public static JsonObject toObjectMap(final String string) {
    if (Property.hasValue(string)) {
      final StringReader in = new StringReader(string);
      try (
        final JsonObjectReader reader = new JsonObjectReader(in, true)) {
        for (final JsonObject object : reader) {
          return object;
        }
      }
    }
    return new JsonObjectHash();
  }

  public static final Record toRecord(final RecordDefinition recordDefinition,
    final String string) {
    final StringReader in = new StringReader(string);
    final JsonRecordIterator iterator = new JsonRecordIterator(recordDefinition, in, true);
    try {
      if (iterator.hasNext()) {
        return iterator.next();
      } else {
        return null;
      }
    } finally {
      iterator.close();
    }
  }

  public static List<Record> toRecordList(final RecordDefinition recordDefinition,
    final String string) {
    final StringReader in = new StringReader(string);
    final JsonRecordIterator iterator = new JsonRecordIterator(recordDefinition, in);
    try {
      final List<Record> objects = new ArrayList<>();
      while (iterator.hasNext()) {
        final Record object = iterator.next();
        objects.add(object);
      }
      return objects;
    } finally {
      iterator.close();
    }
  }

  public static String toString(final List<? extends Map<String, Object>> list) {
    return toString(list, false);
  }

  public static String toString(final List<? extends Map<String, Object>> list,
    final boolean indent) {
    final StringWriter writer = new StringWriter();
    final JsonMapWriter mapWriter = new JsonMapWriter(writer, indent);
    for (final Map<String, Object> map : list) {
      mapWriter.write(map);
    }
    mapWriter.close();
    return writer.toString();
  }

  public static String toString(final Map<String, ? extends Object> values) {
    final StringWriter writer = new StringWriter();
    try (
      final JsonWriter jsonWriter = new JsonWriter(writer, false)) {
      jsonWriter.write(values);
    }
    return writer.toString();
  }

  public static String toString(final Map<String, ? extends Object> values, final boolean indent) {
    final StringWriter writer = new StringWriter();
    try (
      final JsonWriter jsonWriter = new JsonWriter(writer, indent)) {
      jsonWriter.write(values);
    }
    return writer.toString();
  }

  public static String toString(final Object value) {
    return toString(value, true);
  }

  public static String toString(final Object value, final boolean indent) {
    final StringWriter stringWriter = new StringWriter();
    try (
      JsonWriter jsonWriter = new JsonWriter(stringWriter, indent)) {
      jsonWriter.value(value);
    }
    return stringWriter.toString();
  }

  public static final String toString(final Record object) {
    final RecordDefinition recordDefinition = object.getRecordDefinition();
    final StringWriter writer = new StringWriter();
    final JsonRecordWriter recordWriter = new JsonRecordWriter(recordDefinition, writer);
    recordWriter.setProperty(IoConstants.SINGLE_OBJECT_PROPERTY, Boolean.TRUE);
    recordWriter.write(object);
    recordWriter.close();
    return writer.toString();
  }

  public static String toString(final RecordDefinition recordDefinition,
    final List<? extends Map<String, Object>> list) {
    final StringWriter writer = new StringWriter();
    final JsonRecordWriter recordWriter = new JsonRecordWriter(recordDefinition, writer);
    for (final Map<String, Object> map : list) {
      final Record object = new ArrayRecord(recordDefinition);
      object.setValues(map);
      recordWriter.write(object);
    }
    recordWriter.close();
    return writer.toString();
  }

  public static String toString(final RecordDefinition recordDefinition,
    final Map<String, ? extends Object> parameters) {
    final Record object = new ArrayRecord(recordDefinition);
    object.setValues(parameters);
    return toString(object);
  }

  public static void writeMap(final Map<String, ? extends Object> object, final Object target) {
    writeMap(object, target, true);
  }

  public static void writeMap(final Map<String, ? extends Object> object, final Object target,
    final boolean indent) {
    final Resource resource = Resource.getResource(target);
    try (
      final Writer writer = resource.newWriter()) {
      writeMap(writer, object, indent);
    } catch (final IOException e) {
    }
  }

  public static void writeMap(final Writer writer, final Map<String, ? extends Object> object) {
    writeMap(writer, object, true);
  }

  public static void writeMap(final Writer writer, final Map<String, ? extends Object> object,
    final boolean indent) {
    try (
      final JsonMapWriter out = new JsonMapWriter(writer, indent)) {
      out.setSingleObject(true);
      out.write(object);
    } catch (final RuntimeException | Error e) {
      throw e;
    }
  }

  public Json() {
    super("JSON");
    addMediaTypeAndFileExtension(MIME_TYPE, FILE_EXTENSION);
  }

  @Override
  public boolean isReadFromZipFileSupported() {
    return true;
  }

  @Override
  public MapReader newMapReader(final Resource resource) {
    return new JsonMapReader(resource.getInputStream());
  }

  @Override
  public MapWriter newMapWriter(final OutputStream out, final Charset charset) {
    return newMapWriter(out);
  }

  @Override
  public MapWriter newMapWriter(final Writer out) {
    return new JsonMapWriter(out);
  }

  @Override
  public RecordWriter newRecordWriter(final String baseName,
    final RecordDefinitionProxy recordDefinition, final OutputStream outputStream,
    final Charset charset) {
    final OutputStreamWriter writer = FileUtil.newUtf8Writer(outputStream);
    return new JsonRecordWriter(recordDefinition, writer);
  }
}
