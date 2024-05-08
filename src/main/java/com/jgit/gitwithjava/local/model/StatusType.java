package com.jgit.gitwithjava.local.model;

public enum StatusType {
    MODIFY,
    IGNORE,
    CONFLICT,
    UNTRACKED,
    MISSING,
    REMOVED;

    public String getValue() {
        return this.name();
    }
}
