package com.dockerino.demo.repository;

import com.dockerino.demo.exception.UserNotFoundException;
import com.dockerino.demo.exception.authentication.AuthenticationException;
import com.dockerino.demo.exception.authentication.UserExistsException;
import com.dockerino.demo.model.User;
import com.dockerino.jooq.generated.Tables;
import com.dockerino.jooq.generated.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.jooq.Select;
import org.jooq.TableField;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static com.dockerino.jooq.generated.tables.UserRoles.USER_ROLES;
import static com.dockerino.jooq.generated.tables.Users.USERS;

@Repository
public class UserRepository {

    private final DSLContext dsl;

    public UserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Optional<User> findUserBySub(String sub) {
        return dsl.selectFrom(Tables.USERS)
                .where(USERS.AUTH0_SUB.eq(sub))
                .fetchOptional()
                .map(user -> new User(user.getId(), user.getAuth0Sub(), user.getCreatedAt(), user.getUpdatedAt(), user.getIsActive()));
    }

    public Boolean existsByAuth0Sub(String id) {
        return existsByGivenField(USERS.AUTH0_SUB, id);
    }

    public User save(String sub, UUID roleId) {
        try {
            UsersRecord record = dsl.insertInto(USERS, USERS.AUTH0_SUB, USERS.IS_ACTIVE)
                    .values(sub, true)
                    .returning(USERS.ID, USERS.AUTH0_SUB)
                    .fetchOne();

            if (record == null) {
                throw new AuthenticationException("Failed to create account");
            }

            User user = mapRecordToUser(record);

            int inserted = dsl.insertInto(USER_ROLES, USER_ROLES.USER_ID, USER_ROLES.ROLE_ID)
                    .values(user.id(), roleId)
                    .execute();

            if (inserted != 1) {
                throw new AuthenticationException("Failed to create account");
            }

            return user;

        } catch (DataIntegrityViolationException ex) {
            throw new UserExistsException("The email or username is already taken", ex);
        }
    }

    private <T> Boolean existsByGivenField(TableField<UsersRecord, T> tableField, T expectedValue) {
        Select<UsersRecord> usersSelect = dsl.selectFrom(USERS)
                .where(tableField.eq(expectedValue));

        return dsl.fetchExists(usersSelect);
    }

    private User mapRecordToUser(UsersRecord r) {
        if (r == null) {
            throw new UserNotFoundException();
        }

        return new User(r.getId(),
                r.getAuth0Sub(),
                r.getCreatedAt(),
                r.getUpdatedAt(),
                r.getIsActive());
    }
}
