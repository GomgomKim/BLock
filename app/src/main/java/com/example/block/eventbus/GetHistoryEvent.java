package com.example.block.eventbus;

public class GetHistoryEvent{

    String result;

    public GetHistoryEvent(String result){
        this.result = result;
    }

    public String getResult(){
        return this.result;
    }
}
