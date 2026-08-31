package com.github.cpburnz.minecraft_prometheus_exporter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.shsts.tinactory.api.TinactoryKeys;

import io.prometheus.client.Collector;
import io.prometheus.client.Counter;

/**
 * The TinactoryCollector class exports electricity metrics reported by
 * Tinactory.
 */
public class TinactoryCollector extends Collector implements Collector.Describable {

	private static final String PREFIX = TinactoryKeys.ID + "_";

	private final Map<String, Collector> metrics = new HashMap<>();

	/**
	 * Construct the collector.
	 */
	public TinactoryCollector() {
		this.counter("electric_consumed", "Electric Consumed", "team");
		this.counter("electric_generated", "Electric Generated", "team");
		this.counter("electric_buffer_charged", "Electric Buffer Charged", "team");
		this.counter("electric_buffer_discharged", "Electric Buffer Discharged", "team");
	}

	private void counter(String name, String description, String... labels) {
		Counter counter = Counter.build()
			.name(PREFIX + name)
			.help(description)
			.labelNames(labels)
			.create();

		this.metrics.put(name, counter);
	}

	@Override
	public List<MetricFamilySamples> describe() {
		return this.metrics.values().stream()
			.flatMap(metric -> metric instanceof Collector.Describable describable
				? describable.describe().stream()
				: Stream.empty())
			.toList();
	}

	@Override
	public List<MetricFamilySamples> collect() {
		return this.metrics.values().stream()
			.flatMap(metric -> metric.collect().stream())
			.toList();
	}

	/**
	 * Record a metric reported by Tinactory.
	 *
	 * @param name The Tinactory metric name.
	 * @param labels The metric label values.
	 * @param value The reported value.
	 */
	public void report(String name, List<String> labels, double value) {
		Collector collector = this.metrics.get(name);
		if (collector instanceof Counter counter) {
			counter.labels(labels.toArray(String[]::new)).inc(value);
		}
	}
}
