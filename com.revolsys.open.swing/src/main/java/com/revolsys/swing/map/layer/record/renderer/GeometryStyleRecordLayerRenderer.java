package com.revolsys.swing.map.layer.record.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.jeometry.common.data.type.DataType;

import com.revolsys.collection.map.MapEx;
import com.revolsys.geometry.model.BoundingBox;
import com.revolsys.geometry.model.Geometry;
import com.revolsys.geometry.model.GeometryDataTypes;
import com.revolsys.record.schema.FieldDefinition;
import com.revolsys.record.schema.RecordDefinition;
import com.revolsys.swing.Icons;
import com.revolsys.swing.map.layer.LayerRenderer;
import com.revolsys.swing.map.layer.record.AbstractRecordLayer;
import com.revolsys.swing.map.layer.record.LayerRecord;
import com.revolsys.swing.map.layer.record.renderer.shape.LineStringShape;
import com.revolsys.swing.map.layer.record.style.GeometryStyle;
import com.revolsys.swing.map.layer.record.style.panel.GeometryStylePanel;
import com.revolsys.swing.map.layer.record.style.panel.GeometryStylePreview;
import com.revolsys.swing.map.view.ViewRenderer;

public class GeometryStyleRecordLayerRenderer extends AbstractRecordLayerRenderer
  implements GeometryStyleLayerRenderer<AbstractRecordLayer> {

  private static final Icon ICON = Icons.getIcon("style_geometry");

  public static GeneralPath getLineShape() {
    final GeneralPath path = new GeneralPath();
    path.moveTo(0, 0);
    path.lineTo(15, 0);
    path.lineTo(0, 15);
    path.lineTo(15, 15);
    return path;
  }

  public static GeneralPath getPolygonShape() {
    final GeneralPath path = new GeneralPath();
    path.moveTo(0, 0);
    path.lineTo(7, 0);
    path.lineTo(15, 8);
    path.lineTo(15, 15);
    path.lineTo(8, 15);
    path.lineTo(0, 7);
    path.lineTo(0, 0);
    path.closePath();
    return path;
  }

  private GeometryStyle style = new GeometryStyle();

  public GeometryStyleRecordLayerRenderer(final AbstractRecordLayer layer) {
    this(layer, new GeometryStyle());
  }

  public GeometryStyleRecordLayerRenderer(final AbstractRecordLayer layer,
    final GeometryStyle style) {
    this(layer, null, style);
  }

  public GeometryStyleRecordLayerRenderer(final AbstractRecordLayer layer,
    final LayerRenderer<?> parent) {
    super("geometryStyle", "Geometry Style", layer, parent);
    setIcon(ICON);
  }

  public GeometryStyleRecordLayerRenderer(final AbstractRecordLayer layer,
    final LayerRenderer<?> parent, final GeometryStyle style) {
    super("geometryStyle", "Geometry Style", layer, parent);
    setStyle(style);
    setIcon(ICON);
  }

  public GeometryStyleRecordLayerRenderer(final Map<String, ? extends Object> properties) {
    super("geometryStyle", "Geometry Style");
    setIcon(ICON);
    setProperties(properties);
  }

  @Override
  public GeometryStyleRecordLayerRenderer clone() {
    final GeometryStyleRecordLayerRenderer clone = (GeometryStyleRecordLayerRenderer)super.clone();
    if (this.style != null) {
      clone.setStyle(this.style.clone());
    }
    return clone;
  }

  @Override
  public DataType getGeometryType() {
    final AbstractRecordLayer layer = getLayer();
    final RecordDefinition recordDefinition = layer.getRecordDefinition();
    final FieldDefinition geometryField = recordDefinition.getGeometryField();

    if (geometryField != null) {
      final DataType geometryDataType = geometryField.getDataType();
      if (GeometryDataTypes.GEOMETRY_COLLECTION.equals(geometryDataType)) {
        return GeometryDataTypes.GEOMETRY;
      } else if (GeometryDataTypes.MULTI_POINT.equals(geometryDataType)) {
        return GeometryDataTypes.POINT;
      } else if (GeometryDataTypes.MULTI_LINE_STRING.equals(geometryDataType)) {
        return GeometryDataTypes.LINE_STRING;
      } else if (GeometryDataTypes.MULTI_POLYGON.equals(geometryDataType)) {
        return GeometryDataTypes.POLYGON;
      } else {
        return geometryDataType;
      }
    }
    return null;
  }

  @Override
  public Icon getIcon() {
    Icon icon = super.getIcon();
    if (icon == ICON) {
      icon = newIcon();
      setIcon(icon);
    }
    return icon;
  }

  @Override
  public GeometryStyle getStyle() {
    return this.style;
  }

  @Override
  public Icon newIcon() {
    final AbstractRecordLayer layer = getLayer();
    if (layer == null) {
      return ICON;
    } else {
      final GeometryStyle geometryStyle = getStyle();
      Shape shape = null;
      final DataType geometryDataType = layer.getGeometryType();
      if (GeometryDataTypes.POINT.equals(geometryDataType)
        || GeometryDataTypes.MULTI_POINT.equals(geometryDataType)) {
        return this.style.getMarker().newIcon(geometryStyle);
      } else if (GeometryDataTypes.LINE_STRING.equals(geometryDataType)
        || GeometryDataTypes.MULTI_LINE_STRING.equals(geometryDataType)) {
        shape = new LineStringShape(GeometryStylePreview.getLineString(16));
      } else if (GeometryDataTypes.POLYGON.equals(geometryDataType)
        || GeometryDataTypes.MULTI_POLYGON.equals(geometryDataType)) {
        shape = getPolygonShape();
      } else {
        return ICON;
      }

      final BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
      final Graphics2D graphics = image.createGraphics();
      graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      if (GeometryDataTypes.POLYGON.equals(geometryDataType)
        || GeometryDataTypes.MULTI_POLYGON.equals(geometryDataType)) {
        graphics.setPaint(geometryStyle.getPolygonFill());
        graphics.fill(shape);
      }
      final Color color = geometryStyle.getLineColor();
      graphics.setColor(color);

      graphics.draw(shape);
      graphics.dispose();
      return new ImageIcon(image);

    }
  }

  @Override
  public GeometryStylePanel newStylePanel() {
    return new GeometryStylePanel(this);
  }

  @Override
  public void propertyChange(final PropertyChangeEvent event) {
    final Object source = event.getSource();
    if (source == this.style) {
      refreshIcon();
    }
    super.propertyChange(event);
  }

  @Override
  public void renderRecord(final ViewRenderer view, final BoundingBox visibleArea,
    final AbstractRecordLayer layer, final LayerRecord record) {
    final Geometry geometry = record.getGeometry();
    view.drawGeometry(geometry, this.style);
  }

  @Override
  public void setProperties(final Map<String, ? extends Object> properties) {
    super.setProperties(properties);
    if (this.style != null) {
      this.style.setProperties(properties);
    }
  }

  @Override
  public void setStyle(final GeometryStyle style) {
    if (this.style != null) {
      this.style.removePropertyChangeListener(this);
    }
    this.style = style;
    if (this.style != null) {
      this.style.addPropertyChangeListener(this);
    }
    firePropertyChange("style", null, style);
    refreshIcon();
  }

  @Override
  public MapEx toMap() {
    final MapEx map = super.toMap();
    if (this.style != null) {
      final Map<String, Object> styleMap = this.style.toMap();
      map.putAll(styleMap);
    }
    return map;
  }
}
