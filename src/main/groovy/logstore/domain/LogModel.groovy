package logstore.domain

class LogModel {

    String agentID

    String timestamp

    String logType

    List<String> tags

    Map content

    Map toMap() {
        Map fields = [
                "agent_id" : agentID,
                "timestamp" : timestamp,
                "log_type" : logType,
                "tags" : tags,
                "content" : content
        ]
    }

}
