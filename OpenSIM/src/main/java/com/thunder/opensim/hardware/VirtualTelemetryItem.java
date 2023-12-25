package com.thunder.opensim.hardware;

import androidx.annotation.Nullable;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class VirtualTelemetryItem implements Telemetry.Item {

    private String caption, format;
    private Object value;
    private Func<>

    @Override
    public String getCaption() {
        return null;
    }

    @Override
    public Telemetry.Item setCaption(String caption) {
        return null;
    }

    @Override
    public Telemetry.Item setValue(String format, Object... args) {
        return null;
    }

    @Override
    public Telemetry.Item setValue(Object value) {
        return null;
    }

    @Override
    public <T> Telemetry.Item setValue(Func<T> valueProducer) {
        return null;
    }

    @Override
    public <T> Telemetry.Item setValue(String format, Func<T> valueProducer) {
        return null;
    }

    @Override
    public Telemetry.Item setRetained(@Nullable Boolean retained) {
        return null;
    }

    @Override
    public boolean isRetained() {
        return false;
    }

    @Override
    public Telemetry.Item addData(String caption, String format, Object... args) {
        return null;
    }

    @Override
    public Telemetry.Item addData(String caption, Object value) {
        return null;
    }

    @Override
    public <T> Telemetry.Item addData(String caption, Func<T> valueProducer) {
        return null;
    }

    @Override
    public <T> Telemetry.Item addData(String caption, String format, Func<T> valueProducer) {
        return null;
    }

    private static class Value<T> {
        private Object[] value;
        private Func<T> producer;
        private String format;

        public Value() {

        }

        public String getValue() {
            if (producer == null) return String.format(format, value.toString());
            else return producer.value().toString();
        }
    }
}
