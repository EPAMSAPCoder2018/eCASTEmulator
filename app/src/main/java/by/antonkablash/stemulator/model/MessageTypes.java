package by.antonkablash.stemulator.model;

/**
 * Created by Anton_Kablash on 6/26/2018.
 */
public enum MessageTypes {
    START("a33784ad3a8eaf737ebb"),
    STOP("41fcfb2baeaab4f33fba"),
    INPROCESS("be96cf06e5ea0b975737");

    private String operationId;

    MessageTypes(String operationId){
        this.operationId = operationId;
    }

    public String getOperationId() {
        return operationId;
    }
}
