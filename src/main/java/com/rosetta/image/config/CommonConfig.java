package com.rosetta.image.config;

import org.opencv.core.Core;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: nya
 * @Date: 18-8-29 上午10:34
 */
@Configuration
public class CommonConfig {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

}
