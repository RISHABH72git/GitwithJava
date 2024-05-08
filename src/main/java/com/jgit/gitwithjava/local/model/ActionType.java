package com.jgit.gitwithjava.local.model;

public enum ActionType {
    ADD,
    REMOVE,
    ROLLBACK,
    COMMIT;

    public String getValue() {
        return this.name();
    }
}
