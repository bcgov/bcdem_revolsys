package com.revolsys.swing.map;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.ResponseCache;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreePath;

import org.springframework.core.io.FileSystemResource;

import com.revolsys.io.datastore.RecordStoreConnectionManager;
import com.revolsys.io.datastore.RecordStoreConnectionRegistry;
import com.revolsys.io.file.FolderConnectionManager;
import com.revolsys.jts.geom.BoundingBox;
import com.revolsys.jts.geom.GeometryFactory;
import com.revolsys.jts.geom.impl.BoundingBoxDoubleGf;
import com.revolsys.jts.util.BoundingBoxUtil;
import com.revolsys.net.urlcache.FileResponseCache;
import com.revolsys.swing.Icons;
import com.revolsys.swing.SwingUtil;
import com.revolsys.swing.action.InvokeMethodAction;
import com.revolsys.swing.action.enablecheck.ObjectPropertyEnableCheck;
import com.revolsys.swing.component.BaseFrame;
import com.revolsys.swing.listener.InvokeMethodPropertyChangeListener;
import com.revolsys.swing.logging.Log4jTableModel;
import com.revolsys.swing.map.layer.Layer;
import com.revolsys.swing.map.layer.LayerGroup;
import com.revolsys.swing.map.layer.Project;
import com.revolsys.swing.map.overlay.MeasureOverlay;
import com.revolsys.swing.map.print.SinglePage;
import com.revolsys.swing.menu.MenuFactory;
import com.revolsys.swing.parallel.Invoke;
import com.revolsys.swing.parallel.SwingWorkerProgressBar;
import com.revolsys.swing.pdf.SaveAsPdf;
import com.revolsys.swing.preferences.PreferencesDialog;
import com.revolsys.swing.table.worker.SwingWorkerTableModel;
import com.revolsys.swing.tree.BaseTree;
import com.revolsys.swing.tree.node.BaseTreeNode;
import com.revolsys.swing.tree.node.ListTreeNode;
import com.revolsys.swing.tree.node.file.FileSystemsTreeNode;
import com.revolsys.swing.tree.node.file.FolderConnectionsTreeNode;
import com.revolsys.swing.tree.node.layer.ProjectTreeNode;
import com.revolsys.swing.tree.node.record.RecordStoreConnectionsTreeNode;
import com.revolsys.util.ExceptionUtil;
import com.revolsys.util.OS;
import com.revolsys.util.PreferencesUtil;
import com.revolsys.util.Property;

public class ProjectFrame extends BaseFrame {
  public static void addSaveActions(final JComponent component,
    final Project project) {
    final InputMap inputMap = component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    inputMap.put(
      KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK),
      SAVE_PROJECT_KEY);
    inputMap.put(
      KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.META_DOWN_MASK),
      SAVE_PROJECT_KEY);

    inputMap.put(
      KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK
        | InputEvent.ALT_DOWN_MASK), SAVE_CHANGES_KEY);
    inputMap.put(
      KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.META_DOWN_MASK
        | InputEvent.ALT_DOWN_MASK), SAVE_CHANGES_KEY);

    final ActionMap actionMap = component.getActionMap();
    actionMap.put(SAVE_PROJECT_KEY, new InvokeMethodAction(SAVE_PROJECT_KEY,
      project, "saveAllSettings"));
    actionMap.put(SAVE_CHANGES_KEY, new InvokeMethodAction(SAVE_CHANGES_KEY,
      project, "saveChanges"));
  }

  public static ProjectFrame get(final Layer layer) {
    if (layer == null) {
      return null;
    } else {
      final LayerGroup project = layer.getProject();
      if (project == null) {
        return null;
      } else {
        return project.getProperty(PROJECT_FRAME);
      }
    }
  }

  public static final String PROJECT_FRAME = "projectFrame";

  public static final String SAVE_PROJECT_KEY = "Save Project";

  public static final String SAVE_CHANGES_KEY = "Save Changes";

  private static final long serialVersionUID = 1L;

  static {
    ResponseCache.setDefault(new FileResponseCache());
  }

  private Project project;

  private JTabbedPane leftTabs = new JTabbedPane();

  private JTabbedPane bottomTabs = new JTabbedPane();

  private MapPanel mapPanel;

  private BaseTree catalogTree;

  private boolean exitOnClose = true;

  private BaseTree tocTree;

  private JSplitPane leftRightSplit;

  public ProjectFrame(final String title) {
    this(title, new Project());
  }

  public ProjectFrame(final String title, final File projectDirectory) {
    this(title);
    Invoke.background("Load project", this, "loadProject", projectDirectory);
  }

  public ProjectFrame(final String title, final Project project) {
    super(title, false);
    this.project = project;
    init();
  }

  public void actionNewProject() {
    if (this.project != null && this.project.saveWithPrompt()) {
      this.project.reset();
    }
  }

  protected void addCatalogPanel() {
    final RecordStoreConnectionsTreeNode recordStores = new RecordStoreConnectionsTreeNode();

    final FileSystemsTreeNode fileSystems = new FileSystemsTreeNode();

    final FolderConnectionsTreeNode folderConnections = new FolderConnectionsTreeNode();

    final ListTreeNode root = new ListTreeNode(recordStores, fileSystems,
      folderConnections);

    final BaseTree tree = new BaseTree(root);
    tree.setRootVisible(false);

    recordStores.expandChildren();
    fileSystems.expand();
    folderConnections.expandChildren();

    this.catalogTree = tree;

    addTabIcon(this.leftTabs, "tree_catalog", "Catalog", this.catalogTree);
  }

  protected void addLogPanel() {
    final JPanel panel = Log4jTableModel.createPanel();
    addTabIcon(this.bottomTabs, "error", "Logging", panel);
  }

  protected MapPanel addMapPanel() {
    this.mapPanel = new MapPanel(this.project);
    if (OS.isMac()) {
      this.mapPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 9, 9));
    } else {
      this.mapPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    }
    return this.mapPanel;
  }

  protected void addMenu(final JMenuBar menuBar, final MenuFactory menuFactory) {
    final JMenu menu = menuFactory.createComponent();
    menuBar.add(menu, menuBar.getMenuCount() - 1);
  }

  public int addTabIcon(final JTabbedPane tabs, final String iconName,
    final String toolTipText, final Component component) {
    final JScrollPane scrollPane = new JScrollPane(component);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());

    tabs.addTab(null, Icons.getIcon(iconName), scrollPane);
    final int tabIndex = tabs.getTabCount() - 1;
    tabs.setToolTipTextAt(tabIndex, toolTipText);
    return tabIndex;
  }

  protected void addTableOfContents() {
    final Project project = getProject();
    this.tocTree = ProjectTreeNode.createTree(project);

    Property.addListener(this.project, "layers",
      new InvokeMethodPropertyChangeListener(true, this, "expandLayers",
        PropertyChangeEvent.class));

    addTabIcon(this.leftTabs, "tree_layers", "TOC", this.tocTree);

  }

  protected void addTasksPanel() {
    final JPanel panel = SwingWorkerTableModel.createPanel();
    final int tabIndex = addTabIcon(this.bottomTabs, "time",
      "Background Tasks", panel);

    final SwingWorkerProgressBar progressBar = this.mapPanel.getProgressBar();
    final JButton viewTasksAction = InvokeMethodAction.createButton(null,
      "View Running Tasks", Icons.getIcon("time_go"), this.bottomTabs,
      "setSelectedIndex", tabIndex);
    viewTasksAction.setBorderPainted(false);
    viewTasksAction.setBorder(null);
    progressBar.add(viewTasksAction, BorderLayout.EAST);
  }

  @Override
  protected JMenuBar createMenuBar() {
    final JMenuBar menuBar = super.createMenuBar();
    addMenu(menuBar, createMenuFile());

    final MenuFactory tools = createMenuTools();

    if (OS.isWindows()) {
      tools.addMenuItem("options", "Options...", "Options...", null, null,
        PreferencesDialog.get(), "showPanel");
    }
    addMenu(menuBar, tools);
    return menuBar;
  }

  protected MenuFactory createMenuFile() {
    final MenuFactory file = new MenuFactory("File");

    file.addMenuItemTitleIcon("project", "New Project", "layout_add", this,
      "actionNewProject");

    file.addMenuItemTitleIcon("project", "Save Project", "layout_save",
      this.project, "saveAllSettings");

    file.addMenuItemTitleIcon("project", "Save Project As...", "layout_save",
      this.project, "saveAllSettingsAs");

    file.addMenuItemTitleIcon("save", "Save as PDF", "save", SaveAsPdf.class,
      "save");

    file.addMenuItemTitleIcon("print", "Print", "printer", SinglePage.class,
      "print");

    file.addMenuItemTitleIcon("exit", "Exit", null, this, "exit");
    return file;
  }

  protected MenuFactory createMenuTools() {
    final MenuFactory tools = new MenuFactory("Tools");
    final MapPanel map = getMapPanel();
    tools.addCheckboxMenuItem("map",
      new InvokeMethodAction("Measure", Icons.getIcon("ruler"), map,
        "toggleMode", MeasureOverlay.MEASURE), new ObjectPropertyEnableCheck(
        map, "overlayAction", MeasureOverlay.MEASURE));

    tools.addMenuItem("script", "Run Script...", "Run Script",
      Icons.getIcon("script_go"), this, "runScript");
    return tools;
  }

  @Override
  public void dispose() {
    if (SwingUtilities.isEventDispatchThread()) {
      setVisible(false);
      super.dispose();
      Property.removeAllListeners(this);
      if (this.project != null) {
        final RecordStoreConnectionRegistry recordStores = this.project.getRecordStores();
        RecordStoreConnectionManager.get().removeConnectionRegistry(
          recordStores);
        if (Project.get() == this.project) {
          Project.set(null);
        }
      }
      this.tocTree = null;
      this.project = null;
      this.leftTabs = null;
      this.leftRightSplit = null;
      this.bottomTabs = null;

      if (this.mapPanel != null) {
        this.mapPanel.destroy();
        this.mapPanel = null;
      }
      setMenuBar(null);
      final ActionMap actionMap = getRootPane().getActionMap();
      actionMap.put(SAVE_PROJECT_KEY, null);
      actionMap.put(SAVE_CHANGES_KEY, null);

      setRootPane(new JRootPane());
      removeAll();
    } else {
      Invoke.later(this, "dispose");
    }
  }

  public void exit() {
    final Project project = getProject();
    if (project != null && project.saveWithPrompt()) {
      final Window[] windows = Window.getOwnerlessWindows();
      for (final Window window : windows) {
        window.dispose();
      }
      System.exit(0);
    }
  }

  public void expandLayers(final Layer layer) {
    if (layer != null) {
      if (SwingUtilities.isEventDispatchThread()) {
        final LayerGroup group;
        if (layer instanceof LayerGroup) {
          group = (LayerGroup)layer;
        } else {
          group = layer.getLayerGroup();
        }
        if (group != null) {
          final List<Layer> layerPath = group.getPathList();
          this.tocTree.expandPath(layerPath);
        }
      } else {
        Invoke.later(this, "expandLayers", layer);
      }
    }
  }

  public void expandLayers(final PropertyChangeEvent event) {
    final Object source = event.getSource();
    if (source instanceof LayerGroup) {
      final Object newValue = event.getNewValue();
      if (newValue instanceof LayerGroup) {
        expandLayers((LayerGroup)newValue);
      }
    }
  }

  public JTabbedPane getBottomTabs() {
    return this.bottomTabs;
  }

  public double getControlWidth() {
    return 0.20;
  }

  protected BoundingBox getDefaultBoundingBox() {
    return new BoundingBoxDoubleGf();
  }

  // public void expandConnectionManagers(final PropertyChangeEvent event) {
  // final Object newValue = event.getNewValue();
  // if (newValue instanceof ConnectionRegistry) {
  // final ConnectionRegistry<?> registry = (ConnectionRegistry<?>)newValue;
  // final ConnectionRegistryManager<?> connectionManager =
  // registry.getConnectionManager();
  // if (connectionManager != null) {
  // final List<?> connectionRegistries =
  // connectionManager.getConnectionRegistries();
  // if (connectionRegistries != null) {
  // final ObjectTree tree = catalogPanel.getTree();
  // tree.expandPath(connectionRegistries, connectionManager, registry);
  // }
  // }
  // }
  // }

  public JTabbedPane getLeftTabs() {
    return this.leftTabs;
  }

  public MapPanel getMapPanel() {
    return this.mapPanel;
  }

  public Project getProject() {
    return this.project;
  }

  public BaseTree getTocTree() {
    return this.tocTree;
  }

  public BaseTreeNode getTreeNode(final Layer layer) {
    final List<Layer> layerPath = layer.getPathList();
    final TreePath treePath = this.tocTree.getTreePath(layerPath);
    if (treePath == null) {
      return null;
    } else {
      return (BaseTreeNode)treePath.getLastPathComponent();
    }
  }

  @Override
  protected void init() {
    setMinimumSize(new Dimension(600, 500));
    setBounds((Object)null);

    final JRootPane rootPane = getRootPane();

    addSaveActions(rootPane, this.project);

    final BoundingBox defaultBoundingBox = getDefaultBoundingBox();
    this.project.setViewBoundingBox(defaultBoundingBox);
    Project.set(this.project);
    this.project.setProperty(PROJECT_FRAME, this);

    addMapPanel();

    this.leftTabs.setMinimumSize(new Dimension(100, 300));
    this.leftTabs.setPreferredSize(new Dimension(300, 700));

    this.mapPanel.setMinimumSize(new Dimension(300, 300));
    this.mapPanel.setPreferredSize(new Dimension(700, 700));
    this.leftRightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
      this.leftTabs, this.mapPanel);

    this.leftRightSplit.setBorder(BorderFactory.createEmptyBorder());
    this.bottomTabs.setBorder(BorderFactory.createEmptyBorder());

    final JSplitPane topBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
      this.leftRightSplit, this.bottomTabs);
    this.bottomTabs.setMinimumSize(new Dimension(600, 100));

    topBottom.setDividerLocation(600);
    topBottom.setResizeWeight(1);

    add(topBottom, BorderLayout.CENTER);

    addTableOfContents();
    addCatalogPanel();

    addTasksPanel();
    addLogPanel();

    super.init();
  }

  public void loadProject(final File projectDirectory) {
    final FileSystemResource resource = new FileSystemResource(projectDirectory);
    this.project.readProject(resource);

    final Object frameBoundsObject = this.project.getProperty("frameBounds");
    setBounds(frameBoundsObject);
    setVisible(true);

    final RecordStoreConnectionManager recordStoreConnectionManager = RecordStoreConnectionManager.get();
    recordStoreConnectionManager.removeConnectionRegistry("Project");
    recordStoreConnectionManager.addConnectionRegistry(this.project.getRecordStores());

    final FolderConnectionManager folderConnectionManager = FolderConnectionManager.get();
    folderConnectionManager.removeConnectionRegistry("Project");
    folderConnectionManager.addConnectionRegistry(this.project.getFolderConnections());

    final MapPanel mapPanel = getMapPanel();
    final BoundingBox initialBoundingBox = this.project.getInitialBoundingBox();
    final Viewport2D viewport = mapPanel.getViewport();
    if (!BoundingBoxUtil.isEmpty(initialBoundingBox)) {
      final GeometryFactory geometryFactory = initialBoundingBox.getGeometryFactory();
      this.project.setGeometryFactory(geometryFactory);
      this.project.setViewBoundingBox(initialBoundingBox);
      viewport.setGeometryFactory(geometryFactory);
      viewport.setBoundingBox(initialBoundingBox);

    }
    viewport.setInitialized(true);
  }

  public void runScript() {
    final JFileChooser fileChooser = SwingUtil.createFileChooser(
      "Select Script", "com.revolsys.swing.tools.script", "directory");
    final FileNameExtensionFilter groovyFilter = new FileNameExtensionFilter(
      "Groovy Script", "groovy");
    fileChooser.addChoosableFileFilter(groovyFilter);
    fileChooser.setMultiSelectionEnabled(false);
    final int returnVal = fileChooser.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {

      final Binding binding = new Binding();
      final GroovyShell shell = new GroovyShell(binding);
      final File scriptFile = fileChooser.getSelectedFile();
      final String[] args = new String[0];
      try {
        PreferencesUtil.setUserString("com.revolsys.swing.tools.script",
          "directory", scriptFile.getParent());
        shell.run(scriptFile, args);
      } catch (final Throwable e) {
        ExceptionUtil.log(getClass(), "Unable to run script:" + scriptFile, e);
      }
    }
  }

  public void setBounds(final Object frameBoundsObject) {
    if (SwingUtilities.isEventDispatchThread()) {
      if (frameBoundsObject instanceof List) {
        try {
          @SuppressWarnings("unchecked")
          final List<Number> frameBoundsList = (List<Number>)frameBoundsObject;
          if (frameBoundsList.size() == 4) {
            int x = frameBoundsList.get(0).intValue();
            int y = frameBoundsList.get(1).intValue();
            int width = frameBoundsList.get(2).intValue();
            int height = frameBoundsList.get(3).intValue();

            final Rectangle screenBounds = SwingUtil.getScreenBounds(x, y);

            width = Math.min(width, screenBounds.width);
            height = Math.min(height, screenBounds.height);
            setSize(width, height);

            if (x < screenBounds.x || x > screenBounds.x + screenBounds.width) {
              x = 0;
            } else {
              x = Math.min(x, screenBounds.x + screenBounds.width - width);
            }
            if (y < screenBounds.y || x > screenBounds.y + screenBounds.height) {
              y = 0;
            } else {
              y = Math.min(y, screenBounds.y + screenBounds.height - height);
            }
            setLocation(x, y);
            return;
          }
        } catch (final Throwable t) {
        }
      }
      final Rectangle screenBounds = SwingUtil.getScreenBounds();
      setLocation(screenBounds.x + 10, screenBounds.y + 10);
      setSize(screenBounds.width - 20, screenBounds.height - 20);
    } else {
      if (frameBoundsObject == null) {
        Invoke.later(this, "setBounds", new Object());
      } else {
        Invoke.later(this, "setBounds", frameBoundsObject);
      }
    }
  }

  public void setExitOnClose(final boolean exitOnClose) {
    this.exitOnClose = exitOnClose;
  }

  public void setProject(final Project project) {
    if (this.project != project) {
      final Project oldProject = this.project;
      this.mapPanel.setProject(project);
      this.tocTree.setRoot(new ProjectTreeNode(project));
      if (oldProject != null) {
        oldProject.delete();
      }
      this.project = project;
      firePropertyChange("project", oldProject, project);
    }
  }

  @Override
  public void windowClosing(final WindowEvent e) {
    if (this.exitOnClose) {
      exit();
    } else {
      dispose();
    }
  }

}
