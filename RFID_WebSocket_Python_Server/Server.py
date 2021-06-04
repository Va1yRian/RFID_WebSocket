from datetime import datetime

from websocket_server import WebsocketServer

def startWebsocketServer():
    def new_client(client, server):
        server.send_message_to_all("Hey all, a new client has joined us")

    def mySend(client, server, msg):
        global a
        a += 1
        global start_time
        speed = a/(datetime.now()-start_time).seconds
        print(msg)
        print(speed)
        print(a)
        server.send_message_to_all(msg)
        if msg == "_end":  # 如果收到了_end 消息，那么断开连接
            server.server_close()

    server = WebsocketServer(8888, host='localhost')
    server.set_fn_new_client(new_client)
    server.set_fn_message_received(mySend)
    server.run_forever()


if __name__ == "__main__":
    global a
    a = 0
    global start_time
    start_time = datetime.now()
    startWebsocketServer()