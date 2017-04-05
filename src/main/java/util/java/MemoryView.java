package util.java;

import java.text.DecimalFormat;

public class MemoryView {
	public static String getMemoryInfo() {
		DecimalFormat f1 = new DecimalFormat("#,###KB");
		DecimalFormat f2 = new DecimalFormat("##.#");
		long free = Runtime.getRuntime().freeMemory() / 1024;
		long total = Runtime.getRuntime().totalMemory() / 1024;
		long max = Runtime.getRuntime().maxMemory() / 1024;
		long used = total - free;
		double ratio = (used * 100 / (double) max);
		String info = "Java メモリ情報 : 合計=" + f1.format(total) + "、" + "使用量=" + f1.format(used) + " (" + f2.format(ratio) + "%)、" + "使用可能最大=" + f1.format(max);
		return info;
	}
}
