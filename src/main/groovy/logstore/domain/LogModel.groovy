package logstore.domain

class LogModel {

    String logID

    String customerID

    String agentID

    Long timestamp

    String logType

    List<String> tags

    String content

    Map toMap() {
        Map fields = [
                "log_id" : logID,
                "customer_id" : customerID,
                "agent_id" : agentID,
                "timestamp" : timestamp,
                "log_type" : logType,
                "tags" : tags,
                "content" : content
        ]
    }

}
