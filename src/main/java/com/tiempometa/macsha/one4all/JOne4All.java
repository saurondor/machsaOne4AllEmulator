/*
 * Copyright (c) 2019 Gerardo Esteban Tasistro Giubetic
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package com.tiempometa.macsha.one4all;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.tiempometa.macsha.one4all.commands.SetBuzzerCommand;
import com.tiempometa.macsha.one4all.states.UserInterfaceListener;

/**
 * @author Gerardo Esteban Tasistro Giubetic
 */
public class JOne4All extends JFrame implements UserInterfaceListener {
	private static final String BACKUP_PATH = "BACKUP_PATH";
	private static final Logger logger = Logger.getLogger(JOne4All.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -845275495452201283L;
	private One4AllSimulator simulator = new One4AllSimulator();
	Thread workerThread;
	ClockUpdater clockUpdater = new ClockUpdater();
	private File backupPath = null;
	private DefaultListModel<String> backupListModel = new DefaultListModel<String>();
	SettingsHandler settings = new SettingsHandler();

	public JOne4All() {
		initComponents();
		// load settings and init simulator file path
		try {
			settings.init();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "No se pudo cargar la configuración.\n" + e.getMessage(),
					"Error iniciando", JOptionPane.ERROR_MESSAGE);
		}
		String backupDirectory = settings.getSetting(BACKUP_PATH, SettingsHandler.getDefaultBackupPath());
		simulator.setBackupPath(new File(backupDirectory));
		backupPathLabel.setText(backupDirectory);
		backupPath = new File(backupDirectory);
		loadBackupFiles();

		portTextField.setText(String.valueOf(simulator.getPort()));
		voltageStatusLabel.setText(String.valueOf(batteryLevelSlider.getValue()));
//		bounceLabel.setText(String.valueOf((simulator.getState().getBounce())));
		Thread thread = new Thread(clockUpdater);
		thread.start();
		updateReaderState();
		backupFileList.setModel(backupListModel);
		setAntennaStatus(1, antenna1CheckBox.isSelected());
		setAntennaStatus(2, antenna2CheckBox.isSelected());
		setAntennaStatus(3, antenna3CheckBox.isSelected());
		setAntennaStatus(4, antenna4CheckBox.isSelected());
		setAntennaStatus(5, antenna5CheckBox.isSelected());
		setAntennaStatus(6, antenna6CheckBox.isSelected());
		setAntennaStatus(7, antenna7CheckBox.isSelected());
		setAntennaStatus(8, antenna8CheckBox.isSelected());
	}

	public static void main(String[] args) {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		JOne4All simulator = new JOne4All();
		simulator.setVisible(true);
	}

	private void powerUpButtonActionPerformed(ActionEvent e) {
		simulator.setPort(Integer.valueOf(portTextField.getText()));
		simulator.setUIListener(this);
		try {
			simulator.start();
			workerThread = new Thread(simulator);
			workerThread.start();
			powerUpButton.setForeground(Color.GREEN);
			powerUpButton.setText("Running");
			startReadingsButton.setEnabled(true);
			pushReadsButton.setEnabled(true);
			buzzerButton.setEnabled(true);
			openBackupPathButton.setEnabled(false);
			powerUpButton.setEnabled(false);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void startReadingsButtonActionPerformed(ActionEvent e) {
		if (simulator.getState().isReadingTags()) {
			logger.info("Setting stop mode");
			setStopReadMode();
			simulator.getState().stopReading();
		} else {
			logger.info("Setting start mode");
			setStartReadMode();
			try {
				simulator.getState().startReading();
				loadBackupFiles();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, "No se pudo iniciar el respaldo.\n" + e1.getMessage(),
						"Error de respaldo", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * 
	 */
	private void setStartReadMode() {
		startReadingsButton.setText("Stop");
		sendTagReadButton.setEnabled(true);
		tagTextField.setEnabled(true);
		readingStatusLabel.setText("Running");
		readingStatusLabel.setForeground(Color.GREEN);
	}

	/**
	 * 
	 */
	private void setStopReadMode() {
		startReadingsButton.setText("Start");
		sendTagReadButton.setEnabled(false);
		tagTextField.setEnabled(false);
		readingStatusLabel.setText("Stopped");
		readingStatusLabel.setForeground(Color.RED);
	}

	private void pushReadsButtonActionPerformed(ActionEvent e) {
		if (simulator.getState().isPushingTags()) {
			setPushReadOff();
			simulator.getState().stopPushingTags();
		} else {
			setPushReadOn();
			simulator.getState().startPushingTags();
		}
	}

	/**
	 * 
	 */
	private void setPushReadOn() {
		pushReadsButton.setText("Stop Pushing");
		pushReadingsStatusLabel.setText("Running");
		pushReadingsStatusLabel.setForeground(Color.GREEN);
	}

	/**
	 * 
	 */
	private void setPushReadOff() {
		pushReadsButton.setText("Start Pushing");
		pushReadingsStatusLabel.setText("Stopped");
		pushReadingsStatusLabel.setForeground(Color.RED);
	}

	private void buzzerButtonActionPerformed(ActionEvent e) {
		if (simulator.getState().isBuzzerOn()) {
			setBuzzerOff();
			simulator.getState().setBuzzerOff();
		} else {
			setBuzzerOn();
			simulator.getState().setBuzzerOn();
		}
	}

	private void batteryLevelSliderStateChanged(ChangeEvent e) {
		voltageStatusLabel.setText(String.valueOf(batteryLevelSlider.getValue()));
		simulator.getState()
				.setBatteryLevel(100f * ((Integer.valueOf(batteryLevelSlider.getValue()).floatValue() - 18f) / 6));
		simulator.getState().setVoltage(batteryLevelSlider.getValue());
	}

	private void acPowerCheckBoxStateChanged(ChangeEvent e) {
		if (acPowerCheckBox.isSelected()) {
			simulator.setAcPowerOn();
		} else {
			simulator.setAcPowerOff();
		}
	}

	private void sendTagReadButtonActionPerformed(ActionEvent e) {
		sendCode();
	}

	/**
	 * 
	 */
	private void sendCode() {
		try {
			simulator.sendTagRead(tagTextField.getText());
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(this, "No se pudo envíar el tag. " + e1.getMessage());
			e1.printStackTrace();
		}
	}

	private void openBackupPathButtonActionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser(backupPath);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int response = fc.showOpenDialog(this);
		if (response == JFileChooser.APPROVE_OPTION) {
			backupPath = fc.getSelectedFile();
			backupPathLabel.setText(backupPath.getAbsolutePath());
			settings.setSetting(BACKUP_PATH, backupPath.getAbsolutePath());
			try {
				settings.flush();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, "No se pudo guardar la configuración.\n" + e1.getMessage(),
						"Guardar configuración", JOptionPane.ERROR_MESSAGE);
			}
			loadBackupFiles();
		}
	}

	private void loadBackupFiles() {
		File[] backupFiles = backupPath.listFiles();
		backupListModel = new DefaultListModel<String>();
		backupFileList.setModel(backupListModel);
		for (File file : backupFiles) {
			backupListModel.addElement(file.getName());
		}
		simulator.setBackupPath(backupPath);
	}

	private void antenna5CheckBoxItemStateChanged(ItemEvent e) {
		setAntennaStatus(5, antenna5CheckBox.isSelected());
	}

	private void antenna2CheckBoxItemStateChanged(ItemEvent e) {
		setAntennaStatus(2, antenna2CheckBox.isSelected());
	}

	private void setAntennaStatus(int antenna, boolean status) {
		simulator.setAntennaStatus(antenna, status);
	}

	private void antenna6CheckBoxItemStateChanged(ItemEvent e) {
		setAntennaStatus(6, antenna6CheckBox.isSelected());
	}

	private void antenna3CheckBoxItemStateChanged(ItemEvent e) {
		setAntennaStatus(3, antenna3CheckBox.isSelected());
	}

	private void antenna7CheckBoxItemStateChanged(ItemEvent e) {
		setAntennaStatus(7, antenna7CheckBox.isSelected());
	}

	private void antenna4CheckBoxStateChanged(ChangeEvent e) {
		setAntennaStatus(4, antenna4CheckBox.isSelected());
	}

	private void antenna8CheckBoxStateChanged(ChangeEvent e) {
		setAntennaStatus(8, antenna8CheckBox.isSelected());
	}

	private void antenna1CheckBoxItemStateChanged(ItemEvent e) {
		setAntennaStatus(1, antenna1CheckBox.isSelected());
	}

	private void tagTextFieldKeyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			sendCode();
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("com.tiempometa.macsha.one4all.macshareader");
		label1 = new JLabel();
		powerUpButton = new JButton();
		label2 = new JLabel();
		portTextField = new JTextField();
		label7 = new JLabel();
		label18 = new JLabel();
		label8 = new JLabel();
		readingStatusLabel = new JLabel();
		startReadingsButton = new JButton();
		label19 = new JLabel();
		tagTextField = new JTextField();
		label6 = new JLabel();
		pushReadingsStatusLabel = new JLabel();
		pushReadsButton = new JButton();
		label20 = new JLabel();
		bibTextField = new JTextField();
		label5 = new JLabel();
		buzzerStatusLabel = new JLabel();
		buzzerButton = new JButton();
		sendTagReadButton = new JButton();
		scrollPane1 = new JScrollPane();
		backupFileList = new JList();
		label17 = new JLabel();
		bounceLabel = new JLabel();
		label4 = new JLabel();
		label10 = new JLabel();
		label11 = new JLabel();
		backupPathLabel = new JLabel();
		label12 = new JLabel();
		readerTimeLabel = new JLabel();
		openBackupPathButton = new JButton();
		label3 = new JLabel();
		voltageStatusLabel = new JLabel();
		batteryLevelSlider = new JSlider();
		acPowerCheckBox = new JCheckBox();
		label9 = new JLabel();
		panel1 = new JPanel();
		antenna1CheckBox = new JCheckBox();
		antenna5CheckBox = new JCheckBox();
		antenna2CheckBox = new JCheckBox();
		antenna6CheckBox = new JCheckBox();
		antenna3CheckBox = new JCheckBox();
		antenna7CheckBox = new JCheckBox();
		antenna4CheckBox = new JCheckBox();
		antenna8CheckBox = new JCheckBox();

		// ======== this ========
		setTitle(bundle.getString("JOne4All.this.title"));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setIconImage(new ImageIcon(getClass().getResource("/com/tiempometa/macsha/one4all/tiempometa_icon_small.png"))
				.getImage());
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
				"10dlu, $lcgap, default, $lcgap, 33dlu, $lcgap, default, $lcgap, 24dlu, $lcgap, 41dlu, $lcgap, 115dlu",
				"7dlu, 11*($lgap, default)"));

		// ---- label1 ----
		label1.setText(bundle.getString("JOne4All.label1.text"));
		contentPane.add(label1, CC.xy(3, 3));

		// ---- powerUpButton ----
		powerUpButton.setText(bundle.getString("JOne4All.powerUpButton.text"));
		powerUpButton.setForeground(Color.red);
		powerUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				powerUpButtonActionPerformed(e);
			}
		});
		contentPane.add(powerUpButton, CC.xywh(5, 3, 3, 1));

		// ---- label2 ----
		label2.setText(bundle.getString("JOne4All.label2.text"));
		contentPane.add(label2, CC.xy(11, 3));

		// ---- portTextField ----
		portTextField.setText(bundle.getString("JOne4All.portTextField.text"));
		contentPane.add(portTextField, CC.xy(13, 3));

		// ---- label7 ----
		label7.setText(bundle.getString("JOne4All.label7.text"));
		label7.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPane.add(label7, CC.xy(3, 5));

		// ---- label18 ----
		label18.setText(bundle.getString("JOne4All.label18.text"));
		label18.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPane.add(label18, CC.xywh(11, 5, 3, 1));

		// ---- label8 ----
		label8.setText(bundle.getString("JOne4All.label8.text"));
		contentPane.add(label8, CC.xy(3, 7));

		// ---- readingStatusLabel ----
		readingStatusLabel.setText(bundle.getString("JOne4All.readingStatusLabel.text"));
		contentPane.add(readingStatusLabel, CC.xy(5, 7));

		// ---- startReadingsButton ----
		startReadingsButton.setText(bundle.getString("JOne4All.startReadingsButton.text"));
		startReadingsButton.setEnabled(false);
		startReadingsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startReadingsButtonActionPerformed(e);
			}
		});
		contentPane.add(startReadingsButton, CC.xy(7, 7));

		// ---- label19 ----
		label19.setText(bundle.getString("JOne4All.label19.text"));
		contentPane.add(label19, CC.xy(11, 7));

		// ---- tagTextField ----
		tagTextField.setEnabled(false);
		tagTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				tagTextFieldKeyReleased(e);
			}
		});
		contentPane.add(tagTextField, CC.xy(13, 7));

		// ---- label6 ----
		label6.setText(bundle.getString("JOne4All.label6.text"));
		contentPane.add(label6, CC.xy(3, 9));

		// ---- pushReadingsStatusLabel ----
		pushReadingsStatusLabel.setText(bundle.getString("JOne4All.pushReadingsStatusLabel.text"));
		contentPane.add(pushReadingsStatusLabel, CC.xy(5, 9));

		// ---- pushReadsButton ----
		pushReadsButton.setText(bundle.getString("JOne4All.pushReadsButton.text"));
		pushReadsButton.setEnabled(false);
		pushReadsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pushReadsButtonActionPerformed(e);
			}
		});
		contentPane.add(pushReadsButton, CC.xy(7, 9));

		// ---- label20 ----
		label20.setText(bundle.getString("JOne4All.label20.text"));
		contentPane.add(label20, CC.xy(11, 9));

		// ---- bibTextField ----
		bibTextField.setEnabled(false);
		contentPane.add(bibTextField, CC.xy(13, 9));

		// ---- label5 ----
		label5.setText(bundle.getString("JOne4All.label5.text"));
		contentPane.add(label5, CC.xy(3, 11));

		// ---- buzzerStatusLabel ----
		buzzerStatusLabel.setText(bundle.getString("JOne4All.buzzerStatusLabel.text"));
		contentPane.add(buzzerStatusLabel, CC.xy(5, 11));

		// ---- buzzerButton ----
		buzzerButton.setText(bundle.getString("JOne4All.buzzerButton.text"));
		buzzerButton.setEnabled(false);
		buzzerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buzzerButtonActionPerformed(e);
			}
		});
		contentPane.add(buzzerButton, CC.xy(7, 11));

		// ---- sendTagReadButton ----
		sendTagReadButton.setText(bundle.getString("JOne4All.sendTagReadButton.text"));
		sendTagReadButton.setEnabled(false);
		sendTagReadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendTagReadButtonActionPerformed(e);
			}
		});
		contentPane.add(sendTagReadButton, CC.xy(13, 11));

		// ======== scrollPane1 ========
		{
			scrollPane1.setViewportView(backupFileList);
		}
		contentPane.add(scrollPane1, CC.xywh(11, 19, 3, 5));

		// ---- label17 ----
		label17.setText(bundle.getString("JOne4All.label17.text"));
		contentPane.add(label17, CC.xy(3, 13));

		// ---- bounceLabel ----
		bounceLabel.setText(bundle.getString("JOne4All.bounceLabel.text"));
		contentPane.add(bounceLabel, CC.xy(5, 13));

		// ---- label4 ----
		label4.setText(bundle.getString("JOne4All.label4.text"));
		label4.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPane.add(label4, CC.xywh(11, 13, 3, 1));

		// ---- label10 ----
		label10.setText(bundle.getString("JOne4All.label10.text"));
		contentPane.add(label10, CC.xy(3, 15));

		// ---- label11 ----
		label11.setText(bundle.getString("JOne4All.label11.text"));
		contentPane.add(label11, CC.xywh(5, 15, 3, 1));
		contentPane.add(backupPathLabel, CC.xywh(11, 15, 3, 1));

		// ---- label12 ----
		label12.setText(bundle.getString("JOne4All.label12.text"));
		contentPane.add(label12, CC.xy(3, 17));

		// ---- readerTimeLabel ----
		readerTimeLabel.setText(bundle.getString("JOne4All.readerTimeLabel.text"));
		contentPane.add(readerTimeLabel, CC.xywh(5, 17, 3, 1));

		// ---- openBackupPathButton ----
		openBackupPathButton.setText(bundle.getString("JOne4All.openBackupPathButton.text"));
		openBackupPathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openBackupPathButtonActionPerformed(e);
			}
		});
		contentPane.add(openBackupPathButton, CC.xywh(11, 17, 3, 1));

		// ---- label3 ----
		label3.setText(bundle.getString("JOne4All.label3.text"));
		contentPane.add(label3, CC.xy(3, 19));

		// ---- voltageStatusLabel ----
		voltageStatusLabel.setText(bundle.getString("JOne4All.voltageStatusLabel.text"));
		contentPane.add(voltageStatusLabel, CC.xy(5, 19));

		// ---- batteryLevelSlider ----
		batteryLevelSlider.setMaximum(24);
		batteryLevelSlider.setMinimum(18);
		batteryLevelSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				batteryLevelSliderStateChanged(e);
			}
		});
		contentPane.add(batteryLevelSlider, CC.xy(7, 19));

		// ---- acPowerCheckBox ----
		acPowerCheckBox.setText(bundle.getString("JOne4All.acPowerCheckBox.text"));
		acPowerCheckBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				acPowerCheckBoxStateChanged(e);
			}
		});
		contentPane.add(acPowerCheckBox, CC.xy(7, 21));

		// ---- label9 ----
		label9.setText(bundle.getString("JOne4All.label9.text"));
		contentPane.add(label9, CC.xy(3, 23));

		// ======== panel1 ========
		{
			panel1.setLayout(new FormLayout("default, $lcgap, default", "3*(default, $lgap), default"));

			// ---- antenna1CheckBox ----
			antenna1CheckBox.setText(bundle.getString("JOne4All.antenna1CheckBox.text"));
			antenna1CheckBox.setSelected(true);
			antenna1CheckBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					antenna1CheckBoxItemStateChanged(e);
				}
			});
			panel1.add(antenna1CheckBox, CC.xy(1, 1));

			// ---- antenna5CheckBox ----
			antenna5CheckBox.setText(bundle.getString("JOne4All.antenna5CheckBox.text"));
			antenna5CheckBox.setSelected(true);
			antenna5CheckBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					antenna5CheckBoxItemStateChanged(e);
				}
			});
			panel1.add(antenna5CheckBox, CC.xy(3, 1));

			// ---- antenna2CheckBox ----
			antenna2CheckBox.setText(bundle.getString("JOne4All.antenna2CheckBox.text"));
			antenna2CheckBox.setSelected(true);
			antenna2CheckBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					antenna2CheckBoxItemStateChanged(e);
				}
			});
			panel1.add(antenna2CheckBox, CC.xy(1, 3));

			// ---- antenna6CheckBox ----
			antenna6CheckBox.setText(bundle.getString("JOne4All.antenna6CheckBox.text"));
			antenna6CheckBox.setSelected(true);
			antenna6CheckBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					antenna6CheckBoxItemStateChanged(e);
				}
			});
			panel1.add(antenna6CheckBox, CC.xy(3, 3));

			// ---- antenna3CheckBox ----
			antenna3CheckBox.setText(bundle.getString("JOne4All.antenna3CheckBox.text"));
			antenna3CheckBox.setSelected(true);
			antenna3CheckBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					antenna3CheckBoxItemStateChanged(e);
				}
			});
			panel1.add(antenna3CheckBox, CC.xy(1, 5));

			// ---- antenna7CheckBox ----
			antenna7CheckBox.setText(bundle.getString("JOne4All.antenna7CheckBox.text"));
			antenna7CheckBox.setSelected(true);
			antenna7CheckBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					antenna7CheckBoxItemStateChanged(e);
				}
			});
			panel1.add(antenna7CheckBox, CC.xy(3, 5));

			// ---- antenna4CheckBox ----
			antenna4CheckBox.setText(bundle.getString("JOne4All.antenna4CheckBox.text"));
			antenna4CheckBox.setSelected(true);
			antenna4CheckBox.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					antenna4CheckBoxStateChanged(e);
				}
			});
			panel1.add(antenna4CheckBox, CC.xy(1, 7));

			// ---- antenna8CheckBox ----
			antenna8CheckBox.setText(bundle.getString("JOne4All.antenna8CheckBox.text"));
			antenna8CheckBox.setSelected(true);
			antenna8CheckBox.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					antenna8CheckBoxStateChanged(e);
				}
			});
			panel1.add(antenna8CheckBox, CC.xy(3, 7));
		}
		contentPane.add(panel1, CC.xy(7, 23));
		setSize(695, 435);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
	private JLabel label1;
	private JButton powerUpButton;
	private JLabel label2;
	private JTextField portTextField;
	private JLabel label7;
	private JLabel label18;
	private JLabel label8;
	private JLabel readingStatusLabel;
	private JButton startReadingsButton;
	private JLabel label19;
	private JTextField tagTextField;
	private JLabel label6;
	private JLabel pushReadingsStatusLabel;
	private JButton pushReadsButton;
	private JLabel label20;
	private JTextField bibTextField;
	private JLabel label5;
	private JLabel buzzerStatusLabel;
	private JButton buzzerButton;
	private JButton sendTagReadButton;
	private JScrollPane scrollPane1;
	private JList backupFileList;
	private JLabel label17;
	private JLabel bounceLabel;
	private JLabel label4;
	private JLabel label10;
	private JLabel label11;
	private JLabel backupPathLabel;
	private JLabel label12;
	private JLabel readerTimeLabel;
	private JButton openBackupPathButton;
	private JLabel label3;
	private JLabel voltageStatusLabel;
	private JSlider batteryLevelSlider;
	private JCheckBox acPowerCheckBox;
	private JLabel label9;
	private JPanel panel1;
	private JCheckBox antenna1CheckBox;
	private JCheckBox antenna5CheckBox;
	private JCheckBox antenna2CheckBox;
	private JCheckBox antenna6CheckBox;
	private JCheckBox antenna3CheckBox;
	private JCheckBox antenna7CheckBox;
	private JCheckBox antenna4CheckBox;
	private JCheckBox antenna8CheckBox;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	@Override
	public void updateReaderState() {
		if (simulator.getState().isBuzzerOn()) {
			setBuzzerOn();
		} else {
			setBuzzerOff();
		}
		if (simulator.getState().isReadingTags()) {
			setStartReadMode();
		} else {
			setStopReadMode();
		}
		if (simulator.getState().isPushingTags()) {
			setPushReadOn();
		} else {
			setPushReadOff();
		}
		bounceLabel.setText(String.valueOf((simulator.getState().getBounce())));
		loadBackupFiles();
		synchronized (readerTimeLabel) {
			setClock();
		}
	}

	private void setClock() {
		readerTimeLabel
				.setText((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(simulator.getState().getReaderTime()));
	}

	private void setBuzzerOff() {
		buzzerButton.setText("Set buzzer On");
		buzzerStatusLabel.setText("Off");
		buzzerStatusLabel.setForeground(Color.RED);
	}

	private void setBuzzerOn() {
		buzzerButton.setText("Set buzzer Off");
		buzzerStatusLabel.setText("On");
		buzzerStatusLabel.setForeground(Color.GREEN);
	}

	private class ClockUpdater implements Runnable {

		@Override
		public void run() {
			do {
				synchronized (readerTimeLabel) {
					setClock();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} while (true);
		}
	}
}
