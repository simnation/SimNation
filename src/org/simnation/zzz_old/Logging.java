/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable 
 * and used JSimpleSim as technical backbone for concurrent discrete event simulation.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simnation.zzz_old;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.simnation.agents.ch;
import org.simplesim.core.scheduling.Time;

/**
 *
 */
public class Logging {
	

	/*public void log(Level level, Time time, String msg) {
		LOGGER.log(level,time.toString()+getName()+": "+msg);
	}*/

	@Override
	public void log(Time time, String msg) {
		log(Level.INFO,time,msg);
	}

	public static void enableConsoleLogger() throws Exception {
		LOGGER.setUseParentHandlers(false);
		final ConsoleHandler ch=new ConsoleHandler();
		ch
		ch.setFormatter(new Formatter() {
			public String format(LogRecord record) { return record.getMessage()+"\n"; }
		});
		LOGGER.addHandler(ch);
	}
	
	public static void enableFileLogger(String fileName) throws Exception {
		LOGGER.setUseParentHandlers(false);
		final FileHandler fh=new FileHandler(fileName);
		fh.setFormatter(new Formatter() {
			public String format(LogRecord record) { return record.getMessage()+"\n"; }
		});
		LOGGER.addHandler(fh);
	}

	public static void setLogLevel(Level level) {
		LOGGER.setLevel(level);
	}


}
