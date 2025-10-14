package com.dockerino.demo.repository;

import com.dockerino.demo.exception.UserNotFoundException;
import com.dockerino.demo.exception.authentication.AuthenticationException;
import com.dockerino.demo.exception.authentication.UserAlreadyExistsException;
import com.dockerino.demo.model.User;
import com.dockerino.demo.model.dtos.RegisterUserRequest;
import com.dockerino.jooq.generated.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.jooq.Select;
import org.jooq.TableField;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

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

    public User findByEmail(String email) {
        UsersRecord record = dsl.selectFrom(USERS)
                .where(USERS.EMAIL.eq(email))
                .fetchOne();

        return mapRecordToUser(record);
    }

    public Boolean existsById(UUID id) {
        return existsByGivenField(USERS.ID, id);
    }

    public Boolean existsByEmail(String email) {
        return existsByGivenField(USERS.EMAIL, email);
    }

    public Boolean existsByUsername(String username) {
        return existsByGivenField(USERS.USERNAME, username);
    }

    private <T> Boolean existsByGivenField(TableField<UsersRecord, T> tableField, T expectedValue) {
        Select<UsersRecord> usersSelect = dsl.selectFrom(USERS)
                .where(tableField.eq(expectedValue));

        return dsl.fetchExists(usersSelect);
    }

    public Object[] save(RegisterUserRequest registerUserRequest) {
        try {
            UsersRecord record = dsl.insertInto(USERS, USERS.EMAIL, USERS.USERNAME, USERS.PASSWORD)
                    .values(
                            registerUserRequest.email(),
                            registerUserRequest.username(),
                            passwordEncoder.encode(registerUserRequest.password())
                    )
                    .returning(USERS.ID, USERS.EMAIL, USERS.USERNAME)
                    .fetchOne();

            if (record == null) {
                throw new AuthenticationException("Failed to create account");
            }

            return new Object[]{record.getId(), record.getEmail(), record.getUsername()};

        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistsException("The email or username is already taken", ex);
        }
    }

    private User mapRecordToUser(UsersRecord r) {
        if (r == null) {
            throw new UserNotFoundException();
        }

        return new User(r.getId(),
                r.getEmail(),
                r.getPassword(),
                r.getUsername(),
                r.getCreatedAt(),
                r.getUpdatedAt());
    }
}
