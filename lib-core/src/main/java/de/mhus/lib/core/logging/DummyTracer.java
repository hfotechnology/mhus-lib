package de.mhus.lib.core.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.mhus.lib.core.MProperties;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;

public class DummyTracer implements ITracer {
	
	@Override
	public Scope start(String spanName, boolean active, String ... tagPairs) {
		return enter(spanName, tagPairs);
	}

	@Override
	public Scope enter(String spanName, String ... tagPairs ) {
		Span span = new DummySpan(spanName);
		for (int i = 0; i < tagPairs.length-1; i=i+2)
			span.setTag(tagPairs[i], tagPairs[i+1]);
		return new DummyScope(span);
	}

	private static class DummySpan implements Span {

		private String name;
		private DummyContext context;
		private MProperties tags;
		
		public DummySpan(String spanName) {
			name = spanName;
			context = new DummyContext();
		}

		@Override
		public SpanContext context() {
			return context;
		}

		@Override
		public Span setTag(String key, String value) {
			getTags().setString(key, value);
			return this;
		}

		private MProperties getTags() {
			if (tags == null)
				tags = new MProperties();
			return tags;
		}

		@Override
		public Span setTag(String key, boolean value) {
			getTags().put(key, value);
			return this;
		}

		@Override
		public Span setTag(String key, Number value) {
			getTags().put(key, value);
			return this;
		}

		@Override
		public Span log(Map<String, ?> fields) {
			return this;
		}

		@Override
		public Span log(long timestampMicroseconds, Map<String, ?> fields) {
			return this;
		}

		@Override
		public Span log(String event) {
			return this;
		}

		@Override
		public Span log(long timestampMicroseconds, String event) {
			return this;
		}

		@Override
		public Span setBaggageItem(String key, String value) {
			context.getBaggage().put(key, value);
			return this;
		}

		@Override
		public String getBaggageItem(String key) {
			return context.getBaggage().get(key);
		}

		@Override
		public Span setOperationName(String operationName) {
			name = operationName;
			return this;
		}

		@Override
		public void finish() {
			
		}

		@Override
		public void finish(long finishMicros) {
			
		}
		
		public String toString() {
			return name + (tags == null ? "" : tags);
		}
		
	}
	
	private static class DummyContext implements SpanContext {
		
		private HashMap<String,String> baggage = null;
		
		@Override
		public Iterable<Entry<String, String>> baggageItems() {
			return getBaggage().entrySet();
		}

		public synchronized HashMap<String, String> getBaggage() {
			if (baggage == null)
				baggage = new HashMap<>();
			return baggage;
		}
		
	}
	
	private static class DummyScope implements Scope {

		private Span span;

		public DummyScope(Span span) {
			this.span = span;
		}

		@Override
		public void close() {
			
		}

		@Override
		public Span span() {
			return span;
		}
		
	}
}