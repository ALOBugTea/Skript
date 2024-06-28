package org.skriptlang.skript.addon;

import org.jetbrains.annotations.ApiStatus;
import org.skriptlang.skript.Skript;
import org.skriptlang.skript.registration.SyntaxRegistry;

/**
 * A module is a component of a {@link SkriptAddon} used for registering syntax and other {@link Skript} components.
 */
@ApiStatus.Experimental
@FunctionalInterface
public interface AddonModule {

	/**
	 *
	 * @param addon The addon this module belongs to.
	 */
	void load(SkriptAddon addon);

}
