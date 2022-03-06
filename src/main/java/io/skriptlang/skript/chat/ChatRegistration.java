/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package io.skriptlang.skript.chat;

import ch.njol.skript.SkriptAddon;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.Converters;
import io.skriptlang.skript.chat.util.ComponentHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.Tag.Argument;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static io.skriptlang.skript.chat.util.ComponentHandler.registerPlaceholder;

public class ChatRegistration {

	public void register(SkriptAddon addon) {

		try {
			addon.loadClasses("io.skriptlang.skript.chat.elements");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Converters.registerConverter(String.class, Component.class, ComponentHandler::parse);
		Converters.registerConverter(Component.class, String.class, ComponentHandler::toLegacyString);

		Classes.registerClass(new ClassInfo<>(Component.class, "component")
			.user("components?")
			.name("Component")
			.since("INSERT VERSION")
			.parser(new Parser<Component>() {
				@Override
				public boolean canParse(ParseContext context) {
					return false;
				}

				@Override
				public String toString(Component component, int flags) {
					return ComponentHandler.toLegacyString(component);
				}

				@Override
				public String toVariableNameString(Component component) {
					return "component:" + component;
				}
			})
		);

		// Just to initialize it now
		ComponentHandler.getAdventure();

		registerPlaceholder("dark_cyan", "<dark_aqua>");
		registerPlaceholder("dark_turquoise", "<dark_aqua>");
		registerPlaceholder("cyan", "<dark_aqua>");

		registerPlaceholder("purple", "<dark_purple>");

		registerPlaceholder("dark_yellow", "<gold>");
		registerPlaceholder("orange", "<gold>");

		registerPlaceholder("light_grey", "<grey>");
		registerPlaceholder("light_gray", "<grey>");
		registerPlaceholder("silver", "<grey>");

		registerPlaceholder("dark_silver", "<dark_grey>");

		registerPlaceholder("light_blue", "<blue>");
		registerPlaceholder("indigo", "<blue>");

		registerPlaceholder("light_green", "<green>");
		registerPlaceholder("lime_green", "<green>");
		registerPlaceholder("lime", "<green>");

		registerPlaceholder("light_cyan", "<aqua>");
		registerPlaceholder("light_aqua", "<aqua>");
		registerPlaceholder("turquoise", "<aqua>");

		registerPlaceholder("light_red", "<red>");


		registerPlaceholder("pink", "<light_purple>");
		registerPlaceholder("magenta", "<light_purple>");

		registerPlaceholder("light_yellow", "<yellow>");

		registerPlaceholder("underline", "<underlined>");

		ComponentHandler.registerResolver(TagResolver.resolver("unicode", (argumentQueue, context) -> {
			String unicode = argumentQueue.popOr("A unicode tag must have an argument of the unicode").value();
			return Tag.selfClosingInserting(Component.text(StringEscapeUtils.unescapeJava("\\" + unicode)));
		}));

	}

}
