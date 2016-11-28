package com.edu.ncu.cc.ncumapwiki;

public class Search {
    private String SearchName;
    private String SearchType;
    public String SearchId;


    public Search (String SearchName,String SearchType,String SearchId)
    {
        this.SearchName=SearchName;
        this.SearchType=SearchType;
        this.SearchId=SearchId;
    }
    public void setSearchName(String Name) {this.SearchName = Name;}
    public String getSearchName() {return this.SearchName;}
    public void setSearchType(String Type) {this.SearchType = Type;}
    public String getSearchType() {return this.SearchType;}
    public void setSearchId(String Id) {this.SearchType = Id;}
    public String getSearchId() {return this.SearchId;}
}
