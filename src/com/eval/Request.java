package com.eval;

/**
 * Request class
 */
public class Request {
    int requestId;
    boolean isRedundant;

    public Request(){

    }
    public void setRequestId(int requestId){
        this.requestId = requestId;
    }

    public void setIsRedundant(int isRedundant){
        this.isRedundant = (isRedundant == 1)? true : false;
    }
}
