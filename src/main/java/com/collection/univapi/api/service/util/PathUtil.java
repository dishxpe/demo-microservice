package com.collection.univapi.api.service.util;

import java.nio.file.Path;

public class PathUtil {

    public static String normalize(Path path) {
        return path.toString().replace("\\", "/");
    }
}
