package com.readonlydev.core;

import java.util.regex.Pattern;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CrashReportHandler {
	
	private static CrashReportHandler _instance;
	
	private final static String DATE = "(19|20)[0-9]{2}[- \\/.](0[1-9]|1[012])[- \\/.](0[1-9]|[12][0-9]|3[01])";
	private final static String TIME = "(?:[01]\\d|2[0123]).(?:[012345]\\d).(?:[012345]\\d)";

	private final static String TIMEDATE = DATE + "_" + TIME;
	
	private final static String REPORT_PREFIX = "crash-";
	private final static String REPORT_SUFFIX = "-(client|server).(txt)|(message).(txt)";

	public static final Pattern CRASHLOG = create(group(REPORT_PREFIX + TIMEDATE + REPORT_SUFFIX));
	public static final Pattern LOGS_TXT = create("(latest|message).(txt|log)");
	
	public static CrashReportHandler instance() {
		if(_instance == null) {
			_instance = new CrashReportHandler();
		}
		return _instance;
	}
	
	public void handleMessageAttachments(MessageReceivedEvent event) {
		event.getMessage().getAttachments().forEach(attachment -> {
			if(matchesExtensions(attachment.getFileExtension())) {
				
			}
		});
	}
	
	private final static Pattern create(String regex) {
		return Pattern.compile(regex);
	}
	
	private final static String group(String regex) {
		return "(" + regex + ")";
	}

	private boolean matchesExtensions(String extension) {
		return extension.matches("txt|log");
	}
}
