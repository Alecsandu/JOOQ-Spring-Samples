package com.dockerino.demo.repository;

import com.dockerino.demo.model.AuthProvider;
import com.dockerino.demo.model.User;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
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
        if (r == null) return null;
        User user = new User();
        user.setId(r.get(USERS.ID));
        user.setEmail(r.get(USERS.EMAIL));
        user.setPassword(r.get(USERS.PASSWORD));
        user.setUsername(r.get(USERS.EMAIL));
        String providerStr = r.get(USERS.PROVIDER);
        if (providerStr != null) {
            user.setProvider(AuthProvider.valueOf(providerStr));
        }
        user.setProviderId(r.get(USERS.PROVIDER_ID));
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
        return dsl.fetchExists(
                dsl.selectFrom(USERS)
                        .where(USERS.EMAIL.eq(email))
        );
    }

    public Optional<User> findByProviderId(String providerId) {
        Record record = dsl.selectFrom(USERS)
                .where(USERS.PROVIDER_ID.eq(providerId))
                .fetchOne();
        return Optional.ofNullable(mapRecordToUser(record));
    }

    public User save(User user) {
        if (user.getId() == null || !dsl.fetchExists(dsl.selectFrom(USERS).where(USERS.ID.eq(user.getId())))) {
            if (user.getId() == null && user.getProvider() == AuthProvider.LOCAL) { // Or any new user
                user.setId(UUID.randomUUID()); // Ensure ID is set if not DB generated by default for all
            }

            return dsl.insertInto(USERS)
                    .set(USERS.ID, user.getId()) // If you generate ID in Java
                    .set(USERS.EMAIL, user.getEmail())
                    .set(USERS.USERNAME, user.getUsername())
                    .set(USERS.PASSWORD, user.getPassword())
                    .set(USERS.PROVIDER, user.getProvider() != null ? user.getProvider().name() : null)
                    .set(USERS.PROVIDER_ID, user.getProviderId())
                    .set(USERS.CREATED_AT, OffsetDateTime.now()) // Or rely on DB default
                    .set(USERS.UPDATED_AT, OffsetDateTime.now()) // Or rely on DB default
                    .returningResult(USERS.ID, USERS.EMAIL, USERS.USERNAME, USERS.PASSWORD, USERS.PROVIDER, USERS.PROVIDER_ID, USERS.CREATED_AT, USERS.UPDATED_AT)
                    .fetchOne(this::mapRecordToUser); // Map the returned record;
        } else {
            dsl.update(USERS)
                    .set(USERS.EMAIL, user.getEmail())
                    .set(USERS.USERNAME, user.getUsername())
                    .set(USERS.PASSWORD, user.getPassword()) // Be careful about overwriting password unintentionally
                    .set(USERS.PROVIDER, user.getProvider() != null ? user.getProvider().name() : null)
                    .set(USERS.PROVIDER_ID, user.getProviderId())
                    .set(USERS.UPDATED_AT, OffsetDateTime.now()) // Or rely on DB trigger
                    .where(USERS.ID.eq(user.getId()))
                    .execute();
            return findById(user.getId()).orElseThrow(() -> new IllegalStateException("User not found after update"));
        }
    }
}
