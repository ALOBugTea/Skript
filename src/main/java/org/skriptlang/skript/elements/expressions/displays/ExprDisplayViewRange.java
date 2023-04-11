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
package org.skriptlang.skript.elements.expressions.displays;

import org.bukkit.entity.Display;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

@Name("Display View Range")
@Description({
	"Returns or changes the view range of <a href='classes.html#display'>displays</a>.",
	"Default value is 1.0."
})
@Examples("set view range of the last spawned text display to 2.7")
@Since("INSERT VERSION")
public class ExprDisplayViewRange extends SimplePropertyExpression<Display, Float> {

	static {
		if (Skript.isRunningMinecraft(1, 19, 4))
			register(ExprDisplayViewRange.class, Float.class, "view (range|radius)", "displays");
	}

	@Override
	@Nullable
	public Float convert(Display display) {
		return display.getViewRange();
	}

	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		return CollectionUtils.array(Number.class);
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		Display[] displays = getExpr().getArray(event);
		float change = delta == null ? 0F : (int) ((Number) delta[0]).floatValue();
		change = Math.max(0F, change);
		switch (mode) {
			case REMOVE_ALL:
			case REMOVE:
				change = -change;
			case ADD:
				for (Display display : displays) {
					float value = Math.max(0F, display.getViewRange() + change);
					display.setViewRange(value);
				}
				break;
			case DELETE:
			case RESET:
				for (Display display : displays)
					display.setViewRange(1.0F);
				break;
			case SET:
				for (Display display : displays)
					display.setViewRange(change);
				break;
		}
	}

	@Override
	public Class<? extends Float> getReturnType() {
		return Float.class;
	}

	@Override
	protected String getPropertyName() {
		return  "view range";
	}

}
