package com.readonlydev.commands.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.readonlydev.cmd.CommandEvent;


public class ArgumentIndex {

	private List<IArgument<?>> idx;
	private final Pattern multiWord = Pattern.compile("(?>\")\\s*(?:.*?)\\s*(?>\")");
 	
	public ArgumentIndex(CommandEvent event) {
		this.idx = new ArrayList<>();
		buildIndex(event);
	}
	
	private void buildIndex(CommandEvent event) {
		if(event.getArgs().length() > 0) {
			String commandArgs = event.getArgs();
			String removeFromArr = "";
			Matcher matcher = multiWord.matcher(commandArgs);
			if(matcher.find()) {
				removeFromArr = matcher.group(0).replace("'", "");
				commandArgs = commandArgs.replace(matcher.group(0), "");
			}
			String[] argArr = commandArgs.split("\\s+");
			int c = 0;
			for (int i = 0; i < argArr.length; i++) {
				idx.add(i, new Argument(argArr[i]));
				c =+ 1;
			}
			if(removeFromArr.length() > 0) {
				idx.add(c, new Argument(removeFromArr));
			}
		}
	}
	
	public Argument getArg(Integer index) {
		return (Argument) idx.get(index);
	}
	
	public boolean isEmpty() {
		return idx.isEmpty();
	}
	
	public int count() {
		return idx.size();
	}
	
	public List<IArgument<?>> list() {
		return idx;
	}
}
