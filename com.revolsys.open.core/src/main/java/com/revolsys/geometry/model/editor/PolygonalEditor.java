package com.revolsys.geometry.model.editor;

import com.revolsys.geometry.model.Polygonal;

public interface PolygonalEditor extends GeometryEditor<PolygonEditor>, Polygonal {
  @Override
  Polygonal newGeometry();
}
