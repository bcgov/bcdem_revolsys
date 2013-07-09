package com.revolsys.swing.field;

import java.awt.Color;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.springframework.util.StringUtils;

import com.revolsys.swing.layout.SpringLayoutUtil;
import com.revolsys.swing.listener.InvokeMethodActionListener;
import com.revolsys.swing.undo.UndoManager;

public class DirectoryNameField extends JPanel implements ValidatingField {
  private static final long serialVersionUID = -8433151755294925911L;

  private final TextField directoryName = new TextField(70);

  private final JButton browseButton = new JButton();

  private final String fieldName = "fieldValue";

  private String errorMessage;

  private String originalToolTip;

  public DirectoryNameField() {
    super(new SpringLayout());

    add(directoryName);
    browseButton.setText("Browse...");
    browseButton.addActionListener(new InvokeMethodActionListener(this,
      "browseClick"));
    add(browseButton);
    SpringLayoutUtil.makeCompactGrid(this, 1, 2, 5, 5, 5, 5);
  }

  public void browseClick() {
    try {
      final JFileChooser fileChooser = new JFileChooser();

      fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      final String directoryPath = getDirectoryPath();
      final File initialFile = new File(directoryPath);

      if (initialFile.getParentFile() != null
        && initialFile.getParentFile().exists()) {
        fileChooser.setCurrentDirectory(initialFile.getParentFile());
      }

      fileChooser.setMultiSelectionEnabled(false);

      if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(SwingUtilities.windowForComponent(this))) {
        final File file = fileChooser.getSelectedFile();
        if (file != null) {
          directoryName.setText(file.getCanonicalPath());
        }
      }
    } catch (final Throwable t) {
      JOptionPane.showMessageDialog(this, t.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);
    }
  }

  public File getDirectoryFile() {
    final String directoryPath = getDirectoryPath();
    if (StringUtils.hasText(directoryPath)) {
      return new File(directoryPath);
    } else {
      return null;
    }
  }

  public String getDirectoryPath() {
    return directoryName.getText();
  }

  @Override
  public String getFieldName() {
    return fieldName;
  }

  @Override
  public String getFieldValidationMessage() {
    final File directory = getDirectoryFile();
    if (directory == null) {
      return "No directory specified";
    } else if (directory.exists()) {
      return null;
    } else {
      return "Directory does not exist";
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getFieldValue() {
    return (T)directoryName.getText();
  }

  @Override
  public boolean isFieldValid() {
    final File directory = getDirectoryFile();
    if (directory == null || !directory.exists()) {
      directoryName.setForeground(Color.RED);
      directoryName.setSelectedTextColor(Color.RED);
      directoryName.setBackground(Color.PINK);
      return false;
    } else {
      directoryName.setForeground(Color.BLACK);
      directoryName.setSelectedTextColor(Color.BLACK);
      directoryName.setBackground(Color.WHITE);
      return true;
    }
  }

  public void setDirectoryPath(final String directoryPath) {
    directoryName.setText(directoryPath);
  }

  @Override
  public void setFieldBackgroundColor(Color color) {
    if (color == null) {
      color = TextField.DEFAULT_BACKGROUND;
    }
    setBackground(color);
  }

  @Override
  public void setFieldForegroundColor(Color color) {
    if (color == null) {
      color = TextField.DEFAULT_FOREGROUND;
    }
    setForeground(color);
  }

  @Override
  public void setFieldInvalid(final String message) {
    directoryName.setForeground(Color.RED);
    directoryName.setSelectedTextColor(Color.RED);
    directoryName.setBackground(Color.PINK);
    this.errorMessage = message;
    super.setToolTipText(errorMessage);
  }

  @Override
  public void setFieldToolTip(final String toolTip) {
    setToolTipText(toolTip);
  }

  @Override
  public void setFieldValid() {
    directoryName.setForeground(TextField.DEFAULT_FOREGROUND);
    directoryName.setSelectedTextColor(TextField.DEFAULT_SELECTED_FOREGROUND);
    directoryName.setBackground(TextField.DEFAULT_BACKGROUND);
    this.errorMessage = null;
    super.setToolTipText(originalToolTip);
  }

  @Override
  public void setFieldValue(final Object fieldValue) {
    setDirectoryPath(fieldValue.toString());
  }

  @Override
  public void setToolTipText(final String text) {
    this.originalToolTip = text;
    if (!StringUtils.hasText(errorMessage)) {
      super.setToolTipText(text);
    }
  }

  @Override
  public void setUndoManager(final UndoManager undoManager) {
    directoryName.setUndoManager(undoManager);
  }

  @Override
  public String toString() {
    return getFieldName() + "=" + getFieldValue();
  }
}
