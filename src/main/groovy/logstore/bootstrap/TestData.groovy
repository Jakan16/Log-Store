package logstore.bootstrap

import logstore.domain.LogModel

class TestData {

    static LogModel createLog(String agentID) {
        LogModel log = new LogModel(
                logID: "5",
                customerID: "customer_1",
                agentID: agentID,
                timestamp: "24-11-2019",
                logType: "log",
                tags: ["log"],
                content: "log_field_1 : xyzxyz, log_field_2 : xyzxyz"
        )
    }

}
