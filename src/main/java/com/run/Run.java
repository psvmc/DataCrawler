package com.run;

import com.jfinal.core.JFinal;

public class Run {

	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 8080, "/", 5);
	}
}
