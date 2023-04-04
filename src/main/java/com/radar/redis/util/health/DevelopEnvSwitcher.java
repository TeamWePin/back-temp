package com.radar.redis.util.health;

import lombok.Getter;
import lombok.Setter;

public final class DevelopEnvSwitcher {
    @Getter @Setter private static boolean developEnv = true;
}
