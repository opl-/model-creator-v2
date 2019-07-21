package me.opl.apps.modelcreator2.menu;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import me.opl.apps.modelcreator2.ModelCreator;
import me.opl.apps.modelcreator2.model.BlockModel;
import me.opl.apps.modelcreator2.tool.ToolManager;
import me.opl.apps.modelcreator2.tool.tool.ElementCreateTool;
import me.opl.apps.modelcreator2.tool.tool.MoveTool;
import me.opl.apps.modelcreator2.tool.tool.RotateTool;

public class ContextMenuManager {
	private ArrayList<MenuSection> sections = new ArrayList<>();

	public MenuSection[] getSections() {
		return sections.toArray(new MenuSection[sections.size()]);
	}

	public void addSection(MenuSection menuSection) {
		sections.add(menuSection);
	}

	public static ContextMenuManager createDefaultWindowMenuManager(final ModelCreator mc) {
		ContextMenuManager windowMenuManager = new ContextMenuManager();

		MenuSection ms = new MenuSection("File");
		MenuItem mi = new MenuItem("New block model");
		mi.addActionListener(new MenuActionListener() {
			@Override
			public void onMenuAction(MenuItem menuItem) {
				// TODO: make dialog abstract
				BlockModel model = new BlockModel(mc, null, JOptionPane.showInputDialog("Enter block model name:"));
				model.setHasElements(true);
				mc.addModel(model);
			}
		});
		ms.addItem(mi);

		mi = new MenuItem("Exit");
		mi.addActionListener(new MenuActionListener() {
			@Override
			public void onMenuAction(MenuItem menuItem) {
				System.exit(0);
			}
		});
		ms.addItem(mi);
		windowMenuManager.addSection(ms);

		ms = new MenuSection("File");

		mi = new MenuItem("Undo");
		mi.addActionListener(new MenuActionListener() {
			@Override
			public void onMenuAction(MenuItem menuItem) {
				System.exit(0);
			}
		});
		ms.addItem(mi);

		mi = new MenuItem("Redo");
		mi.addActionListener(new MenuActionListener() {
			@Override
			public void onMenuAction(MenuItem menuItem) {
				System.exit(0);
			}
		});
		ms.addItem(mi);

		windowMenuManager.addSection(ms);

		ms = new MenuSection("Tool");

		mi = new MenuItem("Rotate");
		mi.addActionListener(new MenuActionListener() {
			@Override
			public void onMenuAction(MenuItem menuItem) {
				for (ToolManager tm : mc.getToolManagers()) {
					tm.setActiveTool(RotateTool.class);
				}
			}
		});
		ms.addItem(mi);

		mi = new MenuItem("Move");
		mi.addActionListener(new MenuActionListener() {
			@Override
			public void onMenuAction(MenuItem menuItem) {
				for (ToolManager tm : mc.getToolManagers()) {
					tm.setActiveTool(MoveTool.class);
				}
			}
		});
		ms.addItem(mi);

		mi = new MenuItem("Create element");
		mi.addActionListener(new MenuActionListener() {
			@Override
			public void onMenuAction(MenuItem menuItem) {
				for (ToolManager tm : mc.getToolManagers()) {
					tm.setActiveTool(ElementCreateTool.class);
				}
			}
		});
		ms.addItem(mi);

		windowMenuManager.addSection(ms);

		return windowMenuManager;
	}
}
