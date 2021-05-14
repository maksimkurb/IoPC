package ru.cubly.iopc.util.conditions;

import org.apache.commons.lang3.SystemUtils;
import org.apache.tomcat.jni.OS;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;
import ru.cubly.iopc.util.PlatformType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class OnPlatformCondition implements Condition {

    public static final String ANNOTATION_VALUE = "value";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(ConditionalOnPlatform.class.getName());
        if (attributes == null || attributes.isEmpty() || !attributes.containsKey(ANNOTATION_VALUE) || attributes.get(ANNOTATION_VALUE).size() == 0)
            throw new IllegalArgumentException("@ConditionalOnPlatform annotation requires at least one platform specified");

        List<Object> platforms = attributes.get(ANNOTATION_VALUE);

        return platforms
                .stream()
                .flatMap(obj -> {
                    if (obj instanceof PlatformType[]) {
                        return Arrays.stream((PlatformType[])obj);
                    }
                    return Stream.of(obj);
                })
                .anyMatch(this::platformMatches);
    }

    private boolean platformMatches(Object value) {
        PlatformType platform = (PlatformType) value;

        switch (platform) {
            case Windows:
                return SystemUtils.IS_OS_WINDOWS;
            case Linux:
                return SystemUtils.IS_OS_LINUX;
            case MacOS:
                return SystemUtils.IS_OS_MAC_OSX;
            default:
                return false;
        }
    }
}
