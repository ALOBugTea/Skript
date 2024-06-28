package org.skriptlang.skript.registration;

import ch.njol.skript.lang.SyntaxElement;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.skriptlang.skript.util.ClassUtils;
import org.skriptlang.skript.util.Priority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

class SyntaxInfoImpl<T extends SyntaxElement> implements SyntaxInfo<T> {

	private final SyntaxOrigin origin;
	private final Class<T> type;
	@Nullable
	private final Supplier<T> supplier;
	private final Collection<String> patterns;
	private final Priority priority;

	protected SyntaxInfoImpl(
		SyntaxOrigin origin, Class<T> type, @Nullable Supplier<T> supplier,
		Collection<String> patterns, Priority priority
	) {
		if (supplier == null && !ClassUtils.isNormalClass(type)) {
			throw new IllegalArgumentException(
				"Failed to register a syntax info for '" + type.getName() + "'."
					+ " Element classes must be a normal type unless a supplier is provided."
			);
		}
		if (patterns.isEmpty()) {
			throw new IllegalArgumentException(
				"Failed to register a syntax info for '" + type.getName() + "'."
					+ " There must be at least one pattern."
			);
		}
		this.origin = origin;
		this.type = type;
		this.supplier = supplier;
		this.patterns = ImmutableList.copyOf(patterns);
		this.priority = priority;
	}

	@Override
	public SyntaxOrigin origin() {
		return origin;
	}

	@Override
	public Class<T> type() {
		return type;
	}

	@Override
	public T instance() {
		try {
			return supplier == null ? type.getDeclaredConstructor().newInstance() : supplier.get();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@Unmodifiable
	public Collection<String> patterns() {
		return patterns;
	}

	@Override
	public Priority priority() {
		return priority;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SyntaxInfo)) {
			return false;
		}
		SyntaxInfo<?> info = (SyntaxInfo<?>) other;
		return Objects.equals(origin(), info.origin()) &&
				Objects.equals(type(), info.type()) &&
				Objects.equals(patterns(), info.patterns());
	}

	@Override
	public int hashCode() {
		return Objects.hash(origin(), type(), patterns());
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("origin", origin())
				.add("type", type())
				.add("patterns", patterns())
				.toString();
	}

	@SuppressWarnings("unchecked")
	static class BuilderImpl<B extends Builder<B, E>, E extends SyntaxElement> implements Builder<B, E> {

		/**
		 * A default origin that describes the class of a syntax.
		 */
		private static final class ClassOrigin implements SyntaxOrigin {

			private final String name;

			ClassOrigin(Class<?> clazz) {
				this.name = clazz.getName();
			}

			@Override
			public String name() {
				return name;
			}

		}

		final Class<E> type;
		SyntaxOrigin origin;
		@Nullable
		Supplier<E> supplier;
		final List<String> patterns = new ArrayList<>();
		Priority priority = SyntaxInfo.COMBINED;

		BuilderImpl(Class<E> type) {
			this.type = type;
			origin = new ClassOrigin(type);
		}

		public B origin(SyntaxOrigin origin) {
			this.origin = origin;
			return (B) this;
		}

		public B supplier(Supplier<E> supplier) {
			this.supplier = supplier;
			return (B) this;
		}

		public B addPattern(String pattern) {
			this.patterns.add(pattern);
			return (B) this;
		}

		public B addPatterns(String... patterns) {
			Collections.addAll(this.patterns, patterns);
			return (B) this;
		}

		public B addPatterns(Collection<String> patterns) {
			this.patterns.addAll(patterns);
			return (B) this;
		}

		@Override
		public B priority(Priority priority) {
			this.priority = priority;
			return (B) this;
		}

		public SyntaxInfo<E> build() {
			return new SyntaxInfoImpl<>(origin, type, supplier, patterns, priority);
		}

	}

}
