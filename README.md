 sellem-mongodb

Web service to create/replace/update and delete objects
on mongo database collections product and registered users.

show dbs
use sellem
show collections
db.product.find()
db.product.drop()

to set up mongo database
from command line
1. start mongo d
2. in sepeate command prompt
    use sellem
    db.product.drop()
    db.registeredusers.drop()
3. sbt run project
     sbt "run 9010" (from project folder)
4. from browser
      localhost:9010/registeredUserreloadtestdata
      localhost:9010/productreloadtestdata



