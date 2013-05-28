package com.revolsys.swing.map.layer;

import java.awt.Graphics2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;

import com.revolsys.swing.component.ValueField;
import com.revolsys.swing.map.Viewport2D;

public interface LayerRenderer<T extends Layer> extends PropertyChangeListener {

  Map<String, Object> getAllDefaults();

  String getName();

  <V> V getValue(String name);

  boolean isVisible();

  void render(Viewport2D viewport, Graphics2D graphics);

  void setVisible(boolean visible);

  PropertyChangeSupport getPropertyChangeSupport();
  
  <V extends ValueField<?>> V createStylePanel();
}
