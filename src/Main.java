import javax.security.auth.callback.Callback;
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

/**
 * Runs Python 3 code for Discord!
 * @author willi
 *
 */

interface ReturnTextCallback extends Callback {
	void callback(int success, MessageChannel channel, String out, String err);
}

public class Main {

	public static void main(String[] args) {
		try {
			Discord.init();
		} catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
