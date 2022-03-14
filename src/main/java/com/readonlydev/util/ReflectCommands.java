package com.readonlydev.util;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import com.google.common.collect.Sets;
import com.readonlydev.annotation.GalacticCommand;
import com.readonlydev.cmd.BotCommand;
import com.readonlydev.core.Accessors;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReflectCommands {

	private static Reflections reflections;

	private static final HashSet<Class<?>> getCommandClasses() {
		return Sets.newHashSet(reflections.getTypesAnnotatedWith(GalacticCommand.class));
	}

	public static final Set<BotCommand> commands() {
		return getCommandClasses().stream().map(c -> toInstance(c)).collect(Collectors.toSet());
	}

	private static final BotCommand toInstance(Class<?> clazz) {
		try {
			return (BotCommand) clazz.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	static {
		reflections = new Reflections(Accessors.botInfo().getCommandPackages(), Scanners.values());
	}
}
