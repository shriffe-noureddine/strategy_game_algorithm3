HTTP Method     URL                                      Input                          Description
_______________________________________________________________________________________________________________________

POST            http://localhost:8080/barracks           application/json                Create a new barrack
                                                        {
                                                        "id": "1",
                                                        "name": "Nour"
                                                        }
_______________________________________________________________________________________________________________________
GET             http://localhost:8080/barracks          N/A                             Read all barracks
_______________________________________________________________________________________________________________________

GET             http://localhost:8080/barracks/1    Path parameter                  Read the user with id '1'
_______________________________________________________________________________________________________________________

PATCH           http://localhost:8080/barracks/1    Path parameter                  Update the student with id '1'
                                                    application/json                
                                                    {                           
                                                    "id": "2",
                                                    "name": "NECH"
                                                    }
_______________________________________________________________________________________________________________________

DELETE          http://localhost:8080/barracks/1    Path parameter                  Delete the student with id '1'
