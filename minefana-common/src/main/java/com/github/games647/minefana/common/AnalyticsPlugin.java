package com.github.games647.minefana.common;

import java.nio.file.Path;

import org.slf4j.Logger;

public interface AnalyticsPlugin {

    void registerEvents();

    void registerTasks();

    Path getPluginFolder();

    Logger getLog();

    AnalyticsCore getCore();
}
