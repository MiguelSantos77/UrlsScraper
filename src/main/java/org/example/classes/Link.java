package org.example.classes;

public class Link {

    private int Id;
    private String url;
    private boolean isVisited;




    public int getId() {
        return Id;
    }

    public String getUrl() {
        return url;
    }

    public boolean isVisited(){
        return  isVisited;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public  Link(){

    }
    public  Link(String url){
        this.url = url;
    }
}
