import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.GridLayout;

public class GraycoderFrame extends JFrame implements ActionListener {

	private BufferedImage grayImage;
	private BufferedImage originalImage;
	private BufferedImage powerImage;

	private File imageFile;
	private File outputFile;

	private float maximum;
	private float minimum;

	private HashMap<String, String> settingsMap;

	private int laserSpeed;
	private int travelSpeed;

	private JButton generateButton;
	private JButton previewButton;
	private JButton selectFileButton;
	private JButton selectImageButton;

	private JFileChooser imageChooser;
	private JFileChooser outputChooser;

	private JFrame imageFrame;
	private JFrame outputFrame;
	private JFrame previewFrame;

	private JLabel cutLabel;
	private JLabel grayLabel;
	private JLabel maximumLabel;
	private JLabel minimumLabel;
	private JLabel originalLabel;
	private JLabel passesLabel;
	private JLabel powerLabel;
	private JLabel stepsLabel;
	private JLabel travelLabel;

	private JTextField cutField;
	private JTextField maximumField;
	private JTextField minimumField;
	private JTextField passesField;
	private JTextField stepsField;
	private JTextField travelField;

	public GraycoderFrame() {

		super("Graycoder");

		settingsMap = new HashMap<String, String>();

		loadSettings();

		imageFile = new File(settingsMap.get("default image"));
		outputFile = new File(settingsMap.get("default output"));

		setLayout(new GridLayout(8, 1));

		minimumLabel = new JLabel("Minimum Power:");
		add(minimumLabel);

		minimumField = new JTextField(settingsMap.get("minimum power"));
		add(minimumField);

		maximumLabel = new JLabel("Maximum Power:");
		add(maximumLabel);

		maximumField = new JTextField(settingsMap.get("maximum power"));
		add(maximumField);

		travelLabel = new JLabel("Travel Speed:");
		add(travelLabel);

		travelField = new JTextField(settingsMap.get("travel speed"));
		add(travelField);

		cutLabel = new JLabel("Cut Speed:");
		add(cutLabel);

		cutField = new JTextField(settingsMap.get("cut speed"));
		add(cutField);

		passesLabel = new JLabel("Outline Passes:");
		add(passesLabel);

		passesField = new JTextField(settingsMap.get("outline passes"));
		add(passesField);

		stepsLabel = new JLabel("Steps Per Pixel:");
		add(stepsLabel);

		stepsField = new JTextField(settingsMap.get("steps per pixel"));
		add(stepsField);

		selectImageButton = new JButton("Select Input Image");
		selectImageButton.setActionCommand("image");
		selectImageButton.addActionListener(this);
		add(selectImageButton);

		previewButton = new JButton("Preview");
		previewButton.setActionCommand("preview");
		previewButton.addActionListener(this);
		add(previewButton);

		selectFileButton = new JButton("Select Output File");
		selectFileButton.setActionCommand("output");
		selectFileButton.addActionListener(this);
		add(selectFileButton);

		generateButton = new JButton("Generate GCode");
		generateButton.setActionCommand("generate");
		generateButton.addActionListener(this);
		add(generateButton);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		pack();
		setVisible(true);

	}

	public void loadSettings() {

		Scanner in = null;

		try {

			in = new Scanner(new File("settings.ini"));

		} catch (IOException e) {

			System.out.println("Error! Could not read settings file.");

		}

		while (in.hasNextLine()) {

			String line = in.nextLine();
			String[] pair = line.split(": ");
			settingsMap.put(pair[0], pair[1]);

		}

	}

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if ("generate".equals(command)) {

			originalImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

			try {

				originalImage = ImageIO.read(imageFile);

			} catch (IOException ex) {

				System.out.println("Error! Could not read image.");

			}

			float[][] gray = GraycoderCore.convertToGray(originalImage);
			float low = Float.parseFloat(minimumField.getText());
			float high = Float.parseFloat(maximumField.getText());
			float[][] power = GraycoderCore.convertToPower(gray, low, high);
			GraycoderCore.reflectX(power);
			int travel = Integer.parseInt(travelField.getText());
			int cut = Integer.parseInt(cutField.getText());
			int passes = Integer.parseInt(passesField.getText());
			double steps = Double.parseDouble(stepsField.getText());
			ArrayList<String> gcode = GraycoderCore.convertToGCode(power, travel, cut, passes, steps);
			GraycoderCore.writeToFile(outputFile, gcode);

		} else if ("image".equals(command)) {

			imageChooser = new JFileChooser();
			imageChooser.addActionListener(this);

			imageFrame = new JFrame("Choose Input Image");
			imageFrame.add(imageChooser);
			imageFrame.pack();
			imageFrame.setVisible(true);

		} else if ("output".equals(command)) {

			outputChooser = new JFileChooser();
			outputChooser.addActionListener(this);

			outputFrame = new JFrame("Chooser Output File");
			outputFrame.add(outputChooser);
			outputFrame.pack();
			outputFrame.setVisible(true);

		} else if ("preview".equals(command)) {

			try {

				originalImage = ImageIO.read(imageFile);

			} catch (IOException ex) {

				System.out.println("Error! Could not read image.");

			}

			grayImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			float[][] gray = GraycoderCore.convertToGray(originalImage);
			grayImage = GraycoderCore.convertToGrayImage(gray);
			float low = Float.parseFloat(minimumField.getText());
			float high = Float.parseFloat(maximumField.getText());
			float[][] power = GraycoderCore.convertToPower(gray, low, high);

			for (int i = 0; i < power.length; i++) {

				for (int j = 0; j < power[0].length; j++) {

					power[i][j] = 1.0f - power[i][j];

				}

			}

			powerImage = GraycoderCore.convertToGrayImage(power);

			previewFrame = new JFrame("Preview");
			previewFrame.setLayout(new GridLayout(1, 3));
			originalLabel = new JLabel(new ImageIcon(originalImage));
			grayLabel = new JLabel(new ImageIcon(grayImage));
			powerLabel = new JLabel(new ImageIcon(powerImage));
			previewFrame.add(originalLabel);
			previewFrame.add(grayLabel);
			previewFrame.add(powerLabel);
			previewFrame.pack();
			previewFrame.setVisible(true);

		} else if ("ApproveSelection".equals(command)) {

			if (e.getSource() == imageChooser) {

				imageFile = imageChooser.getSelectedFile();
				imageFrame.dispose();

			} else if (e.getSource() == outputChooser) {

				outputFile = outputChooser.getSelectedFile();
				outputFrame.dispose();

			}

		} else if ("CancelSelection".equals(command)) {

			if (e.getSource() == imageChooser) {

				imageFrame.dispose();

			} else if (e.getSource() == outputChooser) {

				outputFrame.dispose();

			}

		}

	}

}
