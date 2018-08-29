package com.rosetta.image.entity;

public class Image {
    private Integer id;

    private String imageKey;

    private String imageSrc;

    private Double norm;

    private Integer dhash32Key;

    private String dhash128Key;

    private Long updateTime;

    private Long createTime;

    private String pyramidHash;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey == null ? null : imageKey.trim();
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc == null ? null : imageSrc.trim();
    }

    public Double getNorm() {
        return norm;
    }

    public void setNorm(Double norm) {
        this.norm = norm;
    }

    public Integer getDhash32Key() {
        return dhash32Key;
    }

    public void setDhash32Key(Integer dhash32Key) {
        this.dhash32Key = dhash32Key;
    }

    public String getDhash128Key() {
        return dhash128Key;
    }

    public void setDhash128Key(String dhash128Key) {
        this.dhash128Key = dhash128Key == null ? null : dhash128Key.trim();
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getPyramidHash() {
        return pyramidHash;
    }

    public void setPyramidHash(String pyramidHash) {
        this.pyramidHash = pyramidHash == null ? null : pyramidHash.trim();
    }
}