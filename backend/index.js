import app from "./server.js"
import mongodb from "mongodb"
import ChatDao from "./dao/chatDAO.js"

const MongoClient = mongodb.MongoClient
const uri = "mongodb://localhost:27017"

const port = 8000

MongoClient.connect(
    uri,
    {
        maxPoolSize: 50,
        wtimeoutMS:2500,
        useNewUrlParser: true
    }
).catch(err => {
    console.error(err.stack)
    process.exit(1)
}).then(async client =>{
    await ChatDao.injectDB(client)
    app.listen(port, () => {
        console.log(port)
    })
})