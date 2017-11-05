package com.dobest.irondb.metastore.executor;
class Title
{
    private String type;
    private String store;
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
    public void setStore(String store){
        this.store = store;
    }
    public String getStore(){
        return this.store;
    }
}
 class Description
{
    private String type;
    private String index;

    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
    public void setIndex(String index){
        this.index = index;
    }
    public String getIndex(){
        return this.index;
    }
}
class Price
{
    private String type;
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
}
class OnSale
{
    private String type;
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
}
class Type
{
    private String type;

    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
}
class CreateDate
{
    private String type;
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
}
class Properties
{
    private Title title;
    private Description description;
    private Price price;
    private OnSale onSale;
    private Type type;
    private CreateDate createDate;
    public void setTitle(Title title){
        this.title = title;
    }
    public Title getTitle(){
        return this.title;
    }
    public void setDescription(Description description){
        this.description = description;
    }
    public Description getDescription(){
        return this.description;
    }
    public void setPrice(Price price){
        this.price = price;
    }
    public Price getPrice(){
        return this.price;
    }
    public void setOnSale(OnSale onSale){
        this.onSale = onSale;
    }
    public OnSale getOnSale(){
        return this.onSale;
    }
    public void setType(Type type){
        this.type = type;
    }
    public Type getType(){
        return this.type;
    }
    public void setCreateDate(CreateDate createDate){
        this.createDate = createDate;
    }
    public CreateDate getCreateDate(){
        return this.createDate;
    }
}

class Mappings
{
    private Properties properties;

    public void setProperties(Properties properties){
        this.properties = properties;
    }
    public Properties getProperties(){
        return this.properties;
    }
}

public class Root
{
    private Mappings mappings;

    public void setMappings(Mappings mappings){
        this.mappings = mappings;
    }
    public Mappings getMappings(){
        return this.mappings;
    }
}
