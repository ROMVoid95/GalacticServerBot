package com.readonlydev.util.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

public class FileUtil {
	public static String readFromFile(File file) {
		try {
			Scanner scanner = new Scanner(file);
			StringBuilder stringBuilder = new StringBuilder();
			while (scanner.hasNextLine()) {
				stringBuilder.append(scanner.nextLine()).append("\n");
			}
			scanner.close();
			return stringBuilder.toString();
		} catch (FileNotFoundException e) {}	
		return null;
	}

	public static int readIntFromFile(File file) {
		try {
			FileUtil.createFileIfNotExist(file);
			String s = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
			return Integer.parseInt(s);
		} catch (IOException e) {

		}
		return -1;
	}
	
	
	
	public static void writeToFile(File file, int variable) {
		FileUtil.createFileIfNotExist(file);
		try {
			FileUtils.write(file, String.valueOf(variable), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static File createFileIfNotExist(File file) {
		try {
			boolean nuser = file.createNewFile();
			if (nuser) {
				FileWriter writer = new FileWriter(file);
				writer.write("0");
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	public static void handle(File file, boolean delete) {
		if (!delete) {
			try {
				FileUtil.createFileIfNotExist(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (delete) {
			try {
				FileUtils.forceDelete(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
