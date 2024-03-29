# define-sn-map-generator
This is a File SN Mapping Generator with input serial number files.
The application handles reading file consists of predefine serial numbers and map them based on criteria.

## Pre-requisite
* git installed
* mvn installed

## How to Use
* Clone this repository by run this command:
`$ git clone git@github.com:Fathyaff/define-sn-map-generator.git`

* This application is API based application which receives several parameters and multipart file. 
* Prepare these request parameters value
```json
{
  "proId": "PRO",
  "productCode": "SKU",
  "batchId": "BATCH",
  "locationCode": "PFT1",
  "locationNo": "L1",
  "totalPallet": 1,
  "quantityBox": 2,
  "quantityBottle": 3,
}
```
```
curl --location --request POST 'http://localhost:8001/map-generator/define-sn?productCode=SKU&proId=PRO&batchId=BATCH&totalPallet=2&quantityBox=2&quantityBottle=3&locationCode=PFT1&locationNo=L1' \
--header 'Cookie: SESSION=a7ba04d5-781c-4fa5-b8a0-39f32c038fd9' \
--form 'file=@"/path/to/file"'
```

* Prepare CSV file which consists of serial numbers which will be used for serial number mapping.

## Quick Start
This project using Docker Compose to run the application. Make sure to run docker on your machine.
<br>
<br>
`$ mvn clean && mvn install`
<br>
<br>
`$ docker-compose up`
