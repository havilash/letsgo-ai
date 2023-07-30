import mongodb from "mongodb"

const { ObjectId } = mongodb;

let chats

export default class ChatsDAO {
    static async injectDB(conn) {
        if (chats) {
            return
        }
        try {
            chats = await conn.db("letsgoai").collection("chats")
        } catch (e) {
            console.error(`Unable to establish collection handles in chatDAO: ${e}`)
        }
    }

    static async addChat( status, messages){
        console.log(chats)
        try {
            const chatDoc = {
                status: status,
                messages: messages
            }
            console.log("adding")
            return await chats.insertOne(chatDoc)
        } catch (e) {
            console.error(`Unable to post chat: ${e}`)
            return { error: e }
        }
    }

    static async getChat(chatId) {
        try {
            return await chats.findOne({ _id: new ObjectId(chatId) })
        } catch (e) {
            console.error(`Unable to get chat: ${e}`)
            return { error: e }
        }
    }

    static async updateChat(status,messages, id) {
        try {
            const updateResponse = await chats.updateOne(
                { _id: new ObjectId(id) },
                { $set: { status: status, messages: messages} }
            )

            return updateResponse
        } catch (e) {
            console.error(`Unable to update chat: ${e}`)
            return { error: e }
        }
    }

    static async deleteChat(chatId) {

        try {
            return await chats.deleteOne({
                _id: new ObjectId(chatId),
            })
        } catch (e) {
            console.error(`Unable to delete chat: ${e}`)
            return { error: e }
        }
    }

    static async getAllChats() {
        try {
            console.log(chats)
            const cursor = await chats.find()
            return cursor.toArray()
        } catch (e) {
            console.error(`Unable to get chat: ${e}`)
            return { error: e }
        }
    }

}