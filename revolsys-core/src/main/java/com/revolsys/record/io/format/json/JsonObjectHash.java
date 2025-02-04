package com.revolsys.record.io.format.json;

import java.util.Map;

import com.revolsys.collection.map.LinkedHashMapEx;

public class JsonObjectHash extends LinkedHashMapEx implements JsonObject {

  public JsonObjectHash() {
    super();
  }

  public JsonObjectHash(final Map<? extends String, ? extends Object> m) {
    super(m);
  }

  public JsonObjectHash(final String key, final Object value) {
    add(key, value);
  }

  @Override
  public JsonObjectHash clone() {
    return new JsonObjectHash(this);
  }

  @Override
  public String toString() {
    return Json.toString(this, false);
  }
}
