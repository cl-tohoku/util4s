package util.java;

public class Stopwatch {
	String processName;
	long start;
	long end;
	long totalStart;
	boolean began = false;

	public void start(String name) {
		processName = name;
		System.err.println(name);
		start = System.currentTimeMillis();
		if (!began) {
			totalStart = start;
			began = true;
		}
	}

	public String stop() {
		end = System.currentTimeMillis();
		return String.format("%s, Proccess Time (ms): %1$,3d", processName, end - start);
	}

	public String split(String name) {
		end = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder(processName).append(", Proccess Time (ms):").append(String.format("%1$,3d", end - start));
		processName = name;
		start = end;
		return sb.toString();
	}

	public String showTotal() {
		return String.format("Total Proccess Time (ms): %1$,3d", end - totalStart);
	}
}
