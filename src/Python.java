import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;

public class Python {
	
	private static PythonThread[] threads = new PythonThread[5];
	
	static class TimeoutThread extends Thread {
		
		PythonThread python;
		int millis;
		
		public TimeoutThread(int millis, PythonThread correspond) {
			this.millis = millis;
			this.python = correspond;
		}
		
		@Override
		public void run() {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				// timeout interrupt, doesn't really do anything
				return;
			}
			if (python.py != null && python.py.isAlive()) {
				python.py.destroy();
				python.setError(PythonThread.TIME_LIMIT_EXCEEDED);
			}
		}
	}
	
	static class PythonThread extends Thread {
		
		public static final int TIME_LIMIT_EXCEEDED = 1;
		public static final int OUTPUT_LENGTH_EXCEEDED = 2;
		public static final int FILE_NOT_FOUND = 4;
		
		private static final int MAX_OUTPUT_LEN = 1900;
		private static final int MAX_TIME_LEN = 10000;
		
		public static final String[] errors = {
			"",
			"Operation took longer than 10 minutes. Aborting...",
			"Output was longer than 1900 characters.",
			"Failed to start and execute the process.",
			"File could not be found."
		};
		
		private final String code;
		private int success;
		public StringBuilder out, err;
		public ReturnTextCallback cb;
		public Process py;
		public MessageChannel channel; // discord channel where it was posted
		public Guild guild;
		
		public PythonThread(String code, Guild g, MessageChannel channel, ReturnTextCallback r) {
			super();
			cb = r;
			this.code = code;
			success = -1;
			guild = g;
			this.channel = channel;
			out = new StringBuilder();
			err = new StringBuilder();
			py = null;
			start();
		}
		
		public void setError(int error) {
			if (success == -1) {
				success = error;
				err.append("ERROR: " + errors[error]);
			}
		}

		/**
		 * Runs the Python file at file. Sets error codes and messages according to output.
		 * If the thread fails to join, then the entire program will be killed.
		 * 
		 * error = -1 is reserved for currently running
		 */
		@Override
		public void run() {
			TimeoutThread timeout = new TimeoutThread(PythonThread.MAX_TIME_LEN, this);
			try {
				ProcessBuilder pyb = new ProcessBuilder("python3");
				py = pyb.start();
				timeout.start();
				InputStream stdout = py.getInputStream();
				InputStream stderr = py.getErrorStream();
				BufferedWriter inputWriter = new BufferedWriter(new OutputStreamWriter(py.getOutputStream()));
				inputWriter.write(code);
				inputWriter.flush();
				inputWriter.close();
				
				int in = 0;
				while (out.length() < MAX_OUTPUT_LEN && (in = stdout.read()) != -1) {
					out.append((char) in);
				}
				if (out.length() >= MAX_OUTPUT_LEN) {
					out.append("\n...");
					setError(OUTPUT_LENGTH_EXCEEDED);
				} else {
					while (err.length() < MAX_OUTPUT_LEN && (in = stderr.read()) != -1) {
						err.append((char) in);
					}
					if (err.length() >= MAX_OUTPUT_LEN) err.append("...and some more!");
					int success = py.waitFor();
					System.out.println("> Process returned with error code " + success);
				}
			} catch (IOException e) {
				setError(3);
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				if (timeout.isAlive())
					timeout.interrupt();
				timeout.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("I was interrupted.");
				System.exit(-999);
			}
			cb.callback(this.success, channel, out.toString(), err.toString());
		}
	}
	
	public static int maintainThreads() {
		int available = -1;
		for (int a = 0; a < threads.length; a++) {
			if (threads[a] != null && !threads[a].isAlive()) {
				try {
					threads[a].join();
					threads[a] = null;
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.exit(-999);
				}
			}
			if (available < 0 && threads[a] == null)
				available = a;
		}
		return available;
	}
	
	public static boolean run(String code, int avail, Guild g, MessageChannel channel, ReturnTextCallback callback) {
		PythonThread thread = new PythonThread(code, g, channel, callback);
		threads[avail] = thread;
		return true;
	}
	
}
