package com.tjcg.menuo.data.response.BusinessList;

public class BusinessItem {

    private String id = "";
    private String name = "";
//    private String owner_id = "";
    private String email = "";

    public BusinessItem() {
    }

    public BusinessItem(String id,String name,String email){
        this.name = name;
        this.id = id;
//        this.owner_id = owner_id;
        this.email = email;
    }

    public String getItemName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }
//    public String getOwner_id() {
//        return owner_id;
//    }

    public void setName(String name) {
        this.name = name;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public void setOwner(String owner_id) {
//        this.owner_id = owner_id;
//    }

}