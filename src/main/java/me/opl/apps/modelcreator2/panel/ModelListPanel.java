package me.opl.apps.modelcreator2.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import me.opl.apps.modelcreator2.MCVariableProvider;
import me.opl.apps.modelcreator2.ModelCreator;
import me.opl.apps.modelcreator2.event.BlockStateOpenedEvent;
import me.opl.apps.modelcreator2.event.EventHandler;
import me.opl.apps.modelcreator2.event.EventListener;
import me.opl.apps.modelcreator2.event.ModelOpenedEvent;
import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.BlockState;
import me.opl.libs.tablib.AbstractPanel;

// TODO: render model previews to use as icons
// TODO: listen to model and blockstate closed events
public class ModelListPanel extends AbstractPanel {
	private JPanel panel;

	private JTree tree;

	public ModelListPanel(MCVariableProvider vp) {
		super(vp);

		createPanel(vp.getModelCreator());
	}

	private void createPanel(ModelCreator modelCreator) {
		ModelListTreeModel treeModel = new ModelListTreeModel(modelCreator);
		modelCreator.getEventDispatcher().registerListeners(treeModel);

		tree = new JTree(treeModel);

		tree.setCellRenderer(new ModelListTreeCellRenderer());

		for (int i = 0; i < treeModel.getChildCount(treeModel.getRoot()); i++) {
			tree.expandPath(new TreePath(new Object[] {treeModel.getRoot(), treeModel.getChild(treeModel.getRoot(), i)}));
		}

		ToolTipManager.sharedInstance().registerComponent(tree);

		tree.setRootVisible(false);
		tree.addTreeWillExpandListener(new TreeWillExpandListener() {
			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
				throw new ExpandVetoException(event);
			}

			@Override public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {}
		});

		panel = new JPanel(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(tree);
		panel.add(scrollPane, BorderLayout.CENTER);
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public String getTitle() {
		return "Model List";
	}

	public static class ModelListTreeModel implements TreeModel, EventListener {
		public static final String NODE_ROOT = "node.root";
		public static final String NODE_MODELS = "Models";
		public static final String NODE_BLOCK_STATES = "Block States";

		private static final String[] CHILDREN_ROOT = new String[] {NODE_MODELS, NODE_BLOCK_STATES};

		private ModelCreator modelCreator;

		private List<TreeModelListener> listeners = new ArrayList<>();

		public ModelListTreeModel(ModelCreator modelCreator) {
			this.modelCreator = modelCreator;
		}

		@Override
		public Object getChild(Object parent, int index) {
			if (parent.equals(NODE_ROOT)) {
				return CHILDREN_ROOT[index];
			} else if (parent.equals(NODE_MODELS)) {
				return modelCreator.getModels()[index];
			} else if (parent.equals(NODE_BLOCK_STATES)) {
				return modelCreator.getBlockStates()[index];
			}

			return null;
		}

		@Override
		public int getChildCount(Object parent) {
			if (parent instanceof String) {
				if (parent.equals(NODE_ROOT)) {
					return CHILDREN_ROOT.length;
				} else if (parent.equals(NODE_MODELS)) {
					return modelCreator.getModelCount();
				} else if (parent.equals(NODE_BLOCK_STATES)) {
					return modelCreator.getBlockStateCount();
				}
			}

			return 0;
		}

		@Override
		public int getIndexOfChild(Object parent, Object child) {
			if (parent instanceof String) {
				if (parent.equals(NODE_MODELS)) {
					return modelCreator.getModelCount();
				} else if (parent.equals(NODE_BLOCK_STATES)) {
					return modelCreator.getBlockStateCount();
				}
			}

			return -1;
		}

		@Override
		public Object getRoot() {
			return NODE_ROOT;
		}

		@Override
		public boolean isLeaf(Object node) {
			return !(node instanceof String);
		}

		@Override
		public void addTreeModelListener(TreeModelListener listener) {
			listeners.add(listener);
		}

		@Override
		public void removeTreeModelListener(TreeModelListener listener) {
			listeners.remove(listener);
		}

		@EventHandler
		public void onModelOpened(ModelOpenedEvent event) {
			TreeModelEvent treeEvent = new TreeModelEvent(this, new Object[] {NODE_ROOT, NODE_MODELS}, new int[] {modelCreator.getModelIndex(event.getModel())}, new Object[] {event.getModel()});

			for (TreeModelListener l : listeners) l.treeNodesInserted(treeEvent);
		}

		@EventHandler
		public void onBlockStateOpened(BlockStateOpenedEvent event) {
			TreeModelEvent treeEvent = new TreeModelEvent(this, new Object[] {NODE_ROOT, NODE_BLOCK_STATES}, new int[] {modelCreator.getBlockStateIndex(event.getBlockState())}, new Object[] {event.getBlockState()});

			for (TreeModelListener l : listeners) l.treeNodesInserted(treeEvent);
		}

		@Override
		public void valueForPathChanged(TreePath path, Object newValue) {
			System.out.println("TreeModel.valueForPathChanged called");
		}
	}

	public static class ModelListTreeCellRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = -5633290313772700036L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

			if (value instanceof String) {
				String str = (String) value;

				setText(str);

				if (str.equals(ModelListTreeModel.NODE_MODELS)) {
					setIcon(new ImageIcon(getClass().getResource("/icon/modellist/models.png")));
				}
			} else if (value instanceof BaseModel) {
				BaseModel model = (BaseModel) value;

				setText(model.getName());
			} else if (value instanceof BlockState) {
				setText(((BlockState) value).getName());
			} else {
				setText(value.toString());
			}

			return this;
		}
	}
}
