package com.jgit.gitwithjava.github.model.branches;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter @Setter @ToString
public class RequiredStatusChecks {
    public String enforcement_level;
    public ArrayList<String> contexts;
}
