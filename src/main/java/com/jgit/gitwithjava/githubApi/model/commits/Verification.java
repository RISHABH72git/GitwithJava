package com.jgit.gitwithjava.githubApi.model.commits;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Verification {
    public boolean verified;
    public String reason;
    public Object signature;
    public Object payload;
}
