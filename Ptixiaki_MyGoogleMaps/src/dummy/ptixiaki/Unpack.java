package dummy.ptixiaki;

import android.text.TextUtils;

public class Unpack {
	
	public static float[] toFloat(String str) {
        if (TextUtils.isEmpty(str)) {
        	System.out.println("Unpack() returns NULL...\n");
            return new float[0]; // or null depending on your needs
        }
        else {
        	String[] srtData = TextUtils.split(str, "\n");
            float[] result = new float[srtData.length];
            for (int i = 0; i < srtData.length; i++) {
                result[i] = Float.parseFloat(srtData[i]);
            }
            //System.out.println("DisplayActivity results in array: results[7]\n");
            return result;
        }
    }
	
	public static double[] toDouble(String str) {
        if (TextUtils.isEmpty(str)) {
        	System.out.println("UnpackToDouble() returns NULL...\n");
            return new double[0]; // or null depending on your needs
        }
        else {
        	String[] srtData = TextUtils.split(str, "\n");
            double[] result = new double[srtData.length];
            for (int i = 0; i < srtData.length; i++) {
                result[i] = Double.parseDouble(srtData[i]);
            }
            //System.out.println("DisplayActivity results in array: results[7]\n");
            return result;
        }
    }
}
