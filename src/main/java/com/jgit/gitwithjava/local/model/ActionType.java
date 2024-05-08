package com.jgit.gitwithjava.local.model;

public enum ActionType {
    ADD,
    REMOVE,
    ROLLBACK,
    CLEAN,
    COMMIT;

    public String getValue() {
        return this.name();
    }
}
