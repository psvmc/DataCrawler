package com.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @作者 张剑
 * @版本 1.1
 * @日期 2014年8月16日
 * @时间 下午2:15:59
 */
public final class ZJ_ValiCodeUtils {

	// 产生多少字符
	private static final int SIZE = 4;
	// 干扰线数量
	private static final int LINES = 12;
	// 图片宽
	private static final int WIDTH = 120;
	// 图片高
	private static final int HEIGHT = 40;
	// 字体大小
	private static final int FONT_SIZE = 30;

	private static String getRandomStr() {
		String randomString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String str2 = randomString.toLowerCase();
		randomString += str2;
		StringBuffer sb = new StringBuffer();
		Random ran = new Random();
		// 画随机字符
		for (int i = 1; i <= SIZE; i++) {
			int r = ran.nextInt(randomString.length());
			sb.append(randomString.charAt(r));
		}
		return sb.toString();
	}

	private static float getRandomFloat(float min, float max) {
		return (float) (min + (max - min) * Math.random());
	}

	private static Color getRandomColor(float min, float max) {
		Random ran = new Random();
		float a = getRandomFloat(min, max);
		Color color = new Color(ran.nextFloat(), ran.nextFloat(), ran.nextFloat(), a);
		return color;
	}

	/**
	 * 干扰字符
	 * 
	 * @param graphic
	 */
	private static void setZifu(Graphics graphic) {
		Random ran = new Random();
		for (int i = 1; i <= LINES; i++) {
			graphic.setColor(getRandomColor(0.1f, 0.2f));
			graphic.setFont(new Font("Consolas", Font.BOLD + Font.ROMAN_BASELINE, 40));
			int x1 = ran.nextInt(WIDTH);
			int y1 = ran.nextInt(WIDTH);
			graphic.drawString(getRandomStr(), x1, y1);
		}
	}

	/**
	 * 干扰线
	 * 
	 * @param graphic
	 */
	@SuppressWarnings("unused")
	private static void setLine(Graphics graphic) {
		Random ran = new Random();
		for (int i = 1; i <= LINES; i++) {
			graphic.setColor(getRandomColor(0.1f, 0.2f));
			graphic.setFont(new Font("Consolas", Font.BOLD + Font.ROMAN_BASELINE, 40));
			int x1 = ran.nextInt(WIDTH);
			int y1 = ran.nextInt(WIDTH);
			int x2 = x1 + ran.nextInt(20) - 20;
			int y2 = y1 + ran.nextInt(20) - 20;
			graphic.drawLine(x1, y1, x2, y2);
		}
	}

	public static void createImage(HttpServletRequest request, HttpServletResponse response) {
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics graphic = image.getGraphics();
		graphic.setColor(Color.WHITE);
		graphic.setFont(new Font("Consolas", Font.ITALIC, FONT_SIZE));
		graphic.fillRect(0, 0, WIDTH, HEIGHT);
		String randomStr = getRandomStr();
		// 画干扰线
		setZifu(graphic);
		// 画随机字符
		for (int i = 1; i <= SIZE; i++) {
			graphic.setColor(getRandomColor(0.6f, 1.0f));
			graphic.setFont(new Font("Consolas", Font.BOLD + Font.ROMAN_BASELINE, FONT_SIZE));
			graphic.drawString(randomStr.charAt(i - 1) + "", (i - 1) * WIDTH / SIZE, FONT_SIZE);
		}

		try {
			response.setContentType("image/jpeg");// 设置相应类型,告诉浏览器输出的内容为图片
			response.setHeader("Pragma", "No-cache");// 设置响应头信息，告诉浏览器不要缓存此内容
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expire", 0);
			ImageIO.write(image, "JPEG", response.getOutputStream());
			request.getSession().setAttribute("valicode", randomStr);
		} catch (Exception e) {

		}
	}

}
