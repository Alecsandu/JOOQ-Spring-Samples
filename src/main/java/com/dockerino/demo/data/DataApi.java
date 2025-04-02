package com.dockerino.demo.data;

import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@RestController
@RequestMapping("/data")
public class DataApi {

    private final DSLContext dslContext;

    public DataApi(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @GetMapping("/table")
    public String createTable() {
        dslContext.createTableIfNotExists("data")
                .column(field("id", SQLDataType.UUID))
                .column(field("name", SQLDataType.VARCHAR(50)))
                .execute();

        return UUID.randomUUID().toString();
    }

    @GetMapping("/insert")
    public boolean addData() {
        dslContext.insertInto(table("data"), field("name"))
                .values("Mere" + UUID.randomUUID())
                .execute();

        return true;
    }

    @GetMapping("/all")
    public List<String> getData() {
        return dslContext.select(table("data"))
                .stream()
                .map(record -> record.get("id") + " " + record.get("name"))
                .toList();
    }

}
