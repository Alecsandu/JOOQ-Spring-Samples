package com.dockerino.demo.data;

import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Service
public class DataService {

    private final DSLContext dslContext;

    public DataService(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public String addData(String payload) {
        UUID id = UUID.randomUUID();
        int count = dslContext.insertInto(table("data"), field("id"), field("name"))
                .values(id, payload)
                .execute();

        return id.toString();
    }

    public String getDataById(UUID id) {
        Optional<String> result = Optional.ofNullable(dslContext.select(field("id"), field("name"))
                .from(table("data"))
                .where(field("id").eq(id))
                .fetchOne())
                .map(r -> r.get(field("name")).toString());

        return result.orElseThrow(() -> new NoSuchElementException("No data found for id: " + id));
    }

    public List<String> getData() {
        return dslContext.select(field("id"), field("name"))
                .from(table("data"))
                .fetch()
                .map(record -> record.get("id") + " " + record.get("name"));
    }

}
