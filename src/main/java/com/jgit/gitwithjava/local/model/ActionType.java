package com.jgit.gitwithjava.local.model;

public enum ActionType {
    CHECKOUT,
    ADD,
    REMOVE,
    ROLLBACK,
    CLEAN,
    RESTORE,
    COMMIT;

    public String getValue() {
        return this.name();
    }
}
