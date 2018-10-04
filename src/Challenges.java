public class Challenges {

	/*
	private static final ReturnTextCallback setTests = new ReturnTextCallback() {
		@Override
		public void callback(int success, int avail, String out, String err) {
			output = out;
		}
	};
	
	private static final ReturnTextCallback callback = new ReturnTextCallback() {
		@Override
		public void callback(int success, int avail, String out, String err) {
			if (checkAgainstProblem(out)) {
				clean(); // going to need to secure this!
			}
		}
	};
	
	private static final FileFilter probset = new FileFilter() {

		@Override
		public boolean accept(File pathname) {
			return !pathname.getName().contains("test.py");
		}
		
	};
	private static final File folder = new File("py/challenges");
	private static final File[] challenges = folder.listFiles(probset);	
	
	private static File testCases = null;
	private static int currentChallenge = -1;
	private static String desc = "";
	private static String output = null;
	
	public static int getRandomChallenge() {
		if (currentChallenge == -1) {
			try {
				currentChallenge = (int) (Math.random()*(challenges.length));
				Scanner in = new Scanner(challenges[currentChallenge]);
				StringBuilder desc = new StringBuilder();
				String next;
				while (in.hasNextLine()) {
					//docstring
					if ((next = in.nextLine()).equals("\"\"\"")) {
						while (in.hasNextLine() && !(next = in.nextLine()).equals("\"\"\"")) {
							desc.append(next);
						}
						break;
					}
				}
				Challenges.desc = desc.toString();
				testCases = new File(challenges[currentChallenge].getPath().replace(".py", "_test.py"));
				if (!testCases.exists()) throw new FileNotFoundException("Couldn't find the test case file.");
				Python.runIgnoreThreads(testCases.getPath(), callback);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				currentChallenge = -1;
			}
		}
		return currentChallenge;
	}
	
	public static void clean() {
		currentChallenge = -1;
		testCases = null;
		desc = "";
		output = null;
	}
	
	public static String getChallengeDescription() {
		return desc;
	}
	
	public static boolean checkAgainstProblem(String stdout) {
		if (output == null) {
			return false;
		} else return stdout.equals(output);
	}
	*/
}
