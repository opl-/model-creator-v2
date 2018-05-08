package me.opl.apps.modelcreator2.panel;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

import me.opl.apps.modelcreator2.MCVariableProvider;
import me.opl.apps.modelcreator2.event.ElementsRotatedEvent;
import me.opl.apps.modelcreator2.event.EventHandler;
import me.opl.apps.modelcreator2.event.EventListener;
import me.opl.apps.modelcreator2.event.ModelOpenedEvent;
import me.opl.apps.modelcreator2.event.SelectionChangedEvent;
import me.opl.apps.modelcreator2.model.BaseModel;
import me.opl.apps.modelcreator2.model.RotatableElement;
import me.opl.libs.tablib.AbstractPanel;

public class PropertiesPanel extends AbstractPanel implements EventListener {
	private JPanel panel;

	private RotatableElement element;
	private JTextField rotXField;
	private JTextField rotYField;
	private JTextField rotZField;

	public PropertiesPanel(MCVariableProvider variableProvider) {
		super(variableProvider);

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		rotXField = new JTextField();
		rotYField = new JTextField();
		rotZField = new JTextField();

		panel.add(rotXField);
		panel.add(rotYField);
		panel.add(rotZField);

		variableProvider.getModelCreator().getGlobalEventDispatcher().registerListeners(this);
	}

	@EventHandler
	public void onModelOpened(ModelOpenedEvent event) {
		BaseModel m = event.getModel();

		m.getEventDispatcher().registerListeners(PropertiesPanel.this);
	}

	@EventHandler
	public void onSelectionChanged(SelectionChangedEvent event) {
		element = (RotatableElement) (event.getElements().length > 0 ? event.getElements()[0] : null);
		updateRotation();
	}

	@EventHandler
	public void onRotationChanged(ElementsRotatedEvent event) {
		updateRotation();
	}

	public void updateRotation() {
		if (element == null) {
			rotXField.setText("(no element)");
			rotYField.setText("(no element)");
			rotZField.setText("(no element)");
		} else {
			rotXField.setText("" + element.getRotation().getXd());
			rotYField.setText("" + element.getRotation().getYd());
			rotZField.setText("" + element.getRotation().getZd());
		}
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public String getTitle() {
		return "Properties";
	}
}
