package BE;

import java.util.Iterator;

public  class CaesarCipher {
	public static String ecript (int number, String text) {
		StringBuilder res = new StringBuilder();
		 for (char c : text.toCharArray()) {
	                c = (char) (c + number);
	            res.append(c);
	        }
	        return res.toString();
	}
	public static void main(String[] args) {
		
//		System.out.println( ecript(4, "Trí đẹp trai quá Hello"));
		System.out.println( ecript(-4, "Xvñ$ĕẽt$xvem$uyå$Lipps"));
	}
}
