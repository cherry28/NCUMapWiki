package com.edu.ncu.cc.ncumapwiki;
public class Places {
    private String id;
    private String cName;
    private String eName;
    private String type;
    private String description;
    private String lastAuthor;
    public Places (String id,String cName,String eName,String type,String description,String lastAuthor)
    {
        this.id=id;
        this.cName=cName;
        this.eName=eName;
        this.type=type;
        this.description=description;
        this.lastAuthor=lastAuthor;
    }
    public void setId(String id) {this.id = id;}
    public String getId() {return this.id;}
    public void setChineseName(String cName) {this.cName = cName;}
    public String getChineseName() {return this.cName;}
    public void setEnglishName(String eName) {this.eName = eName;}
    public String getEnglishName() {return this.eName;}
    public void setType(String type) {this.type = type;}
    public String getType() {return this.type;}
    public void setDescription(String description) {this.description = description;}
    public String getDescription() {return this.description;}
    public void setLastAuthor(String lastAuthor) {this.lastAuthor = lastAuthor;}
    public String getLastAuthor() {return this.lastAuthor;}
}
