package com.lemfi.send;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HealthControllerTest {
    @Test
    void healthzSaysOk() {
        assertEquals("ok", new HealthController().healthz().get("status"));
    }
}
