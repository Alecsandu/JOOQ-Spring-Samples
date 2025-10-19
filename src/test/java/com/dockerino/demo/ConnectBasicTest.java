package com.dockerino.demo;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Test;

public class ConnectBasicTest {
    @Test
    public void connectBasic() {
        RedisURI uri = RedisURI.Builder
                .redis("host", 17213/*port*/)
                .withAuthentication("username", "pswd")
                .build();
        RedisClient client = RedisClient.create(uri);
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisCommands<String, String> commands = connection.sync();

        //commands.set("foo", "bar");
        String result = commands.get("db::keyValue");
        System.out.println(result); // >>> bar

        connection.close();

        client.shutdown();
    }
}