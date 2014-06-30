package com.ablesky.asdeploy.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.ablesky.asdeploy.util.verifycode.ShadowGimpy;

public class ImageUtil {

	private ImageUtil() {}
	
	public static BufferedImage generateTextImage(int charWidth, int charHeight, String text) {
		int charNum = text.length();
		BufferedImage image = new BufferedImage(charWidth * charNum , charHeight, BufferedImage.TYPE_3BYTE_BGR);
		Graphics graphics = image.getGraphics();
		graphics.setColor(new Color(255, 255, 255));
		graphics.fillRect(0, 0, charWidth * charNum, charHeight);
		graphics.setColor(new Color(0, 153, 255));
		graphics.setFont(new Font("Corbe", Font.BOLD, 20));
		for (int i = 0; i < charNum; i++) {
			char c = text.charAt(i);
			graphics.drawString(String.valueOf(c), charWidth * i, charHeight - 6);
		}
		graphics.dispose();
		return new ShadowGimpy().getDistortedImage(image);
	}
}
