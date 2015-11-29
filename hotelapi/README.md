### Hotel Api

Simple Hotel api for csv based hotels list. 
Data is located under `/conf/data/hoteldb.csv`

```
CITY,HOTELID,ROOM,PRICE
Bangkok,1,Deluxe,1000
Amsterdam,2,Superior,2000
Ashburn,3,Sweet Suite,1300
Amsterdam,4,Deluxe,2200
Ashburn,5,Sweet Suite,1200
Bangkok,6,Superior,2000
Ashburn,7,Deluxe,1600
Bangkok,8,Superior,2400
Amsterdam,9,Sweet Suite,30000
...
```

Supported api calls;

```
Request: GET /auth
Response: 
{
    "X-APIKEY": "RUrPPpDZ40TGE+JeVjPuxpedt7NM+Hv+"
}

Request: GET /search/:city?sort=DESC|ASC
Header: X-APIKEY : RUrPPpDZ40TGE+JeVjPuxpedt7NM+Hv+ 
Response: 
[
    {
        "city": "Amsterdam",
        "hotelId": 9,
        "room": "Sweet Suite",
        "price": 30000
    },
    {
        "city": "Amsterdam",
        "hotelId": 23,
        "room": "Deluxe",
        "price": 5000
    },
    {
        "city": "Amsterdam",
        "hotelId": 26,
        "room": "Deluxe",
        "price": 2300
    },
    ...
]
```

**Api Limitations**

You can edit these in `application.conf`

```
# Rate limit in millisecond,
hotelapi.rateLimit = 10000

# Api blocking duration in millisecond, default 1 minute
hotelapi.blockingDuration = 60000
```





