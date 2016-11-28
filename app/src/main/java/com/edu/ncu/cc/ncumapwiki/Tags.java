package com.edu.ncu.cc.ncumapwiki;
public class Tags {
    private String TagsId;
    private String TagsName;
    public Tags (String TagId,String TagName)
    {
        this.TagsId=TagId;
        this.TagsName=TagName;
    }
    public void setTagsId(String TagId) {this.TagsId = TagId;}
    public String getTagsId() {return this.TagsId;}
    public void setTagsName(String TagName) {this.TagsName = TagName;}
    public String getTagsName() {return this.TagsName;}
}
