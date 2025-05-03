package com.alexcostea.secretsvault.utils.encryption.registry;

import com.alexcostea.secretsvault.utils.encryption.strategies.EncryptionStrategy;
import com.alexcostea.secretsvault.utils.encryption.strategies.AESEncryptionStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EncryptionStrategyRegistry {
    private static final Map<String, Function<String, EncryptionStrategy>> registry = new HashMap<>();

    static {
        registry.put("AES", AESEncryptionStrategy::new);
    }

    public static Map<String, Function<String, EncryptionStrategy>> getRegistry() {
        return registry;
    }

    public static String getDefaultStrategyId() {
        return "AES";
    }
}