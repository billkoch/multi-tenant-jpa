# Multi Tenant JPA Example

## Quick Start
1. Spin up MySQL
```bash
$ docker-compose up -d
```
* Start Spring Boot Application
```bash
$ ./gradlew bootRun
```
* Create a fresh database for the `foo` tenant
```bash
$ http POST :8080/admin/tenants/foo
```
* Create a fresh database for the `bar` tenant
```bash
$ http POST :8080/admin/tenants/bar
```
* Retrieve the list of people persisted in each tenant's database
```bash
$ http GET :8080/api/people X-TENANT-ID:foo
```
```bash
$ http GET :8080/api/people X-TENANT-ID:foo
```
* Create different people in each tenant's database
```bash
$  http POST :8080/api/people firstName=Jane lastName=Doe X-TENANT-ID:foo
```
```bash
$  http POST :8080/api/people firstName=John lastName=Smith X-TENANT-ID:bar
```
