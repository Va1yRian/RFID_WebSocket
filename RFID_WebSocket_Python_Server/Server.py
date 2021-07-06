import time

from websocket_server import WebsocketServer

def startWebsocketServer():
    def new_client(client, server):
        server.send_message_to_all("Hey all, a new client has joined us")

    def mySend(client, server, msg):
        global a
        a += 1
        global start_time
        speed = a/(time.time()-start_time)
        # print("Recieved Messege:"+msg)
        print("speed:"+str(speed))
        # print("count:"+str(a))
        # server.send_message_to_all(msg)
        if msg == "_end":  # 如果收到了_end 消息，那么断开连接
            server.server_close()

    server = WebsocketServer(8001, host='localhost')
    server.set_fn_new_client(new_client)
    server.set_fn_message_received(mySend)
    server.run_forever()


if __name__ == "__main__":
    global a
    a = 0
    global start_time
    start_time = time.time()
    startWebsocketServer()