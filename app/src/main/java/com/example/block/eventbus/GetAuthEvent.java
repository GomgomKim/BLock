package com.example.block.eventbus;

public class GetAuthEvent {

    String result;

    public GetAuthEvent(String result){
        this.result = result;
    }

    public String getResult(){
        return this.result;
    }
}
