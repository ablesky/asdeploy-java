package com.ablesky.asdeploy.security.jcaptcha;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import com.jhlabs.image.PinchFilter;
import com.jhlabs.math.ImageFunction2D;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.deformation.ImageDeformationByBufferedImageOp;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.GlyphsPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.HorizontalSpaceGlyphsVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.OverlapGlyphsUsingShapeVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.RotateGlyphsRandomVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateAllToRandomPointVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateGlyphsVerticalRandomVisitor;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;

/**
 * <p>本类用于生成验证码图片</p>
 * <p>是根据{@link com.octo.captcha.engine.image.gimpy.GmailEngine}修改而成</p>
 * @author zyang
 */
public class MyGmailEngine extends ListImageCaptchaEngine {

	protected void buildInitialFactories() {
		// word generator
		WordGenerator words = new RandomWordGenerator("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");
		TextPaster randomPaster = new GlyphsPaster(4, 4,
				new SingleColorGenerator(new Color(0, 153, 255)),
				new GlyphsVisitors[] {
						new TranslateGlyphsVerticalRandomVisitor(1),
						new RotateGlyphsRandomVisitor(Math.PI / 32),
						new OverlapGlyphsUsingShapeVisitor(3),
						new HorizontalSpaceGlyphsVisitor(5),
						new TranslateAllToRandomPointVisitor(5, 5) });

		BackgroundGenerator back = new UniColorBackgroundGenerator(150, 55, new Color(238, 238, 238));

		FontGenerator shearedFont = new RandomFontGenerator(50, 50, new Font[] {
				new Font("Bell MT", Font.BOLD, 50),
				new Font("Credit valley", Font.BOLD, 50) 
				}, false);

		PinchFilter pinch1 = new PinchFilter();
		pinch1.setAmount(-.5f);
		pinch1.setRadius(70);
		pinch1.setAngle((float) (Math.PI / 16));
		pinch1.setCentreX(0.5f);
		pinch1.setCentreY(-0.01f);
		pinch1.setEdgeAction(ImageFunction2D.CLAMP);

		PinchFilter pinch2 = new PinchFilter();
		pinch2.setAmount(-.6f);
		pinch2.setRadius(70);
		pinch2.setAngle((float) (Math.PI / 16));
		pinch2.setCentreX(0.3f);
		pinch2.setCentreY(1.01f);
		pinch2.setEdgeAction(ImageFunction2D.CLAMP);

		PinchFilter pinch3 = new PinchFilter();
		pinch3.setAmount(-.6f);
		pinch3.setRadius(70);
		pinch3.setAngle((float) (Math.PI / 16));
		pinch3.setCentreX(0.8f);
		pinch3.setCentreY(-0.01f);
		pinch3.setEdgeAction(ImageFunction2D.CLAMP);

		List<ImageDeformation> textDef = new ArrayList<ImageDeformation>();
		textDef.add(new ImageDeformationByBufferedImageOp(pinch1));
		textDef.add(new ImageDeformationByBufferedImageOp(pinch2));
		textDef.add(new ImageDeformationByBufferedImageOp(pinch3));

		// word2image 1
		WordToImage word2image = new DeformedComposedWordToImage(false, shearedFont, back,
				randomPaster, new ArrayList<ImageDeformation>(),
				new ArrayList<ImageDeformation>(), textDef
		);

		this.addFactory(new com.octo.captcha.image.gimpy.GimpyFactory(
				words, word2image, false));
	}
}