import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import javax.imageio.ImageIO;
import java.io.IOException;

public class GraycoderCore {

	public static float[][] convertToGray(BufferedImage image) {

		int height = image.getHeight();
		int width  = image.getWidth();

		float[][] grayArray = new float[height][width];

		for (int i = 0; i < height; i++) {

			for (int j = 0; j < width; j++) {

				Color currentColor = new Color(image.getRGB(j, i));
				float[] hsb = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null);
				//grayArray[i][j] = hsb[2];

			}

		}

		return grayArray;

	}

	public static BufferedImage convertToGrayImage(float[][] grays) {

		BufferedImage image = new BufferedImage(grays[0].length, grays.length, BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < grays.length; i++) {

			for (int j = 0; j < grays[0].length; j++) {

				int colorInt = Color.HSBtoRGB(0.0f, 0.0f, grays[i][j]);
				image.setRGB(j, i, colorInt);

			}

		}

		return image;

	}

	public static float[][] convertToPower(float[][] gray, float low, float high) {

		float width = high - low;

		float[][] power = new float[gray.length][gray[0].length];

		for (int i = 0; i < gray.length; i++) {

			for (int j = 0; j < gray[0].length; j++) {

				float powerLevel = ((1.0f - gray[i][j]) * width) + low;
				power[i][j] = powerLevel;

			}

		}

		return power;

	}

	public static ArrayList<String> convertToGCodePoints(float[][] p, int travelSpeed, int cutSpeed) {

		ArrayList<String> gcodeList = new ArrayList<String>();

		String home = String.format("G0 X0 Y0 F%d %n", travelSpeed);
		gcodeList.add(home);

		for (int i = 0; i < p.length; i++) {

			String startLine = String.format("G0 X0 Y%d F%d %n", i, travelSpeed);
			gcodeList.add(startLine);

			for (int j = 0; j < p[0].length; j++) {

				String command = String.format("G1 X%d Y%d F%d S%.3f %n", j + 1, i, cutSpeed, p[i][j]);
				gcodeList.add(command);

			}

		}

		gcodeList.add(home);

		return gcodeList;

	}

	public static void writeToFile(String filename, ArrayList<String> gcode) {

		try {

			FileWriter output = new FileWriter(new File(filename));

			for (String s : gcode) {

				output.write(s);

			}

			output.close();

		} catch (IOException e) {

			System.out.println("Error! Cannot write to output file.");

		}

	}

	/*public static void main(String[] args) {

		BufferedImage coloredImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

		try {

			coloredImage = ImageIO.read(new File("test.png"));

		} catch (IOException e) {

			System.out.println("Error! Could not read image.");

		}

		float[][] gray = GraycoderCore.convertToGray(coloredImage);
		float[][] power = GraycoderCore.convertToPower(gray, 0.150f, 0.190f);
		ArrayList<String> gcode = GraycoderCore.convertToGCodePoints(power, 2000, 2000);
		GraycoderCore.writeToFile("output.txt", gcode);

	}*/

}
