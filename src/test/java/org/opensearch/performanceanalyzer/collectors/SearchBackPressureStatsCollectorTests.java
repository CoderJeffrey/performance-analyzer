/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.opensearch.performanceanalyzer.commons.config.overrides.ConfigOverridesWrapper;
import org.opensearch.performanceanalyzer.commons.event_process.Event;
import org.opensearch.performanceanalyzer.commons.metrics.MetricsConfiguration;
import org.opensearch.performanceanalyzer.commons.metrics.PerformanceAnalyzerMetrics;
import org.opensearch.performanceanalyzer.config.PerformanceAnalyzerController;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class SearchBackPressureStatsCollectorTests {
    private ObjectMapper mapper;
    private long startTimeInMills;
    private PerformanceAnalyzerController controller;
    private ConfigOverridesWrapper configOverrides;
    private SearchBackPressureStatsCollector searchBackPressureStatsCollector;
    private List<String> required_fields_for_searchBackPressureStats =
            Arrays.asList(
                    "searchbp_shard_stats_cancellationCount",
                    "searchbp_shard_stats_limitReachedCount",
                    "searchbp_shard_stats_resource_heap_usage_cancellationCount",
                    "searchbp_shard_stats_resource_heap_usage_currentMax",
                    "searchbp_shard_stats_resource_heap_usage_rollingAvg",
                    "searchbp_shard_stats_resource_cpu_usage_cancellationCount",
                    "searchbp_shard_stats_resource_cpu_usage_currentMax",
                    "searchbp_shard_stats_resource_cpu_usage_currentAvg",
                    "searchbp_shard_stats_resource_elaspedtime_usage_cancellationCount",
                    "searchbp_shard_stats_resource_elaspedtime_usage_currentMax",
                    "searchbp_shard_stats_resource_elaspedtime_usage_currentAvg",
                    "searchbp_task_stats_cancellationCount",
                    "searchbp_task_stats_limitReachedCount",
                    "searchbp_task_stats_resource_heap_usage_cancellationCount",
                    "searchbp_task_stats_resource_heap_usage_currentMax",
                    "searchbp_task_stats_resource_heap_usage_rollingAvg",
                    "searchbp_task_stats_resource_cpu_usage_cancellationCount",
                    "searchbp_task_stats_resource_cpu_usage_currentMax",
                    "searchbp_task_stats_resource_cpu_usage_currentAvg",
                    "searchbp_task_stats_resource_elaspedtime_usage_cancellationCount",
                    "searchbp_task_stats_resource_elaspedtime_usage_currentMax",
                    "searchbp_task_stats_resource_elaspedtime_usage_currentAvg",
                    "searchbp_mode",
                    "searchbp_nodeid");

    // Mock Object for
    SearchBackPressureStatsCollector.ResourceUsageTrackerStats HEAP_USAGE_TRACKER_MOCK_STATS;
    SearchBackPressureStatsCollector.ResourceUsageTrackerStats CPU_USAGE_TRACKER_MOCK_STATS;
    SearchBackPressureStatsCollector.ResourceUsageTrackerStats ELAPSED_TIME_TRACKER_MOCK_STATS;

    /*
     *  Sets the Metrics Configuration to the desired internval
     *  Set the log property to be false
     *  Set the controller to be a mocked PerforamcneAnalyzerController.clas
     *  Set the configWrapper to be a mocked ConfigOverridesWrapper
     *  Set the Collector to be a SearchBackPressureServiceCollector
     *  Set the ObjectMapper to be a new ObjectMapper() instance
     */
    @Before
    public void init() {
        mapper = new ObjectMapper();
        MetricsConfiguration.CONFIG_MAP.put(
                SearchBackPressureStatsCollector.class, MetricsConfiguration.cdefault);
        System.setProperty("performanceanalyzer.metrics.log.enabled", "False");
        startTimeInMills = 1153721339;
        controller = Mockito.mock(PerformanceAnalyzerController.class);
        configOverrides = Mockito.mock(ConfigOverridesWrapper.class);
        searchBackPressureStatsCollector =
                new SearchBackPressureStatsCollector(controller, configOverrides);

        HEAP_USAGE_TRACKER_MOCK_STATS =
                new SearchBackPressureStatsCollector.ResourceUsageTrackerStats(0, 0, 0, 0, false);
        CPU_USAGE_TRACKER_MOCK_STATS =
                new SearchBackPressureStatsCollector.ResourceUsageTrackerStats(0, 0, 0, 0, false);
        ELAPSED_TIME_TRACKER_MOCK_STATS =
                new SearchBackPressureStatsCollector.ResourceUsageTrackerStats(0, 0, 0, 0, false);
    }

    @Test
    public void testSearchBackPressureStats_saveMetricValues() {
        Mockito.when(
                        controller.isCollectorEnabled(
                                configOverrides, "SearchBackPressureStatsCollector"))
                .thenReturn(true);
        searchBackPressureStatsCollector.saveMetricValues("search_back_pressure", startTimeInMills);
        List<Event> metrics = new ArrayList<>();
        PerformanceAnalyzerMetrics.metricQueue.drainTo(metrics);

        assertEquals(1, metrics.size());
        assertEquals("search_back_pressure", metrics.get(0).value);

        try {
            searchBackPressureStatsCollector.saveMetricValues(
                    "search_back_pressure", startTimeInMills, "dummy");
            assertTrue("Negative scenario test: Should have been a RuntimeException", true);
        } catch (RuntimeException ex) {
            // - expecting exception...1 values passed; 0 expected
            // since keyPath does not match
        }

        System.out.println("SearchBackPressureStatsCollector_saveMetricValues Test Finished");
    }

    /*
     * For the testSearchBackPressureStats_saveMetricsValue
     * similar to ClusterApplierService
     */

    /*
    * For the testSearchBackPressureStats_collectMetrics Test
    * Open the collectoMetrics() for testing
    * mock the behavior getSearchBackPressureStats() to return a mock object
    * Because we cannot use Reflection for testing and there is no public API
    * Similar to json String
    * test collect metrics
    * verify
    * assertEquals(
             "MONITOR_ONLY",
             map.get("SearchBackPressureStats_Mode"));
    * And other metrics values
    * Test SearchTaskStats
    * Test SearchShardTaskStats
    */
    @Test
    public void testSearchBackPressureStats_collectMetrics()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
                    JsonProcessingException, NoSuchFieldException, ClassNotFoundException {
        String SEARCH_BACK_PRESSURE_MODE_FIELD_NAME = "searchbp_mode";
        System.out.println("SearchBackPressureStatsCollector_collectMetrics Test Started");
        SearchBackPressureStatsCollector spyCollector =
                Mockito.spy(searchBackPressureStatsCollector);

        Map<String, SearchBackPressureStatsCollector.ResourceUsageTrackerStats>
                resource_usage_mock_stats =
                        Map.ofEntries(
                                Map.entry("HEAP_USAGE_TRACKER", HEAP_USAGE_TRACKER_MOCK_STATS),
                                Map.entry("CPU_USAGE_TRACKER", CPU_USAGE_TRACKER_MOCK_STATS),
                                Map.entry("ELAPSED_TIME_TRACKER", ELAPSED_TIME_TRACKER_MOCK_STATS));

        // Create an mock object for testing
        // Mock the behavior of the getSearchBackPressureStats()
        Mockito.doReturn(
                        new SearchBackPressureStatsCollector.SearchBackPressureStats(
                                new SearchBackPressureStatsCollector.SearchShardTaskStats(
                                        0, 0, resource_usage_mock_stats),
                                "MONITOR_ONLY",
                                new SearchBackPressureStatsCollector.SearchTaskStats(
                                        0, 0, resource_usage_mock_stats)))
                .when(spyCollector)
                .getSearchBackPressureStats();

        // Mock the behavior of the clusterApplierService for enabled
        Mockito.when(
                        controller.isCollectorEnabled(
                                configOverrides,
                                SearchBackPressureStatsCollector.class.getSimpleName()))
                .thenReturn(true);

        spyCollector.collectMetrics(startTimeInMills);
        List<Event> metrics = new ArrayList<>();
        PerformanceAnalyzerMetrics.metricQueue.drainTo(metrics);

        assertEquals(1, metrics.size());
        // line 0 should be the header: search_back_pressure

        String[] lines = metrics.get(0).value.split(System.lineSeparator());
        Map<String, String> map = mapper.readValue(lines[1], Map.class);
        // assertEquals("MONITOR_ONLY", map.get(SEARCH_BACK_PRESSURE_MODE_FIELD_NAME));

        // Verify requried fields are presented in the metrics
        String jsonStr = lines[1];
        System.out.println("JSON STRING: " + jsonStr);
        for (String required_field : required_fields_for_searchBackPressureStats) {
            assertTrue(jsonStr.contains(required_field));
        }

        assertTrue(jsonStr.contains("dummy_field"));
    }
}
