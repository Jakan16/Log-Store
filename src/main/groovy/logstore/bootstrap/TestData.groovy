package logstore.bootstrap

import logstore.domain.LogModel

class TestData {

    static LogModel createLog(String logID, String customerID, String agentID) {
        LogModel log = new LogModel(
                logID: logID,
                customerID: customerID,
                agentID: agentID,
                timestamp: 1576197266777,
                logType: "log",
                tags: ["log"],
                content: "log_field_1 : xyzxyz, log_field_2 : xyzxyz"
        )
    }

}
