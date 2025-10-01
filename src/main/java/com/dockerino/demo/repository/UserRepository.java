package com.dockerino.demo.repository;

import com.dockerino.demo.model.User;
import com.dockerino.demo.model.dtos.RegisterUserRequest;
import com.dockerino.jooq.generated.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Select;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
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
            return null;
        }

        return new UserRecordBuilder()
                .setId(r.get(USERS.ID, UUID.class))
                .setEmail(r.get(USERS.EMAIL, String.class))
                .setPassword(r.get(USERS.PASSWORD, String.class))
                .setUsername(r.get(USERS.USERNAME, String.class))
                .setCreatedAt(r.get(USERS.CREATED_AT, LocalDateTime.class))
                .setUpdatedAt(r.get(USERS.UPDATED_AT, LocalDateTime.class))
                .build();
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

    public User save(RegisterUserRequest registerUserRequest) {
        return dsl.insertInto(USERS)
                .set(USERS.EMAIL, registerUserRequest.email())
                .set(USERS.USERNAME, registerUserRequest.username())
                .set(USERS.PASSWORD, passwordEncoder.encode(registerUserRequest.password()))
                .returning()
                .fetchOne(this::mapRecordToUser);
    }

    private static class UserRecordBuilder {
        private UUID id;
        private String email;
        private String password;
        private String username;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        UserRecordBuilder() {
        }

        UserRecordBuilder setId(UUID id) {
            this.id = id;
            return this;
        }

        UserRecordBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        UserRecordBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        UserRecordBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        UserRecordBuilder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        UserRecordBuilder setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        User build() {
            return new User(id, email, password, username, createdAt, updatedAt);
        }
    }
}
