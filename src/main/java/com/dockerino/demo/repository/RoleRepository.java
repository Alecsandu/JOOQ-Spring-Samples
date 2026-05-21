package com.dockerino.demo.repository;

import com.dockerino.demo.model.Role;
import org.jooq.DSLContext;
import org.jooq.Records;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.dockerino.jooq.generated.Tables.ROLES;
import static com.dockerino.jooq.generated.Tables.USER_ROLES;

@Repository
public class RoleRepository {

    private final DSLContext dsl;

    public RoleRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Role findRoleByName(String roleName) {
        var role = dsl.selectFrom(ROLES)
                .where(ROLES.NAME.eq(roleName))
                .fetchOne();

        if (role == null) {
            throw new IllegalStateException("Role not found");
        }

        return new Role(role.getId(), role.getName());
    }

    public List<Role> findRoleNamesByUserId(UUID userId) {
         return dsl.select(ROLES.ID, ROLES.NAME)
                 .from(USER_ROLES)
                 .join(ROLES)
                 .on(USER_ROLES.ROLE_ID.eq(ROLES.ID))
                 .where(USER_ROLES.USER_ID.eq(userId))
                 .fetch(Records.mapping(Role::new));
    }
}
