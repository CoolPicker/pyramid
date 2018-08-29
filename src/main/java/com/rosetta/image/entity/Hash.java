package com.rosetta.image.entity;

public class Hash {
    private Integer id;

    private Double idf;

    private Integer pyramidKey;

    private Long createTime;

    private Long updateTime;

    private String images;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getIdf() {
        return idf;
    }

    public void setIdf(Double idf) {
        this.idf = idf;
    }

    public Integer getPyramidKey() {
        return pyramidKey;
    }

    public void setPyramidKey(Integer pyramidKey) {
        this.pyramidKey = pyramidKey;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images == null ? null : images.trim();
    }
}