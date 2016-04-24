import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
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

	public static BufferedImage convertToGrayImage(BufferedImage image) {

		float[][] grays = convertToGray(image);

		for (int i = 0; i < image.getHeight(); i++) {

			for (int j = 0; j < image.getWidth(); j++) {

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

				power[i][j] = ((1.0f - gray[i][j]) * width) + low;

			}

		}

		return power;

	}

	public static void main(String[] args) {

		BufferedImage coloredImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

		try {

			coloredImage = ImageIO.read(new File("test.png"));

		} catch (IOException e) {

			System.out.println("Error! Could not read image.");

		}

		float[][] gray = GraycoderCore.convertToGray(coloredImage);
		float[][] power = GraycoderCore.convertToPower(gray, 0.17f, 1.0f);

		for (int i = 0; i < power.length; i++) {

			for (int j = 0; j < power[0].length; j++) {

				System.out.printf("%.2f  ", power[i][j]);

			}

			System.out.println();

		}

	}

}