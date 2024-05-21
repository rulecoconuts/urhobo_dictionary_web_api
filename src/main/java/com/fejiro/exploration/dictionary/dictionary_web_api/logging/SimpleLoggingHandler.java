package com.fejiro.exploration.dictionary.dictionary_web_api.logging;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleLoggingHandler implements ObservationHandler<Observation.Context> {
    private static final Logger logger = LoggerFactory.getLogger(SimpleLoggingHandler.class);

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }

    @Override
    public void onStart(Observation.Context context) {
        context.put("time", System.currentTimeMillis());
//        ObservationHandler.super.onStart(context);
    }

    @Override
    public void onError(Observation.Context context) {
        logger.info("Failed running {}", context.getName());
        ObservationHandler.super.onError(context);
    }

    @Override
    public void onScopeOpened(Observation.Context context) {
        ObservationHandler.super.onScopeOpened(context);
    }

    @Override
    public void onScopeClosed(Observation.Context context) {
        ObservationHandler.super.onScopeClosed(context);
    }

    @Override
    public void onStop(Observation.Context context) {
        long duration = System.currentTimeMillis() - context.getOrDefault("time", 0L);
        logger.info("Finished running {} in {} ms", context.getName(), duration);
    }
}
