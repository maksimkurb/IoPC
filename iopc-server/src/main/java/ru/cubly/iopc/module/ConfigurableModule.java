package ru.cubly.iopc.module;

import java.util.Map;

public interface ConfigurableModule<T> extends Module {
    T getConfigFragmentModel();

    Map<String, String> buildConfigProperties(T model);
}
