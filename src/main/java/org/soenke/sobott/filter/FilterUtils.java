package org.soenke.sobott.filter;

import java.util.List;
import java.util.stream.Collectors;

public class FilterUtils {

    public static String wrapWithQuotesAndJoin(List<String> strings) {
        return strings.stream()
                .collect(Collectors.joining("\", \"", "\"", "\""));
    }
}
