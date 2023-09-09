package com.jgit.gitwithjava.github.model.branches;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Branches {
    public String name;
    public Commit commit;
    @JsonProperty("protected")
    public boolean myprotected;
    public Protection protection;
    public String protection_url;
}
