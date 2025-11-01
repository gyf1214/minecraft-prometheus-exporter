package com.github.cpburnz.minecraft_prometheus_exporter;

import io.prometheus.client.Collector;
import io.prometheus.client.Counter;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.shsts.tinactory.api.TinactoryKeys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TinactoryCollector extends Collector implements Collector.Describable {
    private static final String PREFIX = TinactoryKeys.ID + "_";

    private final Map<String, Collector> metrics = new HashMap<>();

    public TinactoryCollector() {
        counter("electric_consumed", "Electric Consumed", "team");
        counter("electric_generated", "Electric Generated", "team");
        counter("electric_buffered", "Electric Buffered", "team");
    }

    private void counter(String name, String description, String... labels) {
        var counter = Counter.build()
            .name(PREFIX + name)
            .help(description)
            .labelNames(labels)
            .create();

        metrics.put(name, counter);
    }

    @Override
    public List<MetricFamilySamples> describe() {
        return metrics.values().stream()
            .flatMap($ -> $ instanceof Collector.Describable describable ?
                describable.describe().stream() : Stream.empty())
            .toList();
    }

    @Override
    public List<MetricFamilySamples> collect() {
        return metrics.values().stream()
            .flatMap($ -> $.collect().stream())
            .toList();
    }

    public void report(String name, List<String> labels, double val) {
        if (!metrics.containsKey(name)) {
            return;
        }

        var collector = metrics.get(name);
        if (collector instanceof Counter counter) {
            counter.labels(labels.toArray(String[]::new)).inc(val);
        }
    }
}
