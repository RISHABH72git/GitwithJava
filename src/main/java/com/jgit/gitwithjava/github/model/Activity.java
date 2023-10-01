package com.jgit.gitwithjava.github.model;

import lombok.ToString;

import java.util.Date;
@ToString
public class Activity {
    public long id;
    public String node_id;
    public String before;
    public String after;
    public String ref;
    public Date timestamp;
    public String activity_type;
    public Owner actor;
}
