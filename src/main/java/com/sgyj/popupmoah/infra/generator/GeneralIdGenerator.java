package com.sgyj.popupmoah.infra.generator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GeneralIdGenerator {

    // starts at the year 2010
    private static final long EPOCH_MILLIS = 1262304000000L;
    // left shift amounts
    private static final int TIMESTAMP_SHIFT = 23;
    // exclusive
    private static final int MAX_RANDOM = 0x800000;

    private static final SecureRandom random = new SecureRandom();

    public Object generate() {
        long time = System.currentTimeMillis() - EPOCH_MILLIS;
        return (time << TIMESTAMP_SHIFT) + random.nextInt(MAX_RANDOM);
    }

}

