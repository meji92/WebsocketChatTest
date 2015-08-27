import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import java.io.IOException;
import java.util.ArrayList;

public class ChatTest extends TestVerticle {

    int msg = 0;
    long start = 0;
    //int[] messages  = new int[6];
    ArrayList<Integer> messagesList = new ArrayList<Integer>();

    @Test
    public void test1(){test();}
    @Test
    public void test2(){test();}
    @Test
    public void test3(){test();}
    @Test
    public void test4(){test();}
    @Test
    public void test5(){test();}

    public void test(){
        testClients(50, 25000, 5000, 10000);
        //testClientsMax(10, 4000, 10000);
    }

    public void testClients(int users, long totalMessages, long time, int extra) {
        msg = 0;
        listenerClient("user", time + extra, totalMessages);
        for (int i = 0; i<users; i++){
            senderClient("user"+Integer.toString(i), (time/(totalMessages/users)), time);
        }
    }

    public void testClientsMax(int users, long time, int extra) {
        /**int users = 10;
         int totalMessages = 5000;
         int time = 5000;
         int extra = 1000;**/
        msg = 0;
        listenerClient("user", time + extra, users*time);
        for (int i = 0; i<users; i++){
            senderClient("user"+Integer.toString(i), 1, time);
        }
    }

    public void senderClient(final String name, final long waitTime, final long totalTime) {
        //System.out.println(waitTime+" "+totalTime);
        // Setting host as localhost is not strictly necessary as it's the default
        vertx.createHttpClient().setHost("localhost").setPort(9000).connectWebsocket("/chat", new Handler<WebSocket>() {
            //vertx.createHttpClient().setHost("192.168.1.145").setPort(9000).connectWebsocket("/chat", new Handler<WebSocket>() {
            //vertx.createHttpClient().setHost("192.168.1.69").setPort(9000).connectWebsocket("/chat", new Handler<WebSocket>() {
            @Override
            public void handle(final WebSocket websocket) {
                websocket.dataHandler(new Handler<Buffer>() {
                                          public void handle(Buffer data) {
                                              /**JsonNode message = null;
                                               ObjectMapper mapper = new ObjectMapper();
                                               try {
                                               message = mapper.readTree(data.getBytes());  //message to Json
                                               } catch (IOException e) {
                                               e.printStackTrace();
                                               }
                                               String respuesta = message.get("message").toString();
                                               System.out.println(data);**/
                                              //VertxAssert.assertEquals("\"Devuelve algo?\"", respuesta);
                                              //VertxAssert.testComplete();

                                          }
                                      }

                );
                JsonObject json = new JsonObject();
                json.putString("chat", "chat");
                json.putString("user", name);
                websocket.writeTextFrame(json.toString());
                // Wait 1 second to be sure that the dataHandler has been created
                final long timerID = vertx.setPeriodic(waitTime, new Handler<Long>() {
                    public void handle(Long arg0) {
                        JsonObject json2 = new JsonObject();
                        json2.putString("user", name);
                        json2.putString("message", "wololo");
                        websocket.writeTextFrame(json2.toString());
                    }
                });
                vertx.setTimer(totalTime, new Handler<Long>() {
                            public void handle(Long arg0) {
                                vertx.cancelTimer(timerID);
                            }
                        }

                );
            }
        });
    }

    public void listenerClient(final String name, final long totalTime, final long messages) {
        // Setting host as localhost is not strictly necessary as it's the default
        vertx.createHttpClient().setHost("localhost").setPort(9000).connectWebsocket("/chat", new Handler<WebSocket>() {
            @Override
            public void handle(final WebSocket websocket) {
                websocket.dataHandler(new Handler<Buffer>() {
                                          public void handle(Buffer data) {
                                              JsonNode message = null;
                                              ObjectMapper mapper = new ObjectMapper();
                                              try {
                                                  message = mapper.readTree(data.getBytes());  //message to Json
                                              } catch (IOException e) {
                                                  e.printStackTrace();
                                              }
                                              String respuesta = message.get("message").toString();

                                              msg++;
                                              if (messages == msg) {
                                                  System.out.println("Tiempo:  " + (System.currentTimeMillis() - start));
                                                  VertxAssert.testComplete();
                                              }
                                              //System.out.println(msg);
                                              //VertxAssert.assertEquals("\"Devuelve algo?\"", respuesta);
                                              //VertxAssert.testComplete();

                                          }
                                      }

                );
                JsonObject json = new JsonObject();
                json.putString("chat", "chat");
                json.putString("user", name);
                websocket.writeTextFrame(json.toString());
                start = System.currentTimeMillis();
                vertx.setTimer(totalTime, new Handler<Long>() {
                            public void handle(Long arg0) {
                                System.out.println("Tiempo:  " + (System.currentTimeMillis() - start));
                                VertxAssert.assertEquals(messages, msg);
                                System.out.println(msg + "/" + messages);
                                VertxAssert.testComplete();
                            }
                        }

                );
            }
        });
    }

}



