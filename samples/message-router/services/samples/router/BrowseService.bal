package samples.router;

import ballerina.net.http;

@http:configuration {basePath:"/browse"}
service<http> BrowseService {
    @http:resourceConfig {
        methods:["GET"],
        path:"/{category}"
    }
    resource browseItems (http:Request req, http:Response resp, string category) {
        println("Fetching items for category : " + category);
        json payload;

        // dummy item browsing service
        if (category.equalsIgnoreCase("Electronics")) {
            payload = {
                          "Phones":[
                                   {
                                       "model":"S7 Edge",
                                       "brand":"Samsung"
                                   },
                                   {
                                       "model":"S8 Edge",
                                       "brand":"Samsung"
                                   },
                                   {
                                       "model":"IphoneX",
                                       "brand":"Apple"
                                   }
                                   ],
                          "Televisions":[
                                        {
                                            "model":"LF530 Smart",
                                            "brand":"LG"
                                        },
                                        {
                                            "model":"M5000",
                                            "brand":"Samsung"
                                        }
                                        ]
                      };
        } else if (category.equalsIgnoreCase("Sports")) {
            payload = {
                          "Tennis":[
                                   {
                                       "type":"L3 Racket",
                                       "brand":"Babolat"
                                   },
                                   {
                                       "type":"L2 Racket",
                                       "brand":"Wilson"
                                   }
                                   ],
                          "Cricket":[
                                    {
                                        "type":"Bat",
                                        "brand":"Kookaburra"
                                    },
                                    {
                                        "type":"Bat",
                                        "brand":"GM Icon"
                                    }
                                    ]
                      };


        } else if (category.equalsIgnoreCase("Motors")) {
            payload = {
                          "Tyres":[
                                  {
                                      "type":"185/65R15",
                                      "brand":"Dunlop"
                                  },
                                  {
                                      "type":"195/65R15 ECO",
                                      "brand":"Goodyear"
                                  }
                                  ],
                          "Accessories":[
                                        {
                                            "type":"Car Mat 5 pieces",
                                            "brand":"3M"
                                        },
                                        {
                                            "type":"DDX717WBT DVD Player",
                                            "brand":"Kenwood"
                                        }
                                        ]
                      };

        } else {
            payload = {"Error": "Category not found"};
        }

        resp.setJsonPayload(payload);
        resp.send();
    }

}