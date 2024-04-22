package com.tencent.wxcloudrun.config;

public enum SearchType {
    SEARCH("search"),
    RECOMMENDATIONS("recommendations");
    private String typeName;
    SearchType(String typeName){
        this.typeName = typeName;
    }

    public static SearchType fromTypeName(String typeName){
        for(SearchType searchType:SearchType.values()){
            if(searchType.getTypeName().equals(typeName)){
                return searchType;
            }
        }
        return null;
    }

    public String getTypeName(){
        return this.typeName;
    }
}
