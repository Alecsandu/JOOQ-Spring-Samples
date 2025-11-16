package com.dockerino.demo;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Disabled("Used to verify localhost connection")
public class RedisConnectBasicTest {

    @Test
    void redisConnectBasic() {
        RedisURI uri = RedisURI.Builder
                .redis("localhost", 6379)
                .withAuthentication("redis", "redis")
                .build();
        RedisClient client = RedisClient.create(uri);
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisCommands<String, String> commands = connection.sync();

        commands.set("foo", "bar");
        String result = commands.get("foo");
        assertEquals("bar", result);

        Long rowsDel = commands.del("foo");
        assertEquals(1L, rowsDel);

        String nullResult = commands.get("foo");
        assertNull(nullResult);

        connection.close();
        client.shutdown();
    }

}