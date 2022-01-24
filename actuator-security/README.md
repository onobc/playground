# Actuator Security Playground

## HTTP Method Based Security

Shows how to extend the OOTB Actuator security to restrict endpoint by HTTP method.

### Example

1. The `loggers` endpoint is unrestricted for GET requests:
    ```bash
    curl http://localhost:8080/actuator/loggers/org.hibernate
    
    {"configuredLevel":"DEBUG","effectiveLevel":"DEBUG"}%
    ```
1. The `loggers` endpoint is restricted for POST requests and fails w/o creds:
    ```bash
    curl 'http://localhost:8080/actuator/loggers/org.hibernate' -X POST \
        -H 'Content-Type: application/json' \
        -d '{"configuredLevel":"debug"}'
        
    {"timestamp":"2022-01-24T03:39:41.564+00:00","status":401,"error":"Unauthorized","path":"/actuator/loggers/org.hibernate"}%
    ```
1. The `loggers` endpoint is restricted for POST requests and succeeds w/ creds:
    ```bash
    curl 'http://localhost:8080/actuator/loggers/org.hibernate' -X POST \
        -H 'Content-Type: application/json' \
        -d '{"configuredLevel":"debug"}' \
        --user 'foo:bar' -i
    HTTP/1.1 204
    Set-Cookie: JSESSIONID=AA6B5F8744D66EFBDD1BD193315BDCD3; Path=/; HttpOnly
    X-Content-Type-Options: nosniff
    X-XSS-Protection: 1; mode=block
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    X-Frame-Options: DENY
    Date: Mon, 24 Jan 2022 03:39:51 GMT
    ```
1. The `loggers` endpoint was updated via the previous POST request:
    ```bash
    curl http://localhost:8080/actuator/loggers/org.hibernate
    
    {"configuredLevel":"DEBUG","effectiveLevel":"DEBUG"}%
    ```