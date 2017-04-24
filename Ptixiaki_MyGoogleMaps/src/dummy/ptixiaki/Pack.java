package dummy.ptixiaki;

public class Pack {
	
	public static String toString(double[] data) {
        StringBuilder sb = new StringBuilder();
        sb.append(SmsReceiver.queryString +"\n");
        final int length = data.length;
        for (int i = 0; i < length; i++) {
            sb.append(data[i]);
            if (i < (length - 1)) {
                sb.append("\n");
                //countr++;
            }
        }
        return sb.toString();
    }
	
	public static String toString(float[] data) {
		StringBuilder sb = new StringBuilder();
		sb.append(SmsReceiver.queryString +"\n");
		final int length = data.length;
		for (int i = 0; i < length; i++) {
			sb.append(data[i]);
			if (i < (length - 1)) {
				sb.append("\n");
				//countr++;
			}
		}
		return sb.toString();
	}
}
