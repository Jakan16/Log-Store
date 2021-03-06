package logstore.controller

import io.micronaut.http.HttpResponse
import logstore.domain.LogModel
import logstore.service.LogService
import org.elasticsearch.rest.RestStatus

import javax.inject.Inject

class LogController {

    @Inject
    LogService logService

    HttpResponse saveLog(Map request) {
        Map fields = (Map) request['body']
        LogModel log = new LogModel(
                logID: fields['log_id'],
                customerID: fields['customer_id'],
                agentID: fields['agent_id'],
                timestamp: fields['timestamp'],
                logType: fields['log_type'],
                tags: fields['tags'],
                content: fields['content'])
        Map saved = logService.saveLog(log)
        saved['status'] == RestStatus.CREATED ?
                HttpResponse.created(saved) :
                HttpResponse.serverError(["error" : "something went wrong when saving the log"])
    }

    /*
    HttpResponse getLogsById(Map request) {
        String pathVariable = request['path'].substring(1)
        Map response = logService.getLogsById(pathVariable)
        if (response.status == RestStatus.OK) {
            return HttpResponse.ok(response.content)
        }
        if (response.status in errors) {
            return (HttpResponse) errors[response.status]
        }
        HttpResponse.serverError(["error" : "something went wrong when retrieving the log"])
    }
    */

    HttpResponse getLogsByCustomer(Map request) {
        String pathVariable = request['path'].substring('?customer_id='.length())
        Map response = logService.getLogsByCustomer(pathVariable)
        if (response.status == RestStatus.OK) {
            return HttpResponse.ok(["results" : response.num_results, "content" : response.content])
        }
        if (response.status in errors) {
            return (HttpResponse) errors[response.status]
        }
        HttpResponse.serverError(["error" : "something went wrong when retrieving the log"])
    }

    HttpResponse getLogByLogID(Map request) {
        String pathVariable = request['path'].substring(1)
        Map response = logService.getLogByLogID(pathVariable)
        if (response.status == RestStatus.OK) {
            return HttpResponse.ok(response.content)
        }
        if (response.status in errors) {
            return (HttpResponse) errors[response.status]
        }
        HttpResponse.serverError(["error" : "something went wrong when retrieving the log"])
    }

    HttpResponse getLogs(Map request) {
        //if(request['path'] && (String) request['path'][0] == '/') {
        //    return getLogsById(request)
        //}
        //else
        if(request['path'] && (int) request['path'].length() >= 13 &&
                (String) request['path'].substring(0,13) == '?customer_id=') {
            return getLogsByCustomer(request)
        }
        else if(request['path'] && (String) request['path'][0] == '/') {
            return getLogByLogID(request)
        }
        Map response = logService.getLogs()
        response.status == RestStatus.OK ?
                HttpResponse.ok(["results" : response.num_results, "content" : response.content]) :
                HttpResponse.serverError(["error" : "something went wrong when retrieving the logs"])
    }

    HttpResponse parseFail() {
        HttpResponse.badRequest(["error": "failed to parse request body"])
    }

    Map errors = [
            (RestStatus.NOT_FOUND) : HttpResponse.notFound(["error" : "Resource was not found"])
    ]

}
