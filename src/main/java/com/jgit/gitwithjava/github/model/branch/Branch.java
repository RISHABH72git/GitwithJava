package com.jgit.gitwithjava.github.model.branch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jgit.gitwithjava.github.model.branches.Protection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Branch {
    public String name;
    public Commit commit;
    public Links _links;
    @JsonProperty("protected")
    public boolean myprotected;
    public Protection protection;
    public String protection_url;
}
