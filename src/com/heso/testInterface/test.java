package com.heso.testInterface;

import com.heso.utility.StringTools;

public class test {
	public static void main(String[] args) {
		for(int i=0;i<18;i++){
			String code =StringTools.randomNumberString(6);
			System.out.println(code);
		}
	}
}
