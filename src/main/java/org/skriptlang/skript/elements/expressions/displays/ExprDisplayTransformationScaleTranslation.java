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
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

@Name("Display Transformation Scale/Translation")
@Description("Returns or changes the transformation scale or translation of <a href='classes.html#display'>displays</a>.")
@Examples("set left transformation translation of last spawned text display to the angle between vector 2, 0, 0 and vector 0, 2, 1")
@Since("INSERT VERSION")
public class ExprDisplayTransformationScaleTranslation extends SimplePropertyExpression<Display, Vector> {

	static {
		if (Skript.isRunningMinecraft(1, 19, 4))
			register(ExprDisplayTransformationScaleTranslation.class, Vector.class, "transformation (:scale|translation)", "displays");
	}

	private boolean scale;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		scale = parseResult.hasTag("scale");
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	@Nullable
	public Vector convert(Display display) {
		Transformation transformation = display.getTransformation();
		return toBukkitVector(scale ? transformation.getScale() : transformation.getTranslation());
	}

	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(Vector.class);
		return null;
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		Display[] displays = getExpr().getArray(event);
		if (delta == null)
			return;
		Vector3f vector = toVector((Vector) delta[0]);
		for (Display display : displays) {
			Transformation transformation = display.getTransformation();
			Transformation change;
			if (scale) {
				change = new Transformation(transformation.getTranslation(), transformation.getLeftRotation(), vector, transformation.getRightRotation());
			} else {
				change = new Transformation(vector, transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation());
			}
			display.setTransformation(change);
		}
	}

	private Vector toBukkitVector(Vector3f vector) {
		return new Vector(vector.x, vector.y, vector.z);
	}

	private Vector3f toVector(Vector vector) {
		return new Vector3f((float)vector.getX(), (float)vector.getY(), (float)vector.getZ());
	}

	@Override
	public Class<? extends Vector> getReturnType() {
		return Vector.class;
	}

	@Override
	protected String getPropertyName() {
		return "transformation " + (scale ? "scale" : "translation");
	}

}
