package com.ablesky.asdeploy.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.ablesky.asdeploy.util.verifycode.ShadowGimpy;

public class ImageUtil {

	private ImageUtil() {}
	
	@Deprecated
	public static BufferedImage generateTextImage(int unitWidth, int height, String text, Font font, Color fontColor, Color backgroundColor) {
		int charNum = text.length();
		BufferedImage image = new BufferedImage(unitWidth * charNum , height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics graphics = image.getGraphics();
		graphics.setColor(backgroundColor);
		graphics.fillRect(0, 0, unitWidth * charNum, height);
		graphics.setColor(fontColor);
		graphics.setFont(font);
		for (int i = 0; i < charNum; i++) {
			char c = text.charAt(i);
			graphics.drawString(String.valueOf(c), unitWidth * i + 2, height - 4);
		}
		graphics.dispose();
		return new ShadowGimpy().getDistortedImage(image);
	}
}
