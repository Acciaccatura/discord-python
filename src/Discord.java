import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Discord extends ListenerAdapter {
	
	public static final String BOT_NAME = "PyBot";
	public static final String TOKEN = "";
	private static final ReturnTextCallback callback = new ReturnTextCallback() {
		
		@Override
		public void callback(int success, MessageChannel channel, String out, String err) {
			if (out.length() > 0)
				returnMessage(channel, "Hi!! Here is the result of your code:\n```\n" + out + "\n```");
			if (err.length() > 0)
				returnMessage(channel, "Your code produced error(s)!:\n```\n" + err + "\n```");
		}};
	
	public static JDA jda;
	
	public static void init() throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException {
		jda = new JDABuilder(AccountType.BOT).setToken(TOKEN).buildBlocking();
		jda.addEventListener(new Discord());
		// authorize
	}
	
	public static void returnMessage(MessageChannel channel, String msg) {
		channel.sendMessage(msg).queue();
	}

	/**
	 * 
	 * @param code - what I'm putting in
	 * @param guild - the guild where the caller is running code
	 * @param channel - the channel where the caller is running code
	 */
	public static void interpret(String code, Guild guild, MessageChannel channel) {
		int available = Python.maintainThreads();
		if (available >= 0) {
			System.out.println("captured: \n" + code);
			Python.run(code, available, guild, channel, callback);
		} else returnMessage(channel, "ERROR: Too many processes are being run! Try again later!");
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String message = event.getMessage().getContent();
		if (message.length() > 6 && message.substring(0, BOT_NAME.length() + 1).equals("@" + BOT_NAME)) {
			int index = message.indexOf("```");
			if (index < 0) return;
			message = message.substring(index);
			Pattern codePattern = Pattern.compile("```py(?:thon)?[\n]?([\n\\s\\S]+)```");
			Matcher codeMatch = codePattern.matcher(message);
			if (codeMatch.find()) {
				String code = codeMatch.group(1) + '\n';
				interpret(code, event.getGuild(), event.getChannel());
			}
		}
	}
	
}
