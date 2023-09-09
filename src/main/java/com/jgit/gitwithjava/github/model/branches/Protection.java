package com.jgit.gitwithjava.github.model.branches;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Protection {
    public RequiredStatusChecks required_status_checks;
}
