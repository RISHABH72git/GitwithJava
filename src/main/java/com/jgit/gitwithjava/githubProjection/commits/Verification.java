package com.jgit.gitwithjava.githubProjection.commits;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Verification {
    public boolean verified;
    public String reason;
    public Object signature;
    public Object payload;
}
