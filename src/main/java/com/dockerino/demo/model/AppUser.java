package com.dockerino.demo.model;

import java.util.List;

public record AppUser(User user, List<Role> roles) {
}
