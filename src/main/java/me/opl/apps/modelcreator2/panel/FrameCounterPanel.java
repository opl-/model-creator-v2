package me.opl.apps.modelcreator2.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import me.opl.apps.modelcreator2.MCVariableProvider;
import me.opl.apps.modelcreator2.model.PlaybackState;
import me.opl.libs.tablib.AbstractPanel;
import me.opl.libs.tablib.VariableProvider;

public class FrameCounterPanel extends AbstractPanel {
	private JPanel panel;
	private JTextField frameField;

	private PlaybackState playbackState;

	public FrameCounterPanel(VariableProvider variableProvider) {
		super(variableProvider);

		playbackState = ((MCVariableProvider) variableProvider).getModelCreator().getRenderManager().getPlaybackState();

		panel = createPanel();

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000 / 60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					frameField.setText("" + playbackState.getCurrentFrame());
				}
			}
		}).start();
	}

	private JPanel createPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));

		JButton button = new JButton("\u23EE");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				playbackState.setCurrentFrame(0);

				frameField.setText("" + playbackState.getCurrentFrame());
			}
		});
		controls.add(button);

		button = new JButton("\u23F8");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (!playbackState.isPlaying()) {
					playbackState.setSpeed(1f);
				} else {
					playbackState.setSpeed(0f);
				}

				frameField.setText("" + playbackState.getCurrentFrame());
			}
		});
		controls.add(button);

		button = new JButton("\u23F4");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				playbackState.setSpeed(0f);
				playbackState.setCurrentFrame(playbackState.getCurrentFrame() - 1);

				frameField.setText("" + playbackState.getCurrentFrame());
			}
		});
		controls.add(button);

		button = new JButton("\u23F5");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				playbackState.setSpeed(0f);
				playbackState.setCurrentFrame(playbackState.getCurrentFrame() + 1);

				frameField.setText("" + playbackState.getCurrentFrame());
			}
		});
		controls.add(button);

		panel.add(controls, BorderLayout.PAGE_START);

		JPanel bottom = new JPanel(new BorderLayout());

		frameField = new JTextField("0");
		bottom.add(frameField);
		panel.add(bottom, BorderLayout.CENTER);

		return panel;
	}

	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public String getTitle() {
		return "Frame counter";
	}
}
