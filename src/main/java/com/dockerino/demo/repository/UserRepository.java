package com.dockerino.demo.repository;

import com.dockerino.demo.model.User;
import com.dockerino.jooq.generated.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Select;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static com.dockerino.jooq.generated.tables.Users.USERS;

@Repository
public class UserRepository {

    private final DSLContext dsl;

    public UserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private User mapRecordToUser(Record r) {
        if (r == null) {
            return null;
        }

        User user = new User();
        user.setId(r.get(USERS.ID));
        user.setEmail(r.get(USERS.EMAIL));
        user.setPassword(r.get(USERS.PASSWORD));
        user.setUsername(r.get(USERS.EMAIL));
        user.setCreatedAt(r.get(USERS.CREATED_AT).toLocalDateTime());
        user.setUpdatedAt(r.get(USERS.UPDATED_AT).toLocalDateTime());

        return user;
    }

    public Optional<User> findById(UUID id) {
        Record record = dsl.selectFrom(USERS)
                .where(USERS.ID.eq(id))
                .fetchOne();

        return Optional.ofNullable(mapRecordToUser(record));
    }

    public Optional<User> findByEmail(String email) {
        Record record = dsl.selectFrom(USERS)
                .where(USERS.EMAIL.eq(email))
                .fetchOne();

        return Optional.ofNullable(mapRecordToUser(record));
    }

    public Boolean existsByEmail(String email) {
        Select<UsersRecord> usersSelect = dsl.selectFrom(USERS)
                .where(USERS.EMAIL.eq(email));

        return dsl.fetchExists(usersSelect);
    }

    public User save(User user) {
        return dsl.insertInto(USERS)
                .set(USERS.EMAIL, user.getEmail())
                .set(USERS.USERNAME, user.getUsername())
                .set(USERS.PASSWORD, user.getPassword())
                .returning()
                .fetchOne(this::mapRecordToUser);
    }
}
