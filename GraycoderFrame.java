import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class GraycoderFrame extends JFrame implements ActionListener {

	private BufferedImage grayImage;
	private BufferedImage originalImage;
	private BufferedImage powerImage;

	private File imageFile;

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

	private JFrame imageFrame;

	private JLabel cutLabel;
	private JLabel maximumLabel;
	private JLabel minimumLabel;
	private JLabel travelLabel;

	private JTextField cutField;
	private JTextField maximumField;
	private JTextField minimumField;
	private JTextField travelField;

	public GraycoderFrame() {

		super("Graycoder");

		settingsMap = new HashMap<String, String>();

		loadSettings();

		setLayout(new GridLayout(12, 1));

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

		selectImageButton = new JButton("Select Image");
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

		imageChooser = new JFileChooser();
		imageChooser.addActionListener(this);

		imageFrame = new JFrame("Choose Input Image");
		imageFrame.add(imageChooser);
		imageFrame.setVisible(true);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

		System.out.println("ActionCommand: " + command);

		if ("generate".equals(command)) {

			originalImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

			try {

				originalImage = ImageIO.read(imageFile);

			} catch (IOException ex) {

				System.out.println("Error! Could not read image.");

			}

			//this line is causing issues, original image is null
			float[][] gray = GraycoderCore.convertToGray(originalImage);

			try {

				float low = Float.parseFloat(minimumField.getText());
				float high = Float.parseFloat(maximumField.getText());
				float[][] power = GraycoderCore.convertToPower(gray, low, high);
				int travel = Integer.parseInt(travelField.getText());
				int cut = Integer.parseInt(cutField.getText());
				ArrayList<String> gcode = GraycoderCore.convertToGCodePoints(power, travel, cut);
				GraycoderCore.writeToFile("output.txt", gcode);

			} catch (Exception ex) {

				System.out.println("Message: " + ex.getMessage());

			}

		} else if ("image".equals(command)) {

			imageChooser.setVisible(true);

		} else if ("output".equals(command)) {

		} else if ("preview".equals(command)) {

		} else if ("ApproveSelection".equals(command)) {

			if (e.getSource() == imageChooser) {

				imageFile = imageChooser.getSelectedFile();
				imageFrame.dispose();

			}

		} else if ("CancelSelection".equals(command)) {

			if (e.getSource() == imageChooser) {

				imageFrame.dispose();

			}

		}

	}

}