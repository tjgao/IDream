package com.dream.wechat;

import java.awt.color.CMMException;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;


public class CommonUtils {
	public static String getFileNameExtension(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0)
			return fileName.substring(i + 1);
		return "";
	}

	public static String md5(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void cleanDirectory(File dir) {
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		File[] files = dir.listFiles();
		for (File i : files) {
			if (i.isFile())
				i.delete();
			else if (i.isDirectory()) {
				cleanDirectory(i);
				i.delete();
			}
		}
	}

	public static void cleanDirectory(String dir) {
		File f = new File(dir);
		cleanDirectory(f);
	}

	public static boolean moveFile(String srcFile, String dstDir) {
		try {
			File src = new File(srcFile);
			File dst = new File(dstDir + File.separator + src.getName());
			return src.renameTo(dst);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getTempDir() {
		String t = System.getProperties().getProperty("java.io.tmpdir")
				+ File.separator + "_iKuguan";
		File f = new File(t);
		if (f.exists()) {
			if (f.isFile())
				f.delete();
		} else {
			f.mkdir();
		}
		return t;
	}
	
	public static void downloadImg(String url, String dst) throws Exception {
		URL u = new URL(url);
		BufferedImage img = ImageIO.read(u);
		ImageIO.write(img, "jpg", new File(dst));
	}
	
	public static void downloadAndThumb(String url, String dst, String thumb, int width, int height) throws Exception {
		URL u = new URL(url);
		BufferedImage img = ImageIO.read(u);
		//crop the image
		int w = img.getWidth();
		int h = img.getHeight();
	     
		int x = ( w < h) ? 0 : ( w - h )/2;
		int y = ( h < w) ? 0 : ( h - w )/2;
		int m = (w > h) ? h : w;
		
		BufferedImage cropped = img.getSubimage(x, y, m, m);
		
		BufferedImage thumbnail = Scalr.resize(cropped, Method.QUALITY,
				Mode.AUTOMATIC, width, height, Scalr.OP_ANTIALIAS);
		ImageIO.write(img, "jpg", new File(dst));
		ImageIO.write(thumbnail, "jpg", new File(thumb));
	}
	
	public static void thumbnail(String src, String dst, int width, int height)
			throws Exception {
		File fsrc = new File(src);
		String ext = getFileNameExtension(src).toLowerCase();
		BufferedImage img = null;
		try {
			img = ImageIO.read(fsrc);
		} catch (CMMException ce) {
			ce.printStackTrace();
			return;
//			SeekableStream ss = new FileSeekableStream(src);
//			ParameterBlock pb = new ParameterBlock();
//			pb.add(ss);
//			img = JAI.create("jpeg", pb).getAsBufferedImage();
		}
		//crop the image
		int w = img.getWidth();
		int h = img.getHeight();
	     
		int x = ( w < h) ? 0 : ( w - h )/2;
		int y = ( h < w) ? 0 : ( h - w )/2;
		int m = (w > h) ? h : w;
		BufferedImage cropped = img.getSubimage(x, y, m, m);
		
		BufferedImage thumbnail = Scalr.resize(cropped, Method.QUALITY,
				Mode.AUTOMATIC, width, height, Scalr.OP_ANTIALIAS);
		File fdst = new File(dst);
		ImageIO.write(thumbnail, ext, fdst);
	}
}
