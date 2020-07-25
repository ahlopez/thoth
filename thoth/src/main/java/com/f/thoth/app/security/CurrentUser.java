package com.f.thoth.app.security;

import com.f.thoth.backend.data.entity.User;

@FunctionalInterface
public interface CurrentUser {

   User getUser();
}
