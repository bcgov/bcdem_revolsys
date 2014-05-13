package com.revolsys.jtstest.testrunner;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.springframework.util.StringUtils;

import com.revolsys.io.FileUtil;
import com.revolsys.io.map.MapObjectFactoryRegistry;

public class TestDirectory extends TestSuite {

  private final int index;

  public TestDirectory parent;

  public TestDirectory(final TestDirectory parent, final int index,
    final File directory, final String name) throws Throwable {
    this.parent = parent;
    this.index = index;
    if (parent == null) {
      setName(name);
    } else {
      setName(getId() + "." + name);
    }
    final TestReader testReader = TopologyTest.getTestReader();
    int runIndex = 1;
    for (final File file : directory.listFiles()) {
      if (file.isDirectory()) {
        final Test test = new TestDirectory(this, runIndex, file,
          file.getName());
        addTest(test);
      } else if (file.isFile()) {
        TestFile testRun;
        if (FileUtil.getFileNameExtension(file).equals("json")) {
          testRun = MapObjectFactoryRegistry.toObject(file);
        } else {
          testRun = testReader.createTestRun(this, file, runIndex);
        }
        addTest(testRun);
      }
      runIndex++;
    }
  }

  public String getId() {
    if (parent == null) {
      return "";
    } else {
      final String parentId = parent.getId();
      if (StringUtils.hasText(parentId)) {
        return parentId + "." + index;
      } else {
        return String.valueOf(index);
      }
    }
  }

  @Override
  public String toString() {
    return getName();
  }
}
