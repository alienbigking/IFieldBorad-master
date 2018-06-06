package com.innotek.ifieldborad.utils;

import com.google.gson.annotations.Expose;

/**
 * Created by Raleigh.Luo on 18/6/6.
 */

public class VersionEntity {
    private String versionShort;
    private int version;
    private String install_url;
    private String updated_at;
    private String changelog;
    @Expose
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getChangelog() {
        return changelog==null?"":changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getVersionShort() {
        return versionShort==null?"":versionShort;
    }

    public void setVersionShort(String versionShort) {
        this.versionShort = versionShort;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getInstall_url() {
        return install_url;
    }

    public void setInstall_url(String install_url) {
        this.install_url = install_url;
    }
}
