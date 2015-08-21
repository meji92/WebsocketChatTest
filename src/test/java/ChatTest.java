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
import java.util.Timer;

public class ChatTest extends TestVerticle {

    int msg = 0;

    @Test
    public void testClients() {
        int users = 10;
        int totalMesssages = 5000;
        int time = 5000;
        int extra = 1000;
        listenerClient("user", time+extra, totalMesssages);
        for (int i = 0; i<users; i++){
            senderClient("user"+Integer.toString(i), (time/(totalMesssages/users)), time);
        }
    }

    public void senderClient(final String name, final int waitTime, final int totalTime) {
        // Setting host as localhost is not strictly necessary as it's the default
        vertx.createHttpClient().setHost("localhost").setPort(9000).connectWebsocket("/chat", new Handler<WebSocket>() {
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
                        json2.putString("user", "user");
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

    public void listenerClient(final String name, final int totalTime, final int messages) {
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
                                              System.out.println(msg);
                                              //VertxAssert.assertEquals("\"Devuelve algo?\"", respuesta);
                                              //VertxAssert.testComplete();

                                          }
                                      }

                );
                JsonObject json = new JsonObject();
                json.putString("chat", "chat");
                json.putString("user", name);
                websocket.writeTextFrame(json.toString());
                vertx.setTimer(totalTime, new Handler<Long>()
                        {
                            public void handle(Long arg0) {
                                VertxAssert.assertEquals(msg,messages);
                                VertxAssert.testComplete();
                            }
                        }

                );
            }
        });
    }

}



