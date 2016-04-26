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
				grayArray[i][j] = hsb[2];

			}

		}

		return grayArray;

	}

	public static BufferedImage convertToGrayImage(float[][] grays) {

		int height = grays.length;
		int width = grays[0].length;

		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < width; i++) {

			for (int j = 0; j < height; j++) {

				int color = Color.HSBtoRGB(0.0f, 0.0f, grays[j][i]);
				newImage.setRGB(i, j, color);

			}

		}

		return newImage;

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

	public void copyImage(BufferedImage source, BufferedImage destination) {

		int sourceHeight = source.getHeight();
		int sourceWidth = source.getWidth();
		int destinationHeight = destination.getHeight();
		int destinationWidth = destination.getWidth();

		if (sourceHeight == destinationHeight && sourceWidth == destinationWidth) {

			for (int i = 0; i < sourceWidth; i++) {

				for (int j = 0; j < sourceHeight; j++) {

					destination.setRGB(i, j, source.getRGB(i, j));

				}

			}

		}

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

}