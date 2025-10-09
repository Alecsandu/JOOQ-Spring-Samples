package com.dockerino.demo.repository;

import com.dockerino.demo.exception.UserNotFoundException;
import com.dockerino.demo.model.User;
import com.dockerino.demo.model.dtos.RegisterUserRequest;
import com.dockerino.jooq.generated.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Select;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.dockerino.jooq.generated.tables.Users.USERS;

@Repository
public class UserRepository {

    private final DSLContext dsl;
    private final PasswordEncoder passwordEncoder;

    public UserRepository(DSLContext dsl, PasswordEncoder passwordEncoder) {
        this.dsl = dsl;
        this.passwordEncoder = passwordEncoder;
    }

    private User mapRecordToUser(Record r) {
        if (r == null) {
            throw new UserNotFoundException();
        }

        return new User(
                r.get(USERS.ID, UUID.class),
                r.get(USERS.EMAIL, String.class),
                r.get(USERS.PASSWORD, String.class),
                r.get(USERS.USERNAME, String.class),
                r.get(USERS.CREATED_AT, LocalDateTime.class),
                r.get(USERS.UPDATED_AT, LocalDateTime.class)
        );
    }

    public User findById(UUID id) {
        Record record = dsl.selectFrom(USERS)
                .where(USERS.ID.eq(id))
                .fetchOne();

        return mapRecordToUser(record);
    }

    public User findByEmail(String email) {
        Record record = dsl.selectFrom(USERS)
                .where(USERS.EMAIL.eq(email))
                .fetchOne();

        return mapRecordToUser(record);
    }

    public Boolean existsByEmail(String email) {
        Select<UsersRecord> usersSelect = dsl.selectFrom(USERS)
                .where(USERS.EMAIL.eq(email));

        return dsl.fetchExists(usersSelect);
    }

    public User save(RegisterUserRequest registerUserRequest) {
        return dsl.insertInto(USERS)
                .set(USERS.EMAIL, registerUserRequest.email())
                .set(USERS.USERNAME, registerUserRequest.username())
                .set(USERS.PASSWORD, passwordEncoder.encode(registerUserRequest.password()))
                .returning()
                .fetchOne(this::mapRecordToUser);
    }
}
