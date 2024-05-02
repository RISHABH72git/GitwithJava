package com.jgit.gitwithjava.local.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement
public class Details {

    @XmlElement
    private List<SiteDetail> siteDetailList;

    public List<SiteDetail> getSiteDetailList() {
        return siteDetailList;
    }

    public void setSiteDetailList(List<SiteDetail> siteDetailList) {
        this.siteDetailList = siteDetailList;
    }

    @Override
    public String toString() {
        return "Details{" +
                "siteDetailList=" + siteDetailList +
                '}';
    }
}

